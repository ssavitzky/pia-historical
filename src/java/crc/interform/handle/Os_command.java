////// Os_command.java:  Handler for <os-command>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;

import java.lang.Process;
import java.lang.Runtime;


/** Handler class for &lt;os-command&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;os-command [bypass]&gt;command&lt;/os-command&gt;
 * <dt>Dscr:<dd>
 *	Execute CONTENT as an operating system command 
 *	in the background with proxies set to PIA.  
 *	Optionally BYPASS proxies.
 *  </dl>
 */
public class Os_command extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<os-command [bypass]>command</os-command>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Execute CONTENT as an operating system command \n" +
    "in the background with proxies set to PIA.  \n" +
    "Optionally BYPASS proxies.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    crc.interform.Environment env = ii.environment;
    Runtime runtime = Runtime.getRuntime();
    SGML content = Interp.expandEntities(it.content(),
					 Interp.defaultEntities());
    String cmd = (content == null)? null : content.toString();
    if (cmd == null || "".equals(cmd)) {
      ii.error(ia, "Null command");
    }

    String proxies = it.hasAttr("bypass")? "" : env.proxies();
    if (! "".equals(proxies)) proxies += " ";

    if (cmd.indexOf('>') < 0) { // redirect to /dev/null if no redirection.
      cmd = cmd + " > /dev/null";
    }

    // using pipes instead of redirection works even if "cmd" contains
    // redirection or pipes.
    cmd = "cat /dev/null | "+proxies+ " " + cmd;
    ii.message("Executing: "+cmd);

    // We have to use a string array because Java doesn't parse
    // shell commands correctly, and uses execve instead of system.

    String cmdArray[] = {"/bin/sh", "-c", cmd};

    try {
      runtime.exec(cmdArray);
    } catch (Exception e) {
      ii.error(ia, "attempting to run '"+cmd+"' ->\n"+e.toString());
    }

    ii.deleteIt();
  }
}

