////// Wrapper.java: Wrap an Input as an InputFrame.
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.input;
import crc.dps.AbstractInputFrame;
import crc.dps.Token;
import crc.dps.Input;
import crc.dps.InputStackFrame;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * An InputFrame that is simply a wrapper for an Input that does not
 *	have its own links.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 * 
 * @see crc.dps.Token
 * @see crc.dps.Processor
 * @see java.util.Enumeration
 * @see java.util.NoSuchElementException */

public class Wrapper extends AbstractInputFrame {
  
  /************************************************************************
  ** Instance variables:
  ************************************************************************/
  
  protected Input wrappedInput;

  /************************************************************************
  ** Input operations:
  ************************************************************************/

  public Token nextToken() { return wrappedInput.nextToken(); }

  public boolean atEnd() { return wrappedInput.atEnd(); }

  public Object nextElement() throws NoSuchElementException {
    return wrappedInput.nextElement();
  }

  public boolean hasMoreElements() { return wrappedInput.hasMoreElements(); }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public Wrapper() {}

  public Wrapper(Input anInput, InputStackFrame nextFrame) {
    super(nextFrame);
    wrappedInput = anInput;
  }

}
