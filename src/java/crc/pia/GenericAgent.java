// GenericAgent.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.pia;
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
import crc.pia.HTTPResponse;
import crc.pia.Machine;
import crc.pia.Resolver;
import crc.pia.Content;
import crc.pia.ByteStreamContent;
import crc.pia.Pia;

import crc.ds.Features;
import crc.ds.Table;
import crc.ds.List;
import crc.ds.Criteria;
import crc.ds.Criterion;

import crc.sgml.SGML;
import crc.sgml.Attrs;
import crc.sgml.AttrBase;
import crc.sgml.AttrTable;
import crc.sgml.Text;

import crc.util.regexp.RegExp;
import crc.util.regexp.MatchInfo;
import java.util.Enumeration;
import crc.interform.Run;
import w3c.www.http.HTTP;

/** The minimum concrete implementation of the Agent interface.  A
 *	GenericAgent is used if no specialized class can be loaded for
 *	an agent.
 *
 *	@see crc.pia.Agent */
public class GenericAgent extends AttrBase implements Agent {
  
  private String filesep = System.getProperty("file.separator");
  public boolean DEBUG = false;  

 /**
   * Attribute table for storing options
   */
  protected AttrTable attributes = new AttrTable();


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
  protected Table dirTable;

  /**
   * Attribute index - files that this agent can write to
   *
   */
  protected Table fileTable;


  /**
   * A list of match criteria.
   */
  protected Criteria criteria;

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
   * Create a new request given method, url
   */
  public void createRequest(String method, String url, String queryString){
    Transaction request = null;

    if( queryString == null ){
      request =  new HTTPRequest();
      request.fromMachine( machine() );

    }else{
      FormContent c = new FormContent( queryString );
      request = new HTTPRequest( machine(), c, false );

      request.setHeader("Version", version());
      request.setContentType( "application/x-www-form-urlencoded" );
      request.setHeader("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, */*");
      request.setContentLength( queryString.length() );
    }

    request.toMachine( Pia.instance().thisMachine() );
    request.setMethod( method );
    request.setRequestURL( url );

    request.startThread();
  }

  /**
   * Create a new request given method, url
   */
  public void createRequest(Machine m, String method, String url, String queryString){
    Transaction request = null;

    if( queryString == null ){
      request =  new HTTPRequest();
      request.fromMachine( m );

    }else{
      FormContent c = new FormContent( queryString );
      request = new HTTPRequest( m, c, false );
      request.setContentLength( queryString.length() );
    }

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
  public void fileAttribute(String key, List value){
    fileTable.put( key, value ); 
  }

  /**
   * get a file attribute
   */
  public List fileAttribute(String key){
    String v = null;
    List res = null;

    if( fileTable.containsKey( key ) )
      res = (List)fileTable.get(key);
    if( res == null ){
      v = optionAsString( key );
      if ( v!=null && v.startsWith("~/") ){
	  StringBuffer value = null;
	  String home = System.getProperty("user.home");
	  value = new StringBuffer( v.substring(1) );
	  value.insert(0,home);
	  v = new String( value );
	  res = new List();
	  res.push( v );
	  fileTable.put( key, res );
      }else if( v!=null){
	res = new List();
	res.push( v );
	fileTable.put( key, res );
      }
      
    }
    
    Enumeration enum = res.elements();
    String zpath = null;
    while( enum.hasMoreElements() ){
      try{
	zpath = (String)enum.nextElement();
	Pia.instance().debug(this, "file attribute-->"+ zpath);
      }catch(Exception e){}
    }
    return res;
  }

  /**
   * set a directory attribute
   *
   */
  public void dirAttribute(String key, List value){
    dirTable.put( key, value ); 
  }

  /**
   * retrieve a directory attribute. 
   * Makes sure that it ends in a file separator character.
   */
  public List dirAttribute(String key){
    String v = null;
    List res = null;

    if( dirTable.containsKey( key ) )
      res = (List)dirTable.get(key);
    if( res== null ){
      v = optionAsString( key );
      if ( v!=null && v.startsWith("~/") ){
	  StringBuffer value = null;
	  String home = System.getProperty("user.home");
	  value = new StringBuffer( v.substring(1) );
	  value.insert(0,home);
	  v = new String( value );
	  if(!v.endsWith(filesep) ) { v = v + filesep; }
	  res = new List();
	  res.push( v );
	  dirTable.put( key, res );
      }else if( v!=null){
	res = new List();
	res.push( v );
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
    List directories = dirAttribute("agent_directory");
    if( directories!=null && directories.nItems() > 0)
      return (String)directories.at(0);

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
	  List dirs = new List();
	  dirs.push( dir );
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
   * the features themselves are maintained by a Features object
   * attached to each transaction.
   */
  public Criteria criteria(){
    return criteria;
  }

  /** 
   * Add a match criterion to our list of criteria;
   */
  public void matchCriterion(Criterion c) {
    if (criteria == null) criteria = new Criteria();
    criteria.push(c);
  }

  /**
   * Set a match criterion that exactly matches a given value.
   * feature is string naming a feature
   * value is 0,1 (exact match--for don't care, omit the feature)
   */
  public void matchCriterion(String feature, Object value) {
    matchCriterion(Criterion.toMatch(feature, value));
  }
  
  /**
   * Set a boolean match criterion.
   */
  public void matchCriterion(String feature, boolean test) {
    matchCriterion(Criterion.toMatch(feature, test));
  }

  /**
   * Set a match criterion from a string of the form name=value.
   */
  public void matchCriterion(String match) {
    matchCriterion(Criterion.toMatch(match));
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
  public void respond(Transaction trans, Resolver res)
       throws PiaRuntimeException{
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
      throw new PiaRuntimeException(this, "respond",
				    "No InterForm file found for "+trans.url());
    }
  }

  /************************************************************************
  ** Attrs interface: 
  ************************************************************************/

  /** Return the number of defined. */
  public synchronized int nAttrs() {
    return attributes.nAttrs();
  }

  /** Test whether an attribute exists. */
  public synchronized  boolean hasAttr(String name) {
    return attributes.hasAttr(name);
  }
  
  /** Retrieve an attribute by name.  Returns null if no such
   *	attribute exists. */
  public synchronized SGML attr(String name) {
    return attributes.attr(name);
  }

  /** Enumerate the defined attributes. */
  public java.util.Enumeration attrs() {
    return attributes.attrs();
  }

  /** Set an attribute. */
  public synchronized void attr(String name, SGML value) {
    attributes.attr(name, value);
  }
  
  /**
   * Options are strings stored in attributes.  Options may have
   * corresponding features derived from them, which we compute on demand.
   */
  public void option(String key, String value) throws NullPointerException {
    if( key == null || value == null )
      throw new NullPointerException("Key or value can not be null.");

      attributes.attr( key, value );
  }

  /**
   * Return an option's value 
   *
   */
  public String optionAsString(String key){
    if( key == null ) return null;
    return attributes.attrString(key);
  }


  /**
   * @return an option's value as boolean 
   * @return false if key not found or null
   */
  public boolean optionAsBoolean(String key){
    if( key == null ) return false;
    if( attributes.containsKey( key ) ){
      String v = attributes.attrString( key );
      if (v == null) return false;
      if ("false".equalsIgnoreCase(v)) return false;
      return true;
    } else
      return false;
  }


  /**
   * Set options with a hash table
   *
   */
  public void parseOptions(Table hash){
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
    
    List if_path = dirAttribute( "if_path" );
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
      
      List roots = dirAttribute( "if_root" );
      String root;
      if ( roots!= null && roots.nItems() > 0 ){
	// handle a user-defined root first:
	
	root = (String)roots.at(0);
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
      
      
      List list = new List();
      for(int i=0; i<pathVector.size(); i++){
	String onePath = (String)pathVector.elementAt(i);
	list.push( onePath );
	if( DEBUG )
	  System.out.println("GenericAgent findInterform-->" + (String)onePath );
      }
      dirAttribute("if_path", list );
      
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
    Transaction abort = new HTTPResponse(Pia.instance().thisMachine,
					 req.fromMachine(), ct, false);
    abort.setStatus(HTTP.NOT_FOUND);
    abort.setContentType( "text/html" );
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
    dirTable = new Table();
    fileTable = new Table();
    name("GenericAgent");
    type("GenericAgent");
  }

  public GenericAgent(String name, String type){
    dirTable = new Table();
    fileTable = new Table();

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

















