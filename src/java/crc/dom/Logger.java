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
 * The Logger class logs information into three files trace, log, errlog
 * which presumably locate in a pia subdirectory named log
 */

public class Logger {
    private   byte              msgbuf[] = null ;
    protected RandomAccessFile  log      = null ;
    protected RandomAccessFile  errlog   = null ;
    protected RandomAccessFile  trace    = null ;
    protected String            logdir   = "logs" ;
    protected int               bufsize  = 52;
    protected int               bufptr   = 0;
    protected byte              buffer[] = null;
    protected String            fileDir = null;

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

    Logger () {
	this.msgbuf = new byte[128] ;
    }

}












