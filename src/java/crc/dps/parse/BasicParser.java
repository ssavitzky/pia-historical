////// BasicParser.java: minimal implementation of the Parser interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.parse;

import crc.dom.NodeList;

import crc.dps.NodeType;
import crc.dps.Parser;
import crc.dps.Token;
import crc.dps.TokenList;
import crc.dps.BasicToken;
import crc.dps.BasicTokenList;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.BitSet;

import java.io.Reader;
import java.io.IOException;

/**
 * A basic implementation of the Parser interface. <p>
 *
 *	BasicParser operates with no knowledge of the DTD of the
 *	document it is parsing, except for what it can get from the
 *	Tagset.  In particular, the lexical level is essentially the
 *	SGML reference syntax as used by HTML and XML. <p>
 *
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Parser

 */

public class BasicParser extends AbstractParser {

  /************************************************************************
  ** SGML Recognizers:
  ************************************************************************/

  /** Pull an entity off the input stream and return it in <code>next</code>.
   *	Assume that <code>last</code> contains an ampersand.  If the next
   *	available character does not belong in an identifier, appends the
   *	ampersand to <code>buf</code>.  Eat a trailing semicolon if present.
   * <p>
   *	Correctly handles <code>&amp;<i>ident</i>=</code>, which is not
   *	an entity reference but part of a query string.
   *
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
    if (last == '=') {
      buf.append("&" + ident + "=");
      last = 0;
      return false;
    }
    next = new BasicToken(NodeType.ENTITY);
    next.setName(ident);
    if (last == ';') next.setHasClosingDelimiter(true);
    if (last == ';') last = 0;
    return true;
  }

  /** Get a literal, i.e. everything up to <code>endString</code>.
   *	Clear endString and ignoreEntities when the end string is seen.
   *	If ignoreEntities is false, entities will be recognized.
   */
  TokenList getLiteral(String endString, boolean ignoreEntities) {

    buf = new StringBuffer();
    TokenList list = new crc.dps.BasicTokenList();
    
    try {
      for ( ; ; ) {
	if (eatUntil(endString, !ignoreEntities)) {
	  if (buf.length() != 0) 
	    list.append(new BasicToken(buf.toString()));
	  break;
	}
	if (last == '&' && getEntity()) {
	  list.append(next);
	}
      }
    } catch (Exception e) {}
    return list;
  }


  /** Get a value (after an attribute name inside a tag).
   *	
   *	@return the value; <code>null</code> if no "=" is present.
   */
  NodeList getValue() throws IOException {
    if (last != '=') return null;
    BasicTokenList list = new BasicTokenList();

    last = in.read();
    if (last == '\'' || last == '"') {
      int quote = last;
      StringBuffer tmp = buf;
      buf = new StringBuffer();
      last = 0;
      for ( ; ; ) {
	if (eatUntil(quote, true)) {
	  if (buf.length() != 0) {
	    list.append(new BasicToken(buf.toString()));
	    buf.setLength(0);
	  }
	  if (last == quote) break;
	} else break;
	if (getEntity()) {
	  list.append(next);
	}
      }
      last = 0;
      //debug("=" + (char)quote + (list.isText()? ".." : ".&.") + (char)quote);
      //debug("=" + (char)quote + next.toString() + (char)quote);
      buf = tmp;
      return list;
    } else if (last <= ' ' || last == '>') {
      list.append(new BasicToken(""));
      return list;
    } else {
      StringBuffer tmp = buf;
      buf = new StringBuffer();
      for ( ; ; ) {
	if (eatUntil(notAttr, true)) {
	  if (buf.length() != 0) {
	    list.append(new BasicToken(buf.toString()));
	    buf.setLength(0);
	  }
	} else break;
	if (getEntity()) {
	  list.append(next);
	} else break;
      }
      //debug("=" + (list.isText()? ".." : ".&."));
      buf = tmp;
      return list;
    }
    /* === checking for an Ident doesn't work; too many missing quotes === */
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
      //debug(ident);

      BasicToken it = new BasicToken(ident, -1);
      String a; StringBuffer v;

      // Now go after the attributes.
      //    They have to be separated by spaces.

      while (last >= 0 && last != '>') {
	// need to be appending the identifier in case we lose ===
	eatSpaces();	
	if (eatIdent()) {
	  a = ident.toLowerCase();
	  buf.append(ident);
	  //debug(" "+a);
	  it.addAttr(a, getValue());
	} else if (last == '/') {
	  // XML-style empty-tag indicator.
	  it.setHasEmptyDelimiter(true);
	  it.setSyntax(-2);
	  it.setIsEmptyElement(true);
	  last = 0;
	} else break;
      }
      if (last != '>') return false;

      // Look the token up in the Tagset.

      // Done.  Clean up the buffer and return the new tag in next.
      buf.setLength(tagStart);
      next = it;
      if (last >= 0) last = 0;
    } else if (last == '/') {	// </...	end tag
      // debug("'/'");
      buf.append("/"); last = 0;
      eatIdent(); buf.append(ident);
      // debug(ident);

      eatSpaces();
      if (last != '>') return false;
      Token it = new BasicToken(ident, 1); // cannonicalize name?
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
	next = new BasicToken(NodeType.COMMENT, buf.toString());
      } else {
	// it's an SGML declaration: <!...>
	// == Comments or occurrences of '>' inside will fail.
	eatUntil('>', false);
	if (last == '>') last = 0;
	// === bogus -- really a declaration, and must be further analyzed. //
	next = new BasicToken(NodeType.COMMENT, ident, buf.toString());
      }
      buf = tmp;
      buf.setLength(buf.length()-1); // remove the extraneous '<'
    } else if (last == '?') {	// <?...	PI
      StringBuffer tmp = buf;
      buf = new StringBuffer();
      last = 0; ident = null;
      // note that -- is an identifier, so check for it with eatIdent
      eatIdent();
      eatUntil('>', false);
      if (last == '>') last = 0;
      next = new BasicToken(NodeType.PI, ident, buf.toString());
      buf = tmp;
      buf.setLength(buf.length()-1); // remove the extraneous '<'
    } else if (last == '>') {	// <>		empty start tag
      next = new BasicToken(ident, -1);
    } else {			// not a tag.
      return false;
    }
    return true;
  }

  /** Get text starting with <code>last</code>.  If the text is
   *	terminated by an entity or tag, the entity or tag ends up in
   *	<code>next</code>, and the character that terminated
   *	<em>it</em> is left in <code>last</code>.  
   *
   * === This will eventually get split so we can detect space, etc. ===
   */
  Token getText() throws IOException {

    while (eatText()) {
      if ((last == '&' && getEntity()) ||
	  (last == '<' && getTag()) ||
	  (last < 0)) break;
    }
    return (buf.length() > 0)? new BasicToken(buf.toString()) : null;
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
   *	separately.  Subclasses will do this differently.  <p>
   *
   */
  public Token nextToken() {
    if (nextText == null && next == null) try {
      buf.setLength(0);
      nextText = getText();	// Try to get some text.
    } catch (IOException e) {};
    
    // At this point we have to check for nesting and the presence of 
    // ignorable whitespace between, e.g., list tags.
    return checkNextToken();
  }


  /************************************************************************
  ** Construction:
  ************************************************************************/

  public BasicParser() {
    super();
  }

  public BasicParser(crc.dps.InputStack previous) {
    super(previous);
  }

  public BasicParser(java.io.InputStream in, crc.dps.InputStack previous) {
    super(in, previous);
  }

  public BasicParser(Reader in, crc.dps.InputStack previous) {
    super(in, previous);
  }

}
