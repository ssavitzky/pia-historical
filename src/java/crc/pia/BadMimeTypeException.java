// BadMimeTypeException.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.pia;

/** Thrown when an unknown MIME type is encountered. */
public class BadMimeTypeException extends Exception {
    
    public BadMimeTypeException(String msg) {
	super(msg);
    }
}
