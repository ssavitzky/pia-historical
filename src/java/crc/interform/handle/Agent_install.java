////// Agent_install.java:  Handler for <agent-install>
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
 *	<agent-install name=ident [type=ident]>...</agent-install>
 * Dscr:
 *	Install an agent with given NAME and TYPE.  CONTENT is options form.
 *	Returns the agent's name.
 */

/** Handler class for &lt;agent-install&gt tag */
public class Agent_install extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", "=undefined=");
    String type = Util.getString(it, "type", name);
    Run env = Run.environment(ii);

    ii.error(ia, "incomplete"); // === incomplete ===

    ii.replaceIt(name);
  }
}

/* ====================================================================

### <agent-install name='n' type='t'>

define_actor('agent-install', 'content' => 'options',
	     'dscr' => "Installs an agent with given OPTIONS (content).
Returns the agent's name." );

sub agent_install_handle {
    my ($self, $it, $ii) = @_;

    ## urlQuery is undefined because installation is normally a POST
    #my $options = get_hash($it, 'options'); # === broken: urlQuery undef. ===
    my $options = IF::Run::request()->parameters;

    my $agent = IF::Run::agent(); # had better be agency
    $agent = $agent->install($options);
    my $name = ref $agent ? $agent->name : '';
    $ii->replace_it($name);
}

*/
