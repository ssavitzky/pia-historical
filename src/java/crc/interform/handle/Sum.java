////// Sum.java:  Handler for <sum>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;

/** Handler class for &lt;sum&gt tag */
public class Sum extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.deleteIt();
  }
}

/* ====================================================================
###### Number Processing:

### <sum>list</sum> <difference>list</difference> 
### <product>list</product> <quotient>list</quotient>

define_actor('sum', 'dscr' => "Return sum of numbers in CONTENT");

sub sum_handle {
    my ($self, $it, $ii) = @_;

    my $list = list_items($it);
    my $result=0;

    my $n;
    foreach $n (@$list) {
	$result += ref($n)? $n->content_text : $n;
    }
    $ii->replace_it($result);
}
*/
