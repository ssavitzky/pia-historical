// IsAgentResponse.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

 
package crc.tf;

import crc.ds.UnaryFunctor;
import java.net.URL;
import crc.pia.Transaction;

public final class IsAgentResponse implements UnaryFunctor{

  /**
   * Is this an agent's response transaction. 
   * @param object A transaction 
   * @return true if this transaction's "Version" header is "pia" and  either of the following condition is true:
   * 1- The request transaction attached to this  transaction is not defined
   * 2- The request transaction is a request for the agency.
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
	if (request.test("IsAgentRequest"))
	  return new Boolean( true );
      }
      return new Boolean( false );
	
    }
}


