// Setup.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

/**
 * Setup is used to isolate the PIA's setup process: reading properties and
 *	environment variables, checking for the existance of the various
 *	directories, and adding the things we need to the system properties.<p>
 *
 *	There is some hope that a sufficiently-clever runtime could
 *	unload the code after it is needed.  Even if that proves
 *	impossible, the only class that depends on Setup is Pia, so
 *	changes in the setup code will not cause massive recompilation.
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

import crc.pia.Pia;
import crc.pia.Configuration; 

import crc.ds.List;

class Setup extends Configuration {

  /** Reference to the Pia instance. */
  protected Pia pia;

  /** PIA environment table: */
  protected String[] piaEnvTable = {
    "USER",	"user.name",
    "HOME",	"user.home",
    "PIA_DIR",	"crc.pia.piaroot",
    "USR_DIR",	"crc.pia.usrroot",
    "PIA_PORT",	"crc.pia.port",
  };

  /** PIA option table: */
  protected String[] piaOptTable = {
    "-u",	"crc.pia.usrroot",	"dir",		null,
    "-p",	"crc.pia.port",		"number",	"8888",
    "-d",	"crc.pia.debug",	"bool",		null,
    "-v",	"crc.pia.verbose",	"bool",		null,
    "-port",	"crc.pia.port",		"number",	"8888",
    "-root",	"crc.pia.piaroot",	"dir",		null,
    "-profile",	"crc.pia.profile",	"file",		null,
    "-filemap",	"crc.pia.filemap",	"file",		null,
  };

  /* Perl options: ================================================
	-s PIA_DIR	source dir: (.:~/pia/src:/pia1/pia/src)
	-u USR_DIR	(~/.PIA)
	-l logfile
	-p port		(8001)
	-c command
	-v		verbose
	-q		quiet
	-d[N]		debugging
	-e		print out setenv commands for proxying
	-f		proxy ftp as well (optional because flaky)
	-x		exit after printing info, starting command (if any)
  */


  /** Print a usage message. */
  public void xusage () {
	PrintStream o = System.out ;

	o.println("usage: PIA [OPTIONS]") ;
	o.println("-port <8001>          : listen on the given port number.");
	o.println("-root <pia dir : /pia>: pia directory.");
	o.println("-u    <~/Agent>       : user directory.") ;
	o.println("-p    </pia/config/pia.props>       : property file to read.");
	o.println("-d                    : turns debugging on.") ;
	o.println("-v                    : print pia Piaproperties.");
	o.println("?                     : print this help.");
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** 
   * Construct a Setup object and initialize the tables.
   */
  public Setup() {
    super();

    pia = Pia.instance();
    properties = pia.properties();

    envTable = piaEnvTable;
    optTable = piaOptTable;
  }
  
  /************************************************************************
  ** Configuration:
  ************************************************************************/

  /**
   * Perform special configuration for the PIA
   */
  public boolean configure(String[] commandLineArgs) {
    List path;

    /* Call super to parse the command line and system props. */
    boolean results = super.configure(commandLineArgs);

    /* Merge properties from the profile, if specified. */

    String profile = properties.getProperty("crc.pia.profile");
    if (profile != null) {
      if (mergeProperties(profile)) {
	System.err.println("Loaded properties from "+profile);
      } else {
	System.err.println("Warning! Profile (properties) file " + profile
			   + " does not exist. \nProceeding anyway.");
      }
    }

    /* Make sure we have a PIA directory */

    String piaRoot = properties.getProperty("crc.pia.piaroot");
    
    if (piaRoot == null) {
      System.err.println("Cannot locate PIA root (install) directory.\n"
			 + "  Please put the PIA's binary directory in your "
			 + "shell's search path, \n  or specify the -root "
			 + "option on the command line." );
      return true;
    }
    piaRoot = fixFileName(piaRoot);
    if (!fileExists(piaRoot)) {
      System.err.println("Error: "+piaRoot+" does not exist.\n");
      return true;
    }
    if (!dirExists(piaRoot)) {
      System.err.println("Error: "+piaRoot+" is not a directory.\n");
      return true;
    }
    properties.put("crc.pia.piaroot", piaRoot);

    /* Check to see if we have a user directory.  Warn the user if we don't,
     *	but proceed (to possible disaster). */

    String usrRoot = properties.getProperty("crc.pia.usrroot");
    if (usrRoot == null) {
      if (dirExists(home + filesep + ".pia")) {
	usrRoot = home + filesep + ".pia";
      }
    }
    if (usrRoot == null) {
      System.err.println("Warning! "
			 + "  You do not have a personal '.pia' directory.\n"
			 + "  Please create one, or specify an alternative "
			 + "with the -u option on the command line." );

      // We could get more elaborate and use /tmp or try some alternatives.

      System.err.println("Continuing without a user root");
    } else {
      properties.put("crc.pia.usrroot", fixFileName(usrRoot));
    }

    /* Load the user's default profile if there is one and it hasn't already
     *	been loaded. */
    if (profile == null) {
      profile = usrRoot+filesep+"Config" + filesep + "pia.props";
      if (fileExists(profile) && mergeProperties(profile)) {
	System.err.println("Loaded properties from "+profile);
      }
    }

    /* Make sure there's a filemap */

    String fileMap = properties.getProperty("crc.pia.filemap");
    if (fileMap == null) {
      path = new List();
      if (usrRoot != null) path.push(usrRoot);
      path.push(piaRoot);
      fileMap = findFileInPath("Config" + filesep + "filemap.props",
			       path.elements());
      if (fileMap == null) {
	System.err.println("Cannot locate Config/filemap.props.\n"
			   + "  Please specify with the -filemap "
			   + "option on the command line.");
	return false;
      }
    } else {
      properties.put("crc.pia.filemap", fixFileName(fileMap));
    }

    return results;
  }
  
}

