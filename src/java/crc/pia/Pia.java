// Pia.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.pia;

import java.io.File;
import crc.pia.PiaInitException;
import crc.pia.Machine;
import crc.pia.agent.Agency;
import crc.pia.Resolver;
import crc.pia.Logger;


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


  private String  docurl      = null;
  private Logger  logger      = null;
  private Logger  loggerClassName = null;
  
  
  private String  rootStr    = null;
  private String  rootDir    = null;
  
  private String  piaUsrRootStr = null;
  private String  piaUsrRootDir = null;
  
  private String  url        = null;
  private String  host       = null;
  private int     port       = 8001;
  private boolean verbose    = false;
  private boolean debug      = false;
  private boolean debugToFile= false;
  

  private HashTable proxies           = new HashTable();
  private String[]  noProxies        = null;

  private String piaAgentsStr    = null;
  private String piaAgentsDir    = null;

  private String piaUsrAgentsStr = null;
  private String piaUsrAgentsDir = null;
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
    return port.toString();
  } 

  /**
   * @toggle debug flag 
   */
  public void debug(boolean onoff){
    return debug = onoff;
  } 

  /**
   * @toggle debug to file flag
   * true if you want debug message to trace file.
   */
  public void debugToFile(boolean onoff){
    return debugToFile = onoff;
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

  /**
   * @return the directory where user agents live
   */
  public File piaUsrAgentsDir(){
    return piaUsrAgentsDir;
  } 

  /**
   * @return the proxy string
   */
  public HashTable proxies(){
    return proxies;
  } 

  /**
   * @return the no proxy schemes
   */
  public String[] noProxies(){
    return noProxies;
  } 


  public Pia(){
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
	o.println("-p    </pia/Bin/agency.props>       : property file to read.");
	o.println("-d                    : turns debugging on.") ;
	o.println("-v                    : print pia properties.");
	o.println("?                     : print this help.");
	System.exit (1) ;
  }

  public static void verbose () {
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
	    warning ("no logger specified, not logging.");
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
	  proxies.put( key, properties.getProperty( keyEntry ) );
	}
      }catch( NoSuchElementException e ){
      }
    }
    String noproxies = properties.getProperty(this, PIA_NO_PROXIES, null);
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
      }catch(NoSuchElementException e) {
      }
    }
      


  }


  private void initializeProperties() throws PiaInitException{
    String thisHost = null;
    try {
      thisHost = InetAddress.getLocalHost().getHostName();
    }catch (UnknownHostException e) {
      thisHost = null;
    }


    verbose         = properties.getProperty(PIA_VERBOSE, verbose);
    debug           = properties.getProperty(PIA_DEBUG, debug);
    rootStr         = properties.getProperty(PIA_ROOT, null);
    piaUsrRootStr   = properties.getProperty(PIA_USR_ROOT, null);
    host            = properties.getProperty(PIA_HOST, thisHost);
    port            = properties.getProperty(PIA_PORT, port);
    loggerClassName = properties.getProperty(PIA_LOGGER, null);

    // i. e. agency.crc.pia.proxy_http=foobar 
    // get keys from properties
    // enumerate thru keys looking for PIA_PROXIES
    // if one found, get string pass underscore; this is the key
    // get from properties its value
    // push k,v to proxies

    initializeProxies();

    // file separator
    String filesep = System.getProperty("file.separator");
    String home    = System.getProperty("user.home");
    String userName    = System.getProperty("user.name");

    if( host == null ){
      throw new PiaInitException(this.getClass().getName()
						 +"[initializeProperties]: "
						 +"[host] undefined.");
    }
    
    if( rootStr == null ){
	    File piafile = new File(".","Pia.java");
	    if( piafile.exists() )
	      rootStr = ".."+filesep;
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

        // Now the directories that depend on it:
        rootDir = new File( rootStr );
    
	//  we are at /pia/Agents -- this is for interform
	piaAgentsStr = rootStr+"Agents";
	piaAgentsDir = new File( piaAgentsStr );
	

	if( piaUsrRootStr == null ){ 
	  if( home && home != "" )
	    // i.e. we have ~/bob
	    piaUsrRootStr = home;
	  else{
	    // i.e. we have /pia/Users and if bob is valid user's name
	    // we have /pia/Users/bob
	    File usersDir = new File(rootStr,"Users");
	    if( usersDir.exists() ){
	      if( userName && userName != "" )
		pisUsrRootStr = usersDir + userName; 
	    }
	    else throw new PiaInitException(this.getClass().getName()
					    +"[initializeProperties]: "
					    +"[user root directory] undefined.");

	    
	  }
	}
	piaUsrRootDir = new File( piaUsrRootStr );
	

	piaUsrAgentsStr = piaUsrRootStr + "/Agents";
	piaUsrAgentsDir = new File( piaUsrAgentsStr );
	

	url = url();
	
  }

  private void initializePiaAgency(){
    thisMachine  = new Machine( host, port, null );
    resolver     = new Resolver();
    agency       = new Agency("agency", null);
    resolver.registerAgent( agency );
    accepter     = new Accepter( port );

    if( cmdverbose )
      verbose();
  }

  public void initialize(Properties cmdProps) throws PiaInitException{
    this.properties = cmdProps;

    initializeProperties();
    initializeLogger();
    initializePiaAgency();

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
	
	Properties piaprops = new Properties(System.getProperties());

	// sucks in the property file and set properties here

	if ( cmdprop != null ) {
	    System.out.println ("loading properties from: " + cmdprop) ;
	    try {
		File propfile = new File(cmdprop) ;
		piaprops.load (new FileInputStream(propfile)) ;
		piaprops.put (PIA_PROP_PATH, propfile.getAbsolutePath()) ;
	    } catch (FileNotFoundException ex) {
		System.out.println ("Unable to load properties: "+cmdprop);
		System.out.println ("\t"+ex.getMessage()) ;
		System.exit (1) ;
	    } catch (IOException ex) {
		System.out.println ("Unable to load properties: "+cmdprop);
		System.out.println ("\t"+ex.getMessage()) ;
		System.exit (1) ;
	    }
	    System.setProperties (piaprops) ;
	}

	if( cmdroot != null )
	  piaprops.put(PIA_ROOT, rootStr );

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
	    piaAgency.initialize(piaprops);
	  }catch(Exception e)
	    {
	       System.out.println ("===> Initialization failed, aborting !") ;
	       e.printStackTrace () ;
	    }
  }

}













