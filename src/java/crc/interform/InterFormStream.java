////// InterFormStream.java:  Read characters from an InterForm
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;
import java.io.InputStream;
import java.io.IOException;

import crc.sgml.SGML;
import crc.sgml.Text;
import crc.sgml.Tokens;

import crc.interform.Interp;

/**
 * Read characters from an InterForm interpretor.
 */
public class InterFormStream extends InputStream {

  /************************************************************************
  ** Components:
  ************************************************************************/

  protected Interp interp;
  protected String currentOutput;
  protected int currentPosition;

  /************************************************************************
  ** Operations:
  ************************************************************************/

  public int read() throws IOException {
    if (interp == null) 
      throw new IOException("closed");

    if (currentOutput != null && currentPosition >= currentOutput.length()) {
      currentOutput = null;
    }
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

    return currentOutput.charAt(currentPosition++);
  }

  /** Close the output stream.  Sets it to null. */
  public void close() {
    interp = null;
  }
    
  /************************************************************************
  ** Construction:
  ************************************************************************/

  public InterFormStream() {
    super();
  }

  public InterFormStream(Interp ii) {
    this();
    interp = ii;
    ii.setStreaming();
  }
}
