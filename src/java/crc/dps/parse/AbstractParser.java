////// AbstractParser.java: abstract implementation of the Parser interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.parse;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.BitSet;

import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.IOException;

import crc.dps.Token;
import crc.dps.Tagset;
import crc.dps.Parser;
import crc.dps.Processor;
import crc.dps.Handler;
import crc.dps.NodeType;
import crc.dps.BasicToken;
import crc.dps.InputStack;
import crc.dps.EntityTable;
import crc.dps.input.AbstractInputFrame;

import crc.dom.AttributeList;

/**
 * An abstract implementation of the Parser interface.  <p>
 *
 *	This class contains the methods required to recognize the basic
 *	low-level syntactic elements of SGML such as identifiers and tags.
 *
 *	<p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Parser
 */

public abstract class AbstractParser extends AbstractInputFrame
				     implements Parser
{

  /************************************************************************
  ** Processor Access:
  ************************************************************************/

  protected int		 guardedDepth;
  protected Processor    processor;

  public Processor getProcessor() { return processor; }

  public void setProcessor(Processor aProcessor) {
    processor 	 = aProcessor;
    guardedDepth = aProcessor.getDepth();
    tagset	 = aProcessor.getHandlers();
  }

  /************************************************************************
  ** Reader Access:
  ************************************************************************/

  protected Reader in = null;

  public Reader getReader() { return in; }
  public void setReader(Reader aReader) { in = aReader; }

  /************************************************************************
  ** Access to Bindings:
  ************************************************************************/

  protected Tagset tagset = null;
  protected EntityTable entities = null; 

  public Tagset getTagset() { return tagset; }
  public void setTagset(Tagset aTagset) {
    tagset = aTagset;
    if (aTagset != null) caseFoldTagnames = aTagset.caseFoldTagnames();
  }

  public EntityTable getEntities() { return entities; }
  public void setEntities(EntityTable anEntityTable) {
    entities = anEntityTable;
  }

  protected boolean caseFoldTagnames = true;

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

  /** Holds characters that have been ``eaten'' from the stream. */
  protected StringBuffer buf = new StringBuffer(256);

  /** Holds an identifier ``eaten'' from the stream. */
  protected String ident;

  /** Holds the character that terminated the current token, or -1 if
   *    the token was terminated by end-of-file.  It will be prepended to 
   *	the <em>next</em> token if non-null.  This gives the scanner
   *	one character of lookahead, which is almost always enough.
   */
  protected int last = 0;

  /** Returns true if it is known that no more tokens are available. 
   *	The implementation takes advantage of the fact that a Reader returns
   *	<code>-1</code> if no input is available, and that the last character
   *	read is always in <code>last</code>.
   */
  public boolean atEnd() {
    return last < 0;
  }

  /** Holds the next item, usually either an entity reference or a tag. */
  protected Token next;

  /** Starting at <code>last</code> (or the next available character
   *	if <code>last</code> is zero), append characters to
   *	<code>buf</code> until the next non-ordinary character (&amp; or
   *	&lt;) or end-of-buffer is seen.  The terminating character ends
   *	up in <code>last</code>.
   *
   *	@return true if at least one character is eaten. */
   protected final boolean eatText() throws IOException {
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
   protected final boolean eatIdent() throws IOException {
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
   protected final boolean eatUntil(int aCharacter, boolean checkEntities)
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
   protected final boolean eatUntil(BitSet aBitSet, boolean checkEntities)
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
   protected final boolean eatUntil(String aString, boolean checkEntities)
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
   protected final boolean eatSpaces() throws IOException {
    if (last == 0) last = in.read();
    if (last < 0) return false;
    while (last >= 0 && last <= ' ') {
      buf.append((char)last);
      last = in.read();
    }
    return last >= 0;    
  }


  /************************************************************************
  ** Parsing Utilities:
  ************************************************************************/

  /** Get the syntax (Handler) for a Token.
   *	It is more efficient to consult the Tagset directly if the type
   *	is known ahead of time.
   *
   * @return the Token with its handler field set.
   */
  protected final Token getSyntax(Token t) {
    if (tagset == null) return t;

    int nodeType = t.getNodeType();
    Handler handler = null;
    if (nodeType == NodeType.ELEMENT && ! t.isEndTag()) {
      handler = tagset.getHandlerForTag(t.getTagName());
      handler = handler.getHandlerForToken(t);
      if (handler.isEmptyElement(t)) { 
	t.setIsEmptyElement(true);
	// === Do we need to set syntax=0 if isEmptyElement???
      }
    } else if (nodeType == NodeType.TEXT) {
      handler = tagset.getHandlerForText(t.getIsWhitespace());
    } else if (nodeType == NodeType.ENTITY) {
      handler = tagset.getHandlerForEntity(t.getName());
    } else {
      handler = tagset.getHandlerForType(nodeType);
    }
    t.setHandler(handler);
    return t;
  }

  /** Current text token.
   *	Along with <code>next</code>, this lets the Parser look at both
   *	the text and the tag (if that's what it is) that follows it.  
   */
  protected Token nextText = null;

  /** Perform syntax checking. 
   *	Uses <code>nextText</code> and <code>next</code>.  Assumes that
   *	<code>next</code> was recognized after <code>nextText</code>.
   *
   * @return the correct result to return from <code>nextToken</code>
   */
  protected Token checkNextToken() {
    Token it;
    if (nextText != null) {
      if (next != null && next.isStartTag()) {
	// === check for ignorable whitespace before list element ===
	// === requires one more bit of state, in returnedEndTag ...
      }
      it = nextText;
      nextText = null;
      return it;
    } else if (next != null) {
      if (next.isEndTag()) {
	returnedEndTag = true;

	// check for missing end tags, and supply them if necessary.
	// This is tedious, but straightforward.
	String tag = next.getTagName();
	String inside = processor.elementTag();
	if (caseFoldTagnames) {
	  tag = tag.toLowerCase();
	  inside = inside.toLowerCase();
	}
	if (tag == null || tag.equals(inside)) {
	  // Current tag.  Everything's fine.
	} else if (!checkedBadNesting) {
	  // Not the current element.  Are we somewhere inside it?
	  checkedBadNesting = true;
	  if (processor.insideElement(tag, caseFoldTagnames, guardedDepth)) {
	    // ... Yes, we're OK.  End the current element.
	    return new BasicToken(inside, 1, true);
	  } else {
	    // ... Bad nesting.  Change next to an appropriate comment.
	    next = new BasicToken(NodeType.COMMENT, "Bad end tag: " + tag);
	  }
	} else {
	  // We've already checked for bad nesting, and we're OK.
	  // End the current element.
	  return new BasicToken(inside, 1, true);
	}
      } else if (tagset != null &&
		 tagset.checkElementNesting(next, processor) > 0) {
	returnedEndTag = true;
	return new BasicToken(processor.elementTag(), 1);
      } else {
	returnedEndTag = false;
      }
      checkedBadNesting = false;
      it = next;
      next = null;
      return it;
    } else if (processor.getDepth() > guardedDepth) {
      // Cribbed from crc.dps.input.Guard: 
      //   make sure we pop back to the depth we started at.
      return new BasicToken(processor.elementTag(), 1, false);
    } else {
      return null;
    }
  }

  /** <code>true</code> if <code>checkNextToken</code> has checked for
   *	an end tag with no corresponding start tag.
   */
  protected boolean checkedBadNesting = false;

  /** <code>true</code> if <code>checkNextToken</code> has just returned
   *	an end tag.
   */
  protected boolean returnedEndTag = false;

  /************************************************************************
  ** Mode Creation:
  ************************************************************************/
  
  /** Called during parsing to return a suitable start tag Token for the
   *	given tagname and attribute list. 
   */
  protected Token createStartToken(String tagname, AttributeList attrs) {
    if (tagset != null) return tagset.createStartToken(tagname, attrs, this);
    return new BasicToken(tagname, -1, attrs, null);
  }

  /** Called during parsing to return an end tag Token. 
   */
  protected Token createEndToken(String tagname) {
    return new BasicToken(tagname, 1);
  }

  /** Called during parsing to return a suitable Token for a generic
   *	Node with String data. 
   */
  protected Token createToken(int nodeType, String data) {
    if (tagset != null) return tagset.createToken(nodeType, data, this);
    return new BasicToken(nodeType, data);
  }

  /** Called during parsing to return a suitable Token for a generic
   *	Node with a name, and String data. 
   */
  protected Token createToken(int nodeType, String name, String data) {
    if (tagset != null) return tagset.createToken(nodeType, name, data, this);
    return new BasicToken(nodeType, name, data);
  }

  /** Called during parsing to return a suitable Token for a new Text.
   */
  protected Token createTextToken(String text) {
    if (tagset != null) return tagset.createTextToken(text, this);
    return new BasicToken(text);
  }


  /************************************************************************
  ** Construction:
  ************************************************************************/
  
  public AbstractParser() {
    if (isIdent == null) initializeTables();
  }

  public AbstractParser(InputStack previous) {
    super(previous);
    if (isIdent == null) initializeTables();
  }

  public AbstractParser(InputStream in, InputStack previous) {
    super(previous);
    this.in = new InputStreamReader(in);
    if (isIdent == null) initializeTables();
  }

  public AbstractParser(Reader in, InputStack previous) {
    super(previous);
    this.in = in;
    if (isIdent == null) initializeTables();
  }

}
