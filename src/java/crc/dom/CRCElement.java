// CRCElement.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * Implements w3c's dom Element interface.  This object
 * stores element tag name and attribute list
 */

package crc.dom;

import java.io.*;

public class CRCElement extends BasicElement implements Element {

  public CRCElement(){
    setParent( null );
    setPrevious( null );
    setNext( null );
    setTagName( "" );
    setAttributes( null );
  }

  public CRCElement(Node myParent){
    if( myParent != null )
      setParent( (AbstractNode) myParent );
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

  public void setTagName(String tagName){}
  public String getTagName(){ return tagName; }
  
  public void setAttributes(AttributeList attributes){}
  public AttributeList getAttributes(){ return attrList; }
  
  public void setAttribute(Attribute newAttr){}

  public void setElementDef(ElementDefinition d ){
    elementDef = d;
  }

  public ElementDefinition getElementDef(){
    return elementDef;
  }
  
  public NodeEnumerator getElementsByTagName(String name){ return null; }


  /**
   * ElementDefinition
   */
  protected ElementDefinition elementDef;
}




