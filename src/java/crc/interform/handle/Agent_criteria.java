////// Agent_criteria.java:  Handler for <agent-criteria>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.sgml.SGML;
import crc.sgml.Token;
import crc.sgml.Tokens;
import crc.sgml.Text;


/** Handler class for &lt;agent-criteria&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;agent-criteria [name=ident]&gt;
 * <dt>Dscr:<dd>
 *	Return the match-criteria list for agent NAME or the current agent.
 *  </dl>
 */
public class Agent_criteria extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<agent-criteria [name=ident]>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Return the match-criteria list for agent NAME or the current agent.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.unimplemented(ia);
  }
}

/* ====================================================================

### <agent-criteria>

define_actor('agent-criteria', 'empty' => 1,  'unsafe' => 1,
	     'dscr' => "Returns list of match criteria for agent NAME" );

sub agent_criteria_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    my $agent;

    if ($name) {
	$agent = IF::Run::resolver()->agent($name);
    } else {
	$agent = IF::Run::agent();
	$name = $agent->name;
    }

    $ii->replace_it(criteria_to_list($agent->criteria));
}


sub criteria_to_list {
    my ($criteria) = @_;

    my $list = IF::IT->new('dl');
    my $i;

    for ($i = 0; $i <= $#$criteria; $i++) {
	my $c = $$criteria[$i];
	if (! ref $c) {
	    $list->push(IF::IT->new('dt', $c));
	    local $v = $$criteria[++$i];
	    if (! ref $v) {
		$list->push(IF::IT->new('dd', $v));
	    } else {
		$list->push(IF::IT->new('dd', $v));
	    }
	} elsif (ref($c) eq 'ARRAY') {
	    $list->push(IF::IT->new('dt', criteria_to_list($c)));
	}
    }
    return $list;
}    
*/
