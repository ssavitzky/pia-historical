////// Loader.java: Tagset loading and initialization utilities.
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.tagset;

import crc.dps.Tagset;
import crc.ds.Table;

public class Loader {

  /** Table of all globally-defined tagsets, by name. */
  static Table tagsets = new Table();


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

  /** Load a Tagset from a file. */
  protected static Tagset loadTagsetFile(String name) {
    return null;		// ===
  }

  /** Load a named Tagset.  First tries to load a file with a ".ts"
   *	extension.  If that fails, tries to load a class, which had
   *	better be a subclass of Tagset, and create an instance of it
   *	(which had better have the right name). */
  protected static Tagset loadTagset(String name) {
    Tagset ts = loadTagsetFile(name + ".ts");
    return ts != null? ts : loadTagsetSubclass(name);
  }


}
