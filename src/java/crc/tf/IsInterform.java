// IsInterForm.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

 
  /**
   *Feature Computers.
   *All take a transaction as their argument, and most return a
   *boolean.  Feature computers may use the utility method
   *transaction->assert(name,value) to set additional features. 
   *
   *By convention, a feature computer "is_foo" computes a feature
   *named "foo". 
   *
   *Default Features: 
   *	These are computed by default when a transaction is created;
   *	they may have to be recomputed if the transaction is modified.
   *
   */
package crc.tf;

import java.net.URL;
import crc.ds.UnaryFunctor;
import crc.pia.Transaction;

public final class IsInterform implements UnaryFunctor{

  /**
   * 
   * @param object A transaction 
   * @return object boolean
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



