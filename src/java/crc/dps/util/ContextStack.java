////// BasicContext.java: A linked-list stack of current nodes.
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.aux;

import java.io.PrintStream;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Element;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.Text;

import crc.dps.*;
import crc.dps.active.*;

/**
 * A stack frame for a linked-list stack of current nodes. 
 *	It is designed to be used for saving state in a Cursor that is
 *	not operating on a real parse tree.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 * 
 * @see crc.dps.Cursor
 */
public class ContextStack  implements Context {

  /************************************************************************
  ** Sub-processing:
  ************************************************************************/

  public Context newContext() {
    return new ContextStack(input, this, output, entities);
  }

  public Processor subProcess(Input in, Output out) {
    return new BasicProcessor(in, this, out, entities);
  }

  public Processor subProcess(Input in, Output out, EntityTable entities) {
    return new BasicProcessor(in, this, out, entities);
  }

  /************************************************************************
  ** State:
  ************************************************************************/

  protected Context stack = null;
  protected int depth = 0;
  protected EntityTable entities;
  protected Input input;
  protected Output output;
  protected int verbosity = 0;
  protected TopContext top = null;
  protected PrintStream log = System.err;


 /************************************************************************
  ** Accessors:
  ************************************************************************/

  public EntityTable getEntities() { return entities; }
  public void setEntities(EntityTable bindings) { entities = bindings; }

  public Input getInput() { return input; }
  public void  setInput(Input in) { input = in; }

  public Output getOutput() { return output; }
  public void  setOutput(Output out) { output = out; }

  public int getDepth() { return depth; }
  public Context getPreviousContext() { return stack; }
  public TopContext getTopContext() { return top; }


  /************************************************************************
  ** Bindings:
  ************************************************************************/

  /** Get the value of an entity, given its name. 
   * @return <code>null</code> if the entity is undefined.
   */
  public NodeList getEntityValue(String name, boolean local) {
    EntityTable ents = getEntities();
    debug("Looking up " + name + (local? " locally" : " globally")
	  + (ents == null? " NO TABLE" : "") + "\n");
    return (ents == null)? null : ents.getEntityValue(name, local);
  }

  /** Get the value of an index, i.e. a dotted list of entity names. 
   * @return <code>null</code> if the value is undefined.
   */
  public NodeList getIndexValue(String index) {
    EntityTable ents = getEntities();
    // === getIndexValue currently broken ===
    debug("Looking up index " + index
	  + (ents == null? " NO TABLE" : "") + "\n");
    return (ents == null)? null : ents.getEntityValue(index, false);
  }

  /** Set the value of an entity. 
   */
  public void setEntityValue(String name, NodeList value, boolean local) {
    EntityTable ents = getEntities();
    EntityTable context = (stack == null)? null : stack.getEntities();
    if (ents == null || (local && ents == context)) {
      // Either there is no entity table, or we need a local one.
      // In either case,  make a new one.
      ents = new BasicEntityTable(ents);
      setEntities(ents);
    }
    ents.setEntityValue(name, value, local);
  }

  /** Set the value of an index, i.e. a dotted list of entity names. 
   */
  public void setIndexValue(String index, NodeList value) {
    // === getIndexValue currently broken ===
    setEntityValue(index, value, false);
  }



  /************************************************************************
  ** Debugging:
  **	This is a subset of crc.util.Report.
  ************************************************************************/

  public int 	getVerbosity() 		{ return verbosity; }
  public void 	setVerbosity(int value) { verbosity = value; }
  public PrintStream getLog() 		{ return log; }
  public void 	setLog(PrintStream stream) { log = stream; }

  public void message(int level, String text, int indent, boolean endline) {
    if (verbosity < level) return;
    String s = "";
    for (int i = 0; i < indent; ++i) s += " ";
    s += text;
    if (endline) log.println(s); else log.print(s);
  }

  public final void debug(String message) {
    if (verbosity >= 2) log.print(message);
  }

  public final void debug(String message, int indent) {
    if (verbosity < 2) return;
    String s = "";
    for (int i = 0; i < indent; ++i) s += " ";
    s += message;
    log.print(s);
  }

  public String logNode(Node aNode) { return Log.node(aNode); }
  public String logString(String s) { return Log.string(s); }


  /************************************************************************
  ** Construction and Copying:
  ************************************************************************/

  protected void copy(ContextStack old) {
    input 	= old.input;
    output 	= old.output;
    entities 	= old.entities;
    stack 	= old.stack;
    verbosity 	= old.verbosity;
    top 	= old.top;
    log 	= old.log;
  }

  public ContextStack() {}

  public ContextStack(Input in, Context prev, Output out, EntityTable ents) {
    stack    	= prev;
    input    	= in;
    output   	= out;
    entities 	= ents;
    top	     	= prev.getTopContext();
    verbosity 	= prev.getVerbosity();
    log	      	= prev.getLog();
    depth    	= prev.getDepth() + 1;
  }


}
