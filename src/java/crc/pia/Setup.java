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

class Setup extends Configuration {

  /** Reference to the Pia instance. */
  protected Pia pia;

  /** PIA environment table: */
  protected String[] piaEnvTable = {
    "USER",	"user.name",
    "HOME",	"user.home",
    "PIA_DIR",	"crc.pia.root",
    "USR_DIR",	"crc.pia.usrroot",
    "PIA_PORT",	"crc.pia.port",
  };

  /** PIA option table: */
  protected String[] piaOptTable = {
    "-port",	"crc.pia.port",		"number",	"8888",
    "-root",	"crc.pia.piaroot",	"file",		null,
    "-u",	"crc.pia.usrroot",	"file",		null,
    "-d",	"crc.pia.debug",	"bool",		null,
    "-v",	"crc.pia.verbose",	"bool",		null,
    "-p",	"crc.pia.profile",	"file",		null,
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

    /* Call super to parse the command line and system props. */
    boolean results = super.configure(commandLineArgs);

    /* Get the Piaproperties because it has getBoolean, etc. */
    Piaproperties props = pia.properties();

    return results;
  }
  
}

