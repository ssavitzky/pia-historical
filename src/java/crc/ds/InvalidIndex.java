// InvalidIndex.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.ds;

/** Attempt to parse an invalid Index expression. */

public class InvalidIndex extends Exception {
  public InvalidIndex() { super(); }
  public InvalidIndex(String s) {super(s);}
}

