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
import crc.pia.Transaction;
import crc.pia.Pia;

public final class IsAgentRequest implements UnaryFunctor{
  public boolean DEBUG = false;

  /**
   * 
   * @param object A transaction 
   * @return object boolean
   */
    public Object execute( Object o ){
      Object zfalse = new Boolean( false );
      Object ztrue  = new Boolean( true );
      String lhost = null;
      String lport = null;

      Transaction trans = (Transaction)o; 
      if( !trans.isRequest() ) return zfalse;
      URL url = trans.requestURL();
      if( url == null ) return zfalse;

      String host = url.getHost();
      if( host!= null ) 
	lhost = host.toLowerCase();
      else
	lhost = "";

      lport = Integer.toString( url.getPort() );

      if( lhost.startsWith("agency") || lhost == "" )
	return ztrue;

      if( !DEBUG ){
	if( Pia.instance().port() == lport && Pia.instance().host().startsWith( lhost ) )
	  return ztrue;
      }
      
      return zfalse;
    }
}












