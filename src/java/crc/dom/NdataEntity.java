// NdataEntity.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

public class NdataEntity extends AbstractEntity implements ExternalNDATAEntity{

  /**
   * Set isNDATA to true if this entity has binary content.
   */
  public void setIsNDATA(boolean isNDATA);
  
  
  /**
   * Returns true if this entity has binary content.
   * @return true if this is binary.
   */
  public boolean getIsNDATA();
  
  
  /**
   * Set isPublic to true if this entity has public identifier.
   */
  public void setIsPublic(boolean isPublic);
  
  
  /**
   * Returns true if public identifier is present.
   * @return true if public identifier is present.
   */
  public boolean getIsPublic();
  
  
  /**
   * Set public identifier.
   */
  public void setPublicIndentifier(String publicIndentifier);

  /**
   * Set system identifier.
   */
  public void setSystemIndentifier(String systemIndentifier);
  
  /**
   * Get system identifier.
   */
  public String getSystemIdentifier();


  /**
   * Set content
   */
  public void setContent(byte[] content);
  
  /**
   * Get content
   */
  public byte[] getContent();

  /**
   * Set notation.
   */
  public void setNotation(Notation notation);

  /**
   * Get notation.
   */
  public Notation getNotation();

  protected Notation notation;
  protected byte[] content;
}

