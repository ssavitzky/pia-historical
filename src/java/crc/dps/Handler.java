////// Handler.java: Node Handler interface
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


package crc.dps;
import crc.dom.Node;
import crc.dom.NodeList;

/**
 * The interface for a Node's Handler. 
 *
 *	A Node's Handler provides all of the necessary syntactic and semantic
 *	information required for parsing, processing, and presenting a Node.
 *	As such, it is simply a combination of the Action and Syntax
 *	interfaces. <p>
 *
 *	The separation of Action and Syntax reflects the fact that the
 *	Syntax is only required during parsing, while the Action is only
 *	required during processing.  Their combination in Handler reflects
 *	the fact that the two are almost always tied together.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Processor
 * @see crc.dps.Token
 * @see crc.dps.Input 
 * @see crc.dom.Node */

public interface Handler extends Action, Syntax {

}
