// Header.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.pia;

import java.util.Hashtable;
import w3c.www.http.HttpEntityMessage;
import w3c.www.http.HttpMessage;

class Headers {
  /**
   * HttpMessage
   */
  private HttpMessage zheaders;

  /**
   * @return content length
   */
  public int contentLength(){
    int len = -1;
    if( zheaders )
      len = zheaders.getContentLength();
    return len;
  }

  /**
   * set content length
   */
  public void setContentLength(int length){
    if( zheaders )
      len = zheaders.setContentLength( length );
  }

  /**
   * @return content type
   */
  public String contentType(){
    if( zheaders ){
      MimeType mt = zheaders.getContentType();
      return mt.toString();
    }
    else
      return null;
  }

  /**
   * set content type
   */
  public void setContentType(String type){
    if( zheaders ){
      MimeType mt = new MimeType( type );
      return zheaders.setContentType( mt );
    }
    else
      return null;
  }

  /**
   * @return a header field value as a String.
   */
  protected String header(String name){
    if( zheaders )
      return zheaders.getValue( name );
    else
      return null;
  }

  /**
   * set value of a field
   */
  public void setHeader(String name,
                       String strval){
    if( zheaders )
      zheaders.setValue( name, strval );
  }

  /** 
  * Sets all the headers to values given in hash table
  * hash keys are field names
  * throws exception if not allowed to set.
  */
  public void setHeaders(Hashtable table){
    if( zheaders ){
      Enumeration keys = table.keys();
      while( keys.hasMoreElements() ){
	zheaders.setValue( name, strval );
      }
  }

  /**
   * Return as a string all existing header information for this object.
   * @return String with HTTP style header <tt> name: value </tt><br>
   */
  public String toString(){
    ByteArrayOutputStream out;
    if( zheaders ){
      out = new ByteArrayOutputStream();
      zheaders.emit( out, HttpMessage.EMIT_HEADERS);
      return out.toString();
    }
    return null;
  }

  Headers(HttpMessage h){
    zheaders = h;
  }
}







