////// Tokens.java:  List of InterForm Tokens
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;
import java.util.Vector;

/**
 * A List (sequence) of SGML Token's.  
 *	Unlike a simple List, Strings and Lists are merged when appended.
 */
public class Tokens implements SGML {

  /************************************************************************
  ** Components:
  ************************************************************************/

  Vector content = new Vector();

  /************************************************************************
  ** Object operations:
  ************************************************************************/

  public String toString() {
    StringBuffer s = new StringBuffer();
    for (int i = 0; i < nItems(); ++i) {
      s.append(content.elementAt(i).toString());
    }
    return s.toString();
  }

  /************************************************************************
  ** SGML list interface:
  ************************************************************************/

  public int nItems() {
    return content.size();
  }
  public SGML itemAt(int i) {
    return i >= nItems() ? null : (SGML)content.elementAt(i);
  }
  public SGML itemAt(int i, SGML v) {
    content.setElementAt(v, i);
    return this;
  }

  /************************************************************************
  ** SGML interface:
  ************************************************************************/

  /** Return true if the object is an individual SGML token. */
  public boolean isToken() {
    return false;
  }

  /** Return true if the object is an individual SGML element. */
  public boolean isElement() {
    return false;
  }

  /** Parser state:  0 for a complete element. */
  public byte incomplete() {
    return 0;
  }

  /** Set parser state.  Ignored for all but Token. */
  public void incomplete(byte i) {
  }

  /** Return true for a list of tokens. */
  public boolean isList() {
    return true;
  }

  /** Return true for an empty list or a token with no content. */
  public boolean isEmpty() {
    return content.isEmpty();
  }

  /** Return true if the SGML is pure text, or a 
   * 	singleton list containing a Text. */
  public boolean isText() {
    return nItems() == 1 && itemAt(0).isText();
  }

  /** A string ``tag'' that is guaranteed to be null if isList(),
   *	and "" if isText(). */
  public String tag() {
    return null;
  }

  public String entityName() {
    return null;
  }

  /** Convert the entire object to text. */
  public Text toText() {
    if (isText()) {
      return itemAt(0).toText();
    }
    return new Text(toString());
  }

  /** Convert the object to a single token. */
  public Token toToken() {
    return (nItems() == 1) ? itemAt(0).toToken() : new Token(null, this);
  }

  /** The object's content.  This is the same as this if isList(); 
   *	it is null if isEmpty(). */
  public Tokens content() {
    return this;
  }

  /** The result of appending some SGML tokens.  Same as this if isList(). */
  public SGML append(SGML sgml) {
    if (sgml.isList()) {
      sgml.appendContentTo(this);
    } else if (sgml.isText() && nItems() > 0 && itemAt(nItems()-1).isText()) {
      itemAt(nItems()-1).appendText(sgml.toText());
    } else {
      content.addElement(sgml);
    }
    return this;
  }

  /** The result of appending some text.  */
  public SGML appendText(Text t) {
    return append(t);
  }

  /** Append this as text. */
  public void appendTextTo(SGML t) {
    for (int i = 0; i < nItems(); ++i) {
      itemAt(i).appendTextTo(t);
    }
  }

  /** Append contents to a Tokens list. */
  public void appendContentTo(Tokens list) {
    for (int i = 0; i < nItems(); ++i) {
      list.append(itemAt(i));
    }
  }
    
  /************************************************************************
  ** Access to parts of content:
  ************************************************************************/

  /** Return only the text portions of the content */
  public Text contentText() {
    Text t = new Text();
    for (int i = 0; i < nItems(); ++i) {
      t.append(itemAt(i).contentText());
    }
    return t;
  }

  /** Return only the content inside of markup (including text content). */
  public Tokens contentMarkup() {
    Tokens t = new Tokens();
    for (int i = 0; i < nItems(); ++i) {
      if (! itemAt(i).isText()) t.append(itemAt(i));
    }
    return t;
  }

  /** Return only the text inside the given tag */
  public Text linkText(String tag) {
    Text t = new Text();
    for (int i = 0; i < nItems(); ++i) {
      if (itemAt(i).tag() == tag) t.append(itemAt(i).contentText());
    }
    return t;
  }

  /** Return the content with leading and trailing whitespace removed. */
  public Tokens contentTrim() {
    Tokens t = new Tokens();
    for (int i = 0; i < nItems(); ++i) {
      if (i == 0 && itemAt(i).isText()) {
	// === We really ought to treat first and last differently.
	String s = itemAt(i).toString().trim();
	if (s != "") t.append(new Text(s));
      } else if (i == nItems() && itemAt(i).isText()) {
	// === We really ought to treat first and last differently.
	String s = itemAt(i).toString().trim();
	if (s != "") t.append(new Text(s));
      } else {
	t.append(itemAt(i));
      }
    }
    return t;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public Tokens() {
  }

  public Tokens(SGML s) {
    this();
    this.append(s);
  }


}
