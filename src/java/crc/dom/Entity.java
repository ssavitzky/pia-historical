// Entity.java
// $Id$

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/

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


