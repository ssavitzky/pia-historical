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

import java.net.URL;

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
  public boolean DEBUG = false;
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
  public void install(Hashtable ht) throws NullPointerException, AgentInstallException{
    if( ht == null ) throw new NullPointerException("bad parameter Hashtable ht\n");
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
      zname = (new String( foo )).toUpperCase();
      String therest = type.substring(1);
      zname += therest;

      className = "crc.pia.agent." + zname; 
    }

    if( className != null ){
      try{
	newAgent = (Agent)Class.forName(className).newInstance() ;
	newAgent.name( name );
	newAgent.type( zname );
	newAgent.initialize();
	newAgent.parseOptions(ht);
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
  public String[] noProxies() throws NoSuchElementException{
    String[] list = Pia.instance().noProxies();
    if( list == null ) 
      throw new NoSuchElementException("no-proxies list is empty");
    else
      return list;
  }

  /**
   * @return proxy string given protocol
   */
  public String proxy(String protocol){
    Hashtable ht = Pia.instance().proxies();
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

    Pia.instance().debug(this, "actOn...");
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
    
    if( DEBUG ){
      System.out.println("From act on: agent name is "+name);
    }

    Pia.instance().debug(this, "Looking for agent :" + name);
    Agent agent = res.agent( name );
    if( agent == null ){
      Pia.instance().debug(this, "Agent not found");
      return;
    }else{
      Pia.instance().debug(this, "Agent found");
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

 private static void printusage(){
    System.out.println("Here is the command --> java crc.pia.agent.Agency agency.txt");
  }

  /**
   * for debugging only
   */
  private static void sleep(int howlong){
    Thread t = Thread.currentThread();
    
    try{
      t.sleep( howlong );
    }catch(InterruptedException e){;}
    
  }
  
  /**
   * For testing.
   * 
   */ 
  public static void main(String[] args){

    if( args.length == 0 ){
      printusage();
      System.exit( 1 );
    }

    Agency pentagon = new Agency("pentagon", "agency");
    pentagon.DEBUG = true;

    System.out.println("\n\nDumping options -- name , type");
    System.out.println("Option for name: "+ pentagon.optionAsString("name"));
    System.out.println("Option for type: "+pentagon.optionAsString("type"));
    System.out.println("Version " + pentagon.version());
    String path = null;
    System.out.println("Agent url: " + pentagon.agentUrl( path ));
    pentagon.option("agent_directory", "~/pia/pentagon");
    System.out.println("Agent directory: " + pentagon.agentDirectory());
    pentagon.option("agent_file", "~/pia/pentagon/foobar.txt");
    String files[] = pentagon.fileAttribute("agent_file");
    System.out.println("Agent file: " + files[0]);


    System.out.println("\n\nTesting proxyFor -- http");
    String proxyString = pentagon.proxyFor("napa", "http");
    if( proxyString != null )
      System.out.println( proxyString );

    if( args[0] == null ) System.exit( 1 );

    String filename = args[0];
    try{
      InputStream in = new FileInputStream (filename);
      Machine machine1 = new Machine();
      machine1.setInputStream( in );

      boolean debug = true;
      Transaction trans1 = new HTTPRequest( machine1, debug );
      Thread thread1 = new Thread( trans1 );
      thread1.start();

      while( true ){
	sleep( 1000 );
	if( !thread1.isAlive() )
	  break;
      }


      trans1.assert("IsAgentRequest", new Boolean( true ) );
      pentagon.actOn( trans1, Pia.instance().resolver() );
      pentagon.option("if_root", "~/pia/pentagon");
      // looking for an home.if in ~/pia/pentagon
      System.out.println("Find interform: " + pentagon.findInterform( trans1.requestURL(), false ));
      System.exit( 0 );
      /*
      System.out.println("\n\n------>>>>>>> Installing a Dofs agent <<<<<-----------");
      Hashtable ht = new Hashtable();
      ht.put("agent", "Dofs");
      ht.put("type", "dofs");
      pentagon.install( ht );
      */
    }catch(Exception e ){
      System.out.println( e.toString() );
    }

    System.out.println("done");
  }



}





















