// ParseTreeDocument.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dps.active;

import java.io.*;
import crc.dom.*;
import crc.dps.Handler;
import crc.dps.Namespace;
import crc.dps.util.Copy;

/** 
 * Class for Document nodes. <p>
 *
 *	Document nodes can also be used as Element nodes.
 */
public class ParseTreeDocument extends ParseTreeElement
				       /* implements ActiveDocument */
{

  /************************************************************************
  ** Instance Variables:
  ************************************************************************/

  protected int nodeType = NodeType.DOCUMENT;

  /************************************************************************
  ** Accessors:
  ************************************************************************/

  public int getNodeType()		{ return nodeType; }

  /** In some cases it may be necessary to make the node type more specific. */
  void setNodeType(int value) 		{ nodeType = value; }
  
  public void setTagName(String name) { 
    super.setTagName(name);
    setNodeType(name == null? NodeType.DOCUMENT : NodeType.ELEMENT);
  }

  /************************************************************************
  ** Construction and Copying:
  ************************************************************************/

  public ParseTreeDocument() {
    super();
  }

  public ParseTreeDocument(String tag) {
    super(tag, null, null, null);
    nodeType = (tag == null)? NodeType.DOCUMENT : NodeType.ELEMENT;
  }

  public ParseTreeDocument(String tag, ActiveAttrList attrs, NodeList content) {
    super(tag, attrs, content);
    nodeType = (tag == null)? NodeType.DOCUMENT : NodeType.ELEMENT;
  }

  /** Construct a document given headers and content. */
  public ParseTreeDocument(String tag, ActiveAttrList attrs, 
			   ActiveElement headers, NodeList content) {
    this(tag, attrs, null);
    if (headers != null) addChild(headers);
    if (headers != null) addChild(new ParseTreeText("\n", true));
    Copy.appendNodes(content, this);
  }

  /**
   * deep copy constructor.
   */
  public ParseTreeDocument(ParseTreeDocument attr, boolean copyChildren){
    super((ParseTreeElement)attr, copyChildren);
    nodeType = attr.getNodeType();
  }

  public ActiveNode shallowCopy() {
    return new ParseTreeDocument(this, false);
  }

  public ActiveNode deepCopy() {
    return new ParseTreeDocument(this, true);
  }

}



