// PiaRuntimeException.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.pia ;

/**
 * PIA runtime exception.
 * These exeptions should be thrown whenever as a programmer you encounter
 * an abnormal situation. These exception are guaranted to be catched, and to
 * only kill the client (if this makes sense) that triggered it.
 */

public class PiaRuntimeException extends RuntimeException {
    /**
     * Create a new Runtime exception. 
     * @param o The object were the error originated.
     * @param mth The method were the error originated.
     * @param msg An message explaining why this error occured.
     */

    public PiaRuntimeException (Object o, String mth, String msg) {
      /*
	super (o.getClass().getName()
	       + "[" + mth + "]: "
	       + msg) ;
      */
	super ( msg );
    }

}




