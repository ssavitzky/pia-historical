// Agent.java
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
import crc.util.regexp.MatchInfo;
import crc.util.regexp.RegExp;
import crc.pia.Transaction;


public final class Agent implements UnaryFunctor{

  /**
   * 
   * @param object A transaction 
   * @return object boolean
   */
    public Object execute( Object o ){
      String name = "agency";

      Transaction trans = (Transaction) o;
      URL url = trans.requestURL();
      if( url == null ) return null;

      String path = url.getFile();
      if( path == null ) return null;
      
      RegExp re = null;
      MatchInfo mi = null;

      try{
	re = new RegExp("^/\\w+/*");
	String lpath = path.toLowerCase();
	mi = re.match( lpath );
      }catch(Exception e){;}
      if( mi != null )
	name = mi.matchString();

      return name;
    }
}





