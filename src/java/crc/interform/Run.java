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

import java.net.URL;

import java.util.Date;

import crc.interform.Parser;
import crc.interform.Input;
import crc.interform.Interp;
import crc.interform.Tagset;
import crc.interform.Environment;
import crc.interform.SecureAttrs;
import crc.sgml.SGML;
import crc.sgml.Text;
import crc.sgml.Token;
import crc.sgml.Tokens;
import crc.sgml.AttrTable;
import crc.sgml.AttrWrap;

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
    debug = fn != null && Pia.debug() && Pia.verbose();
  }

  /************************************************************************
  ** Association with Interpretor:
  ************************************************************************/

  //override use to add secure entities
  public void use(Interp ii) {
    super.use(ii);
    secureEntities(ii);
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

      URL url = transaction.requestURL();
      if (url != null) {
	ent("url", transaction.requestURL().toString());
	ent("urlPath", transaction.requestURL().getFile());
      }

      // form parameters might be either query string or POST data
      if(transaction.hasQueryString()){
        ent("urlQuery",  (SGML)new Text(transaction.queryString()));
        ent("FORM",new AttrWrap(new AttrTable(transaction.getParameters())));
       } else {
            ent("urlQuery",  (SGML)Token.empty);
       }

      if (transaction.test("agent-request") ||
	   transaction.test("agent-response")) {

	String aname = transaction.getFeatureString("agent");
	String atype = transaction.getFeatureString("agent-type");

	ent("transAgentName", aname);
	ent("transAgentType", atype); 
      } else {
	ent("transAgentName", Token.empty);
	ent("transAgentType", Token.empty);
      }

      crc.pia.Pia pia = crc.pia.Pia.instance();
      ent("piaHOST", pia.properties().getProperty(Pia.PIA_HOST));
      ent("piaPORT", pia.properties().getProperty(Pia.PIA_PORT));
      ent("piaDIR", pia.properties().getProperty(Pia.PIA_ROOT));

      // === shouldn't have to convert these to text.
      Tokens anames = new Tokens(resolver.agentNames(), " ").sortAscending();
      ent("agentNames", anames.toText());
    }

    /* Set these even if we retrieved the entity table from the */
    /* transaction -- the agent is (necessarily) different      */

    ent("agentName", agent.name());
    ent("agentType", agent.type());

    ent("entityNames", "");
    Tokens enames = new Tokens(entities.keys(), " ").sortAscending();
    ent("entityNames", enames.toText());

    return entities;
  }

  /** add things like agent and transaction to the entity table with
       a mechanism for controlling access. 
   */
  public Table secureEntities(Interp context) {
    // construct entries for "AGENT.foo" lookup
    ent("AGENT",new SecureAttrs(agent, context));
    ent("TRANS",new SecureAttrs(transaction, context));
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

  /** Run a standard InterForm file and discard the output. */
  public static void interformSkipFile(Agent agent, String filepath, 
				       Transaction trans, Resolver res) {
    new Run(agent, trans, res, filepath).skipFile("Standard");
  }

  /** Run an already-parsed InterForm element as an Agent's actOn hook. 
   *	A String ``filename'' can be passed for use in error messages.
   */
  public static void interformHook(Agent agent, SGML code, String fn,
				   Transaction trans, Resolver res) {
    new Run(agent, trans, res, fn).runCode(code, "Standard");
  }

  /************************************************************************
  ** Used by Actors:
  ************************************************************************/

  /** Look up an interform file on behalf of the agent invoked on the
   *      given SGML. */
  public String lookupFile(String fn, SGML it, boolean write) {
    return (write)? Util.makePath(agent.agentIfDir(), fn) 
      		  : agent.findInterform(fn);
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
