////// Loader.java: Tagset loading and initialization utilities.
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.tagset;

import crc.dps.Parser;
import crc.dps.Input;
import crc.dps.Processor;
import crc.dps.Tagset;
import crc.dps.TopContext;

import crc.dps.output.DiscardOutput;

import crc.ds.Table;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.net.URL;
import java.net.URLConnection;

/** Loader for Tagsets.
 *
 *	Tagsets can be loaded either by instantiating an implementation class
 *	(by far the fastest method if the class exists), or from a file.
 *	Files can be either Java objects (<code>.tso</code>), XML tagsets
 *	(<code>.ts</code>), or XML tagsets stripped of extraneous
 *	documentation (<code>.tss</code>).  To make matters worse, files can
 *	be loaded either from the file system (as one might expect), from the
 *	Web, or from Java ``resources'' associated with this module.  Loader
 *	knows the correct search path.
 *
 */
public class Loader {

  /** Table of all globally-defined tagsets, by name.
   *
   *	=== we will have multiple tagset namespaces at some point.
   */
  static Table tagsets = new Table();

  /** It is convenient to have an instance of a class in the tagset package,
   *	to use for loading resources.
   */
  static BOOT_ts boot_ts = new BOOT_ts();

  protected static Class boot_class   = boot_ts.getClass();
  protected static ClassLoader loader = boot_ts.getClass().getClassLoader();

  static int verbosity = 0;
  static PrintStream log = System.err;

  /** Set the verbosity to be used when loading tagsets. */
  public static void setVerbosity(int v) { verbosity = v;  }
  public PrintStream getLog() 		 { return log; }
  public void 	setLog(PrintStream stream) { log = stream; }

  /** Initialize the static table with the HTML, BOOT, and tagset tagsets. */
  static {
    tagsets.at("BOOT", boot_ts);
    tagsets.at("HTML", new HTML_ts());
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

  /** Load a named Tagset.  Files loaded relative to a TopContext. */
  public static Tagset loadTagset(String name, TopContext cxt) {
    Tagset ts = null;

    if (name.indexOf("/") >= 0
	|| name.endsWith(".ts")
	|| name.endsWith(".tss")
	|| name.endsWith(".tso")) {
      // Definitely a file. 
      return loadTagsetFile(name, cxt);
    } else if (name.indexOf(".") >= 0) {
      // Definitely a resource or class
      return loadTagsetFromResource(name);
    }
    ts = loadTagsetSubclass(name);
    if (ts != null) return ts;
    ts = loadTagsetFile(name, cxt);
    if (ts != null) return ts;
    ts = loadTagsetFromResource(name);
    return ts;
  }

  /** Load a named Tagset.  First tries to load a file with a ".ts"
   *	extension.  If that fails, tries to load a class, which had
   *	better be a subclass of Tagset, and create an instance of it
   *	(which had better have the right name). */
  public static Tagset loadTagset(String name) {
    return loadTagset(name, null);
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
      e.printStackTrace(log);
      return null;
    }
  }

  /** Load a Tagset implementation from an InputStream. 
   * 
   * @param src the InputStream to load from
   * @param boot if true, use the BOOT tagset for a stripped
   *	(<code>.tss</code>) tagset file. 
   */
  protected static Tagset loadTagsetFromStream(InputStream src, boolean boot) {
    Tagset ts = boot? (Tagset)new BOOT_ts() : (Tagset)new tagset();
    Parser p = ts.createParser();
    p.setReader(new InputStreamReader(src));
    TagsetProcessor proc = new TagsetProcessor();
    proc.setInput(p);
    proc.setTagset(ts);
    proc.setOutput(new DiscardOutput());

    proc.setVerbosity(verbosity);

    proc.run();
    return proc.getNewTagset();
  }

  /** Open a URLConnection to a named resource.  */
  protected static URLConnection loadResource(String name, String ext) {
    // === getResource or getSystemResource? ===
    // === prepend package name; change dots to slashes.

    if (ext != null) name += ext;
    URL u = boot_class.getResource(name);
    if (u != null) try {
      return u.openConnection();
    } catch (IOException ex) {
      ex.printStackTrace(log);
    }
    return null;
  }

  /** Close the input stream associated with a URLConnection. */
  protected static void close(URLConnection uc) {
    if (uc == null) return;
    try { uc.getInputStream().close(); } catch (Exception ex) {}
  }

  /** Return the last-modified date of a URLConnection.
   *	In some (all?) Java implementations a local resource may have
   *	a zero lastModified date, which is BAD.
   */
  protected static long lastModified(URLConnection uc) {
    if (uc == null) return 0;
    if (uc.getLastModified() != 0) return uc.getLastModified();
    // Stupid server or java impl. didn't return a date.  Bad java.  No cookie.
    return 0;
  }

  /** Load a Tagset implementation from a Java ``resource''. 
   *
   *	Tries ".tso", ".tss", and ".ts" in that order (but will not load a
   *	".tso" or ".tss" file older than the ".ts" file, since that is
   *	presumably the master copy.
   */
  protected static Tagset loadTagsetFromResource(String name) {
    boolean boot = false;
    Tagset ts = null;
    InputStream s = null;

    URLConnection tsUC  = loadResource(name, ".ts");
    URLConnection tsoUC = loadResource(name, ".tso");
    URLConnection tssUC = loadResource(name, ".tss");

    if (tsoUC != null && tsUC != null
	&& lastModified(tsoUC) >= lastModified(tsUC)) {
      try {
	ts = (Tagset)crc.util.Utilities.readObjectFrom(tsoUC.getInputStream());
      } catch (Exception ex) {
	ex.printStackTrace(log);
	ts = null;
      }
      if (ts != null) {
	close(tsUC); close(tssUC); close(tsoUC);
	if (verbosity > 0) {
	  log.println("Tagset loaded from resource " + name + ".tso");
	}
	return ts;
      }
    }
    if (tssUC != null && tsUC != null
	&& lastModified(tssUC) >= lastModified(tsUC)) {
      try { s = tssUC.getInputStream(); } catch (IOException ex) {
	ex.printStackTrace(log);
	s = null;
      }
      boot = true;
    } else if (tsUC != null) {
      try { s = tsUC.getInputStream(); } catch (IOException ex) {
	ex.printStackTrace(log);
	s = null;
      }
    } else return null;

    ts = (s == null)? null : loadTagsetFromStream(s, boot);
    close(tsUC); close(tssUC); close(tsoUC);
    if (verbosity > 0) {
      log.println("Tagset loaded from resource " + name
		  + (boot? ".tss" : ".ts"));
    }
    return ts;
  }

  /** Attempt to locate a file relative to a TopContext. 
   *	If the TopContext is null, just return a new File.
   */
  protected static File locateFile(String name, TopContext cxt) {
    return (cxt == null)
      ? new File(name) : cxt.locateSystemResource(name, false);
  }

  /** Load a Tagset from a file.  
   *
   *	Tries ".tso", ".tss", and ".ts" in that order (but will not load a
   *	".tso" or ".tss" file older than the ".ts" file, since that is
   *	presumably the master copy.
   */
  protected static Tagset loadTagsetFile(String name, TopContext cxt) {
    boolean boot = false;
    Tagset ts = null;
    File theFile;

    if (name.endsWith(".ts")
	|| name.endsWith(".tss")
	|| name.endsWith(".tso")) {
      // We know the extension already.
      theFile = locateFile(name, cxt);
    } else {
      File tsFile  = locateFile(name + ".ts", cxt);
      File tsoFile = locateFile(name + ".tso", cxt);
      File tssFile = locateFile(name + ".tss", cxt);

      if (tsoFile != null && tsoFile.exists() 
	  && tsFile != null && tsFile.exists()
	  && tsoFile.lastModified() > tsFile.lastModified()) {
	try {
	  ts = (Tagset)crc.util.Utilities.readObjectFrom(name+".tso");
	  if (verbosity > 0 && ts != null) {
	    log.println("Tagset loaded from file " + name + ".tso");
	  }
	} catch (Exception ex) {
	  ex.printStackTrace(log);
	  ts = null;
	}
	if (ts != null) return ts;
      }
      if (tssFile != null && tssFile.exists()
	  && tsFile != null && tsFile.exists()
	  && tssFile.lastModified() > tsFile.lastModified()) {
	name += ".tss";
	boot = true;
	theFile = tssFile;
      } else {
	name += ".ts";
	theFile = tsFile;
      }
    }
    FileInputStream s = null;
    try {
      s = new FileInputStream(theFile);
    } catch (FileNotFoundException ex) {
      return null;
    }
    if (verbosity > 0) {
      log.println("Tagset loaded from file " + name
		  + (boot? ".tss" : ".ts"));
    }
    ts = (s == null)? null : loadTagsetFromStream(s, boot);
    if (s != null) try { s.close(); } catch (Exception ex) {}
    return ts;
  }

}
