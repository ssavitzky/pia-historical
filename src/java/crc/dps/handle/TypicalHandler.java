////// TypicalHandler.java: Node Handler typical implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Element;
import crc.dom.DOMFactory;

import crc.dps.*;
import crc.dps.active.*;

/**
 * Handler for <>....</>  <p>
 *
 *	This is a sample implementation of a specialized subclass of
 *	GenericHandler for the ``typical'' case.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Context
 * @see crc.dps.Processor
 * @see crc.dps.Handler
 * @see crc.dps.handle.GenericHandler
 * @see crc.dps.Tagset
 * @see crc.dps.BasicTagset
 * @see crc.dps.Token
 * @see crc.dps.Input 
 * @see crc.dom.Node
 */

public class TypicalHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** This will normally be the only thing to customize. */
  public void action(Input in, Context c, Output out) {

  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public TypicalHandler() {
    /* Expansion control: */
    parseContent = true;	// false 	Build parse tree?
    expandContent = true;	// false	Expand content?
    passElement = false;	// true 	pass while expanding?
    noCopyNeeded = false;	// true 	don't copy parse tree?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    elementSyntax = -1;			// -1: non-empty 1: empty 0: check
  }
}
