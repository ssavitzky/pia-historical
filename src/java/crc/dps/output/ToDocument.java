////// ToDocument.java: Token output Stream to Document
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.output;

import crc.dps.Output;

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
