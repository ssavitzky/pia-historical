// Declaration.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
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
