// Token.java:  InterForm (SGML) Token
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;

import crc.ds.List;
import crc.ds.Table;
import crc.interform.Tokens;
import crc.interform.Text;

/* === Should be the superclass for Text, Element, Entity, and Declaration */

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
 *	@see Tokens.  */
public class Token implements SGML {

  /** An empty token, used as the value for an attribute with no value */
  public static final Token empty = new Token();

  /************************************************************************
  ** Components:
  ************************************************************************/

  /** Tag field.  The tag is null for lists and an empty String for
   *	text. */
  String tag;

  /** Parser state.  0 for a complete element, 1 for a start tag, -1
   *	for an end tag.  2 or -2 for a tag that has just been scanned. */
  byte incomplete = 0;

  /** Syntax flag: 0 if an end tag is optional, 1 if an end tag is
   *	always required, and -1 if an end tag is implicit. */
  byte endTagRequired = 0;

  /** Content. */
  Tokens content = null;

  /** Special format:  start and end tags are first and last content items. */
  boolean specialFormat = false;

  /** Attributes. */
  Table attrs = null;

  /** Attribute names, in the order defined.  
   *	Names are kept in their original form (i.e. uppercase), and multiple
   *	entries are permitted.
   */
  List attrNames = null;

  /** Attribute values, in order defined.  */
  List attrValues = null;


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
  ** Access to flags:
  ************************************************************************/

  /** Parser state:  0 for a complete element, 1 for a start tag, -1
   *	for an end tag.  */
  public byte incomplete() {
    return incomplete;
  }

  /** Set parser state.  Ignored for all but Token. */
  public void incomplete(byte i) {
    incomplete = i;
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
    return tag != null && tag != "" && tag != "&";
  }

  /** Return true for a list of tokens. */
  public boolean isList() {
    return tag == null && incomplete == 0;
  }

  /** Test whether the Token consists only of text. */
  public boolean isText() {
    return tag.equals("");
  }

  /** Return true for an empty list or a token with no content. */
  public boolean isEmpty() {
    return content == null || content.isEmpty();
  }

  /** Return true if the token has an end tag */
  public boolean hasEndTag() {
    return incomplete < 0 || ! isEmpty() || endTagRequired > 0;
  }

  /** Return the name of the entity to which this is a reference. */
  public String entityName() {
    return (tag.equals("&"))? content.itemAt(1).toString() : null;
  }


  /************************************************************************
  ** Access to attributes:
  ************************************************************************/
  
  /** Retrieve an attribute by name. */
  public SGML attr(String name) {
    return (attrs == null)? null : (SGML)attrs.at(name.toLowerCase());
  }

  /** Retrieve an attribute by name, returning its value as a String. */
  public String attrString(String name) {
    if (attrs == null) return null;
    Object attr = attrs.at(name.toLowerCase());
    return (attr == null)? null : attr.toString();
  }

  /** Set an attribute's value, recording its name if it has not yet
   *	been defined.
   */
  public Token attr(String name, SGML value) {
    name = name.toLowerCase();
    if (attrs == null) {
      attrs = new Table();
      attrNames = new List();
      attrValues= new List();
    }
    if (!attrs.has(name)) {	// New: append name and value to lists
      attrNames.push(name);
      attrValues.push(value);
    } else {			// Old: fix up last occurrance in attrValues
      int i = attrNames.lastIndexOf(name);
      attrValues.at(i, value);
    }
    attrs.at(name, value);
    return this;
  }

  /** Set an attribute to an arbitrary object */
  public Token attr(String name, Object value) {
    return attr(name, new Text(value));
  }

  /** Append an attribute name and value to attrNames and attrValues, and
   *	stash its value in attrs for easy lookup.
   */
  public Token addAttr(String name, SGML value) {
    name = name.toLowerCase();
    if (attrs == null) {
      attrs = new Table();
      attrNames = new List();
      attrValues= new List();
    }
    attrNames.push(name);
    attrValues.push(value);
    attrs.at(name, value);
    return this;
  }

  /** Add an attribute to an arbitrary object */
  public Token addAttr(String name, Object value) {
    return addAttr(name, new Text(value));
  }

  /** Return the number of recorded attributes */
  public int nAttrs() {
    return (attrNames == null)? 0 : attrNames.nItems();
  }

  /** Test whether an attribute exists. */
  public boolean hasAttr(String name) {
    return attrs != null && attrs.has(name.toLowerCase());
  }

  /** Retrieve an attribute's name by its index in attrNames */
  public String attrNameAt(int i) {
    return (i >= nAttrs())? null : attrNames.at(i).toString();
  }

  /** Retrieve an attribute's value by its index in attrNames */
  public SGML attrValueAt(int i) {
    return (i >= nAttrs())? null : (SGML)attrValues.at(i);
  }

  /** Change an attribute's value by its index in attrNames.  Both the 
   *	value in attrValues and the one in the attrs Table are changed.
   */
  public Token attrValueAt(int i, SGML value) {
    attrValues.at(i, value);
    attr((String)attrNames.at(i), value);
    return this;
  }

  /************************************************************************
  ** Access to content:
  ************************************************************************/
  
  public Tokens content() {
    return content;
  }

  /************************************************************************
  ** SGML list interface:
  ************************************************************************/

  public int nItems() {
    return (content == null)? 0 : content.nItems();
  }

  public SGML itemAt(int i) {
    return (content == null)? null : content.itemAt(i);
  }

  public SGML itemAt(int i, SGML v) {
    content.itemAt(i, v);
    return this;
  }

  public SGML append(SGML v) {
    if (content == null) { content = new Tokens(); }
    content.append(v);
    return this;
  }

  /** The result of appending a single item.  No merging is done. */
  public SGML addItem(SGML sgml) {
    if (content == null) { content = new Tokens(); }
    content.addItem(sgml);
    return this;
  }

  public SGML appendText(Text v) {
    if (content == null) { content = new Tokens(); }
    content.appendText(v);
    return this;
  }

  /** Append contents to a Tokens list. */
  public void appendContentTo(Tokens list) {
    if (content != null) list.append((SGML)content);
  }

  /************************************************************************
  ** Conversion to Tokens:
  ************************************************************************/

  /** Convert the object to a single token. */
  public Token toToken() {
    return this;
  }

  /** Return a copy of the Token's start tag. */
  public Token startToken() {
    Token t = startTagFor(tag);
    for (int i = 0; i < nAttrs(); ++i) {
      t.addAttr(attrNameAt(i), attrValueAt(i));
    }
    return t;
  }

  /** Return a copy of the Token's start tag. */
  public Token endToken() {
    return endTagFor(tag);
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public Token () {
  }

  public Token (String tag) {
    this.tag = tag;
  }

  public Token (String tag, SGML content) {
    this.tag = tag;
    append(content);
  }

  public Token (String tag, String content) {
    this.tag = tag;
    append(new Text(content));
  }

  public Token (Token it) {
    this.tag = it.tag;
    copyAttrsFrom(it);
    copyContentFrom(it);
  }

  /** Make a Token with a special format, in which the first and last
   *	content items are really the start and end ``tag'' respectively. */
  public Token (String tag, String content, String end) {
    this.tag = tag;
    append(new Text(tag));
    append(new Text(content));
    if (end != null) append(new Text(end));
    specialFormat = true;
  }

  public Token (String tag, StringBuffer content, String end) {
    this.tag = tag;
    append(new Text(tag));
    append(new Text(content));
    if (end != null) append(new Text(end));
    specialFormat = true;
  }

  public Token (String tag, String start, StringBuffer content, String end) {
    this.tag = tag;
    if (start != null) append(new Text(start));
    append(new Text(content));
    if (end != null) append(new Text(end));
    specialFormat = true;
  }

  public Token (String tag, String start, String content, String end) {
    this.tag = tag;
    if (start != null) append(new Text(start));
    append(new Text(content));
    if (end != null) append(new Text(end));
    specialFormat = true;
  }

  /** Construct an end tag. */
  public static Token endTagFor(String tag) {
    Token t = new Token(tag);
    t.incomplete = (byte)-1;
    return t;
  }

  /** Construct a start tag. */
  public static Token startTagFor(String tag) {
    Token t = new Token(tag);
    t.incomplete = (byte)1;
    return t;
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Copy a token's attributes. */
  void copyAttrsFrom(Token it) {
    for (int i = 0; i < it.nAttrs(); ++i) 
      addAttr(it.attrNameAt(i), it.attrValueAt(i));
  }

  /** Copy a token's content. */
  void copyContentFrom(Token it) {
    if (content == null && it.nItems() > 0) { content = new Tokens(); }
    for (int i = 0; i < it.nItems(); ++i) 
      content.addItem(it.itemAt(i));
  }

  public Object clone() {
    return new Token(this);
  }

  /************************************************************************
  ** Conversion to String:
  ************************************************************************/

  /* === instead of checking explicitly for & and so on, we use the
   *	 boolean specialFormat, and use the first and last content items
   *	 as the starting and ending strings, respectively.
   */

  /** Return an SGML start tag for the given token. */
  public Text startTag() {
    if (tag == null || tag.equals("") || specialFormat) {
      return null;
    } else {
      Text t = new Text("<" + tag);
      for (int i = 0; i < nAttrs(); ++i) {
	t.append(" ");
	t.append(attrNameAt(i));
	SGML v = attrValueAt(i);
	if (v != null && !(v.isList() && v.isEmpty())) {
	  t.append("=");
	  v.appendTextTo(t);
	}
      }
      t.append(">");
      return t;
    }
  }

  /** Return an SGML end tag for the given token. */
  public Text endTag() {
    if (tag == null || tag.equals("") || specialFormat) {
      return null;
    } else {
      return new Text("</" + tag + ">");
    }
  }

  public Text toText() {
    Text t = new Text();
    appendTextTo(t);
    return t;
  }

  public String toString() {
    return toText().toString();
  }

  public void appendTextTo(SGML t) {
    if (incomplete >= 0) t.append(startTag());
    if (content != null && incomplete == 0) content.appendTextTo(t);
    if (hasEndTag() && incomplete <= 0) t.append(endTag());
  }

  /************************************************************************
  ** Access to parts of content:
  ************************************************************************/

  /** Return only the text portions of the content */
  public Text contentText() {
    return (content == null)? null : content.contentText();
  }

  /** Return only the content inside of markup (including text content). */
  public Tokens contentMarkup() {
    return (content == null)? null : content.contentMarkup();
  }

  /** Return only the text inside the given tag */
  public Text linkText(String tag) {
    return (content == null)? null : content.linkText(tag);
  }

  /** Return the content with leading and trailing whitespace removed. */
  public Tokens contentTrim() {
    return (content == null)? null : content.contentTrim();
  }

  /** Return the content as a single token (making a tagless Token
   *	if necessary)
   */
  public Token contentToken() {
    return (content == null)? null : content.toToken();
  }

}
