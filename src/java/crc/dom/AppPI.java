// AppPI.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

import w3c.dom.PI;

public class AppPI extends AbstractNode implements PI {

  public AppPI(){
    name = "";
    data = "";
  }
  
  public void setName(String name){ this.name = name; }
  public String getName(){return name;}

  public void setData(String data){ this.data = data; }
  public String getData(){return data;}

  public int getNodeType(){ return AbstractNode.NodeType.PI; }

  protected String name;
  protected String data;
};
