////// Quotient.java:  Handler for <quotient>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;

/** Handler class for &lt;quotient&gt tag */
public class Quotient extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.deleteIt();
  }
}

/* ====================================================================
define_actor('quotient', 'dscr' => "Return quotient of numbers in CONTENT");

sub quotient_handle {
    my ($self, $it, $ii) = @_;

    my $list = list_items($it);
    my $result=shift(@$list);
    $result = $result->content-text if ref($result);

    my $n;
    foreach $n (@$list) {
	$n = $n->content_text if ref($n);
	if ($n == 0) {
	    $ii->replace_it('***');
	    return;
	}
	$result /= $n;
    }
    $ii->replace_it($result);
}

*/
