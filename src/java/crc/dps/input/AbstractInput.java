////// AbstractInput.java: Input abstract base class
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.input;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import crc.dom.Node;
import crc.dom.Element;
import crc.dom.Attribute;

import crc.dps.*;
import crc.dps.aux.*;
import crc.dps.active.*;


/**
 * An abstract base class for implementations of the Input interface
 *	that operate on generic Node objects.<p>
 *
 *	An Input is essentially a tree iterator, with the additional
 *	ability to provide handlers for the nodes it produces.  In 
 *	most cases these handlers will stored in the (active) nodes
 *	themselves. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 * 
 * @see crc.dps.Processor
 */

public abstract class AbstractInput extends CurrentNode implements Input {
  public Node toNextNode() 		{ return super.toNextNode(); }
  public Node toNextSibling() 		{ return super.toNextSibling(); }
  public Node toFirstChild() 		{ return super.toFirstChild(); }
  public boolean atFirst() 		{ return super.atFirst(); }
  public boolean atLast() 		{ return super.atLast(); }
  public boolean hasChildren() 		{ return super.hasChildren(); }
  public boolean hasAttributes() 	{ return super.hasAttributes(); }
  public Node getTree() 		{ return super.getTree(); }
}
