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

public class Accepter extends Thread {
  /**
   * The default port number if non is given.
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
   * stop thread 
   */
  protected shutdown(){
    finish = true;
  }

  /**
   * shutdown socket
   */
  protected cleanup(boolean restart){
    try {
      listenSocket.close();
      listenSocket = null;
      finish = false;
    }catch(IOException ex){
      Pia.errlog ("[cleanup]: IOException while closing server socket.");
    }
  }

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
	if( listenSocket != null && clientSocket != null )
	  handleConnection( clientSocket );
      }
    }catch(IOException e){
       errSys(e, "There is an exception while listening for connection.");
    }
    cleanup(false);
  }

  /**
  * this gets called by accepter whenever new request is received
  * ,creates transaction and places on stack of resolver (will be private).
  * @return nothing. 
  */ 
  public void handleConnection(Socket clientSocket) {
   
    InetAddress iaddr = clientSocket.getInetAddress();
    int         port  = clientSocket.port();

    String hostName = iaddr.getHostName();
    logMsg( "connection from : "+ hostName + " at: " + String.ValueOf( port ) );
    
    Transaction ts = createRequestTransaction(hostName, port, clientSocket);
    Pia.resolver.push( ts );
   
  }

 /**
  * Creates a transaction from the client's request (will be private).
  * @return a PIA transaction. 
  */ 
 public Transaction createRequestTransaction ( String addr, int port, Socket client) {
    // Create a request transaction

    Machine machine =  new Machine(addr, port, in);
    return new Transaction( machine );
 }

  /**
   * restart socket
   */
  protected void restart(){
    try {
      listenSocket = new ServerSocket( port );
    }catch(IOException e){
       errSys(e, "There is an exception creating accepter's socket.");
    }
    System.out.println("Accepter: listening on port" + port);
    this.start();
  }

  /**
  * Starts thread here
  * 
  */ 
  public Accepter( int port ){
    if(port == 0) port = DEFAULT_PORT;

    logMsg("Server starts on port" + String.ValueOf(port));

    this.port = port;
    try {
      listenSocket = new ServerSocket( port );
    }catch(IOException e){
       errSys(e, "There is an exception creating accepter's socket.");
    }
    System.out.println("Accepter: listening on port" + port);
    this.start();
  }


 /**
  * Create Accepter for debugging.
  * 
  */ 
  public static void main(String[] args){
    int port = 0;
    if(args.length ==1){
      try {
	port = Integer.parseInt(args[0]);
      } catch(NumberFormatException e){
	port = 0;
      }
    }
    new Accepter(port);
  }

}










