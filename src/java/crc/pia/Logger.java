// CommonLogger.java
// Id: CommonLogger.java,v 1.12 1996/10/01 18:45:00 abaird Exp 
// (c) COPYRIGHT MIT and INRIA, 1996.
// Please first read the full copyright statement in file COPYRIGHT.html
//
// Logger.java -- modified from CommonLogger; does not inherit from any class
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.pia;

import crc.pia.Pia;
import java.io.IOException ;
import java.util.Date ;
import java.io.*;

import crc.pia.PiaRuntimeException;
/**
 * The Logger class logs information into three files trace, log, errlog
 * which presumably locate in a pia subdirectory named log
 */

public class Logger {
    private   byte              msgbuf[] = null ;
    protected RandomAccessFile  log      = null ;
    protected RandomAccessFile  errlog   = null ;
    protected RandomAccessFile  trace    = null ;
    protected Pia               agency   = null ;
    protected String            logdir   = "logs" ;
  //  protected int               bufsize  = 8192;
    protected int               bufsize  = 52;
    protected int               bufptr   = 0;
    protected byte              buffer[] = null;


    /**
     * Output the given message to the given RandomAccessFile.
     * This method makes its best effort to avoid one byte writes (which you
     * get when writing the string as a whole). It first copies the string 
     * bytes into a private byte array, and than, write them all at once.
     * @param f The RandomAccessFile to write to, which should be one of
     *    log, errlog or trace.
     * @param msg The message to be written.
     * @exception IOException If writing to the output failed.
     */

    protected synchronized void output (RandomAccessFile f, String msg)
       throws IOException
    {
	int len = msg.length() ;
	if ( len > msgbuf.length ) 
	    msgbuf = new byte[len] ;
	msg.getBytes (0, len, msgbuf, 0) ;
	f.write (msgbuf, 0, len) ;
    }

    protected synchronized void appendLogBuffer(String msg)
    throws IOException
    {
	int msglen = msg.length();
	if ( bufptr + msglen > buffer.length ) {
	    // Flush the buffer:
	    log.write(buffer, 0, bufptr);
	    bufptr = 0;
	    // Check for messages greater then buffer:
	    if ( msglen > buffer.length ) {
		byte huge[] = new byte[msglen];
		msg.getBytes(0, msglen, huge, 0);
		log.write(huge, 0, msglen);
		return;
	    }
	} else {
	    // Append that message to buffer:
	    msg.getBytes(0, msglen, buffer, bufptr);
	    bufptr += msglen;
	}
    }

    protected void logmsg (String msg) {
	if ( log != null ) {
	    try {
		if ( buffer == null ) {
		    output (log, msg) ;
		} else {
		    appendLogBuffer(msg);
		}
	    } catch (IOException e) {
		throw new PiaRuntimeException (this,"logmsg",e.getMessage()) ;
	    }
	}
    }
    
    protected void errlogmsg (String msg) {
	if ( errlog != null ) {
	    try {
		output (errlog, msg) ;
	    } catch (IOException e) {
		throw new PiaRuntimeException (this
						, "errlogmsg"
						, e.getMessage()) ;
	    }
	}
    }
    
    protected void tracemsg (String msg) {
	if ( trace != null ) {
	    try {
		output (trace, msg) ;
	    } catch (IOException e) {
		throw new PiaRuntimeException (this
						, "tracemsg"
						, e.getMessage()) ;
	    }
	}
    }

    public void log(String msg) {
	logmsg(msg);
    }

    public void errlog (Object client, String msg) {
	errlogmsg (client.toString() + ": " + msg + "\n") ;
    }

    public void errlog (String msg) {
	errlogmsg (msg + "\n") ;
    }

    public void trace (Object client, String msg) {
	tracemsg (client.toString() + ": " + msg + "\n") ;
    }

    public void trace (String msg) {
	tracemsg (msg + "\n") ;
    }

    /**
     * Get the complete path of log, errlog, or trace.
     */

    protected String getFileName (String def) {
	    File root_dir = agency.usrRootDir();
	    if ( root_dir == null ) {
		String msg = "unable to get the agency root directory\n";
		throw new PiaRuntimeException (this
						, "getFileName"
						, msg) ;
	    }
	    File flogdir = new File(root_dir, logdir) ;

	    if ( flogdir != null )
	      if( flogdir.exists() == false ){
		Pia.debug("creating logs");
		flogdir.mkdir();
	      }
	    return (new File(flogdir, def)).getAbsolutePath() ;
    }

    /**
     * Open this logger log file.
     */

    protected void openLogFile () {
	String logname = getFileName("log");
	try {
	    RandomAccessFile old = log ;
	    log = new RandomAccessFile (logname, "rw") ;
	    log.seek (log.length()) ;
	    if ( old != null )
		old.close () ;
	} catch (IOException e) {
	    throw new PiaRuntimeException (this
					    , "openLogFile"
					    , "unable to open "+logname);
	}
    }

    /**
     * Open this logger error log file.
     */

    protected void openErrorLogFile () {
	String errlogname = getFileName("errlog");
	try {
	    RandomAccessFile old = errlog ;
	    errlog = new RandomAccessFile (errlogname, "rw") ;
	    errlog.seek (errlog.length()) ;
	    if ( old != null )
		old.close() ;
	} catch (IOException e) {
	    throw new PiaRuntimeException (this
					    , "openErrorLogFile"
					    , "unable to open "+errlogname);
	}
    }

    /**
     * Open this logger trace file.
     */

    protected void openTraceFile () {
	String tracename = getFileName("traces");
	try {
	    RandomAccessFile old = trace ;
	    trace = new RandomAccessFile (tracename, "rw") ;
	    trace.seek (trace.length()) ;
	    if ( old != null )
		old.close() ;
	} catch (IOException e) {
	    throw new PiaRuntimeException (this
					    , "openTraceFile"
					    , "unable to open "+tracename);
	}
    }

    /**
     * Shutdown this logger.
     */

    public synchronized void shutdown () {
	try {
	    // Flush any pending output:
	    if ( buffer != null ) {
		log.write(buffer, 0, bufptr);
		bufptr = 0;
	    }
	    log.close() ; 
	    log = null ;
	    errlog.close() ;
	    errlog = null ;
	    trace.close() ;
	    trace = null ;
	} catch (IOException e) {
	    e.printStackTrace() ;
	}
    }
		
    /**
     * Initialize this logger for the given server.
     * This method gets the server properties describe above to
     * initialize its various log files.
     * @param server The server to which thiss logger should initialize.
     */

    public void initialize (Pia agency) {
	this.agency = agency ;
	// Open the various logs:
	openLogFile () ;
	openErrorLogFile() ;
	openTraceFile() ;
	// Setup the log buffer is possible:
	if ( bufsize > 0 ) 
	    buffer = new byte[bufsize];
	return ;
    }
	
    /**
     * Construct a new Logger instance.
     */
     
    Logger () {
	this.msgbuf = new byte[128] ;
    }
}












