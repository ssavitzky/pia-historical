// LocalEntity.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

import java.lang.reflect.*;


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
   * Copy constructor
   */
  public LocalEntity(LocalEntity ae){
    
    setpIsNDATA( ae.getpIsNDATA() );
    setpIsPublic( ae.getpIsPublic() );
    setpPublicIdentifier( ae.getpPublicIdentifier() );
    setpSystemIdentifier( ae.getpSystemIdentifier() );
    setName( ae.getName() );
    setIsParameterEntity( ae.getIsParameterEntity() );
    setIsBound( ae.getIsBound() );
    setIsExternal( ae.getIsExternal() );
    setValue( new ChildNodeList( ae.getValue() ));
    setNameSpace( ae.getNameSpace() );
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

