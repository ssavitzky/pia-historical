// GenericAgent.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.pia;

import crc.tf.UnknownNameException;
import java.io.File;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.StringBufferInputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.net.MalformedURLException;
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
  public static boolean DEBUG = false;  

  /** If true, run the request for <code>initialize.if</code> through the 
   *	Resolver.  Otherwise, run the interpretor over it directly.
   */
  public static boolean RESOLVE_INITIALIZE = false;

  /**
   * Attribute table for storing options
   */
  protected AttrTable attributes = new AttrTable();


  /**
   * Attribute index - name of this agent
   */
  protected String agentname;

  /**
   * Attribute index - type of this agent
   */
  protected String agenttype;

  /**
   * Attribute index - type of this agent
   */
  protected String version;

  /**
   * Attribute index - directory that this agent can write to
   */
  protected Table dirTable;

  /**
   * Attribute index - files that this agent can write to
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
   * Act-on Hook.  A pre-parsed piece of InterForm code that is run when 
   *	a transaction is matched by the agent.  Initialized by setting the 
   *	agent's <code>act-on</code> or <code>_act_on</code> attribute.
   */
  protected SGML actOnHook;

  /**
   * Handle Hook.  A pre-parsed piece of InterForm code that is run when 
   *	a transaction is being satisfied by the agent.  Initialized by 
   *	setting the agent's <code>handle</code> or <code>_handle</code> 
   *	attribute.
   */
  protected SGML handleHook;


  /************************************************************************
  ** Initialization:
  ************************************************************************/

  /** Initialization.  Subclasses may override, but this should rarely
   *	be necessary.  If they <em>do</em> override this method it is
   *	important to call <code>super.initialize()</code>.
   */
  public void initialize(){
    String n = name();
    String t = type();
    String url = "/" + n + "/" + "initialize.if";
    Transaction request;

    if( t != null && !n.equalsIgnoreCase( t ) ) 
      type( t );

    attr("name", n);
    attr("type", type());

    if( DEBUG ) {
      System.out.println("[GenericAgent]-->"+"Hi, I am in debugging mode." +
			 "No interform request is put onto the resolver.");
      return;
    }

    if (RESOLVE_INITIALIZE) {
      createRequest("GET", url, null );
    } else {
      /* Run the initialization in the current thread to ensure that 
       * agents are initialized in the correct order and that no requests
       * are made on partially-initialized agents.
       */
      String fn = findInterform("initialize.if");
      if (fn == null) return;
      try {
	Run.interformSkipFile(this, fn,
			      makeRequest(machine(), "GET", url, null),
			      Pia.instance().resolver());
      } catch (Exception e) {
	System.err.println(e.toString());
	e.printStackTrace();
	System.err.println("PIA recovering.");
      }
    }
  }

  /************************************************************************
  ** Creating and Submitting Requests:
  ************************************************************************/

  /**
   * Create a new request given method, url, query.  The results are tossed
   * on the floor.
   */
  public void createRequest(String method, String url, String queryString){
    makeRequest(machine(), method, url, queryString).startThread();
  }

  /**
   * Create a new request given destination machine, method, url, queryString.
   */
  public void createRequest(Machine m, String method, String url,
			    String queryString) {
    makeRequest(m, method, url, queryString).startThread();
  }

  /** Make a new request Transaction on this agent. */
  public Transaction makeRequest(Machine m, String method, String url, 
				 String queryString) {
    Pia.debug(this, "makeRequest -->"+method+" "+url 
	      + ((queryString == null)? "" : "?"+queryString));

    Transaction request = null;

    if( queryString == null ){
      request =  new HTTPRequest();
      request.fromMachine( m );
    } else if ("GET".equalsIgnoreCase(method)) {
      request =  new HTTPRequest();
      request.fromMachine( m );
      url += "?" + queryString;
    } else {
      FormContent c = new FormContent( queryString );
      request = new HTTPRequest( m, c, false );

      request.setHeader("Version", version());
      request.setContentType( "application/x-www-form-urlencoded" );
      request.setHeader("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, */*");
      request.setContentLength( queryString.length() );
    }

    request.toMachine( Pia.instance().thisMachine() );
    request.setMethod( method );
    request.setRequestURL( url );
    return request;
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

  // === file attributes: used only in Dofs (for root) ===

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
      v = attrString( key );
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
      v = attrString( key );
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
   *  usrRoot/agentName/  --> example, ~/Joe/pia/myHistory/ ( ~/Joe/pia is usrRoot ) 
   *  usrRoot/agentType/  --> example, ~/Joe/pia/History/   ( ~/Joe/pia is usrRoot ) 
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
    String root = Pia.instance().usrRoot();

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

    Pia.errLog( name()+ "could not find appropriate, writable directory");
    return null;
  }


  /**
   *  Returns a path to a directory that we can write InterForms into.
   *  Creates one if necessary, starting with the following directory in order:
   *  usrRoot/Agents/agentName/,
   *  usrRoot/Agents/agentType/,
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
    String root = Pia.instance().usrRoot();
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

    Pia.errLog( name()+ "could not find appropriate, writable directory");
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
    Pia.debug(this, name()+" match "+feature+"="+value);
    matchCriterion(Criterion.toMatch(feature, value));
  }
  
  /**
   * Set a boolean match criterion.
   */
  public void matchCriterion(String feature, boolean test) {
    Pia.debug(this, name()+" match "+feature+"?"+test);
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
    Pia.debug(this, "Getting agent machine" );
    if( virtualMachine==null ){
      virtualMachine = new AgentMachine( (Agent)this );
      Pia.debug(this, "Creating virtual agent machine" );
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
   */
  public void actOn(Transaction ts, Resolver res){
    if (actOnHook != null) {
      Pia.debug(this, name()+".actOnHook", "="+actOnHook.toString());
      Run.interformHook(this, actOnHook, name()+".act-on", ts, res);
    }
  }

  /**
   * Handle a transaction matched by an act_on method. 
   * Requests directly _to_ an agent are handled by its Machine;
   * the "handle" method is used only by agents like "cache" that
   * may want to intercept a transaction meant for somewhere else.
   */
  public boolean handle(Transaction ts, Resolver res) {
    if (handleHook != null) {
      Pia.debug(this, name()+".handleHook", "="+handleHook.toString());
      Run.interformHook(this, handleHook, name()+".act-on", ts, res);
      return true;
    } else
      return false;
  }


  /**
   * Respond to a direct request.
   * 	This is called from the agent's AgentMachine
   */
  public void respond(Transaction trans, Resolver res)
       throws PiaRuntimeException{

    crc.pia.Pia.debug(this, "Running interform...");
    if (! respondToInterform( trans, res ) ){
      respondNotFound( trans, trans.requestURL() );
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
    return attributes.hasAttr(name.toLowerCase());
  }
  
  /** Retrieve an attribute by name.  Returns null if no such
   *	attribute exists. */
  public synchronized SGML attr(String name) {
    return attributes.attr(name.toLowerCase());
  }

  /** Enumerate the defined attributes. */
  public java.util.Enumeration attrs() {
    return attributes.attrs();
  }

  /** Set an attribute. */
  public synchronized void attr(String name, SGML value) {
    name = name.toLowerCase();
    attributes.attr(name, value);
    if (name.equals("act-on") || name.equals("_act_on")) {
      actOnHook = value;
      Pia.debug(this, "Setting ActOn hook", ":="+value.toString());
    } else if (name.equals("handle") || name.equals("_handle")) {
      handleHook = value;
      Pia.debug(this, "Setting handle hook", ":="+value.toString());
    }
  }

  /**
   * Set options with a hash table (typically a form).
   *	Ignore the <code>agent</code> option, which comes from the fact
   *	that most install forms use it in place of <code>name</code>.
   *	The Agency will accept either.
   */
  public void parseOptions(Table hash){
    if (hash == null) return;
    Enumeration e = hash.keys();
    while( e.hasMoreElements() ){
      Object keyObj = e.nextElement();
      String key = (String)keyObj;
      // Ignore "agent", which is replaced by "name".
      if (key.equalsIgnoreCase("agent")) continue;
      String value = (String)hash.get( keyObj );
      attr( key, value );
    }
  }

  /************************************************************************
  ** Finding and Executing InterForms:
  ************************************************************************/

  /**
   * Send redirection to client
   */
  protected boolean redirectTo( Transaction req, String path ) {
    URL oldUrl = req.requestURL();

    URL redirUrl = null;
    String redirUrlString = null;

    try{
      redirUrl = new URL(oldUrl, path);
      redirUrlString = redirUrl.toExternalForm();
      Pia.debug(this, "The redirected url-->" + redirUrlString);
    }catch(MalformedURLException e){
      String msg = "Malformed URL redirecting to "+path;
      throw new PiaRuntimeException(this, "redirectTo", msg);
    }

    String msg ="Redirecting " + oldUrl.toExternalForm()
      + " to:" + redirUrlString; 

    Pia.debug(this, msg);

    Content ct = new ByteStreamContent( new StringBufferInputStream(msg) );
    Transaction response = new HTTPResponse( Pia.instance().thisMachine,
					     req.fromMachine(), ct, false);
    response.setHeader("Location", redirUrlString);
    response.setStatus(HTTP.MOVED_PERMANENTLY);
    response.setContentLength( msg.length() );
    response.startThread();
    return true;
  }

  /**
   * Test whether an InterForm request is a redirection.
   * @return true if the request has been handled.
   */
  protected boolean isRedirection( Transaction req, String path ) {
    String originalPath = null;
    URL redirUrl = null;
    String redirUrlString = null;
    URL url = req.requestURL();

    if ( path == null ) return false;

    Pia.debug(this, "  path on entry -->"+ path);
    // === path, name, and typed were all getting lowercased.  Wrong!

    String myname = name();
    String mytype = type();

    // default to index.if

    originalPath = path;

    if (path.equals("/")) {
      path = "/Agency/ROOTindex.if";
    } else if (path.equals("/" + myname)
	       || path.equals("/" + mytype)
	       || path.equals("/" + mytype + "/" + myname) ) {
      path += "/home.if";
    } else if (path.equals("/" + myname + "/")
	       || path.equals("/" + mytype + "/")
	       || path.equals("/" + mytype + "/" + myname + "/") ) {
      path += "index.if";
    }

    if( originalPath == path ) // we don't have redirection
      return false;

    // check for existence
    String wholePath = findInterform( path );
    if( wholePath == null ){
      respondNotFound(req, path);
    } else {
      redirectTo(req, path);
    }
    return true;
  }

  /** 
   * Return a suitable path list for InterForm search.
   *  The path puts any  defined if_root first 
   *   (if_root/myname, if_root/mytype/myname if_root/mytype, if_root),
   *  If the above is not defined, it will try:
   *    .../name, .../type/name, .../type
   *    relative to each of (usrAgentsStr, piaAgentsStr)
   *
   * and finally  (usrAgentsStr, piaAgentsStr)
   */
  public List interformSearchPath() {
    List path = dirAttribute("if_path");
    if (path != null) return path;

    /* The path attribute wasn't defined, so do it now. */

    path = new List();

    /* Tails: type/name, name, type 
     *	 Check type/name first because it's the most specific.  That way,
     *	 sub-agents don't interfere with top-level agents with the same name.
     */

    String myname = name();
    String mytype = type();

    List tails = new List();

    if (!myname.equals(mytype)) tails.push(mytype + filesep + myname + filesep);
    tails.push(myname + filesep);
    if (!myname.equals(mytype)) tails.push(mytype + filesep);

    /* Roots: if_root, ~/.pia/Agents, pia/Agents, pia/src/Agents */

    List roots = dirAttribute( "if_root" );
    if (roots == null) roots = new List();

    for (int i = 0; i < roots.nItems(); ++i) {
      int fslength = filesep.length();

      // handle a user-defined root first:  Trim a trailing /name or /type
      // because it gets automatically added below.
	
      String root = (String)roots.at(i);
      if ( !root.endsWith( filesep ) ) { root = root + filesep; }
      if ( root.endsWith( filesep + myname + filesep )) {
	root = root.substring(0, root.length() - myname.length() - fslength);
      } else if ( root.endsWith( filesep + mytype + filesep )) {
	root = root.substring(0, root.length() - mytype.length() - fslength);
      }

      roots.at(i, root);
    }	

    roots.push(Pia.instance().usrAgents());
    roots.push(Pia.instance().piaAgents());
    roots.push(Pia.instance().piaRoot() +
	       filesep + "src" + filesep + "Agents" + filesep);

    /* Make sure all the roots end in filesep */

    for (int i = 0; i < roots.nItems(); ++i) {
      String root = (String)roots.at(i);
      if ( !root.endsWith(filesep) ) { roots.at(i, root + filesep); }
    }	

    /* Now combine the roots and tails
     *	Do all the roots for each tail so that , for example, 
     *	usr/name/x will override pia/type/x
     */

    for (int i = 0; i < tails.nItems(); ++i) 
      for (int j = 0; j < roots.nItems(); ++j)
	path.push(roots.at(j).toString() + tails.at(i).toString());
    
    /* Finally, try just the roots */

    for (int j = 0; j < roots.nItems(); ++j)
      path.push(roots.at(j).toString());

    if(DEBUG )
      for(int i=0; i < path.nItems(); i++){
	String onePath = path.at(i).toString();
	System.out.println("GenericAgent findInterform-->"+(String)onePath );
      }

    // Now cache the lookup path list as a dirAttribute

    dirAttribute("if_path", path );
    return path;
  }

  /**
   * Find a filename relative to this Agent.
   */
  public String findInterform(String path){
    if ( path == null ) return null;
    return findInterform(path, name(), type(), interformSearchPath());
  }

  /**
   * Find a filename relative to an arbitrary agent.
   */
  public String findInterform(String path, String myname, String mytype,
			      List if_path){
    if ( path == null ) return null;
    Pia.debug(this, "  path on entry -->"+ path);

    boolean hadName = false;	// these might be useful someday.
    boolean hadType = false;
    
    /* Remove a leading /type or /name or /type/name from the path. */

    if (path.startsWith("/" + mytype + "/")) {
      path = path.substring(mytype.length() + 1);
      hadType = true;
    }
    
    if (path.startsWith("/" + myname + "/")) {
      path = path.substring(myname.length() + 1);
      hadName = true;
    }
    
    if( path.startsWith("/") )	path = path.substring(1);
    Pia.debug(this, "Looking for -->"+ path);

    File f;
    Enumeration e = if_path.elements();
    while( e.hasMoreElements() ){
      String zpath = e.nextElement().toString();
      String wholepath = zpath + path;
      Pia.debug(this, "  Trying -->"+ wholepath);
      f = new File( wholepath );
      if( f.exists() ) return wholepath;
    }
    
    return null;
}

  /** 
   * Send an error message that includes the agent's name and type.
   */
  public void sendErrorResponse( Transaction req, int code, String msg ) {
    msg = "Agent=" + name()
      + (! name().equals(type())? " Type=" + type() : "")
      + "<br>\n"
      + msg ;
    req.errorResponse(code, msg);
  }

  /**
   * Send error message for not found interform file
   */
  public void respondNotFound( Transaction req, URL url){
    String msg = "No InterForm file found for <code>" +
      url.getFile() + "</code>.  "
      + "See this agent's <a href=\"/" + name() + "/\">index page</a> "
      + "for a more information.";
    sendErrorResponse(req, HTTP.NOT_FOUND, msg);
  }

  /**
   * Send error message for not found interform file
   */
  public void respondNotFound( Transaction req, String path){
    String msg = "File <code>" + path + "</code> not found. "
      + "See this agent's <a href=\"/" + name() + "/\">index page</a> "
      + "for a more information.";
    sendErrorResponse(req, HTTP.NOT_FOUND, msg);
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
   *
   * @return false if the file cannot be found.  This allows the caller
   *	to check for an InterForm first, then do something different with
   *	other requests.
   */
  public boolean respondToInterform(Transaction request, Resolver res){

    URL url = request.requestURL();
    if (url == null) return false;

    return respondToInterform(request, url.getFile(), res);
  }

  /**
   * Respond to a request directed at one of an agent's interforms, 
   * with a (possibly-modified) path.
   *
   * @return false if the file cannot be found.
   */
  public boolean respondToInterform(Transaction request, String path,
				    Resolver res){

    // If the request needs to be redirected, do so now.
    if (isRedirection( request, path )) return true;

    // If the path includes a query string, remove it now
    int end = path.indexOf('?');
    if(end > 0) path = path.substring(0, end);
      

    // Find the file.  If not found, return false.
    String file = findInterform( path );
    if( file == null ) return false;
      
    if( file != null )
      Pia.debug(this, "The path of interform is -->"+file);

    String interformOutput = null;
      
    if( file.endsWith(".if") ){
      // If find_interform substituted .../home.if for .../ 
      // we have to tell what follows that it's an interform.
      request.assert("interform");
    }
    if( request.test("interform") ){
      try{
	InputStream in =  Run.interformFile(this, file, request, res);
	sendStreamResponse(request, in);
      }catch(PiaRuntimeException ee ){
	throw ee;
      } catch (Exception e) {
	System.err.println(e.toString());
	e.printStackTrace();
	System.err.println("PIA recovering.");
	throw new PiaRuntimeException(this, "respondToInterform",
				      "Exception in InterForm: "
				      + e.toString());
      }
    } else if( file.endsWith(".cgi") ){
      try{
	execCgi( request, file );
      }catch(PiaRuntimeException ee ){
	throw ee;
      }
    } else {
      crc.pia.FileAccess.retrieveFile(file, request, this);
    }
    return true;
    }

  protected void execCgi( Transaction request, String file ) throws PiaRuntimeException
  {
    Runtime rt = Runtime.getRuntime();
    Process process = null;
    InputStream in;
    PrintStream out;

    try{
      String[] envp = setupEnvironment( request );

      process = rt.exec( file, envp );

      if( request.method() == "POST" ){
	out = new PrintStream( process.getOutputStream() );
	out.print( request.queryString() );
	out.flush();
      }

      in = process.getInputStream();

      Content ct = new ByteStreamContent( in );
      Transaction response = new HTTPResponse( request, ct);
      
    }catch(IOException ee){
      String msg = "can not exec :"+file;
      throw new PiaRuntimeException (this, "respondToInterform", msg) ;
    }
  }

  /**
   * Prepare environment variables for CGI
   */
  protected String[] setupEnvironment(Transaction req){
    String path = req.requestURL().getFile();

    String[] envp = new String[9];
    envp[0]="CONTENT_TYPE=";
    envp[1]="CONTENT_LENGTH=";
    
    if( req.method() == "POST" ){
      envp[0] += req.contentType();
      envp[1] += req.contentLength();
    }
    envp[2]="GATEWAY_INTERFACE=" + "CGI/1.0";
    envp[3]="SERVER_PORT="       + Pia.instance().port();
    envp[4]="SERVER_PROTO="      + req.version();
    envp[5]="REQUEST_METHOD="    + req.method();
    envp[6]="REMOTE_ADDR=";
    envp[7]="QUERY_STRING=";
    
    if( req.method() == "GET" )
      envp[7] += req.queryString();
    
    envp[8]="PATH_INFO="	+ path;

    for(int i = 0; i < envp.length; i++){
      Pia.debug(this, "The environment var -->" + envp[i]);
    }
    return envp;
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
  }

}










