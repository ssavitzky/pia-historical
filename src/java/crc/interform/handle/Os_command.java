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

/* Syntax:
 *	
 * Dscr:
 *	
 */

/** Handler class for &lt;os-command&gt tag */
public class Os_command extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.unimplemented(ia);
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
