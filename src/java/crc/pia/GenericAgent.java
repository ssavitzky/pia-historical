// GenericAgent.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.pia;
import java.util.Hashtable;
import java.util.Vector;
import crc.tf.UnknownNameException;
import java.io.File;
import java.io.InputStream;
import java.io.StringBufferInputStream;

import java.net.URL;

import crc.pia.Agent;
import crc.pia.agent.AgentMachine;
import crc.pia.Transaction;
import crc.pia.HTTPRequest;
import crc.pia.Machine;
import crc.pia.Resolver;
import crc.pia.Content;
import crc.pia.ByteStreamContent;
import crc.pia.Pia;

import crc.ds.Features;
import crc.util.regexp.RegExp;
import crc.util.regexp.MatchInfo;
import java.util.Enumeration;
import crc.interform.Run;
import w3c.www.http.HTTP;

public class GenericAgent implements Agent {
  
  private String filesep = System.getProperty("file.separator");
  public boolean DEBUG = false;  



  /**
   * Attribute index - attribute table for storing options
   */
  protected Hashtable attributes = new Hashtable();


  /**
   * features 
   */
  protected Features features;

  /**
   * Attribute index - name of this agent
   *
   */
  protected String agentname;

  /**
   * Attribute index - type of this agent
   *
   */
  protected String agenttype;

  /**
   * Attribute index - type of this agent
   *
   */
  protected String version;

  /**
   * Attribute index - directory that this agent can write to
   *
   */
  protected Hashtable dirTable;

  /**
   * Attribute index - files that this agent can write to
   *
   */
  protected Hashtable fileTable;


  /**
   * Attribute index - a list of criteria, value pairs
   *
   */
  protected Vector criteria;

  /**
   * Attribute index - virtual Machine to which local requests are directed.
   *
   */
  protected AgentMachine virtualMachine;

  /**
   * Initialization; sub classes may override
   */
  public void initialize(){
    String n = name();
    String t = type();
    String url;
    Transaction request;

    features = new Features( this );

    if( t != null && !n.equalsIgnoreCase( t ) ) 
      type( t );

    option("name", n);
    option("type", type());

    if( DEBUG )
      System.out.println("[GenericAgent]-->"+"Hi, I am in debugging mode.  No interform request is put onto the resolver.");
    else{
      /*
      url = "/" + n + "/" + "initialize.if";
      createRequest("GET", url );
      */
    }

  }

  /**
   * set features
   */
  public void setFeatures( Features f ){
    features = f;
  }

  /**
   * Create a new request given method, url
   */
  private void createRequest(String method, String url){
    Transaction request =  new HTTPRequest();
    request.toMachine( Pia.instance().thisMachine() );
    request.setMethod( method );
    request.setRequestURL( url );
    request.startThread();
  }
 
  /**
   * @return name of agent
   */
  public String name(){
    return agentname; 
  }

  /**
   * set name of agent
   */
  public void name(String name){
    this.agentname = name;
  }

  /**
   * @return type of agent
   */
  public String type(){
    return agenttype;
  }

  /**
   * set type of agent
   */
  public void type(String type){
    this.agenttype = type;
  }

  /**
   * @return version
   */
  public String version(){
    if(version ==null){
      StringBuffer v = new StringBuffer( "PIA/" );
      if( !type().equalsIgnoreCase( name() ) )
	v.append( type() + "/" );
      v.append( name() );
      return new String( v );
    }
    else
      return version;
  }

  /**
   * set version
   */
  public void version(String version){
    if( version != null)
      this.version = version;
  }

  /**
   * set a file attribute
   *
   */
  public void fileAttribute(String key, String[] value){
    fileTable.put( key, value ); 
  }

  /**
   * get a file attribute
   */
  public String[] fileAttribute(String key){
    String v = null;
    String[] res = null;

    if( fileTable.containsKey( key ) )
      res = (String[])fileTable.get(key);
    if( res == null ){
      v = optionAsString( key );
      if ( v!=null && v.startsWith("~/") ){
	  StringBuffer value = null;
	  String home = System.getProperty("user.home");
	  value = new StringBuffer( v.substring(1) );
	  value.insert(0,home);
	  v = new String( value );
	  res = new String[1];
	  res[0] = v;
	  fileTable.put( key, res );
      }else if( v!=null){
	res = new String[1];
	res[0] = v;
	fileTable.put( key, res );
      }
      
    }

    return res;
  }

  /**
   * set a directory attribute
   *
   */
  public void dirAttribute(String key, String[] value){
    dirTable.put( key, value ); 
  }

  /**
   * retrieve a directory attribute. 
   * Makes sure that it ends in a file separator character.
   */
  public String[] dirAttribute(String key){
    String v = null;
    String[] res = null;

    if( dirTable.containsKey( key ) )
      res = (String[])dirTable.get(key);
    if( res== null ){
      v = optionAsString( key );
      if ( v!=null && v.startsWith("~/") ){
	  StringBuffer value = null;
	  String home = System.getProperty("user.home");
	  value = new StringBuffer( v.substring(1) );
	  value.insert(0,home);
	  v = new String( value );
	  if(!v.endsWith(filesep) ) { v = v + filesep; }
	  res = new String[1];
	  res[0] = v;
	  dirTable.put( key, res );
      }else if( v!=null){
	res = new String[1];
	res[0] = v;
	dirTable.put( key, res );
      }
    }

    return res;
  }

  /**
   *  returns a directory that we can write data into.
   *  creates one if necessary, starts with the following directory in order:
   *  piaUsrRoot/agentName/  --> example, ~/Joe/pia/myHistory/ ( ~/Joe/pia is piaUsrRoot ) 
   *  piaUsrRoot/agentType/  --> example, ~/Joe/pia/History/   ( ~/Joe/pia is piaUsrRoot ) 
   *  /tmp/myHistory/    
   * @return the first qualified directory out of the possible three above.
   * A directory is qualified if it can be writen into.
   */
  public String agentDirectory(){
    String[] directories = dirAttribute("agent_directory");
    if( directories!=null && directories.length > 0)
      return directories[0];

    String[] possibilities = { Pia.instance().piaUsrRoot() + filesep + name() + filesep,
			       Pia.instance().piaUsrRoot() + filesep + type() + filesep,
			       filesep + "tmp" + filesep + name() + filesep };
    String name = name();
    String type = type();
    
    for(int i = 0; i < possibilities.length; i++){
      String dir = possibilities[i];

      File myFileDir = new File( dir );
      if( myFileDir.exists() || myFileDir.mkdir() ){
	if( myFileDir.isDirectory() && myFileDir.canWrite() ){
	  String[] dirs = new String[1];
	  dirs[0] = dir;
	  dirAttribute( "agent_directory", dirs );
	  return dir;
	}
      }
    }

    Pia.instance().errLog( name()+ "could not find appropriate, writable directory");
    return null;
  }


  /**
   * returns the base url (as string) for this agent
   * optional path argument just for convenience--
   * returns full url for accessing that file
   */
  public String agentUrl(String path){
    String url = Pia.instance().url() + "/" + name() + "/";

    if( path!= null )
      url += path;

    return url;
  }

  /**
   * Agents maintain a list of feature names and expected values;
   * the features themselves are maintained by a FEATURES object
   * attached to each transaction.  Add either a feature name or its value.
   */
  public void criteria(Object element){
    if( element != null ){
      if(criteria == null ) criteria = new Vector();
      criteria.addElement( element );
    }
  }

  /**
   * Agents maintain a list of feature names and expected values;
   * the features themselves are maintained by a FEATURES object
   * attached to each transaction.
   */
  public Vector criteria(){
    if(criteria == null) criteria = new Vector();
    return criteria;
  }

  /**
   * Set a match criterion.
   * feature is string naming a feature
   * value is 0,1 (exact match--for don't care, omit the feature)
   * @return criteria table
   */
  public Vector matchCriterion(String feature, Object value){
    if(value == null)
      value = new Boolean( true );
    criteria( feature );
    criteria( value );
    return criteria();
  }
  
  /**
   * agents are associated with a virtual machine which is an
   * interface for actually getting and sending transactions.  Posts
   * explicitly to an agent get sent to the agent's machine (then to
   * the agent's interform_request method). Other requests can be
   * handled implicitly by the agent.  If one does not exist,
   * create a pia.agent.Machine
   * @return virtual machine
   */
  public Machine machine(){
    Pia.instance().debug(this, "Getting agent machine" );
    if( virtualMachine==null ){
      virtualMachine = new AgentMachine( (Agent)this );
      Pia.instance().debug(this, "Creating virtual agent machine" );
    }
    return virtualMachine;
  }

  /**
   * Setting the virtual machine.  
   */
  public void machine( Machine vmachine){
    AgentMachine machine = null;
 
    if( virtualMachine==null && vmachine==null ){
      machine = new AgentMachine( (Agent) this );
    }
    if( machine!=null )
      virtualMachine = machine;
  }

  /**
   * Act on a transaction that we have matched.  
   *
   */
  public void actOn(Transaction ts, Resolver res){
  }

  /**
   * Handle a transaction matched by an act_on method. 
   * Requests directly _to_ an agent are handled by its Machine;
   * the "handle" method is used only by agents like "cache" that
   * may want to intercept a transaction meant for somewhere else.
   */
  public boolean handle(Transaction ts, Resolver res){
    return false;
  }


  /**
   * Respond to a direct request.
   * This is called from the agent's Agent::Machine
   */
  public void respond(Transaction trans, Resolver res) throws PiaRuntimeException{
    URL zurl = trans.requestURL();

    String reply = respondToInterform( trans, zurl, res );
    InputStream in = null;
    if( reply != null  ){
      in = new StringBufferInputStream( reply );
      ByteStreamContent c = new ByteStreamContent( in );

      Transaction response = new HTTPResponse( trans, false );
      response.setStatus( 200 ); 
      response.setContentType( "text/html" );
      response.setContentObj( c );
      response.startThread();
    }else{
      //interformErr( request, url );
      throw new PiaRuntimeException(this, "respond", "No InterForm file found for "+trans.url());
    }
  }

  /**
   * Options are strings stored in attributes.  Options may have
   * corresponding features derived from them, which we compute on demand.
   */
  public void option(String key, String value) throws NullPointerException{
    if( key == null || value == null )
      throw new NullPointerException("Key or value can not be null.");

      attributes.put( key, value );
      if( features.has( key ) )
	features.compute( key, this ); 
  }

  /**
   * Return an option's value 
   *
   */
  public String optionAsString(String key){
    if( key == null ) return null;
    if( attributes.containsKey( key ) )
      return (String)attributes.get( key );
    else
      return null;
  }


  /**
   * @return an option's value as boolean 
   * @return false if key not found
   */
  public boolean optionAsBoolean(String key){
    if( key == null ) return false;
    if( attributes.containsKey( key ) ){
      String v = (String)attributes.get( key );
      return "true".equalsIgnoreCase(v) ? true : false ;
    }
    else
      return false;
  }


  /**
   * Set options with a hash table
   *
   */
  public void parseOptions(Hashtable hash){
    Enumeration e = null;
    if( hash != null )
      e = hash.keys();
      while( e.hasMoreElements() ){
	Object keyObj = e.nextElement();
	String key = (String)keyObj;
	String value = (String)hash.get( keyObj );
	option( key, value );
      }
  }

  /**
   * Find an interform, using a simple search path and a crude kind
   * of inheritance.  Allow for the fact that the user may be trying
   * to override the interform by putting it in piaUsrAgentsStr/name/.
   */
  public String findInterform( URL url, boolean noDefault ){
    if( url == null ) return null;
    Vector pathVector = new Vector();
    int index = -1;
    String zform = null;


    String host =  url.getHost();
    if( host!= null && !host.equalsIgnoreCase( name() ))
	return null;

    String urlString = url.toExternalForm();
    String path = urlString.toLowerCase();
    
    String myname = name().toLowerCase();
    String mytype = type().toLowerCase();
    
    // Find interform name in URL.
    
    if ( noDefault ){
      // are v doing defaults? no no
    }
    else { // default to index.if
      RegExp re = null;
      MatchInfo mi = null;
      
      try{
	re = new RegExp(myname + "/" + ".*$");
	mi = re.match(path);
      }catch(Exception e){;}
      if( mi != null ){
	// default to index.if
	index = path.indexOf(myname + "/"); 
	zform  = path.substring( index + (myname+"/").length() );
	if( zform.length() == 0 )
	  zform = "index.if";
      }else if( path.endsWith( myname ) ){  // default to home.if
	zform = "home.if";
      }else if( path == "/" || path.length() == 0 ){
	zform = "ROOTindex.if";
      }
    }
    
    String[] if_path = dirAttribute( "if_path" );
    if( if_path == null ){
      /*
       * If the path isn't already defined, set it up now.
       *
       *  the path puts any  defined if_root first 
       *   (if_root/myname, if_root/mytype, if_root),
       *
       *  piaAgentsStr/agentName/  --> example, /pia/Agents/myHistory/ 
       *  piaAgentsStr/agentType/  --> example, /pia/Agents/History/
       *  piaAgentsStr/            --> example, /pia/Agents/    
       *
       * If the above is not define
       * next :
       *
       *  piaUsrAgentsStr/agentName/  --> example, ~Joe/pia/Agents/myHistory/ 
       *  piaUsrAgentsStr/agentType/  --> example, ~Joe/pia/Agents/History/
       *  piaAgentsStr/agentName/     --> example, /pia/Agents/myHistory/
       *  piaAgentsStr/agentType/     --> example, /pia/Agents/History/
       *  piaUsrAgentsStr/            --> example, ~Joe/pia/Agents/
       *  piaAgentsStr/               --> example, /pia/Agents
       * 
       */
      String home = Pia.instance().piaAgents();
      if ( !home.endsWith( filesep ) ){ home = home + filesep; }
      
      String[] roots = dirAttribute( "if_root" );
      String root;
      if ( roots!= null && roots.length == 1 ){
	// handle a user-defined root first:
	
	root = roots[0];
	if ( !root.endsWith( filesep ) ) { root = root + filesep; }
	if ( root.endsWith( filesep + myname + filesep )) {
	  try{
	    RegExp re = new RegExp( filesep + myname + filesep + "$" );
	    re.substitute( root, filesep, true );
	  }catch(Exception e){;}
	}
	
	if ( root.endsWith( filesep + mytype + filesep )) {
	  try{
	    RegExp re = new RegExp( filesep + mytype + filesep + "$" );
	    re.substitute( root, filesep, true );
	  }catch(Exception e){;}
	}
	

	pathVector.addElement( root+myname+filesep );
	if( myname != mytype )
	  pathVector.addElement( root+mytype+filesep );
	pathVector.addElement( root );

	for(int j=0; j<pathVector.size(); j++){
	  if( DEBUG )
	    System.out.println("GenericAgent findInterform [if-root]-->" + (String) pathVector.elementAt(j) );
	}


      }	
      /*
       * Then see whether the user has overridden the form.
       *    It's possible that one of these will be a duplicate.
       *    That slows us down, but not much.
       */
      
      root = Pia.instance().piaUsrAgents();
      if ( !root.endsWith( filesep ) ) { root = root + filesep; }
      pathVector.addElement( root+myname+filesep );

      if ( myname != mytype )
	pathVector.addElement( root+mytype+filesep );

      pathVector.addElement( home+myname+filesep );

      if( myname != mytype )
	pathVector.addElement( home+ mytype+filesep );

      pathVector.addElement( root );
      pathVector.addElement( home );
      
      
      String[] tmp = new String[pathVector.size()];
      for(int i=0; i<pathVector.size(); i++){
	tmp[i] = (String) pathVector.elementAt(i);
	if( DEBUG )
	  System.out.println("GenericAgent findInterform-->" + tmp[i] );
      }
      dirAttribute("if_path", tmp );
      
    }
    File f;
    Enumeration e = pathVector.elements();
    while( e.hasMoreElements() ){
      String zpath = (String)e.nextElement();
      String wholepath = zpath + zform;
      f = new File( wholepath );
      if( f.exists() ) return wholepath;
    }
    
    return null;
}

  /**
   * Send error message for not found interform file
   */
  private void interformErr( Transaction req, URL url){
    String msg = "No InterForm file found for "+url.toExternalForm();
    Content ct = new ByteStreamContent( new StringBufferInputStream( msg ) );
    Transaction abort = new HTTPResponse(Pia.instance().thisMachine, req.fromMachine(), ct, false);
    abort.setStatus(HTTP.NOT_FOUND);
    abort.setContentType( "text/plain" );
    abort.setContentLength( msg.length() );
    abort.startThread();
  }

  /**
   * Respond to a request directed at one of an agent's interforms.
   * The InterForm's url may be passed separately, since the agent may
   * need to modify the URL in the request.  It can pass a full
   * URL.
   */
  public String respondToInterform(Transaction request, URL url, Resolver res){
    Pia.instance().debug(this, "respondToInterform url version");
    URL myurl;
    String file;
    String interformOutput = null;

    if(request.method().equalsIgnoreCase("PUT") && url == null){
      //return respondToInterformPut( request );
    }

    if( url != null )
      myurl = url;
    else
      myurl = request.requestURL();
    file = findInterform( myurl, false );
    Pia.instance().debug(this, "The path of interform is -->"+file);

    if( file == null )
      //send error response
      return null;
    else{
      String lfile = file.toLowerCase();

      if( lfile.endsWith(".if") ){
	// If find_interform substituted .../home.if for .../ 
	// we have to tell what follows that it's an interform.
	request.assert("interform");
      }
      if( request.test("interform") ){
	interformOutput = Run.interformFile(this, file, request, res);
	return interformOutput;
      }
      
      if( lfile.endsWith(".cgi") ){
	//what to do here
      }

      return null;
      
    }
    
  }
  

  /**
   * Respond to a request directed at one of an agent's interforms.
   * The InterForm's url may be passed separately, since the agent may
   * need to modify the URL in the request.  It can pass either a full
   * URL or a path.
   */
    public String respondToInterformPut(){
      return "";
    }

  /* name and type should be set latter */
  public GenericAgent(){
    dirTable = new Hashtable();
    fileTable = new Hashtable();
    name("GenericAgent");
    type("GenericAgent");
  }

  public GenericAgent(String name, String type){
    dirTable = new Hashtable();
    fileTable = new Hashtable();

    if( name != null ) this.name( name );
    if( type != null )
      this.type( type );
    else
      this.type( name );
    initialize();
  }

  /**
   * Compute a feature associated with this agent.
   * @param featureName a feature name.
   */
 public Object computeFeature( String featureName ) throws UnknownNameException{
    if( featureName == null )
      throw new UnknownNameException("bad feature name.");
    else
      return null;
 }

}

















