// CRCAttribute.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dom;

import java.io.*;

public class CRCAttribute extends BasicAttribute implements Attribute {

  public CRCAttribute(Node myParent){
    super( myParent );
    attrModel = null;
  }

  public CRCAttribute(String n, NodeList v){
    super( n, v );
    attrModel = null;
  }

  public Object clone(){
    CRCAttribute n = (CRCAttribute)super.clone();
    n.setName( getName() );
    setValue( new ChildNodeList( getValue() ));
    n.copyChildren( this );
    n.setSpecified( getSpecified() );
    n.setAttrModel( getAttrModel() );
    return n;
  }


  /* Accessor and mutator for attribute node definition */
  public AttributeDefinition getAttrModel(){ return attrModel; }
  public void setAttrModel(AttributeDefinition m){
    if ( m == null ) return;
    // value is not expicitly set; set value base on 
    // default if any
    if( !getIsAssigned() ){
      NodeList dv = m.getDefaultValue();
      if( dv != null )
	value = dv;
    }
  }


  /* reference to attribute model node create in document DTD */
  protected AttributeDefinition attrModel;
}






