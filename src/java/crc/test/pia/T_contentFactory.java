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
   * usage
   */
  private static void usage(){
    System.out.println("Testing the creation of a FormContent using requestbody.txt");
    System.out.println("java crc.pia.ContentFactory requestbody.txt");
  }

  /**
   * For testing.
   * 
   */ 
  public static void main(String[] args){
    if( args.length == 0 )
      System.out.println("Need file content filename.");

    String filename = args[0];

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








