////// Os_command.java:  Handler for <os-command>
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

import java.lang.Process;
import java.lang.Runtime;

/* Syntax:
 *	<os-command [bypass]>command</os-command>
 * Dscr:
 *	Execute CONTENT as an operating system command 
 *	in the background with proxies set to PIA.  
 *	Optionally BYPASS proxies.
 */

/** Handler class for &lt;os-command&gt tag */
public class Os_command extends crc.interform.Handler {
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

    if (cmd.indexOf('>') < 0) { // redirect to /dev/null if no redirection.
      cmd = cmd + " > /dev/null";
    }

    // using pipes instead of redirection works even if "cmd" contains
    // redirection or pipes.
    cmd = "sh -c '"+proxies+" cat /dev/null | "+cmd+"'";

    ii.message("Executing: "+cmd);
    try {
      runtime.exec(cmd);
    } catch (Exception e) {
      ii.error(ia, "in command '"+cmd+"'");
    }

    ii.deleteIt();
  }
}

/* ====================================================================

###### Operating System:

### <os-command>command</os-command>

define_actor('os-command', 'unsafe' => 1, 'content' => 'command', 
	     'dscr' => "Execute CONTENT as an operating system command 
in the background with proxies set.  Optionally BYPASS proxies." );

sub os_command_handle {
    my ($self, $it, $ii) = @_;

    my $command = get_text($it, 'command');
    my $proxies = $it->attr('bypass')? "" : $main::proxies;

				# replace &gt; with > symbol
    $command .="  > /dev/null " unless $command =~ s/\&gt\;/>/g;

    print "Executing command '$command'\n" unless $main::quiet;

    my $pid;
    unless ($pid = fork) {
	unless (fork) {
	    system("sh -c '$proxies cat /dev/null | $command '");
	    exit 0;
	}
	exit 0;
    }
    waitpid($pid, 0);

    $ii->delete_it;
}
*/
