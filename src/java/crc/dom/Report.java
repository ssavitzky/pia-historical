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
    // System.out.println("going to tracemsg"+logger.toString()+ (new Boolean(debugToFile)).toString());
    if (!debug) return;
    if( logger != null && debugToFile ){
      //System.out.println("going to tracemsg"+logger.toString()+ (new Boolean(debugToFile)).toString());
      logger.trace ( msg );
    }
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

    
  /************************************************************************
  ** Initialization:
  ************************************************************************/

  /**
   * Initialize the server logger and the statistics object.
   */
  private void initializeLogger(){
    try {
      logger = new Logger( traceFilePath );
    } catch (ReportException ex) {
      throw ex;
    }
  }

  private boolean initialize(String fp ) {
    try{
      traceFilePath = fp; 
      initializeLogger();
      return true;
    }catch(Exception e){
      System.out.println( e.toString() );
      return false;
    }
  }

  /************************************************************************
  ** Creating the Report instance:
  ************************************************************************/

  /**
   * set where to write to
   */
  public static void setDebugFilePath(String fp)
  { 
    instance().initialize( fp );
  }

  /**
   * Close trace file
   */
  public static void closeTraceFile(){
    if ( instance().logger != null )
      logger.close();
  }

  /** Return the Report's only instance.  */
  public static Report instance() {
    if( instance != null )
      return instance;
    else{
      makeInstance();
      return instance;
    }
  }


  /** Create the Report's single instance. */
  private Report() {
    instance = this;
  }

  private static void makeInstance(){

    /* Create a Report instance */

    Report report = new Report();
    //report.debug = true;
    //report.setDebugFilePath(".");
    //report.debugToFile = false;

    /** Initialize it from its properties. */
    //if (! report.initialize()) System.exit(1);

  }

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
  static private Report instance;
  
  // trace file
  private String traceFilePath="logfile";
}

