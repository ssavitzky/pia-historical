//   ParsedContent.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.content.text;

import crc.content.GenericContent;
import crc.pia.ContentOperationUnavailable;
import crc.interform.Environment;
import crc.interform.Tagset;
import crc.interform.Actor;
import crc.interform.Handler;  // where is best place to create insertion handler?
import crc.sgml.Tokens;
import crc.sgml.SGML;


import java.io.Reader;
import java.io.Writer;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;

/**
 * Parsed content gets run through a parser on the fly.
 * Although it is usually text, the operations are very differently
 * implemented via tagsets.
 */
public class ParsedContent extends  GenericContent{

  /**
   * source and sink variables  --
   */
  Reader source;
  OutputStream sink; // really this should be a Writer

  /**
   * the object which contains the interpreter, entities, etc.
   */
   Environment env;

  /**
   * hold the tagset -- most editing operations affect this
   */
  Tagset tagset;

  /**
   * hold a parse tree if not operating in stream mode
   */
  Tokens parseTree;

  /**
   * operate in streaming mode?  Default is true
   */
   boolean streaming = true;
  

  /**
   * add a parsing state
   */
  static final String PARSING = "PARSING";

  String[] states =  { START, READING, PARSING, WRITING, END  };



  /************************************************************
  ** constructors
  ************************************************************/

  public ParsedContent(){
  }

  public ParsedContent(InputStream s){
    try{source(s);} catch(ContentOperationUnavailable e){
      // should always be available at construction
    }
  }


  public ParsedContent(Environment e){
    setEnvironment(e);
  }

  /************************************************************
  ** interface implementation
  ************************************************************/
  /**
   * use an input stream as a source.  Converts to a reader.
   * this should be modified to use content encoding
   */
  public void source(InputStream s) throws ContentOperationUnavailable{
    if(isVisitedState(READING)){
      throw( new ContentOperationUnavailable(" already started reading"));
    }
    // this should be modified to use content encoding
     source(new InputStreamReader(s));
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
  protected void beginProcessing(){
    if(env==null){
      setEnvironment(new Environment());
    }
    if(tagset==null){
       initializeTagset();
       
    }


    exitState(START);
    enterState(READING);
  }
	

  /************************************************************
  ** process output -- return number of characters sent to sink
  ************************************************************/
  protected int processOutput() throws IOException {
    // returns -1 because only need to call  once
    if(isParsed()) {
      new OutputStreamWriter(sink).write(parseTree.toString());
    } else {
      env.runStream(source,sink,tagset);
    }
    return -1;
  }


  /**
   * set the run time environment for parsing
   */
  public void setEnvironment(Environment e){
    env = e;
  }

  /************************************************************
  ** parsing functions
  ************************************************************/
  protected boolean parseMore(){
    // should build parse tree here
     return true;
  }

  protected boolean isParsed(){
    return (! streaming && isVisitedState(PARSING) && !isCurrentState(PARSING));
  }

  /************************************************************
  ** treat as parse tree instead of stream
  ************************************************************/
  public SGML getParseTree(){
     buildParseTree();
    return  parseTree;
  }

  protected void buildParseTree(){
    streaming = false;
    enterState(PARSING);
    parseTree = env.parse(source,tagset);
    exitState(PARSING);
  }

  /************************************************************
  ** editing / tagset functions
  ************************************************************/
  protected void initializeTagset(){
    if(headers.contentType().indexOf("html") > -1){
       tagset = Tagset.tagset("HTML");
    } else {
       tagset = Tagset.tagset("Standard");
    }
  }

  /**
   * set a tagset as the one to use
   */

 public void  setTagset(Tagset t){
   tagset = t;
  }
  /**
   * override add method for actors. where is meaningless...
   * actor added to tagset
   */
  public void add(Actor actor, int where) throws ContentOperationUnavailable {
     if(tagset == null)initializeTagset();
     tagset.define(actor);
  }


  /**
   * override add method for strings. where = 0 prepends to body, -1 appends
   * body actor added to tagset
   */
  public void add(String s, int where) throws ContentOperationUnavailable {
     if(tagset == null)initializeTagset();
     // create a actor which matches body tags and pushes s onto its content
     // === TBD ===
     //     Actor  actor = new Actor();
     //     tagset.define(actor);
  }

  /**
   * override  replace method for actors. where is meaningless...
   * actor added to tagset
   */
  public  void replace(Actor src_actor, Actor dst_actor) throws ContentOperationUnavailable {
     if(tagset == null)initializeTagset();
     // any way to remove actors?
     tagset.define(dst_actor);
  }


}

