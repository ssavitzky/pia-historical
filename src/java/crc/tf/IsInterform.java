// IsInterForm.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

 
package crc.tf;

import java.net.URL;
import crc.ds.UnaryFunctor;
import crc.pia.Transaction;

public final class IsInterform implements UnaryFunctor{

  /**
   * Is this transaction's request a request for interform
   * @param o Transaction 
   * @return true if the URL's file portion ends in ".if"
   */
    public Object execute( Object o ){
      Transaction trans = (Transaction) o;

      URL url = trans.requestURL();
      if( url != null ){
	String path = url.getFile();
	String lpath = path.toLowerCase();
	if( path.endsWith(".if") )
	  return new Boolean( true );
	else
	  return new Boolean( false );
      }else return new Boolean( false );



    }
}



