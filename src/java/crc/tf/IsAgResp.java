// IsAgResp.java
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

public final class IsAgResp implements UnaryFunctor{

  /**
   * 
   * @param object A transaction 
   * @return object boolean
   */
    public Object execute( Object trans ){
      URL url = null;
      boolean defineRequest = false;
      Transaction request;

      if ( !trans.isResponse() )
	return new Boolean( false );

      String agent = trans.getHeader("Version");
      if( (request = trans.getRequestTrans()) ){
	defineRequest = true;
	url = request.getRequestURL();
      }

      if( url && url.getFile().toLowerCase().startsWith("/http:") )
	return new Boolean( false );

      if( agent.toLowerCase().startsWith("pia") ){
	if(!defineRequest)
	  return new Boolean( true );
	if( request.is("agent_request") )
	  return new Boolean( true );
      }
      return new Boolean( false );
	
    }
}


