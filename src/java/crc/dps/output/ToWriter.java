////// ToWriter.java: Token output Stream to Writer
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.output;

import crc.dps.Token;
import crc.dps.Output;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.io.Writer;
import java.io.FileWriter;

/**
 * Output a Token stream to a Writer (character output stream). <p>
 *
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Token
 * @see crc.dps.Input
 * @see crc.dps.Processor
 */

public class ToWriter extends AbstractOutput {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected Writer destination = null;

  /************************************************************************
  ** Pull Mode Operations:
  ************************************************************************/

  /** Accepts the next token from the associated Processor, converts
   *	it to a string, and sends it to the associated Writer.
   *
   *	@return <code>true</code> if the Output is willing to accept more
   *		output, <code>false</code> to pause the Processor.
   */
  public boolean nextToken(Token theToken) {
    try {
      destination.write(theToken.toString());
    } catch (java.io.IOException e) {
      System.err.println(e.toString()); // === error handling needs improvement
    }
    return true;
  }

  /** Informs the Output that no more Tokens are available.
   */
  public void endOutput() {
    try {
      destination.close();
    } catch (java.io.IOException e) {}
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
