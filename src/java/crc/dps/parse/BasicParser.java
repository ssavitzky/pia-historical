////// BasicParser.java: minimal implementation of the Parser interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.parse;

import crc.dom.NodeList;

import crc.dps.NodeType;
import crc.dps.Parser;
import crc.dps.active.*;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.BitSet;

import java.io.Reader;
import java.io.InputStream;
import java.io.IOException;

/**
 * A basic implementation of the Parser interface. <p>
 *
 *	BasicParser operates with no knowledge of the DTD of the
 *	document it is parsing, except for what it can get from the
 *	Tagset.  In particular, the lexical level is essentially the
 *	SGML reference syntax as used by HTML and XML.  The simplified
 *	syntax offered by the Syntax interface is used. <p>
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
   * @param strict if <code>true</code>, <em>require</em> a semicolon.
   * @return false if the next available character does not belong in
   *	an entity name.
   */
  protected boolean getEntity(boolean strict) throws IOException {
    if (last != entityStart) return false;
    last = 0;
    if (!eatIdent()) {
      buf.append(entityStart); 
      return false;
    }
    if (last == '=') {
      buf.append(entityStart); buf.append(ident);
      return false;
    } else if (strict && last != entityEnd) {
      buf.append(entityStart); buf.append(ident);
      return false;
    }

    next = tagset.createActiveEntity(ident, null);
    //if (last == entityEnd) next.setHasClosingDelimiter(true);
    if (last == entityEnd) last = 0;
    return true;
  }

  /** Get a literal, i.e. everything up to <code>endString</code>.
   *	Clear endString and ignoreEntities when the end string is seen.
   *	If ignoreEntities is false, entities will be recognized.
   */
  protected ParseNodeList getLiteral(String endString, boolean ignoreEntities) {

    buf = new StringBuffer();
    ParseNodeList list = new ParseNodeList();
    
    try {
      for ( ; ; ) {
	if (eatUntil(endString, !ignoreEntities)) {
	  if (buf.length() != 0) 
	    list.append(createActiveText(buf.toString(), false));
	  break;
	}
	if (last == '&' && getEntity(strictEntities)) {
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
  protected ParseNodeList getValue() throws IOException {
    if (last != '=') return null;
    ParseNodeList list = new ParseNodeList();

    last = in.read();
    if (last == '\'' || last == '"') {
      int quote = last;
      StringBuffer tmp = buf;
      buf = new StringBuffer();
      last = 0;
      for ( ; ; ) {
	if (eatUntil(quote, true)) {
	  if (buf.length() != 0) {
	    list.append(createActiveText(buf.toString(), false));
	    buf.setLength(0);
	  }
	  if (last == quote) break;
	} else break;
	if (getEntity(strictEntities)) {
	  list.append(next);
	}
      }
      last = 0;
      //debug("=" + (char)quote + (list.isText()? ".." : ".&.") + (char)quote);
      //debug("=" + (char)quote + next.toString() + (char)quote);
      buf = tmp;
      return list;
    } else if (last <= ' ' || last == '>') {
      list.append(createActiveText("", false));
      return list;
    } else {
      StringBuffer tmp = buf;
      buf = new StringBuffer();
      for ( ; ; ) {
	if (eatUntil(notAttr, true)) {
	  if (buf.length() != 0) {
	    list.append(createActiveText(buf.toString(), false));
	    buf.setLength(0);
	  }
	} else break;
	if (getEntity(strictEntities)) {
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
   *	An end tag is returned with <code>null</code> in <code>next</code>
   *	and the tag in <code>nextEnd</code>.
   */
  protected boolean getTag() throws IOException {
    int tagStart = buf.length(); // save position in case we lose
    buf.append("<");		// append the "<" that we know is there
    last = 0;			// force eatIdent to read the next char.
    next = null;

    if (eatIdent()) {		// <tag...	start tag
      buf.append(ident);
      //debug(ident);

      ActiveElement it = createActiveElement(ident);
      boolean hasEmptyDelim = false;
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
	  it.addAttribute(a, getValue());
	} else if (last == '/') {
	  // XML-style empty-tag indicator.
	  hasEmptyDelim = true;
	  last = 0;
	} else break;
      }
      if (last != '>') return false;

      // === Check for content entity and element handling ===

      // Done.  Clean up the buffer and return the new tag in next.
      buf.setLength(tagStart);
      next = correctActiveElement(it, hasEmptyDelim);
      if (last >= 0) last = 0;
    } else if (last == '/') {	// </...	end tag
      // debug("'/'");
      buf.append("/"); last = 0;
      eatIdent(); buf.append(ident);
      // debug(ident);

      eatSpaces();
      if (last != '>') return false;
      next = null;
      nextEnd = (ident == null)? "" : ident;
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
	next = createActiveNode(NodeType.COMMENT, buf.toString());
      } else {
	// it's an SGML declaration: <!...>
	// == Comments or occurrences of '>' inside will fail.
	eatUntil('>', false);
	if (last == '>') last = 0;
	// === bogus -- really a declaration, and must be further analyzed. //
	next = createActiveNode(NodeType.DECLARATION, ident, buf.toString());
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
      next = createActiveNode(NodeType.PI, ident, buf.toString());
      buf = tmp;
      buf.setLength(buf.length()-1); // remove the extraneous '<'
    } else if (last == '>') {	// <>		empty start tag
      // === <> needs to get tag from enclosing element, which it ends.
      next = createActiveElement(ident, null, false);
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
  protected ActiveText getText() throws IOException {

    while (eatText()) {
      if ((last == '&' && getEntity(strictEntities)) ||
	  (last == '<' && getTag()) ||
	  (last < 0)) break;
    }
    return (buf.length() > 0)? createActiveText(buf.toString(), false) : null;
  }


  /************************************************************************
  ** Construction:
  ************************************************************************/

  public BasicParser() {
    super();
  }

  public BasicParser(InputStream in) {
    super(in);
  }

  public BasicParser(Reader in) {
    super(in);
  }

}
