////// SGML.java:  Interface for SGML tokens
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.sgml;

/**
 * Interface for SGML tokens and collections of tokens.<p>
 *
 * === All implementations of <code>crc.sgml.SGML</code> should
 *      support <code>equals</code>, <code>equalsIgnoreCase</code>,
 *      and <code>compareTo</code> methods as well.
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

  /** Return true if the object implements the Attrs interface */
  boolean isAttrs();

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
   *	"" if istext(), and "&amp;" if instanceof Entity. */
  String tag();

  /** Convert the entire object to text */
  Text toText();

  /** Convert to a single token if it's a singleton list. */
  public SGML simplify();

  /** The object's content.  This is the same as <code>this</code> 
   *	if isList(); it is null if isEmpty(). */
  Tokens content();

  /** The text part of the object's content. */
  Text contentText();

  /** The object's content as a String.  Always returns a valid String,
   *	which may be null. */
  String contentString();

  /** The result of appending some sgml.  Appends as text for Text, 
   *	content for Token;  merges lists and text. */
  SGML append(SGML sgml);

  /** The result of appending some text.  Slightly more efficient. */
  SGML appendText(Text t);

  /** The result of appending a string. */
  SGML append(String s);

  /** Append this as text.  Note that the destination need <em>not</em>
   * 	be pure text; it could be a list or a token.  */
  void appendTextTo(SGML sgml);

  /** Append content to a Tokens list. */
  void appendContentTo(Tokens list);

  /** Retrieve an attribute by name.  Semantics vary by SGML type*/
  SGML attr(String name);

  /** Set an attribute by name. Semantics may vary by SGML type*/
  void attr(String name, SGML value);

  /** Retrieve an attribute by name, returning its value as a String. */
  String attrString(String name);

  /** Test whether an attribute exists. */
  boolean hasAttr(String name);

  /** Convert to a number (double, being the most general form available). */
  double numValue();

}
