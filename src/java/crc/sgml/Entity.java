// Entity.java:  InterForm (SGML) entity reference
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.sgml;

import crc.ds.List;
import crc.ds.Table;


/**
 * The representation of an SGML entity reference. <p>
 *
 */
public class Entity extends Token {

  /************************************************************************
  ** Components:
  ************************************************************************/

  protected String entityName;

  protected boolean semicolon;


  /************************************************************************
  ** SGML Predicates:
  ************************************************************************/
  
  /** Return the name of the entity to which this is a reference. */
  public String entityName() {
    return entityName;
  }

  public boolean hasSemicolon() {
    return semicolon;
  }


  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct an entity reference.  A boolean flag indicates whether
   *	or not a semicolon is present. */
  public Entity(String ident, boolean semi) {
    super("&");
    entityName = ident;
    semicolon  = semi;
  }

  /** Construct an entity reference.  A boolean flag indicates whether
   *	or not a semicolon is present. */
  public Entity(String tag, String ident, boolean semi) {
    super(tag);
    entityName = ident;
    semicolon  = semi;
  }

  public Entity(Entity e) {
    this(e.tag, e.entityName, e.semicolon);
  }

  /************************************************************************
  ** Copying:
  ************************************************************************/

  public Object clone() {
    return new Entity(this);
  }

  /************************************************************************
  ** Conversion to String:
  ************************************************************************/

  public String toString() {
    return tag + entityName + (semicolon? ";" : "");
  }

}
