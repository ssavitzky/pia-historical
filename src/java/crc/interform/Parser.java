////// Parser.java:  InterForm SGML Parser
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;
import crc.interform.Input;

import crc.sgml.SGML;
import crc.sgml.Text;
import crc.sgml.TextBuffer;

import crc.sgml.Token;
import crc.sgml.Element;
import crc.sgml.Entity;
import crc.sgml.Tokens;

import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.Reader;

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
 * <b>Nomenclature:</b> Methods with names starting in <code>eat</code>
 *	attempt to consume a lexical token from the input stream, and
 *	return <code>true</code> if they are successful.  If they
 *	fail, they leave the input stream unchanged.  Methods with
 *	names starting in <code>get</code> attempt to consume a more
 *	complex construct, and leave their rejected input in
 *	<code>buf</code> if they fail. <p>
 *
 * <b>NOTE:</b> The Parser is currently unable to correctly handle SGML
 *	comments that contain the string "-->" internally.  <p>
 *
 * <b>NOTE:</b> We are <strong>not</strong> using a StreamTokenizer at this
 *	point.  The reason is that we need all characters to be significant
 *	and ``ordinary'' outside of tags, so the StreamTokenizer would
 *	simply add an extra layer of overhead for very little effect. <p>
 *
 *	@see http:/PIA/src/perl/IFParser.pm <p>
 */
public class Parser extends Input {

  /************************************************************************
  ** Variables:
  ************************************************************************/

  /** The interpretor.  This is used to obtain syntax information. */
  Interp interp = null;

  /** Tell the Input what interpretor it is working for. */
  public void interp(Interp ii) {
    interp = ii;
  }

  /** The input stream.  It is possible to use an ordinary InputStream
   *	at this point; all buffering is done internally for efficiency. */
  Reader in = null;

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
  ** Debugging:
  ************************************************************************/

  /** Debugging flag. */
  boolean debug;

  public final void debug(String s) {
    if (debug) { System.err.print(s); }
  }

  /************************************************************************
  ** Input (pull) Interface:
  ************************************************************************/

  /** Return the next token in the input stream.   Waits, if necessary,
   *	for more input to appear.<p>
   *
   * Debugging output: The first character of each token is output in
   *	single quotes unless it was recognized as part of the previous
   *	token.  "(" + flag + tag + attr... + ")" for each token. The
   *	flag is "/" for end tags, "\" for start tags, and "|" for
   *	completed elements.
   * */
  public SGML nextInput() {
    SGML it = null;

    if (next != null) {		// There's one hanging fire, so return it.
      it = next;
      next = null;
    } else try {		// We'll have to do some I/O for this one.

      // Get a character if the last one was already eaten (last == 0).
      //	If we hit EOF, return.

      if (last == 0) last = in.read();
      if (last < 0) return null;

      debug("'" + (char)last + "'");

      if (endString != null) {
	it = getLiteral();
      } else {
	it = getToken();
      }
      
      // Protect this section against IO exceptions and other road hazards.
    } catch (IOException e) {}

    if (debug && it == null) {
      debug("(=eof=)\n");
    }      
    return it;
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
    if (isIdent == null) initializeTables();
  }

  public Parser(InputStream in, Input previous) {
    super(previous);
    this.in = new InputStreamReader(in);
    if (isIdent == null) initializeTables();
  }

  public Parser(Reader in, Input previous) {
    super(previous);
    this.in = in;
    if (isIdent == null) initializeTables();
  }

  /************************************************************************
  ** Syntax tables:
  ************************************************************************/

  /** True for every character that is part of an identifier.  Does not
   *	distinguish the characters ('-' and '.') that are not officially
   *	permitted at the <em>beginning</em> of an identifier. */
  public static BitSet isIdent;

  /** True for every character that is whitespace. */
  public static BitSet isSpace;
  
  /** True for every character permitted in a URL */
  public static BitSet isURL;

  /** True for every character not permitted in an attribute */
  public static BitSet notAttr;

  /** Initialize the identifier and whitespace BitSet's.  Since we are only 
   *	concerned with the SGML reference syntax, we don't have to make these 
   *	public or have a set for each Parser object. */
  static void initializeTables() {
    int i;
    isIdent = new BitSet();
    isSpace = new BitSet();
    isURL = new BitSet();
    notAttr = new BitSet();
    for (i = 0; i <= ' '; ++i) { isSpace.set(i); notAttr.set(i); }
    for (i = 'A'; i <= 'Z'; ++i) { isIdent.set(i); isURL.set(i); }
    for (i = 'a'; i <= 'z'; ++i) { isIdent.set(i); isURL.set(i); }
    for (i = '0'; i <= '9'; ++i) { isIdent.set(i); isURL.set(i); }
    isIdent.set('-'); isURL.set('-');
    isIdent.set('.'); isURL.set('.');
    String url = ":/?+~%&;";
    for (i = 0; i < url.length(); ++i) isURL.set(url.charAt(i));
    String s = "<>\"'";
    for (i = 0; i < s.length(); ++i) notAttr.set(s.charAt(i));
  }

  /************************************************************************
  ** Low-level Recognizers:
  ************************************************************************/

  /** Starting at <code>last</code> (or the next available character
   *	if <code>last</code> is zero), append characters to
   *	<code>buf</code> until the next non-ordinary character (&amp; or
   *	&lt;) or end-of-buffer is seen.  The terminating character ends
   *	up in <code>last</code>.
   *
   *	@return true if at least one character is eaten. */
  final boolean eatText() throws IOException {
    if (last == 0) last = in.read();
    if (last < 0) return false;
    if (last == '&' || last == '<') return true;
    do {
      buf.append((char)last);
      last = in.read();
    } while (last >= 0 && last != '&' && last != '<');
    return last >= 0;    
  }

  /** Starting at <code>last</code> (or the next available character
   *	if <code>last</code> is zero), append characters to a
   *	String until a character that does not belong in an identifier
   *	is found.  Identifiers, in SGML-land, may include letters,
   *	digits, "-", and ".".  The terminating character ends up in
   *	<code>last</code>, and the string in <code>ident</code>.
   *
   *	@return true if at least one character is eaten.  */
  final boolean eatIdent() throws IOException {
    if (last == 0) last = in.read();
    String id = "";
    if (last < 0 || ! isIdent.get(last)) return false;
    do {
      id += (char)last;
      last = in.read();
    } while (last >= 0 && isIdent.get(last));
    ident = id;
    return true;    
  }
    
  /** Starting at the next available character, append characters to
   *	<code>buf</code> until <code>aCharacter</code> (typically a
   *	quote) is seen.
   *
   *	@return false if end-of-file is reached before a match. */
  final boolean eatUntil(int aCharacter, boolean checkEntities)
       throws IOException {
    if (last == 0) last = in.read();
    while (last >= 0 && last != aCharacter
	   && !(checkEntities && last == '&')) {
      buf.append((char)last);
      last = in.read();
    } 
    return last >= 0;    
  }

  /** Starting at the next available character, append characters to
   *	<code>buf</code> until a character in <code>aBitSet</code> is seen.
   *
   *	@return false if end-of-file is reached before a match. */
  final boolean eatUntil(BitSet aBitSet, boolean checkEntities)
       throws IOException {
    if (last == 0) last = in.read();
    while (last >= 0 && ! aBitSet.get(last)
	   && !(checkEntities && last == '&')) {
      buf.append((char)last);
      last = in.read();
    } 
    return last >= 0;    
  }

  /** Starting at the next available character, append characters to
   *	<code>buf</code> until <code>aString</code> (typically an end
   *	tag) is matched.  A case-insensitive match is done.
   *
   *	@return false if end-of-file is reached before a match. */
  final boolean eatUntil(String aString, boolean checkEntities)
       throws IOException {
    int start = buf.length();
    aString = aString.toLowerCase();
    int matchLength = aString.length();
    char aCharacter = aString.charAt(0);
    int itsPosition = -1;
    int nextPosition = aString.indexOf(aCharacter, 1);

    if (last == 0) last = in.read();
    while (last >= 0) {

      /* This could be faster, but it could be a lot slower, too.  We
       * append, looking for aCharacter, the first character in
       * aString.  We keep track of its position in itsPosition, the
       * tentative starting point of a match to aString.
       */
      if (Character.toLowerCase((char)last) == aCharacter && itsPosition < 0) {
	itsPosition = buf.length();
      }
      buf.append((char)last);

      /* When we have enough characters to match the whole string, we
       * try for a match.  This would be much simpler if StringBuffer
       * had all the methods of String.
       */
      if (itsPosition >= 0 &&
	  (buf.length() - itsPosition) == matchLength) {
	int i = 1, j = itsPosition + 1;
	for ( ; i < matchLength; ++i, ++j) {
	  if (aString.charAt(i) != Character.toLowerCase(buf.charAt(j))) {
	    j = 0;
	    break;
	  }
	}
	if (j > 0) {		// Success
	  buf.setLength(buf.length() - matchLength);
	  return true;
	}

	/* The match failed.  Advance the tentative starting point to the
	 * next occurrence of aCharacter, if any.
	 */
	if (nextPosition > 0 && nextPosition < i) 
	  itsPosition += nextPosition;
	else
	  itsPosition = -1;
      }
      last = in.read();
    } 
    // ===
    return false;
  }

  /** Starting at <code>last</code> (or the next available character
   *	if <code>last</code> is zero), append spaces to 
   * <code>buf</code> until a non-blank character is reached.  */
  final boolean eatSpaces() throws IOException {
    if (last == 0) last = in.read();
    if (last < 0) return false;
    while (last >= 0 && last <= ' ') {
      buf.append((char)last);
      last = in.read();
    }
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
    if (last != '&') return false;
    last = 0;
    if (!eatIdent()) {
      buf.append("&"); 
      return false;
    }
    next = new Entity(ident, last == ';');
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
	    list.append(new TextBuffer(buf));
	  break;
	}
	if (last == '&' && getEntity()) {
	  list.append(next);
	}
      }
    } catch (Exception e) {}
    endString = null;
    ignoreEntities = false;
    return list;
  }

  /** Get a value (after an attribute name inside a tag).
   *	Returns true if <code>last</code> is an equal sign and is 
   *	followed by either an identifier or a quoted string.
   *	=== should really allow any URL-permissible chars. ===
   *	@return the value in <code>next</code>
   */
  boolean getValue() throws IOException {
    if (last != '=') return false;

    last = in.read();
    if (last == '\'' || last == '"') {
      int quote = last;
      StringBuffer tmp = buf;
      buf = new StringBuffer();
      Tokens list = new Tokens();
      last = 0;
      for ( ; ; ) {
	if (eatUntil(quote, true)) {
	  if (list.isEmpty() || ! (buf.length() == 0)) {
	    list.append(new TextBuffer(buf));
	    buf = new StringBuffer();
	  }
	  if (last == quote) break;
	} else break;
	if (getEntity()) {
	  list.append(next);
	}
      }
      next = list.nItems() == 1? list.itemAt(0) : list;
      //eatUntil(quote, false);
      //next = new Text(buf);	// === need to check for entities
      last = 0;
      debug("=" + (char)quote + (list.isText()? ".." : ".&.") + (char)quote);
      debug("=" + (char)quote + next.toString() + (char)quote);
      buf = tmp;
      return true;
    } else if (last <= ' ' || last == '>') {
      next = new Text("");
      return true;
    } else {
      StringBuffer tmp = buf;
      buf = new StringBuffer();
      Tokens list = new Tokens();
      for ( ; ; ) {
	if (eatUntil(notAttr, true)) {
	  if (list.isEmpty() || ! (buf.length() == 0)) {
	    list.append(new TextBuffer(buf));
	    buf = new StringBuffer();
	  }
	} else break;
	if (getEntity()) {
	  list.append(next);
	} else break;
      }
      // === using next=list.simplify() here drops pieces.
      next = list.nItems() == 1? list.itemAt(0) : list;
      debug("=" + (list.isText()? ".." : ".&."));
      buf = tmp;
      return true;
    }
    /* === checking for an Ident doesn't work; too many missing quotes ===
    } else if (eatIdent()) {
      next = new Text(ident);
      debug("="+ident);
      return true;
    } else {
      next = new Text("");
      return true;
    }
    === */
  }

  /** Get a tag starting with <code>last='&amp;'</code> and return it in
   *	<code>next</code>.  If what follows is not, in fact, a valid
   *	tag, it returns false and leaves the bad characters appended
   *	to <code>buf</code>.  getTag is only called from getText. <p>
   *
   *	One could argue that it should allow space after the &lt;. */
  boolean getTag() throws IOException {
      int tagStart = buf.length(); // save position in case we lose
      buf.append("<");		// append the "<" that we know is there
      last = 0;			// force eatIdent to read the next char.

    if (eatIdent()) {		// <tag...	start tag
      buf.append(ident);
      debug(ident);

      Element it = new Element(ident.toLowerCase());
      String a; StringBuffer v;

      // Now go after the attributes.
      //    They have to be separated by spaces.

      while (last >= 0 && last != '>') {
	// need to be appending the identifier in case we lose ===
	eatSpaces();	
	if (eatIdent()) {
	  a = ident.toLowerCase();
	  buf.append(ident);
	  debug(" "+a);
	  if (getValue()) it.addAttr(a, next);
	  else		  it.addAttr(a, Token.empty);
	} else break;
      }
      if (last != '>') return false;

      // Done.  Clean up the buffer and return the new tag in next.
      buf.setLength(tagStart);
      it.incomplete((byte)2);
      next = it;
      if (last >= 0) last = 0;
    } else if (last == '/') {	// </...	end tag
      debug("'/'");
      buf.append("/"); last = 0;
      eatIdent(); buf.append(ident);
      debug(ident);

      eatSpaces();
      if (last != '>') return false;
      Element it = Element.endTagFor(ident.toLowerCase());
      it.incomplete((byte)-2);
      it.endTagRequired((byte)1);
      next = it;
      buf.setLength(tagStart);
      if (last >= 0) last = 0;
    } else if (last == '!') {	// <!...	comment or declaration
      StringBuffer tmp = buf;
      buf = new StringBuffer();
      last = 0; ident = null;
      // note that -- is an identifier, so check for it with eatIdent
      if (eatIdent() && ident.length() >= 2 &&
	  ident.charAt(0) == '-' && ident.charAt(1) == '-') {
	// it must be a comment
	if (last != '>') eatUntil("-->", false);
	if (last == '>') last = 0;
	next = new Element("!--", "<!" + ident, buf, "-->");
      } else {
	// it's an SGML declaration: <!...>
	// == Comments or occurrences of '>' inside will fail.
	eatUntil('>', false);
	if (last == '>') last = 0;
	next = new Element("!", "<!" + ident, buf, ">");
      }
      buf = tmp;
      buf.setLength(buf.length()-1); // remove the extraneous '<'
    } else if (last == '>') {	// <>		empty start tag
      next = new Element(ident);
      next.incomplete((byte)2);
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
    return (buf.length() > 0)? new Text(buf.toString()) : null;
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
   *	run the full test next time around.  On the other hand, it
   *	might be useful to return words, punctuation, etc. as separate
   *	tokens, and the interpretor might appreciate getting entities
   *	separately .*/
  SGML getToken() {
    buf = new StringBuffer(256);
    SGML it = null;

    try {
      it = getText();		// Try to get some text.
    } catch (IOException e) {};
    if (it == null) {		// If that failed,
      it = next;		// 	a tag or entity must be in next
      next = null;		// 	so clear next for next time.
    } else {
      // debug("\"..\"");
    }
    return it;
  }

}
