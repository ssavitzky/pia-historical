////// BasicContext.java: A linked-list stack of current nodes.
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.aux;

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
  protected int verbosity = 0;


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
  ** Debugging:
  **	This is a subset of crc.util.Report.
  ************************************************************************/

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

  public String logNode(Node aNode) {
    switch (aNode.getNodeType()) {
    case crc.dom.NodeType.ELEMENT:
      Element e = (Element)aNode;
      AttributeList atts = e.getAttributes();
      return "<" + e.getTagName()
	+ ((atts != null && atts.getLength() > 0)? " " + atts.toString() : "")
	+ ">";

    case crc.dom.NodeType.TEXT: 
      Text t = (Text)aNode;
      return t.getIsIgnorableWhitespace()
	? "space"
	: ("text: '" + logString(t.getData()) + "'");

    default: 
      return aNode.toString();      
    }
  }

  public String logString(String s) {
    if (s == null) return "null";
    String o = "";
    int i = 0;
    for ( ; i < s.length() && i < 15; ++i) {
      char c = s.charAt(i);
      switch (c) {
      case '\n': o += "\\n"; break;
      default: o += c;
      }
    }
    if (i < s.length()) o += "..."; 
    return o;
  }


  public void setDebug() 	{ verbosity = 2; }
  public void setVerbose() 	{ verbosity = 1; }
  public void setNormal() 	{ verbosity = 0; }
  public void setQuiet() 	{ verbosity = -1; }


  /************************************************************************
  ** Construction and Copying:
  ************************************************************************/

  protected void copy(ContextStack old) {
    input = old.input;
    output = old.output;
    entities = old.entities;
    stack = old.stack;
    verbosity = old.verbosity;
  }

  public ContextStack() {}

  public ContextStack(Context prev, Input in, Output out, EntityTable ents) {
    stack    = prev;
    input    = in;
    output   = out;
    entities = ents;
    verbosity = prev.getVerbosity();
    depth    = prev.getDepth() + 1;
  }


}
