// Agency.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

/**
 * This is the class for the ``agency'' agent; i.e. the one that
 * handles requests directed at agents.  It slso owns the resolver,
 * which may not be a good idea.
 */

package crc.pia.agent;
import java.util.NoSuchElementException;

import crc.pia.GenericAgent;
import crc.pia.Resolver;


public class Agency extends GenericAgent {

  public Agency(String name, String type){
    super(name, type);
  }

  /**
   * initialize 
   */
  public void initialize(){
    matchCriterion("request", true);
    matchCriterion("agent_request", true);
    super.initialize();
  }


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
  public void install(HashTable ht) throws NullPointerException, AgentInstallException{
    if( !ht ) throw new NullPointerException("bad parameter Hashtable ht\n");
    String name      = (String)ht.get("agent");
    String type      = (String)ht.get("type");
    String className = (String)ht.get("class");
    Agent newAgent = null;

    if(!type)
      type = name;

    if( className != null ){
      try{
	newAgent = (Agent)Class.forName(className).newInstance() ;
	newAgent.parseOptions(0, ht);
	installAgent( newAgent );
      }catch(Exception ex){
	String err = ("Unable to create agent of class ["
			      + className +"]"
			      + "\r\ndetails: \r\n"
			      + ex.getMessage());
		throw new AgentInstallException(err);
      }
    }
    
  }

  /**
   * return a string indicating the proxy to use for retrieving this request
   * this is for standard proxy notions only, for automatic redirection
   * or re-writes of addresses, use an appropriate agent
   */
  public String proxyFor(String destination, String protocol){
    String s = null;
    try{
      String[] list = noProxies();
      for(int i = 0; i < list.length; i++){
	s = list[i];
	if( s.indexOf("destination") != -1 )
	  return null;
      }
    }catch(NoSuchElementException e ){
    }
    return proxy(protocol);
  }

  /**
   * @return no proxies list from PIA
   */
  public String[] noProxies() throws NoSuchElementException{
    String[] list = Pia.instance().proxy();
    if( !list ) 
      throw new NoSuchElementException("no-proxies list is empty");
    else
      return list;
  }

  /**
   * @return proxy string given protocol
   */
  public String proxy(String protocol){
    HashTable ht = Pia.instance().proxy();
    if( !ht.isEmpty() && ht.containsKey( protocol ) ){
      String v = (String)v.get( protocol );
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
    if( !trans.is("agent_request") ) return;
    URL url = trans.requestURL();
    if(!url) return;
    
    String path = url.getFile();
    String lpath = "";
    if( path )
      String lpath = path.toLowerCase();
    
    String name = name();
    if( lpath.startsWith("/") ){
	StreamParser sp = new StreamParser( new StringBufferInputStream(lpath) );
	try{
	  Object o = sp.nextToken();  // first "/"
	  o = sp.nextToken();         
	  if( o instanceof String ){
	    name = (String)o;         // \w+ equivalent
	  }
	}catch(IOException e1){
	  e.printStackTrace();
	}catch(NoSuchElementException e2){
	  
	}
    }// if

    Agent agent = Pia.instance().resolver().agent( name );
    if( ! agent ){
      return;
    }else{
      trans.toMachine( agent.machine() );
    }







  }
}








