////// Os_command_output.java:  Handler for <os-command-output>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;
import crc.interform.Tokens;
import crc.interform.Text;
import crc.interform.Util;

/* Syntax:
 *	<os-command-output [bypass]>command</os-command-output>
 * Dscr:
 *	Execute CONTENT as an operating system command 
 *	and capture its output.  Optionally BYPASS proxies.
 */

/** Handler class for &lt;os-command-output&gt tag */
public class Os_command_output extends crc.interform.Handler {
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

    // using pipes instead of redirection works even if "cmd" contains
    // redirection or pipes.
    cmd = "sh -c '"+proxies+" cat /dev/null | "+cmd+"'";

    ii.message("Executing: "+cmd);

    boolean process = it.hasAttr("process");

    java.io.InputStream in = null;
    String buffer = "";
    try {
      Process p = runtime.exec(cmd);
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
      }
    } catch (Exception e) {
      ii.error(ia, "in command '"+cmd+"'");
    } finally {
      if (process) {
	if (in != null) try {in.close();} catch(Exception e){}
	ii.replaceIt(buffer);
      } else {
	ii.deleteIt();
      }
    }
  }
}
