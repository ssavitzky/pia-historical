////// Util.java: Utilities
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;
import crc.interform.Actor;
import crc.interform.Token;
import crc.interform.Tokens;
import crc.interform.Input;
import crc.interform.State;
import crc.interform.Interp;
import crc.interform.Handler;

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
    Tokens from = it.content();
    Tokens list = new Tokens();

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
  public static String trimLeading(String s) {
    s += '.';
    s = s.trim();
    return s.substring(0, s.length()-1);
  }

  /** Trim the trailing blanks from a String. */
  public static String trimTrailing(String s) {
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

  /** Turn a string into a valid Java classname.  Turn period and
   *	hyphen (in fact any non-letter, non-digit) into underscore,
   *	and capitalize the first character.  If the name is already
   *	mixed case or uppercase, this will be preserved. */
  public static final String javaName(String s) {
    StringBuffer n = new StringBuffer(s);
    for (int i = 0; i < s.length(); ++i) {
      if (i == 0) n.setCharAt(i, Character.toUpperCase(n.charAt(i)));
      if (!Character.isLetterOrDigit(n.charAt(i))) 
	n.setCharAt(i, '_');
    }
    return n.toString();
  }

  /** Protect markup in a string by converting &lt;, &gt;, and &amp; to
   *	the corresponding entities. */
  public static final String protectMarkup(String s) {
    if (s == null || s.length() < 1) return s;
    String n = "";
    for (int i = 0; i < s.length(); ++i) {
      if (s.charAt(i) == '&') n += "&amp;";
      else if (s.charAt(i) == '>') n += "&gt;";
      else if (s.charAt(i) == '<') n += "&lt;";
      else n += s.charAt(i);
    }
    return n;    
  }

  /** Add markup to a String, using commonly-accepted text
   *	conventions.  Things that look like tags are boldfaced; things
   *	that look like attributes are italicized, and so on.  */
  public static final SGML addMarkup(String s) {
    if (s == null || s.length() < 1) return null;
    String n = "";
    for (int i = 0; i < s.length(); ++i) {
      if (s.charAt(i) == '&') n += "&amp;";
      else if (s.charAt(i) == '>') n += "&gt;";
      else if (s.charAt(i) == '<') n += "&lt;";
      else n += s.charAt(i);
    }
    return new Text(n);    
  }

  /************************************************************************
  ** Attribute utilities:
  ************************************************************************/

  public static String getString(SGML it, String attr, String dflt) {
    String v = it.attrString(attr);
    return (v == null)? dflt : v;
  }

  public static int getInt(SGML it, String attr, int dflt) {
    SGML v = it.attr(attr);
    return (v == null)? dflt : (int) v.numValue();
  }


  /************************************************************************
  ** File and Class Loading utilities:
  ************************************************************************/

  /** Load a named handler class and return an instance of it.  The
   *	prefix is prepended only if the handle name contains no "."
   *	characters.  Util.javaName can be used to convert an SGML
   *	identifier (e.g. a tag) to a class name.
   *	@return an instance of the newly-loaded class.
   *	@see Util.javaName */
  public static Handler loadHandler(String h, String prefix) {
    if (h.indexOf(".") < 0) h = prefix + h;
    try {
      Class handleClass = Class.forName(h);
      return (Handler) handleClass.newInstance();
    } catch (Exception e) { }
    return null;
  }

  /** Load a named class.  The prefix is prepended only if the handle
   *	name contains no "." characters.  Util.javaName can be used to
   *	convert an SGML identifier (e.g. a tag) to a class name.
   *	@see Util.javaName */
  public static Class loadClass(String h, String prefix) {
    if (h.indexOf(".") < 0) h = prefix + h;
    try {
      Class handleClass = Class.forName(h);
      return handleClass;
    } catch (Exception e) { }
    return null;
  }

  /* 
sub file_lookup {
    my ($self, $it, $ii, $write) = @_;

    ## Look up a file.

    my $file = $it->attr('file');
    my $base = $it->attr('base');

    if ($it->attr('interform')) {
	$file = IF::Run::agent()->find_interform($file);
	$base = '';
				# file should be properly quantified
	return $file;
    }
    if ($file =~ /^~/) {
	$file =~ s/^~//;
	$base = $ENV{'HOME'};
    } elsif ($file =~ /^\//) {
	$base = '';
    } elsif ($base eq '') {
	$base = IF::Run::agent()->agent_directory;
    }
    if ($base ne '' && $base !~ /\/$/) {
	$base .= '/';
    }
    my $fn = "$base$file";
    $fn =~ s://:/:g;

    return $fn;
}
*/

}
