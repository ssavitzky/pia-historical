// BasicPI.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;




public class BasicPI extends AbstractNode implements PI {

  public BasicPI(){
    name = "";
    data = "";
  }

  public BasicPI(String n, String data){
    name = n;
    data = data;
  }


  public BasicPI(BasicPI bpi)
  {
    if( bpi != null ){
      setParent( null );
      setPrevious( null );
      setNext( null );
      setName( bpi.getName() );
      setData( bpi.getData() );
      copyChildren( bpi );
    }
  }

  public Object clone(){
    BasicPI n = (BasicPI)super.clone();
    n.setName( getName() );
    n.setData( getData() );
    n.copyChildren( this );
    return n;
  }
  
  public void setName(String name){ this.name = name; }
  public String getName(){return name;}

  public void setData(String data){ this.data = data; }
  public String getData(){return data;}

  public int getNodeType(){ return NodeType.PI; }

  protected String name;
  protected String data;
};
