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
import java.util.Hashtable;
import java.util.NoSuchElementException;
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
   */
  public void install(Table ht)
       throws NullPointerException, AgentInstallException {

    if( ht == null ) throw new NullPointerException("bad parameter Table ht\n");
    String name      = (String)ht.get("agent");
    String type      = (String)ht.get("type");
    String className = (String)ht.get("class");

    if (name == null)
      throw new AgentInstallException("No agent name");

    if (type == null)
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
	newAgent = (Agent)Class.forName(className).newInstance() ;
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
    try{
      List list = noProxies();
      Enumeration e = list.elements();
      while( e.hasMoreElements() ){
	s = (String)e.nextElement();
	if( s.indexOf(destination) != -1 )
	  return null;
      }
    }catch(NoSuchElementException e ){
    }
    return proxy(protocol);
  }

  /**
   * @return no proxies list from PIA
   */
  public List noProxies() throws NoSuchElementException{
    List list = Pia.instance().noProxies();
    if( list == null ) 
      throw new NoSuchElementException("no-proxies list is empty");
    else
      return list;
  }

  /**
   * @return proxy string given protocol
   */
  public String proxy(String protocol){
    Table ht = Pia.instance().proxies();
    if( !ht.isEmpty() && ht.containsKey( protocol ) ){
      String v = (String)ht.get( protocol );
      return v;
    }
    return null;
  }

  /**
   * Act on a transaction that we have matched.  
   * Since the Agency matches all requests to agents, this means
   * that we need to find the agent that should handle this request
   * and push it onto the transaction.
   */
  public void actOn(Transaction trans, Resolver res){
    String lhost = null;

    boolean isAgentRequest = trans.test("IsAgentRequest");
    
    if(! isAgentRequest ) return;

    URL url = trans.requestURL();
    if(url == null) return;
    
    String path = url.getFile();
    Pia.debug(this, "actOn..." + path);

    if (path.equals("/")) {
      // Root is handled by Agency agent.
      trans.toMachine( machine() );
      return;
    }

    /* Now check for either /name/ or /type/name */

    if (path.startsWith("/")) path = path.substring(1);
    List pathList = new List(new java.util.StringTokenizer(path, "/"));

    Agent agent = res.agentFromPath(path);

    if( agent == null ){
      Pia.debug(this, "Agent not found");
      return;
    }else{
      Pia.debug(this, "Agent found: " + agent.name());
      trans.toMachine( agent.machine() );
    }
  }

  /**
   * Constructor.
   */
  public Agency(String name, String type){
    super(name, type);
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





















