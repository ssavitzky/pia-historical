// NdataEntity.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

/**
 * A binary entity.  More work needs to be done on cloning and copy constructor.
 */


public class NdataEntity extends AbstractEntity implements ExternalNDATAEntity{

  /**
   * Set isNDATA to true if this entity has binary content.
   */
  public void setIsNDATA(boolean isNDATA){
    return;
  }
  
  
  /**
   * Returns true if this entity has binary content.
   * @return true if this is binary.
   */
  public boolean getIsNDATA(){
    return true;
  }
  
  
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
   * Set content
   */
  public void setContent(byte[] content){
    this.content = content;
  }
  
  /**
   * Get content
   */
  public byte[] getContent(){
    return content;
  }

  /**
   * Set notation.
   */
  public void setNotation(Notation notation){
    this.notation = notation;
  }

  /**
   * Get notation.
   */
  public Notation getNotation(){
    return notation;
  }

  protected Notation notation;
  protected byte[] content;
}

