////// Util.java: Utilities
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;
import crc.interform.Actor;
import crc.interform.Input;
import crc.interform.State;
import crc.interform.Interp;
import crc.interform.Handler;

import crc.sgml.SGML;
import crc.sgml.Text;
import crc.sgml.Attrs;
import crc.sgml.AttrWrap;
import crc.sgml.Token;
import crc.sgml.Element;

import crc.ds.List;
import crc.ds.Table;

import java.util.Date;
import java.util.Enumeration;

/** The Util class contains no data, only static methods.  For the
 *	most part, these are operations that belong on classes like
 *	String that are part of the standard library and hence can't
 *	be extended.  In Smalltalk we would just extend them and be
 *	done with it. */
public class Util extends crc.sgml.Util {

  /************************************************************************
  ** Date hacks (JDK 1.1 doesn't have them):
  ************************************************************************/

  /** Compute Julian day.  Saturday is 0 modulo 7.  <p>
   *
   * Note: This code has been in the Savitzky family for generations:
   *	the original implementation was done in FORTRAN by Abraham
   *	Savitzky in 1963 or thereabouts.
   */
  public static long julianDay(long y, long m, long d) {
    if (m > 2) {			  /* march = 0 kludge */
        m -= 3;
    } else {
        m += 9; y -= 1;
    }
    long c = y/100; long ya = y % 100;
    return((146097L * c) / 4 + (1461L * ya) / 4 +
             (153L * m + 2) / 5 + d + 1721119L);
  }

  /** Compute Julian day from a Date object. */
  public static long julianDay(Date aDate) {
    return julianDay(aDate.getYear()+1900, aDate.getMonth(), aDate.getDay());
  }

  /** Compute day of the week (Sunday = 0) from a Julian day. */
  public static int getWeekday(Date aDate) {
    // === should be +1; indicates an arithmetic problem in julianDay ===
    return (int)(julianDay(aDate) -2) % 7;
  }

  /** Compute day of the month from a Julian day. */
  public static int day(long j) {
    long ji, jy, jm, jd;

    ji = j - 1721119L;
    jy = 4 * ji - 1;        jd = (jy % 146097L) / 4;    jy /= 146097L;
    ji = 4 * jd + 3;        jd = (ji % 1461L + 4) / 4;  ji /= 1461L;
    jm = 5 * jd - 3;        jd = (jm % 153L + 5) / 5;   jm /= 153L;
    jy = 100 * jy + ji;

    if (jm < 10) jm += 3;
    else             {jm -= 9; jy += 1;}
    return((int)jd);
  }


  /** Compute  month from a Julian day. */
  public static int month(long j) {
    long ji, jy, jm, jd;

    ji = j - 1721119L;
    jy = 4 * ji - 1;        jd = (jy % 146097L) / 4;    jy /= 146097L;
    ji = 4 * jd + 3;        jd = (ji % 1461L + 4) / 4;  ji /= 1461L;
    jm = 5 * jd - 3;        jd = (jm % 153L + 5) / 5;   jm /= 153L;

    if (jm < 10) jm += 3;
    else             {jm -= 9;}
    return((int)jm);
  }


  /** Compute month from a Julian day. */
  public static int year(long j) {
    long ji, jy, jm, jd;

    ji = j - 1721119L;
    jy = 4 * ji - 1;        jd = (jy % 146097L) / 4;    jy /= 146097L;
    ji = 4 * jd + 3;        jd = (ji % 1461L + 4) / 4;  ji /= 1461L;
    jm = 5 * jd - 3;        jd = (jm % 153L + 5) / 5;   jm /= 153L;
    jy = 100 * jy + ji;

    if (jm < 10) jm += 3;
    else             {jm -= 9; jy += 1;}
    return((int)jy);
  }


  /************************************************************************
  ** InterForm-Specific String Utilities.
  ************************************************************************/

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
  ** Actor Argument utilities:
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
  ** Actor Result utilities:
  ************************************************************************/

  /** Return a list as the result from expanding an element
   *	<code>it</code>.  The result will be a blank-separated string
   *	unless <code>it</code> has <code>sep</sep> or <code>tag</code>
   *	attributes.
   */
  public static SGML listResult(SGML it, Enumeration aList) {

    if (it.hasAttr("tag")) {
      return new Element(it.attrString("tag"), aList);
    } else {
      String sep = getString(it, "sep", " ");
      String s = "";
      while (aList.hasMoreElements()) {
	String n = aList.nextElement().toString();
	s += n;
	if (aList.hasMoreElements()) s += sep;
      }

      return new Text(s);
    }
  }

  /** Return attributes as the result from expanding an element
   *	<code>it</code>.  The result will be a blank-separated string
   *	of <code><em>attr</em>=<em>value</em></code> pairs
   *	unless <code>it</code> has <code>sep</sep> or <code>tag</code>
   *	attributes.
   */
  public static SGML attrsResult(SGML it, Attrs anAttrs) {
    java.util.Enumeration e = anAttrs.attrs();

    if (it.hasAttr("tag")) {
      return new Element(it.attrString("tag"), anAttrs);
    } else {
      String sep = getString(it, "sep", " ");
      String s = "";
      while (e.hasMoreElements()) {
	String n = e.nextElement().toString();
	SGML v = anAttrs.attr(n);

	s += n;
	if (v != Token.empty && !v.isList() && !v.isEmpty()) {
	  s += "=\"" + v.toString() + "\"";
	}
	if (e.hasMoreElements()) s += sep;
      }

      return new Text(s);
    }
  }

  /** Return a query string as the result from expanding an element
   *	<code>it</code>.  The result will be a blank-separated string
   *	of <code><em>attr</em>=<em>value</em></code> pairs
   *	unless <code>it</code> has <code>sep</sep> or <code>tag</code>
   *	attributes.
   */
  public static SGML queryResult(SGML it, Attrs anAttrs) {
    java.util.Enumeration e = anAttrs.attrs();

    if (it.hasAttr("tag")) {
      return new Element(it.attrString("tag"), anAttrs);
    } else if (it.hasAttr("sep")) {
      return attrsResult(it, anAttrs);
    } else {
      return new AttrWrap(anAttrs).toText();
    }
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

  private static String home     = System.getProperty("user.home");
  private static String fileSep  = System.getProperty("file.separator");

  /** Make a path from a directory and a filename.   */
  public static String makePath(String directory, String filename) {
    if (! directory.endsWith(fileSep) && ! directory.equals(""))
      directory += fileSep;
    if (filename.startsWith(fileSep) && ! directory.equals(""))
      filename = filename.substring(fileSep.length());
    return directory + filename;
  }

  /** Extract a filename from a token. */
  public static String getFileName(SGML it, Interp ii, boolean write) {
    String file = getString(it, "file", it.attrString("name"));
    if (file == null) return null;

    if (it.hasAttr("interform")) {
      return (ii.environment != null) ?
	ii.environment.lookupFile(file, it, write) : null;
    }
    String base = getString(it, "base", "");
    if (file.startsWith("~")) {
      file = file.substring(1);
      base = home;
    } else if (file.startsWith(fileSep)) {
      base = "";
    } else if (base.equals("")) {
      if (ii.environment != null) base = ii.environment.baseDir(it);
    }
    if (! base.equals("") && ! base.endsWith(fileSep)) {
      base += fileSep;
    }
    return base + file;
  }

}
