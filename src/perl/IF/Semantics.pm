package IF::Semantics; ###### Semantic utilities for Actors.
###	$Id$

##############################################################################
 # The contents of this file are subject to the Ricoh Source Code Public
 # License Version 1.0 (the "License"); you may not use this file except in
 # compliance with the License.  You may obtain a copy of the License at
 # http://www.risource.org/RPL
 #
 # Software distributed under the License is distributed on an "AS IS" basis,
 # WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 # for the specific language governing rights and limitations under the
 # License.
 #
 # This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 # created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 # Rights Reserved.
 #
 # Contributor(s):
 #
############################################################################## 

###
###	This module contains the utilities that define the semantics 
###	(and to a lesser extent the syntax) of the ``standard'' set of
###	InterForm Interpretor actors.  They are all used _by_ Actors 
###	to access data _in_ Tokens.

### ===	It would be simpler all around if many of these were methods on 
###	IF::IT or DF::Tokens (which probably should be in IF).

require Exporter;
push(@ISA,Exporter);
@EXPORT = qw(is_list split_list is_one_of tagset
	     analyze remove_spaces list_items list_pairs
	     get_text get_list get_pairs get_hash
	     text_item lc_text_item string_item lc_string_item
	     link_item lc_link_item
	     prep_item_sub 
	     test_result list_result pair_result); # === can't get tags to work.

use URI::URL;

#############################################################################
###
### Predicates:
###

sub is_list {
    my $v = shift;

    ## Returns true if the argument is a list.
    ##	  The ARRAY test is probably obsolete now.

    return ref $v && (ref($v) eq 'ARRAY' || $v->is_list);
}


#############################################################################
###
### Strings:
###
###	These routines are used primarily for analyzing the values of 
###	attributes, or content consisting of a single string.
###

sub split_list {
    my ($in) = @_;

    ## If $in is a string, or a token whose content is a string,
    ##	  split it on whitespace

    $in = singleton_string($in);
    return unless defined $in;

    $in =~ s/\n/ /s;
    return \(split $in);
}

sub split_query {
    my ($in) = @_;

    ## If $in is a string, or a token whose content is a string,
    ##	  split it as a query string.

    $in = singleton_string($in);
    return unless defined $in;
    return [] unless $in =~ /\=/s;

    $in =~ s/^[\n\s]*//s;
    $in =~ s/[\n\s]*$//s;
    $in =~ s/\s*=\s*/=/g;

    my @out = ();
    $in = 'http://foo/bar?'.$in unless $in =~ /\?/;
    print "$in\n";
    my $u = url($in);
    my @tmp = $u->query_form;
    my ($a, $v);
    while (($a, $v) = splice(@tmp, 0, 2)) {
	push(@out, [$a, $v]);
    }
    return \@out;
}

sub singleton_string {
    my ($in) = @_;

    ## If $in is a string or a token whose content is a string,
    ##	 return the string; otherwise return undefined;

    return $in unless ref $in;

    if (ref $in ne 'ARRAY') {
	$in = $in->content;
    }
    return $in unless is_list($in);
    if (@$in == 1 && !ref($$in[0])) {
	return $$in[0];
    }
    return;
}
    
#############################################################################
###
### Tags:
###
###	These routines are used primarily for analyzing tags.
###

sub tagset {
    ## Construct a hash table that associates every tag in the arglist
    ##	 with true.  The arglist may contain references to arrays.

    my $tags = {};
    for (@_) {
	if (ref $_) {
	    for (@$_) { $$tags{$_} = 1; }
	} else {
	    $$tags{$_} = 1;
	}
    } 
    return $tags;
}

sub is_one_of {
    my ($t, $tags) = @_;

    ## Returns true if $t is one of the listed tags (or names).

    my $x;
    if (ref $tags) {
	for $x (@$tags) {
	    return 1 if $x eq $t;
	}
    } elsif ($tags) {
	my @tags = split $tags;
	for $x (@tags) {
	    return 1 if $x eq $t;
	}
    } else {
	return 1;
    } 
    return 0;
}


#############################################################################
###
### Content:
###
###	These routines are used mainly for analyzing content, although 
###	sometimes (see "analyze") parts of the content are passed in
###	attributes.
###

sub remove_spaces {
    my ($in) = @_;

    ## The result is an array that contains each item of $in with
    ##	  leading and trailing whitespace removed, and with items that
    ##	  consist only of whitespace deleted completely.

    my @out = ();
    $in = [] unless defined $in;
    $in = [$in] unless ref $in;
    $in = $in->content unless (ref($in) eq 'ARRAY');

    foreach $x (@$in) {
	if (! ref $x) {
	    $x =~ s/^[\n\s]*//s;
	    $x =~ s/[\n\s]*$//s;
	}
	push(@out, $x) unless ($x eq '');
    }
    return \@out;
}


sub analyze {
    my ($in, $tags, $flag) = @_;

    ## The result is a hash that associates each of the given tags with 
    ##	  that tag's content in the top level of array @$in.  Anything
    ##	  outside any of the tags is associated with '_', or the first
    ##	  empty tag if $flag is true.  Blanks outside tags are ignored.

    ##	  If applied to a token instead of an array, attributes will be
    ##	  used if they exist, and the token will be returned instead of
    ##	  constructing a new hash.

    my ($out, $x, @tmp, %tags);

    if (ref($in) eq 'IF::IT') {
	$out = $in;
	$in = $in->content;
    } else {
	$out = {};
	$in = [ $in ] unless ref($in);
    }

    print "Analzying [@$in]\n" if  $main::debugging>1;
    for $x (@$tags) {
	$tags{$x} = 1;
    }
    for $x (@$in) {
	if (ref $x) {
	    my $tag = $x->tag;
	    if (exists $tags{$tag}) {
		print "pushing <$tag...> to attributes\n" if $main::debugging>1;
		$out->{$tag} = $x->content;
	    } else {
		print "pushing <$tag...> to tmp\n" if $main::debugging>1;
		push(@tmp, $x);
	    }
	} else {
	    print "pushing '$x' to tmp\n" if $main::debugging>2;
	    push(@tmp, $x) unless $x =~ /^[\s\n]*$/s;
	}
    }
    if (@tmp) {
	if ($flag) {
	    for $x (@$tags) {
		if (! exists $out->{$x}) {
		    $out->{$x} = \@tmp;
		    return $out;
		}
	    }	    
	} else {
	    $out->{'_'} = \@tmp;
	}
    }
    return $out;
}

sub list_items {
    my ($in) = @_;

    ## $in is turned into an array of list items as follows:
    ##	  If it contains only text, it is split on whitespace.
    ##	  If it contains a single <ol> or <ul> element, each 
    ##	  list item is extracted and its <li> tag removed.
    ##	  Otherwise, the contents of <li> elements are extracted, but
    ##	  other tokens are used as-is.

    my ($x, @out);

    $in = $in->content_text if (ref $in && $in->is_text);
    if (! ref $in) {
	$x = $in;
	$x =~ s/\n/ /s;
	@out = split(' ', $x);
    } else {
	$in = remove_spaces($in);
	if (@$in == 1 && ref($in->[0]) 
	    && ($in->[0]->tag =~ /^[oud]l$/)) { # dl returns keys
	    $in = remove_spaces($in->[0]->content);
	}
#
#	if (@$in == 1 && ref($in->[0]) 
 #	    && ($in->[0]->tag =~ /^dl$/)) { # returns associative list
 #	    return list_pairs($in);
 #	}

	for $x (@$in) {
	    if (ref($x) && $x->tag =~ /^(li|dt)$/) {
		push(@out, $x->content_token);
	    } else {
		push(@out, $x);
	    }
	}
    }
    return \@out;
}


sub list_pairs {
    my ($in) = @_;

    ## $in is turned into an array of [key . value...] lists, as follows:
    ##	  If it contains only text, it is parsed as a query string.
    ##	  If it contains a single <dl> element, each <dt><dd> pair
    ##	  is a name-value pair; if a table, the name is the first <th>
    ##	  or <td> in the row, and the value is the rest of the row.
    ##	  Otherwise the result is like list_items except that each
    ##	  item is a singleton list.
    ##
    ## Note that if there are multiple values, as in a <table> or a <dl>
    ##	  with multiple <dd> items, the values are simply pushed onto
    ##	  the array with the key.

    my ($x, @out);

    $in = remove_spaces($in);

    $x = split_query($in);
    return $x if defined $x;

    if (@$in == 1 && ref($in->[0]) 
	&& is_one_of($in->[0]->tag, qw(dl ul dl table))) {
	$in = remove_spaces($in->[0]->content);
    }
    my $x;
				# this seems incomplete
				# ... added temporary hack for non li
    for $x ($in) {
	if (ref($x) && ref($x) ne 'ARRAY' && $x->tag =~ /^(li|dt|dd|tr|th)$/) {
	    push(@out, $x->content);
	} else {
	    push(@out, $x);
	}
    }
    return \@out;
}


#############################################################################
###
### Content or Attributes:
###

sub get_text {
    my ($it, $attr) = @_;

    ## Returns a string
    ##	  This is either the value of the $attr (if present),
    ##	  or the text extracted from the content.

    my $text = $it->attr($attr) if defined $attr;
    return (defined $text)? $text : $it->content_text;
}

sub get_list {
    my ($it, $attr) = @_;

    ## Returns a reference to an array of list items.
    ##	  These are either the value of the $attr (default 'list'),
    ##	  or the result of splitting the content.

    $attr = 'list' unless defined $attr;
    my $list = $it->attr($attr);
    return list_items((defined $list)? $list : $it);
}

sub get_pairs {
    my ($it, $attr) = @_;

    ## Returns a reference to an hash that associates names and values.
    ##	  These are either the value of the $attr if present,
    ##	  or the result of splitting the content.

    my $list = $it->attr($attr) if defined $attr;
    return list_pairs((defined $list)? $list : $it);
}

sub get_hash {
    my ($it, $attr) = @_;

    my $pairs = get_pairs($it, $attr);
    my %hash;
    if ($pairs) {
	for (@$pairs) {
	    $hash{$_->[0]} =  $_->[1];
	}
    }
    return \%hash;
}

#############################################################################
###
### List Items:
###
###	The *_item operations all operate on the first element if passed
###	an array; this is useful for extracting the key of an item returned
###	by list_pairs
###
### ===	Probably need second argument for the list behavior.

sub text_item {
    my ($it) = @_;

    ## Returns its argument as a string without markup.
    ##	 if the argument is a list, selects the first item

    $it = $it->[0] if is_list($it);
    $it = $it->content_text if ref $it;
    $it;
}

sub lc_text_item {
    my ($it) = @_;

    ## Returns its argument as a lowercase string without markup.
    ##	 if the argument is a list, selects the first item

    $it = $it->[0] if is_list($it);
    $it = $it->content_text if ref $it;
    lc $it;
}

sub link_item {
    my ($it) = @_;

    ## Returns its argument as a string without markup.
    ##	 if the argument is a list, selects the first item

    $it = $it->[0] if is_list($it);
    $it = $it->link_text if ref $it;
    $it;
}

sub lc_link_item {
    my ($it) = @_;

    ## Returns its argument as a lowercase string without markup.
    ##	 if the argument is a list, selects the first item

    $it = $it->[0] if is_list($it);
    $it = $it->link_text if ref $it;
    lc $it;
}

sub string_item {
    my ($it) = @_;

    ## Returns its argument as a string, including markup.
    ##	 if the argument is a list, selects the first item

    $it = $it->[0] if is_list($it);
    $it = $it->content_string if ref $it;
    $it;
}

sub lc_string_item {
    my ($it) = @_;

    ## Returns its argument as a lowercase string, including markup.
    ##	 if the argument is a list, selects the first item

    $it = $it->[0] if is_list($it);
    $it = $it->content_string if ref $it;
    lc $it;
}

#############################################################################
###
### Processing items:
###

sub prep_item_sub {
    my ($it) = @_;

    ## Returns a reference to the appropriate item-processing routine
    ##	 given the values of CASE, TEXT, and NUMERIC attributes of $it.
    ##   LINK is like TEXT except that text outside links is discarded.
    ##	 This lets you put insignificant words outside the link, e.g.
    ##	 the <a href="foo">Foo Fables</a>.

    if ($it->attr('numeric')) {
	return \&text_item;
    } elsif ($it->attr('link')) {
	## Check 'link' first so that 'LINK TEXT' works as expected.
	if ($it->attr('case')) {
	    return \&link_item;
	} else {
	    return \&lc_link_item;
	}
    } elsif ($it->attr('text')) {
	if ($it->attr('case')) {
	    return \&text_item;
	} else {
	    return \&lc_text_item;
	}
    } else {
	if ($it->attr('case')) {
	    return \&string_item;
	} else {
	    return \&lc_string_item;
	}
    }
}

#############################################################################
###
### Returning Results:
###

sub test_result {
    my ($result, $it, $ii) = @_;

    ## Return a boolean result from a test.
    ##	 handles NOT, IFTRUE, IFFALSE attributes.

    $result = ! $result if $it->attr('not');
    $result = $result ? 1 : '';

    if ($result) {
	my $res = $it->attr('iftrue');
	$result = $res if defined $res;
    } else {
	my $res = $it->attr('iffalse');
	$result = $res if defined $res;
    }

    if ($result ne '') {
	$ii->replace_it($result);
    } else {
	$ii->delete_it;
    } 
}

sub list_result {
    my ($in, $it, $ii) = @_;

    ## Return a list or array $in.
    ## 	 === incomplete -- needs to look at $it ===

    if (ref $in eq 'ARRAY') {
	$in = join(' ', @$in);
    }
    $ii->replace_it($in);
}

sub pair_result {
    my ($in, $it, $ii) = @_;

    $ii->replace_it($in);
}

#############################################################################
1;
