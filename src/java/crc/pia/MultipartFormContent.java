// MultipartFormContent.java

// (c) COPYRIGHT Ricoh California Research Center, 1998

/**
 * MultipartFormContent is a minimal version of FormContent
 * which instead handles multipart forms encoded with the
 * multipart/form-data MIME type.  It deals with uploading
 * but not downloading (i.e. it can construct multipart/form-data
 * messages but not parse or interpret them).
 *
 * The encoding is specified in
 * <A HREF="http://info.internet.isi.edu:80/in-notes/rfc/files/rfc1867.txt">RFC1867</A>
 *
 * @see crc.pia.FormContent
 * @see crc.interform.handle.Submit_forms 
 */

package crc.pia;

import crc.pia.Machine;
import crc.pia.Agent;
import crc.pia.ContentOperationUnavailable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

import java.util.Properties;


public class MultipartFormContent extends Properties implements InputContent

{

 /**
   * headers
   */
  private Headers headers;

  /**
   * body
   */
  private OutputStream body;

  
  /** 
  * Return as header associated with this content.
  * @return header associated w/ this content.
  */
  public Headers headers(){
    if( headers!= null )
      return headers;
    else
      return null;
  }
  
  /** 
   * Set a header associated with this content.
   */
  public void setHeaders( Headers headers ){
    if( headers!= null ) {
      crc.pia.Pia.debug(this,"New headers "+headers.toString());
      this.headers = headers;
    }
  }
 
  /**
   * set content length to a supplied value
   */
  private void setContentLength(int len){
    if (headers != null)
      headers.setContentLength(len);
  }


  /**
   * get content length from current data stream
   */
  public int getCurrentContentLength() throws ContentOperationUnavailable {
    if (body == null) {
      return 0;
    } else {
      if (body instanceof ByteArrayOutputStream) {
	return ((ByteArrayOutputStream)body).size();
      } else {
	// For an arbitrary OutputStream not sure how to get size
	 throw(new ContentOperationUnavailable("Cannot determine content length"));
      }
    }
  }



  /************************************************************
  * Content producers:
  ************************************************************/

  /**
   * Set source.
   * param o the source object, which in this case must be an InputStream.
   * @exception crc.pia.ContentOperationUnavailable if the source is
   *	anything but an InputStream.
   * @see java.io.InputStream
   */
  public void source(Object o)  throws ContentOperationUnavailable {
    OutputStream s;
    if ( o instanceof OutputStream ){
      s = ( OutputStream ) o;
      source( s );
    }else
    throw(new ContentOperationUnavailable(" cannot handle type as source"));
  }

 /** 
  * set a source stream for this object
  * usually this will come from a machine
  */
  public void source(OutputStream stream){
    if( stream != null )
      body = stream;
  }



  /************************************************************
  ** Access functions:
  ************************************************************/

 /** 
  * Access functions. 
  *  when the "To machine" is ready to send a response, it reads
  *  data from the content object.  At that point (if not sooner)
  *  the content object should start sucking data from the source,
  *  processing in any matter specified by agents, and then finally spitting
  *  out the data. <p>
  *
  *  Essentially stream functions.
  */

  /**
   * sets the target for write operations and writes data to this stream
   * this method will block until all data is written to output stream.
   * @param outStream the OutputStream to write on.
   * @return the number of items written
   * @exception crc.pia.ContentOperationUnavailable if the operation
   *	cannot be performed by this type of Content.
   * @exception java.io.IOException if thrown by the OutputStream.
   */
  public int writeTo(OutputStream outStream)
    throws ContentOperationUnavailable, IOException
    {
      // crc.pia.Pia.debug(this,"Sending body to output stream "+body.toString());
      ((ByteArrayOutputStream)body).writeTo(outStream);
      return 0;
    }


  public void tapOut(OutputStream tap)
    throws ContentOperationUnavailable
    {
       throw new ContentOperationUnavailable("");
    }


  public void tapIn(OutputStream tap)
    throws ContentOperationUnavailable
    {
       throw new ContentOperationUnavailable("");
    }
  

  public void notifyWhen(Agent interested, String state, Object arg)
       throws ContentOperationUnavailable
    {
       throw new ContentOperationUnavailable("");
    }
  

  /**
   * return a list of states that this content can go through
   * agents can use this to determine when to be notified
   */
   public String[] states()
    {
      return null;
    }
  

  /**
   * If the content object exists as a data structure in memory, then
   * it is persistent. Otherwise, it is not persistent (streams are
   * not persistent).
   */
  public boolean isPersistent()
    {
      return true;
    }


  /** 
   * Begin processing input.
   * The transaction calls this before the "to machine" is ready to
   * send a response, giving us an opportunity to fill up our internal
   * buffers
   *
   * @return false if content is complete */
  public boolean processInput()
    {
      return false;
    }
  

  
  public void add(Object moreContent, int where)
    throws ContentOperationUnavailable
    {
      throw new ContentOperationUnavailable("");
    }
  
 
  /************************************************************
  ** Constructors:
  ************************************************************/
  public MultipartFormContent(OutputStream out){
    source( out );
  }


}	









