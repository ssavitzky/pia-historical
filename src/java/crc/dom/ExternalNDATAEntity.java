// ExternalNDATAEntity.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

public interface ExternalNDATAEntity extends ExternalEntity {
  /**
   * Set notation
   */
  void setNotation(Notation notation);

  /**
   * Get notation
   */
  Notation getNotation();

  /**
   * Set content
   */
  void setContent(byte[] content);

  /**
   * Get content
   */
  byte[] getContent();
}
