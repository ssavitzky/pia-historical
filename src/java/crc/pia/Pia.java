// Pia.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

 /**
  * Pia contains the Agency's main program, and serves as a repository
  *	for the system's global information.  Most initialization is done
  *	by the auxiliary class Setup.
  *
  *	@see crc.pia.Setup
  */

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

import crc.pia.Configuration;

public class Pia {
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
   * Name of pia's top-level install directory.
   */
  public static final String PIA_ROOT = "crc.pia.piaroot";

  /**
   * Name of server port
   */
  public static final String PIA_PORT = "crc.pia.port";

  /**
   * Name of this host
   */
  public static final String PIA_HOST = "crc.pia.host";

  /**
   * Name of user's pia state directory (normally ~/.pia)
   */
  public static final String USR_ROOT = "crc.pia.usrroot";

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


  /************************************************************************
  ** Private fields:
  ************************************************************************/

  private Properties    piaFileMapping = null;
  private Piaproperties properties     = null;

  private static Pia     instance      = null;
  private String  docurl               = null;
  private static Logger  logger        = null;

  private String  piaRootStr    = null;
  private File    piaRootDir    = null;
  
  private String  usrRootStr	= null;
  private File    usrRootDir 	= null;
  
  private String  url        = null;
  private String  host       = null;
  private int     port       = 8888;
  private int     reqTimeout = 50000;

  private static boolean verbose = false;
  private static boolean debug   = false;  
  // always print to screen or file if debugToFile is on
  private static boolean debugToFile= false;

  private Table proxies          = new Table();
  private List  noProxies        = null;

  private String piaAgentsStr    = null;
  private File   piaAgentsDir    = null;

  private String usrAgentsStr 	= null;
  private File   usrAgentsDir 	= null;

  // file separator
  private String filesep  = System.getProperty("file.separator");
  private String home     = System.getProperty("user.home");
  private String userName = System.getProperty("user.name");

  /************************************************************************
  ** Protected fields:
  ************************************************************************/

  /** The name of the class that will perform our setup. 
   *	@see crc.pia.Setup
   */
  protected String setupClassName 	= "crc.pia.Setup";

  /** The name of the class that will perform logging.
   *	@see crc.pia.Logger
   */
  protected String loggerClassName 	= "crc.pia.Logger";

  /** The name of the class that is our initial Agency.
   *	@see crc.pia.agents.Agency
   *	@see crc.pia.agents.Fallback -- used if the pia's files can't be found.
   */
  protected String agencyClassName 	= "crc.pia.agent.Agency";

  /** The command-line options passed to Java on startup.
   */
  protected String[] commandLine;

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


  /************************************************************************
  ** Access to Components:
  ************************************************************************/

  /**
   * @return file mapping for local file retrieval
   */
  public Properties piaFileMapping(){
    return piaFileMapping;
  }

  /**
   * @return global properties.
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
   * @return a File object representing the root directory
   */
  public File piaRootDir(){
    return piaRootDir;
  }  

  /**
   * @return the root directory path-- i.e /pia
   */
  public String piaRoot(){
    return piaRootStr;
  }  

  /**
   * @return the user directory path -- i.e ~/pia
   */
  public String usrRoot(){
    return usrRootStr;
  }  

  /**
   * @return a File object for the user directory path -- i.e ~/pia
   */
  public File usrRootDir(){
    return usrRootDir;
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
  public String usrAgents(){
    return usrAgentsStr;
  } 


  /**
   * @return the directory where user agents live
   */
  public File usrAgentsDir(){
    return usrAgentsDir;
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


  /************************************************************************
  ** Error Reporting and Messages:
  ************************************************************************/

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


  public void verboseMessage() {
	PrintStream o = System.out ;

	o.println(piaRootStr   + " (parent of src, lib, Agents)");
	o.println(piaAgentsStr + " (agent interforms)");
	o.println(usrRootStr   + " (user directory)");
	o.println(usrAgentsStr + " (user interforms)");
	o.println(Integer.toString( requestTimeout() ) + " (request time out)\n");
	o.println(url+"\n");
  }

  /************************************************************************
  ** Initialization:
  ************************************************************************/

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

    /* Set global variables from properties. */

    verbose 		= properties.getBoolean(PIA_VERBOSE, false);
    debug		= properties.getBoolean(PIA_DEBUG, false);
    piaRootStr		= properties.getProperty(PIA_ROOT, null);
    usrRootStr 		= properties.getProperty(USR_ROOT, null);
    host 		= properties.getProperty(PIA_HOST, thisHost);
    port 		= properties.getInteger(PIA_PORT, port);
    reqTimeout 		= properties.getInteger(PIA_REQTIMEOUT, 60000);
    loggerClassName 	= properties.getProperty(PIA_LOGGER, loggerClassName);
    docurl 		= properties.getProperty(PIA_DOCURL, docurl);

    // Initialize proxies. 
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
    
    if( piaRootStr == null ){
	    File piafile = new File("Pia.java");
	    if( piafile.exists() ){
	      path = piafile.getAbsolutePath();
	      piaRootStr = path.substring(0, path.indexOf("Pia.java")-1);
	    }
	    else{
	      File piadir = new File(home, "pia");

	      // check if we have a copy of the working directory
	      if ( piadir.exists() && piadir.isDirectory() )
		piaRootStr = piadir.getAbsolutePath();
	      else
		throw new PiaInitException(this.getClass().getName()
					   +"[initializeProperties]: "
					   +"[pia root directory] undefined.");
	    }
    }

    if ( piaRootStr.startsWith("~") ){
      piaRootStr = home + piaRootStr.substring(1);
    }
    // Now the directories that depend on it:
    piaRootDir = new File( piaRootStr );
    
    //  we are at /pia/Agents -- this is for interform
    piaAgentsStr = piaRootStr + filesep + "Agents";
    piaAgentsDir = new File( piaAgentsStr );

    if( usrRootStr == null ){ 
      if( home!=null && home != "" ){
	// i.e. we have ~/bob and looking for ~/bob/pia
	File dir = new File(home, ".pia");
	if( dir.exists() ){
	  usrRootStr = dir.getAbsolutePath(); 
	}
	dir = new File(home, "my");
	if( dir.exists() ){
	  usrRootStr = dir.getAbsolutePath(); 
	}
      }
	  
      if( usrRootStr == null ){
	// i.e. we have /pia/users and if bob is valid user's name
	// we have /pia/users/bob
	File usersDir = new File(piaRootStr,"users");
	if( usersDir.exists() ){
	  if( userName!=null && userName != "" )
	    usrRootStr = usersDir.getAbsolutePath() + filesep + userName; 
	}
	else throw new PiaInitException(this.getClass().getName()
					+"[initializeProperties]: "
					+"[user root directory] undefined.");
      }
    }

    if ( usrRootStr.startsWith("~") ){
      usrRootStr = home + usrRootStr.substring(1);
    }
    usrRootDir = new File( usrRootStr );
	
    usrAgentsStr = usrRootStr + filesep + "Agents";
    usrAgentsDir = new File( usrAgentsStr );

    /* Now set the properties that defaulted. */

    properties.setBoolean(PIA_VERBOSE, verbose);
    properties.setBoolean(PIA_DEBUG, debug);
    properties.setProperty(PIA_ROOT, piaRootStr);
    properties.setProperty(USR_ROOT, usrRootStr);
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
    
    if( verbose )
      verboseMessage();

    try{
      accepter = new Accepter( port );
    }catch(IOException e){
      errSys( e, "Can not create Accepter" );
    }

    String docdir = docUrl();
    if( docdir != null )
      System.out.println( "The documentation directory is :" + docdir );

  }

  /** Initialize fields from properties. */
  public boolean initialize() {
    try{
      initializeProperties();
      initializeLogger();

      String fileMap = properties.getProperty("crc.pia.filemap");
      loadFileMapping(fileMap);

      return true;
    }catch(Exception e){
      System.out.println( e.toString() );
      return false;
    }
  }

  /************************************************************************
  ** Shutdown and cleanup:
  ************************************************************************/

  /** Perform cleanup operations, and possibly try to restart the Agency.
   *	@param restart - if true, try to restart operations.
   */

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
  
  /** Shut down the Agency.
   *	@param restart - If true, start the Agency back up again.
   */
  public void shutdown(boolean restart){
    cleanup( restart );
  }

  /** Call cleanup when the Agency is finalized. */
  protected void finalize() throws IOException{
    cleanup( false );
  }


  /************************************************************************
  ** Creating the Pia instance:
  ************************************************************************/

  /** Create the Pia's single instance. */
  private Pia() {
    instance = this;
  }

  /** Return the Pia's only instance.  */
  public static Pia instance() {
    return instance;
  }


  /** Load the MIME type mappings.  */
  protected Properties loadFileMapping( String where ){
    if (where == null) where = properties.getProperty("crc.pia.filemap");

    Properties zFileMapping = new Properties();
    File mapFile            = null;

    try {
      if ( where != null ) {
	verbose ("loading file mapping from: " + where) ;
	
	File mapfile = new File( where ) ;
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
    } catch (IOException exp){
    } finally{
      if (zFileMapping.size() == 0){
	zFileMapping.put("html", "text/html");
	zFileMapping.put("gif", "image/gif");
      }
    }

   instance.piaFileMapping = zFileMapping;
   return zFileMapping;
  }

  static void reportProps(Properties p, String msg) {
    if (verbose) {
      if (msg != null) verbose(msg);
      Enumeration e =  p.propertyNames();
      while( e.hasMoreElements() ){
	try{
	  String k = (String) e.nextElement();
	  String v = p.getProperty( k );
	  verbose("    " + k + "="+ v);
	}catch( NoSuchElementException ex ){
	}
      }
    }
  }

  /************************************************************************
  ** Main Program:
  ************************************************************************/

  public static void main(String[] args){

    /* Create a PIA instance */

    Pia pia = new Pia();
    pia.commandLine = args;
    pia.debug = false;
    pia.debugToFile = false;
    pia.properties = new Piaproperties(System.getProperties());

    /** Configure it. */

    Configuration config = Configuration.loadConfig(pia.setupClassName);
    if (config.configure(args)) {
      config.usage();

      /** Continue with the initialization if the user requested props. */

      verbose = pia.properties.getBoolean("crc.pia.verbose", false);
      if (verbose) {
	pia.reportProps(pia.properties, "Properties");
      }
      System.exit(1);
    }

    /** Initialize it from its properties. */
    if (! pia.initialize()) System.exit(1);

    reportProps(instance.properties, "System Properties:");
    reportProps(instance.piaFileMapping, "File (MIME type) mapping");

    try {
      instance.createPiaAgency();
      //new Thread( new Shutdown() ).start();
    }catch(Exception e){
      System.out.println ("===> Initialization failed, aborting !") ;
      e.printStackTrace () ;
    }
  }

}
