////// BasicProcessor.java: Document Processor basic implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Element;

import crc.dps.aux.*;

/**
 * A minimal implementation for a document Processor. <p>
 *
 *	BasicProcessor extends ParseStack, which makes it somewhat
 *	more efficient to maintain the corresponding information.  It
 *	implements the Input interface but not InputStack; if it did,
 *	there would be conflicts between the Input stack that it
 *	<em>uses</em> and any that it might be linked <em>onto</em> as
 *	part of a chain of processors. <p>
 *
 * ===	In order to generate a parse tree, we need to start with 
 *	<code>node</code> being the Document.
 *
 * ===	The bottom thing on the input stack needs to be a guard that makes
 *	sure that all unmatched start tags get ended.
 *
 * === NOTE: Both Parser and Processor need DTD and parse stack info. ===
 * === it's up to the Parser to associate Handler, etc. with Token. ===
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Token
 * @see crc.dps.Input */

public class BasicProcessor extends ContextStack implements Processor {


  /************************************************************************
  ** Processing:
  ************************************************************************/

  /** Process the current Node */
  public void processNode() {
    Action action = input.getAction();
    if (action != null) {
      additionalAction(action.action(input, this, output));
    } else {
      expandNode();
    }
  }

  /** Perform any additional action requested by the action routine. */
  protected final void additionalAction(int flag) {
    switch (flag) {
    case -1: copyNode(); return;
    case  0: return;
    case  1: expandNode(); return;
    }
  }

  public void expandNode() {
    Node node = input.getNode();
    if (node.getNodeType() == NodeType.ENTITY) {
      // === expand entity.  Very simple:  copy value to output.
      output.putNode(node);	// === for now ===
    } else if (input.hasChildren() || input.hasActiveAttributes()) {
      if (/*===*/false && input.hasActiveAttributes()) {
	output.startElement(input.getElement());
	processAttributes();
      } else {
	output.startNode(node);
      }
      if (input.hasChildren()) processChildren();
      // === if element and no end tag, need endElement(true)
      output.endNode();
    } else {
      output.putNode(node);
    }
  }

  public void copyNode() {
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

  /** Process the children of the current Node */
  public void copyChildren() {
    for (Node node = input.toFirstChild() ;
	 node != null;
	 node = input.toNextSibling()) {
      copyNode();
    }
    input.toParent();
  }

  /** Process the attributes  of the current Node */
  public void processAttributes() {
    for (Node node = input.toFirstAttribute() ; node != null;
	 node = input.toNextAttribute()) { 
      processNode();
    }
    input.toParentElement();
  }


  /************************************************************************
  ** Input and Output:
  ************************************************************************/

  protected Input input = null;
  public Input getInput() { return input; }
  public void setInput(Input anInput) { input = anInput; }

  protected Output output = null;
  public Output getOutput() { return output; }
  public void setOutput(Output anOutput) { output = anOutput; }


  /************************************************************************
  ** Processing:
  ************************************************************************/

  protected boolean running;

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
  ** Debugging:
  **	This is a subset of crc.util.Report.
  ************************************************************************/

  protected int verbosity = 0;

  public int getVerbosity() { return verbosity; }
  public void setVerbosity(int value) { verbosity = value; }

  public void debug(String message) {
    if (verbosity >= 2) System.err.print(message);
  }

  public void debug(String message, int indent) {
    if (verbosity < 2) return;
    String s = "";
    for (int i = 0; i < indent; ++i) s += " ";
    s += message;
    System.err.print(s);
  }

  public void setDebug() 	{ verbosity = 2; }
  public void setVerbose() 	{ verbosity = 1; }
  public void setNormal() 	{ verbosity = 0; }
  public void setQuiet() 	{ verbosity = -1; }

}
