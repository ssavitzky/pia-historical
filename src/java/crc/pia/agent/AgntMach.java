// AgntMach.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

/**
 * subclass of machine for agents, these are really virtual machines
 * used by agents when they want to receive transactions
 */

package crc.pia.agent;
import crc.pia.Machine;
import crc.pia.Agent;
import crc.ds.TernFunc; 

class AgntMach extends Machine {
  /**
   * Agent that creates this machine
   */
  protected Agent agent;

  /**
   * Callback functor
   */
  protected  TernFunc callback; 

  public AgntMach( Agent agent ){
    setAgent( agent );
  }

  /**
   * set agent
   */
  public void setAgent( Agent agent ){
    if( agent )
      this.agent = agent;
  }

  /**
   * @return agent
   */
  public Agent getAgent(){
      return agent;
  }

  /**
   * set callback functor
   */
  public void setCallback( TernFunc callback ){
    if( callback ) this.callback = callback;
  }

  /**
   * @return callback
   */
  public TernFunc getCallback(){
      return callback;
  }

  /**
   * send response using a predefined callback
   */
   public void sendResponse (Transaction reply, Resolver resolver) {
     TernFunc cb = getCallback();
     cb.execute(agent, reply, resolver);
   }
  
  /**
   * Handle a direct request to an agent.
   * Normally done by running an InterForm, but the agent can 
   * perform special processing first.
   */
  public Transaction getRequest(Transaction request, Resolver resolver) {
    Transaction response = null;

    Agent agnt = agent;
    if( agnt )
      response = agnt.respond(request, resolver);
    return response;
  }
}




