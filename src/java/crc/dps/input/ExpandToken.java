////// ExpandToken.java: an InputFrame to expand a Token
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.input;
import crc.dps.AbstractInputFrame;
import crc.dps.Token;
import crc.dps.BasicToken;
import crc.dps.Input;
import crc.dps.InputStack;

import crc.dom.Node;
import crc.dom.NodeType;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * An InputFrame that expands (pushes into) a Token representing an Element.
 *	The start-tag Token, content, and end-tag Token are delivered in
 *	that order.  The start tag and end tag are omitted if the Token's
 *	<code>tagname</code> is <code>null</code>.
 *
 * ===	This is very twisty.  Do we traverse the entire tree, or just
 *	return the children?  Do we copy them?  Ideally not, which means
 *	that the Processor doesn't always get a new Token and has to check.
 *
 * ===	We should NOT traverse, but return the children as nodes.
 *	Handler.nodeAction should perhaps eval (copy for cached nodes.)
 *	Token may need to know whether it's part of a cached tree.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 * 
 * @see crc.dps.Token
 * @see crc.dps.Processor
 * @see java.util.Enumeration
 * @see java.util.NoSuchElementException */

public class ExpandToken extends AbstractInputFrame {
  
  /************************************************************************
  ** Instance variables:
  ************************************************************************/
  
  protected Token theToken;
  protected int state;
  protected int item = 0;
  protected Node currentChild = null;

  /************************************************************************
  ** Input operations:
  ************************************************************************/

  public Token nextToken() { 
    if (state < 0) {
      if (! theToken.isElement() || theToken.isEmptyElement()) {
	state = 2; // no content or end tag required.
	return theToken;
      }
      state++;
      currentChild = theToken.getFirstChild();
      if (currentChild == null) state++;
      return theToken.startToken();
    } else if (state == 0) {
      Node temp = currentChild;
      currentChild = currentChild.getNextSibling();
      if (currentChild == null) state++;
      return BasicToken.createToken(temp, true);
    } else if (state == 1) {
      state++;
      return theToken.endToken();
    } else {
      return null;
    }
   }

  public boolean atEnd() { return state > 1; }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public ExpandToken(Token aToken, InputStack nextFrame) {
    super(nextFrame);
    theToken = aToken;
    state = -1;
  }

}
