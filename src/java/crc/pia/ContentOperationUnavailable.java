// ContentOperationUnavailable.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.


package crc.pia;

/**
 * Exception thrown when content object cannot perform requested
 * operation. e.g. trying to filter after data has been written out.
 */

public class ContentOperationUnavailable extends Exception {
    
    public ContentOperationUnavailable(String msg) {
	super(msg);
    }
}
