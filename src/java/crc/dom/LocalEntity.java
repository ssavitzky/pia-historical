// LocalEntity.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

/**
 * Internal entity within a dtd.
 *
 */



public class LocalEntity extends TextEntity implements InternalEntity{

  public LocalEntity(){
  }

  public LocalEntity(String name){
    setName( name );
  }


  /**
   * Set namespace
   */
  void setNameSpace(NamedNodeList nameSpace){
    this.nameSpace = nameSpace;
  }
  
  /**
   * Return this name space
   * @return this name space
   */
  NamedNodeList getNameSpace(){
    return nameSpace;
  }

  protected NamedNodeList nameSpace;
  
}
