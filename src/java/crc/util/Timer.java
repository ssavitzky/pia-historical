// Timer.java
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


package crc.util;
import java.io.*;
import w3c.tools.timers.EventManager;
import w3c.tools.timers.EventHandler;
import crc.ds.UnaryFunctor;



/**
 * implements a one shot timer
 */
public class Timer implements EventHandler{

  /**
   * store functor that will be execute upon timer arrival
   */
  UnaryFunctor f;

  /**
   * data to be passed to functor
   */ 
  Object zdata;

  /**
   * handle in case need to stop timer
   */
  Object handle;

  public boolean notdone = true;

  private static EventManager em = null;

  private EventManager em(){
    if( em == null ){
      em = new EventManager();
      em.setDaemon(true);
      em.start();
    }
    return em;
  }
  public synchronized void handleTimerEvent (Object data, long time) {
    UnaryFunctor f = (UnaryFunctor)data;
    f.execute( zdata );
    notdone = false;
  }

  public synchronized void setTimeout (int ms) {
    handle = em().registerTimer (ms, this, f) ;
  }

  public void stop(){
    em().recallTimer( handle );
  }

  public Timer( UnaryFunctor f, Object data ){
    this.f = f;
    this.zdata = data;
  }


  public static void main(String argv[]){
    Timer ztimer = new Timer( new hello(), "Crazy u" );
    ztimer.setTimeout(50);

    while( ztimer.notdone ){
      sleep( 1000 );
    }
  }

  private static void sleep(int howlong){
    Thread t = Thread.currentThread();
    
    try{
      t.sleep( howlong );
    }catch(InterruptedException e){;}
    
  }
  
}

  /** 
   * for testing only
   */
  class hello implements UnaryFunctor{

    public Object execute( Object object ){
      String s = (String) object;
      System.out.println( s );
      return object;
    }
    
  }











