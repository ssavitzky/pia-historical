////// Add_markup.java:  Handler for <add-markup>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;

/** Handler class for &lt;add-markup&gt tag */
public class Add_markup extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.deleteIt();
  }
}

/* ====================================================================
### <add-markup>text</add-markup>

define_actor('add-markup', 
	     'dscr' => "convert common text conventions to markup");

sub add_markup_handle {
    my ($self, $it, $ii) = @_;

    my $content = $it->content;
    my $uc = $it->attr('uc') || 'strong'; # What to do with uppercase
    my $text;
    my @result;

    foreach $text (@$content) {
	if (ref $text) {
	    push(@result, $text); # already marked up
	} else {
	    ## Uppercased words.
	    while ($text =~ /([A-Z][A-Z]+)/ ) {
		my $x = "<$uc>" . lc $1 . "</$uc>";
		$text =~ s/$1/$x/;
	    }

	    ## Words surrounded by _ or *

	    ## Paragraph breaks

	    ## Line breaks

	    ## Horizontal rules

	    push(@result, $text);
	}
    }

    $ii->replace_it(\@result);
}
====================================================================== */
