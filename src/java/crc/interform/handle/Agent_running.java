////// Agent_running.java:  Handler for <agent-running>
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

/** Handler class for &lt;agent-running&gt tag */
public class Agent_running extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.unimplemented(ia);
  }
}

/* ====================================================================
### <agent-running name=name>
###	Tests whether an agent is running

define_actor('agent-running', 'content' => 'name', 
	     'dscr' => "Tests whether an agent is running (installed)" );

sub agent_running_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    $name = $it->content_text unless defined $name;

    my $a = IF::Run::resolver()->agent($name);
    if (ref $a) {
	$ii->replace_it($name);
    } else {
	$ii->delete_it();
    }
}

*/
