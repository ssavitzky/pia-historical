// T_headerFactory.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.test.pia;

import java.io.InputStream;
import crc.pia.HeaderFactory;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import crc.pia.Headers;
import crc.pia.Pia;

/**  HeaderFactoryTest
 * creates an appropriate header object from a HTTP stream
 */
public class T_headerFactory
{

  private static void usage(){
    System.out.println("Test the creation of a header.  Use headerstest.txt for input.");
    System.out.println("java crc.pia.HeaderFactory headerstest.txt");
  }

  /**
  * For testing.
  * 
  */ 
  public static void main(String[] args){
    if( args.length == 0 ){
      usage();
      System.exit( 1 );
    }

    System.out.println("Test creating a header from the HeaderFactory class.");
    System.out.println("Input is read from a file input/headerstest.txt.");
    System.out.println("Output is a dump of the created header.\n\n");
    String filename = args[0];

    HeaderFactory hf = new HeaderFactory();

    try{
      InputStream in = (new BufferedInputStream
			(new FileInputStream (filename)));
    
      Headers h = hf.createHeader( in );
      Pia.debug( true );
      Pia.debug( h.toString() );
    }catch(Exception e ){
      Pia.debug( e.toString() );
    }finally{
      System.exit( 0 );
    }
  }
}





