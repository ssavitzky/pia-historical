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


public class GenericAgent implements Agent {
  
  private String filesep = System.getProperty("file.separator");
  public boolean DEBUG = false;  



  /**
   * Attribute index - attribute table
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
   * Attribute index - virtual Machine to which explicit requests are directed.
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
      request = createRequest("GET", url );
      Pia.instance().resolver().unshift( request );
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
  public Transaction createRequest(String method, String url){
    Transaction request =  new HTTPRequest(Pia.instance().thisMachine());
    request.setMethod( method );
    request.setRequestURL( url );
    return request;
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
   *  creates one if necessary, starts with agent_directory,
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
   * attached to each transaction.
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
    if( virtualMachine==null )
      virtualMachine = new AgentMachine( (Agent)this );
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
   *  They can also be handled by code or InterForm hooks.  
   *
   */
  public void actOn(Transaction ts, Resolver res){
    Pia.instance().errLog(name() + "If you come this far, you have execute the generic agent actOn.");
  }

  /**
   * Handle a transaction matched by an act_on method. 
   * Requests directly _to_ an agent are handled by its Machine;
   * the "handle" method is used only by agents like "cache" that
   * may want to intercept a transaction meant for somewhere else.
   */
  public boolean handle(Transaction ts, Resolver res){
    Pia.instance().errLog(name() + "If you come this far, you have execute the generic agent Handle.");
    return false;
  }


  /**
   * Respond to a direct request.
   * This is called from the agent's Agent::Machine
   */
  public Transaction respond(Transaction trans, Resolver res){
    URL zurl = trans.requestURL();
    String url = zurl.getFile();
    Transaction response = null;

    String reply = respondToInterform( trans, url, res );
    InputStream in = null;
    if( reply != null  )
      in = new StringBufferInputStream( reply );
    ByteStreamContent c = new ByteStreamContent( in );

    response = new HTTPResponse( trans, c );
    return response;
  }

  /**
   * Options are strings stored in features.  Options may have
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
   * to override the interform by putting it in $USR_ROOT/$name/.
   * The search path used is:
   *     option(root)/name : option(root)
   *     USR_ROOT/name : PIA_ROOT/name : USR_ROOT : PIA_ROOT
   */
  public String findInterform(URL url, boolean noDefault){
    if(  url == null ) return null;
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
       *   (if_root/$myname, if_root/$mytype, if_root),
       *  then USR_ROOT (USR_ROOT/$myname, USR_ROOT/$mytype)
       *  then PIA_ROOT (PIA_ROOT/$myname, PIA_ROOT/$mytype)
       *  then USR_ROOT, then PIA_ROOT (for inheriting forms)
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
   * Respond to a request directed at one of an agent's interforms.
   * The InterForm's url may be passed separately, since the agent may
   * need to modify the URL in the request.  It can pass either a full
   * URL or a path.
   */
    public String respondToInterform(Transaction t, String url, Resolver res){
      String html = "<html>\n <head>\n  <title>Generic Agent</title>\n </head>\n" +
	"<body>\n <center>\n  <h1>respond to interform</h1>\n </center>\n" +
	"Hello World\n" + "</body>\n</html>\n";
      return html;
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

 public Object computeFeature( String featureName ) throws UnknownNameException{
    if( featureName == null )
      throw new UnknownNameException("bad feature name.");
    else
      return null;
 }

}













