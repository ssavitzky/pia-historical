//   StringContent.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.


 package crc.content.text;

import crc.content.GenericContent;
import crc.pia.ContentOperationUnavailable;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;

/**
 * a simple class for wrapping a string as content
 */

public class StringContent extends GenericContent{
  /**
   * the source string, and a version for manipulating
   */

 String originalString,myString;
  
 /************************************************************
  ** constructors:
  ************************************************************/
  public StringContent(String text){
    try{
      source(text);
    } catch (ContentOperationUnavailable e){
      // should not happen on construction
    }
  }

  public void source(String text) throws ContentOperationUnavailable{
    if(isVisitedState(START)){
      throw(new ContentOperationUnavailable("already  started"));
    }
    originalString = text;
    myString = text;
    enterState(START);
  }

  /************************************************************
  ** interface implementation
  ************************************************************/
  
   OutputStreamWriter sink;

  protected void setSink( OutputStream out){
     sink = new OutputStreamWriter(out);
  }

  public int writeTo(OutputStream out) throws ContentOperationUnavailable, IOException{
    processInput();
    if(myString == null){
      throw( new ContentOperationUnavailable(" no string defined"));
    }
    enterState(WRITING);
    setSink(out);
    sink.write(myString);
    sink.flush();
    unsetSink();
    exitState(WRITING);
   return -1; // done writing
 }


  public boolean processInput(){
    if(originalString == null){
      return true; // maybe someone will set the source
    }
     exitState(START);
     return false; // nothing to do for strings
  }

  public boolean isPersistent(){
    return true;
  }

  /************************************************************
  ** editing operations 
  ************************************************************/
  // translate into regular expression operations

}

