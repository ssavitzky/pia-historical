//  StreamingContent.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.


package crc.content;
import crc.pia.Pia;
import crc.pia.Machine;
import crc.pia.Agent;
import crc.pia.Headers;
import crc.ds.Table;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import crc.pia.Content;

import crc.pia.ContentOperationUnavailable;
import crc.ds.List;

import java.util.Enumeration;
/** StreamingContent
 * provides content interface for streams ( and readers) allowing agents to
 * operate on the content as though it were an object.
 * This class provides abstract methods for handling circular buffer pointers,
 * with lots of hooks for sub classes to override, especially the readData and
 * writeData.
 * The buffer data type is not defined--
 * subclass should override the createBuffer,  read/writeData methods
 * with appropriate types.
  */
public abstract   class  StreamingContent  extends GenericContent {

   /**
    * Streams for tapping the data
    */
   protected OutputStream[] outTaps = new OutputStream[0];
   protected OutputStream[] inTaps = new OutputStream[0];

  /**
   * default size for internal buffers
   */
  protected static int defaultBufferSize = 8192;

  /**
   * buffer length --  sub classes should create buf in createBuffer
   */
   protected int buflength;
  /**
   * createBuffer -- subclass should override with correct type
   */
  protected void createBuffer(int size){
    //buf = new type[size]
    buflength = size;
  }

  /**
   * underlying input and output objects
   * subclass  can override
   */

   protected InputStream source;
   protected OutputStream sink;


  /**  variables to maintain position in internal buffer
   * things get confusing because buffer wraps around
   *  if wrapped is false: nextOut <= outLimit <= nextIn <= buflength unwrapped
   * if wrapped is true: nextIn <= nextOut <= outLimit <= buflength
   * if olWrapped is true: outLimit <= nextIn <= nextOut <= buflength
   */
  protected  boolean wrapped = false, olWrapped = false;
  
  protected int totalIn =0, totalOut=0;
  
  /**
   * position where next bit of input should go
   */
  protected int  nextIn = 0 ;

  /**
   * position where next bit of output should be read from
   */
  protected int  nextOut = 0 ;

  /**
   *  last position that it is safe to get outPut from
   */
  protected int outLimit = 0;

  /************************************************************
  ** set source
  ************************************************************/

  /**
   * attempt to set source, subclass should override for particular data types
   */

  public void source(Object input) throws ContentOperationUnavailable{
    if(isVisitedState(READING)){
      throw( new ContentOperationUnavailable(" cannot set source already started reading"));
    }
    //dispatch on class type
    try{
      source((InputStream) input);
      return;
    } catch(Exception e){
      //conversion exception
    }
    
    Pia.debug(this,input.getClass().getName() + " not handled");
    throw( new ContentOperationUnavailable(input.getClass().getName() + " not handled by" + this.getClass().getName()));
  }

  public void source(InputStream input) throws ContentOperationUnavailable{
    if(isVisitedState(READING)){
      throw( new ContentOperationUnavailable(" cannot set source already started reading"));
    }
    source = input;
    enterState(START);
       
    }


  /************************************************************
  ** manipulate pointers
  ************************************************************/
  /**
   * move internal pointers indicating where reading / writing should occur
   */

  /**
   * add distance to  next input -- where characters should be placed
   */
  protected int advanceNextIn(int distance){
    nextIn += distance;
    if(nextIn < 0) {
      if(wrapped) {
	nextIn += buflength;
	wrapped = false;
      } else{
	nextIn = 0;
	// warning message about invalid pointer
      }
    }
    if(nextIn >= buflength){
      // if nextIn is already wrapped this is trouble
      nextIn = nextIn % buflength;
       wrapped = true;
    }
     return nextIn;
  }


  /**
   * add distance to  next out -- where characters should be read from
   */
  protected int advanceNextOut(int distance){
    nextOut +=  distance;
    if(nextOut < 0 ){
      Pia.debug(this,"advancing out " +  distance);
      // this should not happen
      //      nextOut += buflength;
      //       wrapped = true;
      //       olWrapped = true;
    }
    if(nextOut >= buflength){
      nextOut = nextOut % buflength;
      // assumes next in and out limit precede us
      wrapped = false;
      olWrapped = false;
    }
    return nextOut;
  }
  /**
   * add distance to   out limit --  last position characters should be  read from
   */
  protected int advanceOutLimit(int distance){
     outLimit += distance;
     if(outLimit < 0) {
       outLimit += buflength;
       olWrapped = false;
     }
     if(outLimit >= buflength){
       outLimit =  outLimit % buflength;
       olWrapped = true;
     }
     return outLimit;
  }

  /************************************************************
  ** read and process methods
  ************************************************************/


 
   /**   send the next chunk of data -- calls writeData to actually output bits
    *  @return number of bytes sent
    */
    protected int processOutput() throws IOException{

      debug();
      // manipulate data between outLimit and nextIn, returns
      // number of bits processed 
      int toProc = (wrapped == olWrapped) ? nextIn - outLimit : buflength - outLimit + nextIn;
      Pia.debug(this," processing "+toProc+" bits in buffer");
      advanceOutLimit(processBuffer(toProc));


      // make sure we have enough data to write something
      int a = available ();
      // if none available and no more will be read, we are done
      if(a==0 && !isCurrentState(READING) && !moreToProcess()){
	return -1;
      }
      int len = writeData(a);
      
      // satisfy any taps
      writeOutputTaps(nextOut,len);

      // update out  position
      advanceNextOut(len);
      totalOut += len;
      
      return len;
    }
   

  /**
   * return number of bytes available for outputting
   */
  public int available(){
    // up to outLimit or buflength, whichever comes first
    return (!olWrapped)? outLimit - nextOut : buflength - nextOut;
  }

  /**
   * return number of bytes available for inputting
   */
  protected int availableToRead(){
    //  up to nextOut or buflength, whichever comes first
    return (wrapped)? nextOut - nextIn : buflength - nextIn;
  }


   /************************************************************
   ** handle tapping
   ************************************************************/

   /**
    * write output taps -- subclass may override
    */
   protected void writeOutputTaps( int offset, int length){
     for(int i=0;i<outTaps.length;i++){
       try{
	 //           outTaps[i].write(buf, offset, length);
	 writeData(outTaps[i], offset, length);
       }
       catch(IOException e) {
	 // notify of error write
	 // Pia.errLog(" problem tapping");
       }
     }
   }

   /**
    * write input taps -- subclass  may override
    */
   protected void writeInputTaps( int offset, int length){
     for(int i=0;i<inTaps.length;i++){
       try{
	 //inTaps[i].write(buf, offset, length);
	 writeData(inTaps[i], offset, length);
       }
	 catch(IOException e)
	   {
	     // notify of error write
	     // Pia.errLog(" problem tapping");
	   }
     }
   }
  



   /************************************************************
   ** process input stream
   ************************************************************/

   /**
    * read incoming data.
    * @return true if more data to be read exists
    */
   public boolean processInput()
     {
       
       int len =   availableToRead();
       return processInput(len);
       
     }

   /**
    * hook for initialization
    */

   protected void beginProcessing(){

     // initialize buffer
     int size =defaultBufferSize;
     
     int length = 0;
     if(headers != null) length = headers().contentLength();
     if(length > 0 && length < size){
       size =  length;
       }
     //buf  = new  byte[size];
     createBuffer(size);

     exitState(START);
     enterState(READING);
   }


   /**
    * read length incoming data --  calls readData (which subclass 
    * should override).
    *  changes state to READING
    */
   protected boolean  processInput(int length)
     {
       // make sure that streams are appropriately initialize
       if(!isVisitedState(READING)){
	 beginProcessing();
       } 
       int len = 0;
       boolean moreToRead=true;
       // insert pending items 
       int pread = insertPending(length);   
       // update nextIn  -- should not overwrap
       advanceNextIn(pread);

       length = length - pread;

       if (!isCurrentState(READING)){
	 // done with reading
	 moreToRead = false;
       }

       if( headers().contentLength() >0 && headers().contentLength() == totalIn){
       	 exitState(READING);
       	 moreToRead=false;
       }

       if(length > 0 && moreToRead){
	 try{
	   len = readData(length);      
	  } catch(IOException e){
	     // assume stream closed
	     len = -1;
	   }
	 if(len < 0) {
	   exitState(READING);
	   moreToRead=false;
	   len = 0;  // nothing was read
	 }
       }
       if(len > 0) {
	 // write input taps  -- pure input, no pending and no processed items
	 writeInputTaps( nextIn, len);

	 // update position
	 advanceNextIn(len);
	 totalIn += len;
       }
      	
       debug();
       // return true if more data remains to be read 
       // more processing can wait until we begin processing output
       return moreToRead;// || moreToProcess();
       
     }
   
  /**
   * insert any pending items
   * @return number of items inserted
   */
  protected int insertPending(int length){
    // default is to not support pending operations
    return 0;
  }

  /************************************************************
  ** Agent interactions:
  ************************************************************/


  /** Add an output stream to "tap" the data before it is written. 
   * Taps will get data during a read operation just before the data "goes
   * out the door" 
   * Uses  arrays for slightly more efficiency
   */
   public void tapOut(OutputStream tap) throws ContentOperationUnavailable{
     if(isVisitedState(WRITING)){
       throw(new ContentOperationUnavailable("Already started WRITING"));
     }

     OutputStream[] oldTaps = outTaps;
     outTaps =  new OutputStream[oldTaps.length + 1];
     for(int i=0;i<oldTaps.length;i++){
       outTaps[i]=oldTaps[i];
     }
     outTaps[oldTaps.length] = tap;
   }


  /** Add an output stream to "tap" the data as it is read.
   * Taps will get data	 as soon as it is  available to the content 
   * -- before any processing occurs.  
   * tapOut == tapIn if content does not support editing/processing
   * @throws if already started reading
   */
   public void tapIn(OutputStream tap) throws ContentOperationUnavailable{
     if(isVisitedState(READING)){
       throw(new ContentOperationUnavailable("Already started READING"));
     }

     OutputStream[] oldTaps = inTaps;
     inTaps =  new OutputStream[oldTaps.length + 1];
     for(int i=0;i<oldTaps.length;i++){
       inTaps[i]=oldTaps[i];
     }
     inTaps[oldTaps.length] = tap;
   }

  
   /**
    * If the content object exists as a data structure in memory, then
    * it is persistent. Otherwise, it is not persistent (streams are
    * not persistent).
    */

   public boolean isPersistent(){
     return false; // default is streaming
   }


   

   /************************************************************
   ** internal read and write operations --  subclass should override
   ************************************************************/
   /**
    * set sink for output operations
    * subclass may want to change class or manipulate sink
    */
   protected void setSink(OutputStream stream){
      sink = stream;
   }

   /**
    * unset sink -- output complete
    */
   protected void unsetSink(){
     sink = null;
   }

   /**
    *  write outgoing data
    * called by processOut which maintains the pointers
    * subclass should override with appropriate type
    */
   protected int writeData(int length) throws IOException{
     if(sink == null) return 0; // this should not happen
     if(length + nextOut > buflength)  length = buflength - nextOut;

     // do the actual work

     // subclass should override sink.write((byte[]) buf,nextOut, length);
      return writeData(sink,nextOut,length);
     //  exception could be caught here
   }

      
  /**
   * write data to an output stream -- subclass should override
   */
  protected int writeData(OutputStream out, int start,int length) throws IOException{
    throw(new IOException(" subclass must override "));
    // subclass should override

  }

   /**
    * read data from input source.  Subclass should override with appropriate type.
    * try to read length data, up to buffer size
    */

   protected int readData(int length) throws IOException
     {
       if(source == null) return -1;
       if(length + nextIn > buflength)  length = buflength - nextIn;
       
       int read = 0;

       //subclass must override
       // read = source.read((byte[])buf,nextIn,length);

       return read;
     }




 /************************************************************
  ** Content specific operations:
  ************************************************************/

  /**
   * Add an object to the content
   * if object is not a compatible type, throws exception
   * @param  where: interpretation depend on content,  by convention 0 means at front
   * -1 means at end, everything else is subject to interpretation
   */
  public void add(Object moreContent, int where) throws ContentOperationUnavailable {
    Pia.debug(this, "adding " + moreContent.getClass().getName() + " not supported by " + this.getClass().getName());
    throw(new ContentOperationUnavailable("adding " + moreContent.getClass().getName() + " not supported by " + this.getClass().getName()));
  }
  

  /**
   * hook for processing characters in buffer between outLimit and nextIn
   * return number of characters process
   * @param newChars is number of characters added to buffer in last read operation
   *@return number of characters processed
   * caller uses this to set outLimit = return + outLimit
   * note that wrapping occurs  e.g. nextIn < outLimit if wrapping = true
   *  subclass should override
   */

  protected int processBuffer(int newChars){
				//  default is no processing
    //  remember to check for buffer wrap around
    return newChars; // caller adds this to outLimit
     
   }

  /**
   *return true if more processing needs to be done
   */

  protected boolean moreToProcess(){
    // default is to say no if outLimit == nextIn
    return !(outLimit == nextIn && (wrapped == olWrapped));    
  }

  protected void debug(){
    Pia.debug(this,"nextIn = " + nextIn + " wrapped =" + wrapped + " nextOut= "  +  nextOut +"oLim="+outLimit + "olWrapped=" +olWrapped + getCurrentStates());
  }
}
