// UnaryFunctor.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.ds;

public interface UnaryFunctor
  {
  /**
   * Return the result of executing with a single Object.
   * @param object The object to process.
   * @return The result of processing the input Object.
   */
  Object execute( Object object );
  }
