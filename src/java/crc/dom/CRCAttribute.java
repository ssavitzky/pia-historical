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

  public int getNodeType() { return NodeType.ATTRIBUTE; }

  /* Accessor and mutator for attribute node definition */
  public Node getAttrModel(){ return attrModel; }
  public void setAttrModel(Node m){}


  /* reference to attribute model node create in document DTD */
  protected AttributeDefinition attrModel;
}






