// Text.java
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

/**
 * The Text object contains the non-markup portion of a document. For XML documents,
 * all whitespace between markup results in Text nodes being created. 
 * 
 * 
 * wstring data 
 *      This holds the actual content of the text node. Text nodes contain just plain
 *      text, without markup and without entities, both of which are manifest as
 *      separate objects in the DOM. 
 * boolean isIgnorableWhitespace 
 *      This is true if the Text node contains only whitespace, and if the whitespace
 *      is ignorable by the application. Only XML processors will make use of this, as
 *      HTML abides by SGML's rules for whitespace handling. 
 */

public interface Text extends Node {

  void setData(String data);
  String getData();

  void setIsIgnorableWhitespace(boolean isIgnorableWhitespace);

  boolean getIsIgnorableWhitespace();

};
