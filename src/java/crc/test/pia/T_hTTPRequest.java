//  T_httprequest.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

/** 
 * implement transaction for HTTP request
 */


package crc.test.pia;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.net.URLEncoder;
import java.io.IOException;
import java.io.PrintStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.StringBufferInputStream;
import java.util.Properties;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;

import crc.pia.Pia;
import crc.pia.Machine;
import crc.pia.agent.AgentMachine;
import crc.pia.Content;
import crc.pia.ByteStreamContent;
import crc.pia.Transaction;
import crc.pia.HTTPResponse;

import crc.ds.Queue;
import crc.ds.Features;
import crc.ds.Table;
import crc.util.Utilities;
import crc.tf.Registry;

import crc.pia.HTTPRequest;
import crc.pia.FormContent;
import crc.pia.Headers;
public class  T_hTTPRequest {


  private static void sleep(int howlong){
   Thread t = Thread.currentThread();

   try{
     t.sleep( howlong );
   }catch(InterruptedException e){;}

  }

  private static void test1( String filename ){
    System.out.println("Testing request w/ from machine as the only argument.");
    System.out.println("Input is read from post.txt and set as machine's source.");

    try{
      crc.pia.GenericAgent.DEBUG = true;
      //Pia.debug( true );
      InputStream in = new FileInputStream (filename);
      Machine machine1 = new Machine(Pia.instance().host(), 8222);
      machine1.setInputStream( in );

      // make use of debugging version, which uses HTTPRequest run method
      boolean debug = true;
      Transaction trans1 = new HTTPRequest( machine1, debug );

      Thread thread1 = new Thread( trans1 );
      thread1.start();
   

      while( true ){
	sleep( 1000 );
	if( !thread1.isAlive() )
	  break;
      }
      
      printMethods( trans1 );
      System.exit(0);
    }catch(Exception e ){
      System.out.println( e.toString() );
    }
  }

  private static void test2( String filename ){
    System.out.println("Testing request w/ from machine and content as argument.");
    System.out.println("A blank header is created in the contstructor.");
    System.out.println("Content's processInput should get called.");
    System.out.println("Content is set to data from requestbody.");

    try{
      crc.pia.GenericAgent.DEBUG = true;
      Machine machine = new Machine();
      FormContent c = new FormContent();

      InputStream in = new FileInputStream (filename);
      c.source( in );


      Boolean debug = new Boolean( true );
      Transaction trans = new HTTPRequest( machine, c, debug );
      //printOn only prints if method is post and content has data
      trans.setMethod( "POST" );
      trans.setContentLength( 51 );
      ((HTTPRequest)trans).setParam();

      Thread thread1 = new Thread( trans );
      thread1.start();
   

      while( true ){
	sleep( 1000 );
	if( !thread1.isAlive() )
	  break;
      }

      printMethods( trans );
      System.exit(0);
    }catch(Exception e ){
      System.out.println( e.toString() );
    }

  }

  private static void test3( ){
    System.out.println("Testing request w/ from machine, content, and header as argument.");

    crc.pia.GenericAgent.DEBUG = true;
    Machine machine = new Machine();
    Headers h = null;
    try{
      h = new Headers();
      h.setHeader("Host", "napa.crc.ricoh.com:9999");
      h.setContentType("text/html");
      h.setContentLength( 555 );
      h.setHeader("Content-Type", "image/gif");
    }catch(Exception e){;}

    FormContent c = new FormContent();

    boolean debug = true;
    Transaction trans = new HTTPRequest( machine, c, h, debug );
    Thread thread = new Thread( trans );
    thread.start();

    while( true ){
      sleep( 1000 );
      if( !thread.isAlive() )
	break;
    }

    printMethods( trans );
    System.exit( 0 );
  }

  private static void test4( ){
    System.out.println("Testing request w/ from machine, content, and header as argument.");
    System.out.println("Also, test transaction features.");

    crc.pia.GenericAgent.DEBUG = true;
    Machine machine = new Machine();
    Headers h = null;
    try{
      h = new Headers();
      h.setHeader("Host", "napa.crc.ricoh.com:9999");
      h.setContentType("text/html");
      h.setContentLength( 555 );
      h.setHeader("Content-Type", "image/gif");
    }catch(Exception e){;}

    FormContent c = new FormContent();

    boolean debug = true;
    Transaction trans = new HTTPRequest( machine, c, h, debug );
    trans.setRequestURL("http://agency/im3/initialize.inf");
    System.out.println( "----->>>> Headers are the following: <<<<<------ " );
    System.out.println(trans.headersAsString());
    System.out.println("\n");
    System.out.println( "----->>>> Testing Transaction Features <<<<<------ " );
    System.out.println( "----->>>> url is http://agency/im3/initialize.inf  " );
    System.out.println( "Is agent request? ->" + trans.compute("IsAgentRequest").toString() );
    System.out.println( "Getting agent ->" + trans.compute("Agent").toString() );
    System.out.println( "Checking for netscape -->"+ trans.compute("IsClientNetscape") );
    
    System.out.println("\n------>>>> Changing url to file:/slackware/home/foo.html <<<<-------------------");
    trans.setRequestURL("file:/slackware/home/foo.if");
    System.out.println("Is this a file request? ->" + trans.compute("IsFileRequest").toString() );
    System.out.println("Is content-type [text/html]? ->" + trans.compute("IsHtml").toString() );
    System.out.println("Is content-type [image]? ->" + trans.compute("IsImage").toString() );
    System.out.println("Is interform request? ->" + trans.compute("IsInterform").toString() );
    System.out.println("Is local request? ->" + trans.compute("IsLocal").toString() );
    System.out.println( "----->>>> url is back to http://agency/im3/initialize.inf  " );
    trans.setRequestURL("http://agency/im3/initialize.inf");
    trans.setHeader("Host", "agency");
    System.out.println("Is proxy request? ->" + trans.compute("IsProxyRequest").toString() );
    System.out.println("Is content-type text? ->" + trans.compute("IsText").toString() );  

    System.out.println("\n------>>>> Testing assertion <<<<-------------------");
    System.out.println("Asserting IsText to true\n");
    trans.assert( "IsText" );
    System.out.println("Make use of test(...) to check IsText ->" + trans.test( "IsText" ));
    trans.deny( "IsText" );
    System.out.println("Recheck IsText (should be false) ->" + trans.test( "IsText" ));
    System.out.println("Checking has( IsText ) (should be true) ->" + trans.has( "IsText" ));
    System.out.println("Checking getFeature ( IsImage ) (should be true) ->" + trans.getFeature( "IsImage" ).toString());
    System.exit( 0 );
  }



  private static void printusage(){
    System.out.println("Needs to know what kind of test");
    System.out.println("For test 1, here is the command --> java crc.pia.HTTPRequest -1 post.txt");
    System.out.println("For test 2, here is the command --> java crc.pia.HTTPRequest -2 requestbody.txt");
    System.out.println("For test 3, here is the command --> java crc.pia.HTTPRequest -3");
    System.out.println("For test 4, here is the command --> java crc.pia.HTTPRequest -4");
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

    if( args.length == 1 ){
      if ( args[0].equals ("-3") )
	test3();
      else if ( args[0].equals ("-4") )
	test4();
      else {
	printusage();
	System.exit( 1 );
      }
    }else if (args.length == 2 ){
      if( args[0].equals ("-1") && args[1] != null )
	test1( args[1] );
      else if( args[0].equals ("-2") && args[1] != null )
	test2( args[1] );
      else{
	printusage();
	System.exit( 1 );
      }
    }

  }

  private static void printMethods(Transaction t){
    PrintStream out = new PrintStream( System.out );
    out.println( "----->>>> Testing headers <<<<<------ " );
    System.out.println( t.headersAsString() );
    out.println( "----->>>> End testing headers <<<<<------ " );

    out.println("\n\n");
    out.println( "----->>>> Testing request methods <<<<<------ " );
    out.println( "content length method -->" + Integer.toString( t.contentLength() ) );
    out.println( "content type   method -->" + t.contentType() );
    out.println( "method         method -->" + t.method() );
    out.println( "protocol       method -->" + t.protocol() );
    out.println( "host           method -->" + t.host() );
    if( t.requestURL() != null )
      out.println( "url            -->" + t.requestURL().getFile() );
    out.println( "HTTP version   -->" + t.version() ); 
    if( t.hasQueryString() )
      out.println( "query string -->" + t.queryString() );
    out.println( "----->>>> End testing methods <<<<<------ " );
    out.println("\n\n");
    out.println( "----->>>> The whole request message <<<<<------ " );
    try{
      t.printOn( out );
    }catch(Exception e){ 
      e.printStackTrace();
		      }
  }  

}








