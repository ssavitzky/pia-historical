////// Util.java: Utilities
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.sgml;

import crc.sgml.Token;
import crc.sgml.Tokens;
import crc.sgml.Text;

import crc.ds.List;
import crc.ds.Table;

/** The Util class contains no data, only static methods.  For the
 *	most part, these are operations that belong on classes like
 *	String that are part of the standard library and hence can't
 *	be extended.  In Smalltalk we would just extend them and be
 *	done with it. */
public class Util {

  /************************************************************************
  ** SGML utilities:
  ************************************************************************/

  /** Wrap an object in a Text if it isn't already SGML. */
  public static final SGML toSGML(Object o) {
    try {
      return (SGML)o;
    } catch (Exception e) {
      return new Text(o);
    }
  }

  /************************************************************************
  ** Token List utilities:
  ************************************************************************/

  /** Pick an SGML object apart into Tokens.  Text is split on 
   *	whitespace.  */
  public static final Tokens listItems(SGML it) {
    if (it.isText()) {
      return splitTokens(it.toString());
    } else {
      return it.content();
    }
  }

  /** Pick an SGML object apart into attribute-value pairs.  Text is
   *	parsed as a query string.  */
  public static final Tokens listPairs(SGML it) {
    return null;// ===
  }

  /** Copy a list of Tokens (or the content of an object), with spaces
   *	removed.  Each text item has leading and trailing whitespace
   *	removed, and items that consist only of whitespace are deleted
   *	completely.  Whitespace inside of SGML elements is protected
   *	from this process.  SGML comments and declarations count as
   *	whitespace. */
  public static final Tokens removeSpaces(SGML it) {
    Tokens list = new Tokens();
    if (it == null) return list;

    Tokens from = it.content();

    for (int i = 0; i < from.nItems(); ++i) {
      SGML s = from.itemAt(i);
      if (! s.isText()) {
	if (! "!".equals(s.tag()) && ! "!--".equals(s.tag())) 
	  list.append(s);
      } else {
	String ss = s.toString().trim();
	if (ss != null && ! "".equals(ss)) list.append(new Text(ss));
      }
    }
    return list;
  }

  /** Remove leading and trailing spaces from a list of Tokens.  The
   *	first and last items have leading and trailing whitespace
   *	removed, and if they consist only of whitespace are deleted
   *	completely. */
  public static final Tokens trimSpaces(SGML it) {
    Tokens from = it.content();
    Tokens list = new Tokens();
    boolean trimmed = false;

    for (int i = 0; i < from.nItems(); ++i) {
      SGML s = from.itemAt(i);
      if (trimmed) {
	list.append(s);
      } else if (! s.isText()) {
	if (! "!".equals(s.tag()) && ! "!--".equals(s.tag())) {
	  list.append(s);
	  trimmed = true;
	}
      } else {
	String ss = trimLeading(s.toString()); 
	if (ss != null && ! "".equals(ss)) list.append(new Text(ss));
	trimmed = true;
      }
    }
    for (int i = list.nItems(); i >= 0; --i) {
      SGML s = list.itemAt(i);
      if (! s.isText()) {
	if ("!".equals(s.tag()) && ! "!--".equals(s.tag())) {
	  list.pop();
	} else {
	  break;
	}
      } else {
	String ss = trimTrailing(list.pop().toString()); // === just trailing
	if (ss != null && ! "".equals(ss)) list.append(new Text(ss));
	break;
      }
    }
    return list;
  }

  /** Trim the leading blanks from a String. */
  public static final String trimLeading(String s) {
    s += '.';
    s = s.trim();
    return s.substring(0, s.length()-1);
  }

  /** Trim the trailing blanks from a String. */
  public static final String trimTrailing(String s) {
    s = "." + s;
    s = s.trim();
    return s.substring(1);
  }

  /** Convert an SGML object to a number (double).  Double is used because
   *	Java doesn't handle generic numbers.
   */
  public static final double numValue(SGML it) {
    if (it == null) return 0.0;
    return it.contentText().numValue();
  }

  /** Convert an object to a number (double).  Double is used because
   *	Java doesn't handle generic numbers.
   */
  public static final double numValue(Object o) {
    if (o == null) return 0.0;
    try {
      return java.lang.Double.valueOf(o.toString()).doubleValue();
    } catch (Exception e) {
      return 0.0;
    }
  }


  /************************************************************************
  ** String utilities:
  ************************************************************************/

  /** Split a String on whitespace. */
  public static final List split(String s) {
    return new List(new java.util.StringTokenizer(s));    
  }

  /** Split a String on whitespace and return Text Tokens. */
  public static final Tokens splitTokens(String s) {
    return new Tokens(new java.util.StringTokenizer(s));    
  }

  /** Split a String on a given character. */
  public static final List split(String s, char c) {
    List l = new List();
    int i, j;
    j = 0;
    while (j < s.length() && (i = s.indexOf(c, j)) >= 0) {
      l.push(s.substring(j, i));
      s = s.substring(j, i);
      j = i+1;
    }
    if (j < s.length()) 
      l.push(s.substring(j));
    return l;
  }

  /** Split a String on whitespace and make a table with a non-null entry
   *	for each token.  Tokens are lowercased.  This is typically used for 
   *	sets of tags. */
  public static final Table tagTable(String s) {
    return new Table(new java.util.StringTokenizer(s), true);
  }

  /** Split a String into a Tokens list of Text and entity references. */
  public static final Tokens splitEntities(String s) {
    Tokens l = new Tokens();
    int i, j;
    j = 0;
    while (j < s.length() && (i = s.indexOf("&", j)) >= 0) {
      l.push(s.substring(j, i));
      s = s.substring(j, i);
      j = i+1;
    }
    if (j < s.length()) 
      l.push(s.substring(j));
    return l;
    // === splitEntities unimplemented: incomplete!
  }

  /** Capitalize a string.  All characters except the first are
   *	lowercased.  This routine does not preserve mixed-case or
   *	upppercase names, nor does it correctly handle Unicode
   *	uppercase ligatures, which should be titlecased. */
  public static final String capitalize(String s) {
    if (s == null || s.length() < 1) return s;
    String n = s.substring(0, 1).toUpperCase();
    if (s.length() > 1) n += s.substring(1).toLowerCase();
    return n;    
  }

  /** Force the first character of a string to uppercase.  This
   *	routine preserves mixed-case and upppercase names, but does
   *	not correctly handle Unicode lowercase ligatures, which should
   *	be titlecased. */
  public static final String initialCap(String s) {
    if (s == null || s.length() < 1) return s;
    StringBuffer n = new StringBuffer(s);
    n.setCharAt(0, Character.toUpperCase(n.charAt(0)));
    return n.toString();
  }

  /** Decode a string in <code>x-www-form-urlencoded</code> format.
   *	This exists only because java.net.URLDecoder doesn't. <p>
   *	
   *	To convert a String, each character is examined in turn: 
   *	<ul>
   *	  <li> The ASCII characters 'a' through 'z', 'A' through 'Z',
   *		and '0' through '9' remain the same.   
   *	  <li> The space character ' ' is converted into a plus sign '+'. 
   *	  <li> All other characters are converted into the 3-character
   *		string "%xy", where xy is the two-digit 
   *		hexadecimal representation of the lower 8-bits of the
   *		character.  
   *	</ul>
   */
  public static final String urlDecode(String s) {
    if (s.indexOf('+') < 0 && s.indexOf('%') < 0) return s;

    String ss = "";
    for (int i = 0; i < s.length(); ++i) {
      char c = s.charAt(i);
      if (c == '+') {
	ss += ' ';
      } else if (c == '%') {
	int cc = (int)s.charAt(++i);
	cc = cc * 16 + (int)s.charAt(++i);
	ss += (char)cc;
      } else {
	ss += c;
      }
    }
    // === unimplemented
    return s;
  }
}
