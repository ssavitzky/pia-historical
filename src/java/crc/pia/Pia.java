// Pia.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.pia;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.Properties;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import crc.pia.PiaInitException;
import crc.pia.Machine;
import crc.pia.agent.Agency;
import crc.pia.Resolver;
import crc.pia.Logger;
import crc.pia.Transaction;
import crc.pia.agent.AgentInstallException;

import crc.ds.Table;
import crc.ds.List;
 /**
  * Pia stores all of the information relevant to the creation of an 
  * angency.
  */

public class Pia{
  /**
   * where to proxy
   */
  public static final String PIA_PROXIES = "crc.pia.proxy_";

  /**
   * a list of scheme not to proxy
   */
  public static final String PIA_NO_PROXIES = "crc.pia.no_proxy";


  /**
   * Name of path of pia properties
   */
  public static final String PIA_PROP_PATH = "crc.pia.profile";

  /**
   * Name of URL of pia doc
   */
  public static final String PIA_DOCURL = "crc.pia.docurl";

  /**
   * Name of pia directory from which other directories derive
   */
  public static final String PIA_ROOT = "crc.pia.root";

  /**
   * Name of server port
   */
  public static final String PIA_PORT = "crc.pia.port";

  /**
   * Name of this host
   */
  public static final String PIA_HOST = "crc.pia.host";

  /**
   * Name of user's agent directory
   */
  public static final String PIA_USR_ROOT = "crc.pia.usrroot";

  /**
   * Name of debugging flag
   */
  public static final String PIA_DEBUG = "crc.pia.debug";

  /**
   * Name of verbose flag
   */
  public static final String PIA_VERBOSE = "crc.pia.verbose";

  /**
   * Name of pia logger class
   */
  public static final String PIA_LOGGER = "crc.pia.logger";


  /**
   * Name of pia logger class
   */
  public static final String PIA_REQTIMEOUT = "crc.pia.reqtimeout";



  private Properties    piaFileMapping = null;
  private Piaproperties properties     = null;
  private static Pia     instance      = null;
  private String  docurl               = null;
  private static Logger  logger        = null;
  private String  loggerClassName = "crc.pia.Logger";
  
  
  private String  rootStr    = null;
  private File    rootDir    = null;
  
  private String  piaUsrRootStr = null;
  private File    piaUsrRootDir = null;
  
  private String  url        = null;
  private String  host       = null;
  private int     port       = 8001;
  private int     reqTimeout = 50000;
  private static boolean verbose = true;
  private static boolean debug   = true;  // always print to screen or file if debugToFile is on
  private static boolean debugToFile= false;
  

  private Table proxies           = new Table();
  private List  noProxies        = null;

  private String piaAgentsStr    = null;
  private File   piaAgentsDir    = null;

  private String piaUsrAgentsStr = null;
  private File  piaUsrAgentsDir = null;

  // file separator
  private String filesep  = System.getProperty("file.separator");
  private String home     = System.getProperty("user.home");
  private String userName = System.getProperty("user.name");

  /**
   * Attribute index - accepter
   */
  protected Accepter accepter;

  /**
   * Attribute index - thread pool
   */
  protected ThreadPool threadPool;

  /**
   *  Attribute index - this machine
   */
  protected Machine thisMachine;

  /**
   *  Attribute index - resolver
   */
  protected Resolver resolver;

  /**
   *  Attribute index - Agency
   */
  protected Agency agency;


  /**
   * @return file mapping for local file retrieval
   */
  public Properties piaFileMapping(){
    return piaFileMapping;
  }

  /**
   *
   */
  public Piaproperties properties(){
    return properties;
  }

  /**
   * @return thread pool
   */
  public ThreadPool threadPool(){
    return threadPool;
  }

  /**
   * @return this machine
   */
  public Machine thisMachine(){
    return thisMachine;
  }
  
  /**
   * @return resolver
   */
  public Resolver resolver(){
    return resolver;
  }

  /**
   * @return agency
   */
  public Agency agency(){
    return agency;
  }

  /**
   * @return the URL for the documentation
   */
  public String docUrl(){
    return docurl;
  } 

  /**
   * @return the root directory path-- i.e /pia
   */
  public String root(){
    return rootStr;
  }  

 /**
   * @return a File object representing the root directory
   */
  public File rootDir(){
    return rootDir;
  }  

  /**
   * @return the user directory path -- i.e ~/pia
   */
  public String piaUsrRoot(){
    return piaUsrRootStr;
  }  

  /**
   * @return a File object for the user directory path -- i.e ~/pia
   */
  public File piaUsrRootDir(){
    return piaUsrRootDir;
  }  


  /**
   * @return this host name
   */
  public String host(){
    return host;
  } 

  /**
   * @return this port name
   */
  public String port(){
    Integer i = new Integer( port );
    return i.toString();
  } 

  /**
   * @return this port number
   */
  public int portNumber() {
    return port;
  }

  /**
   * @return request time out
   */
  public int requestTimeout(){
    return reqTimeout;
  }
 
  /** @return the debug flag */
  public static boolean debug() {
    return Pia.instance().debug;
  }

  /** @return the verbose flag */
  public static boolean verbose() {
    return Pia.instance().verbose;
  }

  /**
   * @toggle debug flag 
   */
  public static void debug(boolean onoff){
    debug = onoff;
  } 

  /**
   * @toggle debug to file flag
   * true if you want debug message to trace file.
   * Note: this method will not print to file if the Pia is not running
   */
  public static void debugToFile(boolean onoff){
    debugToFile = onoff;
  } 

 /**
   * @return the directory path where agents live
   */
  public String piaAgents(){
    return piaAgentsStr;
  } 

  /**
   * @return a File object for the directory where agents live
   */
  public File piaAgentsDir(){
    return piaAgentsDir;
  } 

  /**
   * @return the directory where user agents live
   */
  public String piaUsrAgents(){
    return piaUsrAgentsStr;
  } 


  /**
   * @return the directory where user agents live
   */
  public File piaUsrAgentsDir(){
    return piaUsrAgentsDir;
  } 

  /**
   * @return the proxy string
   */
  public Table proxies(){
    return proxies;
  } 

  /**
   * @return the no proxy schemes
   */
  public List noProxies(){
    return noProxies;
  } 


  public Pia(){
    instance = this;
  }

  /**
   * Fatal system error -- print message and throw runtime exception.
   * Do a stacktrace.
   */
  public static void errSys(Exception e, String msg){
    System.err.println("Pia: " + msg);
    e.printStackTrace();
    
    throw new RuntimeException(msg);
  }

  /**
   * Fatal system error -- print message and throw runtime exception
   *
   */
  public static void errSys(String msg){
    System.err.println("Pia: " + msg);
    throw new RuntimeException(msg);
  }

  /**
   * Print warning message
   *
   */
  public static void warningMsg( String msg ){
    System.err.println( msg );
  }

  /**
   * Print warning message
   *
   */
  public static void warningMsg(Exception e, String msg ){
    System.err.println( msg );
    e.printStackTrace();
    
  }

  /** 
   * Display a message to the user if the "verbose" flag is set.
   */
  public static void verbose(String msg) {
    if (debug) debug(msg);
    else if (verbose) System.err.println(msg);
  }

  /**
   * Dump a debugging statement to the trace file
   *
   */
  public static void debug( String msg )
  {
    if (!debug) return;
    if( logger != null && debugToFile )
	logger.trace ( msg );
    else
      System.out.println( msg );
  }

  /**
   * Dump a debugging statement to the trace file on behalf of
   * an object
   */
  public static void debug(Object o, String msg) {
    if (!debug) return;
    if( logger != null && debugToFile )
	logger.trace ("[" +  o.getClass().getName() + "]-->" + msg );
    else
      System.out.println("[" +  o.getClass().getName() + "]-->" + msg );
  }

    
  /**
   * Dump a debugging statement to the trace file, with an extra message
   *	if verbose.
   */
  public static void debug( String msg, String vmsg ) {
    if (!debug) return;
    if (verbose) msg = (msg == null)? vmsg : msg + vmsg;
    if (msg == null) return;
    if( logger != null && debugToFile )
	logger.trace ( msg );
    else
      System.out.println( msg );
  }

  /**
   * Dump a debugging statement to the trace file on behalf of
   *	 an object, with an extra message if verbose.
   */
  public static void debug(Object o, String msg, String vmsg) {
    if (!debug) return;
    if (verbose) msg = (msg == null)? vmsg : msg + vmsg;
    if (msg == null) return;
    if( logger != null && debugToFile )
	logger.trace ("[" +  o.getClass().getName() + "]-->" + msg );
    else
      System.out.println("[" +  o.getClass().getName() + "]-->" + msg );
  }

    
  /**
   * Output a message to the log.
   *
   */
  public static void log( String msg )
  {
    if( logger != null )
	logger.log ( msg );
  }

  /**
   * error to log
   *
   */
  public static void errLog( String msg )
  {
    if( logger != null )
	logger.errlog ( msg );
  }

  /**
   * error to log on behalf of an object
   *
   */
  public static void errLog(Object o, String msg )
  {
    if( logger != null )
	logger.errlog ("[" +  o.getClass().getName() + "]-->" + msg );
  }

  /**
   * Get the server URL.
   */

   public String url() {
     if ( url == null ) {
       if ( port != 80 ) 
	 url = "http://" + host + ":" + port ;
       else
	 url = "http://" + host ;
       }		
     return url ;
  }


  public static void usage () {
	PrintStream o = System.out ;

	o.println("usage: PIA [OPTIONS]") ;
	o.println("-port <8001>          : listen on the given port number.");
	o.println("-root <pia dir : /pia>: pia directory.");
	o.println("-u    <~/Agent>       : user directory.") ;
	o.println("-p    </pia/config/pia.props>       : property file to read.");
	o.println("-d                    : turns debugging on.") ;
	o.println("-v                    : print pia Piaproperties.");
	o.println("?                     : print this help.");
	System.exit (1) ;
  }

  public void verboseMessage() {
	PrintStream o = System.out ;

	o.println(rootStr         + " (parent of src, lib, Agents)");
	o.println(piaAgentsStr + " (agent interforms)");
	o.println(piaUsrRootStr   + " (user directory)");
	o.println(piaUsrAgentsStr + " (user interforms)");
	o.println(Integer.toString( requestTimeout() ) + " (request time out)\n");
	o.println(url+"\n");
  }

	/**
	 * Initialize the server logger and the statistics object.
	 */

    private void initializeLogger() throws PiaInitException
    {
	if ( loggerClassName != null ) {
	    try {
		logger = (Logger) Class.forName(loggerClassName).newInstance() ;
		logger.initialize (this) ;
	    } catch (Exception ex) {
		String err = ("Unable to create logger of class ["
			      + loggerClassName +"]"
			      + "\r\ndetails: \r\n"
			      + ex.getMessage());
		throw new PiaInitException(err);
	    }
	} else {
	    warningMsg ("no logger specified, not logging.");
	}
    }

  private void initializeProxies(){
    // i. e. agency.crc.pia.proxy_http=foobar 
    // get keys from properties
    // enumerate thru keys looking for PIA_PROXIES
    // if one found, get string pass underscore; this is the key
    // get from properties its value
    // push k,v to proxies

    Enumeration e =  properties.propertyNames();
    while( e.hasMoreElements() ){
      try{
	String keyEntry = (String) e.nextElement();
	if( keyEntry.indexOf(PIA_PROXIES) != -1 ){
	  String key = keyEntry.substring( keyEntry.indexOf('_') + 1 );
	  String v   = properties.getProperty( keyEntry );
	  proxies.put( key, v );
	}
      }catch( NoSuchElementException ex ){
      }
    }
    String noproxies = properties.getProperty(PIA_NO_PROXIES, null);
    if( noproxies != null ){
      StringTokenizer parser = new StringTokenizer(noproxies, ",");
      try{
	noProxies = new List();
	while(parser.hasMoreTokens()) {
	  String v = parser.nextToken().trim();
	  noProxies.push( v );
	}
      }catch(NoSuchElementException ex) {
      }
    }
      


  }

  private void initializeProperties() throws PiaInitException{
    String thisHost = null;
    String path = null;

    try {
      thisHost = InetAddress.getLocalHost().getHostName();
    }catch (UnknownHostException e) {
      thisHost = null;
    }

    verbose         = properties.getBoolean(PIA_VERBOSE, true);
    debug           = properties.getBoolean(PIA_DEBUG, false);
    rootStr         = properties.getProperty(PIA_ROOT, null);
    piaUsrRootStr   = properties.getProperty(PIA_USR_ROOT, null);
    host            = properties.getProperty(PIA_HOST, thisHost);
    port            = properties.getInteger(PIA_PORT, port);
    reqTimeout      = properties.getInteger(PIA_REQTIMEOUT, 60000);
    loggerClassName = properties.getProperty(PIA_LOGGER, loggerClassName);

    // i. e. agency.crc.pia.proxy_http=foobar 
    // get keys from properties
    // enumerate thru keys looking for PIA_PROXIES
    // if one found, get string pass underscore; this is the key
    // get from properties its value
    // push k,v to proxies

    initializeProxies();

    if( host == null ){
      throw new PiaInitException(this.getClass().getName()
				 +"[initializeProperties]: "
				 +"[host] undefined.");
    }
    
    if( rootStr == null ){
	    File piafile = new File("Pia.java");
	    if( piafile.exists() ){
	      path = piafile.getAbsolutePath();
	      rootStr = path.substring(0, path.indexOf("Pia.java")-1);
	    }
	    else{
	      File piadir = new File(home, "pia");

	      // check if we have a copy of the working directory
	      if ( piadir.exists() && piadir.isDirectory() )
		rootStr = piadir.getAbsolutePath();
	      else
		throw new PiaInitException(this.getClass().getName()
					   +"[initializeProperties]: "
					   +"[pia root directory] undefined.");
	    }
    }

    if ( rootStr.startsWith("~") ){
      rootStr = home + rootStr.substring(1);
    }
    // Now the directories that depend on it:
    rootDir = new File( rootStr );
    
    //  we are at /pia/Agents -- this is for interform
    piaAgentsStr = rootStr + filesep + "Agents";
    piaAgentsDir = new File( piaAgentsStr );

    if( piaUsrRootStr == null ){ 
      if( home!=null && home != "" ){
	// i.e. we have ~/bob and looking for ~/bob/pia
	File dir = new File(home, "pia");
	if( dir.exists() ){
	  piaUsrRootStr = dir.getAbsolutePath(); 
	}
      }
	  
      if( piaUsrRootStr == null ){
	// i.e. we have /pia/users and if bob is valid user's name
	// we have /pia/users/bob
	File usersDir = new File(rootStr,"users");
	if( usersDir.exists() ){
	  if( userName!=null && userName != "" )
	    piaUsrRootStr = usersDir.getAbsolutePath() + filesep + userName; 
	}
	else throw new PiaInitException(this.getClass().getName()
					+"[initializeProperties]: "
					+"[user root directory] undefined.");
      }
    }

    if ( piaUsrRootStr.startsWith("~") ){
      piaUsrRootStr = home + piaUsrRootStr.substring(1);
    }
    piaUsrRootDir = new File( piaUsrRootStr );
	
    piaUsrAgentsStr = piaUsrRootStr + filesep + "Agents";
    piaUsrAgentsDir = new File( piaUsrAgentsStr );

    /* Now set the properties that defaulted. */

    properties.setBoolean(PIA_VERBOSE, verbose);
    properties.setBoolean(PIA_DEBUG, debug);
    properties.setProperty(PIA_ROOT, rootStr);
    properties.setProperty(PIA_USR_ROOT, piaUsrRootStr);
    properties.setProperty(PIA_HOST, host);
    properties.setInteger(PIA_PORT, port);
    properties.setInteger(PIA_REQTIMEOUT, reqTimeout);
    properties.setProperty(PIA_LOGGER, loggerClassName);

	url = url();
	
  }

  private void createPiaAgency() throws IOException{
    thisMachine  = new Machine( host, port, null );
    threadPool   = new ThreadPool();

    resolver     = new Resolver();
    Transaction.resolver = resolver;
    
    agency       = new Agency("Agency", null);
    resolver.registerAgent( agency );
    
    /*
    debug(this, "\n\n------>>>>>>> Installing a Dofs agent <<<<<-----------");
    Table ht = new Table();
    ht.put("agent", "DOFS");
    try{
      agency.install( ht );
    }catch(AgentInstallException e){
      debug(this, "Unable to install: " + e.getMessage() );
    }
    */

    if( verbose )
      verboseMessage();

    try{
      accepter = new Accepter( port );
    }catch(IOException e){
      errSys( e, "Can not create Accepter" );
    }

  }

  public void initialize(Piaproperties cmdProps) throws PiaInitException{
    this.properties = cmdProps;

    initializeProperties();
    initializeLogger();
  }

  protected void cleanup(boolean restart){
    debug(this, "Shutting down accepter...");
    accepter.shutdown();

    /*
    debug(this, "Shutting down threads...");
    threadPool().stop();

    debug(this, "Shutting down resolver...");
    resolver.shutdown();

    // Finally close the log
    if ( logger != null )
      logger.shutdown() ;
    logger = null;


    Piaproperties initProps = properties ;
    properties = null;

    if ( restart ){
      try {
	initialize( initProps );
	accepter.restart();
	resolver.restart();
      } catch(Exception ex){
	System.out.println("*** restart failed.") ;
	ex.printStackTrace() ;
      }
    }
    */

  }
  
  public void shutdown(boolean restart){
    cleanup( restart );
  }

  protected void finalize() throws IOException{
    cleanup( false );
  }

  public static Pia instance(){
    Piaproperties piaprops = null;

    if( instance == null ){
      instance = new Pia();
      try{
	piaprops = instance.loadProperties(null);
	instance.piaFileMapping = instance.loadFileMapping(null);
	instance.initialize(piaprops);
      }catch(Exception e){
	  System.out.println( e.toString() );
      }finally{
	try{
	  instance.createPiaAgency();
	}catch(IOException e2){
	  System.out.println( e2.toString() );
	}
      }

    }


    return instance;
  }

  private static Properties loadFileMapping( String where ){
    String fileMapProp      = null;
    Properties zFileMapping = new Properties();
    File guess              = null;
    File mapFile            = null;

    String filesep  = System.getProperty("file.separator");

    if ( where == null ){
      where = filesep+"pia";
      // Try to guess it, cause it is really required:
      guess = new File (new File( where, "Config" ),"filemap.props");
    }else guess = new File( where );

    fileMapProp = guess.getAbsolutePath();

    try {
      if ( fileMapProp != null ) {
	System.out.println ("loading file mapping from: " + where) ;
	
	File mapfile = new File( fileMapProp ) ;
	zFileMapping.load ( new FileInputStream( mapfile ) );

	// convert everything to lowercase
	Enumeration keys = zFileMapping.keys();
	while( keys.hasMoreElements() ){
	  String k = (String) keys.nextElement();
	  String v = (String) zFileMapping.get( k );
	  zFileMapping.remove( k );

	  zFileMapping.put( k.toLowerCase(), v.toLowerCase() );
	}

      }
    } catch (FileNotFoundException ex) {
      zFileMapping.put("html", "text/html");
      zFileMapping.put("gif", "image/gif");
    } catch (IOException exp){
      zFileMapping.put("html", "text/html");
      zFileMapping.put("gif", "image/gif");
    }

    Enumeration e =  zFileMapping.propertyNames();
    while( e.hasMoreElements() ){
      try{
	String keyEntry = (String) e.nextElement();
	System.out.println("file mapping key :" + keyEntry);
	String v   = zFileMapping.getProperty( keyEntry );
	System.out.println("file mapping value :" + v);
      }catch( NoSuchElementException ex ){
      }
    }

   return zFileMapping;
  }

  private static void setDefaultProperties( Piaproperties piaprops ){
    piaprops.put(PIA_PORT, "8888");
    piaprops.put(PIA_REQTIMEOUT, "50000");
    piaprops.put(PIA_DEBUG, "true");
    piaprops.put(PIA_VERBOSE, "true");
    piaprops.put(PIA_LOGGER, "crc.pia.Logger");
  }
  
  private static Piaproperties loadProperties(String cmdroot)throws IOException{
    String cmdprop      = null;
    Piaproperties piaprops = null;
    File guess = null;

    String filesep  = System.getProperty("file.separator");

    if (cmdroot == null){
      cmdroot = filesep+"pia";
      // Try to guess it, cause it is really required:
      guess           = new File (new File(cmdroot, "Config"),"pia.props");
    }else 
      guess = new File( cmdroot );
    
    cmdprop = guess.getAbsolutePath();

    try {
      if ( cmdprop != null ) {
	System.out.println ("loading properties from: " + cmdprop) ;
	
	piaprops = new Piaproperties(System.getProperties());
	File propfile = new File(cmdprop) ;
	piaprops.load (new FileInputStream(propfile)) ;
	piaprops.put (PIA_PROP_PATH, propfile.getAbsolutePath()) ;
      }
    } catch (FileNotFoundException ex) {
      setDefaultProperties( piaprops );
    } catch (IOException exp){
      throw exp;
    }
    System.setProperties (piaprops) ;

  return piaprops;
}


  public static void main(String[] args){
        Integer cmdport      = null ;
	String  cmdroot      = null ;
	String  cmdusrdir    = null ;
	String  cmdprop      = null ;
	Boolean cmddebugging = null ;
	Boolean cmdverbose   = null ;


    // Parse command line options:
	for (int i = 0 ; i < args.length ; i++) {
	    if ( args[i].equals ("-port") ) {
		try {
		    cmdport = new Integer(args[++i]) ;
		} catch (NumberFormatException ex) {
		    System.out.println ("invalid port number ["+args[i]+"]");
		    System.exit (1) ;
		}
	    } else if ( args[i].equals ("-root") ) {
		cmdroot = args[++i] ;
	    } else if ( args[i].equals ("-u") ) {
		cmdusrdir = args[++i] ;
	    } else if ( args[i].equals ("-p") ) {
		cmdprop = args[++i] ;
	    } else if ( args[i].equals ("-d") ) {
		cmddebugging = Boolean.TRUE;
		++i;
	    } else if ( args[i].equals ("-v") ) {
	        cmdverbose = Boolean.TRUE;
	    } else if ( args[i].equals ("?") || args[i].equals ("-help") ) {
		usage() ;
	    } else {
		System.out.println ("unknown option: ["+args[i]+"]") ;
		System.exit (1) ;
	    }
	}
	
	Piaproperties piaprops = null;
	String where = null;
	String whereFileMap = null;

	// guessing /pia/config is where it is at

	  try{

	    if( cmdprop == null )
	      where = cmdroot;
	    else{
	      File f = new File(cmdprop);
	      whereFileMap = f.getParent()+ System.getProperty("file.separator") + "filemap.props";
	      where = cmdprop;
	    }
	    piaprops = loadProperties( where );

	  }catch(FileNotFoundException ex) {
	    System.out.println ("Unable to load properties: "+cmdprop);
	    System.out.println ("\t"+ex.getMessage()) ;
	    System.out.println ("Running with default settings.");
	    piaprops = new Piaproperties(System.getProperties());
	  }catch(IOException ex) {
	    System.out.println ("Unable to load properties: "+cmdprop);
	    System.out.println ("\t"+ex.getMessage()) ;
	    System.exit (1) ;
	  }

	

	if( cmdroot != null )
	  piaprops.put(PIA_ROOT, cmdroot );

	if( cmdport != null )
	  piaprops.put(PIA_PORT, cmdport.toString());
	if( cmdusrdir != null )
	  piaprops.put(PIA_USR_ROOT, cmdusrdir );
	if( cmddebugging != null )
	  piaprops.put(PIA_DEBUG, "true" );
	if( cmdverbose != null )
	  piaprops.put(PIA_VERBOSE, "true" );


	
	try
	  {
	    Pia piaAgency = new Pia();
	    piaAgency.debug = true;
	    piaAgency.debugToFile = false;
	    piaAgency.initialize(piaprops);
	    piaAgency.piaFileMapping = piaAgency.loadFileMapping( whereFileMap );
	    piaAgency.createPiaAgency();

	    //new Thread( new Shutdown() ).start();
	  }catch(Exception e){
	    System.out.println ("===> Initialization failed, aborting !") ;
	    e.printStackTrace () ;
	  }
	    
  }

}














