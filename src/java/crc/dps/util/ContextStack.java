////// BasicContext.java: A linked-list stack of current nodes.
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.util;

import java.io.PrintStream;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Element;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.Text;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.process.BasicProcessor;

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

  protected Context 	stack = null;
  protected int 	depth = 0;
  protected EntityTable entities = null;
  protected Input 	input;
  protected Output 	output;
  protected int 	verbosity = 0;
  protected TopContext 	top = null;
  protected PrintStream	log = System.err;

  protected Context   	nameContext = null;

 /************************************************************************
 ** Accessors:
 ************************************************************************/

  /** getEntities returns the most-local Entity namespace. */
  public EntityTable getEntities() 	{ 
    return (entities == null && nameContext != null) 
      ? nameContext.getEntities() 
      : entities;
  }
  public void setEntities(EntityTable bindings) { entities = bindings; }

  public Input getInput() 		{ return input; }
  public void  setInput(Input in) 	{ input = in; }

  public Output getOutput() 		{ return output; }
  public void  setOutput(Output out) 	{ output = out; }

  public int getDepth() 		{ return depth; }
  public Context getPreviousContext() 	{ return stack; }
  public TopContext getTopContext() 	{ return top; }
  public Context getNameContext() 	{ return nameContext; }
  public Processor getProcessor() {
    return (stack == null)? null : stack.getProcessor();
  }

  /************************************************************************
  ** Namespaces:
  ************************************************************************/

  /** Return a namespace with a given name.  If the name is null, 
   *	returns the most-locally namespace.
   */
  public Namespace getNamespace(String name) {
    if (entities != null) {
      if (name == null || name.equals(entities.getName())) {
	return entities;
      } else if (entities.containsNamespaces()) {
	ActiveNode ns = entities.getBinding(name);
	if (ns != null && ns.asNamespace() != null)
	  return ns.asNamespace();
      }
    }
    if (nameContext != null) {
      return nameContext.getNamespace(name);
    } else {
      return null;
    }
  }

  /** Return the locally-defined namespace, if any. */
  public Namespace getLocalNamespace() {
    return entities;
  }

  /************************************************************************
  ** Bindings:
  ************************************************************************/

  /** Get the value of an entity, given its name. 
   * @return <code>null</code> if the entity is undefined.
   */
  public NodeList getEntityValue(String name, boolean local) {
    ActiveEntity binding = getEntityBinding(name, local);
    return (binding != null)? binding.getValue() :  null;
  }

  /** Set the value of an entity. 
   */
  public void setEntityValue(String name, NodeList value, boolean local) {
    ActiveEntity binding = getEntityBinding(name, local);
    if (binding != null) {
      binding.setValue(value);
    } else {
      if (entities == null && (local || nameContext == null))
	entities = new BasicEntityTable();
      getEntities().setValue(name, value);
    } 
  }

  /** Get the binding (Entity node) of an entity, given its name. 
   * @return <code>null</code> if the entity is undefined.
   */
  public ActiveEntity getEntityBinding(String name, boolean local) {
    ActiveEntity ent = (entities == null)
      ? null : entities.getEntityBinding(name);
    return (local || ent != null || nameContext == null)
      ? ent : nameContext.getEntityBinding(name, local);
  }

  /** Set the binding (Entity node) of an entity, given its name. 
   *	Note that the given name may include a namespace part. 
   */
  public void setEntityBinding(String name, ActiveEntity ent, boolean local) {
    // === currently hard to implement.  Fake it. ===
    setEntityValue(name, ent.getValue(), local);
  }



  /************************************************************************
  ** Debugging:
  **	This is a subset of crc.util.Report.
  ************************************************************************/

  public int 	getVerbosity() 		{ return verbosity; }
  public void 	setVerbosity(int value) { verbosity = value; }
  public PrintStream getLog() 		 { return log; }
  public void setLog(PrintStream stream) { log = stream; }

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
    verbosity 	= old.verbosity;
    top 	= old.top;
    log 	= old.log;
  }

  public ContextStack() {}

  public ContextStack(ContextStack prev) {
    copy(prev);
    stack = prev;
    nameContext = (prev.entities == null)? prev.nameContext : prev;
    entities = null;
  }

  public ContextStack(Input in, Context prev, Output out, EntityTable ents) {
    stack    	= prev;
    input    	= in;
    output   	= out;
    entities 	= ents;
    top	     	= prev.getTopContext();
    nameContext = ((prev.getLocalNamespace() == null)?
		   prev.getNameContext() : prev);
    verbosity 	= prev.getVerbosity();
    log	      	= prev.getLog();
    depth    	= prev.getDepth() + 1;
  }

  public ContextStack(Input in, Context prev, Output out) {
    stack    	= prev;
    input    	= in;
    output   	= out;
    entities 	= null;
    top	     	= prev.getTopContext();
    nameContext = ((prev.getLocalNamespace() == null)?
		   prev.getNameContext() : prev);
    verbosity 	= prev.getVerbosity();
    log	      	= prev.getLog();
    depth    	= prev.getDepth() + 1;
  }


}
