////// Agent_list.java:  Handler for <agent-list>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.sgml.SGML;
import crc.sgml.Token;
import crc.sgml.Tokens;
import crc.sgml.Text;


/** Handler class for &lt;agent-list&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;agent-list [type=type][subs]&gt;
 * <dt>Dscr:<dd>
 *	List the agents with given TYPE. Possibly SUBS only.
 *  </dl>
 */
public class Agent_list extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<agent-list [type=type][subs]>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "List the agents with given TYPE. Possibly SUBS only.\n" +
"";
 
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
