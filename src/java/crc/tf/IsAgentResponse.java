// IsAgentResponse.java
// $Id$

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/


 
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


