// Athread.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.pia;
import crc.ds.UnaryFunctor;

public class Athread implements UnaryFunctor{
  static final int C_IDLE = 0;	// Zombie
  static final int C_BUSY = 1;	// Is in busy list
  
  protected int status;
  protected Thread zthread;

  public Object execute( Object o ){
    Transaction t = (Transaction) o;
    zthread = new Thread( t );
    zthread.start();
    return o;
  }

  Athread(){
    status = C_IDLE;
  }
}

