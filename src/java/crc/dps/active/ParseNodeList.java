////// ParseNodeList.java: ActiveNodeList implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.active;

import crc.dom.ArrayNodeList;
import crc.dom.NodeList;
import crc.dom.Node;
import crc.dom.NodeEnumerator;

import java.util.Enumeration;

/**
 * A list or sequence of ActiveNode objects.  
 *
 *	The contents need not be children of the same Node.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Token
 * @see crc.dom.Node
 */
public class ParseNodeList extends ArrayNodeList
implements ActiveNodeList, java.io.Serializable {

  /**
   * Returns the indexth item in the collection, as a ActiveNode.
   * 	If index is greater than or equal to the number of nodes in the
   * 	list, null is returned.  
   *
   * @return a ActiveNode at index position.
   * @param index Position to get node.
   */
  public ActiveNode activeNodeAt(long index) { 
    try {
      return (ActiveNode)item(index);
    } catch (crc.dom.NoSuchNodeException e) {
      return null;
    }
  }

  /** Append a new ActiveNode.
   */
  public void append(ActiveNode newChild) { append((Node)newChild); }

  public ParseNodeList() {}

  public ParseNodeList(ActiveNode initialChild) {
    super((Node)initialChild);
  }
  public ParseNodeList(NodeList initialChildren) {
    super(initialChildren);
  }

  public ParseNodeList(Enumeration elements) {
    while (elements.hasMoreElements()) {
      Object o = elements.nextElement();
      if (o instanceof ActiveNode) append((ActiveNode) o);
      else append(new ParseTreeText(o.toString()));
    }
  }

  public ParseNodeList(NodeEnumerator elements) {
    for (Node n = elements.getFirst(); n != null; n = elements.getNext()) {
      if (n instanceof ActiveNode) append((ActiveNode) n);
    }
  }
}
