// NameUtils.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.util;


/**
 * This class contains static functions that deal with names and name-like
 *	strings.
 */
public class NameUtils {

  /************************************************************************
  ** Generic name utilities:
  ************************************************************************/

  /** Capitalize a string, producing a valid Java identifier.  
   *	Any character which is not a letter or digit is deleted and the next
   *	character is capitalized.  Uppercase characters are preserved.
   */
  public static final String capitalize(String s) {
    StringBuffer n = new StringBuffer();
    boolean capitalize = true;
    for (int i = 0; i < s.length(); ++i) {
      if (!Character.isLetterOrDigit(n.charAt(i))) 
	capitalize = true;
      else {
	n.append(capitalize? Character.toUpperCase(s.charAt(i)) : s.charAt(i));
	capitalize = false;
      }
    }
    return n.toString();
  }

  /** Uppercase a string, producing  a valid Java classname.  Turn period and
   *	hyphen (in fact any non-letter, non-digit) into underscore. */
  public static final String uppercase(String s) {
    StringBuffer n = new StringBuffer(s);
    for (int i = 0; i < s.length(); ++i) {
      if (!Character.isLetterOrDigit(n.charAt(i))) 
	n.setCharAt(i, '_');
      else
	n.setCharAt(i, Character.toUpperCase(n.charAt(i)));
    }
    return n.toString();
  }

  /** Lowercase a string, producing  a valid Java classname.  Turn period and
   *	hyphen (in fact any non-letter, non-digit) into underscore. */
  public static final String lowercase(String s) {
    StringBuffer n = new StringBuffer(s);
    for (int i = 0; i < s.length(); ++i) {
      if (!Character.isLetterOrDigit(n.charAt(i))) 
	n.setCharAt(i, '_');
      else
	n.setCharAt(i, Character.toLowerCase(n.charAt(i)));
    }
    return n.toString();
  }


  /************************************************************************
  ** File name utilities:
  ************************************************************************/

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

  /************************************************************************
  ** Class name utilities:
  ************************************************************************/

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

  /** Load a named class.  The <code>packagePrefix</code> is prepended only if
   *	the handle name contains no "." characters.  Util.javaName can be used
   *	to convert an SGML identifier (e.g. a tag) to a class name.
   *
   * @see Util#javaName
   */
  public static Class loadClass(String h, String prefix) {
    if (h.indexOf(".") < 0) h = prefix + h;
    try {
      Class handleClass = Class.forName(h);
      return handleClass;
    } catch (Exception e) { }
    return null;
  }

}