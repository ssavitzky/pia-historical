// IsLocal.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.tf;

import crc.ds.UnaryFunctor;
import crc.pia.Transaction;
import crc.pia.Pia;


public final class IsLocal implements UnaryFunctor{
  public boolean DEBUG = false;

  /**
   * Is the request for a local host to handle
   * @param o Transaction 
   * @return true if request's host start with "agency","", Pia's host is that same as request's host,
   * or request's host is "localhost".
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




