
package crc.pia;

import java.io.InputStream;


/**  ContentFactory
 * creates an appropriate content object for an HTTP stream
 *  creates a hash table for the headers
 */
public class ContentFactory
{
  /**
   * first line of stream
   */
  protected StringBuffer buf;

 /**  creates a new content factory with default list of possible content objects
 */
  public ContentFactory()
  {
  }


  /** create a content object from the given stream
   */
  public Content createContent(String contentType, InputStream input)
  {
    Content c = null;

    try{
      MimeType ztype  = new MimeType( contentType );
      // This is too simple,minded.  Need to test others
      if( ztype.match( APPLICATION_X_WWW_FORM_URLENCODED ) )
	c = new FormContent();
      else
	c = new ByteStreamContent();

      c.source( input );

      return newContent;
    }catch(Exception ex){
      ex.printStackTrace();
      System.out.println("MimeParser <factory> <file>");
    }
    
  }
  
}








