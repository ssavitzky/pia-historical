// T_content.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.test.pia;
import crc.pia.Content;
import crc.pia.ContentOperationUnavailable;
import crc.content.*;
import crc.content.text.*;
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

public class T_content{
  private static void printusage(){
    System.out.println("Needs to know what kind of test");
    System.out.println("For test 1, here is the command --> java crc.pia.T_content -1 postno1line.txt");
    System.out.println("For test 2, here is the command --> java crc.pia.T_content CONTENT_CLASS_ID FILE_NAME");

  }


  public static void main(String[] args){

    if( args.length != 2 ){
      printusage();
      System.exit( 1 );
    }
    
    if( args[0].equals ("-1") && args[1] != null )
      test1( args[1] );
      else 
	test2( args[0], args[1] );
    System.exit( 1 );
  }



  /**
  * For testing.
  * 
  */ 
  private static void test1(String filename){

   
    System.out.println("Testing  byte stream content class by creating a header and a bscontent.");
    System.out.println("Content is then pointed at header from which it will get content length.");
    System.out.println("Input is read from a  " + filename);
    System.out.println("Output is a dump of file.\n\n");

  HeaderFactory hf = new HeaderFactory();
  Content c;

    try{
      InputStream in = (new BufferedInputStream
			(new FileInputStream (filename)));
    
      Headers h = hf.createHeader( in );
       c = new ByteStreamContent( in );
      c.setHeaders( h );
      c.writeTo(System.out);
    }catch(Exception e ){
      e.printStackTrace();
      System.out.println( e.toString() );
    }
    System.exit( 0 );
  }


 
  /**
  * For testing.
  * 
  */ 
  private static void test2( String aclass, String filename){

  HeaderFactory hf = new HeaderFactory();
  Content c;

    System.out.println("Testing creation of" + aclass + " with input from" + filename);

    try{
      InputStream in = (new BufferedInputStream
			(new FileInputStream (filename)));
    
      Headers h = hf.createHeader( in );

      Class myClass = Class.forName(aclass);
      
      c = (Content) myClass.newInstance();
      c.setHeaders( h );
      c.source(in);
      c.writeTo(System.out);
      
    }catch(Exception e ){
      e.printStackTrace();
      System.out.println( e.toString() );
    }
    System.exit( 0 );
  }
 
 
 
 

}










