// HeaderFactory.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.


package crc.pia;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import crc.pia.Headers;
import crc.pia.Pia;
import w3c.www.mime.MimeParserFactory;
import w3c.www.mime.MimeParser;
import w3c.www.http.HttpEntityMessage;
import w3c.www.http.MimeParserMessageFactory; 

/**  HeaderFactory
 * creates an appropriate header object from a HTTP stream
 */
public class HeaderFactory
{
 /**  
  * creates a new headers object 
  */
  public HeaderFactory()
  {
  }

  /**
   * Creates a blank header.
   */
  public Headers createHeader(){
      Headers headers = new Headers();
      return headers;
  }

  /** create a header object from the given stream
   */
  public Headers createHeader(InputStream input)
  {
    // Create the factory:
    MimeParserFactory f = null;

    f = (MimeParserFactory) new MimeParserMessageFactory();
    
    try{
      MimeParser p  = new MimeParser(input, f);

      Pia.debug(this, "Parsing header...\n\n");
      HttpEntityMessage jigsawHeaders = (HttpEntityMessage) p.parse();

      Headers headers = new Headers( jigsawHeaders );
      Pia.debug(this, "Header is done\n");

      return headers;
    }catch(Exception ex){
      ex.printStackTrace();
      return null;
    }
    
  }
}












