////// If.java:  Handler for <if>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;

/** Handler class for &lt;if&gt tag */
public class If extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.deleteIt();
  }
}

/* ====================================================================

###### Control Structure:

### <if><test>condition</test><then>...</then><else>...</else></if>
###	condition is false if it is empty or consists only of whitespace.

define_actor('if', 
	     'dscr' => "if TEST non-null, expand THEN, else ELSE.");
define_actor('then', 'quoted' => 'quoted', 'handle' => 'null',
	     'dscr' => "expanded if TEST true in an &lt;if&gt;");
define_actor('else', 'quoted' => 'quoted', 'handle' => 'null',
	     'dscr' => "expanded if TEST true in an &lt;if&gt;");

sub if_handle {
    my ($self, $it, $ii) = @_;

    ## The right way to do this would be to parse the condition, then activate 
    ##	  appropriate actors for <then> and <else>.

    my $parts = &analyze($it->content, ['cond', 'then', 'else'], 1);
    my $test = remove_spaces($parts->{'cond'});
    $test = scalar @$test if ref($test);

    if ($test) {
	print "<if >$test<then>...\n" if $main::debugging > 1;
	$ii->push_into($parts->{'then'});
    } else {
	print "<if >$test<else>...\n" if $main::debugging > 1;
	$ii->push_into($parts->{'else'});
    }
    $ii->delete_it;
}

*/
