// GenericAgent.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.pia;

import crc.tf.UnknownNameException;
import java.io.File;
import java.io.InputStream;

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

import java.util.Enumeration;
import crc.interform.Run;
import w3c.www.http.HTTP;

/** The minimum concrete implementation of the Agent interface.  A
 *	GenericAgent is used if no specialized class can be loaded for
 *	an agent; it also serves as the base class for all known Agent
 *	implementations.
 *
 *	@see crc.pia.Agent
 */
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
   * A list of match criteria.  These are matched against the
   * Features of every Transaction to see if this Agent's actOn method
   * should be called.
   */
  protected Criteria criteria;

  /**
   * Attribute index - virtual Machine to which local requests are directed.
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
   * Create a new request given method, url, query.  The results are tossed
   * on the floor.
   */
  public void createRequest(String method, String url, String queryString){
    Transaction request = null;

    if( queryString == null ) {
      request =  new HTTPRequest();
      request.fromMachine( machine() );
    } else if ("GET".equalsIgnoreCase(method)) {
      request =  new HTTPRequest();
      request.fromMachine(machine());
      url += queryString;
    } else {
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
   * Create a new request given destination machine, method, url, queryString.
   */
  public void createRequest(Machine m, String method, String url,
			    String queryString) {
    Transaction request = null;

    if( queryString == null ){
      request =  new HTTPRequest();
      request.fromMachine( m );
    } else if ("GET".equalsIgnoreCase(method)) {
      request =  new HTTPRequest();
      request.fromMachine( m );
      url += queryString;
    } else {
      FormContent c = new FormContent( queryString );
      request = new HTTPRequest( m, c, false );
      request.setContentLength( queryString.length() );
    }

    request.toMachine( Pia.instance().thisMachine() );
    request.setMethod( method );
    request.setRequestURL( url );

    request.startThread();
  }
 
  /************************************************************************
  ** Access to fields:
  ************************************************************************/

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

  /************************************************************************
  ** File attributes:
  ************************************************************************/

  // === file attributes don't seem to be used at all ===

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
      if ( v!=null && v.startsWith("~"+filesep) ) {
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
   *  Returns a path to a directory that we can write data into.
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

    String name = name();
    String type = type();
    String root = Pia.instance().piaUsrRoot();

    String[] possibilities = { root + filesep + name() + filesep,
			       root + filesep + type() + filesep,
			       filesep + "tmp" + filesep + name() + filesep };

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
   *  Returns a path to a directory that we can write InterForms into.
   *  Creates one if necessary, starting with the following directory in order:
   *  piaUsrRoot/Agents/agentName/,
   *  piaUsrRoot/Agents/agentType/,
   *  /tmp/Agents/agentName
   * @return the first qualified directory out of the possible three above.
   * A directory is qualified if it can be writen into.
   */
  public String agentIfDir(){
    List directories = dirAttribute("agent_if_directory");
    if( directories!=null && directories.nItems() > 0)
      return (String)directories.at(0);

    String name = name();
    String type = type();
    String root = Pia.instance().piaUsrRoot();
    root += filesep + "Agents";

    String[] possibilities = { root + filesep + name() + filesep,
			       root + filesep + type() + filesep,
			       filesep + "tmp" + filesep + name() + filesep };

    for(int i = 0; i < possibilities.length; i++){
      String dir = possibilities[i];

      File myFileDir = new File( dir );
      if( myFileDir.exists() || myFileDir.mkdir() ){
	if( myFileDir.isDirectory() && myFileDir.canWrite() ){
	  List dirs = new List();
	  dirs.push( dir );
	  dirAttribute( "agent_if_directory", dirs );
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

  /************************************************************************
  ** Matching Features:
  ************************************************************************/

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


  /************************************************************************
  ** Machine: 
  ************************************************************************/

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

  /************************************************************************
  ** Actions and Hooks:
  ************************************************************************/

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

    if (! respondToInterform( trans, zurl, res ) ){
      interformErr( trans, zurl );
      //throw new PiaRuntimeException(this, "respond",
      //			    "No InterForm file found for "+trans.url());
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

  // === option, optionAsXXX are now unnecessary.

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

  /************************************************************************
  ** Finding and Executing InterForms:
  ************************************************************************/

  /**
   * Find an interform, using a simple search path and a crude kind
   * of inheritance.  Allow for the fact that the user may be trying
   * to override the interform by putting it in piaUsrAgentsStr/name/.
   */
  public String findInterform( URL url, boolean noDefault ){
    if( url == null ) return null;

    /* === this is totally bogus!  host must be hostname, not agent name...
    String host =  url.getHost();
    if( host!= null && !host.equalsIgnoreCase( name() ))
	return null;
    === */

    String path = url.getFile();
    return findInterform(url.getFile(), noDefault);
  }

  /**
   * Find an interform starting with a string pathname.
   */
  public String findInterform( String path, boolean noDefault ){
    if ( path == null ) return null;

    // === path, name, and typed were all getting lowercased.  Wrong!

    String myname = name();
    String mytype = type();
    
    // Find interform name in URL.
    
    // === This is wrong.  We should use redirect instead. ===

    if ( noDefault ){
      // are v doing defaults? no no
    }
    else { // default to index.if
      if (path.equals("/")) {
	path = "/ROOTindex.if";
      } else if (path.equals("/" + myname)) {
	path += "/home.if";
      } else if (path.equals("/" + myname + "/")) {
	path += "index.if";
      }
    }

    Pia.instance().debug(this, "Looking for -->"+ path);

    /* Remove a leading /type or /name or /type/name from the path. */

    if (path.startsWith("/" + mytype + "/")) 
      path = path.substring(mytype.length() + 1);
    
    if (path.startsWith("/" + myname + "/")) 
      path = path.substring(myname.length() + 1);
    

    List if_path = dirAttribute( "if_path" );
    if ( if_path == null ) {
      if_path = new List();

      /*
       * If the path isn't already defined, set it up now.
       *
       * === Should also try .../type/name/...
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

	// handle a user-defined root first:  Trim a trailing /name or /type
	// because it gets automatically added below.
	
	root = (String)roots.at(0);
	if ( !root.endsWith( filesep ) ) { root = root + filesep; }
	if ( root.endsWith( filesep + myname + filesep )) {
	  root = root.substring(0, root.length() - myname.length() -
				filesep.length());
	} else if ( root.endsWith( filesep + mytype + filesep )) {
	  root = root.substring(0, root.length() - mytype.length() -
				filesep.length());
	}

	if_path.push( root+myname+filesep );
	if( myname != mytype )
	 if_path.push( root+mytype+filesep );
	if_path.push( root );
      }	

      /*
       * Then see whether the user has overridden the form.
       *    It's possible that one of these will be a duplicate.
       *    That slows us down, but not much.
       */
      
      root = Pia.instance().piaUsrAgents();
      if ( !root.endsWith( filesep ) ) { root = root + filesep; }
      if_path.push( root+myname+filesep );

      if ( myname != mytype )
	if_path.push( root+mytype+filesep );

      if_path.push( home+myname+filesep );

      if( myname != mytype )
	if_path.push( home+ mytype+filesep );

      if_path.push( root );
      if_path.push( home );
      
      
      if( DEBUG )
	for(int i=0; i < if_path.nItems(); i++){
	  String onePath = if_path.at(i).toString();
	  System.out.println("GenericAgent findInterform-->"+(String)onePath );
	}

      // Now cache the lookup path list as a dirAttribute

      dirAttribute("if_path", if_path );
    }

    File f;
    Enumeration e = if_path.elements();
    while( e.hasMoreElements() ){
      String zpath = (String)e.nextElement();
      String wholepath = zpath + path;
      Pia.instance().debug(this, "  Trying -->"+ wholepath);
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
    req.errorResponse(HTTP.NOT_FOUND, msg);
  }

  /**
   * Respond to a transaction with a stream of HTML.
   */
  public void sendStreamResponse ( Transaction trans, InputStream in ) {

    ByteStreamContent c = new ByteStreamContent( in );

    Transaction response = new HTTPResponse( trans, false );
    response.setStatus( 200 ); 
    response.setContentType( "text/html" );
    response.setContentObj( c );
    response.startThread();
  }

  /**
   * Respond to a request directed at one of an agent's interforms.
   * The InterForm's url may be passed separately, since the agent may
   * need to modify the URL in the request.
   *
   * @return false if the file cannot be found.
   */
  public boolean respondToInterform(Transaction request, URL url, Resolver res){

    if (url == null) url = request.requestURL();
    String interformOutput = null;

    Pia.instance().debug(this, "respondToInterform: url is -->" +
			 url.getFile());

    String file = findInterform( url, false );
    Pia.instance().debug(this, "The path of interform is -->"+file);

    if( file == null ) {    //send error response
      return false;
    } 

    if( file.endsWith(".if") ){
      // If find_interform substituted .../home.if for .../ 
      // we have to tell what follows that it's an interform.
      request.assert("interform");
    }
    if( request.test("interform") ){
      InputStream in =  Run.interformFile(this, file, request, res);
      sendStreamResponse(request, in);
    } else if( file.endsWith(".cgi") ){
      // === CGI should be executed with the right stuff in the environment.
      String msg = "cgi invocation unimplemented.";
      throw new PiaRuntimeException (this, "respondToInterform", msg) ;
    } else {
      crc.pia.agent.Dofs.retrieveFile(file, request, this);
    }

    return true;
  }
  

  /************************************************************************
  ** Construction:
  ************************************************************************/

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










