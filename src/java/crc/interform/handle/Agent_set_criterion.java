////// Agent_set_criterion.java:  Handler for <agent-set-criterion>
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
import crc.interform.Run;

/* Syntax:
 *	<agent-set-criterion name="name" [value="value"] 
 *			     [agent="agent-name"]>
 * Dscr:
 *	set match criterion NAME to VALUE (default 1), 
 *	optionally in AGENT.
 */

/** Handler class for &lt;agent-set-criterion&gt tag */
public class Agent_set_criterion extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.unimplemented(ia);
  }
}

/* ====================================================================

define_actor('agent-set-criterion', 'empty' => 1, 'unsafe' => 1, 
	     'dscr' => "set match criterion NAME to VALUE (default 1), 
optionally in AGENT.");

sub agent_set_criterion_handle {
    my ($self, $it, $ii) = @_;

    my $aname = $it->attr('agent');
    my $agent;

    if ($aname) {
	$agent = IF::Run::resolver()->agent($aname);
    } else {
	$agent = IF::Run::agent();
	$aname = $agent->name;
    }

    my $name = $it->attr('name');
    my $value = $it->attr('value');
    $value = 1 unless defined $value;
    $agent->match_criterion($name, $value);
    $ii->delete_it();
}
*/
