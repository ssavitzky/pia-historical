// ThreadPool.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.pia;
import crc.ds.UnaryFunctor;
import crc.pia.Athread;
import java.util.Vector;
import crc.pia.Piaproperties;


public class ThreadPool{
  /**
   * max number of threads
   */
  public final static String MAXTHREADS = "crc.pia.maxthreads";

  int              maxThreads = 0;

  Vector freeList = null;

  Piaproperties props;

  public static final int MAXTHREADCOUNT = 50;

  boolean running = true;

  /**
   * check a thread out 
   */
  public synchronized Athread checkOut(){
    Athread one = null;
    
    for(int i = 0; i < freeList.size(); i ++){
      one = (Athread)freeList.elementAt( i );
      if( one.status == one.C_IDLE )
	break;
    }

    if( one!= null ){
      one.status = one.C_BUSY;
      return one;
    }
    else
      return null;
  }

  public synchronized void notifyDone(Athread e){
    e.status = e.C_IDLE;
  }


  protected synchronized void addThread( ){
    Athread oneThread = new Athread();

    freeList.addElement( oneThread );
  } 

  ThreadPool(){
    //get pool properities from Pia
    this.props = Pia.instance().properties();
    int maxThreads = props.getInteger(MAXTHREADS, MAXTHREADCOUNT);
   
    freeList = new Vector();

    // now creates a bunch of threads
    for( int i =0; i < maxThreads; i++ ){
      addThread();
    }
    
  }
}


