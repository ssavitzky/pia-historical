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

import crc.ds.List;
import crc.ds.Table;

/** The Util class contains no data, only static methods.  For the
 *	most part, these are operations that belong on classes like
 *	String that are part of the standard library and hence can't
 *	be extended.  In Smalltalk we would just extend them and be
 *	done with it. */
public class Util extends crc.sgml.Util {


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
