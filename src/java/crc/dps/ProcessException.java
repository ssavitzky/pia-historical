// ProcessException.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dps;

/** Runtime Exception in the Document Processing System. 
 */
public class ProcessException extends RuntimeException {
  public ProcessException(String msg){
    super( msg );
  }
  public ProcessException(){
    super( "Document Processing System Exception." );
  }
};

