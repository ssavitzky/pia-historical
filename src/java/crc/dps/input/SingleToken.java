////// SingleToken.java: an InputFrame for a single Token
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.input;
import crc.dps.AbstractInputFrame;
import crc.dps.Token;
import crc.dps.Input;
import crc.dps.InputStack;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * An InputFrame that is simply a single Token.  The Token is delivered
 *	exactly once. 
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 * 
 * @see crc.dps.Token
 * @see crc.dps.Processor
 * @see java.util.Enumeration
 * @see java.util.NoSuchElementException */

public class SingleToken extends AbstractInputFrame {
  
  /************************************************************************
  ** Instance variables:
  ************************************************************************/
  
  protected Token theToken;

  /************************************************************************
  ** Input operations:
  ************************************************************************/

  public Token nextToken() { 
    Token temp = theToken;
    theToken = null;
    return temp;
  }

  public boolean atEnd() { return theToken == null; }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public SingleToken() {}

  public SingleToken(Token aToken, InputStack nextFrame) {
    super(nextFrame);
    theToken = aToken;
  }

}
