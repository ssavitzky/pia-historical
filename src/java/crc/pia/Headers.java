// Header.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.pia;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import crc.ds.Table;
import w3c.www.http.HttpEntityMessage;
import w3c.www.http.HttpMessage;
import w3c.www.mime.MimeType;

import w3c.www.mime.MimeTypeFormatException;

import crc.pia.BadMimeTypeException;

public class Headers {
  /**
   * HttpMessage
   */
  private HttpEntityMessage zheaders;

  /**
   * @return content length
   */
  public int contentLength(){
    int len = -1;
    if( zheaders!= null )
      len = zheaders.getContentLength();
    return len;
  }

  /**
   * set content length
   */
  public void setContentLength(int length){
    if( zheaders!= null )
      zheaders.setContentLength( length );
  }

  /**
   * @return content type
   */
  public String contentType(){
    if( zheaders!= null ){
      MimeType mt = zheaders.getContentType();
      if( mt != null )
	return mt.toString();
      else return null;
    }
    else
      return null;
  }

  /**
   * set content type
   */
  public void setContentType(String type) throws BadMimeTypeException{
    if( zheaders!= null ){
      try{
	MimeType mt = new MimeType( type );
	zheaders.setContentType( mt );
      }catch( MimeTypeFormatException e ){
	throw new BadMimeTypeException("Bad mime type.");
      }
    }
  }

  /**
   * @return a header field value as a String.
   */
   public String header(String name){
    if( zheaders!= null )
      return zheaders.getValue( name );
    else
      return null;
  }

  /**
   * set value of a field
   */
  public void setHeader(String name,
                       String strval){
    if( zheaders!= null )
      zheaders.setValue( name, strval );
  }

  /** 
  * Sets all the headers to values given in hash table
  * hash keys are field names
  * throws exception if not allowed to set.
  */
  public void setHeaders(Table table){
    if( zheaders!= null ){
      Enumeration keys = table.keys();
      while( keys.hasMoreElements() ){
	String key = (String)keys.nextElement();
	String v   = (String)table.get( key );
	zheaders.setValue( key, v );
      }
    }
  }

  /**
   * Return as a string all existing header information for this object.
   * @return String with HTTP style header <tt> name: value </tt><br>
   */
  public String toString(){
    ByteArrayOutputStream out;
    if( zheaders!=null ){
      out = new ByteArrayOutputStream();
      try{
	zheaders.emit( out, HttpMessage.EMIT_HEADERS);
	return out.toString();
      }catch(IOException e){return null;}
    }
    return null;
  }

  protected Headers(HttpEntityMessage h){
    zheaders = h;
  }


  /**
   * This is public because we nee to test it
   */
  public Headers(){
    zheaders = new HttpEntityMessage();
  }

}







