// Report.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

 /**
  * A reporting class for displaying messages.
  */

package crc.dom;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Report {
 
  /************************************************************************
  ** Access to Components:
  ************************************************************************/

  /** @return the debug flag */
  public static boolean debug() {
    return Report.instance().debug;
  }

  /** @return the verbose flag */
  public static boolean verbose() {
    return Report.instance().verbose;
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
   * Note: this method will not print to file if the Report is not running
   */
  public static void debugToFile(boolean onoff){
    debugToFile = onoff;
  } 

  /************************************************************************
  ** Error Reporting and Messages:
  ************************************************************************/

  /**
   * Fatal system error -- print message and throw runtime exception.
   * Do a stacktrace.
   */
  public static void errSys(Exception e, String msg){
    System.err.println("Report: " + msg);
    e.printStackTrace();
    
    throw new RuntimeException(msg);
  }

  /**
   * Fatal system error -- print message and throw runtime exception
   *
   */
  public static void errSys(String msg){
    System.err.println("Report: " + msg);
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
    if (debug) Report.debug(msg);
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
      ;
      //logger.trace ( msg );
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
      ;
      //logger.trace ("[" +  o.getClass().getName() + "]-->" + msg );
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
      ;
      //logger.trace ( msg );
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
      ;
      //logger.trace ("[" +  o.getClass().getName() + "]-->" + msg );
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
      ;
      //logger.log ( msg );
  }

  /**
   * error to log
   *
   */
  public static void errLog( String msg )
  {
    if( logger != null )
      ;
      //logger.errlog ( msg );
  }

  /**
   * error to log on behalf of an object
   *
   */
  public static void errLog(Object o, String msg )
  {
    if( logger != null )
      ;
      //logger.errlog ("[" +  o.getClass().getName() + "]-->" + msg );
  }

  /************************************************************************
  ** Initialization:
  ************************************************************************/

  /**
   * Initialize the server logger and the statistics object.
   */
  // private void initializeLogger() throws ReportInitException {
  //   if ( loggerClassName != null ) {
  //try {
  //  logger = (Logger) Class.forName(loggerClassName).newInstance() ;
  //  logger.initialize (this) ;
  //} catch (Exception ex) {
  //  String err = ("Unable to create logger of class ["
  //		+ loggerClassName +"]" + "\r\ndetails: \r\n"
  //		+ ex.getMessage());
  //  throw new ReportInitException(err);
  //}
  //  } else {
  //warningMsg ("no logger specified, not logging.");
  //  }
  //}

  public boolean initialize() {
    return true;
    //try{
    //  initializeLogger();
    //}catch(Exception e){
    //  System.out.println( e.toString() );
    //  return false;
    //}
  }

  /************************************************************************
  ** Creating the Report instance:
  ************************************************************************/

  /** Create the Report's single instance. */
  private Report() {
    instance = this;
  }

  /** Return the Report's only instance.  */
  public static Report instance() {
    if( instance != null )
      return instance;
    else{
      String[] args = new String[1];
      args[0] ="";
      makeInstance(args);
      return instance;
    }
  }


  private static void makeInstance(String[] args){

    /* Create a Report instance */

    Report report = new Report();
    report.commandLine = args;
    report.debug = true;
    report.debugToFile = false;

    /** Initialize it from its properties. */
    if (! report.initialize()) System.exit(1);

  }

  /**
   * Property name of Report logger class
   */
  public static final String REPORT_LOGGER = "crc.dom.logger";

  /************************************************************************
  ** Protected fields:
  ************************************************************************/

  /** The name of the class that will perform logging.
   *	@see crc.dom.Logger
   */
  protected String loggerClassName 	=  REPORT_LOGGER;
  protected String[] commandLine;

  /************************************************************************
  ** Private fields:
  ************************************************************************/

  private static Logger logger 		= null;

  private static boolean verbose = false;
  private static boolean debug   = true;  

  // always print to screen or file if debugToFile is on
  private static boolean debugToFile= false;

  // file separator
  private String filesep  = System.getProperty("file.separator");
  private String home     = System.getProperty("user.home");
  private String userName = System.getProperty("user.name");

  static private Report instance;
  /************************************************************************
  ** Main Program:
  ************************************************************************/

  public static void main(String[] args){

    Report report = new Report();
    report.commandLine = args;
    report.debug = false;
    report.debugToFile = false;

    /** Initialize it from its properties. */
    if (! report.initialize()) System.exit(1);

  }




}
