// InternalEntity.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;


/**
 * The "InternalEntity" interface represents general entities.
 * If an entity is defined without any separate storage file,
 * and the replacement text is given in its declaration, the entity is
 * called an internal entity. 
 */

public interface InternalEntity extends Entity {
  /**
   * Set the entity's content
   */
  void setContent(String value);
  
  /**
   * Return this entity content
   * @return this entity content
   */
  String getContent();
};


