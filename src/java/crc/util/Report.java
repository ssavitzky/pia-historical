////// Report.java: Error and debugging message reporting
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
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * The interface for reporting information to the user.
 *
 *	Message reporting is under the control of the ``verbosity''
 *	level: it takes on the following <a name=verbosity>values</a>:
 *	<dl>
 *	    <dt> -4 <dd> nothing (server mode)
 *	    <dt> -3 <dd> internal errors only
 *	    <dt> -2 <dd> errors only
 *	    <dt> -1 <dd> ``quiet'': errors and warnings only
 *	    <dt>  0 <dd> normal
 *	    <dt>  1 <dd> ``verbose''
 *	    <dt>  2 <dd> debugging
 *	    <dt> &gt;2 <dd> more debugging
 *	</dl> 
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
 * @see crc.util.Severity
 * @see crc.util.Message
 *
 */

public interface Report {

  /************************************************************************
  ** Verbosity:
  ************************************************************************/

  /** Get the current <a href=#verbosity>verbosity</a> level.
   */
  public int getVerbosity(); 

  /** Set the current <a href=#verbosity>verbosity</a> level. 
   */
  public void setVerbosity(int value);

  /************************************************************************
  ** Basic message reporting:
  ************************************************************************/

  /** Report a message to the user, provided its <code>severity</code>
   *	is less than or equal to the current verbosity level. */
  public void report(int severity, String message);

  /** Report a message to the user, with more control over formatting.
   *	This is normally used for making debugging messages more compact.
   *
   * @param severity the message severity level.
   * @param message the text of the message.
   * @param indent the number of spaces to indent.  
   * @param noNewline if <code>true</code>, the message is not terminated
   *	with a newline.
   */
  public void report(int severity, String message,
		     int indent, boolean noNewline );

  /** Report a Message to the user, provided its <code>severity</code>
   *	is less than or equal to the current verbosity level.  All 
   *	necessary severity and formatting information is part of the Message
   *	object.
   */
  public void report(Message message);

  /************************************************************************
  ** Convenience Functions:
  ************************************************************************/

  public void setDebug();
  public void setVerbose();
  public void setNormal();
  public void setQuiet();

  public void fatal(String message);
  public void fatal(Exception e);

  public void internal(String message);
  public void internal(Exception e);

  public void error(String message);
  public void error(String message, String fileName, int line);

  public void warning(String message);
  public void warning(String message, String fileName, int line);

  public void message(String message);
  public void verbose(String message);

  public void debug(String message);
  public void debug(String message, int indent);
  public void debug(String message, String fileName, int line);
}


