// Entity.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;


/**
 * The "Entity" interface represents general entities.
 * If an entity is defined without any separate storage file,
 * and the replacement text is given in its declaration, the entity is
 * called an internal entity.  Otherwise, it is external.
 */

public interface Entity extends Node {
  /**
   * Set the name of this entity.
   * @param name entity name.
   */
  void setName(String name);

  /**
   * Returns the name of this entity. 
   * @return entity name.
   */
  String getName();
  
  /**
   * set isParameterEntity
   */
  void setIsParameterEntity(boolean isParameterEntity);

  /**
   * Returns true if this is a parameter entity.
   * @return true if this a parameter entity.
   */
  boolean getIsParameterEntity();
};


