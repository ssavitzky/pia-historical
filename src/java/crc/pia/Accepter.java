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
  public boolean DEBUG = false;
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
  protected void shutdown(){
    finish = true;
  }

  /**
   * shutdown socket
   */
  protected void cleanup(boolean restart){
    if( DEBUG )
      System.out.println("cleaning started....");

    try {
      listenSocket.close();
      listenSocket = null;
      finish = false;
    }catch(IOException ex){
      if( DEBUG )
	System.out.println("[cleanup]: IOException while closing server socket.");
      else
	Pia.instance().errLog ("[cleanup]: IOException while closing server socket.");
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
      if (DEBUG)
	System.out.println("There is an exception while listening for connection.");
      else
       Pia.instance().errSys(e, "There is an exception while listening for connection.");
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
    int         port  = clientSocket.getPort();

    String hostName = iaddr.getHostName();
    if ( DEBUG ){
	  PrintStream clientSend = null;
	  DataInputStream clientReceive = null;
	  String nextLine;

	System.out.println("connection from : "+ hostName + " at: " + String.valueOf( port ));
	try{
	  clientSend = new PrintStream( clientSocket.getOutputStream() );
	  clientReceive = new DataInputStream( clientSocket.getInputStream() );
	  clientSend.println("Hello from Accepter");
	  clientSend.flush();

	  while( true ) {
	    nextLine = clientReceive.readLine();
	    System.out.println( nextLine );

	    if (nextLine == null) break;

	    nextLine = nextLine.toLowerCase();
	    if( nextLine.indexOf("quit") == 0) 
	      break;

	    clientSend.println( nextLine );
	  }
	    clientSend.println("bye");
	    clientSend.flush();
	 }catch(IOException e){
	    System.err.println("Failed I/O: " + e);
	  }finally{
	    try{
	      if (clientSend != null) clientSend.close();
	      if (clientReceive != null) clientReceive.close();
	      if (clientSocket != null) clientSocket.close();
	      finish = true;
	    }catch(IOException ee){
	      System.err.println("Failed I/O: " + ee);
	    }
	  }

    }
    else{
      Pia.instance().debug( this, "connection from : "+ hostName + " at: " + String.valueOf( port ) );
      Pia.instance().log( "connection from : "+ hostName + " at: " + String.valueOf( port ) );

      Transaction ts = createRequestTransaction(hostName, port, clientSocket);
    }

  }

 /**
  * Creates a transaction from the client's request (will be private).
  * @return a PIA transaction. 
  */ 
 public Transaction createRequestTransaction ( String addr, int port, Socket client) {
    // Create a request transaction

    Machine machine =  new Machine(addr, port, client);
    return new HTTPRequest( machine );
 }

  /**
   * restart socket
   */
  protected void restart(){
    try {
      listenSocket = new ServerSocket( port );
    }catch(IOException e){
      if( DEBUG )
	System.out.println("There is an exception creating accepter's socket.");
      else
	Pia.instance().errSys(e, "There is an exception creating accepter's socket.");
    }
    if( DEBUG )
      System.out.println("Accepter: listening on port" + port);
    this.start();
  }

  /**
  * Starts thread here
  * 
  */ 
  public Accepter( int port ) throws IOException{
    if(port == 0) port = DEFAULT_PORT;

    if (DEBUG)
      System.out.println("Server starts on port" + String.valueOf(port));
    else{
      Pia.instance().debug(this, "Server starts on port" + String.valueOf(port));
      Pia.instance().log("Server starts on port" + String.valueOf(port));
    }

    this.port = port;
    try {
      listenSocket = new ServerSocket( port );
    }catch(IOException e){
      throw e;
    }
    if( DEBUG )
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
    try{
      new Accepter(port);
    }catch(IOException e){;}
    
  }

}










