////// ParseTreeAttribute.java -- implementation of ActiveAttribute
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.active;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.DOMFactory;

import crc.dom.BasicAttribute;
import crc.dom.Attribute;

import crc.dps.*;

/**
 * An implementation of the ActiveAttribute interface, suitable for use in 
 *	DPS parse.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dom.Node
 * @see crc.dps.active.ActiveNode
 */
public class ParseTreeAttribute extends BasicAttribute
	implements ActiveAttribute
{

  /************************************************************************
  ** Instance Variables:
  ************************************************************************/

  protected Handler handler = null;
  protected Action  action  = null;

  /************************************************************************
  ** ActiveNode interface:
  ************************************************************************/

  public Syntax getSyntax() 		{ return handler; }
  public Action getAction() 		{ return action; }
  public Handler getHandler() 		{ return handler; }

  public void setAction(Action newAction) { action = newAction; }

  public void setHandler(Handler newHandler) {
    handler = newHandler;
    action  = handler;
  }

  // At most one of the following will return <code>this</code>:

  public ActiveElement	 asElement() 	{ return null; }
  public ActiveText 	 asText()	{ return null; }
  public ActiveAttribute asAttribute() 	{ return this; }
  public ActiveEntity 	 asEntity() 	{ return null; }
  public ActiveDocument  asDocument() 	{ return null; }

  /** Append a new child.
   *	Can be more efficient than <code>insertBefore()</code>
   */
  public void addChild(ActiveNode newChild) {
    insertAtEnd((crc.dom.AbstractNode)newChild);
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public ParseTreeAttribute() {
    super(null, null);
  }

  public ParseTreeAttribute(ParseTreeAttribute e) {
    super((BasicAttribute)e);
    handler = e.handler;
    action = e.action;
  }

  /** Construct a node with given data. */
  public ParseTreeAttribute(String name, NodeList value) {
    super(name, value);
  }

  /** Construct a node with given data and handler. */
  public ParseTreeAttribute(String name, NodeList value, Handler handler) {
    super(name, value);
    setHandler(handler);
  }


  /************************************************************************
  ** Presentation:
  ************************************************************************/

  public String startString() {
    return getName() + ((! getSpecified() || getValue() == null)? "" : "=");
  }

  public String contentString() {
    return (! getSpecified() || getValue() == null)
      ? ""
      : "'" + getValue().toString() + "'";
  }

  public String endString() {
    return "";
  }


  public String toString() {
    return startString() + contentString() + endString();
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Token. 
   *	Since an attribute's value is kept in its children, we actually
   *	need to do a deep copy.
   */
  public ActiveNode shallowCopy() {
    return new ParseTreeAttribute(this);
  }

  /** Return a deep copy of this Token.  Attributes and children are copied.
   */
  public ActiveNode deepCopy() {
    return new ParseTreeAttribute(this);
  }

  /** Return new node corresponding to this Token, made using the given 
   *	DOMFactory.  Children <em>are not</em> copied.
   */
  public Node createNode(DOMFactory f) {
    Attribute e = f.createAttribute(getName(), getValue()); 
    return e;
  }

}