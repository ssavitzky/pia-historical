////// Subst.java:  Handler for <subst>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Text;


/** Handler class for &lt;subst&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;subst match="pattern" result="string"&gt;text&lt;/subst&gt;
 * <dt>Dscr:<dd>
 *	Substitute RESULT string for MATCH pattern in CONTENT.
 *  </dl>
 */
public class Subst extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<subst match=\"pattern\" result=\"string\">text</subst>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Substitute RESULT string for MATCH pattern in CONTENT.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.unimplemented(ia);
  }
}

/* ====================================================================
### <subst match="pattern" result="pattern">text</subst>
### global by default
define_actor('subst', 'dscr' => "substitute pattern in text");

sub subst_handle{
    my ($self, $it, $ii) = @_;

    my $match = $it->attr('match');
    return unless defined $match;
    my $pattern = $it->attr('result');
    my $string = $it->content_string;
    if($string =~ s/$match/$pattern/g){
	$ii->replace_it($string);
	return;
    }
    $ii->replace_it('');
		    
}

*/
