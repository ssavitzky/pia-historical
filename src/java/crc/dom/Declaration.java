// Declaration.java
// $Id$

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

package crc.dom;

/** Interface for an SGML Declaration.
 *
 *	NOTE: This interface is not currently part of the W3C's DOM. 
 *	It may be used as an abstract base class for other declarations
 *	such as DocumentType, Entity, and ElementDefinition.  <p>
 *
 *	SGML declarations have the form:<br>
 *	    <code>&lt;!<em>tag name</em> ...&gt;</code><br>
 *	where the tag identifies the type of the declaration, and the
 *	name identifies the object being declared. <p>
 *
 */
public interface Declaration extends Node {

  /** Get the name of the object being declared. */
  String getName();
  void setName(String name);

  /** Get the tagName that identifies the type of the declaration. */
  String getTagName();
  void setTagName(String name);

  void setData(String data);
  String getData();
};
