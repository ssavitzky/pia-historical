// GenericContent.java
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



package crc.content;
import crc.pia.Pia;
import crc.pia.Machine;
import crc.pia.Agent;
import crc.pia.Headers;
import crc.ds.Table;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;


import crc.pia.Content;

import crc.pia.ContentOperationUnavailable;
import crc.ds.List;

import java.util.Enumeration;
/** GenericContent
 *  an abstract class for content types.  This class provides default
 * methods for most of the agent interactions.  Such as tapping, and
 * notification on state changes. 
 */
public abstract  class  GenericContent implements crc.pia.Content {
  /**
   *  States  keep track of the status of the content.
   * Useful for notifying agents
   * Standard states are:
   * <dl>
   *   <dt>  START
   *   <dd>  content has been initialized, but no data read yet (OK to tapIn)
   *   <dt> READING
   *   <dd>   some data has been read, but none written (OK to tapOut)
   *  <dt>  WRITING
   *  <dd>  some data has been written out
   *   <dt>  END
   *   <dd>  all data has been written to output, streams are closed
   * </dl>
   * multiple states may be active at any one time
   */

   protected static final String START = "START", READING = "READING", 
                     WRITING = "WRITING" , END = "END";
    
  /**
   * an array of stares in canonical order
   */
  protected String[] states = { START, READING, WRITING, END  };
  
  

   /**
    * a  List of all active states
    */
   protected List currentStates = new List();
   protected List visitedStates = new List();

  /**
   * hold output stream -- subclass may override
   */
   protected OutputStream sink;


  /**
   * headers
   */  
  protected Headers headers;

  /**
   * a hash of agent, object pairs to be notify on a given  state change
   */
  protected Table agents = new Table();
 
  /************************************************************
  ** header functions
  ************************************************************/

  
  /**
   * return the headers object
   */
  public Headers headers(){
    return headers;
  }

  /**
   * set Headers object
   */
  public void setHeaders( Headers headers ){
      this.headers = headers;
  }


  /************************************************************
  ** set source
  ************************************************************/

  /**
   * attempt to set source, subclass should override for particular data types
   */

  public void source(Object input) throws ContentOperationUnavailable{
    Pia.debug(this,input.getClass().getName() + " not handled");
    throw( new ContentOperationUnavailable(input.getClass().getName() + " not handled by" + this.getClass().getName()));
  }


 /************************************************************
  ** Write methods:
  ** sub class should override processOutput
  ************************************************************/
  /**
   * subclass should override this method to actually send data
   * out the sink
   */

  protected int processOutput() throws IOException{
    // sink.write(buffer};
     throw(new IOException(" subclass must provide processOutput method"));
  }

  /**
   * send data to output stream  does not return until all data written
   */
  public int writeTo(OutputStream output) throws ContentOperationUnavailable, IOException{
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
     while(written >= 0) {
       total += written;

       processInput();         // catches IO exceptions
       written = processOutput();  // throws IOexceptions
       Pia.debug(this, "wrote " + written);
	 
     }
      // remove sink
      unsetSink();

      exitState(WRITING);
      enterState(END);
      return total;
  }


  /**
   * subclass should override
   */
  protected void setSink(OutputStream out){
    sink = out;
  }

  protected void unsetSink(){
    sink = null;
  }
 
  /************************************************************
  ** Agent interactions:
  ************************************************************/


  /** Add an output stream to "tap" the data before it is written. 
   * Taps will get data during a read operation just before the data "goes
   * out the door" 
   * Uses  arrays for slightly more efficiency
   * sub class should override
   */
   public void tapOut(OutputStream tap) throws ContentOperationUnavailable{
       throw(new ContentOperationUnavailable("OutputTap not supported by" + this.getClass().getName()));
   }

  /** Add an input stream to "tap" the data as it is read.
   * Taps will get data	 as soon as it is  available to the content 
   * -- before any processing occurs.  
   * tapOut == tapIn if content does not support editing/processing
   * @throws ContentOperationUnavailable if already started reading or 
   * tapping not supported
   */
   public void tapIn(OutputStream tap) throws ContentOperationUnavailable{
     throw(new ContentOperationUnavailable("InputTap not supported by" + this.getClass().getName()));
   }
  
  /** specify an agent to be notified when  entering a state, for
   * example "END"
   * @param state: string naming the state change agent is interested in
   * @param arg:  arbitrary object that will be sent back to be agent when
   *		the state occurs and the agents contentUpdate method is called
   * @throws  exception if state already visited or not known
   */
   public void notifyWhen(Agent interested, String state, Object arg) throws ContentOperationUnavailable{
     if(isVisitedState(state)){
       throw(new ContentOperationUnavailable("Already entered" + state));
     }
     if(!isValidState(state)){
       throw(new ContentOperationUnavailable(state + " not valid"));
     }
     // add array of agent and argument to list of agents for this state
     Object[] ao = new Object[2];
     ao[0]= interested;
     ao[1] = arg;
     List notify;
     // make list for state if none exists
     if(!agents.has(state)){
        notify = new List();
	agents.at(state, notify);
     }
      notify = (List) agents.at(state);
      notify.push(ao);
   }



   /************************************************************
   ** state manipulation:
   ************************************************************/

  /**
   * return a list of states that this content can go through
   * agents can use this to determine when to be notified
   */
   public String[] states(){
     return states;
   }

   /**
    * return true if this state is known to us
    */
   public boolean isValidState(String state){
     String[] states = states();
     for(int i=0;i< states.length;i++){
       if (states[i].equalsIgnoreCase(state)) return true;
     }
      return false;
   }

   /**
    * return concatenation of active states
    */
   public String getCurrentStates(){
     return currentStates.join(" ");
   }

   /**
    * is this state active?  
    */
  protected boolean isCurrentState(String state) {
				//  this would be faster with a hash
     return (currentStates.indexOf(state) >= 0)? true : false;
   }
   

  protected boolean isVisitedState(String state) {
		//  this would be faster with a hash
      return (visitedStates.indexOf(state) >= 0)? true : false;
    }


    
   /**
    * enter a state
    */
   protected void enterState(String state){
     Pia.debug(this,"Entering state" + state);
     
     currentStates.push(state);
     visitedStates.push(state);
     List notify = (List) agents.at(state);
     if(notify != null){
       Enumeration lists = notify.elements();
       while(lists.hasMoreElements()){
	 Object[] ao = (Object[]) lists.nextElement();
	 Agent agent = (Agent) ao[0];
	 Object arg = ao[1];
	 agent.updateContent( (crc.pia.Content) this,state,arg);
       }
     }
   }

   /**
    * exit a state
    */
   protected void exitState(String state){
     Pia.debug(this, "Exiting state " + state);
     currentStates.remove(state);
   }
   
   /**
    * assert a state -- enter if not already in
    */
   protected void setState(String state){
     if( ! isCurrentState(state)){
       enterState(state);
     }
   }

   


 /************************************************************
  ** Content specific operations:
  ************************************************************/

  /**
   * Add an object to the content
   * if object is not a compatible type, throws exception
   * @param  where: interpretation depend on content,  by convention 0 means at front
   * -1 means at end, everything else is subject to interpretation
   * subclass should override
   */
  public void add(Object moreContent, int where) throws ContentOperationUnavailable {
    Pia.debug(this, "adding " + moreContent.getClass().getName() + " not supported by " + this.getClass().getName());
    throw(new ContentOperationUnavailable("adding " + moreContent.getClass().getName() + " not supported by " + this.getClass().getName()));
  }
  
}
