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
    for (int i = 0; i < size(); ++i) {
      s.append(content.elementAt(i).toString());
    }
    return s.toString();
  }

  /************************************************************************
  ** SGML list interface:
  ************************************************************************/

  public int size() {
    return content.size();
  }
  public SGML itemAt(int i) {
    return i >= size() ? null : (SGML)content.elementAt(i);
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
    return size() == 1 && itemAt(0).isText();
  }

  /** A string ``tag'' that is guaranteed to be null if isList(),
   *	and "" if isText(). */
  public String tag() {
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
    return (size() == 1) ? itemAt(0).toToken() : new Token(null, this);
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
    } else if (sgml.isText() && size() > 0 && itemAt(size()-1).isText()) {
      itemAt(size()-1).appendText(sgml.toText());
    } else {
      content.addElement(sgml);
    }
    return this;
  }

  /** The result of appending some text.  Same as this if isText(). */
  public Text appendText(Text t) {
    return toText().appendText(t);
  }

  /** Append this as text. */
  public void appendTextTo(Text t) {
    for (int i = 0; i < size(); ++i) {
      itemAt(i).appendTextTo(t);
    }
  }

  /** Append contents to a Tokens list. */
  public void appendContentTo(Tokens list) {
    for (int i = 0; i < size(); ++i) {
      list.append(itemAt(i));
    }
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
