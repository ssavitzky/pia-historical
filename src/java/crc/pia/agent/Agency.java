// Agency.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

/**
 * This is the class for the ``Agency'' agent; i.e. the one that
 *	handles requests directed at agents.  It contains the specialized
 *	code that installs agents, and an <code>actOn</code> method that
 *	determines which agent should handle a request.
 */

package crc.pia.agent;
import java.io.FileInputStream;
import java.io.InputStream;

import java.util.Enumeration;

import java.net.URL;

import crc.ds.Table;
import crc.ds.List;
import crc.pia.GenericAgent;
import crc.pia.Resolver;
import crc.pia.Agent;
import crc.pia.Pia;
import crc.pia.Transaction;
import crc.pia.Machine;
import crc.pia.HTTPRequest;

public class Agency extends GenericAgent {
  /**
   * Uninstall (unRegister) an agent.
   */
  public void unInstallAgent(String name){
    Pia.instance().resolver().unRegisterAgent( name );
  }

  /**
   * Install (register) an agent.
   */
  public void installAgent(Agent newAgent){
    Pia.instance().resolver().registerAgent( newAgent );
  }

  /**
   * Create and install a named agent.
   *	Automatically loads the class if necessary.
   *	@param ht initial options.
   *	@exception crc.pia.agent.AgentInstallException if problems are
   *	  found, for example a null table or missing parameter.
   */
  public void install(Table ht)
       throws NullPointerException, AgentInstallException {

    if( ht == null ) throw new NullPointerException("bad parameter Table ht\n");
    String name      = (String)ht.get("agent");
    String type      = (String)ht.get("type");
    String className = (String)ht.get("class");

    if (name == null || name.equals(""))
      throw new AgentInstallException("No agent name");

    if (type == null || type.equals(""))
      type = name;

    /* Compute a plausible class name from the type. */

    if (className == null){
      char[] foo = new char[1]; 
      foo[0] = type.charAt(0);

      // Capitalize name.  
      //	=== Should preserve case in rest of agent name ===
      //	=== should use interform.Util.javaName ===
      String zname = (new String( foo )).toUpperCase();
      if (type.length() > 1) zname += type.substring(1).toLowerCase();
      className = "crc.pia.agent." + zname; 
    } else if (className.length() > 0 && className.indexOf('.') < 0) {
      className = "crc.pia.agent." + className; 
    }

    /* Load the class, if it exists. */

    Agent newAgent = null;
    if( className != null && className.length() > 0){
      try{
	newAgent = (Agent) (Class.forName(className).newInstance()) ;
	newAgent.name( name );
	newAgent.type( type );
      }catch(Exception ex){
      }
    }

    /* If the class doesn't exist, use GenericAgent. */

    if (newAgent == null) newAgent = new GenericAgent(name, type);

    /* Install and initialize the new agent.  The Resolver actually 
       does the intialization, after installing the agent.
     */

    newAgent.parseOptions(ht);
    installAgent( newAgent );
  }

  /**
   * return a string indicating the proxy to use for retrieving this request
   * this is for standard proxy notions only, for automatic redirection
   * or re-writes of addresses, use an appropriate agent
   */
  public String proxyFor(String destination, String protocol){
    String s = null;
    List list = noProxies();

    if (list != null && destination != null) {
      Enumeration e = list.elements();
      while( e.hasMoreElements() ){
	s = (String)e.nextElement();
	if( s.indexOf(destination) != -1 )
	  return null;
      }
    }
    return proxy(protocol);
  }

  /**
   * @return no proxies list from PIA
   */
  public List noProxies() {
    return Pia.instance().noProxies();
  }

  /**
   * @return proxy string given protocol
   */
  public String proxy(String protocol){
    if( protocol == null ) return null;

    Table ht = Pia.instance().proxies();

    String myprotocol = protocol.toLowerCase().trim();
    //    if( !ht.isEmpty() && ht.containsKey( myprotocol ) ){
    if( ht.isEmpty() != false ){
      if(ht.containsKey( myprotocol ) ){
	String v = (String)ht.get( myprotocol );
	return v;
      }
    }

    return null;
  }

  /**
   * Act on a transaction that we have matched. 
   *
   * <p> Since the Agency matches all requests to agents, this means
   * 	 that we need to find the agent that should handle this request
   * 	 and push it onto the transaction.
   */
  public void actOn(Transaction trans, Resolver res){
    boolean isAgentRequest = trans.test("IsAgentRequest");
    if (!isAgentRequest) return; // sanity check -- it's an agent request.

    URL url = trans.requestURL();
    if (url == null) return;	 // sanity check -- there's a URL
    
    String path = url.getFile();
    Pia.debug(this, "actOn..." + path);

    // Ask the resolver for the correct agent.  
    Agent agent = res.agentFromPath(path);

    if (agent != null) {
      Pia.debug(this, "Agent found: " + agent.name());
      trans.toMachine( agent.machine() );
    } else if (isValidRootPath(path)) {
      Pia.debug(this, "Root path redirected to " + name());
      trans.toMachine(this.machine()); 
    } else {
      Pia.debug(this, "Agent not found");
    }
  }

  /** The directory (under Agency) in which we keep root files. */
  protected String rootPrefix = "ROOT";

  /** Test path to see whether it is a valid root path. */
  protected boolean isValidRootPath(String path) {
    // First see if it's a possibility.
    if (!isPossibleRootPath(path)) return false;
    return null != findInterform(rewriteRootPath(path));
  }

  /** Test path to see whether it is a possible root path. 
   *	This is indicated by the absence of a leading <code>/Agency</code>.
   *	Note that by this point we have already established (in Agency's
   *	<code>actOn</code> method) that either Agency or no valid agent
   *	owns the path.
   */
  protected boolean isPossibleRootPath(String path) {
    // We've already checked for an agent at this point, so we know that if
    // it starts with Agency it's not a root path.
    return !path.startsWith("/" + name());
  }

  /** Rewrite a root path.  
   *	Correctly handle the case where a legacy <code>ROOTindex.if</code>
   *	exists in the user's <code>.pia/Agents/Agency</code> directory.
   *	Otherwise, rewrite to <code>Agency/ROOT/<em>path</em></code>.
   */
  protected String rewriteRootPath(String path) {
    if (path.equals("/")) {
      // root index directory -- might be ROOTindex
      if (findInterform("ROOTindex") != null) return "/ROOTindex";
      return "/" + rootPrefix + "/index";
    } else {
      return "/" + rootPrefix + path;
    }
  }

  /** Perform any necessary rewriting on the given path. */
  protected String rewriteInterformPath(Transaction request, String path) {
    if (isPossibleRootPath(path)) {
      return rewriteRootPath(path);
    } else {
      return path;
    }
  }

  /**
   * Constructor.
   */
  public Agency(String name, String type){
    super(name, type);
  }

  /** Default constructor. */
  public Agency() {
    super();
  }

  /**
   * initialize 
   */
  public void initialize() {
    matchCriterion("IsRequest", true);
    matchCriterion("IsAgentRequest", true);
    super.initialize();
  }

}





















