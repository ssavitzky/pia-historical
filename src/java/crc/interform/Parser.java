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
 * Nomenclature: Methods with names starting in <code>eat</code>
 *	attempt to consume a lexical token from the input stream, and
 *	return <code>true</code> if they are successful.  If they
 *	fail, they leave the input stream unchanged.  Methods with
 *	names starting in <code>get</code> attempt to consume a more
 *	complex construct, and leave their rejected input in
 *	<code>buf</code> if they fail. <p>
 *
 * NOTE: The Parser is currently unable to correctly handle SGML
 *	comments that contain the string "-->" internally.  This is
 *	the only construct that requires more than a single character
 *	of lookahead. <p>
 *
 * NOTE: We are NOT using a StreamTokenizer at this point.  The reason
 *	is that we need all characters to be significant and
 *	``ordinary'' outside of tags, so the StreamTokenizer would
 *	simply add an extra layer of overhead for very little effect. <p> */
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

  /** Holds the next item, usually either an entity reference or a tag. */
  SGML next;

  /** If true, nextInput will wait for more input.  If false, it will
   *	return null if the buffer does not contain a complete token.
   */
  public boolean pull = false;

  /** If true, there is no more input. */
  public boolean done = false;

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

  /** True for every character that is part of an identifier.  Does not
   *	distinguish the characters ('-' and '.') that are not officially
   *	permitted at the <em>beginning</em> of an identifier. */
  static BitSet isIdent;

  /** True for every character that is whitespace. */
  static BitSet isSpace;
  
  /** Initialize the identifier and whitespace BitSet's.  Since we are only 
   *	concerned with the SGML reference syntax, we don't have to make these 
   *	public or have a set for each Parser object. */
  static void initializeTables() {
    for (int i = 0; i <= ' '; ++i) isSpace.set(i);
    for (int i = 'A'; i <= 'Z'; ++i) isIdent.set(i);
    for (int i = 'a'; i <= 'z'; ++i) isIdent.set(i);
    for (int i = '0'; i <= '9'; ++i) isIdent.set(i);
    isIdent.set('-');
    isIdent.set('.');
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
  boolean eatUntil(int aCharacter, boolean checkEntities) throws IOException {
    last = in.read();
    if (last < 0) return false;
    if (checkEntities && last == '&') return false;
    if (last == aCharacter) return true;
    do {
      buf.append((char)last);
      last = in.read();
    } while (last >= 0 && last != aCharacter
	     && !(checkEntities && last == '&'));
    return last >= 0;    
  }

  /** Starting at the next available character, append characters to
   *	<code>buf</code> until <code>aString</code> (typically an end
   *	tag) is matched.
   *
   *	@return false if end-of-file is reached before a match. */
  boolean eatUntil(String aString, boolean checkEntities) throws IOException {

    return false;
  }

  /** Starting at the next available character, append spaces to 
   *	<code>buf</code> until a non-blank character is reached.
   */
  boolean eatSpaces() throws IOException {
    last = in.read();
    if (last < 0) return false;
    if (last <= ' ') return true;
    do {
      buf.append((char)last);
      last = in.read();
    } while (last >= 0 && last <= ' ');
    return last >= 0;    
  }



  /************************************************************************
  ** SGML Recognizers:
  ************************************************************************/

  /** Pull an entity off the input stream and return it in <code>next</code>.
   *	Assume that <code>last</code> contains an ampersand.  If the next
   *	available character does not belong in an identifier, appends the
   *	ampersand to <code>buf</code>.  Eat a trailing semicolon if present.
   *	@return false if the next available character does not belong in
   *	an entity name.
   */
  boolean getEntity() throws IOException {
    if (!eatIdent()) {
      buf.append("&"); 
      buf.append((char)last);
      return false;
    }
    next = new Token("&", ident, (last == ';')? ";" : null);
    if (last == ';') last = 0;
    return true;
  }

  /** Get a literal, i.e. everything up to <code>endString</code>.
   *	Clear endString and ignoreEntities when the end string is seen.
   *	If ignoreEntities is false, entities will be recognized.
   */
  SGML getLiteral() {

    buf = new StringBuffer();
    Tokens list = new Tokens();
    
    try {
      for ( ; ; ) {
	if (eatUntil(endString, !ignoreEntities)) {
	  if (list.isEmpty() || ! (buf.length() == 0)) 
	    list.append(new Text(buf));
	  break;
	}
	if (getEntity()) {
	  list.append(next);
	}
      }
    } catch (Exception e) {}
    endString = null;
    ignoreEntities = false;
    return list;
  }

  /** Get a value (after an attribute name inside a tag.
   *	Returns true if <code>last</code> is an equal sign and is 
   *	followed by either an identifier or a quoted string.
   *	@return the value in <code>next</code>
   */
  boolean getValue() throws IOException {
    if (last != '=') return false;

    last = 0;
    if (eatIdent()) {
      next = new Text(ident);
      return true;
    } else if (last == '\'' || last == '"') {
      int quote = last;
      StringBuffer tmp = buf;
      buf = new StringBuffer();
      Tokens list = new Tokens();
      
      for ( ; ; ) {
	if (eatUntil(quote, true)) {
	  if (list.isEmpty() || ! (buf.length() == 0)) 
	    list.append(new Text(buf));
	  break;
	}
	if (getEntity()) {
	  list.append(next);
	}
      }
      next = list.isText()? (SGML)list.toText() : (SGML)list;
      buf = tmp;
      return true;
    } else {
      // === maybe should try nonblanks
      return false;
    }
  }

  /** Get a tag starting with <code>last</code> and return it in
   *	<code>next</code>.  If what follows is not, in fact, a valid
   *	tag, it returns false and leaves the bad characters appended
   *	to <code>buf</code>.  getTag is only called from getText. <p>
   *
   *	One could argue that it should allow space after the &lt;. */
  boolean getTag() throws IOException {

    if (eatIdent()) {		// <tag...	start tag
      // Save our position in buf in case we lose.
      int tagStart = buf.length();

      Token it = new Token(ident);
      String a; StringBuffer v;

      // Now go after the attributes.
      //    They have to be separated by spaces.

      while (last >= ' ') {
	// === need to be appending the identifier in case we lose ===
	eatSpaces();	
	if (eatIdent()) {
	  a = ident;
	  if (getValue()) it.attr(a, next);
	  else		  it.attr(a, new Tokens());
	}
      }

      // Done.  Clean up the buffer and return the new tag in next.
      buf.setLength(tagStart);
      next = it;
      if (last >= 0) last = 0;
    } else if (last == '/') {	// </...	end tag
      eatIdent();
      next = new Token(ident);
      next.incomplete((byte)-1);
    } else if (last == '!') {	// <!...	comment or declaration
      StringBuffer tmp = buf;
      buf = new StringBuffer();
      eatIdent();		// note that -- is an SGML identifier
      if (ident.length() >= 2 && // ... so check for it.
	  ident.charAt(0) == '-' && ident.charAt(1) == '-') {
	// it must be a comment
	if (last != '>') eatUntil("-->", false);
	if (last == '>') last = 0;
	next = new Token("!--", "<!" + ident, buf, "-->");
      } else {
	// it's an SGML declaration: <!...>
	eatUntil('>', false);
	if (last == '>') last = 0;
	next = new Token("!", "<!" + ident, buf, ">");
      }
      buf = tmp;
    } else if (last == '>') {	// <>		empty start tag
      next = new Token(ident);
      next.incomplete((byte)1);
    } else {			// not a tag.
      return false;
    }
    return true;
  }

  /** Get text starting with <code>last</code>.  If the text is
   *	terminated by an entity or tag, the entity or tag ends up in
   *	<code>next</code>, and the character that terminated
   *	<em>it</em> is left in <code>last</code>.  */
  SGML getText() throws IOException {

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
    SGML it = null;

    try {
      it = getText();		// Try to get some text.
    } catch (IOException e) {};
    if (it == null) {		// If that failed,
      it = next;		// 	a tag or entity must be in next
      next = null;		// 	so clear next for next time.
    }
    return it;
  }

}
