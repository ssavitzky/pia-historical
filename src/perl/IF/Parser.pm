package IF::Parser;

#### $Id$

### Copied from HTML::Parser by Gisle Aas <aas@sn.no>
### Modified to fit into the PIA code hierarchy by 
###	Stephen Savitzky <steve@crc.ricoh.com>

use strict;

#use HTML::Entities ();
use vars qw($VERSION);
$VERSION = sprintf("%d.%02d", q$Revision$ =~ /(\d+)\.(\d+)/);


sub new
{
    my $class = shift;
    my $self = bless { '_buf' => '' }, $class;
    $self;
}

# How does Netscape do it: It parse <xmp> in the depreceated 'literal'
# mode, i.e. no tags are recognized until a </xmp> is found.
# 
# <listing> is parsed like <pre>, i.e. tags are recognized.  <listing>
# are presentend in smaller font than <pre>
#
# Netscape does not parse this comment correctly (it terminates the comment
# too early):
#
#    <! -- comment -- --> more comment -->
#
# Netscape does not allow space after the initial "<" in the start tag.
# Like this "<a href='gisle'>"
#
# Netscape ignore '<!--' and '-->' within the <SCRIPT> tag.  This is used
# as a trick to make non-script-aware browsers ignore the scripts.


sub parse
{
    my $self = shift;
    my $buf = \ $self->{'_buf'};
    unless (defined $_[0]) {
	# signals EOF (assume rest is plain text)
	$self->text($$buf) if length $$buf;
	$$buf = '';
	return $self;
    }
    $$buf .= $_[0];

    # Parse html text in $$buf.  The strategy is to remove complete
    # tokens from the beginning of $$buf until we can't deside whether
    # it is a token or not, or the $$buf is empty.
    while (1) {  # the loop will end by returning when text is parsed
	# First we try to pull off any plain text (anything before a "<" char)
	if ($$buf =~ s|^([^<]+)||) {
	    unless (length $$buf) {
		my $text = $1;
		# At the end of the buffer, we should not parse white space
		# but leave it for parsing on the next round.
		if ($text =~ s|(\s+)$||) {
		    $$buf = $1;
                # Same treatment for chopped up entites.
		} elsif ($text =~ s/(&(?:(?:\#\d*)?|[-.\w]*))$//) {
		    $$buf = $1;
		};
		$self->text($text);
		return $self;
	    } else {
		$self->text($1);
	    }
	# Then, markup declarations (usually either <!DOCTYPE...> or a comment)
	} elsif ($$buf =~ s|^(<!)||) {
	    my $eaten = $1;
	    my $text = '';
	    # Eat text and beginning of comment
	    while ($$buf =~ s|^(([^>]*?)--)||) {
		$eaten .= $1;
		$text .= $2;
		# Look for end of comment
		if ($$buf =~ s|^((.*?)--)||s) {
		    $eaten .= $1;
		    $self->comment($2);
		} else {
		    # Need more data to get all comment text.  This might
		    # result in the comment callback being called more than
		    # once for the several comment data.
		    $$buf = $eaten . $$buf;
		    return $self;
		}
	    }
	    # Can we finish the tag
	    if ($$buf =~ s|^([^>]*)>||) {
		$text .= $1;
		$self->declaration($text) if $text =~ /\S/;
	    } else {
		$$buf = $eaten . $$buf;  # must start with it all next time
		return $self;
	    }
        # Should we look for 'processing instructions' <? ...> ??
	#} elsif ($$buf =~ s|<\?||) {
	    # ...
	# Then, look for a end tag
	} elsif ($$buf =~ s|^</||) {
	    # end tag
	    if ($$buf =~ s|^\s*([a-z][a-z0-9\.\-]*)\s*>||i) {
		$self->end(lc($1));
	    } elsif ($$buf =~ m|^\s*[a-z]*[a-z0-9\.\-]*\s*$|i) {
		$$buf = "</" . $$buf;  # need more data to be sure
		return $self;
	    } else {
		# it is plain text after all
		$self->text($$buf);
		$$buf = "";
	    }
	# Then, finally we look for a start tag
	} elsif ($$buf =~ s|^<||) {
	    # start tag
	    my $eaten = '<';

	    # This first thing we must find is a tag name.  RFC1866 says:
	    #   A name consists of a letter followed by letters,
	    #   digits, periods, or hyphens. The length of a name is
	    #   limited to 72 characters by the `NAMELEN' parameter in
	    #   the SGML declaration for HTML, 9.5, "SGML Declaration
	    #   for HTML".  In a start-tag, the element name must
	    #   immediately follow the tag open delimiter `<'.
	    if ($$buf =~ s|^(([a-zA-Z][a-zA-Z0-9\.\-]*)\s*)||) {
		$eaten .= $1;
		my $tag = lc $2;
		my @attr;

		# Then we would like to find some attributes
		while ($$buf =~ s|^(([a-zA-Z][a-zA-Z0-9\.\-]*)\s*)||) {
		    $eaten .= $1;
		    my $attr = lc $2;
		    my $val;
		    # The attribute might take an optional value (first we
		    # check for an unquoted value)
		    if ($$buf =~ s|(^=\s*([^\"\'>\s][^>\s]*)\s*)||) { #"
			$eaten .= $1;
			$val = $2;
			$self->expand_entities($val) unless $self->quoting;
		    # or quoted by " "or ' '
		    } elsif ($$buf =~ s|(^=\s*([\"\'])(.*?)\2\s*)||s) { #"
			$eaten .= $1;
			$val = $3;
			$self->expand_entities($val) unless $self->quoting;
                    # truncated just after the '=' or inside the attribute
		    } elsif ($$buf =~ m|^(=\s*)$| or
			     $$buf =~ m|^(=\s*[\"\'].*)|s) { #"
			$$buf = "$eaten$1";
			return $self;
		    } else {
			# assume attribute with implicit value
			$val = $attr;
		    }
		    push(@attr, $attr, $val);
		    #$attr{$attr}=$val
		}

		# At the end there should be a closing ">"
		if ($$buf =~ s|^>||) {
		    $self->start($tag, \@attr);
		} elsif (length $$buf) {
		    # Not a conforming start tag, regard it as normal text
		    $self->text($eaten);
		} else {
		    $$buf = $eaten;  # need more data to know
		    return $self;
		}

	    } elsif (length $$buf) {
		$self->text($eaten);
	    } else {
		$$buf = $eaten . $$buf;  # need more data to parse
		return $self;
	    }

	} elsif (length $$buf) {
	    die; # This should never happen
	} else {
	    # The buffer is empty now
	    return $self;
	}
    }
    $self;
}

sub parse_file
{
    my($self, $file) = @_;
    no strict 'refs';  # so that a symbol ref as $file works
    local(*F);
    unless (ref($file) || $file =~ /^\*[\w:]+$/) {
	# Assume $file is a filename
	open(F, $file) || die "Can't open $file: $!";
	$file = \*F;
    }
    my $chunk = '';
    while(read($file, $chunk, 2048)) {
	$self->parse($chunk);
    }
    close($file);
    $self->parse(undef); #EOF
}

sub text
{
    # my($self, $text) = @_;
}

sub declaration
{
    # my($self, $decl) = @_;
}

sub comment
{
    # my($self, $comment) = @_;
}

sub start
{
    my($self, $tag, $attr) = @_;  # $attr is reference to a HASH
}

sub end
{
    my($self, $tag) = @_;
}

1;
