// IsAgentResponse.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

 
package crc.tf;

import java.net.URL;
import crc.pia.Transaction;

import crc.tf.TFComputer;

public final class IsAgentResponse extends TFComputer {

  /**
   * Is this an agent's response transaction?. 
   * @param object A transaction 
   * @return true if this transaction's "Version" header is "pia" and  either of the following condition is true:
   * 1- The request transaction attached to this  transaction is not defined
   * 2- The request transaction is a request for the agency.
   */
    public Object computeFeature(Transaction trans) {

      if ( !trans.isResponse() ) return False;

      /* This is incorrect: it responds to agents in other agencies. */
      //String agent = trans.header("Version");
      //if( agent != null && agent.toLowerCase().startsWith("pia")) return True;

      Transaction request = trans.requestTran();
      if (request == null) return True;
      if (request.test("agent-request")) return True;

      return False;
    }
}


