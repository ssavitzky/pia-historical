// Comment.java
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
  * Represents the content of a comment, i.e. all the characters between the starting
  * '<!--' and ending '-->'. Note that this is the definition of a comment in XML, and,
  * in practice, HTML, although some HTML tools may implement the full SGML comment
  * structure. 
  *
  * wstring data 
  *   The content of the comment, exclusive of the comment begin and end sequence. 
  *
  */
public interface Comment extends Node {

  void setData(String data);
  String getData();

};
