////// BasicTokenList.java: Token List implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;

import crc.dom.ArrayNodeList;
import crc.dom.Node;

/**
 * A list or sequence of Token objects.  
 *
 *	The contents need not be children of the same Token.
 *
 * ===	This probably needs to be a NodeList. 
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Token
 * @see crc.dom.Node
 */

public class BasicTokenList extends ArrayNodeList implements TokenList {

  /**
   * Returns the indexth item in the collection, as a Token.
   * 	If index is greater than or equal to the number of nodes in the
   * 	list, null is returned.  
   *
   * @return a Token at index position.
   * @param index Position to get node.
   */
  public Token tokenAt(long index) { 
    try {
      return (Token)item(index);
    } catch (crc.dom.NoSuchNodeException e) {
      return null;
    }
  }

  /** Append a new Token.
   */
  public void append(Token newChild) { append((Node)newChild); }

  public BasicTokenList() {}

  public BasicTokenList(Token initialChild) {
    super((Node)initialChild);
  }
}
