//   StringContent.java
// $Id$

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/



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

