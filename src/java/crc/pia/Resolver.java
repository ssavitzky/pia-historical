// Resolver.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.


/**
 *A Resolver (i.e. an instance of PIA::Resolve) acts like a stack of
 * Transaction objects.  A resolver also has a list of agents which have
 * registered their interest.  For each transaction in the list, the
 * resolver attempts to match its features against every agent, and each
 * agent that matches  is given the chance to act_on the transaction.  In
 * general agent->act_on will either push some new transactions, register
 * a handler agent, or both.
 *
 * After each agent has had its chance to act_on the transaction, any
 * registered handlers are called, after which the transaction is
 * discarded. 
 */

package crc.pia;
import java.io.File;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.IOException;
import java.io.FileOutputStream;

import java.util.Vector;

import java.net.URL;
import crc.pia.Agent;
import crc.ds.Queue;


public class Resolver extends Thread{
  /**
   * Attribute index - a collection of agents currently running.
   */

  protected  Hashtable agentCollection;

  /**
   * Attribute index - a collection of computational codes.
   */
  protected Hashtable computers;

  /**
   * Attribute index - transaction queue.
   */
  protected Queue transactions;

  /**
   * Attribute index - whether to stop running
   */
  protected boolean finish = false;
  
  /**
   * queue -- returns transaction list
   * 
   */ 
  protected Enumeration queue(){
    return transactions.queue();
  }

  /**
   * shift -- remove and return the transaction at front of list .
   * If there is no transaction returns null.
   */
  public Object shift(){
    return transactions.shift();
  }

  /**
   * unshift -- put a transaction to the front of the list. 
   * returns the number of elements
   */ 
  public int unshift( Object obj ){
    return transactions.unshift( obj );
  }

  /**
   * push -- push a transaction onto the end of the list. 
   * returns the number of elements
   */  
  public int push( Object obj ){
    Pia.instance().debug(this, "Resolver push get called");
    return transactions.push( obj );
  }

  /**
   * pop -- removes a transaction from the back of the queue and returns it. 
   * returns the number of elements
   */ 
  public Object pop(){
    return transactions.pop();
  }

  /**
   * Number of transactions in queue
   *
   */
  public int size(){
    return transactions.size();
  }

  /**
   * Given agent and its name, store it.
   *
   */
  protected void agent( String name, Agent agent ){
    String zname = null;
    if( name != null && agent != null ){
      zname = name.toLowerCase();
      agentCollection.put( zname, agent );
    }
  }

  /**
   * Get agent collection in a hashtable
   *
   */
  protected Hashtable agent(){
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
   * agentNames 
   * @return agents' names
   */
  public Enumeration agentNames(){
    return agentCollection.keys();
  }

  /**
   * @return agent according to name
   */
  public Agent agent( String name ){
    String zname = name.toLowerCase();
    Object o = agentCollection.get( zname );
    if( o != null )
      return (Agent)o;
    else
      return null;
  }

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
    finish = false;
  }

  protected void finalize() throws IOException{
    cleanup( false );
  } 

  protected void restart(){
    start();
  }

  /**
   *  Resolve -- This is the resolver's main loop.  It starts with one or more
   *  incoming transactions that have been pushed onto its queue, and
   *  loops until they're all taken care of.
   */
  public void run(){
    int count = 0;
    int numb = size();
    Transaction tran;
    String urlString;
    long delay = 1000;
    URL url;

    // Main loop.  
    //	 Entered with some transactions in the input queue.
    //	 Returns total number of transactions processed.
    while( !finish ){

      if( size() == 0 ){
	try{
	  Thread.currentThread().sleep(delay);
	}catch(InterruptedException ex){;}
      }
      else{
	tran = (Transaction) pop();

	Pia.instance().debug(this, "Remaining number of transaction " + Integer.toString( size() ) );

	/*
	  Look for matches.
	  Matching agents have their act_on method called with both the
	  transaction and the resolver as arguments; they can either push
	  transactions onto the resolver, push satisfiers onto the
	  transaction, or directly modify the transaction.
	  */
	Pia.instance().debug(this, "Before match making...");
	match( tran );
	Pia.instance().debug(this, "After match making...");
	
	/*
	  Tell the transaction to go satisfy itself.  
	  It does this by calling each of the handlers that matched agents
	  have pushed onto its queue, and looking for a true response.
	  */
	
	// do indirectly by notifying transaction that it is resolved,
	// the transaction thread becomes responsible for running satisfy
	Pia.instance().debug(this, "Transaction please resolve");
	tran.resolved();      
      }
      
    }
    cleanup(false);
    
  }

  /**
   * Find all agents that match the given transaction.
   * Each agent that matches gets its act_on method called.
   * Returns the number of matches.
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

      if (tran.matches( agent.criteria() )){
	agent.actOn( tran, this );
	++ matches;
      }
      
    }
    return matches;

  }
  
  /**
   * Return a response to a caller
   *
   */
  public Transaction simpleRequest( Transaction tran, String fileName )throws IOException{
    File destinationFile;
    FileOutputStream destination = null;
    PrintStream out = null;
    Transaction response;
    /*
      Return a response to a caller.
      This lets the resolver serve as a user agent in place of LWP.
    */
    match( tran );
    tran.satisfy( this );
    response = (Transaction)pop();
    
    if( fileName != null ){
      try{
	destinationFile = new File( fileName );
	destination = new FileOutputStream( destinationFile );
	out = new PrintStream( destination );
	String str = response.contentObj().toString();
	out.print( str );
	out.flush();
	out.close();
      }catch(IOException e){
	// I need to throw this
      }
    }
    return response;
  }

  public Resolver(){
    agentCollection = new Hashtable();
    computers       = new Hashtable();
    transactions    = new Queue();
    this.start();
  }

}





