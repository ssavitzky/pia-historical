////// Read_file.java:  Handler for <read.file>
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
 *	<read.file file="name" [interform]
 *            [info|head|directory [links] [tag=tag] [all|match="regexp"]] 
 *	      [base="path"] [process [tagset="name"]] >
 * Dscr:
 *	Input from FILE, with optional BASE path.  FILE may be
 *	looked up as an INTERFORM.  Optionally read only INFO or HEAD.
 *	For DIRECTORY, read names or LINKS, and return TAG or ul.
 *	DIRECTORY can read ALL names or those that MATCH; default is
 *	all but backups.  Optionally PROCESS with optional TAGSET.
 */

/** Handler class for &lt;read-file&gt tag */
public class Read_file extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "file", Util.getString(it, "name", null));
    if (name == null || "".equals(name)) {
      ii.error(ia, "file or name attribute required");
      return;
    }
    SGML result = null;

    ii.unimplemented(ia); // === really not clear how to do read.file!
  }
}

/* ====================================================================
### <read [file="name" | href="url"] [base="path"] [tagset="name"] [resolve]>

define_actor('read', 'empty' => 1, 
	     'dscr' => "Input from FILE or HREF, with optional BASE path.
FILE may be looked up as an INTERFORM.   Optionally read only INFO or HEAD.
For DIRECTORY, read names or LINKS, and return TAG or ul.  DIRECTORY can read 
ALL names or those that MATCH; default is all but backups.
Optionally PROCESS with optional TAGSET.  Optionally RESOLVE in pia.");

### === not clear what to do with directories.  LINKS? FILES? ===
### === HEAD? IF-LATER-THAN="date"? 

sub read_handle {
    my ($self, $it, $ii) = @_;

    my $file = $it->attr('file');
    my $href = $it->attr('href');
    my $base = $it->attr('base');
    my $info = $it->attr('info');
    my $head = $it->attr('head');
    my $dir  = $it->attr('directory');
    my $tag  = $it->attr('tag');
    my $f;

    my $content;

    if ($file && ! $href) {		# File
	my $fn = file_lookup($self, $it, $ii);

	## Check to see if the file exists.

	my $exists = -e $fn;
	my $isdir  = -d $fn;

	if (! $exists) {
	    ## Requested file doesn't exist.  Return null.
	    $content = '';
	} elsif ($dir && ! $isdir) {
	    ## Requested a directory, but it isn't.  Return null.
	    $content = '';
	} elsif ($info) {
	    my $w = -w $fn;
	    my $x = -x $fn;
	    my $r = -r $fn;

	    ## === use stat stuff if ALL ===
	    my ($dev,$ino,$mode,$nlink,$uid,$gid,$rdev,$size,
		$atime,$mtime,$ctime,$blksize,$blocks) = stat($fn);

	    if ($info =~ /^d/i)    { $content = $isdir? 'd' : ''; }
	    elsif ($info =~ /^r/i) { $content = $r? 'r' : ''; }
	    elsif ($info =~ /^w/i) { $content = $w? 'w' : ''; }
	    elsif ($info =~ /^x/i) { $content = $x? 'x' : ''; }
	    elsif ($info =~ /^p/i) { $content = $fn; }    	# path
	    elsif ($info =~ /^m/i) { $content = $mtime; } 	# modified
	    elsif ($info =~ /^s/i) { $content = $size; }  	# size
	    else {
		$content = $isdir? 'd' : '-';
		$content .= $r? 'r' : '-';
		$content .= $w? 'w' : '-';
		$content .= $x? 'x' : '-';
		$content .= " $size";
		$content .= "	$fn";
	    }
	} elsif ($dir || $isdir) {
	    my @names;
	    if (opendir(DIR, $fn)) {
		@names = readdir(DIR);
		closedir(DIR);
	    }
	    my @names = sort @names;
	    if (! $it->attr('all')) {
		my @tmp = @names;
		@names = ();
		my $match = $it->attr('match');
		$match = '[^~]$' unless defined $match; #'
		for $f (@tmp) {
		    if ($f ne '.' && $f =~ /$match/) {
			push (@names, $f);
		    }
		}
	    }
	    my $itag = 'li';
	    $itag = 'dt' if $tag eq 'dl';
	    
	    if ($it->attr('links')) {
		$tag = 'ul' unless $tag;
		$content = IF::IT->new($tag);
		for $f (@names) {
		    my $entry = IF::IT->new('a', href=>"file:$fn/$f", $f);
		    $content->push(IF::IT->new($itag, $entry));
		}
	    } elsif ($tag) {
		$content = IF::IT->new($tag);
		for $f (@names) {
		    $content->push(IF::IT->new($itag, $f));
		}
	    } else {
		$content = join(' ', @names);
	    } 
	} elsif ($it->attr('process')) {
	    ## Really just want to push the input stream.
	    ## Requires an input stack that can handle streams.
	    print "processing $fn\n" unless $main::quiet;
	    $content = IF::Run::parse_html_file($fn);
#	    $content = IF::Run::run_file($fn, $ii);
	} else {
	    $content = readFrom($fn);
	}
    } elsif ($href && ! $file) { 	# Href

	## === read href unimplemented ===

    } elsif ($href) {
	print "InterForm error: both HREF and FILE specified\n";
    } else {
	print "InterForm error: neither HREF nor FILE specified\n";
    }

    if ($it->attr('process')) {
	$ii->push_into($content);
    } else {
	$ii->replace_it($content);
    }
}
*/
