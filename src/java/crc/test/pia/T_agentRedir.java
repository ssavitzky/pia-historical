// T_agentRedir.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.test.pia;

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

import crc.gnu.regexp.RegExp;
import crc.gnu.regexp.MatchInfo;
import crc.util.Utilities;

import w3c.www.http.HTTP;
import crc.pia.agent.Dofs;
import crc.pia.agent.Agency;
import crc.pia.ThreadPool;

public class T_agentRedir{
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
    //System.out.println("Option for name: "+ pentagon.optionAsString("name"));
    //System.out.println("Option for type: "+pentagon.optionAsString("type"));
    System.out.println("Version " + pentagon.version());

    return pentagon;
  }

 private static void printusage(){
    System.out.println("Needs to know what kind of test");
    System.out.println("For test, here is the command --> java crc.pia.T_agentRedir input/get_agentredir.txt");
  }

  /**
   * For testing.
   * 
   */ 
 public static void main(String[] args){

    if( args.length < 1 ){
      printusage();
      System.exit( 1 );
    }

    if( args[0] != null )
      test( args[0] );
    else{
      printusage();
      System.exit( 1 );
    }

  }


  public static void test(String filename){
    Pia.debug("Testing redirection");
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






















































