// IsFileRequest.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.tf;

import crc.ds.UnaryFunctor;
import crc.pia.Transaction;

public final class IsFileRequest implements UnaryFunctor{

  /**
   * Is this request transaction a file request 
   * @param o Transaction 
   * @return true if this transaction's protocol is  "file".
   */
    public Object execute( Object o ){
      Transaction trans = (Transaction) o;

      String scheme = trans.protocol();
      if( scheme != null ){
	String lscheme = scheme.toLowerCase();
	if( lscheme.indexOf("file") != -1 )
	  return new Boolean( true );
      }
      return new Boolean( false );
    }
}





