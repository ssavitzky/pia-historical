// Comment.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
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
