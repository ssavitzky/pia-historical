////// Handler.java: Node Handler interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

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
