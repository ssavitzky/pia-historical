// Timer.java
// $Id$
// COPYRIGHT 1997 Ricoh Silicon Valley.

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











