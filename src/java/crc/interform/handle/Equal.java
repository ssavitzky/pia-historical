////// Equal.java:  Handler for <equal>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Token;
import crc.sgml.Tokens;
import crc.sgml.Text;


/** Handler class for &lt;equal&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;equal [not] [case] [text] [link] [numeric]&gt;list...&lt;/equal&gt;
 * <dt>Dscr:<dd>
 *	Test list items in CONTENT for equality; 
 *	return null or IFFALSE if false, else '1' or IFTRUE. 
 *	<dt>Modifiers:<dd> NOT, CASE (sensitive), TEXT, LINK, NUMERIC.
 *  </dl>
 */
public class Equal extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<equal [not] [case] [text] [link] [numeric]>list...</equal>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Test list items in CONTENT for equality; \n" +
    "return null or IFFALSE if false, else '1' or IFTRUE. \n" +
    "Modifiers: NOT, CASE (sensitive), TEXT, LINK, NUMERIC.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.unimplemented(ia);
  }
}

/* ====================================================================
### <equal [options]>strings</equal>

define_actor('equal', 'content' => 'list',
	     'dscr' => "test tokens in LIST (content) for equality; 
return null or IFFALSE if false, else '1' or IFTRUE. 
Modifiers: NOT, CASE (sensitive), TEXT, LINK, NUMERIC.");

sub equal_handle {
    my ($self, $it, $ii) = @_;

    my $list = get_list($it);
    my ($a, $b, $compare);
    my $prep = prep_item_sub($it);

    if ($it->attr('numeric')) {
	$compare = sub{$a == $b};
    } else {
	$compare = sub{$a eq $b};
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
