// BasicElement.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * Implements w3c's dom Element interface.  This object
 * stores element tag name and attribute list
 */

package crc.dom;

import java.io.*;

public class BasicElement extends AbstractNode implements Element {
  public BasicElement(){
    setParent( null );
    setPrevious( null );
    setNext( null );
    setTagName( "" );
    setAttributes( null );
  }

  public BasicElement(Node myParent){
    if( myParent != null )
      setParent( (AbstractNode)myParent );
    else
      setParent( null );
    setPrevious( null );
    setNext( null );
    setTagName( "" );
    setAttributes( null );
  }

  /**
   * implementing Element methods
   */
  public int getNodeType() { return NodeType.ELEMENT; }

  public void setTagName(String tagName){ this.tagName = tagName; }
  public String getTagName(){ return tagName; }
  
  public void setAttributes(AttributeList attributes){}
  public AttributeList getAttributes(){ return attrList; }
  
  public void setAttribute(Attribute newAttr){}
  
  public NodeEnumerator getElementsByTagName(String name){ return null; }


  /* tag name */
  protected String tagName;

  /* attribute list */
  protected AttributeList attrList;

}




