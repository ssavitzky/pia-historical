////// Loader.java: Tagset loading and initialization utilities.
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.tagset;

import crc.dps.Tagset;
import crc.ds.Table;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileReader;

public class Loader {

  /** Table of all globally-defined tagsets, by name.
   *
   *	=== we will have multiple tagset namespaces at some point.
   */
  static Table tagsets = new Table();

  /** Initialize the static table with the HTML, BOOT, and tagset tagsets. */
  static {
    tagsets.at("HTML", new HTML_ts());
    tagsets.at("BOOT", new BOOT_ts());
    tagsets.at("tagset", new tagset());
  }

  /** Return a Tagset with a given name.
   *	If it doesn't exist or the name is null, null is returned.
   *	If a tagset can be loaded (i.e. as a subclass) it is locked.
   */
  public static Tagset require(String name) {
    if (name == null) return null;
    Tagset t = (Tagset)tagsets.at(name);
    if (t == null) { 
      t = loadTagset(name);
      if (t != null) {
	tagsets.at(name, t);
      }
    }
    return t;
  }

  /** Return a Tagset with a given name.
   *	If it doesn't exist or the name is null, a new Tagset is created.
   *	If a tagset can be loaded (i.e. as a subclass) it is locked.
   */
  public static Tagset getTagset(String name) {
    if (name == null) return new crc.dps.tagset.BasicTagset();
    Tagset t = (Tagset)tagsets.at(name);
    if (t == null) { 
      t = loadTagset(name);
      if (t != null) {
	tagsets.at(name, t);
	t.setIsLocked(true);
      }
    }
    return t;
  }

  /** test for the presence of a Tagset with a given name.
   */
  public static boolean tagsetExists(String name) {
    if (name == null) return false;
    return tagsets.at(name) != null;
  }

  /** Load a named Tagset.  First tries to load a file with a ".ts"
   *	extension.  If that fails, tries to load a class, which had
   *	better be a subclass of Tagset, and create an instance of it
   *	(which had better have the right name). */
  public static Tagset loadTagset(String name) {
    Tagset ts = loadTagsetFile(name);
    return ts != null? ts : loadTagsetSubclass(name);
  }


  /** Load a Tagset implementation class and create an instance of it.  */
  protected static Tagset loadTagsetSubclass(String name) {
    try {
      String nn = crc.util.NameUtils.lowercase(name);
      Class c = crc.util.NameUtils.loadClass(nn, "crc.dps.tagset.");
      if (c == null) {
	nn = crc.util.NameUtils.javaName(name) + "_ts";
	c = crc.util.NameUtils.loadClass(nn, "crc.dps.tagset.");
      }
      return (c != null)? (Tagset)c.newInstance() : null;
    } catch (Exception e) { 
      e.printStackTrace(System.err);
      return null;
    }
  }

  /** Load a Tagset implementation from an InputStream. */
  protected static Tagset loadTagsetFromStream(InputStream src) {
    return null; // ===
  }

  /** Load a Tagset implementation from a Java ``resource''. 
   *
   *	Tries ".obj", ".tss", and ".ts" in that order (but will not load a
   *	".obj" or ".tss" file older than the ".ts" file, since that is
   *	presumably the master copy.
   */
  protected static Tagset loadTagsetFromResource(String name) {
    return null; // ===
  }

  /** Load a Tagset from a file.  
   *
   *	Tries ".obj", ".tss", and ".ts" in that order (but will not load a
   *	".obj" or ".tss" file older than the ".ts" file, since that is
   *	presumably the master copy.
   */
  protected static Tagset loadTagsetFile(String name) {
    return null;		// ===
  }

}
