////// Tagset_include.java:  Handler for <tagset-include>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;

/** Handler class for &lt;tagset-include&gt tag */
public class Tagset_include extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.deleteIt();
  }
}

/* ====================================================================
define_actor('tagset-include', 'empty'=> 1,
	     'dscr' => "include an InterForm tagset called NAME.");

sub tagset_include_handle {
    my ($self, $it, $ii) = @_;

    my $name = $it->attr('name');
    $ii->replace_it($ii->tagset->include('name'));
}

*/
