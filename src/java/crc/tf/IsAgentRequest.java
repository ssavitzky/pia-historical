// IsAgentRequest.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
 
package crc.tf;

import crc.ds.UnaryFunctor;
import java.net.URL;
import crc.pia.Transaction;
import crc.pia.Pia;

public final class IsAgentRequest implements UnaryFunctor{
  /**
   * Is this transaction a request for the agency
   * @param object A transaction 
   * @return Boolean true if the host part of a request url starts with "agency" or
   * host part and port  equals those of the Pia's hostname and port number otherwise false.  
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
    
    if( Pia.instance().port().equals( lport ) && Pia.instance().host().startsWith( lhost ) ){
      return ztrue;
    }
    
    return zfalse;
  }
}












