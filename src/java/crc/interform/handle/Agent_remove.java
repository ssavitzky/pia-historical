////// Agent_remove.java:  Handler for <agent-remove>
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

/** Handler class for &lt;agent-remove&gt tag */
public class Agent_remove extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.unimplemented(ia);
  }
}

/* ====================================================================
### <agent-remove>name</agent-remove>

define_actor('agent-remove', 'unsafe' => 1, 'content' => 'name',
	     'dscr' => "Remove (uninstall) an agent with given NAME." );

sub agent_remove_handle {
    my ($self, $it, $ii) = @_;

    my $name = get_text($it, 'name');
    my $agent = IF::Run::agent(); # had better be agency

    $agent->un_install_agent($name) if defined $name;
    $ii->replace_it($name);
}
*/
