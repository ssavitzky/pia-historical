// ReportException.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

public class ReportException extends RuntimeException {
  public ReportException(Object o, String mth, String msg){
    super (o.getClass().getName()
	   + "[" + mth + "]: "
           + msg) ;
  }
};
