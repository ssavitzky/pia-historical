////// BasicProcessor.java: Document Processor basic implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.process;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.Element;
import crc.dom.Entity;
import crc.dom.Text;

import crc.dps.*;
import crc.dps.aux.*;
import crc.dps.active.*;
import crc.dps.output.ToNodeList;

/**
 * A minimal implementation for a document Processor. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Output
 * @see crc.dps.Input 
 * @see crc.dps.Action
 */

public class BasicProcessor extends ContextStack implements Processor {

 /************************************************************************
  ** Accessors:
  ************************************************************************/

  public Processor getProcessor() { return this; }

  /************************************************************************
  ** Processing:
  ************************************************************************/

  protected boolean running = true;

  public boolean isRunning() { return running; }
  public void stop() { running = false; }

  /** Copy nodes from the input to the output. */
  public void copy() {
    copyCurrentNode();
    while (input.toNextSibling() != null) copyCurrentNode();
  }

  /** Run the Processor, pushing a stream of Token objects at its
   *	registered Output, until we either run out of input or the 
   *	<code>isRunning</code> flag is turned off.
   */
  public boolean run() {
    running = true;
    processNode();
    while (running && input.toNextSibling() != null) processNode();
    return running;
  }

  /** Process the current Node */
  public final void processNode() {
    Action handler = input.getAction();
    if (handler != null) {
      additionalAction(handler.actionCode(input, this));
      // MUST BE equivalent to: handler.action(input, this, output);
    } else {
      expandCurrentNode();
      // MUST BE equivalent to the default action for a node.
    }
  }

  /** Process the children of the current Node */
  public final boolean processChildren() {
    for (Node node = input.toFirstChild();
	 node != null && running;
	 node = input.toNextSibling()) {
      processNode();
    }
    input.toParent();
    return running;
  }

  /** Perform any additional action requested by the action routine. */
  protected final void additionalAction(int flag) {
    //debug("   -> " + Action.actionNames[flag+1] + " (" + flag + ")\n");
    switch (flag) {
    case Action.COPY_NODE: copyCurrentNode(); return;
    case Action.COMPLETED: return;
    case Action.EXPAND_NODE: expandCurrentNode(); return;
    case Action.EXPAND_ATTS: expandCurrentAttrs(); return;
    case Action.PUT_NODE: putCurrentNode(); return;
    }
  }

  /************************************************************************
  ** Convenience and Utility Methods:
  ************************************************************************/

  /** Process the current node in the default manner, expanding entities
   *	in its attributes and processing its children re-entrantly.
   */
  public final void expandCurrentNode() {
    ActiveNode node = input.getActive();
    // No need to check for an entity; active ones use EntityHandler.
    if (input.hasActiveAttributes()) {
      ActiveElement oe = node.asElement();
      ActiveElement e = oe.editedCopy(expandAttrs(oe.getAttributes()), null);
      output.startElement(e);
      if (input.hasChildren()) { processChildren(); }
      output.endElement(e.isEmptyElement() || e.implicitEnd());
    } else if (input.hasChildren()) {
      output.startNode(node);
      if (input.hasChildren()) processChildren();
      output.endNode();
    } else {
      output.putNode(node);
    }
  }

  /** Process the current node by expanding entities in its attributes, but
   *	blindly copying its children (content).
   */
  public final void expandCurrentAttrs() {
    ActiveNode node = input.getActive();
    if (input.hasActiveAttributes()) {
      ActiveElement oe = node.asElement();
      ActiveElement e =
	new ParseTreeElement(oe, expandAttrs(oe.getAttributes()));
      output.startElement(e);
      if (input.hasChildren()) { copyChildren(); }
      output.endElement(e.isEmptyElement() || e.implicitEnd());
    } else if (input.hasChildren() && ! node.hasChildren()) {
      output.startNode(node);
      copyChildren();
      output.endNode();
    } else {
      output.putNode(node);
    }
  }

  /** Copy the current node to the output.  
   *
   *	This is done by recursively traversing its children.
   */
  public final void copyCurrentNode() {
    Node n = input.getNode();
    if (input.hasChildren() && ! n.hasChildren()) {
      // Copy recursively only if the node hasn't been fully parsed yet.
      output.startNode(n);
      copyChildren();
      output.endNode();
    } else {
      output.putNode(n);
    }
  }

  /** Copy the current node to the output.  Recursion is not needed.
   *
   *	This is commonly used for, e.g., Text nodes.
   */
  public final void putCurrentNode() {
    Node n = input.getNode();
    output.putNode(n);
  }

  /** Copy the children of the current Node */
  public final void copyChildren() {
    for (Node node = input.toFirstChild() ;
	 node != null;
	 node = input.toNextSibling()) {
      copyCurrentNode();
    }
    input.toParent();
  }

  /* === Many of the following could be done using a suitable Input. === */

  /** Copy nodes in a nodelist. */
  public void copyNodes(NodeList nl) {
    Copy.copyNodes(nl, output);
  }

  /** Expand entities in the attributes of the current Node.
   */
  public AttributeList expandAttrs(AttributeList attrs) {
    return Expand.expandAttrs(this, attrs);
  }

  /** Expand entities in the value of a given attribute. */
  public void expandAttribute(Attribute att,  ActiveElement e) {
    e.setAttributeValue(att.getName(), expandNodes(att.getValue()));
  }

  /** Expand nodes in a nodelist. */
  public NodeList expandNodes(NodeList nl) {
    if (nl == null) return null;
    ToNodeList dst = new ToNodeList();
    expandNodes(nl, dst);
    return dst.getList();
  }

  public void expandNodes(NodeList nl, Output dst) {
    crc.dom.NodeEnumerator e = nl.getEnumerator();
    for (Node n = e.getFirst(); n != null; n = e.getNext()) {
      if (n.getNodeType() == NodeType.ENTITY) {
	expandEntity((Entity) n, dst);
      } else {
	dst.putNode(n);
      }
    }
  }

  /** Expand a single entity. */
  public void expandEntity(Entity n, Output dst) {
    String name = n.getName();
    NodeList value = null;
    if (name.indexOf('.') >= 0) value = Index.getIndexValue(this, name);
    else 
      value = getEntityValue(name, false);
    if (value == null) {
      dst.putNode(n);
    } else {
      Copy.copyNodes(value, dst);
    }
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public BasicProcessor() {}

  public BasicProcessor(Input in, Context prev, Output out, EntityTable ents) {
    super(in, prev, out, ents);
  }

  public BasicProcessor(Input in, Context prev, Output out) {
    super(in, prev, out);
  }

  /** Return a new BasicProcessor copied from an old one. */
  public BasicProcessor(BasicProcessor p) {
    super(p);
    stack = p;
  }

}
