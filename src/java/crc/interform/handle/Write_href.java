////// Write_href.java:  Handler for <write.href>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;


/** Handler class for &lt;write.href&gt tag. 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;write.href href="url" [post] [base="path"] [trim] [line]
 *	       [copy [protect [markup]]] &gt;content&lt;/write.href&gt;
 * <dt>Dscr:<dd>
 *	Output CONTENT to HREF, with optional BASE path. 
 *	Optionally POST.  Optionally TRIM
 *	leading and trailing whitespace. Optionally end LINE.
 *	Optionally COPY content to InterForm.
 *  </dl>
 */
public class Write_href extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<write.href href=\"url\" [post] [base=\"path\"] [trim] [line]\n" +
    "[copy [protect [markup]]] >content</write.href>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Output CONTENT to HREF, with optional BASE path. \n" +
    "Optionally POST.  Optionally TRIM\n" +
    "leading and trailing whitespace. Optionally end LINE.\n" +
    "Optionally COPY content to InterForm.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String url = it.attrString("href");
    if (url == null || "".equals(url)) url = it.attrString("name");
    if (url == null || "".equals(url)) {
      ii.error(ia, "must have non-null name or href attribute");
      return;
    }

    ii.unimplemented(ia);

    if (it.hasAttr("copy")) {
      ii.replaceIt(it.content());
    } else {
      ii.deleteIt();
    }
  }
}

/* ====================================================================
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

}

*/
