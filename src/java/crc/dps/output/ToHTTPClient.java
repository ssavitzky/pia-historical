////// ToHTTPClient.java: output nodes to HTTP client
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.output;

import crc.dps.*;
import crc.dps.util.*;
import crc.dom.*;

import crc.pia.Headers;

import java.util.NoSuchElementException;
import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;

/**
 * Output a Token stream to an HTTP client (represented by its OutputStream). 
 *
 * <p>	Contains extra machinery to output the response line and headers ahead
 *	of the document content.  This allows the DPS to modify the response
 *	type and headers. 
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Output
 * @see crc.dps.Processor
 */

public class ToHTTPClient extends ToExternalForm implements Output {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected Writer destination = null;
  protected OutputStream destStream = null;
  protected boolean headersOutput = false;
  protected Headers headers = null;

  /************************************************************************
  ** Internal utilities:
  ************************************************************************/

  protected void write(String s) {
    try {
      destination.write(s);
    } catch (IOException e) {}
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct an Output given a destination Writer */
  public ToHTTPClient(OutputStream dest, Headers hdrs) {
    destStream  = dest;
    destination = new OutputStreamWriter(destStream);
    headers     = hdrs;
  }

}
