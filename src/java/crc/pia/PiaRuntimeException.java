// PiaRuntimeException.java
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




