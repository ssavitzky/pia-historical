// BasicAttr.java
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

public class BasicAttr extends AbstractNode implements Attribute {

  public BasicAttr(Node myParent){
    setParent( myParent );
    setLeftSibling( null );
    setRightSibling( null );
    setName( "" );
    setValue( null );
    setSpecified( false );
  }

  public int getNodeType() { return AbstractNode.NodeType.ATTRIBUTE; }

  public void setName(String name){ this.name = name; }
  public String getName(){ return name; }
  
  public void setValue(NodeList value){ this.value = value; }
  public NodeList getValue(){ return value; }
  
  public void setSpecified(boolean specified){ this.specified = specified; }
  public boolean getSpecified(){return specified;}

  // provides a connection to the DTD 
  // attribute Node definition;

  public String toString(){ return ""; }

  /* attribute name */
  protected String name;

  /* list of values */
  protected NodeList value;

  /* whether value is specified */
  protected boolean specified;
}


