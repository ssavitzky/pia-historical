////// Write_file.java:  Handler for <write.file>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;

import java.io.File;

/* Syntax:
 *	<write.file file="name" [interform] [append]
 *	       [base="path"] [trim] [line]
 *	       [copy [protect [markup]]] >content</write.file>
 * Dscr:
 *	Output CONTENT to FILE, with optional BASE path.  FILE
 *	may be looked up as an INTERFORM.  BASE directory is created
 *	if necessary.  Optionally APPEND.  Optionally TRIM
 *	leading and trailing whitespace. Optionally end LINE.
 *	Optionally COPY content to InterForm.
 */

/** Handler class for &lt;write.file&gt tag. */
public class Write_file extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getFileName(it, ii, true);
    if (name == null) {
      ii.error(ia, "Must have non-null name or file attribute.");
      ii.deleteIt();
      return;
    }

    String content = (it.hasAttr("text")) ? it.contentText().toString()
      					  : it.contentString();

    if (it.hasAttr("trim")) { content = content.trim(); }
    if (it.hasAttr("line") && ! content.endsWith(lineSep))
      content += lineSep;

    String errmsg = null;

    /* Make sure all directories in the path exist. */

    File file = new File(name);
    File parent = (file.getParent()!=null)? new File(file.getParent()) : null;

    try {
      if (parent != null && ! parent.exists()) {
	if (! parent.mkdirs()) errmsg = "Cannot make parent directory";
      }
      if (it.hasAttr("directory")) {
	file.mkdirs();
	if (! file.exists() || ! file.isDirectory()) {
	  errmsg = "Could not create directory " + name;
	}
      } else if (it.hasAttr("append")) {
	crc.util.Utilities.writeTo(name, content);
      } else {
	crc.util.Utilities.appendTo(name, content);
      }
    } catch (Exception e) {
      errmsg = "Write failed on " + name;
    }

    if (errmsg != null) {
      ii.error(ia, errmsg);
      ii.replaceIt(errmsg);
      return;
    }

    if (it.hasAttr("copy")) {
      // === [copy [protect [markup]]] unimplemented: replaceIt is protect.
      ii.replaceIt(it.content());
    } else {
      ii.deleteIt();
    }
  }

  private static String lineSep  = System.getProperty("line.separator");

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
