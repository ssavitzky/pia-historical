// Nil.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.ds;

/** Nil is a class used to represent "null" in places where you can't
 *	return a null for some reason.   There is only one instance, 
 *	accessed as <code>Nil.value</code>. */
public final class Nil {

  public String toString() { return ""; };

  /** There is only one instance of Nil, so the constructor is private. */
  private Nil() {};

  /** This is the unique instance of Nil */
  public final static Nil value = new Nil();
}
