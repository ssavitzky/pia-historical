////// AbstractInput.java: Input abstract base class
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


package crc.dps.input;

import crc.dom.Node;
import crc.dom.Element;
import crc.dom.Attribute;

import crc.dps.*;
import crc.dps.util.*;
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
  public boolean atFirst() 		{ return super.atFirst(); }
  public boolean atLast() 		{ return super.atLast(); }
  public boolean hasChildren() 		{ return super.hasChildren(); }
  public boolean hasAttributes() 	{ return super.hasAttributes(); }
  public Node getTree() 		{ return super.getTree(); }
}
