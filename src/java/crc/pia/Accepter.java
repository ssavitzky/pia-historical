// Accepter.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.


/**
 * This object accepts connections on a port.
 */

package crc.pia;

import java.io.*;
import java.net.*;

import crc.pia.Transaction;
import crc.pia.Machine;
import crc.pia.Pia;
import crc.pia.HTTPRequest;

public class Accepter extends Thread {
  /**
   * The default port number if none is given.
   */
  public final static int DEFAULT_PORT=8001;

  /**
   * Attribute index - The port to listen on.
   */
  protected int port;

  /**
   * Attribute index - The socket that Accepter listens on.
   */
  protected ServerSocket listenSocket;

  /**
   * Attribute index - whether to shutdown
   */
  protected boolean finish = false;

  /**
   * Stop receiving requests
   */
  protected void shutdown(){
    finish = true;
  }

  /**
   * Shutdown socket
   */
  protected void cleanup(boolean restart){
    try {
      listenSocket.close();
      listenSocket = null;
      finish = false;
    }catch(IOException ex){
      Pia.debug(this, "[cleanup]: IOException while closing server socket.");
      Pia.errLog ("[cleanup]: IOException while closing server socket.");
    }
  }

  /**
   * Clean up without restarting
   */
  protected void finalize() throws IOException{
    cleanup( false );
  }
  
 /**
  * Loop for connections from clients.
  * @return nothing. 
  */ 
  public void run(){
    try{
      while( !finish && listenSocket != null ){
	Socket clientSocket = listenSocket.accept();
	if( listenSocket != null && clientSocket != null ){
	  handleConnection( clientSocket );
	}
      }
    }catch(IOException e){
      Pia.debug(this, "There is an exception while listening for connection.");
      Pia.errSys(e, "There is an exception while listening for connection.");
    }
    cleanup(false);
  }

  /**
   * This gets called by accepter whenever a new request is received.
   * A transaction is created, and it automatically places itself onto the resolver.
   */ 
  protected void handleConnection(Socket clientSocket) {
   
    InetAddress iaddr = clientSocket.getInetAddress();
    int         port  = clientSocket.getPort();

    String hostName = iaddr.getHostName();

    Pia.debug( this, "connection from : "+ hostName + " at: " + String.valueOf( port ) );
    Pia.log( "connection from : "+ hostName + " at: " + String.valueOf( port ) );
    
    createRequestTransaction(hostName, port, clientSocket);

  }

 /**
  * Creates a transaction from the client's request
  * @return a PIA transaction. 
  */ 
 protected void createRequestTransaction ( String addr, int port, Socket client) {
    // Create a request transaction

    Machine machine =  new Machine(addr, port, client);
    new HTTPRequest( machine );
 }

  /**
   * Restart accepter
   */
  protected void restart(){
    try {
      listenSocket = new ServerSocket( port );
    }catch(IOException e){
      Pia.debug(this, "There is an exception creating accepter's socket.");
      Pia.errSys(e, "There is an exception creating accepter's socket.");
    }
    Pia.verbose("Accepter: listening on port" + port);
    this.start();
  }

  /**
  * Starts thread here
  * 
  */ 
  public Accepter( int port ) throws IOException{
    if(port == 0) port = DEFAULT_PORT;

    this.port = port;
    try {
      listenSocket = new ServerSocket( port );
    }catch(IOException e){
      throw e;
    }

    Pia.verbose("Accepter: listening on port " + port);
    this.start();
  }

}











