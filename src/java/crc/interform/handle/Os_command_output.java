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

    ii.unimplemented(ia);
  }
}

/* ====================================================================

### <os-command-output>command</os-command-output>

define_actor('os-command-output', 'unsafe' => 1, 'content' => 'command', 
	     'dscr' => "Execute CONTENT as an operating system command 
and capture its output.  Optionally BYPASS proxies." );

sub os_command_output_handle {
    my ($self, $it, $ii) = @_;

    my $command = get_text($it, 'command');
    my $proxies = $it->attr('bypass')? "" : $main::proxies;
				# replace &gt; with > symbol
    $command =~ s/\&gt\;/>/g;

    print "Executing command `$command`\n" unless $main::quiet;


    my $result;
    eval {
	$result = `$command`;
	$result = `sh -c '$proxies cat /dev/null | $command '`;
    };
    $ii->replace_it($result);
}
*/
