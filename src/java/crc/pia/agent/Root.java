// Root.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

/**
 * This is the class for the ``Root'' agent; i.e. the one that handles
 *	requests directed at the PIA.  It contains an <code>actOn</code>
 *	method that determines which agent should handle a request.
 */

package crc.pia.agent;
import java.io.FileInputStream;
import java.io.InputStream;

import java.util.Enumeration;

import java.net.URL;

import crc.ds.Table;
import crc.ds.List;
import crc.ds.Criterion;

import crc.pia.GenericAgent;
import crc.pia.Resolver;
import crc.pia.Agent;
import crc.pia.Pia;
import crc.pia.Transaction;
import crc.pia.Machine;
import crc.pia.HTTPRequest;

public class Root extends GenericAgent {

  // === The proxy stuff is almost certainly obsolete; 
  // === it was only used in Machine, and has been moved there.

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
   * <p> Since the Root matches all requests to agents, this means
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

    // Forbid paths containing .. (a little extreme, but it works)
    if (path.indexOf("..") >= 0) return;

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

  /** The directory (under ROOT) in which we keep root files. */
  protected String rootPrefix = null;

  /** Test path to see whether it is a valid root path. */
  protected boolean isValidRootPath(String path) {
    // First see if it's a possibility.
    if (!isPossibleRootPath(path)) return false;
    if (path.startsWith("/~")
	|| path.startsWith("/%7e") || path.startsWith("/%7E")) return true;
    return null != findInterform(rewriteRootPath(path));
  }

  /** Test path to see whether it is a possible root path. 
   *	This is indicated by the absence of a leading <code>/Root</code>.
   *	Note that by this point we have already established (in Root's
   *	<code>actOn</code> method) that either Root or no valid agent
   *	owns the path.
   */
  protected boolean isPossibleRootPath(String path) {
    // We've already checked for an agent at this point, so we know that if
    // it starts with ROOT it's not a root path.
    return !path.startsWith("/" + name());
  }

  /** Rewrite a root path.  
   *	Correctly handle the case where a legacy <code>ROOTindex.if</code>
   *	exists in the user's <code>.pia/Agents/Root</code> directory.
   *	Otherwise, rewrite to <code>Root/ROOT/<em>path</em></code>.
   */
  protected String rewriteRootPath(String path) {
    if (path.equals("/")) {
      path = "/index";
    } 
    return (rootPrefix == null)? "/" + name() + path : "/" + rootPrefix + path;
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
  public Root(String name, String type){
    super(name, type);
  }

  /** Default constructor. */
  public Root() {
    super();
  }

  /**
   * initialize 
   * 2/19/99 pg "put" was not matching
   * and agency would not start
   */
  public void initialize() {
    if (initialized) return;
    // put("criteria", "Request Agent_request");
    criteria().push(Criterion.toMatch("IsRequest", true));
    criteria().push(Criterion.toMatch("IsAgentRequest", true));
    super.initialize();
  }

}





















