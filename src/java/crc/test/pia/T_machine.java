// T_machine.java
// $Id$

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/



/**
 * This object represents machines that envolve in a communication transaction.
 * Ideally these should be persistent so that we can keep track of what
 * kind of browser or server we're talking to, but at the moment we
 * don't do that.
 */

package crc.test.pia;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.StringBufferInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.net.ServerSocket;
import java.net.InetAddress;

import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.net.MalformedURLException;
import java.util.Enumeration;

import crc.pia.Content;
import crc.pia.HTTPResponse;
import crc.pia.Transaction;
import crc.pia.Machine;
import crc.pia.HTTPRequest;
import crc.pia.Pia;
import crc.pia.ThreadPool;
import crc.pia.Resolver;

public class T_machine {
  /**
   * for debugging only
   */
  private static void sleep(int howlong){
    Thread t = Thread.currentThread();
    
    try{
      t.sleep( howlong );
    }catch(InterruptedException e){;}
    
  }


  private static void test1( String filename, boolean proxy ){
    System.out.println("This test make use of a server that returns a text/html page.");
    System.out.println("The server is in the test directory and it runs with default port =6666.");
    System.out.println("The file get_machine.txt contains a request for the page.");
    System.out.println("If proxy is true, request is tested thru a proxy.  In the setProxy method, there is a line w/ p=http://..., you should substitute appropriate proxy address for your machine.");
    System.out.println("If proxy is false, java's URL is used to get the page.");

    try{
      // do not create any request
      crc.pia.GenericAgent.DEBUG = true;
      InputStream in = new FileInputStream (filename);
      Machine machine1 = new Machine(Pia.instance().host(), 8853);

      machine1.setInputStream( in );
      machine1.setOutputStream( System.out );

      boolean debug = true;
      Transaction trans1 = new HTTPRequest( machine1, debug );

      Thread thread1 = new Thread( trans1 );
      thread1.start();
     
      while( true ){
	sleep( 1000 );
	if( !thread1.isAlive() )
	  break;
      }

      trans1.handleRequest(Pia.instance().resolver());

      ThreadPool tp = Pia.instance().threadPool();
      while( true ){
	sleep( 1000 );
	if( !tp.isThreadRunning() )
	  break;
      }
      System.exit(0);
    }catch(Exception e ){
      System.out.println( e.toString() );
    }
  }

  private static void test2(String filename){
    try{
      System.out.println("Testing response w/ from and to machines as arguments.");
      System.out.println("From machine gets its data from response.txt file.");
      System.out.println("Also control strings --major tom --is added to the output.");
      
      InputStream in = new FileInputStream (filename);
      Machine machine1 = new Machine(Pia.instance().host(), 9497);

      machine1.setInputStream( in );
      
      Machine machine2 = new Machine(Pia.instance().host(), 9949);
      machine2.setOutputStream( System.out );
      
      boolean debug = true;
      Transaction trans1 = new HTTPResponse( machine1, machine2, debug );
      Thread thread1 = new Thread( trans1 );
      thread1.start();
      
      while( true ){
	sleep( 1000 );
	if( !thread1.isAlive() )
	  break;
      }

      trans1.addControl( "major" );
      trans1.addControl( "tom" );
      Resolver res = null;
      machine2.sendResponse( trans1, res );
      System.exit( 0 );
    }catch(Exception e ){
      System.out.println( e.toString() );
    }
  }


  private static void printusage(){
    System.out.println("Needs to know what kind of test");
    System.out.println("For test 1, here is the command --> java crc.pia.Machine -1 -proxy get_machine.txt");
    System.out.println("For test 1, here is the command --> java crc.pia.Machine -1 -noproxy get_machine.txt");
    System.out.println("For test 2, here is the command --> java crc.pia.Machine -2 response.txt");
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
    if(args.length == 2){
      if( args[0].equals ("-2") && args[1] != null )
	test2( args[1] );
    }else
    
    if (args.length == 3 ){
      if( args[0].equals ("-1") && (args[1].equals ("-proxy") || args[1].equals ("-noproxy")) && args[2] != null )
	if( args[1].equals ("-proxy"))
	  test1( args[2], true );
	else
	  test1( args[2], false );
      else{
	printusage();
	System.exit( 1 );
      }
    }
    
  }
}














