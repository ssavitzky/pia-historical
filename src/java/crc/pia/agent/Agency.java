// Agency.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

/**
 * This is the class for the ``agency'' agent; i.e. the one that
 * handles requests directed at agents.  It slso owns the resolver,
 * which may not be a good idea.
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

import crc.util.regexp.RegExp;
import crc.util.regexp.MatchInfo;

public class Agency extends GenericAgent {
  /**
   * take the agent off the resolver list
   *
   */
  public void unInstallAgent(String name){
    Pia.instance().resolver().unRegisterAgent( name );
  }

  /**
   * install agent
   *
   */
  public void installAgent(Agent newAgent){
    Pia.instance().resolver().registerAgent( newAgent );
  }

  /**
   * Install a named agent.  Automatically loads the class if necessary.
   *
   */
  public void install(Table ht)
       throws NullPointerException, AgentInstallException {

    if( ht == null ) throw new NullPointerException("bad parameter Table ht\n");
    String name      = (String)ht.get("agent");
    String type      = (String)ht.get("type");
    String className = null;
    className = (String)ht.get("class");
    Agent newAgent = null;

    if( name == null )
      throw new AgentInstallException("No agent name");

    if(type == null )
      type = name;

    String zname = null;
    if( className == null ){
      char[] foo = new char[1]; 
      foo[0] = type.charAt(0);

      // Capitalize name.  Should really check for "class" attribute,
      //	=== Should preserve case in rest of agent name ===
      //	=== should use interform.Util.javaName ===
      zname = (new String( foo )).toUpperCase();
      if (type.length() > 1) zname += type.substring(1).toLowerCase();

      className = "crc.pia.agent." + zname; 
    }

    if( className != null ){
      try{
	newAgent = (Agent)Class.forName(className).newInstance() ;
	newAgent.name( name );
	newAgent.type( type );
      }catch(Exception ex){
      }
    }
    if (newAgent == null) newAgent = new GenericAgent(name, type);
    newAgent.initialize();
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
    String name = name();
    String lhost = null;

    Pia.debug(this, "actOn...");
    boolean isAgentRequest = trans.test("IsAgentRequest");
    
    if(! isAgentRequest ) return;

    URL url = trans.requestURL();
    if(url == null) return;
    
    String path = url.getFile();
    RegExp re = null;
    MatchInfo mi = null;

    try{
      re = new RegExp("^/(\\w+)/*");
      mi = re.match( path );
    }catch(Exception e){;}
    if( mi !=null ){
      String matchString = mi.matchString();

      int begin = mi.start();
      int end   = mi.end();
      if( matchString.endsWith("/") )
	name = path.substring( begin+1, end-1 );
      else
	name = path.substring( begin+1, end );
    }
    
    Pia.debug(this, "Looking for agent :" + name);
    Agent agent = res.agent( name );
    if( agent == null ){
      Pia.debug(this, "Agent not found");
      return;
    }else{
      Pia.debug(this, "Agent found");
      trans.toMachine( agent.machine() );
    }
  }

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





















