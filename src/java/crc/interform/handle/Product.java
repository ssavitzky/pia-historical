////// Product.java:  Handler for <product>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;

/** Handler class for &lt;product&gt tag */
public class Product extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.deleteIt();
  }
}

/* ====================================================================
define_actor('product', 'dscr' => "Return product of numbers in CONTENT");

sub product_handle {
    my ($self, $it, $ii) = @_;

    my $list = list_items($it);
    my $result=1;

    my $n;
    foreach $n (@$list) {
	$result *= ref($n)? $n->content_text : $n;
    }
    $ii->replace_it($result);
}

*/
