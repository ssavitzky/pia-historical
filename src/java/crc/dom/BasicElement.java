// BasicElement.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * Implements w3c's dom Element interface.  This object
 * stores element tag name and attribute list
 */

package crc.dom;

import java.io.*;
import w3c.dom.Element;
import w3c.dom.AttributeList;
import w3c.dom.Attribute;
import w3c.dom.NodeEnumerator;
import w3c.dom.Node;
import w3c.dom.NodeList;


public class BasicElement extends AbstractNode implements Element {

  public BasicElement(Node myParent){
    setParent( myParent );
    setLeftSibling( null );
    setRightSibling( null );
    setTagName( "" );
    setAttributes( null );
    children = null;
  }

  /**
   * implementing Element methods
   */
  public int getNodeType() { return AbstractNode.NodeType.ELEMENT; }

  public void setTagName(String tagName){}
  public String getTagName(){ return tagName; }
  
  public void setAttributes(AttributeList attributes){}
  public AttributeList getAttributes(){ return attrList; }
  
  public void setAttribute(Attribute newAttr){}
  
  public NodeEnumerator getElementsByTagName(String name){ return null; }


  /* tag name */
  protected String tagName;

  /* attribute list */
  protected AttributeList attrList;

  /**
   * The child collection
   */
  protected NodeList children;
}




