////// TokenList.java: Token List interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;

/**
 * A list or sequence of Token objects.  
 *
 *	A TokenList is not necessarily a NodeList; it might be a Java
 *	Collection or crc.ds.List.  The contents are not necessarily 
 *	from the same level in the parse tree, though this is usual.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Token
 * @see crc.dom.NodeList
 * @see java.util.Collection
 * @see crc.ds.List
 */

public interface TokenList {

  /**
   * Returns the indexth item in the collection, as a Token.
   * 	If index is greater than or equal to the number of nodes in the
   * 	list, null is returned.  
   *
   * @return a Token at index position.
   * @param index Position to get node.
   */
  public Token tokenAt(long index);

  /** Append a new Token.
   */
  public void append(Token newChild);

  /** Returns the number of Tokens in the list. */
  public long length();
}
