////// NodeType.java: Document Processor basic implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;

/** Extension of crc.dom.NodeType for internally-used types. 
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Token
 */

public class NodeType extends crc.dom.NodeType {
  public static final int TOKENLIST = -1;
  public static final int ENTITYREF = -2;
}
