////// Agent_set_criteria.java:  Handler for <agent-set-criteria>
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


/** Handler class for &lt;agent-set-criteria&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;agent-set-criteria name="agent-name"&gt;query_string&lt;/agent-set-criteria&gt;
 * <dt>Dscr:<dd>
 *	Sets CONTENT (query string or dl) as criteria for agent NAME.
 * <dt>Note:<dd>
 *	this never worked, so perhaps we should delete it.
 *  </dl>
 */
public class Agent_set_criteria extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<agent-set-criteria name=\"agent-name\">query_string</agent-set-criteria>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Sets CONTENT (query string or dl) as criteria for agent NAME.\n" +
"";
  public String note() { return noteStr; }
  static String noteStr=
    "this never worked, so perhaps we should delete it.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.unimplemented(ia);	// === never worked -- delete? ===
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
