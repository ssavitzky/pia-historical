////// Agent_list.java:  Handler for <agent-list>
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
 *	<agent-list [type=type][subs]>
 * Dscr:
 *	List the agents with given TYPE. Possibly SUBS only.
 */

/** Handler class for &lt;agent-list&gt tag */
public class Agent_list extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.unimplemented(ia);
  }
}

/* ====================================================================


sub agent_list_handle {
    my ($self, $it, $ii) = @_;

    my $type = get_text($it, 'type');
    my $subs = $it->attr('subs');

    my $resolver = IF::Run::resolver();
    my @names = sort($resolver->agent_names);
    my @list = ();

    foreach $name (@names) {
	my $agent = $resolver->agent($name);
	next if ($subs && $agent->type eq $agent->name);
	push (@list, $name) if $agent->type eq $type;
    }
    $ii->replace_it(join(' ', @list));
}
*/
