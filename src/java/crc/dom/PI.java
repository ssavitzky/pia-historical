// PI.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

 /**
  * A PI node is a "processing instruction". The content of the PI node is the entire
  * content between the delimiters of the processing instruction 
  * 
  * wstring name 
  *     XML defines a name as the first token following the markup that begins the
  *      processing instruction, and this attribute returns that name. For HTML, the
  *      returned value is null. 
  * wstring data 
  *      The content of the processing instruction, from the character immediately after
  *      the <? (after the name in XML) to the character immediately preceding the ?>. 
  */

public interface PI extends Node {

  void setName(String name);
  String getName();

  void setData(String data);
  String getData();

};
