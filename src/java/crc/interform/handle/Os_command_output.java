////// Os_command_output.java:  Handler for <os-command-output>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import java.lang.Process;
import java.lang.Runtime;

import crc.sgml.SGML;


/** Handler class for &lt;os-command-output&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#os-command-output">Manual
 *	Entry</a> for syntax and description.
 */
public class Os_command_output extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<os-command-output [bypass]>command</os-command-output>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Execute CONTENT as an operating system command \n" +
    "and capture its output.  Optionally BYPASS proxies.\n" +
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

    // using pipes instead of redirection works even if "cmd" contains
    // redirection or pipes.

    cmd = "cat /dev/null | "+proxies+ " " + cmd;
    ii.verbose("Executing: "+cmd);

    // We have to use a string array because Java doesn't parse
    // shell commands correctly, and uses execve instead of system.

    String cmdArray[] = {"/bin/sh", "-c", cmd};

    boolean process = it.hasAttr("process");

    java.io.InputStream in = null;
    String buffer = "";
    String error  = "";
    try {
      Process p = runtime.exec(cmdArray);
      in = p.getInputStream();

      if (process) {
	String tsname = it.attrString("tagset");
	if (tsname != null) ii.useTagset(tsname);
	ii.pushInput(new crc.interform.Parser(in, null));
      } else {
	for(;;){
	  int b = in.read();
	  if (b == -1) break;
	  buffer += (char)b;
	}
	in.close();
	in = null;
	ii.debug("output-->"+buffer);

	in = p.getErrorStream();
	for(;;){
	  int b = in.read();
	  if (b == -1) break;
	  error += (char)b;
	}
	in.close();
	in = null;
	ii.verbose("stderr-->"+error);
      }
    } catch (Exception e) {
      ii.error(ia, "attempting to run '"+cmd+"' ->\n"+e.toString());
    } finally {
      if (!process) {
	if (in != null) try {in.close();} catch(Exception e){}
	ii.replaceIt(buffer);
      } else {
	ii.deleteIt();
      }
    }
  }
}
