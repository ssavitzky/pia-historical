////// HookProcessor.java: Top Processor for processing Agent hooks
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.process;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.DateFormat;

import java.io.PrintStream;

import java.net.URL;

import crc.dps.*;
import crc.dps.util.*;
import crc.dom.NodeList;
import crc.ds.List;

import crc.pia.Pia;
import crc.pia.Agent;
import crc.pia.Transaction;
import crc.pia.Resolver;

/**
 * A TopProcessor for processing actOn hooks in PIA agents.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.pia
 * @see crc.dps.process.TopProcessor
 * @see crc.dps.Processor
 * @see crc.dps.Context */

public class Hook extends ActiveDoc {

  /************************************************************************
  ** Variables:
  ************************************************************************/

  protected Agent 	agent 		= null;
  protected Transaction	transaction 	= null;
  protected Resolver 	resolver 	= null;

  /************************************************************************
  ** PIA information:
  ************************************************************************/

  public String getAgentName() {
    return agent.name();
  }

  /** Convenience function: return the name of the current agent as a 
   *	default if the given name is null.
   */
  public String getAgentName(String name) {
    return (name == null)? agent.name() : name;
  }

  public String getAgentType(String name) {
    if (name == null) return agent.type();
    Agent ia = resolver.agent(name);
    return (ia == null)? null : ia.type();
  }

  public Agent getAgent(String name) {
    return (name == null)? agent : resolver.agent(name);
  }    

  public Agent getAgent() {
    return agent;
  }    

  public Resolver getResolver() {
    return resolver;
  }    

  public Transaction getTransaction() {
    return transaction;
  }

  /************************************************************************
  ** Setup:
  ************************************************************************/

  public void initializeEntities() {
    super.initializeEntities();
    initializeLegacyEntities();
    initializeHookEntities();
  }


  /** Initialize entities that differ for each hook called on a transaction. */
  public void initializeHookEntities() {
    // Set these even if we retrieved an entity table from the 
    // transaction -- the agent is (necessarily) different      

    define("agentName", agent.name());
    define("agentType", agent.type());
    if (agent.name().equals(agent.type())) {
      define("agentPath", "/"+agent.name());
    } else {
      define("agentPath", "/"+agent.type()+"/"+agent.name());
    }

  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  Hook() {
    super();
  }

  public Hook(Agent a, Transaction req, Transaction resp, Resolver res) {
    super(a, req, resp, res);
  }

}

