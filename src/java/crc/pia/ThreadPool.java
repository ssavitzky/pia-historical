// ThreadPool.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.pia;
import crc.ds.UnaryFunctor;

public Athread extend UnaryFunctor{
  static final int C_IDLE = 0;	// Zombie
  static final int C_BUSY = 1;	// Is in busy list
  
  protected int status;
  protected Thread zthread;

  Object execute( Object o ){
    Transaction t = (Transaction) o;
    zthread = new Thread( t );
    zthread.start();
    return o;
  }
  Athread(){
    status = C_IDLE;
  }
}


public class ThreadPool{
  /**
   * max number of threads
   */
  public final static String MAXTHREADS = "crc.pia.maxthreads";

  int              maxThreads = 0;

  Vector freeList = null;

  Properties props;

  public static final int MAXTHREADCOUNT = 50;

  boolean running = true;

  /**
   * check a thread out 
   */
  public synchronized Athread checkOut(){
    Athread one = null;
    
    for(int i = 0; i < freeList.size(); i ++){
      one = (Athread)freeList.elementAt( i );
      if( one.status == C_IDLE )
	break;
    }

    if( one ){
      one.status = C_BUSY;
      return one;
    }
    else
      return null;
  }

  public synchronized void notifyDone(Athread e){
    e.status = C_IDLE;
  }


  protected synchronized void addThread( ){
    Athread oneThread = new Athread();
    count++;

    freeList.addElement( oneThread );
  } 

  ThreadPool{
    //get pool properities from Pia
    this.props = Pia.getProperties();
    String zmaxThreads = props.getProperty(MAXTHREADS, MAXTHREADCOUNT);
    maxThreads = Integer.getInteger( zmaxThreads );
   
    freeList = new Vector();

    // now creates a bunch of threads
    for( int i =0; i < maxThreads; i++ ){
      addThread();
    }
    
  }
}


