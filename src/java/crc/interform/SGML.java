////// SGML.java:  Interface for SGML tokens
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;

/**
 * Interface for SGML tokens and collections of tokens.
 *	At some point this may become a subinterface of Stuff.
 */
public interface SGML extends java.lang.Cloneable {

  /** Return true if the object is an individual SGML token. */
  boolean isToken();

  /** Return true if the object is an individual SGML element.  This is
   *	<em>only</em> true if the object is a member of class Token.
   */
  boolean isElement();

  /** Return true for a list of tokens. */
  boolean isList();

  /** Return true for an empty list or a token with no content. */
  boolean isEmpty();

  /** Return true if the SGML is pure text, or a
   * 	singleton list containing a Text. */
  boolean isText();

  /** Parser state:  0 for a complete element, 1 for a start tag, -1
   *	for an end tag.  */
  byte incomplete();

  /** Set parser state.  Ignored for all but Token. */
  void incomplete(byte i);

  /** A string ``tag'' that is guaranteed to be null if isList(),
   *	"" if istext(), and "&amp;" if entityName is non-null. */
  String tag();

  /** Return the name of the entity to which this is a reference. */
  String entityName();

  /** Convert the entire object to text */
  Text toText();

  /** Convert the object to a single Token.  
   *	A List is converted to a Token with a null tag. */
  Token toToken();

  /** Convert to a single token if it's a singleton. */
  public SGML simplify();

  /** The object's content.  This is the same as this if isList(); 
   *	it is null if isEmpty(). */
  Tokens content();

  /** The text part of the object's content. */
  Text contentText();

  /** The result of appending some sgml.  Appends as text for Text, 
   *	content for Token;  merges lists and text. */
  SGML append(SGML sgml);

  /** The result of appending some text.  Slightly more efficient. */
  SGML appendText(Text t);

  /** Append this as text.  Note that the destination need <em>not</em>
   * 	be pure text; it could be a list or a token.  */
  void appendTextTo(SGML sgml);

  /** Append content to a Tokens list. */
  void appendContentTo(Tokens list);

  /** Retrieve an attribute by name. */
  SGML attr(String name);

  /** Retrieve an attribute by name, returning its value as a String. */
  String attrString(String name);

  /** Test whether an attribute exists. */
  boolean hasAttr(String name);

}
