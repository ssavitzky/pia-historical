// AgentMachine.java
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


/**
 * Subclass of Machine for agents, as the source or destination of a 
 *	transaction.  Sometimes called a ``virtual'' machine
 *	because the Agent is pretending to be a client or server at the
 *	other end of the wire.
 */

package crc.pia.agent;


import w3c.www.http.HTTP;

import crc.ds.TernFunc; 

import crc.pia.Machine;
import crc.pia.Agent;
import crc.pia.Pia;
import crc.pia.Resolver;
import crc.pia.Transaction;
import crc.pia.Content;
import crc.pia.HTTPResponse;
import crc.pia.PiaRuntimeException;


public class AgentMachine extends Machine {
  /**
   * Agent that creates this machine
   */
  protected Agent agent;

  /**
   * Callback functor
   */
  protected  TernFunc callback; 

  public AgentMachine( Agent agent ){
    setAgent( agent );
  }

  /**
   * set agent
   */
  public void setAgent( Agent agent ){
    if( agent != null )
      this.agent = agent;
  }

  /**
   * @return agent
   */
  public Agent agent(){
      return agent;
  }

  /**
   * set callback functor
   */
  public void setCallback( TernFunc callback ){
    if( callback != null ) this.callback = callback;
  }

  /**
   * @return callback
   */
  public TernFunc callback(){
      return callback;
  }

  /**
   * send response using a predefined callback.
   * at this point, callback could set actors for parsed content.
   * If callback is not defined, notify the agent using updateContent.
   * Generally the agent should write the content to an output stream to
   * ensure any processing side effects.
   */
   public void sendResponse (Transaction reply, Resolver resolver) {
     if( reply == null ) return;
     Content c = reply.contentObj();
     if( c == null )  return;

     TernFunc cb = callback();
     if(cb != null){
       // anything to be done with result?
       // any reason for arguments other than c?
       cb.execute(c, reply,  agent);
     }else{
       // let agent handle content
        agent.updateContent(c,"RESOLVED", this);
     }
     // debugging and logging
     Transaction req = reply.requestTran();
     if ( req != null )
       Pia.debug(this, "AgentMachine -- output from sendResponse with request url" + req.requestURL().toExternalForm());
   }
	 
  /**
   * Handle a direct request to an agent.
   * Normally done by running an InterForm, but the agent can 
   * perform special processing first.
   */
  public void getRequest(Transaction request, Resolver resolver)
       throws PiaRuntimeException {

    Agent agnt = agent;
    if( agnt != null ){
      try{
	agnt.respond(request, resolver);
      }catch(PiaRuntimeException ue){
	throw ue;
      }
    }else{
      request.errorResponse(HTTP.NOT_FOUND,
			    "Unable to find agent to handle request. "
			    + "See the <a href=\"/Admin/\">"
			    + "Agent Index</a> for a complete list."
			    );
    }

  }
}  






















