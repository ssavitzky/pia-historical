////// SGML.java:  Interface for SGML tokens
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;

/**
 * Interface for SGML tokens and collections of tokens.
 *	At some point this may become a subinterface of Stuff.
 */
public interface SGML {

  /** Return true if the object is an individual SGML token. */
  boolean isToken();

  /** Return true if the object is an individual SGML element. */
  boolean isElement();

  /** Return true for a list of tokens. */
  boolean isList();

  /** Return true for an empty list or a token with no content. */
  boolean isEmpty();

  /** Return true if the SGML is pure text, or a
   * 	singleton list containing a Text. */
  boolean isText();

  /** A string ``tag'' that is guaranteed to be null if isList(),
   *	and "" if istext(). */
  String tag();

  /** Convert the entire object to text */
  Text toText();

  /** Convert the object to a single token.  
   *	A List is converted to a Token with a null tag. */
  Token toToken();

  /** The object's content.  This is the same as this if isList(); 
   *	it is null if isEmpty(). */
  Tokens content();

  /** The result of appending some sgml.  Same as this if isList(). 
   *	Lists are merged; text tokens are merged as well. */
  SGML append(SGML sgml);

  /** The result of appending some text.  Same as this if isText(). */
  Text appendText(Text t);

  /** Append this as text. */
  void appendTextTo(Text t);

  /** Append content to a Tokens list. */
  void appendContentTo(Tokens list);

}
