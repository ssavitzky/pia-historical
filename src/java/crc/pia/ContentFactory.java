
package crc.pia;

import java.io.InputStream;


/**  ContentFactory
 * creates an appropriate content object for an HTTP stream
 *  creates a hash table for the headers
 */
public class ContentFactory
{
/**  creates a new content factory with default list of possible content objects
 */
  public ContentFactory()
  {
  }
  
/** create a content object from the given stream
 */
  public Content createContent(InputStream http)
  {
    //we need a better data structure that hash table for headers
    //parse the entire header, then check for POST or GET method
    //If POST, create a ParamPropList kind (may passing content in constructor)
    //If GET, do the same as POST passing querystring from url if there is a query string
    
    Hashtable headers=parseHeaders(http);
    
    Content object=contentFor(headers);
    
    object.headers(headers);
    
    object.source(http);
    
    return object;
    
  }
  
/** map header content type to proper object
 */
  private Content contentFor(Hashtable hash)
  {
    String type=hash.get("content-type");
    
    //for now, just use byte stream
    return new ByteStreamContent();
    
  }
  
}
