////// Sorted.java:  Handler for <sorted>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Tokens;


/** Handler class for &lt;sorted&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;sorted [case][text][numeric][reverse]&gt;item, ...&lt;/sorted&gt;
 * <dt>Dscr:<dd>
 *	Test whether items in CONTENT are sorted.  Optionally 
 *	CASE (sensitive), TEXT, NUMERIC, REVERSE.
 */
public class Sorted extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<sorted [case][text][numeric][reverse]>item, ...</sorted>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Test whether items in CONTENT are sorted.  Optionally \n" +
    "CASE (sensitive), TEXT, NUMERIC, REVERSE.\n" +
    "\n" +
"";
 
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
