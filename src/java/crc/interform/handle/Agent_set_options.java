////// Agent_set_options.java:  Handler for <agent-set-options>
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
 *	<agent-set-options [name="agent-name"]>options</agent-set-options>
 * Dscr:
 *	Sets CONTENT as options for agent NAME.
 */

/** Handler class for &lt;agent-set-options&gt tag */
public class Agent_set_options extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.unimplemented(ia);
  }
}

/* ====================================================================
### <agent-set-options>query_string</agent-set-options>

define_actor('agent-set-options', 'unsafe' => 1, 'content' => 'options', 
	     'dscr' => "Sets OPTIONS for agent NAME" );

sub agent_set_options_handle {
    my ($self, $it, $ii) = @_;

    my $options;# = get_hash($it, 'options');
    $options = IF::Run::request()->parameters unless ref $options;
    my $name = $it->attr('name');
    my $agent;

    if ($name) {
	$agent = IF::Run::resolver()->agent($name);
    } else {
	$agent = IF::Run::agent();
	$name = $agent->name;
    }

    my $i = 0;
    if ($options) {
	foreach $key (keys(%{$options})){
	    print("  setting $key = ",$$option{$key},"\n") if  $main::debugging;
	    $agent->option($key, $$options{$key});
	    ++$i;
	}
    }
    if ($i) {
	$ii->replace_it("$i");
    } else {
	$ii->delete_it();
    }
}

*/
