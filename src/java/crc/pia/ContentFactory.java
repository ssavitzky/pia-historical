// ContentFactory.java
// $Id$
// COPYRIGHT 1997 Ricoh Silicon Valley.


package crc.pia;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import w3c.www.mime.MimeType;


/**  ContentFactory
 * creates an appropriate content object for an HTTP stream.
 * Currently, only creates either a FormContent or ByteStreamContent.
 */
public class ContentFactory
{
 /**  creates a new content factory with default list of possible content objects
 */
  public ContentFactory()
  {
  }


  /** create a content object from the given stream.
   * If the parameter contentType is "APPLICATION_X_WWW_FORM_URLENCODED", create
   * a FormContent otherwise create a ByteStreamContent.
   */
  public Content createContent(String contentType, InputStream input)
  {
    Content c = null;
    if (contentType == null) contentType = "text/html";
    try{
      MimeType ztype  = new MimeType( contentType );
      // This is too simple,minded.  Need to test others
      int what = ztype.match( MimeType.APPLICATION_X_WWW_FORM_URLENCODED );

      if( what == ztype.MATCH_SPECIFIC_SUBTYPE ){
	c = new FormContent( input );
      }
      else
	c = new ByteStreamContent( input );

      return c;
    }catch(Exception ex){
      ex.printStackTrace();
      return null;
    }
    
  }

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








