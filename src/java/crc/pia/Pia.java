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
   * A substring that ends all proxy property names.
   */
  public static final String PROXY = "_proxy";

  /**
   * The name of the property that contains a comma-separated list of
   *	sites not to proxy.
   */
  public static final String NO_PROXY = "no_proxy";


  /**
   * Property name of path of pia properties file.
   */
  public static final String PIA_PROP_PATH = "crc.pia.profile";

  /**
   * Property name of URL of pia doc
   */
  public static final String PIA_DOCURL = "crc.pia.docurl";

  /**
   * Property name of pia's top-level install directory.
   */
  public static final String PIA_ROOT = "crc.pia.piaroot";

  /**
   * Property name of server port
   */
  public static final String PIA_PORT = "crc.pia.port";

  /**
   * Property name of this host
   */
  public static final String PIA_HOST = "crc.pia.host";

  /**
   * Property name of user's pia state directory (normally ~/.pia)
   */
  public static final String USR_ROOT = "crc.pia.usrroot";

  /**
   * Property name of debugging flag
   */
  public static final String PIA_DEBUG = "crc.pia.debug";

  /**
   * Property name of verbose flag
   */
  public static final String PIA_VERBOSE = "crc.pia.verbose";

  /**
   * Property name of pia logger class
   */
  public static final String PIA_LOGGER = "crc.pia.logger";

  /** 
   * Property name of pia Agency class
   */
  public static final String PIA_AGENCY = "crc.pia.agency";

  /**
   * Property name of pia logger class
   */
  public static final String PIA_REQTIMEOUT = "crc.pia.reqtimeout";


  /************************************************************************
  ** Private fields:
  ************************************************************************/

  private Properties    piaFileMapping 	= null;
  private Piaproperties properties 	= null;

  private static Pia    instance	= null;
  private String  	docurl		= null;
  private static Logger logger 		= null;

  private String  piaRootStr 		= null;
  private File    piaRootDir    	= null;
  
  private String  usrRootStr		= null;
  private File    usrRootDir 		= null;
  
  private String  url        = null;
  private String  host       = null;
  private int     port       = 8888;
  private int     reqTimeout = 50000;

  private static boolean verbose = false;
  private static boolean debug   = false;  
  // always print to screen or file if debugToFile is on
  private static boolean debugToFile= false;

  private Table proxies          = new Table();
  private List  noProxies        = new List();

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
   * @return the table that maps protocols onto proxy URL's.
   */
  public Table proxies(){
    return proxies;
  } 

  /**
   * List of sites not to proxy.  This is a List and not a Table because
   *	Agency will iterate down it looking for a <em>prefix</em> of the
   *	URL we are trying to reach.
   * @return the list of sites not to proxy.
   */
  public List noProxies() {
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


  /************************************************************************
  ** Initialization:
  ************************************************************************/

  /**
   * Initialize the server logger and the statistics object.
   */
    private void initializeLogger() throws PiaInitException {
      if ( loggerClassName != null ) {
	try {
	  logger = (Logger) Class.forName(loggerClassName).newInstance() ;
	  logger.initialize (this) ;
	} catch (Exception ex) {
	  String err = ("Unable to create logger of class ["
			+ loggerClassName +"]" + "\r\ndetails: \r\n"
			+ ex.getMessage());
	  throw new PiaInitException(err);
	}
      } else {
	warningMsg ("no logger specified, not logging.");
      }
    }

  /** Iterate through the properties looking for keys that end in 
   *	<code>_proxy</code>.  The key <code>no_proxy</code> is assumed to 
   *	contain a comma-separated list of URL's; others are assumed to
   *	contain a single URL.
   */
  protected void initializeProxies(){
    String noproxies = null;
    Enumeration e =  properties.propertyNames();
    while( e.hasMoreElements() ){
      String propName = (String) e.nextElement();
      if (propName.endsWith(NO_PROXY)) {
	noproxies = properties.getProperty(propName);
      } else if (propName.endsWith(PROXY)) {
	/* Remove _proxy and leading dot-separated path from key */
	String scheme = propName.substring(0, propName.indexOf(PROXY));
	if (scheme.indexOf(".") >= 0) 
	  scheme = scheme.substring(scheme.indexOf(".")+1);
	String v = properties.getProperty(propName);
	if (! v.endsWith("/")) v += "/";
	proxies.put(scheme, v);
      }
    }
    
    if (noproxies != null) {
      StringTokenizer parser = new StringTokenizer(noproxies, ",");
      while (parser.hasMoreTokens()) {
	String v = parser.nextToken().trim();
	noProxies.push( v );
      }
    }
  }

  /** 
   * Initialize the Pia from the properties that we already have.
   *	Put the values of any properties that defaulted back into
   *	the property table.
   */
  protected void initializeProperties() throws PiaInitException {
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
    agencyClassName 	= properties.getProperty(PIA_AGENCY, agencyClassName);
    docurl 		= properties.getProperty(PIA_DOCURL, docurl);

    /* Set proxy tables from properties that end in "_proxy" */

    initializeProxies(); 

    /* If we still don't know our host name, complain. */

    if( host == null ){
      throw new PiaInitException(this.getClass().getName()
				 +"[initializeProperties]: "
				 +"[host] undefined.");
    }

    /* Try to find the PIA's root directory.  If it doesn't exist, 
     *	change the agency class to "Fallback" so that the user can be
     *	queried. */
    piaRootDir = new File( piaRootStr );
    if (piaRootDir.exists() && piaRootDir.isDirectory()) {
      piaRootStr = piaRootDir.getAbsolutePath();
    } else {
      piaRootStr = null;
      agencyClassName = "crc.pia.agency.Fallback";
    }
    
    //  we are at /pia/Agents -- this is for interform
    piaAgentsStr = piaRootStr + filesep + "Agents";
    piaAgentsDir = new File( piaAgentsStr );

    /* Try to find the user's PIA state directory.   */

    usrRootDir = new File( usrRootStr );
    usrRootStr = usrRootDir.getAbsolutePath();
	
    if (piaRootDir.exists() && piaRootDir.isDirectory()) {
      piaRootStr = piaRootDir.getAbsolutePath();
    } else {
      piaRootStr = null;
      agencyClassName = "crc.pia.agency.Fallback";
    }
    
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
    properties.setProperty(PIA_AGENCY, agencyClassName);

    url = url();
  }

  private void createPiaAgency() throws IOException{
    thisMachine  = new Machine( host, port, null );
    threadPool   = new ThreadPool();

    resolver     = new Resolver();
    Transaction.resolver = resolver;
    
    try {
      agency = (Agency) Class.forName(agencyClassName).newInstance() ;
      //agency = new Agency("Agency", null);
      agency.name("Agency");
      agency.type("Agency");
    } catch (Exception e) {
      errSys(e, "Cannot create Agency object with class name "
	     + agencyClassName + "\n" + e.toString());
    }
      

    resolver.registerAgent( agency );

    try{
      accepter = new Accepter( port );
    }catch(IOException e){
      System.out.println("Can not create Accepter: " + e.getMessage());
      System.out.println(  "  Try using a different port number:\n" 
			 + "    pia -port nnnn\n"
			 + "  8000+your user ID is a good choice.");
      System.exit(1);
    }

    System.err.println("Created agency with url = <" + url + ">");

  }

  /** Initialize the Pia from the properties.  Can be called again if the
   *	properties change.
   */
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
    if( instance != null )
      return instance;
    else{
      String[] args = new String[1];
      args[0] ="PIA_DIR=../../../..";
      makeInstance(args);
      return instance;
    }
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

  private static void makeInstance(String[] args){

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
