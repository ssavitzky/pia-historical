//   ProcessedContent.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.content.text;

import crc.content.GenericContent;
import crc.pia.ContentOperationUnavailable;
import crc.pia.Agent;
import crc.pia.Resolver;
import crc.pia.Transaction;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.output.ToWriter;
import crc.dps.process.ActiveDoc;

import java.io.Reader;
import java.io.FileReader;
import java.io.Writer;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;

/**
 * Processed content gets run through a Document Processor on the fly. <p>
 *
 *	This class uses the new Document Processing System (DPS) to process
 *	text.  
 *
 * @see crc.dps
 */
public class ProcessedContent extends  GenericContent {

  /** The document source.  This is a DPS Input, which is essentially an
   *	iterator over a parse tree.
   */
  Input source;

  /** A reader to be parsed as input. */
  Reader reader = null;

  /** An input filename */
  String inputFileName = null;

  /** A path extension stripped from the input URL */
  String pathExtension = null;

  /** The TopProcessor that actually does the work. */
  ActiveDoc processor;

  /**
   * hold the tagset -- most editing operations affect this
   */
  Tagset tagset;

  String tagsetName;

  /**
   * hold a parse tree if not operating in stream mode
   */
  ActiveNode parseTree;

  /**
   * operate in streaming mode?  Default is true
   */
   boolean streaming = true;
  

  /**
   * add a parsing state
   */
  static final String PARSING = "PARSING";

  String[] states =  { START, READING, PARSING, WRITING, END  };


  /**
   * interform has an agent context
   */
  Agent agent;

  /************************************************************
  ** Access functions:
  ************************************************************/

  Agent getAgent(){
    return agent;
  }
  void setAgent(Agent a){
    agent = a;
  }

  /************************************************************
  ** constructors
  ************************************************************/

  public ProcessedContent(){
  }


  public ProcessedContent(Agent a, String file, String path, ActiveDoc p,
			  Transaction req, Transaction resp, Resolver res) {

    inputFileName = file;
    pathExtension = path;

    processor = p;
    tagset = processor.getTagset();
  }


  /************************************************************
  ** Content interface implementation:
  ************************************************************/

  /**
   * Use an input stream as a source.  Converts to a reader.
   */
  public void source(InputStream s) throws ContentOperationUnavailable {
    if(isVisitedState(READING)){
      throw( new ContentOperationUnavailable(" already started reading"));
    }
    // === this should be modified to use content encoding
    reader = new InputStreamReader(s);
  }

  /** Use a Reader as a source. */
  public void source(Reader r) throws ContentOperationUnavailable {
    if(isVisitedState(READING)){
      throw( new ContentOperationUnavailable(" already started reading"));
    }
    reader = r;
  }

  /** Use an Input as a source.
   *
   *	Input is the interface for unidirectional parse tree traversers. 
   */
  public void source(Input in) throws ContentOperationUnavailable {
    if(isVisitedState(READING)){
      throw( new ContentOperationUnavailable(" already started reading"));
    }
    source = in;
  }

  /**
   * makes sure that environment is properly set up
   * does not actually start parsing  unless building a parse tree
   */
  public boolean processInput(){
    if(! isVisitedState(READING)){
      beginProcessing();
    }
    if(!isCurrentState(READING)){
      return false;
    }
    if(isCurrentState(PARSING)){
      return parseMore();
    }
     return true;
  }


  public boolean isPersistent(){
     return !streaming;
  }

  /************************************************************
  ** initiate processing
  ************************************************************/

  /** Begin processing.  Most of this ought to go into initialization,
   *	so that the constructor can throw an exception if it fails. */
  protected void beginProcessing(){

    if (source == null && inputFileName != null) {
      // === here we can handle caching! ===
      try {
	reader = new FileReader(inputFileName);
      } catch (IOException e) {}
    }

    if (source == null && reader != null) {
      Parser p = tagset.createParser();
      p.setReader(reader);
      source = p;
    }

    processor.setInput(source);

    exitState(START);
    enterState(READING);
  }
	

  /************************************************************
  ** process output -- return number of characters sent to sink
  ************************************************************/
  protected int processOutput() throws IOException {
    // returns -1 because only need to call  once
    if (sink == null) throw new NullPointerException("no sink");
    Writer w = new OutputStreamWriter(sink);
    processor.setOutput(new ToWriter(w));
    processor.run();
    w.flush();
    return -1;
  }


  /************************************************************
  ** parsing functions
  ************************************************************/
  protected boolean parseMore(){
    // should build parse tree here
     return true;
  }

  protected boolean isProcessed(){
    return (! streaming && isVisitedState(PARSING) && !isCurrentState(PARSING));
  }

  /************************************************************
  ** treat as parse tree instead of stream
  ************************************************************/


  protected void buildParseTree(){
    streaming = false;
    enterState(PARSING);
    //parseTree = env.parse(source,tagset);
    exitState(PARSING);
  }

  /************************************************************
  ** editing / tagset functions
  ************************************************************/

  /**
   * override add method for strings. where = 0 prepends to body, -1 appends
   * body actor added to tagset
   */
  public void add(String s, int where) throws ContentOperationUnavailable {
     // === create a actor which matches body tags and pushes s onto its content
  }


}

