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

  // === we're assuming that a Processor is a Context and has node and handler
  // === Should the input and output also be part of the context?  Unclear.

  /** Process the current Node */
  public void processNode(Input in, Output out) {
    action = in.getAction();
    if (action != null) {
      action.action(node, this, in, out);
    } else {
      defaultNodeProcessing(in, out);
    }
  }

  // === we're going to want to split action to permit an iterative Processor:
  // === startAction, endAction.

  public void defaultNodeProcessing(Input in, Output out) {
    if (node.getNodeType() == NodeType.ENTITY) {
      // === expand entity maybe.  Very simple:  push value onto input.
    } else if (in.hasChildren() || in.hasAttributes()) {
      if (in.hasAttributes()) {
	out.startElement(getElement());
	processAttributes(in, out);
      } else {
	out.startNode(node);
      }
      // === push, or create a new Context?
      if (in.hasChildren()) processChildren(in, out);
      out.endNode();
    } else {
      out.putNode(node);
    }
  }

  /** Process the next Node. */
  public void processNextNode(Input in, Output out) {
    node = in.toNextNode();
    if (node == null) return;
    processNode(in, out);
  }

  /** Process the children of the current Node */
  public void processChildren(Input in, Output out) {
    for (node = in.toFirstChild() ; node != null; node = in.toNextNode()) {
      processNode(in, out);
    }
    in.toParent();
  }

  /** Process the attributes  of the current Node */
  public void processAttributes(Input in, Output out) {
    for (node = in.toFirstAttribute() ; node != null;
	 node = in.toNextAttribute()) { 
      processNode(in, out);
    }
    in.toParentElement();
  }

  public void process() {
    
  }

  /************************************************************************
  ** Generating Output:
  ************************************************************************/

  protected boolean running;

  public boolean isRunning() { return running; }

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

  /** Run the Processor, pushing a stream of Token objects at its
   *	registered Output, until the Output's <code>nextToken</code>
   *	method returns <code>false</code>.
   */
  public void run() {
    running = true;
    process();
    //    if (running && output != null) { output.endOutput(); }
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
