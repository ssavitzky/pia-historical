////// InterFormReader.java:  Read characters from an InterForm
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;

import java.io.Reader;
import java.io.IOException;
import java.io.StringWriter;

import crc.sgml.SGML;
import crc.sgml.Text;
import crc.sgml.Tokens;

import crc.interform.Interp;

/**
 * Read characters from an InterForm interpretor.
 *	A StringWriter and its StringBuffer are kept around as the buffer;
 *	the Interp's output is written directly into the buffer rather than
 *	being converted to a String.
 *
 *	@see InterFormStream
 */
public class InterFormReader extends Reader {

  /************************************************************************
  ** Components:
  ************************************************************************/

  protected Interp interp;
  protected StringBuffer currentOutput;
  protected int 	 currentPosition;
  protected StringWriter writer;

  /************************************************************************
  ** Operations:
  ************************************************************************/

  public int read(char cbuf[], int off, int len) throws IOException {
    if (interp == null) 
      throw new IOException("closed");

    if (currentPosition >= currentOutput.length()) {
      currentOutput.setLength(0);
    }
    while (currentOutput.length() == 0) {
      Tokens output = interp.step();
      if (output == null || output.nItems() == 0) {
	interp = null;
	return -1;
      }
      output.writeOn(writer);
      currentPosition = 0;
      output.clear();
    }

    int nRead;
    for ( nRead = 0; nRead < len; ++nRead, ++off ) {
      cbuf[off] = currentOutput.charAt(currentPosition++);
      if (currentPosition >= currentOutput.length()) {
	currentOutput.setLength(0);
	break;
      }
    }
    return nRead;
  }

  /** Close the output stream.  Sets it to null. */
  public void close() {
    interp = null;
  }
    
  /************************************************************************
  ** Construction:
  ************************************************************************/

  public InterFormReader() {
    super();
    writer = new StringWriter();
    currentOutput = writer.getBuffer();
  }

  public InterFormReader(Interp ii) {
    this();
    interp = ii;
  }
}
