// Agent.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.tf;

import java.net.URL;
import crc.util.regexp.MatchInfo;
import crc.util.regexp.RegExp;
import crc.pia.Transaction;
import crc.tf.TFComputer;

public final class Agent extends TFComputer {

  /**
   * Get an agent's name in a request URL.
   * @param object A transaction 
   * @return agent's name as an object if exists otherwise null
   */
  public Object computeFeature(Transaction trans){
      String name = "agency";

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









