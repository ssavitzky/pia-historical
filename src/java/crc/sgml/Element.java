// Element.java:  SGML Element (tag pair with content)
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.sgml;

import crc.ds.List;
import crc.ds.Table;

/**
 * The representation of an SGML <em>element</em>.  Each Element
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
 *	@see Tokens  
 *	@see Token
 */
public class Element extends Token implements Attrs {

  /** An empty token, used as the value for an attribute with no value */
  public static final Token empty = new Token();

  /************************************************************************
  ** Components:
  ************************************************************************/

  /** Parser state.  0 for a complete element, 1 for a start tag, -1
   *	for an end tag.  2 or -2 for a tag that has just been scanned. */
  protected byte incomplete = 0;

  /** Syntax flag: 0 if an end tag is optional, 1 if an end tag is
   *	always required, and -1 if an end tag is implicit. */
  protected byte endTagRequired = 0;

  /** Content. */
  protected Tokens content = null;

  /** Special format:  start and end tags are first and last content items. */
  protected boolean specialFormat = false;

  /** Attributes. */
  protected Table attrs = null;

  /** Attribute names, in the order defined.  
   *	Names are kept in their original form (i.e. uppercase), and multiple
   *	entries are permitted.
   */
  protected List attrNames = null;

  /** Attribute values, in order defined.  */
  protected List attrValues = null;


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

  /** Syntax flag: 0 if an end tag is optional, 1 if an end tag is
   *	always required, and -1 if an end tag is implicit. */
  public byte endTagRequired() {
    return endTagRequired;
  }

  /** Set endTagRequired flag. */
  public void endTagRequired(byte i) {
    endTagRequired = i;
  }

  /************************************************************************
  ** SGML Predicates:
  ************************************************************************/
  
  /** Return true if the object is an individual SGML element.  Only true
   *	for class Token, and false if it is text, a list, or an entity. 
   */
  public boolean isElement() {
    return tag != null && tag != "" && !specialFormat;
  }

  /** Return true for a list of tokens. */
  public boolean isList() {
    return tag == null && incomplete == 0;
  }

  /** Test whether the Token consists only of text. */
  public boolean isText() {
    return !specialFormat && tag.equals("");
  }

  /** Return true if the object implements the Attrs interface */
  public boolean isAttrs() { return true; }

  /** Return true for an empty list or a token with no content. */
  public boolean isEmpty() {
    return content == null || content.isEmpty();
  }

  /** Return true if the token has an end tag */
  public boolean hasEndTag() {
    return incomplete < 0 || ! isEmpty() || endTagRequired > 0;
  }


  /************************************************************************
  ** Access to attributes:
  ************************************************************************/

  /** Test whether attributes exist. */
  public boolean hasAttrs() {
    return true;
  }

  /** Enumerate the defined attributes. */
  public java.util.Enumeration attrs() {
    return (attrNames == null)? new AttrTable().attrs() : attrNames.elements();
  }


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
  public void attr(String name, SGML value) {
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
  }

  /** Set an attribute with a String value. */
  public void attr(String name, String value) {
    attr(name, new Text(value));
  }

  /** Set an attribute to an arbitrary object */
  public void attr(String name, Object value) {
    attr(name, new Text(value));
  }

  /** Append an attribute name and value to attrNames and attrValues, and
   *	stash its value in attrs for easy lookup.
   */
  public Attrs addAttr(String name, SGML value) {
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

  /** Add an attribute with a String value.  Returns the object itself. */
  public Attrs addAttr(String name, String value) {
    attr(name, value);
    return this;
  }

  /** Add an attribute with an arbitrary object as value. */
  public Attrs addAttr(String name, Object value) {
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

  /** Convert to a number (double, being the most general form available). */
  public double numValue() {
    return contentText().numValue();
  }

  /************************************************************************
  ** Access to content:
  ************************************************************************/
  
  public Tokens content() {
    return content;
  }

  public String contentString() {
    return (content == null)? "" : content.toString();
  }

  public void content(Tokens t) {
    content = t;
  }

  public void content(SGML t) {
    content = Tokens.valueOf(t);
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

  public SGML append(String v) {
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

  /** Convert to a single token if it's a singleton. */
  public SGML simplify() {
    if (isList()) {
      if (nItems() == 0) return empty;
      else return content().simplify();
    } else {
      return this;
    }
  }

  /** Return a copy of the Token's start tag. */
  public Token startToken() {
    Element t = startTagFor(tag);
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

  public Element () {
  }

  public Element (String tag) {
    this();
    this.tag = tag;
  }

  public Element (String tag, SGML content) {
    this(tag);
    if (content != null) append(content);
  }

  public Element (String tag, String content) {
    this(tag);
    if (content != null) append(new Text(content));
  }

  public Element (String tag, List content) {
    this(tag);
    java.util.Enumeration values = content.elements();
    while (values.hasMoreElements()) {
      append(new Element("li", Util.toSGML(values.nextElement())));
    }
  }

  public Element(String tag, Table tbl) {
    this();
    this.tag = tag;
    java.util.Enumeration keys = tbl.keys();
    java.util.Enumeration values = tbl.elements();

    while (keys.hasMoreElements()) {
      append(new Element("dt", keys.nextElement().toString()));
      append(new Element("dd", Util.toSGML(values.nextElement())));
    }
  }

  public Element(String tag, Attrs tbl) {
    this();
    this.tag = tag;
    java.util.Enumeration keys = tbl.attrs();

    while (keys.hasMoreElements()) {
      String key = keys.nextElement().toString();
      append(new Element("dt", key));
      append(new Element("dd", tbl.attr(key)));
    }
  }

  public Element (Element it) {
    this(it.tag);
    copyAttrsFrom(it);
    copyContentFrom(it);
  }

  /** Make a Element with a special format, in which the first and last
   *	content items are really the start and end ``tag'' respectively. */
  public Element (String tag, String s, String end) {
    this(tag);
    content = new Tokens();
    content.push(new Text(tag));
    content.push(new Text(s));
    if (end != null) content.push(new Text(end));
    specialFormat = true;
  }

  public Element (String tag, StringBuffer s, String end) {
    this(tag);
    content = new Tokens();
    content.push(new Text(tag));
    content.push(new Text(s));
    if (end != null) content.push(new Text(end));
    specialFormat = true;
  }

  public Element (String tag, String start, StringBuffer s, String end) {
    this(tag);
    content = new Tokens();
    if (start != null) content.addItem(new Text(start));
    content.addItem(new Text(s));
    if (end != null) content.addItem(new Text(end));
    specialFormat = true;
  }

  public Element (String tag, String start, String s, String end) {
    this(tag);
    content = new Tokens();
    if (start != null) content.addItem(new Text(start));
    content.addItem(new Text(s));
    if (end != null) content.addItem(new Text(end));
    specialFormat = true;
  }

  /** Convert arbitrary SGML to an Element */
  public static final Element valueOf(SGML it) {
    return (it instanceof Element)? (Element)it : new Element(null, it);
  }

  /** Construct an end tag. */
  public static Element endTagFor(String tag) {
    Element t = new Element(tag);
    t.incomplete = (byte)-1;
    return t;
  }

  /** Construct a start tag. */
  public static Element startTagFor(String tag) {
    Element t = new Element(tag);
    t.incomplete = (byte)1;
    return t;
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Copy a Element's attributes. */
  public void copyAttrsFrom(Element it) {
    for (int i = 0; i < it.nAttrs(); ++i) 
      addAttr(it.attrNameAt(i), it.attrValueAt(i));
  }

  /** Copy a Element's content. */
  public void copyContentFrom(Element it) {
    if (content == null && it.nItems() > 0) { content = new Tokens(); }
    for (int i = 0; i < it.nItems(); ++i) 
      content.addItem(it.itemAt(i));
  }

  public Object clone() {
    return new Element(this);
  }

  /************************************************************************
  ** Conversion to String:
  ************************************************************************/

  /* === instead of checking explicitly for & and so on, we use the
   *	 boolean specialFormat, and use the first and last content items
   *	 as the starting and ending strings, respectively.
   */

  /** Return an SGML start tag for the given Element. */
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
	  t.append("=\"");
	  // === Should be more discriminating about quoting ===
	  // === on the other hand some browsers are confused by ' ===
	  // === should also entity-encode <&>
	  v.appendTextTo(t);
	  t.append("\"");
	}
      }
      t.append(">");
      return t;
    }
  }

  /** Return an SGML end tag for the given Element. */
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
    if (incomplete >= 0 && tag != null && !tag.equals("") && !specialFormat)
      t.append(startTag());
    if (content != null && incomplete == 0) content.appendTextTo(t);
    if (incomplete <= 0 && hasEndTag() && 
	tag != null && !tag.equals("") && !specialFormat)       
      t.append(endTag());
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

}