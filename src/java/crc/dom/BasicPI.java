// BasicPI.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;




public class BasicPI extends AbstractNode implements PI {

  public BasicPI(){
    name = "";
    data = "";
  }
  
  public void setName(String name){ this.name = name; }
  public String getName(){return name;}

  public void setData(String data){ this.data = data; }
  public String getData(){return data;}

  public int getNodeType(){ return NodeType.PI; }

  protected String name;
  protected String data;
};
