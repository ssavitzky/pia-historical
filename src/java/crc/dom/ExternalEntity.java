// ExternalEntity.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;


/**
 * The "ExternalEntity" interface represents external entities which can be 
 * text or binary.
 */

public interface ExternalEntity extends Entity {
  /**
   * Set isNDATA to true if this entity has binary content.
   */
  void setIsNDATA(boolean isNDATA);

 
  /**
   * Returns true if this entity has binary content.
   * @return true if this is binary.
   */
  boolean getIsNDATA();


  /**
   * Set isPublic to true if this entity has public identifier.
   */
  void setIsPublic(boolean isPublic);


  /**
   * Returns true if public identifier is present.
   * @return true if public identifier is present.
   */
  boolean getIsPublic();


  /**
   * Set public identifier.
   */
  void setPublicIdentifier(String publicIdentifier);

  /**
   * Get public identifier.
   */
  String getPublicIdentifier();

  /**
   * Set system identifier.
   */
  void setSystemIdentifier(String systemIdentifier);

  /**
   * Get system identifier.
   */
  String getSystemIdentifier();


};


