////// Parser.java:  InterForm SGML Parser
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;
import crc.interform.Input;
import crc.interform.Token;
import crc.interform.Tokens;
import java.io.InputStream;
import java.io.IOException;
import java.util.BitSet;

/** Parser (actually more of a <em>scanner</em>) for SGML.  It is not as
 *	general as it should be; it is basically a slightly extended HTML 
 *	parser.  Some (perhaps many) SGML constructs will not be handled
 *	correctly, or in some cases at all.<p>
 *
 *	There are many differences from the old Perl parser.  The main
 *	one is that it runs in ``pull mode,'' returning a single token
 *	each time it is called.  Start tags are returned with an
 *	<code>incomplete</code> field of 1, and end tags with -1.
 *	Empty tags are flagged as start tags at this point.<p>
 *
 *	Entity references are returned as separate tokens (with tag
 *	"&"); attributes that contain entity references have a Tokens
 *	list as their value.  This makes expansion much faster, since
 *	it is no longer necessary to do any string parsing in the
 *	interpretor.<p>
 *
 *	Attributes with no following <code>=<em>value</em></code> are 
 *	given an empty list as their value; this makes it possible to 
 *	reliably distinguish attributes with no value from those with
 *	a null string value.<p>
 *
 *	The Parser has no access to the Interpretor's current syntax
 *	tables, so it's up to the Interpretor to inform it by setting
 *	the appropriate control variables, which are ignored in other
 *	types of input. <p>
 *
 *	@see http:/PIA/src/lib/perl/IFParser.pm
 *
 * NOTE: The Parser is currently unable to correctly handle SGML
 *	comments that contain the string "-->" internally.  This is
 *	the only construct that requires more than a single character
 *	of lookahead. <p>
 */
class Parser extends Input {

  /************************************************************************
  ** Variables:
  ************************************************************************/

  /** The input stream.  It is possible to use an ordinary InputStream
   *	at this point; all buffering is done internally for efficiency. */
  InputStream in = null;

  /** Holds characters that have been ``eaten'' from the stream. */
  StringBuffer buf = new StringBuffer(256);

  /** Holds an identifier ``eaten'' from the stream. */
  String ident;

  /** Holds the character that terminated the current token, or -1 if
   *    the token was terminated by end-of-file.  It will be prepended to 
   *	the <em>next</em> token if non-null. */
  int last;

  /** Holds the next item, almost invariably an entity reference. */
  SGML next;

  /** If true, nextInput will wait for more input.  If false, it will
   *	return null if the buffer does not contain a complete token.
   */
  public boolean pull = false;

  /** If true, there is no more input. */
  public boolean done = false;

  /* === We are NOT using a StreamTokenizer at this point.  The reason
   *	is that we need all characters to be significant and
   *	``ordinary'' outside of tags, so the StreamTokenizer would
   *	simply add an extra layer of overhead for very little effect.  */

  /************************************************************************
  ** Input (pull) Interface:
  ************************************************************************/

  /** Return the next token in the input stream.   Waits, if necessary,
   *	for more input to appear.
   */
  public SGML nextInput() {
    SGML it;

    if (next != null) {		// There's one hanging fire, so return it.
      it = next;
      next = null;
      return it;
    } else try {		// We'll have to do some I/O for this one.

      // Get a character if the last one was already eaten (last == 0).
      //	If we hit EOF, return.

      if (last == 0) last = in.read();
      if (last < 0) return null;

      if (endString != null) {
	it = getLiteral();
      } else {
	it = getToken();
      }
      
      return it;

      // Protect this section against IO exceptions and other road hazards.
    } catch (Exception e) {}

    return null; // === for now
  }

  /** Return true if there is no more input. */
  public boolean endInput() {
    return last < 0;
  }

  /** Return true if there is nothing left in the buffer. 
   * 	If pull==false it is possible to have bufferEmpty() && !endInput().
   */
  public boolean bufferEmpty() {
    return buf.length() == 0;
  }

  /************************************************************************
  ** Constructors:
  ************************************************************************/
  
  public Parser(Input previous) {
    super(previous);
    initializeTables();
  }

  public Parser(InputStream in, Input previous) {
    super(previous);
    this.in = in;
    initializeTables();
  }

  /************************************************************************
  ** Syntax tables:
  ************************************************************************/

  static BitSet isIdent;
  static BitSet isSpace;
  
  void initializeTables() {

  }

  /************************************************************************
  ** Low-level Recognizers:
  ************************************************************************/

  /** Starting at <code>last</code> (or the next available character
   *	if <code>last</code> is zero, append characters to
   *	<code>buf</code> until the next non-ordinary character (&amp; or
   *	&lt;) or end-of-buffer is seen.  The terminating character ends
   *	up in <code>last</code>.
   *
   *	@return true if at least one character is eaten. */
  boolean eatText() throws IOException {
    if (last == 0) last = in.read();
    if (last < 0) return false;
    if (last == '&' || last == '<') return true;
    do {
      buf.append((char)last);
      last = in.read();
    } while (last >= 0 && last != '&' && last != '<');
    return last >= 0;    
  }

  /** Starting at the next available character, append characters to a
   *	String until a character that does not belong in an identifier
   *	is found.  Identifiers, in SGML-land, may include letters,
   *	digits, "-", and ".".  The terminating character ends up in
   *	<code>last</code>, and the string in <code>ident</code>.
   *
   *	@return true if at least one character is eaten.  */
  boolean eatIdent() throws IOException {
    last = in.read();
    String id = "";
    if (! isIdent.get(last)) return false;
    do {
      id += (char)last;
      last = in.read();
    } while (isIdent.get(last));
    ident = id;
    return true;    
  }
    
  /** Starting at the next available character, append characters to
   *	<code>buf</code> until <code>aCharacter</code> (typically a
   *	quote) is seen.
   *
   *	@return false if end-of-file is reached before a match. */
  boolean eatUntil(int aCharacter) throws IOException {
    last = in.read();
    if (last < 0) return false;
    if (last == aCharacter) return true;
    do {
      buf.append((char)last);
      last = in.read();
    } while (last >= 0 && last != aCharacter);
    return last >= 0;    
  }

  /** Starting at the next available character, append characters to
   *	<code>buf</code> until <code>aString</code> (typically an end
   *	tag) is matched.
   *
   *	@return false if end-of-file is reached before a match. */
  boolean eatUntil(String aString) throws IOException {

    return false;
  }

  /************************************************************************
  ** SGML Recognizers:
  ************************************************************************/

  /** Pull an entity off the input stream and return it in <code>next</code>.
   *	Assume that <code>last</code> contains an ampersand.  If the next
   *	available character does not belong in an identifier, appends the
   *	ampersand to <code>buf</code>.
   *	@return false if the next available character does not belong in
   *	an entity name.
   */
  boolean getEntity() throws IOException {
    if (!eatIdent()) {
      buf.append("&"); 
      buf.append((char)last);
      return false;
    }
    next = new Token("&", ident, ";");
    if (last == ';') last = 0;
    return true;
  }

  /** Scan <code>aBuffer</code> (typically <code>buf</code>) for 
   *	entity references and return either a Text or Tokens object.
   *
   *	@return false if the next available character does not belong in
   *	an identifier. */
  SGML scanForEntities(StringBuffer aBuffer) {

    return null;
  }

  /** Get a literal, i.e. everything up to <code>endString</code>.
   *	Clear endString and ignoreMarkup when the end string is seen.
   *	If ignoreMarkup is false, entities will be recognized.
   */
  SGML getLiteral() {

    // === 

    return null;
  }

  /** Get a tag starting with <code>last</code> and return it in
   *	<code>next</code>.  If what follows is not, in fact, a valid
   *	tag, it returns false and leaves the bad characters appended
   *	to <code>buf</code>.  getTag is only called from getText. <p>
   *
   *	One could argue that it should allow space after the &lt;. */
  boolean getTag() {

    if (eatIdent()) {		// <tag...	start tag

      // === for now assume we have a start tag
    } else if (last == '/') {	// </...	end tag
      eatIdent();
      next = new Token(ident);
      next.incomplete = -1;
    } else if (last == '!') {	// <!...	comment or declaration

    } else if (last == '>') {	// <>		empty start tag
      next = new Token(ident);
      next.incomplete = 1;
    } else {			// not a tag.
      return false;
    }
    return true;
  }

  /** Get text starting with <code>last</code>.  If the text is
   *	terminated by an entity or tag, the entity or tag ends up in
   *	<code>next</code>, and the character that terminated
   *	<em>it</em> is left in <code>last</code>.  */
  SGML getText() {

    while (eatText()) {
      if ((last == '&' && getEntity()) ||
	  (last == '<' && getTag()) ||
	  (last < 0)) break;
    }
    return (buf.length() > 0)? new Text(buf) : null;
  }

  /** Get the SGML token starting with <code>last</code>.  If the
   *	token consists of text, the entity or tag that terminates it
   *	ends up in <code>next</code>, and the character that
   *	terminated <em>it</em> (in the case of an entity with no
   *	semicolon) is left in <code>last</code>.  This is necessary
   *	because anything that is <em>not</em> a complete tag or entity
   *	has to be part of the text. <p>
   *
   *	It would not simplify the parser to return text in chunks,
   *	breaking it at each &amp; or &lt;, because we'd only have to
   *	run the full test next time around and we'd have to save more
   *	state in order to do it.*/
  SGML getToken() {
    buf = new StringBuffer(256);

    SGML it = getText();	// Try to get some text.
    if (it == null) {		// If that failed,
      it = next;		// 	a tag or entity must be in next
      next = null;		// 	so clear next for next time.
    }

    return it;
  }

/* ========================================================================

### The PERL version can be found in /PIA/src/lib/perl/IF/Parser.pm

sub parse {
    my $self = shift;

    ## parse ($text)
    ##	  Append $text to the buffer (which contains whatever couldn't 
    ##	  be parsed from the previous chunk) and scan off as many tokens
    ##	  as possible.

    my $interp = exists $self->{'_interp'}? $self->interp : $self;
    my $buf = \ $self->{'_buf'};
    unless (defined $_[0]) {
	# signals EOF (assume rest is plain text)
	$interp->resolve($$buf) if length $$buf;
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
		$interp->resolve($text);
		return $self;
	    } else {
		$interp->resolve($1);
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
		$interp->end_it(lc($1));
	    } elsif ($$buf =~ s|^\s*>||i) {
		$interp->end_it('', 1);	# Empty end tag
	    } elsif ($$buf =~ m|^\s*[a-z]*[a-z0-9\.\-]*\s*$|i) {
		$$buf = "</" . $$buf;  # need more data to be sure
		return $self;
	    } else {
		# it is plain text after all
		$interp->resolve($$buf);
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
		    # or quoted by " "or ' '
		    } elsif ($$buf =~ s|(^=\s*([\"\'])(.*?)\2\s*)||s) { #"
			$eaten .= $1;
			$val = $3;
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
		    $interp->start_tag($tag, \@attr);
		} elsif (length $$buf) {
		    # Not a conforming start tag, regard it as normal text
		    $interp->resolve($eaten);
		} else {
		    $$buf = $eaten;  # need more data to know
		    return $self;
		}

	    } elsif (length $$buf) {
		$interp->resolve($eaten);
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

============================================================= */
}
