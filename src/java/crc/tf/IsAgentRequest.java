// IsAgentRequest.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
 
package crc.tf;

import java.net.URL;
import crc.pia.Transaction;
import crc.pia.Pia;
import crc.tf.TFComputer;

public final class IsAgentRequest extends TFComputer {
  /**
   * Is this transaction a request for the agency
   * @param object A transaction 
   * @return Boolean true if the host part of a request url starts with "agency" or
   * host part and port  equals those of the Pia's hostname and port number otherwise false.  
   */

  public Object computeFeature(Transaction trans){

    String lhost = null;
    String lport = null;
    
    if( !trans.isRequest() ) return False;
    URL url = trans.requestURL();
    if( url == null ) return False;
    
    String host = url.getHost();
    if( host!= null ) 
      lhost = host.toLowerCase();
    else
      lhost = "";
    
    lport = Integer.toString( url.getPort() );
    
    if( lhost.startsWith("agency") || lhost == "" )
      return True;
    
    if( Pia.instance().port().equals( lport )
	&& Pia.instance().host().startsWith( lhost ) ){
      return True;
    }
    
    return False;
  }
}












