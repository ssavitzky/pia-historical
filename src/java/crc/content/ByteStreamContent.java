//ByteStreamContent.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.content;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
/**
 * default content for byte streams
 */

public class ByteStreamContent extends StreamingContent{
  /**
   * buffer methods
   */
  protected byte[] buf;


  protected void createBuffer(int size){
    // internal buffer used for write operations
    buf = new byte[size];
    buflength = size;
  }

  /************************************************************
  ** constructors:
  ************************************************************/

  public ByteStreamContent(){
    super();
    // caller should set source
  }

  public ByteStreamContent(InputStream in){
     source(in);
  }

  public void source(InputStream in){
     source = in;
     enterState(START);
  }


   /**
    *  write outgoing data
    * called by processOut which maintains the pointers
    * subclass should override with appropriate type
    */
   protected int writeData(OutputStream out, int start,int length) throws IOException{
     // do the actual work

     out.write(buf, start, length);
     //  exception could be caught here
     
     return length;
   }
      

   /**
    * read data from input source.  Subclass should override with appropriate type.
    * try to read length data, up to buffer size
    */

   protected int readData(int length) throws IOException
     {
       if(source == null) return -1;
       if(length + nextIn > buf.length)  length = buf.length - nextIn;
       
       int read = 0;

       read = source.read(buf,nextIn,length);

       return read;
     }




  /************************************************************
  ** all other methods inherited
  ************************************************************/

}
