////// Subst.java:  Handler for <subst>
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
 *	<subst match="pattern" result="string">text</subst>
 * Dscr:
 *	Substitute RESULT string for MATCH pattern in CONTENT.
 */

/** Handler class for &lt;subst&gt tag */
public class Subst extends crc.interform.Handler {
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
