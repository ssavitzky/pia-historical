////// Sort.java:  Handler for <sort>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Tokens;


/** Handler class for &lt;sort&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;sort [case][text][numeric][reverse]&gt;item, ...&lt;/sort&gt;
 * <dt>Dscr:<dd>
 *	Sort items in CONTENT.  Optionally 
 *	CASE (sensitive), TEXT, NUMERIC, REVERSE.
 *  </dl>
 */
public class Sort extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<sort [case][text][numeric][reverse]>item, ...</sort>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Sort items in CONTENT.  Optionally \n" +
    "CASE (sensitive), TEXT, NUMERIC, REVERSE.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.unimplemented(ia);
  }
}

/* ====================================================================
### <sort [options]>strings</sort>
###	sort tokens
### ===	needs to handle pairs, too!
###
###  Modifiers:
###	not
###	case (sensitive)
###	text
###	numeric
###
define_actor('sort', 'content' => 'value', 
	     'dscr' => "sort tokens in  LIST (content).
Modifiers: CASE (sensitive), TEXT, NUMERIC, REVERSE.");

sub sort_handle {
    my ($self, $it, $ii) = @_;

    my $list = get_list($it);
    my $reverse = $it->attr('reverse');
    my $prep = prep_item_sub($it);

    my @tmp = map { [&{$prep}($_), $_] } @$list;
    my @out; 

    if ($it->attr('numeric')) {
	if ($reverse) {
	    @out = map {$_->[1]} sort {$b->[0] <=> $a->[0]} @tmp;
	} else {
	    @out = map {$_->[1]} sort {$a->[0] <=> $b->[0]} @tmp;
	}
    } else {
	if ($reverse) {
	    @out = map {$_->[1]} sort {$b->[0] cmp $a->[0]} @tmp;
	} else {
	    @out = map {$_->[1]} sort {$a->[0] cmp $b->[0]} @tmp;
	}
    }

    list_result(\@out, $it, $ii);
}

*/
