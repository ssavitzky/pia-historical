// Registered.java -- interface for objects registered in hashtables
// 	$Id$

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

package crc.ds;

/** This is the interface for objects in the PIA that are ``registered'' 
 *	in a hash table or some other kind of registry object. <p>
 *
 *	The main reason to have such an interface is so that objects
 *	can be registered after being restored from an ObjectStream.<p>
 *
 * <b>Note:</b>
 *	It would be a <em>very bad</em> thing for a Registered object
 *	to contain a pointer back to its registry; if it did, every other
 *	object in the registry would get dragged in and out with this one.
 *	Don't even <em>think</em> about it!
 *
 */
public interface Registered {
  /** Register the object (in whatever registry is appropriate) */
  public void register();

  /** Remove the object from its registry */
  public void unregister();
}

