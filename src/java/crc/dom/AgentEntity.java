// AgentEntity.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

/**
 * An Agent entity.
 *
 */



public class AgentEntity extends TextEntity implements ExternalTextEntity{

  public AgentEntity(){
    setIsExternal( true ); 
  }

  public AgentEntity(String name, String systemId){
    setName( name );
    setpSystemIdentifier( systemId );
    setIsExternal( true );
  }

  public AgentEntity(String name, String systemId, String publicId){
    setName( name );
    setpSystemIdentifier( systemId );
    setIsExternal( true );
    setpPublicIdentifier( publicId );
  }


  /**
   * Copy constructor
   */
  public AgentEntity(AgentEntity ae){
    
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
   * This is a dummy function created to satify DOM interface.
   */
  public void setIsNDATA(boolean isNDATA){
    return;
  }
  
  
  /**
   * Returns false for this is text.
   * @return false for this is text.
   */
  public boolean getIsNDATA(){ return false; }
  
  
  /**
   * Set isPublic to true if this entity has public identifier.
   */
  public void setIsPublic(boolean isPublic){
    setpIsPublic( isPublic );
  }
  
  
  /**
   * Returns true if public identifier is present.
   * @return true if public identifier is present.
   */
  public boolean getIsPublic(){
    return getpIsPublic();
  }
  
  /**
   * Set public identifier.
   */
  public void setPublicIdentifier(String publicIdentifier){
    setpPublicIdentifier( publicIdentifier );
  }

  /**
   * Get public identifier.
   */
  public String getPublicIdentifier(){
    return getpPublicIdentifier();
  }

  /**
   * Set system identifier.
   */
  public void setSystemIdentifier(String systemIdentifier){
    setpSystemIdentifier( systemIdentifier );
  }
  
  /**
   * Get system identifier.
   */
  public String getSystemIdentifier(){
    return getpSystemIdentifier();
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
