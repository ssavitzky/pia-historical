// Text.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
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
