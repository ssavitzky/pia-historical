////// Sorted.java:  Handler for <sorted>
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
 *	
 * Dscr:
 *	
 */

/** Handler class for &lt;sorted&gt tag */
public class Sorted extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.unimplemented(ia);
  }
}

/* ====================================================================
### <sorted [options]>strings</sorted>

define_actor('sorted', 'content' => 'list', 
	     'dscr' => "test tokens in  LIST (content) for sortedness;
return null or IFFALSE if false, else '1' or IFTRUE. 
Modifiers: NOT, CASE (sensitive), TEXT, LINK, NUMERIC, REVERSE.");

sub sorted_handle {
    my ($self, $it, $ii) = @_;

    my $list = get_list($it);
    my $reverse = $it->attr('reverse');
    my $prep = prep_item_sub($it);
    my $compare;

    if ($it->attr('numeric')) {
	$compare = $reverse? sub {$a >= $b} : sub {$a <= $b};
    } elsif ($it->attr('text')) {
	$compare = $reverse? sub {$a ge $b} : sub {$a le $b};
    } else {
	$compare = $reverse? sub {$a ge $b} : sub {$a le $b};
    }

    foreach $b (@$list) {
	$b = &$prep($b);
	if (defined $a && ! &$compare($a, $b)) {
	    test_result('', $it, $ii);
	    return;
	}	    
	$a = $b;
    }
    test_result(1, $it, $ii);
}

*/
