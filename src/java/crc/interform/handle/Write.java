////// Write.java:  Handler for <write>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;

/** Handler class for &lt;write&gt tag.
 *	&lt;write [file="name" | href="url"] [base="path"] [tagset="name"]
 *	[append] [copy [protect [markup]]] &gt; content &lt;/output&gt; */
public class Write extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.deleteIt();
  }
}

/* ====================================================================

### <write [file="name" | href="url"] [base="path"] [tagset="name"]
###	    [append] [copy [protect [markup]]] >content</output>

define_actor('write', 'content' => 'value', 
	     'dscr' => "Output CONTENT to FILE or HREF, with optional BASE 
path.  FILE may be looked up as an INTERFORM.  BASE directory is created if 
necessary.  Optionally APPEND or POST.  Optionally TRIM leading and trailing 
whitespace. Optionally end LINE.  Optionally COPY content to InterForm.");

sub write_handle {
    my ($self, $it, $ii) = @_;

    my $file = $it->attr('file');
    my $href = $it->attr('href');
    my $base = $it->attr('base');

    my $text = $it->attr('text');
    my $content = $text? $it->content_text : $it->content_string;

    if ($it->attr('trim')) {
	$content =~ s/^[\n\s]*//s;
	$content =~ s/[\n\s]*$//s; 
    }
    if ($it->attr('line')) {
	$content .= "\n" unless $content =~ /\n$/s;
    }
    if ($file && ! $href) {	# File
	my $append = $it->attr('append');
	my $dir = $it->attr('directory');

	if ($it->attr('interform')) {
	    $base = IF::Run::agent()->agent_if_root();
	}
	if ($file =~ /^~/) {
	    $file =~ s/^~//;
	    $base = $ENV{'HOME'};
	} elsif ($file =~ /^\//) {
	    $base = '';
	} elsif ($base eq '') {
	    $base = IF::Run::agent()->agent_directory;
	}
	$base =~ s:/$:: if $base;
	if ($base ne '' && ! -d $base) {
	    if (! mkdir($base, 0777)) {
		my $err = "InterForm error: can't create directory $base\n";
		print $err;
		$ii->replace_it($err);
		return;
	    }
	}

	my $fn = $base? "$base/$file" : $file;
	$fn =~ s://:/:g;
	$fn =~ s:/$::;

	if ($file eq '.') {
	    # nothing to do; just make sure the base directory exists.
	} elsif ($append) {
	    appendTo($fn, $content);
	} else {
	    writeTo($fn, $content);
	}
    } elsif ($href && ! $file) {	# Href (PUT or POST)
	my $post = $it->attr('post');

	## === write href unimplemented ===

    } elsif ($href) {
	my $err = "InterForm error: both HREF and FILE specified\n";
	print $err;
	$ii->replace_it($err);
	return;
    } else {
	my $err = "InterForm error: neither HREF nor FILE specified\n";
	print $err;
	$ii->replace_it($err);
	return;
    }

    if ($it->attr('copy')) {
	$ii->replace_it($it->content);
    } else {
	$ii->delete_it;
    }
}

*/
