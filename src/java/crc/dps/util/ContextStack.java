////// BasicContext.java: A linked-list stack of current nodes.
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.aux;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Element;
import crc.dom.Attribute;

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
  ** Context interface:
  ************************************************************************/

  public Context newContext() {
    return new ContextStack(this, input, output, entities);
  }

  public Context newContext(Input in, Output out) {
    return new ContextStack(this, in, out, entities);
  }

  /************************************************************************
  ** State:
  ************************************************************************/

  protected Context stack = null;
  protected int depth = 0;
  protected EntityTable entities;
  protected Input input;
  protected Output output;

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

  /************************************************************************
  ** Bindings:
  ************************************************************************/

  /** Get the value of an entity, given its name. 
   * @return <code>null</code> if the entity is undefined.
   */
  public NodeList getEntityValue(String name) {
    EntityTable ents = getEntities();
    return (ents == null)? null : ents.getValueForEntity(name, false);
  }

  /** Get the value of an index, i.e. a dotted list of entity names. 
   * @return <code>null</code> if the value is undefined.
   */
  public NodeList getIndexValue(String index) {
    EntityTable ents = getEntities();
    // === getIndexValue currently broken ===
    return (ents == null)? null : ents.getValueForEntity(index, false);
  }


  /************************************************************************
  ** Construction and Copying:
  ************************************************************************/

  protected void copy(ContextStack old) {
    input = old.input;
    output = old.output;
    entities = old.entities;
    stack = old.stack;
  }

  public ContextStack() {}

  public ContextStack(Context prev, Input in, Output out, EntityTable ents) {
    stack    = prev;
    input    = in;
    output   = out;
    entities = ents;
    depth    = prev.getDepth() + 1;
  }


}
