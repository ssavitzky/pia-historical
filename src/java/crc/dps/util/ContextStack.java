////// BasicContext.java: A linked-list stack of current nodes.
//	$Id$

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/


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
    if (binding == null) return null;
    return binding.getValueNodes(this);
  }

  /** Set the value of an entity. 
   */
  public void setEntityValue(String name, NodeList value, boolean local) {
    ActiveEntity binding = getEntityBinding(name, local);
    if (binding != null) {
      binding.setValueNodes(this, value);
    } else {
      if (entities == null && (local || nameContext == null))
	entities = new BasicEntityTable();
      Tagset ts = this.getTopContext().getTagset();
      getEntities().setBinding(name, ts.createActiveEntity(name, value));
    } 
  }

  /** Get the binding (Entity node) of an entity, given its name. 
   * @return <code>null</code> if the entity is undefined.
   */
  public ActiveEntity getEntityBinding(String name, boolean local) {
    ActiveEntity ent = (entities == null)
      ? null : entities.getEntityBinding(name);
    if (debug() && ent != null) 
      debug("Binding found for " + name + " " + ent.getClass().getName());
    return (local || ent != null || nameContext == null)
      ? ent : nameContext.getEntityBinding(name, local);
  }

  /** Get the namespace containing an entity, given its name. 
   * @return <code>null</code> if the entity is undefined.
   */
  public Namespace locateEntityBinding(String name, boolean local) {
    ActiveEntity ent = (entities == null)
      ? null : entities.getEntityBinding(name);
    if (ent != null) return entities;
    return (local || nameContext == null)
      ? null : nameContext.locateEntityBinding(name, local);
  }

  /** Set the binding (Entity node) of an entity, given its name. 
   *	Note that the given name may include a namespace part. 
   */
  public void setEntityBinding(String name, ActiveEntity ent, boolean local) {
    Namespace ns = locateEntityBinding(name, local);
    if (ns == null) {
      if (entities == null && (local || nameContext == null))
	entities = new BasicEntityTable();
      ns = getEntities();
    } 
    ns.setBinding(name, ent);
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

  public final boolean debug() { return verbosity >= 2; }

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
