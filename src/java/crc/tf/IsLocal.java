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

public final class IsLocal implements UnaryFunctor{

  /**
   * 
   * @param object A transaction 
   * @return object boolean
   */
    public Object execute( Object trans ){
      String host = trans.getHost();
      if( host ){
	String lhost = host.toLowerCase();
	if( lhost.startsWith("agency") || lhost == "" )
	  return new Boolean( true );
	String mhost = Pia.getInstance().getHost().toLowerCase();
	if( mhost.startsWith(lhost) )
	  return new Boolean( true );
	if( lhost.indexOf("localhost") != -1 )
	  return new Boolean( true );
	return new Boolean( false );
      }
    }
}



