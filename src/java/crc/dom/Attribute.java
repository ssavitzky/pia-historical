// Attribute.java
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

public interface Attribute extends Node {

  /**
   * Set the name of this attribute.
   * @param name attribute name.
   */
  void setName(String name);

  /**
   * Returns the name of this attribute. 
   * @return attribute name.
   */
  String getName();

  /**
   * Set the attribute's value.
   * @param value The effective value of this attribute. (The attribute's effective value is
   * determined as follows: if this attribute has been explicitly assigned any
   * value, that value is the attribute's effective value; otherwise, if there is a
   * declaration for this attribute, and that declaration includes a default value,
   * then that default value is the attribute's effective value; otherwise, the
   * attribute has no effective value.)Note, in particular, that an effective value
   * of the null string would be returned as a Text node instance whose toString()
   * method will return a zero length string (as will toString() invoked directly on
   * this Attribute instance).If the attribute has no effective value, then this
   * method will return null. Note the toString() method on the Attribute instance can
   * also be used to retrieve the string version of the attribute's value(s).  
   */
  void setValue(NodeList value);

  /**
   * Return attribute value
   * @return attribute value.
   */
  NodeList getValue();

  /**
   * Set specified value.If this attribute was explicitly given a value in the original document, this
   * will be true; otherwise, it will be false. 
   */
  void setSpecified(boolean specified);

  /**
   * Return whether value is specified.
   */
  boolean getSpecified();

  /**
   * Returns the value of the attribute as a string. Character and general entity
   * references will have been replaced with their values in the returned string. 
   */
  String toString();

};


