////// Agent_home.java:  Handler for <agent_home>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;

/** Handler class for &lt;agent-home&gt tag */
public class Agent_home extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.deleteIt();
  }
}

/* ====================================================================
### <agent-home>name</agent-home>
###	expands to the agent's home interForm name.
###	Makes a link if the "link" attribute is present.

###	This is incredibly kludgy, but it works!

define_actor('agent-home', 'content' => 'name', 
	     'dscr' => "Get path to a pia agent's home InterForm.
Optionally make a LINK.  Very kludgy." );

sub agent_home_handle {
    my ($self, $it, $ii) = @_;

    my $name = get_text($it, 'name');
    my $link = $it->attr('link');

    my $a = IF::Run::resolver()->agent($name);

    if (!ref $a) {
	$ii->delete_it;
	return;
    }
    my $type = $a->type;
    my $home = ($type ne $name)? "$type/$name" : "$name";

    $home = IF::IT->new('a', 'href'=>"/$home/home.if", $home) if $link;
    $ii->replace_it($home);
}

*/
