////// BasicProcessor.java: Document Processor basic implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.Element;
import crc.dom.Entity;
import crc.dom.Text;

import crc.dps.aux.*;
import crc.dps.active.*;
import crc.dps.output.ToNodeList;

/**
 * A minimal implementation for a document Processor. <p>
 *
 * === NOTE: Both Parser and Processor need DTD and parse stack info. ===
 * === it's up to the Parser to associate Handler, etc. with Token. ===
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
  ** Processing:
  ************************************************************************/

  /** Process the current Node */
  public void processNode() {
    Action action = input.getAction();
    if (action != null) {
      debug("!! calling action for " + logNode(input.getNode()) + "\n");
      debug("   action class name: " + action.getClass().getName() + "\n");
      additionalAction(action.action(input, this));
    } else {
      debug("!! default action for " + logNode(input.getNode()) + "\n");
      expandCurrentNode();
    }
  }

  /** Perform any additional action requested by the action routine. */
  protected final void additionalAction(int flag) {
    debug("   -> " + Action.actionNames[flag+1] + " (" + flag + ")\n");
    switch (flag) {
    case Action.COPY_NODE: copyCurrentNode(); return;
    case Action.COMPLETED: return;
    case Action.EXPAND_NODE: expandCurrentNode(); return;
    }
  }

  /** Process the current node in the default manner, expanding entities
   *	in its attributes and processing its children re-entrantly.
   */
  public void expandCurrentNode() {
    Node node = input.getNode();
    if (node.getNodeType() == NodeType.ENTITY) {
      expandEntity((Entity) node, output);
    } else if (input.hasChildren() || input.hasActiveAttributes()) {
      if (input.hasActiveAttributes()) {
	ActiveElement oe = input.getActive().asElement();
	ActiveElement e = new ParseTreeElement(oe,
					       expandAttrs(oe.getAttributes()));
	//debug("Starting element" + logNode(e) + "\n");
	output.startElement(e);
	if (input.hasChildren()) { debug("children\n"); processChildren(); }
	output.endElement(e.isEmptyElement() || e.implicitEnd());
	//debug("  done\n");
      } else {
	output.startNode(node);
	if (input.hasChildren()) processChildren();
	output.endNode();
      }
    } else {
      output.putNode(node);
    }
  }

  /** Copy the current node to the output.  
   *
   *	This is done by recursively traversing its children.
   */
  public void copyCurrentNode() {
    if (input.hasChildren()) {
      output.startNode(input.getNode());
      copyChildren();
      output.endNode();
    } else {
      output.putNode(input.getNode());
    }
  }

  /** Process the children of the current Node */
  public void processChildren() {
    for (Node node = input.toFirstChild() ;
	 node != null;
	 node = input.toNextSibling()) {
      processNode();
    }
    input.toParent();
  }

  /** Copy the children of the current Node */
  public void copyChildren() {
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
    Util.copyNodes(nl, output);
  }

  /** Expand entities in the attributes of the current Node.
   */
  public AttributeList expandAttrs(AttributeList attrs) {
    return Util.expandAttrs(this, attrs);
  }

  /** Expand entities in the value of a given attribute. */
  public void expandAttribute(Attribute att,  ActiveElement e) {
    e.setAttribute(att.getName(), expandNodes(att.getValue()));
  }

  /** Expand nodes in a nodelist. */
  public NodeList expandNodes(NodeList nl) {
    debug("*** Null value expanded\n");
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
    if (name.indexOf('.') >= 0) value = getIndexValue(name);
    else 
      value = getEntityValue(name);
    if (value == null) {
      dst.putNode(n);
    } else {
      Util.copyNodes(value, dst);
    }
  }

  /************************************************************************
  ** Processing:
  ************************************************************************/

  protected boolean running = false;

  public boolean isRunning() { return running; }
  public void stop() { running = false; }

  /** Run the Processor, pushing a stream of Token objects at its
   *	registered Output, until we either run out of input or the 
   *	<code>isRunning</code> flag is turned off.
   */
  public void run() {
    running = true;
    processNode();
    while (running && input.toNextSibling() != null) processNode();
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public BasicProcessor() {}

  public BasicProcessor(Input in, Context prev, Output out,
			EntityTable ents) {
    super(prev, in, out, ents);
  }

  public BasicProcessor(Input in, Context prev, Output out) {
    this(in, prev, out, prev.getEntities());
  }

  /** Return a new BasicProcessor copied from an old one. */
  public BasicProcessor(BasicProcessor p) {
    input = p.input;
    output = p.output;
    entities = p.entities;
    stack = p;
  }

  /** Create a sub-processor with a given input and output. */
  public Processor subProcess(Input in, Output out) {
    return new BasicProcessor(in, this, out, entities);
  }

  /** Create a sub-processor with a given output. 
   *
   *	Commonly used to obtain an expanded version of the attributes
   *	and content of the parent's current node.
   */
  public Processor subProcess(Output out) {
    return new BasicProcessor(input, this, out, entities);
  }
}
