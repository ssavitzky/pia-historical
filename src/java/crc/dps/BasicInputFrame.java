////// BasicInputFrame.java: Token Stream base class
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

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

package crc.dps;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class BasicInputFrame extends AbstractInputFrame {
  
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

  public BasicInputFrame() {}

  public BasicInputFrame(Input anInput, InputStackFrame nextFrame) {
    super(nextFrame);
    wrappedInput = anInput;
  }

}
