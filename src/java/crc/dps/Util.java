////// Util.java: Document Processor utilities
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;

import crc.dom.Node;
import crc.dom.NodeList;

/**
 * Utilities (static methods) for a Document Processor. 
 *
 *	This class contains the static methods that apply generally to
 *	objects that implement interfaces in the Document Processor
 *	package.  This way we avoid cluttering up implementations with
 *	duplicate methods.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Token
 * @see crc.dps.Input
 */

public class Util {

  /** Append a Node to a given parent.
   *	If the new Node is already a child of <code>parentNode</code>, nothing
   *	happens.  If its parent is non-<code>null</code> and
   *	<em>different</em> from the <code>parentNode</code>, it will be
   *	reparented.  If the Node's type is NODELIST, its children will be
   *	appended (spliced in). <p>
   *
   * @see crc.dps.NodeType */
  public static void appendNode(Node aNode, Node parentNode) {
    // No node to append to: do nothing.
    if (parentNode == null) return;
    // Current node is the parent: already taken care of.
    if (aNode.getParentNode() == parentNode) return;
    // A NodeList in disguise -- append its children:
    if (aNode.getNodeType() == NodeType.NODELIST) {
      if (aNode.hasChildren()) appendNodes(aNode.getChildren(), parentNode);
      return;
    }
    try {
      // Different parent: re-parent it.
      if (aNode.getParentNode() != null) { 
	aNode.getParentNode().removeChild(aNode);
      }
      parentNode.insertBefore(aNode, null);
    } catch (crc.dom.NotMyChildException e) {
      // === not clear what to do here...  shouldn't happen. ===
    }
  }

  /** Append the nodes in a NodeList to a given parent.
   *
   * @see #appendNode
   */
  public static void appendNodes(NodeList aNodeList, Node parentNode) {
    if (aNodeList == null) return;
    crc.dom.NodeEnumerator e = aNodeList.getEnumerator();
    for (Node node = e.getFirst(); node != null; node = e.getNext()) {
      appendNode(node, parentNode);
    }
  }

  /** Expand an arbitrary Node. 
   *	This is essentially a deep copy except that Token nodes 
   *	anywhere in the tree get expanded.  
   */
  public static NodeList expandNode(Node aNode, Context c) {
    if (aNode instanceof Token) { return ((Token)aNode).expand(c); }
    else return (new BasicToken(aNode)).expand(c);
    // === This implementation is exceptionally crude and really doesn't work
    // === really need to ask the Context to do the expansion.
    // === Need to push a new Context for Elements.
  }
}
