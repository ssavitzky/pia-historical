// NotActiveNodeException.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dps.active;

public class NotActiveNodeException extends RuntimeException {
  public NotActiveNodeException(String msg){
    super( msg );
  }
  public NotActiveNodeException(){
    super( "Ordinary Node used where ActiveNode was expected" );
  }
};

