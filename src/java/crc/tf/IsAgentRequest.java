// IsAgentRequest.java
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

public final class IsAgentRequest implements UnaryFunctor{

  /**
   * 
   * @param object A transaction 
   * @return object boolean
   */
    public Object execute( Object trans ){
      Object zfalse = new Boolean( false );
      Object ztrue  = new Boolean( true );

      if( !trans.isRequest() ) return zfalse;
      URL url = trans.getRequestURL();
      if( !url ) return zfalse;

      String host = url.getHost();
      if( host ) 
	String lhost = host.toLowerCase();
      else
	lhost = "";
      String lport = url.getPort().toString();

      if( lhost.startsWith("agency") || lhost == "" )
	return ztrue;

      if( Pia.getInstance().getPort() == lport && Pia.getInstance().getHost().startsWith( lhost ) )
	return ztrue;
      
      return zfalse;
    }
}


