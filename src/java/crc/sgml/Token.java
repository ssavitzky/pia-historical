// Token.java:  InterForm (SGML) Token
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.sgml;

import crc.ds.List;
import crc.ds.Table;
import crc.ds.Index;


/**
 * The representation of an SGML <em>element</em> or text.  Each Token
 *	has both a set of named <em>attributes</em>, and a set of
 *	indexed <em>items</em> that comprise its <em>content</em>.  In
 *	addition it has a String <em>tag</em>; a Token that contains
 *	only text has a tag of "" (an empty string), while a Token
 *	that contains only items (i.e. is being used for grouping) has
 *	a tag of null and is equivalent to a Tokens list.  A Token with 
 *	a tag of "&amp;" is an entity reference. <p>
 *
 *	Attributes can be retrieved either by name (forced to
 *	lowercase) or by the order received.  Attributes that are
 *	defined but have no associated value have an empty token for
 *	their value; a static constant is provided for the purpose. <p>
 *
 *	A special token, <code>Token.empty</code>, is used as the value 
 *	of attributes and table entries that are present but do not have
 *	an explicit value.  It can be used to distinguish such entries,
 *	which are considered <em>true</em>, from entries whose value is
 *	a null string, which are considered <em>false</em>. <p>
 *
 *	@see Tokens.
 */
public class Token implements SGML {

  /** An empty token, used as the value for an attribute with no value */
  public static final Token empty = new Token();

  /************************************************************************
  ** Components:
  ************************************************************************/

  /** Tag field.  The tag is null for lists and an empty String for
   *	text. */
  protected String tag;

  /************************************************************************
  ** Access to tag:
  ************************************************************************/

  /** Access the tag */
  public String tag() {
    return tag;
  }

  /** Set the tag */
  public void tag(String t) {
    tag = t;
  }

  /************************************************************************
  ** SGML Predicates:
  ************************************************************************/
  
  /** Return true if the object is an individual SGML token. */
  public boolean isToken() {
    return true;
  }

  /** Return true if the object is an individual SGML element.  Only true
   *	for class Token, and false if it is text, a list, or an entity. 
   */
  public boolean isElement() {
    return false;
  }

  /** Return true for a list of tokens. */
  public boolean isList() {
    return tag == null;
  }

  /** Test whether the Token consists only of text. */
  public boolean isText() {
    return "".equals(tag);
  }

  /** Return true if the object implements the Attrs interface */
  public boolean isAttrs() { return false; }

  /** Return true for an empty list or a token with no content. */
  public boolean isEmpty() {
    return true;
  }

  /** Parser state:  0 for a complete element. */
  public byte incomplete() {
    return 0;
  }

  /** Set parser state.  Ignored for all but Token. */
  public void incomplete(byte i) {
  }


  /************************************************************************
  ** Access to attributes:
  ************************************************************************/
  
  /** Retrieve an attribute by name. */
  public SGML attr(String name) {
    return null;
  }

  /** Retrieve an attribute by  index object. */
  public SGML attr(Index name) {
    return null;
  }


  /** Set an attribute by name. */
  public void attr(String name, SGML value) {
    return;
  }

  /** Retrieve an attribute by name, returning its value as a String. */
  public String attrString(String name) {
    return null;
  }

  /** Test whether an attribute exists. */
  public boolean hasAttr(String name) {
    return false;
  }

  /** Convert to a number (double, being the most general form available). */
  public double numValue() {
    return 0.0;
  }

  /************************************************************************
  ** Access to content:
  ************************************************************************/
  
  public Tokens content() {
    return null;
  }

  public String contentString() {
    return toString();
  }

  /************************************************************************
  ** SGML list interface:
  ************************************************************************/

  /** Create a new <code>Tokens</code> list containing both the current
   *	token and the given one. */
  public SGML append(SGML v) {
    Tokens content = new Tokens();
    content.addItem(this);
    content.addItem(v);
    return this;
  }

  public SGML append(String v) {
    return append(new Text(v));
  }

  public SGML appendText(Text v) {
    return append(v);
  }

  /** Append contents to a Tokens list. */
  public void appendContentTo(Tokens list) {
    list.addItem(this);
  }

  /************************************************************************
  ** Conversion to Tokens:
  ************************************************************************/

  /** Convert to a single token if it's a singleton. */
  public SGML simplify() {
    return this;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  protected Token () {
  }

  protected Token (String tag) {
    this();
    this.tag = tag;
  }

  protected Token (Token t) {
    this(t.tag());
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  public Object clone() {
    return new Token(this);
  }

  /************************************************************************
  ** Conversion to String:
  ************************************************************************/

  public Text toText() {
    return new Text(toString());
  }

  public String toString() {
    return "";
  }

  public void appendTextTo(SGML t) {
    t.append(toText());
  }

  /************************************************************************
  ** Access to parts of content:
  ************************************************************************/

  /** Return only the text portions of the content */
  public Text contentText() {
    return (content() == null)? toText() : content().contentText();
  }

  /** Return only the content inside of markup (including text content). */
  public Tokens contentMarkup() {
    return (content() == null)? null : content().contentMarkup();
  }

  /** Return only the text inside the given tag */
  public Text linkText(String tag) {
    return (content() == null)? null : content().linkText(tag);
  }

}
