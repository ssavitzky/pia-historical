////// Difference.java:  Handler for <difference>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;

/** Handler class for &lt;difference&gt tag */
public class Difference extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.deleteIt();
  }
}

/* ====================================================================

define_actor('difference', 'dscr' => "Return difference of numbers in CONTENT");

sub difference_handle {
    my ($self, $it, $ii) = @_;

    my $list = list_items($it);
    my $result=shift(@$list);
    $result = $result->content-text if ref($result);

    my $n;
    foreach $n (@$list) {
	$result -= ref($n)? $n->content_text : $n;
    }
    $ii->replace_it($result);
}

*/
