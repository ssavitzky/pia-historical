// IsImage.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.tf;

import crc.ds.UnaryFunctor;
import crc.pia.Transaction;

public final class IsImage implements UnaryFunctor{

  /**
   *  Is this transaction's content type "image".
   * @param o  Transaction 
   * @return true if  content type is "image"
   */
    public Object execute( Object o ){
      Transaction trans = (Transaction) o;

      String zimage = trans.contentType();
      if( zimage != null ){
	String lzimage = zimage.toLowerCase();
	if( lzimage.startsWith("image") )
	  return new Boolean( true );
      }
      return new Boolean( false );
    }
}



