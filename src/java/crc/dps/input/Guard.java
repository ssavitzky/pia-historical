////// Guard.java: Make sure that an inside Input terminates properly.
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.input;
import crc.dps.Token;
import crc.dps.Input;
import crc.dps.InputStack;
import crc.dps.Processor;

import crc.dps.AbstractInputFrame;
import crc.dps.BasicToken;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * A hack that ensures that whatever InputFrame is pushed on top of it
 *	ends at the same level as it started, i.e. that all of its 
 *	start tags are matched.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 * 
 * @see crc.dps.Token
 * @see crc.dps.Processor
 * @see java.util.Enumeration
 * @see java.util.NoSuchElementException */

public class Guard extends AbstractInputFrame {
  
  /************************************************************************
  ** Instance variables:
  ************************************************************************/
  
  protected Processor guardedProcessor;
  protected int	      guardedDepth;

  /************************************************************************
  ** Input operations:
  ************************************************************************/

  /** Return an end tag as long as the guarded processor remains deeper than
   *	the depth we started at.  Note that we return <em>explicit</em> end
   *	tags, since they really needed to be there in the first place.
   */
  public Token nextToken() {
    if (guardedProcessor.getDepth() <= guardedDepth) return null;
    else return new BasicToken(guardedProcessor.elementTag(), 1, false);
  }

  public boolean atEnd() {
    return guardedProcessor.getDepth() <= guardedDepth;
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public Guard(Processor aProcessor) {
    guardedProcessor = aProcessor;
    guardedDepth     = aProcessor.getDepth();
  }

  public Guard(Processor aProcessor, int depth) {
    guardedProcessor = aProcessor;
    guardedDepth     = depth;
  }

  public Guard(Processor aProcessor, InputStack nextFrame) {
    super(nextFrame);
    guardedProcessor = aProcessor;
    guardedDepth     = aProcessor.getDepth();
  }

}
