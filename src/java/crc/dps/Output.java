////// Output.java: Token Stream interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

/**
 * The interface for a consumer of Token objects.<p>
 *
 *	There are two ways in which an object can interface to a 
 *	Processor:
 *
 *	<ol>
 *	    <li> ``Pull mode'' -- the object requests each individual
 *		 Token.  This treats the Processor as an Input.  Nothing
 *		 special has to be done in this case.
 *
 *	    <li> ``Push mode'' -- the Output registers itself with the
 *		 Processor, which then feeds (pushes) Token objects
 *		 to the Output as they become available.
 *	</ol>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Token
 * @see crc.dps.Input
 * @see crc.dps.Processor
 */

package crc.dps;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public interface Output {

  /************************************************************************
  ** Pull Mode Operations:
  ************************************************************************/

  /** Accepts the next token from the associated Processor. 
   */
  public boolean nextToken(Token theToken);

  /** Informs the Output that no more Tokens are available.
   */
  public void endOutput();

}
