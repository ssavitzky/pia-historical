// Resolver.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.


/**
 * A Resolver (i.e. an instance of PIA.Resolver) acts like a stack of
 * Transaction objects.  A resolver also has a list of agents which have
 * registered their interest.  For each transaction in the list, the
 * resolver attempts to match its features against every agent, and each
 * agent that matches  is given the chance to actOn the transaction.  In
 * general agent.actOn will either push some new transactions, register
 * a handler agent, or both.<p>
 *
 * After each agent has had its chance to act_on the transaction, any
 * registered handlers are called, after which the transaction is
 * discarded. <p>
 *
 * Each time around its loop, the Resolver also checks the time.
 * Approximately once per minute, it polls each Agent in its registry
 * to see whether it has any Crontab entries to run. <p>
 */

package crc.pia;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Enumeration;
import java.util.Date;

import java.net.URL;

import crc.pia.Agent;
import crc.pia.Transaction;

import crc.ds.Queue;
import crc.ds.Table;
import crc.ds.List;

public class Resolver extends Thread {
  /**
   * Attribute index - a collection of agents currently running.
   */

  protected  Table agentCollection;

  /**
   * Attribute index - a collection of computational codes.
   */
  protected Table computers;

  /**
   * Attribute index - whether to stop running
   */
  protected boolean finish = false;

  /**
   * Time of last Crontab check. 
   */
  protected long cronTime = 0;

  /** 
   * Interval between Crontab checks in milliseconds.  Must be less than one
   *	minute to avoid missing a ``once-per-minute'' entry.
   */
  protected static long cronDELAY = 15000;

  /** Constant: one minute. */
  public static final long MINUTE = 60000;
  
  /************************************************************************
  ** Transaction Queue:
  ************************************************************************/

  /**
   * Attribute index - transaction queue.
   */
  protected Queue transactions;

  /**
   * shift -- remove and return the transaction at front of list .
   * If there is no transaction returns null.
   */
  public final synchronized Transaction shift(){
    return (Transaction) transactions.shift();
  }

  /**
   * unshift -- put a transaction to the front of the list. 
   * returns the number of elements
   */ 
  public final synchronized int unshift( Transaction obj ){
    Pia.debug(this, "unshift()");
    int i = transactions.unshift( obj );
    notifyAll();
    return i;
  }

  /**
   * push -- push a transaction onto the end of the list. 
   * returns the number of elements
   */  
  public final synchronized int push( Transaction obj ){
    Pia.debug(this, "push()");
    int i = transactions.push( obj );
    notifyAll();
    //    Pia.debug(this, "current size" + size());
    return i;
  }

  /**
   * pop -- removes a transaction from the back of the queue and returns it. 
   * returns the number of elements
   */ 
  public final synchronized Transaction pop(){
    return (Transaction) transactions.pop();
  }

  /**
   * Number of transactions in queue
   */
  public final synchronized int size(){
    return transactions.size();
  }

  /************************************************************************
  ** Agents:
  ************************************************************************/

  /**
   * Given agent and its name, store it.
   */
  protected void agent( String name, Agent agent ){
    if( name != null && agent != null ){
      agentCollection.put( name, agent );
    }
  }

  /**
   * Get agent collection in a Table
   *
   */
  protected Table agent(){
    return agentCollection;
  }

  /**
   * Register an agent with the resolver. 
   *
   */
  public void registerAgent( Agent agent ){
    String name;

    if ( agent != null ){
      name = agent.name();
      agent( name, agent );
    }

    agent.initialize();
    
  }

  /**
   * Unregister an agent by name, removing and returning it.
   * @return null if no deletion is not successful.
   */
  public Agent unRegisterAgent( String name ){
    Agent deadAgent = null;

    deadAgent = (Agent) agentCollection.remove( name );
    return deadAgent;
  } 

  /**
   * Unregister an agent by reference, removing and returning it.
   * @return null if no deletion is not successful.
   */
  public Agent unRegisterAgent( Agent agent ){
    Agent deadAgent = null;
    String name;

    if( agent != null ){
      name = agent.name();
      deadAgent = (Agent) agentCollection.remove( name );
    }
    return deadAgent;
  } 

  /**
   * agents 
   * @return agents
   */
  public Enumeration agents(){
    return agentCollection.elements();
  }

  /**
   * Agents' names 
   * @return agents' names
   */
  public Enumeration agentNames(){
    return agentCollection.keys();
  }

  /**
   * Find agent by name
   * @return agent according to name
   */
  public Agent agent( String name ){
    Object o = agentCollection.get( name );
    if( o != null )
      return (Agent)o;
    else
      return null;
  }

  /** 
   * Find agent addressed by a pathname.
   */
  public Agent agentFromPath(String path) {

    /* Empty path is handled by Agency. */

    if (path == null || path.equals("/") || path.equals("")) {
      return agent("Agency");
    }

    /* Now check for either /name/ or /type/name */

    if (path.startsWith("/")) path = path.substring(1);
    if (path.startsWith("~")) path = path.substring(1);

    List pathList = new List(new java.util.StringTokenizer(path, "/"));

    Agent a = null;

    if (pathList.nItems() > 1) {
      String name = pathList.at(1).toString();
      String type = pathList.at(0).toString();
      Pia.debug(this, "Looking for agent :" + name);
      a = agent(name);
      if (a == null || !type.equals(a.type())) {
	Pia.debug(this, "Looking for agent :" + name);
	a = agent(type);
      }
    } else {
      String name = pathList.at(0).toString();
      a = agent(name);
    }
    return a;
  }

  /** Run through the agentCollection and tell each Agent to run its
   *	associated Crontab. */
  public void runCrontabs() {

    /* Ensure that this check and last check fall into different 1-minute
     *	buckets as measured since EPOCH. */

    long thisTime = System.currentTimeMillis();
    if (thisTime / MINUTE <= cronTime / MINUTE) return;

    cronTime = thisTime;
    Pia.debug("Running crontabs at cronTime=" + cronTime);
    Enumeration agents = agents();
    while (agents.hasMoreElements()) {
      Agent a = (Agent)agents.nextElement();
      a.handleTimedRequests(cronTime);
    }

  }

  /************************************************************************
  ** Control:
  ************************************************************************/

  /**
   * stop thread 
   */
  protected void shutdown(){
    finish = true;
  }

  /**
   * tell each machine in transaction to shutdown?
   */
  protected void cleanup(boolean restart){
    // off course should check number of transaction
    //pop each transaction and tells its machine to shutdown
    finish = restart;
  }

  protected void finalize() throws IOException{
    cleanup( false );
  } 

  protected void restart(){
    start();
  }

  /************************************************************************
  ** Main Loop:
  ************************************************************************/

  /** Count of the total number of transactions processed. */
  public long count;

  /**
   *  Resolve -- This is the resolver's main loop.  It starts with one or more
   *  incoming transactions that have been pushed onto its queue, and
   *  loops until they're all taken care of.
   */
  public void run(){
    Transaction tran;
    String urlString;
    long delay = 100;

    count = 0;

    // Main loop.  
    //	 Entered with some transactions in the input queue.
    //	 Returns total number of transactions processed.
    while( !finish ){
      //      Pia.debug(this, "Pending items = " + size());
      if( size() == 0 ) synchronized (this) {
	try{
	  // === should really wait for push to signal us. ===
	  //Thread.currentThread().sleep(delay);
	  wait(delay);
	}catch(InterruptedException ex){;}
	catch(Exception e){
	  //	  Pia.debug(this,"exception caught in run");
	  //	  e.printStackTrace();
	}
      }
      else {
	tran = pop();
	count++;

	Pia.debug(this, "Popped "+count+", " + size()+ " left");

	/*
	  Look for matches.
	  Matching agents have their act_on method called with both the
	  transaction and the resolver as arguments; they can either push
	  transactions onto the resolver, push satisfiers onto the
	  transaction, or directly modify the transaction.
	  */
	Pia.debug(this, "Attempting to match");
	int n = match( tran );
	Pia.debug(this, "..." + n + " matches");
	
	/*
	  Tell the transaction to go satisfy itself.  
	  It does this by calling each of the handlers that matched agents
	  have pushed onto its queue, and looking for a true response.
	  */
	
	// do indirectly by notifying transaction that it is resolved,
	// the transaction thread becomes responsible for running satisfy
	Pia.debug(this, "Transaction resolves");
	tran.resolved();      
      }

      if (System.currentTimeMillis() > cronTime + cronDELAY) 
	runCrontabs();
      
    }
    Pia.debug(this, "RUN thread EXITED");
    //    System.out.println("RUN exited");
    cleanup(false);
    
  }

  /************************************************************************
  ** Matching features:
  ************************************************************************/

  /**
   * Find all agents that match the given transaction 
   * each agent that matches gets its actOn method called.
   * @Return the number of matches.
   */

  public int match( Transaction tran ){
    Enumeration e = agents();
    Agent agent;
    int matches = 0;

    /*
     Loop through all the agents looking for matches.
     Every agent that matches is allowed to push new requests onto the
     resolver, or to modify the transaction directly.
    */
    
    while( e.hasMoreElements() ){
      agent = (Agent) e.nextElement();
      Pia.debug(this, "   match " + agent.name() + "?");

      if (tran.matches( agent.criteria() )){
	Pia.debug(this, "   matched " + agent.name());
	agent.actOn( tran, this );
	++ matches;
      }
    }
    return matches;
  }
  
  /************************************************************************
  ** User Agent
  ************************************************************************/

  /**
   * Make a simple request.  This allows the Resolver to serve as
   *	a user agent.  Agents are matched against the transaction.
   *	@return the response transaction.
   *	@param tran the request transaction.
   *	@param fileName if non-null, a file into which the response
   *		content is directed. 
   */
  public Transaction simpleRequest( Transaction tran, String fileName )
       throws IOException{
    File destinationFile;
    FileWriter out = null;

    match( tran );
    tran.satisfy( this );
    Transaction response = pop();
    
    if( fileName != null ){
      try{
	destinationFile = new File( fileName );
	out = new FileWriter( destinationFile );
	String str = response.contentObj().toString();
	out.write( str );
	out.flush();
	out.close();
      }catch(IOException e){
	// I need to throw this
      }
    }
    return response;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public Resolver(){
    agentCollection = new Table();
    computers       = new Table();
    transactions    = new Queue();
    this.start();
  }

}





