////// Agent_options.java:  Handler for <agent-options>
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

/** Handler class for &lt;agent-options&gt tag */
public class Agent_options extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.unimplemented(ia);
  }
}

/* ====================================================================
### <agent-options>

define_actor('agent-options', 'empty' => 1,  'unsafe' => 1,
	     'dscr' => "Returns list of option names for agent NAME" );

sub agent_options_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    my $agent;

    if ($name) {
	$agent = IF::Run::resolver()->agent($name);
    } else {
	$agent = IF::Run::agent();
	$name = $agent->name;
    }

    $ii->replace_it(join(' ', @{$agent->attr_names}));
}

*/
