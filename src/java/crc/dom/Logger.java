// CommonLogger.java
// Id: CommonLogger.java,v 1.12 1996/10/01 18:45:00 abaird Exp 
// (c) COPYRIGHT MIT and INRIA, 1996.
// Please first read the full copyright statement in file COPYRIGHT.html
//
// Logger.java -- modified from CommonLogger; does not inherit from any class
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.dom;

import java.io.IOException ;
import java.util.Date ;
import java.io.*;

/**
 * The Logger class logs information into trace file.
 */

public class Logger {
    private   byte              msgbuf[] = null ;
    protected RandomAccessFile  trace    = null ;
    protected int               bufsize  = 52;
    protected int               bufptr   = 0;
    protected byte              buffer[] = null;
    protected String            traceFilePath = null;

  /**
   * Output the given message to the given RandomAccessFile.
   * This method makes its best effort to avoid one byte writes (which you
   * get when writing the string as a whole). It first copies the string 
   * bytes into a private byte array, and than, write them all at once.
   * @param f The RandomAccessFile to write to, which should be one of
   * trace.
   * @param msg The message to be written.
   * @exception IOException If writing to the output failed.
   */

  protected synchronized void output (RandomAccessFile f, String msg)
       throws IOException
  {
    byte[] msgbuf = msg.getBytes();
    int len = msgbuf.length ;
    f.write (msgbuf, 0, len) ;
  }


  /**
   * Ask output to write message to file.
   */
  protected void tracemsg (String msg){
    if ( trace != null ) {
      try {
	output (trace, msg) ;
      } catch (IOException e) {
	throw new ReportException (this
				   , "tracemsg"
				   , e.getMessage()) ;
      }
    }
  }
  
  /**
   * Trace message to file with an object's name preceeding message.
   */
  public void trace (Object client, String msg) {
    tracemsg (client.toString() + ": " + msg + "\n") ;
  }
  
  /**
   * Trace message 
   */
  public void trace (String msg) {
    tracemsg (msg + "\n") ;
  }

  /**
   * close trace file
   */
  public void close(){
    try {
      if ( trace != null )
	trace.close() ;
    } catch (IOException e) {
      throw new ReportException (this
				 , "closeTraceFile"
				 , "unable to close "+traceFilePath);
    }
  }
  
  /**
   * Open this logger trace file.  Any old trace file with same name is deleted.
   */
  
  protected void openTraceFile (){
    try {
      File oldTraceFile = new File( traceFilePath );
      if( oldTraceFile.exists() && oldTraceFile.canWrite() )
	oldTraceFile.delete();

      trace = new RandomAccessFile (traceFilePath, "rw") ;
    } catch (IOException e) {
      throw new ReportException (this
				 , "openTraceFile"
				 , "unable to open "+traceFilePath);
    }
  }
  
  /**
   * Opens trace file.
   */

  public void initialize (String path) {
    traceFilePath = path;
    // Open the trace file:
    openTraceFile() ;
    // Setup the log buffer is possible:
    if ( bufsize > 0 ) 
      buffer = new byte[bufsize];
    return ;
  }
  
   
  
  Logger (String traceFilePath ) {
    this.msgbuf = new byte[128] ;
    initialize( traceFilePath );
  }
  
}












