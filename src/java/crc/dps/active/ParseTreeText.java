////// ParseTreeText.java -- implementation of ActiveText
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.active;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.ArrayNodeList;
import crc.dom.NodeEnumerator;
import crc.dom.DOMFactory;
import crc.dom.Element;
import crc.dom.BasicText;

import crc.dom.Text;
import crc.dom.Comment;
import crc.dom.PI;

import crc.dps.*;
import crc.dps.aux.*;

/**
 * An implementation of the ActiveText interface, suitable for use in 
 *	DPS parse.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dom.Node
 * @see crc.dps.Context
 * @see crc.dps.Processor
 */
public class ParseTreeText extends BasicText implements ActiveText {

  /************************************************************************
  ** Instance Variables:
  ************************************************************************/

  protected Handler handler = null;
  protected Action  action  = null;

  /** flag for whether the Text is whitespace. */
  protected boolean isWhitespace = false;


  /************************************************************************
  ** ActiveNode interface:
  ************************************************************************/

  public Syntax getSyntax() 		{ return handler; }
  public Action getAction() 		{ return action; }
  public Handler getHandler() 		{ return handler; }

  public void setAction(Action newAction) { action = newAction; }

  public void setHandler(Handler newHandler) {
    handler = newHandler;
    // === used to call newHandler.getHandlerForToken
    action  = handler;
  }

  // Exactly one of the following will return <code>this</code>:

  public ActiveElement	 asElement() 	{ return null; }
  public ActiveText 	 asText()	{ return this; }
  public ActiveAttribute asAttribute() 	{ return null; }
  public ActiveEntity 	 asEntity() 	{ return null; }
  public ActiveDocument  asDocument() 	{ return null; }

  /** Append a new child.
   *	Can be more efficient than <code>insertBefore()</code>
   */
  public void addChild(ActiveNode newChild) {
    insertAtEnd((crc.dom.AbstractNode)newChild);
  }

  /************************************************************************
  ** ActiveText interface:
  ************************************************************************/

  public boolean getIsWhitespace() { return isWhitespace; }
  public void setIsWhitespace(boolean value) { isWhitespace = value; }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct a node with all fields to be filled in later. */
  public ParseTreeText() {
  }

  public ParseTreeText(ParseTreeText e) {
    super(e);
    handler = e.handler;
    action = e.action;
    isWhitespace = e.isWhitespace;
  }

  /** Construct a node with given data. */
  public ParseTreeText(String data) {
    super(data);
    setIsWhitespace(Test.isWhitespace(data));
  }

  /** Construct a node with given data and handler. */
  public ParseTreeText(String data, Handler handler) {
    super(data);
    setIsWhitespace(Test.isWhitespace(data));
    setHandler(handler);
  }

  /** Construct a node with given data, flags, and handler. */
  public ParseTreeText(String data, boolean isIgnorable,
		       boolean isWhitespace, Handler handler) {
    super(data);
    setIsIgnorableWhitespace(isIgnorable);
    setIsWhitespace(isWhitespace);
    setHandler(handler);
  }

  /** Construct a node with given data, flags, and handler. */
  public ParseTreeText(String data, boolean isIgnorable,
		       boolean isWhitespace) {
    super(data);
    setIsIgnorableWhitespace(isIgnorable);
    setIsWhitespace(isWhitespace);
  }

  /** Construct a node with given data, flags, and handler. */
  public ParseTreeText(String data, boolean isIgnorable) {
    super(data);
    setIsIgnorableWhitespace(isIgnorable);
    setIsWhitespace(Test.isWhitespace(data));
  }


  /************************************************************************
  ** Presentation:
  ************************************************************************/

  /** Return the String equivalent of the Token's start tag (for an element)
   *	or the part that comes before the <code>data()</code>.
   */
  public String startString() {
    return "";		// insert character entities ===
  }

  /** Return the String equivalent of the Token's content or
   *	<code>data()</code>.  Entities are substituted for characters
   *	with special significance, such as ampersand.
   */
  public String contentString() {
    return getData();		// insert character entities ===
    //return (getChildren() == null)? getData() : getChildren().toString();
  }

  /** Return the String equivalent of the Token's end tag (for an element)
   *	or the part that comes after the <code>data()</code>.
   */
  public String endString() {
    return "";
  }


  /** Convert the Token to a String using the Handler's
   *	<code>convertToString</code> method, if there is one.
   *	Otherwise it uses  <code>basicToString</code>.
   */
  public String toString() {
    return contentString();
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  /** Return a shallow copy of this Token.  Attributes, if any, are
   *	copied, but children are not.
   */
  public ActiveNode shallowCopy() {
    return new ParseTreeText(this);
  }

  /** Return a deep copy of this Token.  Attributes and children are copied.
   */
  public ActiveNode deepCopy() {
    ActiveNode node = shallowCopy();
    for (Node child = getFirstChild();
	 child != null;
	 child = child.getNextSibling()) {
      ActiveNode newChild = ((ActiveNode)child).deepCopy();
      Copy.appendNode(newChild, node);
    }
    return node;
  }

  /** Return new node corresponding to this Token, made using the given 
   *	DOMFactory.  Children <em>are not</em> copied.
   */
  public Node createNode(DOMFactory f) {
    Text e = f.createTextNode(getData()); 
    return e;
  }

}
