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
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import crc.util.regexp.RegExp;

import crc.pia.PiaInitException;
import crc.pia.Machine;
import crc.pia.agent.Agency;
import crc.pia.Resolver;
import crc.pia.Logger;
import crc.pia.Transaction;


 /**
  * Pia stores all of the information relevant to the creation of an 
  * angency.
  */

public class Pia{
  public boolean DEBUG = false; 
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

  private Piaproperties properties   = null;
  private static Pia     instance    = null;
  private String  docurl      = null;
  private Logger  logger      = null;
  private String  loggerClassName = "crc.pia.Logger";
  
  
  private String  rootStr    = null;
  private File    rootDir    = null;
  
  private String  piaUsrRootStr = null;
  private File    piaUsrRootDir = null;
  
  private String  url        = null;
  private String  host       = null;
  private int     port       = 8001;
  private boolean verbose    = true;
  private boolean debug      = false;
  private boolean debugToFile= false;
  

  private Hashtable proxies           = new Hashtable();
  private String[]  noProxies        = null;

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
   * @return the user directory path -- i.e ~/PIA
   */
  public String piaUsrRoot(){
    return piaUsrRootStr;
  }  

  /**
   * @return a File object for the user directory path -- i.e ~/PIA
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
   * @toggle debug flag 
   */
  public void debug(boolean onoff){
    debug = onoff;
  } 

  /**
   * @toggle debug to file flag
   * true if you want debug message to trace file.
   */
  public void debugToFile(boolean onoff){
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
  public Hashtable proxies(){
    return proxies;
  } 

  /**
   * @return the no proxy schemes
   */
  public String[] noProxies(){
    return noProxies;
  } 


  public Pia(){
    instance = this;
  }

  /**
   * Fatal system error -- print message and throw runtime exception.
   * Do a stacktrace.
   */
  public void errSys(Exception e, String msg){
    System.err.println(this.getClass().getName() +": " + msg);
    e.printStackTrace();
    
    throw new RuntimeException(msg);
  }

  /**
   * Fatal system error -- print message and throw runtime exception
   *
   */
  public void errSys(String msg){
    System.err.println(this.getClass().getName() +": " + msg);
    throw new RuntimeException(msg);
  }

  /**
   * Print warning message
   *
   */
  public void warningMsg( String msg ){
    System.err.println( msg );
  }

  /**
   * Print warning message
   *
   */
  public void warningMsg(Exception e, String msg ){
    System.err.println( msg );
    e.printStackTrace();
    
  }

  /**
   * Dump a debugging statement to trace file
   *
   */
  public void debug( String msg )
  {
    if( debug && logger != null && debugToFile )
	logger.trace ( msg );
    else if( debug )
      System.err.println( msg );
    
  }

    /**
     * Dump a debugging statement to trace file on behalf of
     * an object
     */
  public void debug(Object o, String msg )
  {
    if( debug && logger != null && debugToFile )
	logger.trace ("[" +  o.getClass().getName() + "]-->" + msg );
    else if( debug )
      System.err.println("[" +  o.getClass().getName() + "]-->" + msg );
  }

    
  /**
   * message to log
   *
   */
  public void log( String msg )
  {
    if( logger != null )
	logger.log ( msg );
  }

  /**
   * error to log
   *
   */
  public void errLog( String msg )
  {
    if( logger != null )
	logger.errlog ( msg );
  }

  /**
   * error to log on behalf of an object
   *
   */
  public void errLog(Object o, String msg )
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

  public void verbose () {
	PrintStream o = System.out ;

	o.println(rootStr         + " (parent of src, lib, Agents)\n");
	o.println(piaAgentsStr + " (agent interforms)\n");
	o.println(piaUsrRootStr   + " (user directory)\n");
	o.println(piaUsrAgentsStr + " (user interforms)\n");
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
	int i = 0;
	int count = parser.countTokens();
	noProxies = new String[count];
	while(parser.hasMoreTokens()) {
	  String v = parser.nextToken().trim();
	  noProxies[i++]=v;
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
      try{
        RegExp re = new RegExp("~");
	rootStr = re.substitute(rootStr,home,true);
      }catch(Exception e){
      }
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
	  try{
	    RegExp re = new RegExp("~");
	    piaUsrRootStr = re.substitute(piaUsrRootStr, home, true);
	  }catch(Exception ex){
	  }
	}

	piaUsrRootDir = new File( piaUsrRootStr );
	
	piaUsrAgentsStr = piaUsrRootStr + filesep + "Agents";
	piaUsrAgentsDir = new File( piaUsrAgentsStr );

	url = url();
	
  }

  private void createPiaAgency(){
    try{
      accepter     = new Accepter( port );
    }catch(IOException e){
      errSys( e, "Can not create Accepter" );
    }
    thisMachine  = new Machine( host, port, null );
    threadPool   = new ThreadPool();

    resolver     = new Resolver();
    Transaction.resolver = resolver;
    
    agency       = new Agency("agency", null);
    resolver.registerAgent( agency );

     if( verbose )
      verbose();
  }

  public void initialize(Piaproperties cmdProps) throws PiaInitException{
    this.properties = cmdProps;

    initializeProperties();
    initializeLogger();


  }

  protected void cleanup(boolean restart){
    resolver.shutdown();
    accepter.shutdown();
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
	/*
	if( DEBUG ){
	  System.out.println("Please note that the DEBUG flag is on. and Pia is loaded from the following directory-->/home/rithy/pia.");

	  piaprops = instance.loadProperties("/home/rithy/pia/config/pia.props");
	}
	else
	*/
	  piaprops = instance.loadProperties(null);
	instance.initialize(piaprops);
	instance.createPiaAgency();
      }catch(Exception e){
	  System.out.println( e.toString() );
	//get properties from defaults
	//instance.initialize(piaprops);
	//createPiaAgency();
      }
    }


    return instance;
  }

  private static Piaproperties loadProperties(String cmdroot)throws FileNotFoundException, IOException{
    String cmdprop      = null;
    Piaproperties piaprops = null;
    File guess = null;

    String filesep  = System.getProperty("file.separator");

    if (cmdroot == null){
      cmdroot = filesep+"pia";
      // Try to guess it, cause it is really required:
      guess = new File (new File(cmdroot, "config"),"pia.props");
    }else guess = new File( cmdroot );
    
    
    cmdprop = guess.getAbsolutePath();
    if ( cmdprop != null ) {
      System.out.println ("loading properties from: " + cmdprop) ;
      try {
	piaprops = new Piaproperties(System.getProperties());
	File propfile = new File(cmdprop) ;
	piaprops.load (new FileInputStream(propfile)) ;
	piaprops.put (PIA_PROP_PATH, propfile.getAbsolutePath()) ;
      } catch (FileNotFoundException ex) {
	throw ex;
      } catch (IOException exp){
	throw exp;
      }
      System.setProperties (piaprops) ;
    }
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

	// guessing /pia/config is where it is at

	  try{
	    String where;
	    if( cmdprop == null )
	      where = cmdroot;
	    else
	      where = cmdprop;
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
	    piaAgency.createPiaAgency();
	  }catch(Exception e){
	    System.out.println ("===> Initialization failed, aborting !") ;
	    e.printStackTrace () ;
	  }
	    
  }

}













