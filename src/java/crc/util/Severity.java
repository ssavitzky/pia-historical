////// Severity.java: Error or debugging message
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.util;

/**
 * Message severity levels for error reporting.
 *
 *	Each message has a corresponding <em><a name=severity>
 *	severity</a>level</em>:
 *	<dl>
 *	    <dt> -3 <dd> FATAL
 *	    <dt> -3 <dd> INTERNAL
 *	    <dt> -2 <dd> ERROR
 *	    <dt> -1 <dd> WARNING
 *	    <dt>  0 <dd> MESSAGE
 *	    <dt>  1 <dd> VERBOSE
 *	    <dt>  2 <dd> DEBUG
 *	    <dt> &gt;2 <dd> more extensive debugging
 *	</dl>
 *
 *	``Internal'' errors are those due to a programming error or
 *	other unusual condition (e.g. an unexpected I/O error).
 *	``Ordinary'' errors, also called ``user'' errors, are those
 *	caused by invalid input. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.util.Report
 * @see crc.util.Message
 *
 */

public class Severity {

  /************************************************************************
  ** Message Severities:
  ************************************************************************/

  public static final int FATAL   = -4;
  public static final int INTERNAL= -3;
  public static final int ERROR   = -2;
  public static final int WARNING = -1;
  public static final int MESSAGE =  0;
  public static final int VERBOSE =  1;
  public static final int DEBUG   =  2;


  protected String names[] = {
    "FATAL", "INTERNAL", "ERROR", "WARNING", "MESSAGE", "VERBOSE", "DEBUG"
  };

  protected int offset=4;

  public String getSeverityName(int severity) {
    if ((severity + offset) < 0) 
      return names[0] + "-" + (0-(severity+offset));
    else if ((severity+offset) > names.length()) 
      return names[names.length()-1] + "+" + (severity-offset);
    else 
      return names[severity+offset];
  }

}


