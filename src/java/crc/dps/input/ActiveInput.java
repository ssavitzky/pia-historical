////// AbstractInput.java: Input abstract base class
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.input;

import crc.dom.Node;
import crc.dom.Element;
import crc.dom.Attribute;

import crc.dps.*;
import crc.dps.aux.*;
import crc.dps.active.*;


/**
 * An abstract base class for implementations of the Input interface
 *	that operate on ActiveNode objects.<p>
 *
 *	The assumption that an ActiveInput is restricted to parse trees
 *	makes for a considerable gain in efficiency. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 * 
 * @see crc.dps.Processor
 */

public abstract class ActiveInput extends CurrentActive implements Input {
  public Node toNextNode() 		{ return super.toNextNode(); }
  public Node toNextSibling() 		{ return super.toNextSibling(); }
  public Node toFirstChild() 		{ return super.toFirstChild(); }
  public Attribute toFirstAttribute() 	{ return super.toFirstAttribute(); }
  public Attribute toNextAttribute() 	{ return super.toNextAttribute(); }
  public boolean atFirst() 		{ return super.atFirst(); }
  public boolean atLast() 		{ return super.atLast(); }
  public boolean hasChildren() 		{ return super.hasChildren(); }
  public boolean hasAttributes() 	{ return super.hasAttributes(); }
  public Node getTree() 		{ return super.getTree(); }
}
