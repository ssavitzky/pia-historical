// T_content.java
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
    System.out.println("For test 2, here is the command --> java crc.pia.T_content -2 textString");
    System.out.println("For test 3, here is the command --> java crc.pia.T_content CONTENT_CLASS_ID FILE_NAME");
    System.out.println("For test 2, here is the command --> java crc.pia.T_content originalString targetString replacementString");

  }


  public static void main(String[] args){
    if( args.length < 2 ){
      printusage();
      System.exit( 1 );
    }
    
    if( args.length == 3 ){
      // testing contentString replacement
      // args[0] -- is the original string
      // args[1] -- is the target substring
      // args[2] -- is the replacement 
      testStringContRep( args[0], args[1], args[2] );
    }else{
      if( args[0].equals ("-1") && args[1] != null )
	test1( args[1] );
      else {
	if( args[0].equals ("-2") && args[1] != null ) 
	  testStringCont( args[1] );
	else
	  test2( args[0], args[1] );
      }
    }
    System.exit( 1 );
  }


  private static void testStringContRep( String os, String ts, String rs ){
    System.out.println("Testing  Stringcontent class's replacement function using.");
    System.out.println("Input is original, target, and replacement strings.\n\n");
    System.out.println("Output is a dump of the Stringcontent.\n\n");
    StringContent c;

    try{
      c = new StringContent( os );
      //      c.replace( ts, rs );
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
  private static void testStringCont(String s){
    System.out.println("Testing  Stringcontent class using s as input.");
    System.out.println("Output is a dump of the Stringcontent.\n\n");
    Content c;

    try{
      c = new StringContent( s );
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










