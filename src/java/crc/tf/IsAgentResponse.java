// IsAgentResponse.java
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
import java.net.URL;
import crc.pia.Transaction;

public final class IsAgentResponse implements UnaryFunctor{

  /**
   * 
   * @param object A transaction 
   * @return object boolean
   */
    public Object execute( Object o ){
      URL url = null;
      boolean defineRequest = false;
      Transaction request;
      Transaction trans;

      trans = (Transaction) o;
      if ( !trans.isResponse() )
	return new Boolean( false );

      String agent = trans.header("Version");
      if( (request = trans.requestTran()) != null ){
	defineRequest = true;
	url = request.requestURL();
      }

      if( url!=null && url.getFile().toLowerCase().startsWith("/http:") )
	return new Boolean( false );

      if( agent!= null && agent.toLowerCase().startsWith("pia") ){
	if(!defineRequest)
	  return new Boolean( true );
	Boolean res = (Boolean)request.is("IsAgentRequest");
	if( res.booleanValue() )
	  return new Boolean( true );
      }
      return new Boolean( false );
	
    }
}


