////// InterFormReader.java:  Read characters from an InterForm
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;
import java.io.Reader;
import java.io.IOException;

import crc.sgml.SGML;
import crc.sgml.Text;
import crc.sgml.Tokens;

import crc.interform.Interp;

/**
 * Read characters from an InterForm interpretor.
 */
public class InterFormReader extends Reader {

  /************************************************************************
  ** Components:
  ************************************************************************/

  protected Interp interp;
  protected String currentOutput;
  protected int currentPosition;

  /************************************************************************
  ** Operations:
  ************************************************************************/

  public int read(char cbuf[], int off, int len) throws IOException {
    if (interp == null) 
      throw new IOException("closed");

    while (currentOutput == null || currentOutput.length() == 0) {
      Tokens output = interp.step();
      if (output == null || output.nItems() == 0) {
	interp = null;
	return -1;
      }
      currentOutput = output.toString();
      currentPosition = 0;
      output.clear();
    }

    int nRead;
    for ( nRead = 0; nRead < len; ++nRead, ++off ) {
      cbuf[off] = currentOutput.charAt(currentPosition++);
      if (currentPosition >= currentOutput.length()) {
	currentOutput = null;
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
  }

  public InterFormReader(Interp ii) {
    this();
    interp = ii;
    ii.setStreaming();
  }
}
