// Agent.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.tf;

import java.net.URL;

import crc.pia.Transaction;
import crc.pia.Pia;

import crc.tf.TFComputer;

public final class Agent extends TFComputer {

  /**
   * Get an agent's name in a request URL.
   * @param object A transaction 
   * @return agent's name as an object if exists otherwise null
   */
  public Object computeFeature(Transaction trans) {

    if (trans.isResponse()) trans = trans.requestTran();
    if (trans == null) return null;

    if (! trans.test("agent-request")) return "";

    URL url = trans.requestURL();
    if( url == null ) return "";

    String path = url.getFile();
    if( path == null ) return "";
      
    crc.pia.Agent agent = Pia.instance().resolver().agentFromPath(path);

    if (agent != null) 
      trans.assert("agent-type", agent.type());

    return (agent != null)? agent.name() : "";
  }
}









