// Configuration.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

/**
 * A Configuration is an object that inspects the runtime environment
 *	and attempts to load properties and locate any other configuration
 *	files that may be necessary.  The end result is a Properties
 *	object that has system properties, command-line arguments, and
 *	the contents of configuration files merged in.<p>
 *
 *	There are several reasons for doing initialization this way:
 *	<ol>
 *	  <li> It keeps it out of the main program (i.e. Pia).
 *	  <li> We don't have to keep updating Pia (which everything
 *		depends on) whenever we add a new option.
 *	  <li> We can do initialization in a standardized way.
 *	</ol>
 */

package crc.pia;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.Properties;
import java.util.Enumeration;

import crc.ds.List;
import crc.ds.Table;

class Configuration {

  /************************************************************************
  ** Tables:
  ************************************************************************/

  /** The properties under construction. */
  public Properties properties;

  /** The original command line arguments. */
  public String[] commandLine;

  /** Any unprocessed command-line arguments. */
  public List commandLineTail = new List();

  /** Any unrecognized command-line arguments. */
  public List unrecOptions = new List();

  /** A list of environment variables and corresponding property names. */
  public String[] envTable = {
    "USER",	"user.name",
    "HOME",	"user.home",
  };

  /** A list of command-line options and corresponding property name,
   *	type, and default value.  Type is either "string" if a string
   *	follows the option, "bool" if only the option's presence is
   *	significant, "tail" if the option (usually a filename) is to
   *	be added to commandLineTail, and "rest" if the rest of the
   *	command line is to be collected and put into commandLineTail.
   *	Anything else is treated as "string".<p>
   *
   *	The null string as the option name matches any option that
   *	does not start with "-".  
   */
  public String[] optTable = {
    "", "", "tail", null,	// default is to save only filenames.
  };

  /************************************************************************
  ** Loading:
  ************************************************************************/

  /** Load a Configuration subclass and return an instance of it. */
  public static Configuration loadConfig(String className) {
    try {
      Object o = Class.forName(className).newInstance() ;
      return (Configuration) o;
    } catch (Exception ex) {
      System.err.println("Cannot load Configuration class "+className
			 + "because of:\n" + ex.getMessage());
      return null;
    }
  }
  
  /************************************************************************
  ** Configuration:
  ************************************************************************/

  /** Configure.  Effectively merge the system properties, environment 
   *	variables, and command line.
   *	@return true if any command-line options were unrecognized.
   */
  public boolean configure(String[] commandLineArgs) {
    if (properties == null)
      properties = new Properties(System.getProperties());

    commandLine = commandLineArgs;
    // suckEnvironment();
    return parseCommandLine(commandLine);
  }
  

  /** Suck in the environment variables specified in envTable. */
  protected void suckEnvironment() {
    if (envTable != null) {
      for (int i = 0; i < envTable.length; i += 2) {
	String v = System.getenv(envTable[i]);
	if (v != null) properties.put(envTable[i+1], v);
      }
    }
  }


  /** Print a usage table. */
  public void usage () {
    PrintStream o = System.out ;
    o.println("usage");

    for (int i = 0; i < optTable.length; i += 4) {
      String msg = "\t"+optTable[i]+"\t";

      if (!optTable[i+2].equals("bool")) {
	msg += optTable[i+2];
      }

      msg += "\t"+optTable[i+1];

      if (optTable[i+3] != null) {
	msg += "\t"+optTable[i+3];
      }
      o.println(msg);
    }
  }


  /** Parse the command line.  Any options not specified in optTable 
   *	are collected in unrecOptions.
   */
  protected boolean parseCommandLine(String[] args) {
    Table props = new Table();
    Table types = new Table();
    
    if (optTable != null)
      for (int i = 0; i < optTable.length; i += 4) {
	props.at(optTable[i], optTable[i+1]);
	types.at(optTable[i], optTable[i+2]);

	if (optTable[i+3] != null) {
	  if (properties.getProperty(optTable[i]) != null)
	    properties.put(optTable[i], optTable[i+3]);
	}
      }
    for (int i = 0; i < args.length; ++i) {
      String opt = args[i];
      String type = (String)types.at(opt);
      String prop = (String)props.at(opt);

      if (type == null) {
	if (! opt.startsWith("-")) {
	  type = (String)types.at("");
	  prop = (String)props.at("");
	}
      }

      if (type == null) {
	unrecOptions.push(opt);
      } else if (type.equals("string")) {
	++i;
	String value = (i < args.length)? args[i] : "";
	properties.put(prop, value);
      } else if (type.equals("tail")) {
	commandLineTail.push(opt);
      } else if (type.equals("rest")) {
	for ( ; i < args.length; ++i) 
	  commandLineTail.push(args[i]);
      } else if (type.equals("bool")) {
	properties.put(prop, "true");
      } else {
	++i;
	String value = (i < args.length)? args[i] : "";
	properties.put(prop, value);
      } 
      
    }

    return unrecOptions.nItems() > 0;
  }

  /************************************************************************
  ** Configuration Utilities:
  ************************************************************************/

  // file separator and other information
  private String filesep  = System.getProperty("file.separator");
  private String home     = System.getProperty("user.home");
  private String cwd	  = System.getProperty("user.dir");
  private String userName = System.getProperty("user.name");

  /** Locate a file given an enumeration of the names of directories
   *	it might be in.  Given a null filename, returns the first
   *	directory in the path that actually exists.
   */
  public String findFileInPath(String filename, Enumeration path) {
    while (path.hasMoreElements()) {
      String dir = fixFileName(path.nextElement().toString());
      File d = new File(dir);
      if (d.exists()) {
	if (filename == null) return d.getAbsolutePath();
	File f = new File(d, filename);
	if (f.exists()) return f.getAbsolutePath();
      }
    }
    return null;
  }

  /** Fix up a filename starting with "~" or "." */
  public String fixFileName(String filename) {
    if (filename.startsWith("~"))
      return home+filename.substring(1);
    if (filename.startsWith("."+filesep))
      return cwd+filename.substring(1);
    if (filename.equals(".")) return cwd;
    return filename;
  }
}

