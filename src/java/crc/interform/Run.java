////// Run.java: Run the InterForm Interpretor inside the PIA
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

import java.util.Date;

import crc.interform.Parser;
import crc.interform.Input;
import crc.interform.Interp;
import crc.interform.Tagset;
import crc.interform.Environment;

import crc.sgml.SGML;
import crc.sgml.Text;
import crc.sgml.Token;

import crc.pia.Pia;
import crc.pia.Agent;
import crc.pia.Transaction;
import crc.pia.Resolver;

import crc.ds.List;
import crc.ds.Table;

/** Run the InterForm Interpretor inside the PIA.  This class contains
 *	everything needed for associating an interpretor and a set of
 *	entities with a document, either as an action or a handler. <p>
 *
 *	The Run object itself is used to associate an Interp object with
 *	its corresponding PIA context of Agent, Transaction, and Resolver.
 *
 * @see crc.interform.Filter for standalone operation.  */
public class Run  extends Environment {

  /************************************************************************
  ** Variables:
  ************************************************************************/

  public Agent 		agent = null;
  public Transaction 	transaction = null;
  public Resolver 	resolver = null;

  /************************************************************************
  ** Constructors:
  ************************************************************************/

  protected Run() {
    super();
  }

  protected Run(Agent ia, Transaction tr, Resolver res, String fn) {
    super(fn);
    agent = ia;
    transaction = tr;
    resolver = res;
    filename = fn;
  }

  /************************************************************************
  ** Association with Interpretor:
  ************************************************************************/

  public void use(Interp ii) {
    ii.environment = this;
  }

  public static Run environment(Interp ii) {
    try {
      return (Run) ii.environment;
    } catch (Exception e) {
      return null;
    }
  }

  /************************************************************************
  ** Extract information:
  ************************************************************************/

  public static String getAgentName(Interp ii) {
    Run env = environment(ii);
    return (env == null)? null : env.agent.name();
  }

  public static String getAgentType(Interp ii, String name) {
    Run env = environment(ii);
    if (env == null) return null;
    if (name == null) return env.agent.type();
    Agent ia = env.resolver.agent(name);
    return (ia == null)? null : ia.type();
  }

  public static Agent getAgent(Interp ii, String name) {
    Run env = environment(ii);
    return (env == null)? null : env.resolver.agent(name);
  }    

  public Agent getAgent(String name) {
    if (name == null) return null;
    return resolver.agent(name);
  }    


  public static Resolver getResolver(Interp ii) {
    Run env = environment(ii);
    return (env == null)? null : env.resolver;
  }    


  /************************************************************************
  ** Entity table:
  ************************************************************************/

  /** Initialize and return the entity table */
  public Table initEntities() {
    // === should get entities from transaction if present 
    //     my $ents = $trans->get_feature('entities') if defined $trans;

    if (entities == null) {
      super.initEntities();

      ent("url", transaction.requestURL().toString());
      ent("urlPath", transaction.requestURL().getFile());
      ent("urlQuery", transaction.hasQueryString()? 
	  (SGML)new Text(transaction.queryString()) : (SGML)Token.empty);

      Object aname = transaction.getFeature("agent");
      Agent  ta = (aname == null)? null : resolver.agent(aname.toString());
      if (ta != null) {
	ent("transAgentName", ta.toString());
	ent("transAgentType", ta.type()); 
      } else {
	ent("transAgentName", Token.empty);
	ent("transAgentType", Token.empty);
      }

      crc.pia.Pia pia = crc.pia.Pia.instance();
      ent("piaHOST", pia.properties().getProperty(Pia.PIA_HOST));
      ent("piaPORT", pia.properties().getProperty(Pia.PIA_PORT));
      ent("piaDIR", pia.properties().getProperty(Pia.PIA_ROOT));

      ent("agentNames", new crc.sgml.Tokens(resolver.agentNames(), " "));
      ent("entityNames", "");
      ent("entityNames", new crc.sgml.Tokens(entities.keys(), " "));
    }

    /* Set these even if we retrieved the entity table from the */
    /* transaction -- the agent is (necessarily) different      */

    ent("agentName", agent.name());
    ent("agentType", agent.type());

    return entities;
  }



  /************************************************************************
  ** Run the Interpretor:
  ************************************************************************/

  /** Run a standard InterForm file on behalf of an Agent. Output is
   *  	sent directly to the transaction's receiver.  */
  public static void interform(Agent agent, String filepath, 
			       Transaction trans, Resolver res) {
    OutputStream out = null;	// === get from transaction.
    Run env = new Run(agent, trans, res, filepath);
    env.runStream(env.open(filepath), out, "Standard");
  }

  /** Run a standard InterForm file on behalf of an Agent. Output is
   *  	sent to a given OutputStream.  */
  public static void interform(Agent agent, String filepath, OutputStream out,
			       Transaction trans, Resolver res) {
    Run env = new Run(agent, trans, res, filepath);
    env.runStream(env.open(filepath), out, "Standard");
  }

  /** Run a standard InterForm file and return a String.  Use of this
   *	operation is deprecated, but it will be useful while we're
   *	figuring out how to properly interface using streams. */
  public static String interformEvalFile(Agent agent, String filepath, 
					 Transaction trans, Resolver res) {
    return new Run(agent, trans, res, filepath).evalFile("Standard");
  }

  /** Run a standard InterForm file and return an InputStream. */
  public static InputStream interformFile(Agent agent, String filepath, 
					  Transaction trans, Resolver res) {
    return new Run(agent, trans, res, filepath).filterFile("Standard");
  }

  /** Run an already-parsed InterForm element as an Agent's actOn hook. */
  public static void interformHook(Agent agent, SGML code,
				   Transaction trans, Resolver res) {
    new Run(agent, trans, res, null).runCode(code, "Standard");
  }

  /************************************************************************
  ** Used by Actors:
  ************************************************************************/

  /** Look up an interform file on behalf of the agent invoked on the
   *      given SGML. */
  public String lookupFile(String fn, SGML it, boolean write) {
    // === both agentIfRoot and findInterform(String) are unimplemented! 
    // if (write) return Util.makePath(agent.agentIfRoot(), fn);
    return agent.findInterform(fn, false);
  }

  /** Retrieve a URL. */
  public InputStream retrieveURL(String url, SGML it) {

    // === unimplemented()
    return null;
  }

  /** Return a suitable base directory for read/write files. */
  public String baseDir(SGML it) {
    // === if (it.hasAttr("interform")) return agent.agentIfRoot();
    return agent.agentDirectory();
  }

  /** Return a string suitable for setting the proxy environment variables */
  public String proxies() {
    return "";			// === String Pia.proxies unimplemented()
  }

}
