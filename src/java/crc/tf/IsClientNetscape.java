// IsClientNetscape.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

 
package crc.tf;

import crc.ds.UnaryFunctor;
import crc.pia.Transaction;

public final class IsClientNetscape implements UnaryFunctor{

  /**
   * Is client of this transaction  Netscape.
   * @param o A transaction 
   * @return true if client of this transaction is Netscape
   */
    public Object execute( Object o ){
      Transaction trans = (Transaction) o;

      String agent = trans.header("User-Agent");
      if( agent != null ){
	String lagent = agent.toLowerCase();
	if( lagent.indexOf("netscape") != -1 )
	  return new Boolean( true );
      }
      return new Boolean( false );
    }
}



