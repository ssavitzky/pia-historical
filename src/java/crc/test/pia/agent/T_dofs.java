// T_dofs.java
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


package crc.test.pia.agent;

import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.DataInputStream;
import java.io.StringBufferInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.util.Date;
import java.util.NoSuchElementException;
import java.io.FileNotFoundException;
import java.util.Vector;
import java.util.Properties;

import java.net.URL;
import java.net.MalformedURLException;

import crc.pia.PiaRuntimeException;
import crc.pia.GenericAgent;
import crc.pia.FormContent;
import crc.pia.Resolver;
import crc.pia.Agent;
import crc.pia.Pia;
import crc.pia.Transaction;
import crc.pia.Machine;
import crc.pia.HTTPRequest;
import crc.pia.HTTPResponse;
import crc.pia.Content;
import crc.content.ByteStreamContent;
import crc.ds.Features;
import crc.ds.Table;
import crc.ds.List;

import gnu.regexp.RegExp;
import gnu.regexp.MatchInfo;
import crc.util.Utilities;

import w3c.www.http.HTTP;
import crc.pia.agent.Dofs;
import crc.pia.agent.Agency;
import crc.pia.ThreadPool;

public class T_dofs{
  private static void sleep(int howlong){
    Thread t = Thread.currentThread();
    
    try{
      t.sleep( howlong );
    }catch(InterruptedException e){;}
    
  }


  public static Agency setupAgency(){
    Agency.DEBUG=true;
    Agency pentagon = new Agency("pentagon", "agency");

    System.out.println("\n\nDumping options -- name , type");
    System.out.println("Option for name: "+ pentagon.attr("name"));
    System.out.println("Option for type: "+pentagon.attr("type"));
    System.out.println("Version " + pentagon.version());
    String path = null;
    System.out.println("Agent url: " + pentagon.agentUrl( path ));
    pentagon.attr("agent_directory", "~/pia/pentagon");
    System.out.println("Agent directory: " + pentagon.agentDirectory());
    pentagon.attr("agent_file", "~/pia/pentagon/foobar.txt");
    List files = pentagon.fileAttribute("agent_file");
    System.out.println("Agent file: " + (String)files.at(0));


    System.out.println("\n\nTesting proxyFor -- http");
    String proxyString = pentagon.proxyFor("napa", "http");
    if( proxyString != null )
      System.out.println( proxyString );
    return pentagon;
  }

 private static void printusage(){
    System.out.println("Needs to know what kind of test");
    System.out.println("For test 1, here is the command --> java crc.pia.agent.Dofs -1 dofsagent.txt");
    System.out.println("For test 2, here is the command --> java crc.pia.agent.Dofs -2 dofsgetdir.txt");
    System.out.println("For test 3, here is the command --> java crc.pia.agent.Dofs -3 dofsgetfile.txt");
    System.out.println("For test 4, here is the command --> java crc.pia.agent.Dofs -4 dofsheader.txt");
  }

  /**
   * For testing.
   * 
   */ 
 public static void main(String[] args){

    if( args.length < 2 ){
      printusage();
      System.exit( 1 );
    }

    if( args[0].equals ("-1") && args[1] != null )
      test( args[1] );
    else if( args[0].equals ("-2") && args[1] != null )
      test( args[1] );
    else if( args[0].equals ("-3") && args[1] != null )
      test( args[1] );
    else if( args[0].equals ("-4") && args[1] != null )
      test( args[1] );
    else{
      printusage();
      System.exit( 1 );
    }

  }


  public static void test(String filename){
    Agency pentagon = setupAgency();
    try{
      InputStream in = new FileInputStream (filename);
      Machine machine1 = new Machine();
      machine1.setInputStream( in );
      machine1.setOutputStream( System.out );

      Transaction trans1 = new HTTPRequest( machine1, true );
      Thread thread1 = new Thread( trans1 );
      thread1.start();

      while( true ){
	sleep( 1000 );
	if( !thread1.isAlive() )
	  break;
      }
     
      trans1.assert("IsAgentRequest", new Boolean( true ) );

      System.out.println("\n\n------>>>>>>> Installing a Dofs agent <<<<<-----------");

      Dofs.DEBUG=true;

      Table ht = new Table();
      ht.put("agent", "popart");
      ht.put("type", "Dofs");
      ht.put("root", "~/");
      ht.put("all", "false");
      pentagon.install( ht );
      
      Resolver res = Pia.instance().resolver();

      // put dofs' machine as toMachine of transaction
      pentagon.actOn( trans1, res );

      // will eventually call getRequest of dofs' machine

      trans1.handleRequest( res );

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

}






















































