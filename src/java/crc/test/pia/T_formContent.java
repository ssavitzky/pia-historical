// T_formContent.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.test.pia;
import crc.pia.FormContent;
import crc.pia.HeaderFactory;
import java.io.IOException;
import java.io.EOFException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.ByteArrayInputStream;

import crc.pia.Headers;
import crc.ds.Table;
import crc.ds.List;
import crc.util.Utilities;

public class T_formContent{
  private static void printusage(){
    System.out.println("Needs to know what kind of test");
    System.out.println("For test 1, here is the command --> java crc.pia.FormContent -1 postno1line.txt");
    System.out.println("For test 2, here is the command --> java crc.pia.FormContent -2 postno1line.txt");
    System.out.println("For test 3, here is the command --> java crc.pia.FormContent -3 postbody.txt");
  }


 public static void main(String[] args){

    if( args.length == 0 ){
      printusage();
      System.exit( 1 );
    }

    if (args.length == 2 ){
      if( args[0].equals ("-1") && args[1] != null )
	test1( args[1] );
      else if( args[0].equals ("-2") && args[1] != null )
	test2( args[1] );
      else if( args[0].equals ("-3") && args[1] != null )
	test3( args[1] );
      else{
	printusage();
	System.exit( 1 );
      }
    }

  }


  /**
  * For testing.
  * 
  */ 
  private static void test1(String filename){

    HeaderFactory hf = new HeaderFactory();
    System.out.println("Testing form content class by creating a header and a form content.");
    System.out.println("Content is then pointed at header from which it will get content length.");
    System.out.println("Input is read from a file input/postno1line.txt.");
    System.out.println("Output is a dump of the form content's parameters.\n\n");

    try{
      InputStream in = (new BufferedInputStream
			(new FileInputStream (filename)));
    
      Headers h = hf.createHeader( in );
      FormContent c = new FormContent( in );
      c.setHeaders( h );

      c.setParameters( null );
      c.printParametersOn( System.out );
    }catch(Exception e ){
      System.out.println( e.toString() );
    }
    System.exit( 0 );
  }


 
  /**
  * For testing.
  * 
  */ 
  private static void test2(String filename){
    HeaderFactory hf = new HeaderFactory();

    System.out.println("Testing form content class by creating a header and a form content.");
    System.out.println("Content is then pointed at header from which it will get content length.");
    System.out.println("This case test processInput function.\n");
    System.out.println("Input is read from a file input/postno1line.txt.");
    System.out.println("Output is a dump of the form content's parameters.\n\n");

    try{
      InputStream in = (new BufferedInputStream
			(new FileInputStream (filename)));
    
      Headers h = hf.createHeader( in );
      FormContent c = new FormContent( in );
      c.setHeaders( h );

      boolean done = false;
      while( !done ){
	if( !c.processInput() )
	  done = true;
      }

      c.setParameters( null );
      c.printParametersOn( System.out );
    }catch(Exception e ){
      System.out.println( e.toString() );
    }
    System.exit( 0 );
  }
 
  
 /**
  * For testing.
  * 
  */ 
  private static void test3(String filename){
    System.out.println("Testing form content class by creating a form content with no header.");
    System.out.println("Input is read from a file input/postbody.txt.");
    System.out.println("Output is a dump of the form content's parameters.\n\n");

    try{
      String s = Utilities.readStringFrom( filename );
      
      FormContent c = new FormContent( s );
      c.printParametersOn( System.out );
    }catch(Exception e ){
      System.out.println( e.toString() );
    }
    System.exit( 0 );
  }
 
 

}










