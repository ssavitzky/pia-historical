// Attr.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dom;

import java.io.*;

import w3c.dom.Attribute;
import w3c.dom.AttributeList;
import w3c.dom.Attribute;
import w3c.dom.NodeEnumerator;
import w3c.dom.Node;
import w3c.dom.NodeList;


public class Attr extends BasicAttr implements Attribute {

  public Attr(Node myParent){
    super( myParent );
  }

  public Attr(Node myParent, AttrNodeDef model){
    super( myParent );
    attrModel = model;
  }

  public int getNodeType() { return AbstractNode.NodeType.ATTRIBUTE; }

  /* Accessor and mutator for attribute node definition */
  public Node getAttrModel(){ return attrModel; }
  public void setAttrModel(Node m){}


  /* reference to attribute model node create in document DTD */
  protected AttrNodeDef attrModel;
}






