// ExternalTextEntity.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

public interface ExternalTextEntity extends ExternalEntity {
  /**
   * Set content string
   */
  void setContent(String content);

  /**
   * Get content string
   */
  String getContent();
}
