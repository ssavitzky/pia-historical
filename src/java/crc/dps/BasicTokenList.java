////// BasicTokenList.java: Token List implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;
import crc.ds.List;

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

public class BasicTokenList extends List implements TokenList {

  /**
   * Returns the indexth item in the collection, as a Token.
   * 	If index is greater than or equal to the number of nodes in the
   * 	list, null is returned.  
   *
   * @return a Token at index position.
   * @param index Position to get node.
   */
  public Token tokenAt(long index) { return (Token)at((int)index); }

  /** Append a new Token.
   */
  public void append(Token newChild) { push(newChild); }

  /** Returns the number of Tokens in the list. */
  public long length() { return nItems(); }

  public BasicTokenList() {}
}
