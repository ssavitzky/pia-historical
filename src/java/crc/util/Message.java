////// Message.java: Interface for an error or debugging message
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
 * Interface for a message to the user.
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
 * @see crc.util.Report
 *
 */

public interface Message {

  /************************************************************************
  ** Access functions.
  ************************************************************************/

  /** The string representing the message content. */
  public String getMessage();

  /** Set the message content. */
  public void setMessage( String aString );

  /** Get the name of the file in which the message occurred. */
  public String getFileName();

  /** Set the name of the file in which the message occurred. */
  public void setFileName( String value );

  /** Get the line in the file at which the message occurred. 
   *	Meaningless if <code>getFileName</code> returns <code>null</code>
   */
  public int getLine();

  /** Get the line in the file at which the message occurred. */
  public void setLine(int anInt);

  /** Get the message's severity */
  public int getSeverity();

  /** Set the message's severity */
  public void setSeverity(int anInt);

  /** Get the message's indentation level. */
  public int getIndent();

  /** Set the message's indentation level. */
  public int setIndent( int anInt );

  /** Get the flag that controls whether the final newline is suppressed. */
  public boolean noNewline();

  /** Set the flag that controls whether the final newline is suppressed. */
  public void setNoNewline( boolean value );

}


