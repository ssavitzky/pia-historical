// T_contentFactory.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.test.pia;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import crc.pia.ContentFactory;
import crc.pia.Content;

public class T_contentFactory
{
  /**
   * For testing.
   * 
   */ 
  public static void main(String[] args){
    if( args.length == 0 )
      System.out.println("Need file content filename.");

    String filename = args[0];

    System.out.println("Test creating a form content from the FormContent class.");
    System.out.println("Input is read from a file input/requestbody.txt.");
    System.out.println("Output is a dump of the created form content.\n\n");


    ContentFactory cf = new ContentFactory();

    try{
      InputStream in = (new BufferedInputStream
			(new FileInputStream (filename)));
    
      String ztype = "application/x-www-form-urlencoded";
      Content c = cf.createContent(ztype , in );
      System.out.println( c.toString() );
    }catch(Exception e ){
      System.out.println( e.toString() );
    }
    System.exit(0);
  }
  
  
}








