//  annotatedhtml.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.content.text;

import crc.pia.Pia;
import crc.pia.Transaction;

import ricoh.rh.RH_GlobalVars;
import ricoh.rhpm.RHPMAgent;
import ricoh.rhpm.RHHistoryDB;
import ricoh.rhpm.RHSimilarity;
import ricoh.rhpm.RHCalendar;
import ricoh.rhpm.RHPatternMatcher;
import ricoh.rhpm.RHStopWords;

import java.io.Reader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.File;

import java.lang.Character;

import crc.pia.Content;
import crc.content.text.Default;
import crc.pia.ContentOperationUnavailable;

/** Status:  some strings are hardwired and there are many
 * print statements.  These are still needed for debugging.
 * Hardwired strings can be moved when this info can be
 * obtained from reader's helper
 * It works for some documents, but others are returning pages
 * containing no data.
 */

/**
 * class for annotating html.
 * Unlike StreamingContent and its subclasses, annotatedhtml
 * reads the input stream and writes each buffer-full into
 * a growable ByteArrayOutputStream until all reads are done.  
 * This is because the byteArray will be handed to the ReadersHelper
 * annotation routine, which currently operates on a complete
 * page.  Whenever the annotation routine is modified to annotate
 * based on page chunks, this code should be modified to use the
 * circular buffer and processInput/processOutput/writeTo methods
 * defined in GenericContent and StreamingContent.  The model there
 * is to read a chunk then write a chunk of data.
 */

public class annotatedhtml extends html
{
    // Content class variables
    ByteArrayOutputStream byteArray;
    OutputStreamWriter osWriter;
    boolean hasBeenAnnotated = false;
    int totalLen = 0;   // Total number of bytes read
    byte buf[];
    byte resultBuf[];
    public byte[] newbuf;
    RHPMAgent agent = null;

    public void setAgent(RHPMAgent agnt) {
	agent = agnt;
    }

    /**   changes meaning of location 0 in additions to be just after the body tag
     */


    // override super class
    protected  void createBuffer( int size){
	buf = new byte[size];
	buflength=size;

	// Create output buffer and give it an initial size
	byteArray = new ByteArrayOutputStream(size);
	System.out.println("annotatedhtml createBuffer byteArray size: " + byteArray.size());

	// This was not appending to byte array correctly
	// osWriter=new OutputStreamWriter(byteArray);
    }

    protected  int processBuffer(int newChars){
	System.out.println("annotatedhtml processBuffer called");

	if(isCurrentState(READING)) {
	    return 0;
	}

	if(hasBeenAnnotated) {
	    return 0;
	}

	// Call annotation routine here
	

	// hardwire some data for the first test

	String currentUser = System.getProperty("user.name");
	// String docFile = "/home/sun_home/pia/src/java/ricoh/profiles/testdoc.html";
	String urlstr = "file://home/sun_home/pia/src/java/ricoh/profiles/testdoc.html";
	String conceptsList = "Agents Java Interface ExpSys NLP";
	String version = "test version"; // currently abitrary

	// System.out.println("processBuffer--did we get the transaction: " + transaction.toString());
	// String urlstr = transaction.requestURL().toString();

	System.out.println("urlstr: " + urlstr);

	if(agent == null) {
	    System.out.println("annotatedhtml agent is null");
	    resultBuf = byteArray.toByteArray();
	}
	else {
	    resultBuf = agent.testMethod(currentUser, byteArray.toByteArray(), urlstr, conceptsList, version);
	}
	hasBeenAnnotated = true;

	// Return length of byteArray after it has been
	// annotated.  Number of characters rather than
	// number of bytes?  Return buffer size for now

	return resultBuf.length;
    }


   /**Reads input stream data into char array: buf, then writes it
    * to byteArray so that it can be passed to the Reader's
    * Helper annotation routine.
    */
    protected int readData(int length) throws IOException
    {
	System.out.println("annotatedhtml readData called");
	int read     = 0;
	int writeLen = 0;

	if(source == null) {
	    System.out.println("annotatedhtml readData source is null");
	    return -1;
	}
	// Reads up to buflength, or less, or -1
	read = source.read(buf, 0, buflength);

	// Debug what we read
	// System.out.println("BUF: " + buf);
	if((read == -1) || (totalLen == headers.contentLength())) {
	    System.out.println("read done: read is: " + read + " contentLength is: " + 
			       headers.contentLength());
	    return -1;
	}
	totalLen += read;

	// Dump contents of buf into byteArray
	byteArray.write(buf, 0, read);

	System.out.println("readData after read, byteArray size: " + byteArray.size());
	// System.out.println("readData after read, byteArray contents: " + byteArray.toString());

	System.out.println("read in: " + read + " totalLen: " + totalLen);
	return read;
    }

    /**Writes the data to output.  Returns total bytes written.
     */
    protected int writeData(int length) throws IOException{
	System.out.println("annotatedhtml writeData length is: " + length);
	sink.write(byteArray.toByteArray(), 0, byteArray.size());

	// write out the annotated data
	// sink.write(resultBuf, 0, length);

	// Reset array to start at 0'th element
	byteArray.reset();
	return length;
    }

    /** Method for sending data out taps.... inefficient but not used often
     * 
     */
    protected int writeData(OutputStream out, int start,int length) throws IOException{
	/*
	sink.write(byteArray.toByteArray(), 0, byteArray.size());

	// Reset array to start at 0'th element
	byteArray.reset();
	*/
	return length;
    }

  /**
   * Loops through input reads until done.  Then writes
   * the output.
   */
  public int writeTo(OutputStream output) throws ContentOperationUnavailable, IOException {
      System.out.println("annotatedhtml writeTo called");
      if(isCurrentState(WRITING)) {
	  throw(new ContentOperationUnavailable("output  in progress"));
      }
      if(output == null){
	  throw(new ContentOperationUnavailable("output stream is null"));
      }
      if(isVisitedState(WRITING)){
	  throw(new ContentOperationUnavailable(" content already written"));
      }
      enterState(WRITING);
      // total
      int total = 0;
      // set sink for output
      setSink(output);

      int written=0;
      // repeat until all output written
      while(processInput() == true) {
      }
      written = processOutput();  // throws IOexceptions
      Pia.debug(this, "wrote " + written);
	 
      // remove sink
      unsetSink();
    
      exitState(WRITING);
      enterState(END);
      // Reset to default value
      hasBeenAnnotated=false;
      return total;
  }

    // Reads input data stream
   public boolean processInput() {
       int len =   availableToRead();
       return processInput(len);
     }

    // Figures out if there is more data to read,
    // then calls readData to read it.
   protected boolean  processInput(int length)
     {
	 System.out.println("annotatedhtml processInput called");
       // make sure that streams are appropriately initialize
       if(!isVisitedState(READING)){
	 beginProcessing();
       } 
       int len = 0;
       boolean moreToRead=true;
       // insert pending items 

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
	   //advanceNextIn(len);
	   totalIn += len;
       }
      	
       debug();
       // return true if more data remains to be read 
       // more processing can wait until we begin processing output
       return moreToRead;// || moreToProcess();
     }

    // Write data to the output stream
    protected int processOutput() throws IOException{
	
      debug();
      // make sure we have enough data to write something
      System.out.println("annotatedhtml writeData byteArray data length: " + totalLen);
      System.out.println("annotatedhtml writeData byteArray actual length: " + byteArray.size());
      int len = 0;
      if(totalLen > 0) {
	  // This calls the annotation routine and returns 
	  // the size of the annotated buffer
	  len = processBuffer(totalLen);
	  len = writeData(len);
      }
      
      // satisfy any taps
      writeOutputTaps(nextOut,len);

      return len;
    }

    public annotatedhtml() {
	super();
    }

    public annotatedhtml(java.io.InputStream in, Transaction trans){
	super(in, trans);
    }

    public annotatedhtml(java.io.Reader in, Transaction trans){
	super(in, trans);
    }


    public annotatedhtml(java.io.InputStream in){
	super(in);
    }

    public annotatedhtml(java.io.Reader in){
	super(in);
    }

    public annotatedhtml(Content content) {
	try {
	    source = ((Default)content).getSource();
	    sink = ((Default)content).getSink();
	    headers = ((Default)content).headers();
	}
	catch(ClassCastException e) {
	    System.err.println(e.getMessage() + " content must be a subclass of html");
	}
    }

}


