////// Severity.java: Error or debugging message
//	$Id$

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/


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


