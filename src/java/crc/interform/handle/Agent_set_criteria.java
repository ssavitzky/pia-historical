////// Agent_set_criteria.java:  Handler for <agent-set-criteria>
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
 *	<agent-set-criteria name="agent-name">query_string</agent-set-criteria>
 * Dscr:
 *	Sets CONTENT (query string or dl) as criteria for agent NAME.
 */

/** Handler class for &lt;agent-set-criteria&gt tag */
public class Agent_set_criteria extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.unimplemented(ia);
  }
}

/* ====================================================================

### <agent-set-criteria>query_string</agent-set-criteria>

if (0) { #=== buggy!
define_actor('agent-set-criteria', 'unsafe' => 1, 'content' => 'criteria',
	     'dscr' => "Sets CRITERIA for agent NAME" );
}
sub agent_set_criteria_handle {
    my ($self, $it, $ii) = @_;

    my $criteria = get_list($it, 'criteria');
    my $name = $it->attr('name');
    my $agent;

    if ($name) {
	$agent = IF::Run::resolver()->agent($name);
    } else {
	$agent = IF::Run::agent();
	$name = $agent->name;
    }

    $agent->criteria($criteria); # === almost certainly wrong ===
    $ii->delete_it();
}
*/
