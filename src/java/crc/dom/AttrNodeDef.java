// AttrNodeDef.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dom;

import java.io.*;

public class AttrNodeDef extends AbstractNode {

  public AttrNodeDef(){}
  public AttrNodeDef(String name, Object type, Object defaultValue){
    this.name = name;
    this.type = type;
    this.defaultValue = defaultValue;
  }

  public int getNodeType() { return AbstractNode.NodeType.ATTRIBUTE; }

  public void setName(String name){}
  public String getName(){ return ""; }

  public void setType(Object type){}
  public Object getType(){ return null; }

  public Object getDefaultValue(){ return null; }

  /* XML -- a unique name */
  protected String name;
  
  /* XML -- can be CDATA, ID, IDREF/S, ENTITY/ENTITIES, NMTOKEN/NMTOKENS, A list of names */
  protected Object type;

  /*XML -- can be #REQUIRED, #IMPLIED, "value", #FIXED "value" */
  protected Object defaultValue;
}
