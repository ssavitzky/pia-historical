// IsProxyRequest.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

 
package crc.tf;

import crc.ds.UnaryFunctor;
import crc.pia.Pia;
import crc.pia.Transaction;
import java.net.URL;

public final class IsProxyRequest implements UnaryFunctor{

  /**
   * Is this request a proxy request; not directed at the agency
   * @param o Transaction 
   * @return true if host part of the request does not start with "agency", equals to "", or
   * Pia's host and port are not the same as those of the request.
   */
    public Object execute( Object o ){
      Transaction trans = (Transaction) o;

      Object zfalse = new Boolean( false );
      Object ztrue  = new Boolean( true );
      String lhost = null;

      if( !trans.isRequest() ) return zfalse;
      URL url = trans.requestURL();
      if( url == null ) return zfalse;
      
      String host = url.getHost();
      if( host != null ) 
	lhost = host.toLowerCase();
      else
	lhost = "";
      String lport = Integer.toString( url.getPort() );
      
      if( lhost.startsWith("agency") || lhost == "" )
	return zfalse;
      
      if( Pia.instance().port() == lport && Pia.instance().host().startsWith( lhost ) )
	return zfalse;

      return ztrue;
    }
}


