// IsLocal.java
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

import crc.ds.UnaryFunctor;
import crc.pia.Transaction;
import crc.pia.Pia;


public final class IsLocal implements UnaryFunctor{
  public boolean DEBUG = false;

  /**
   * 
   * @param object A transaction 
   * @return object boolean
   */
    public Object execute( Object o ){
      Transaction trans = (Transaction) o;

      String host = trans.host();
      if (DEBUG)
	System.out.println("the host-->" + host );

      if( host != null ){
	String lhost = host.toLowerCase();
	if( lhost.startsWith("agency") || lhost == "" )
	  return new Boolean( true );

	if(!DEBUG){
	  String mhost = Pia.instance().host().toLowerCase();
	  if( mhost.startsWith(lhost) )
	    return new Boolean( true );
	}

	if( lhost.indexOf("localhost") != -1 )
	  return new Boolean( true );
      }
      return new Boolean( false );

    }
}



