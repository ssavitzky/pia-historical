// T_agency.java
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
 * This is the class for the ``agency'' agent; i.e. the one that
 * handles requests directed at agents.  It slso owns the resolver,
 * which may not be a good idea.
 */

package crc.test.pia.agent;
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

import gnu.regexp.RegExp;
import gnu.regexp.MatchInfo;

import crc.pia.agent.Agency;
public class T_agency {
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
      pentagon.attr("if_root", "~/pia/pentagon");
      // looking for an home.if in ~/pia/pentagon
      //not working      System.out.println("Find interform: " + pentagon.findInterform( trans1.requestURL().path ));
      System.exit( 0 );
      /*
      System.out.println("\n\n------>>>>>>> Installing a Dofs agent <<<<<-----------");
      Table ht = new Table();
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





















