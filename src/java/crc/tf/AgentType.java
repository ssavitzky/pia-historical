// AgentType.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.tf;

import java.net.URL;

import crc.pia.Transaction;
import crc.pia.Pia;

import crc.tf.TFComputer;

public final class AgentType extends TFComputer {

  /**
   * Get an agent's type in a request URL.
   * @param object A transaction 
   * @return agent's name as an object if exists otherwise null
   */
  public Object computeFeature(Transaction trans) {

    if (trans.isResponse()) trans = trans.requestTran();
    if (trans == null) return null;

    if (! trans.test("agent-request")) return null;

    URL url = trans.requestURL();
    if( url == null ) return null;

    String path = url.getFile();
    if( path == null ) return null;
      
    crc.pia.Agent agent = Pia.instance().resolver().agentFromPath(path);

    if (agent != null) 
      trans.assert("agent", agent.name());

    return (agent != null)? agent.type() : null;
  }
}









