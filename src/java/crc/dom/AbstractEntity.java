// AbstractEntity.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

/**
 * AbstractEntity implements DOM's entity and
 * represents all entity objects whether internal
 * or external.  Functions that implements DOM's ExternalEntity
 * are protected; subclasses that implements ExternalEntity should
 * make use of these funtions.
 */



public class AbstractEntity extends AbstractNode implements Entity{
  /**
   * Set the name of this entity.
   * @param name entity name.
   */
  public void setName(String name){
    this.name = name;
  }

  /**
   * Returns the name of this entity. 
   * @return entity name.
   */
  public String getName(){
    return name;
  }
  
  /**
   * set isParameterEntity
   */
  public void setIsParameterEntity(boolean isParameterEntity){
    this.isParameterEntity = isParameterEntity;
  }

  /**
   * Returns true if this is a parameter entity.
   * @return true if this a parameter entity.
   */
  public boolean getIsParameterEntity(){
    return isParameterEntity;
  }


  /**
   * set isExternal
   */
  public void setIsExternal(boolean isExternal){
    this.isExternal = isExternal;
  }


  /**
   * Get isExternal
   */
  public boolean getIsExternal(boolean isExternal){
    return isExternal;
  }

  /**
   * @return Node type.
   */
  public int getNodeType(){
    return NodeType.ENTITY;
  }

  /*=========================================================*/
  /**
   * Set isNDATA to true if this entity has binary content.
   */
  protected void setpIsNDATA(boolean isNDATA){
    this.isNDATA = isNDATA;
  }
  
  
  /**
   * Returns true if this entity has binary content.
   * @return true if this is binary.
   */
  protected boolean getpIsNDATA(){
    return isNDATA;
  }
  
  
  /**
   * Set isPublic to true if this entity has public identifier.
   */
  protected void setpIsPublic(boolean isPublic){
    this.isPublic = isPublic;
  }
  
  
  /**
   * Returns true if public identifier is present.
   * @return true if public identifier is present.
   */
  protected boolean getpIsPublic(){
    return isPublic;
  }
  
  
  /**
   * Set public identifier.
   */
  protected void setpPublicIdentifier(String publicIdentifier){
    this.publicIdentifier = publicIdentifier;
  }

  /**
   * Get public identifier.
   */
  protected String getpPublicIdentifier(){
    return publicIdentifier;
  }

  /**
   * Set system identifier.
   */
  protected void setpSystemIdentifier(String systemIdentifier){
    this.systemIdentifier = systemIdentifier;
  }
  
  /**
   * Get system identifier.
   */
  protected String getpSystemIdentifier(){
    return systemIdentifier;
  }


  /* Attributes====================================*/

  /* flag indicating this is binary entity*/
  protected boolean isNDATA = false;

  /* flag indication public identifier is present */
  protected boolean isPublic = false;

  /* public identifier string */
  protected String publicIdentifier = null; 

  /* system identifier string */
  protected String systemIdentifier = null;

  /* name of this entity */
  protected String name = null;

  /* whether this entity's value is defined
  protected boolean bound = false;

  /* whether this entity is external */
  protected boolean isExternal = false;

  /* whether this is a parameter entity */
  protected boolean isParameterEntity = false;
}

