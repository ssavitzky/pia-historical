////// AbstractParser.java: abstract implementation of the Parser interface
//	$Id$

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/


package crc.dps.parse;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.BitSet;

import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.IOException;

import crc.dom.Node;
import crc.dom.Element;
import crc.dom.Attribute;
import crc.dom.AttributeList;

import crc.dps.Tagset;
import crc.dps.Parser;
import crc.dps.Processor;
import crc.dps.Handler;
import crc.dps.NodeType;
import crc.dps.EntityTable;

import crc.dps.util.*;
import crc.dps.active.*;

/**
 * An abstract implementation of the Parser interface.  
 *
 * <p>	This class contains the methods required to recognize the basic
 *	low-level syntactic elements of SGML such as identifiers and tags,
 *	and to traverse the resulting Document.  It should probably be called
 *	something more descriptive of what it actually does, like ``Scanner.''
 *
 * <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Parser
 */

public abstract class AbstractParser extends CursorStack implements Parser
{
  /************************************************************************
  ** State:
  ************************************************************************/

  protected Processor   processor;
  protected ActiveNode 	document 	= null;
  protected Reader 	in 		= null;
  protected Tagset 	tagset 		= null;
  protected EntityTable entities	= null; 

  /************************************************************************
  ** Access:
  ************************************************************************/

  public ActiveNode getDocument() { return document; }
  public void setDocument(ActiveNode aDocument) {
    document = aDocument; 
    retainTree = (document != null);
  }

  public Processor getProcessor() { return processor; }
  public void setProcessor(Processor aProcessor) { processor = aProcessor; }

  public Reader getReader() { return in; }
  public void setReader(Reader aReader) { in = aReader; }

  public Tagset getTagset() { return tagset; }
  public void setTagset(Tagset aTagset) {
    tagset = aTagset;
    if (aTagset != null) caseFoldTagnames = aTagset.caseFoldTagnames();
  }

  public EntityTable getEntities() { return entities; }
  public void setEntities(EntityTable anEntityTable) {
    entities = anEntityTable;
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
    isIdent.set('_'); isURL.set('_');
    isIdent.set(':'); isURL.set(':');
    String url = "/?+~%&;";
    for (i = 0; i < url.length(); ++i) isURL.set(url.charAt(i));
    String s = "<>\"'";
    for (i = 0; i < s.length(); ++i) notAttr.set(s.charAt(i));
  }

  /************************************************************************
  ** SGML flags:
  **
  ** <p> The flags in this section control various parameters of the SGML
  **	 parsing process.  Strictly speaking they ought to be set from the
  **	 Tagset (DTD).  Defaults will usually differ among Parser
  **	 implementation classes.
  **
  ************************************************************************/

  /** If <code>true</code>, entities must be terminated by ';' */
  protected boolean strictEntities = true;

  /** The character that starts an entity (default '<code>&amp;</code>'). */
  protected char entityStart = '&';

  /** The character that starts a parameter entity (default '<code>%</code>').
   *	=== Parameter entities are presently not recognized. 
   */
  protected char parameterStart = '%';

  /** The character that ends an entity (default '<code>;</code>'). */
  protected char entityEnd = ';';

  /** Do we case-fold tagnames? */
  protected boolean caseFoldTagnames = true;

  /** Do we split text into whitespace and non-whitespace? */
  protected boolean splitTextTokens = false;

  /** Do we require non-identifier attributes to be quoted, or are we lenient?
   *	This interacts with both HTML (where URL's are often mistakenly not
   *	quoted), and XML (where the closing ``<code>/</code>'' that indicates
   *	an empty tag is often next to an identifier).
   */
  protected boolean strictAttributeQuotes = false;


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
    if (last == entityStart || last == '<') return true;
    do {
      buf.append((char)last);
      last = in.read();
    } while (last >= 0 && last != entityStart && last != '<');
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
	   && !(checkEntities && last == entityStart)) {
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
	   && !(checkEntities && last == entityStart)) {
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
    while (last >= 0 && last != entityStart) {

      /* This could be faster, but it could be a lot slower, too.  We
       * append to buf while looking for aCharacter, the first character in
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
	int i = 1; 
	int j = itsPosition + 1;
	for ( ; i < matchLength; ++i, ++j) {
	  if (aString.charAt(i) != Character.toLowerCase(buf.charAt(j))) {
	    break;
	  }
	}
	if (i == matchLength) {		// Success
	  buf.setLength(buf.length() - matchLength);
	  last = 0;
	  return true;
	}

	/* The match failed.  Advance the tentative starting point to the
	 * next occurrence of aCharacter, if any.  
	 */
	if (nextPosition > 0 && nextPosition < i) {
	  // We matched a second occurrence in aString, so go there.
	  itsPosition += nextPosition;
	} else {
	  itsPosition = -1;
	  // It doesn't occur in the part we matched, but may occur later.
	  for (++i, ++j; i < matchLength; ++i, ++j) {
	    if (Character.toLowerCase(buf.charAt(j)) == aCharacter) {
	      itsPosition = j;
	      break;
	    }
	  }
	}
      }
      last = in.read();
    } 
    return last >= 0;
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


  /** Holds the next non-text item, usually an Entity or an Element. */
  protected ActiveNode next;

  /** Holds the next text item. */
  protected ActiveText nextText;

  /** Holds the next unmatched end tag. */
  protected String nextEnd;

  protected abstract ActiveText getText() throws IOException;

  /** Advance to the next ``token''. <p>
   *	
   *	The next ``token'' might be either text, in <code>nextText</code>, 
   *	an ordinary Node, in <code>next</code>, or an end tag, in 
   *	<code>nextEnd</code>.  They are checked in that order. 
   */
  protected ActiveNode nextToken() {
    ActiveNode n = null;
    if (nextText == null && next == null && nextEnd == null) try {
      buf.setLength(0);
      nextText = getText();	// Try to get some text.
    } catch (IOException e) {
      return null;
    }
    if (nextText != null) {
      // === here we check for an implicitly-required paragraph tag.
      n = nextText;
      nextText = null;
    } else if (next != null) {
      n = next;
      next = null;
    } 
    return n;
  }

  /** Perform syntax checking for an end tag. 
   *	The normal action for an end tag is to set the atLast flag,
   *	causing the next call on <code>toNextSibling</code> to return null.
   *
   * @return <dl compact> 
   *		<dt>-1<dd> if it is unmatched (and should be ignored), 
   *	      	<dt> 0<dd> if it ends the current Element (normal),
   *		<dt> 1<dd> if it ends a higher level.
   *	     </dl>
   */
  protected int checkEndTag(String tag) {
    if (stack == null) {
      next = new ParseTreeComment("Bad end tag: " + tag);
      return -1;
    }
    String inside = stack.getTagName();
    if (caseFoldTagnames) {
      tag = tag.toLowerCase();
      inside = inside.toLowerCase();
    }
    if (tag == null || tag.equals(inside)) {
      // Current tag.  Everything's fine.
      return 0;
    } if (insideElement(tag, caseFoldTagnames)) {
      // ... Yes, we're OK.  End the current element.
      return 1;
    } else {
      // ... Bad nesting.  Change next to an appropriate comment.
      next = new ParseTreeComment("Bad end tag: " + tag);
      return -1;
    }
  }


  /** Perform syntax checking for a start tag or other node. 
   *
   * @return <dl compact> 
   *		<dt>-1<dd> if a different element should be started first.
   *			   (The handler will tell us which one.)
   *			   (currently unimplemented.)
   *	      	<dt> 0<dd> if it is acceptable in this context (normal),
   *		<dt> 1<dd> if it ends the current Element.
   *	     </dl>
   */
  protected int checkStartTag(ActiveNode n) {
    // need to check for implicitEnd
    ActiveElement e = n.asElement();
    if (e == null) {
      // === no check for a node starting a different element;
      // === the text check is done in nextToken.
      return 0;
    }
    String tag = getTagName(depth-1);
    if (tag != null && e.getSyntax().implicitlyEnds(tag)) return 1;
    if (tag != null && !e.getSyntax().isChildOf(tag)) return 1;

    return 0;			// === checkStartTag doesn't work yet.
  }


  /************************************************************************
  ** Node Creation:
  ************************************************************************/
  
  /** Creates an ActiveElement; otherwise identical to CreateElement. 
   */
  protected ActiveElement createActiveElement(String tagname,
					      AttributeList attributes,
					      boolean hasEmptyDelim) {
    if (tagset == null) {
      ActiveElement e = new ParseTreeElement(tagname, attributes);
      if (hasEmptyDelim) e.setHasEmptyDelimiter(hasEmptyDelim);
      e.setIsEmptyElement(e.getSyntax().isEmptyElement(e));
      return e;
    } else {
      return tagset.createActiveElement(tagname, attributes, hasEmptyDelim);
    }
  }

  /** Creates an ActiveNode of arbitrary type with (optional) data.
   */
  protected ActiveNode createActiveNode(int nodeType, String data) {
    return createActiveNode(nodeType, null, data);
  }

  /** Creates an ActiveNode of arbitrary type with name and (optional) data.
   */
  protected ActiveNode createActiveNode(int nodeType,
					String name, String data) {
    ActiveNode n = (tagset == null)
      ? Create.createActiveNode(nodeType, name, data)
      : tagset.createActiveNode(nodeType, name, data);
    return n;
  }

  /** Creates an ActiveText node.  Otherwise identical to createText.
   */
  protected ActiveText createActiveText(String text, boolean isIgnorable) {
    ActiveText n = (tagset == null)
      ? new ParseTreeText(text, isIgnorable)
      : tagset.createActiveText(text, isIgnorable);
    n.setAction(n.getSyntax().getActionForNode(n));
    return n;
  }

  /** Creates an ActiveText node.  Includes the <code>isWhitespace</code>
   *	flag, which would otherwise have to be tested for.
   */
  protected ActiveText createActiveText(String text,
					boolean isIgnorable,
					boolean isWhitespace) {
    ActiveText n = (tagset == null)
      ? new ParseTreeText(text, isIgnorable, isWhitespace)
      : tagset.createActiveText(text, isIgnorable, isWhitespace);
    n.setAction(n.getSyntax().getActionForNode(n));
    return n;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  protected void initialize() { advanceParser(); }

  public AbstractParser() {
    if (isIdent == null) initializeTables();
  }

  public AbstractParser(InputStream in) {
    this.in = new InputStreamReader(in);
    if (isIdent == null) initializeTables();
  }

  public AbstractParser(Reader in) {
    this.in = in;
    if (isIdent == null) initializeTables();
  }

  /************************************************************************
  ** Traversal:
  ************************************************************************/
  
  /** Advance the parser to the next Node in the input. 
   *	Used internally in cases where we know there is no possible
   *	sibling, i.e. most of the time.
   */
  protected Node advanceParser() {
    int check;
    // Need to know whether we've seen all the children...
    ActiveNode n = nextToken();
    if (n != null) {
      check = checkStartTag(n);
      if (check == 0) {
	setNode(n);
	if (retainTree) {
	  if (depth > 0) {
	    Node parent = getNode(depth-1);
	    if (parent != null) Copy.appendNode(n, parent);
	  } else {
	    if (document != null) Copy.appendNode(n, document);
	  }
	} 
	atLast = false;
	return n;
      } else {			// === checkStartTag -> -1 not supported
	atLast = true;
	next = n;		// put the start tag back for next time
	return null;
      }
    } else if (nextEnd != null) {
      // End tag, in nextEnd.
      check = checkEndTag(nextEnd);
      if (check < 0) { 
	n = next;
	next = null;
	nextEnd = null;
	return n;
      }
      // If the end tag terminates THIS level, we're done with it.
      if (check == 0) nextEnd = null;
      atLast = true;
      return null;
    } else {
      // end of file. 
      return null;
    }
  }

  /** toNextSibling has to check for the possibility that we already
   *	have the node in question; this happens when we're in an attribute 
   *	list.  It might also happen if we somehow manage to parse several
   *	nodes at once. 
   */
  public Node toNextSibling() {
    // If the current node has a next sibling, just go there.
    Node nn = getNode().getNextSibling();
    if (nn != null) {		// There's a sibling already.  Go there.
      setNode(nn);
      return nn;
    } else {			// No sibling in the tree: get one
      // Check for, and consume, any unparsed children.
      if (hasUnparsedChildren() && !sawChildren) {
	Node c = toFirstChild();
	if (c != null) {
	  for (; c != null; c = toNextSibling()) {}
	  toParent();
	}
      }
      return advanceParser();
    }
  }

  public Node toFirstChild() {
    pushInPlace();
    if (node.hasChildren()) {
      setNode(node.getFirstChild());
      return node;
    } else {
      return advanceParser();
    }
  }

  public Node toNextNode() {
    // Actually need a bit to say whether we should start the children.
    Node n = toNextSibling();
    if (n == null) {
      if (toParent() == null) return null;
      return toNextSibling();
    } else {
      return n;
    }
  }
  
  public boolean atLast() {
    return atLast;
  }

  /** Test whether the current node has children that have not been parsed. */
  protected boolean hasUnparsedChildren() {
    return !node.hasChildren()
      && element != null && !active.asElement().isEmptyElement();
  }

  public boolean hasChildren() {
    return node.hasChildren()
      || (element != null && !active.asElement().isEmptyElement());
  }

  public Node getTree() {
    return retainTree? getNode() : null;
  }

  public void retainTree() {
    retainTree = true;
  }
}
