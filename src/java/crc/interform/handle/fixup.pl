#!/usr/bin/perl -i.bak
# $Id$
# Copyright 1997 Ricoh Silicon Valley
#	fixup.pl handler_file...
#
# This program hacks Java actor-handler files to convert comments 
# of the form:
#
#	/* Syntax:
#	 *	syntax description
#	 * Dscr:
#	 *	text description
#	 */
#
# to the form:
#
#	public String syntax() { return syntax; }
#	static String syntax=
#		"syntax description";
#	public String dscr() { return dscr; }
#	static String dscr=
#		"text description";
#
# and stuff them inside the class definition.  The javaDoc comment is also
# suitably modified.
#

$jblock = '';			# Code block
$cblock = '';			# Comment block
$insideDscr = 0;
$firstLine = 0;
$syntax = " public String syntax() { return syntaxStr; }
  static String syntaxStr=";
$dscr = $syntax;
$dscr =~ s/syntax/dscr/g;
$note = $syntax;
$note =~ s/syntax/note/g;

while (<>) {

    if (/\*\// && $insideDscr) {
	$jblock .= '"";'."\n";
	$insideDscr = '';
    }
    if (/syntax:/i .. /\*\// ) {
	s/\*\// \*/;
	s/\/\*/ \*/;
	s/.\*//;

	$cblock .= $_;

	if (/\:\s*$/) {
	    if ($insideDscr) { $jblock .= '"";'."\n"; }
	    $insideDscr = 0;
	    $firstLine = 1;
	}
	s/\"/\\\"/g;
	s/syntax:/$syntax/i; # "
	s/dscr:/$dscr/i; # "
	s/note:/$note/i; # "
	if (/public/) {
	    $firstLine = 1;
	    $insideDscr = 1;
	} elsif ($firstLine) {
	    s/(\s+)/    \"/;
	    $firstLine = 0;
	    s'$'\\n" +';
	} elsif ($insideDscr) {
	    s'$'\\n" +';
	    s'^[\s]*'    "';	    
	}
	$jblock .= $_;
    } else {
	if ($cblock && /\/\*\*/ .. /\*\//) {
	    $cblock = "\n" . $cblock;
	    $cblock =~ s/\&/&amp;/gs;
	    $cblock =~ s/\</&lt;/gs;
	    $cblock =~ s/\>/&gt;/gs;
	    $cblock =~ s/\n*$//s;
	    $cblock =~ s/(\w+):/<dt>$1:<dd>/g;
	    $cblock =~ s/\n/\n */g;
	    s/\*\//\n * <dl>$cblock <\/dl>\n \*\//;
	    $cblock = '';
	}
	print;
    }

    if (/public class/) {
	print $jblock;
	$jblock = '';
	$cblock = '';
    }
    if (eof) {			# Reset per-file items
	$jblock = '';			# Code block
	$cblock = '';			# Comment block
	$insideDscr = 0;
	$firstLine = 0;
    }
}
