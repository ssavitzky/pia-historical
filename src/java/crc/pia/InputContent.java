// InputContent.java


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


/** Interface for Content objects representing
 * HTML forms, extends basic Content interface
 * by adding a method allowing Content Length to be fetched
 */

package crc.pia;


public interface InputContent extends Content {


  /** Return (if possible) the length of the
   * current content body as an integer
   *
   * @see crc.pia.GenericAgent
   */
  public int getCurrentContentLength() throws ContentOperationUnavailable;


}
