////// InterFormStream.java:  Read characters from an InterForm
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;
import java.io.InputStream;
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
 *	@see crc.interform.Interp
 *	@see crc.interform.InterFormReader
 *	@see java.io.StringWriter
 */
public class InterFormStream extends InputStream {

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

  public int read() throws IOException {
    if (interp == null) 
      throw new IOException("closed");

    if (currentPosition >= currentOutput.length()) {
      currentOutput.setLength(0);
    }
    while (currentOutput.length() == 0) {
      interp.resolve(null, true);
      if (interp.finished()) {
	interp = null;
	return -1;
      }
      currentPosition = 0;
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
    writer = new StringWriter();
    currentOutput = writer.getBuffer();
  }

  public InterFormStream(Interp ii) {
    this();
    interp = ii;
    interp.toWriter(writer);
  }
}
