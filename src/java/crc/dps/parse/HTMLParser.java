////// HTMLParser.java: HTML-specific Parser interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.parse;

import crc.dps.NodeType;
import crc.dps.Parser;
import crc.dps.Token;
import crc.dps.TokenList;
import crc.dps.BasicToken;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.BitSet;

import java.io.Reader;
import java.io.IOException;

/**
 * A Parser specialized for HTML and HTML extended with InterForm and
 *	XML constructs. <p>
 *
 *	HTMLParser does not expect the document it is parsing to have a valid
 *	DTD; it is assumed to be HTML, possibly with InterForm and/or XML
 *	extensions.  At early stages of the implementation it will use
 *	hard-coded information about content models; it is hoped that
 *	eventually HTMLParser will use the DTD for everything. <p>
 *
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Parser

 */

public class HTMLParser extends BasicParser {

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

  public HTMLParser() {
    super();
  }

  public HTMLParser(crc.dps.InputStack previous) {
    super(previous);
  }

  public HTMLParser(java.io.InputStream in, crc.dps.InputStack previous) {
    super(in, previous);
  }

  public HTMLParser(Reader in, crc.dps.InputStack previous) {
    super(in, previous);
  }

}
