// HeaderFactory.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.


package crc.pia;

import java.io.InputStream;


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

  /** create a header object from the given stream
   */
  public Headers createHeader(InputStream input)
  {
    // Create the factory:
    MimeParserFactory f = null;

    f = (MimeParserFactory) new MimeParserMessageFactory();
    
    // Create the parser:
    InputStream in = new BufferedInputStream( http );
    
    try{
      MimeParser p  = new MimeParser(in, f);
      readFirstLine(p);

      HttpEntityMessage jigsawHeaders = (HttpEntityMessage) p.parse();

      Headers headers = new Headers( jigsawHeaders );

      return headers;
    }catch(Exception ex){
      ex.printStackTrace();
      System.out.println("MimeParser <factory> <file>");
    }
    
  }
  
}








