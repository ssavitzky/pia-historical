// AgentMachine.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

/**
 * subclass of machine for agents, these are really virtual machines
 * used by agents when they want to receive transactions
 */

package crc.pia.agent;
import java.io.StringBufferInputStream;
import crc.ds.TernFunc; 

import crc.pia.Machine;
import crc.pia.Agent;
import crc.pia.Pia;
import crc.pia.Resolver;
import crc.pia.Transaction;
import crc.pia.ByteStreamContent;
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
   * send response using a predefined callback
   */
   public void sendResponse (Transaction reply, Resolver resolver) {
     // I really don't know what to do here
     /*
     TernFunc cb = callback();
     cb.execute(agent, reply, resolver);
     */
     if( reply != null ){
       Content c = reply.contentObj();
       if( c != null ){
	 String cs = c.toString();
	 Pia.instance().debug(this, cs);
       }
     }
     
   }
  
  /**
   * Handle a direct request to an agent.
   * Normally done by running an InterForm, but the agent can 
   * perform special processing first.
   */
  public void getRequest(Transaction request, Resolver resolver) throws PiaRuntimeException {
    StringBufferInputStream sb = null;

    Agent agnt = agent;
    if( agnt != null ){
      try{
	agnt.respond(request, resolver);
      }catch(PiaRuntimeException ue){
	throw ue;
      }
    }else{
      throw new PiaRuntimeException(this, "getRequest", "Unable to find agent to handle request");
    }

  }
}  






















