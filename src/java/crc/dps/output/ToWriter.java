////// ToWriter.java: Token output Stream to Writer
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.output;

import crc.dps.*;
import crc.dps.util.*;
import crc.dom.*;

import java.util.NoSuchElementException;
import java.io.Writer;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Output a Token stream to a Writer (character output stream). <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Output
 * @see crc.dps.Processor
 */

public class ToWriter extends ToExternalForm {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected Writer destination = null;


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
  public ToWriter(Writer dest) {
    destination = dest;
  }

  /** Construct an Output given a destination filaname.  Opens the file. */
  public ToWriter(String filename) throws java.io.IOException {
    destination = new FileWriter(filename);
  }
}
