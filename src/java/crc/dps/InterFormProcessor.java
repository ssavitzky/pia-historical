////// InterFormProcessor.java: Top-InterForm Processor for PIA
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.DateFormat;

import java.io.PrintStream;

import java.net.URL;

import crc.dps.aux.*;
import crc.dom.NodeList;
import crc.ds.List;

import crc.pia.Pia;
import crc.pia.Agent;
import crc.pia.Transaction;
import crc.pia.Resolver;

/**
 * A TopProcessor for processing InterForm files in the PIA.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.pia
 * @see crc.dps.TopProcessor
 * @see crc.dps.Processor
 * @see crc.dps.Context */

public class InterFormProcessor extends DocumentProcessor {

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

  public void initializeLegacyEntities() {
    if (transaction != null) {
      URL url = transaction.requestURL();
      if (url != null) {
	define("url", transaction.requestURL().toString());
	define("urlPath", transaction.requestURL().getFile());
      }
      // form parameters might be either query string or POST data
      if(transaction.hasQueryString()){
        define("urlQuery",  transaction.queryString());
	// === define("FORM",new AttrWrap(new AttrTable(transaction.getParameters())));
      } else {
	define("urlQuery",  "");
	//define("FORM",new AttrWrap(new AttrTable()));
      }
      // if no parameters this is an empty table

      if (transaction.test("agent-request") ||
	   transaction.test("agent-response")) {

	String aname = transaction.getFeatureString("agent");
	String atype = transaction.getFeatureString("agent-type");

	define("transAgentName", aname);
	define("transAgentType", atype); 
	if (aname.equals(atype)) {
	  define("transAgentPath", "/"+aname);
	} else {
	  define("transAgentPath", "/"+atype+"/"+aname);
	}
      } else {
	define("transAgentName", null);
	define("transAgentType", null);
	define("transAgentPath", null);
      }
    }
    Pia pia = Pia.instance();
    define("piaHOST", pia.properties().getProperty(Pia.PIA_HOST));
    define("piaHOST", pia.properties().getProperty(Pia.PIA_HOST));
    define("piaPORT", pia.properties().getProperty(Pia.PIA_PORT));
    define("piaDIR", pia.properties().getProperty(Pia.PIA_ROOT));

    define("usrDIR", pia.properties().getProperty(Pia.USR_ROOT));

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

  InterFormProcessor() {
    super(false);
    initializeEntities();
  }

  InterFormProcessor(boolean defaultEntities) {
    super(false);
    if (defaultEntities) initializeEntities();
  }

  public InterFormProcessor(Input in, Output out) {
    super(in, null, out, null);
    initializeEntities();
  }

  public InterFormProcessor(Input in, Output out, EntityTable ents) {
    super(in, null, out, ents);
  }

  public InterFormProcessor(Input in, Context prev, Output out,
			   EntityTable ents) {
    super(in, prev, out, ents);
  }


}

/* === entity initialization from crc.interform.Run




      // === shouldn't have to convert these to text.
      Tokens anames = new Tokens(resolver.agentNames(), " ").sortAscending();
      define("agentNames", anames);
    }

    define("entityNames", "");
    Tokens enames = new Tokens(entities.keys(), " ").sortAscending();
    define("entityNames", enames);

*/
