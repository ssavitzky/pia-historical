////// Test.java:  Handler for <test>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;

/** Handler class for &lt;test&gt tag */
public class Test extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.deleteIt();
  }
}

/* ====================================================================
### <test [options]>string</test>
###
###  Condition Options:
###	zero
###	positive
###	negative
###	match="pattern"
###	null	(stronger than the normal test in which false is nonblank)
### ===	numeric
### ===	length="n"
### ===	defined [pia|env|entity|agent|actor] name="..."
### ===	file [exists|writable|readable|directory|link] name="pathname"
###
###  Modifiers:
###	not
###	case (sensitive)
###	text
###
###  Other Options:
###	iftrue="..."	string to return if result is true->content
###	iffalse="..."	string to return if result is false
###
define_actor('test', 'content' => 'value',
	     'dscr' => "test VALUE (content); 
return null or IFFALSE if false, else '1' or IFTRUE. 
Tests: ZERO, POSITIVE, NEGATIVE, MATCH='pattern'.  
Modifiers: NOT, CASE (sensitive), TEXT, LINK, EXACT (match).");

sub test_handle {
    my ($self, $it, $ii) = @_;
    my $result = '';
    my $value = $it->attr('value');
    my ($match, $text);

    if ($it->attr('link')) {
	$text = $it->link_text unless defined $value;
    } elsif ($it->attr('text')) {
	$text = $it->content_text unless defined $value;
    } else {
	$text = $it->content_string unless defined $value;
    }

    my $test = remove_spaces($value);
    $test = @$test if ref($test);

    if ($it->attr('zero')) {
	$result = 1 if $text == 0;
    } elsif ($it->attr('positive')) {
	$result = 1 if $text > 0;
    } elsif ($it->attr('negative')) {
	$result = 1 if $text < 0;
    } elsif (($match = $it->attr('match'))) {
	$match = "^$match\$" if $it->attr('exact');
	eval {
	    ## in an eval block because an ill-formed match will croak.
	    if ($it->attr('case')) {
		$result = 1 if $text =~ /$match/;
	    } else {
		$result = 1 if $text =~ /$match/i;
	    }
	}
    } else {
	$result = 1 unless $text =~ /^\s*$/;
    }

    test_result($result, $it, $ii);
}

*/
