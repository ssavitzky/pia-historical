////// ActiveDoc.java: Top Processor for PIA active documents
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.process;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.DateFormat;

import java.io.PrintStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.File;

import java.net.URL;

import crc.dps.*;
import crc.dps.util.*;
import crc.dom.NodeList;
import crc.dps.active.ParseNodeList;
import crc.dps.handle.Loader;

import crc.ds.List;
import crc.ds.Table;
import crc.ds.Tabular;

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
 * @see crc.dps.process.TopProcessor
 * @see crc.dps.Processor
 * @see crc.dps.Context */

public class ActiveDoc extends TopProcessor {

  /************************************************************************
  ** Variables:
  ************************************************************************/

  protected Agent 	agent 		= null;
  protected Transaction	request 	= null;
  protected Transaction	response 	= null;
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
    return (response == null)? request : response;
  }

  /************************************************************************
  ** Setup:
  ************************************************************************/

  static {
    // Preload known handler classes.
    Loader.defHandle("submit", new crc.dps.handle.submitHandler());
  }

  /** Initialize the various entities.  
   *	Done in four separate methods (counting super), for easy customization.
   *
   * @see crc.dps.process.TopProcessor#initializeEntities
   * @see #initializeNamespaceEntities
   * @see #initializeLegacyEntities
   * @see #initializeHookEntities
   */
  public void initializeEntities() {
    super.initializeEntities();
    initializeNamespaceEntities();
    initializeLegacyEntities();
    initializeHookEntities();
  }

  /** Initialize the entities that contain namespaces. */
  public void initializeNamespaceEntities() {

    define("PIA", Pia.instance().properties());
    if (agent != null) define("AGENT", agent);

    Transaction transaction = getTransaction();
    if (transaction != null) {
      define("TRANS", transaction);
      //define("HEADERS",transaction.getHeaders());

      Transaction req = transaction.requestTran();
      define("REQ", req);
      if (req != null && req.hasQueryString()){
	define("FORM", req.getParameters());
      }
    }

  }

  /** Initialize the entities that correspond to entities in the old
   *	(legacy) InterForm processor.  Many files still use them.
   */
  public void initializeLegacyEntities() {
    Transaction transaction = getTransaction();

    if (transaction != null) {
      Transaction req = transaction.requestTran();

      URL url = transaction.requestURL();
      if (url != null) {
	define("url", transaction.requestURL().toString());
	define("urlPath", transaction.requestURL().getFile());
      }
      // form parameters might be either query string or POST data
      if (req != null && req.hasQueryString()){
        define("urlQuery",  req.queryString());
      } else {
	define("urlQuery",  "");
	define("FORM", new Table());
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
	define("transAgentName", (Object)null);
	define("transAgentType", (Object)null);
	define("transAgentPath", (Object)null);
      }
    }

    Pia pia = Pia.instance();
    define("PIA", pia.properties());

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

    define("AGENT", agent);
    define("agentName", agent.name());
    define("agentType", agent.type());
    if (agent.name().equals(agent.type())) {
      define("agentPath", "/"+agent.name());
    } else {
      define("agentPath", "/"+agent.type()+"/"+agent.name());
    }

   define("agentNames", resolver.agentNames());

   define("entityNames", "");
   define("entityNames", entities.entityNames());
  }

  /************************************************************************
  ** External Entities:
  ************************************************************************/

  /** Locate a resource accessible as a file. */
  public File locateSystemResource(String path, boolean forWriting) {
    if (path.startsWith("file:")) {
      // Just remove the "file:" prefix.
      path = path.substring(5);
    }
    if (path.startsWith("/")) {
      // Path starting with "/" is relative to document root
      path = agent.findInterform(path, resourceSearch, forWriting);
      return (path == null)? null : new File(path);
    } else if (path.indexOf(":") >= 0) {
      // URL: fail.
      return null;
    } else {
      // Path not starting with "/" is relative to documentBase.
      if (path.startsWith("./")) path = path.substring(2);
      if (documentBase != null) path = documentBase + path;
      path = agent.findInterform(path, resourceSearch, forWriting);
      return (path == null)? null : new File(path);
    }
  }

  /** Search string that allows locateSystemResource to find .inc files. */
  protected String resourceSearch[] = {
    "xh", "xx", "inc", 
    "html", "xml", "htm", "txt", 
  };

  /** Determine whether a resource name is special. 
   *	In our case, paths starting with <code>pia:</code> require
   *	special handling.
   */
  protected boolean isSpecialPath(String path) {
    return (path.startsWith("pia:"));    
  }

  /** Hook on which to hang any specialized paths supported by a subclass. */
  protected InputStream readSpecialResource(String path) {
    // === reading from the pia is hard, because machines go the wrong way.
    return null;
  }

  /** Hook on which to hang any specialized paths supported by a subclass. */
  public OutputStream writeSpecialResource(String path, boolean append,
					    boolean createIfAbsent,
					   boolean doNotOverwrite) {
    return null;
  }

  /************************************************************************
  ** Sub-processing:
  ************************************************************************/

  /** Process a new subdocument. 
   * 
   * @param in the input.
   * @param ts the tagset.  If null, the current tagset is used.
   * @param cxt the parent context. 
   * @param out the output.  If null, the parent context's output is used.
   */
  public TopContext subDocument(Input in, Context cxt, Output out, Tagset ts) {
    if (ts == null) ts = tagset;
    return new ActiveDoc(in, cxt, out, ts, agent, request, response, resolver);
  }

  /************************************************************************
  ** Handler Utilities:
  ************************************************************************/

  public static ActiveDoc getInterFormContext(Context cxt) {
    TopContext top = cxt.getTopContext();
    return (top instanceof ActiveDoc)? (ActiveDoc)top : null;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public ActiveDoc() {
    super();
  }

  public ActiveDoc(Agent a, Transaction req, Transaction resp, Resolver res) {
    super();
    agent = a;
    request = req;
    response = resp;
    resolver = res;
  }

  public ActiveDoc(Input in, Context cxt, Output out, Tagset ts,
		   Agent a, Transaction req, Transaction resp, Resolver res) {
    super(in, cxt, out, (EntityTable)null);
    agent = a;
    request = req;
    response = resp;
    resolver = res;
    setTagset(ts);
  }

}

