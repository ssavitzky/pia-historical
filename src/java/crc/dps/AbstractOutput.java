////// AbstractOutput.java: Token output Stream abstract base class
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * The abstract base class for the Output interface.<p>
 *
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Token
 * @see crc.dps.Input
 * @see crc.dps.Processor
 */

public abstract class AbstractOutput implements Output {

  /************************************************************************
  ** Pull Mode Operations:
  ************************************************************************/

  /** Accepts the next token from the associated Processor. 
   *	The ``basic'' action is to throw it on the floor and walk away.
   *
   *	@return <code>true</code> if the Output is willing to accept more
   *		output, <code>false</code> to pause the Processor.
   */
  public abstract boolean nextToken(Token theToken);

  /** Informs the Output that no more Tokens are available.
   *	In some implementations this will have to do some kind of cleanup. 
   */
  public void endOutput() {

  }

  /************************************************************************
  ** Utilities:
  ************************************************************************/

}
