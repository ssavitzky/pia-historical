////// ToDocument.java: Token output Stream to Document
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.output;

import crc.dps.Token;
import crc.dps.Output;
import crc.dps.AbstractOutput;

import crc.dom.Node;
import crc.dom.Document;
import crc.dom.DOMFactory;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * The basic implementation for a consumer of Token objects.<p>
 *
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Token
 * @see crc.dps.Input
 * @see crc.dps.Processor
 */

public class ToDocument extends AbstractOutput {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected DOMFactory factory = null;
  protected Node current = null;
  protected Document root = null;


  /************************************************************************
  ** Pull Mode Operations:
  ************************************************************************/

  /** Accepts the next token from the associated Processor, and splices
   *	it into the Document under construction. 
   *
   *	@return <code>true</code> if the Output is willing to accept more
   *		output, <code>false</code> to pause the Processor.
   */
  public boolean nextToken(Token theToken) {
    
    return true;
  }

  /** Informs the Output that no more Tokens are available.
   */
  public void endOutput() {
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Output tokens to a given Document. */
  public ToDocument(Document doc) {
    root = doc;
  }

  /** Output tokens to a Document created using a DOMFactory. */
  public ToDocument(DOMFactory aFactory) {
    factory = aFactory;
  }
}
