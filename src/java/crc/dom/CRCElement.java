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
    super();
    setElementDef( null );
  }

  public CRCElement( CRCElement e ){
    super( e );
    elementDef = e.getElementDef();
  }

  public CRCElement(Node myParent){
    super( myParent );
    setElementDef( null );
  }

  public Object clone(){
    CRCElement n = (CRCElement)super.clone();
    n.setTagName( getTagName() );
    n.copyChildren( this );
    AttributeList l = getAttributes();
    if( l != null )
      n.setAttributes( new AttrList( l ) );
    n.setElementDef( getElementDef() );
    return n;
  }

  /**
   * implementing Element methods
   */

  public void setElementDef(ElementDefinition d ){
    elementDef = d;
  }

  public ElementDefinition getElementDef(){
    return elementDef;
  }
  
  /**
   * ElementDefinition
   */
  protected ElementDefinition elementDef;
}




