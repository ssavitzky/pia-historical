////// Input.java: Token Stream interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * The interface for a source or stream of Token objects.
 *
 *	The Processor maintains a stack of Input objects from which
 *	Token objects are obtained as needed.  An Input may have to be
 *	provided with a reference back to the Processor it is sending
 *	input to, in case it needs additional information (e.g. for
 *	parsing).  This is done during initialization and is not part
 *	of the interface. <p>
 *
 *	Note that although an Input superficially resembles a
 *	NodeEnumeration, the two interfaces are quite different.
 *	NodeEnumeration is bidirectional, whereas it may not be
 *	feasible to back up an Input.  Also, an Input may perform a
 *	depth-first traversal of any Element encountered, whereas a
 *	NodeEnumeration returns the entire Element.  <p>
 *
 *	Note that an Input is expected to ensure that every Element
 *	for which a start tag Token is produced, also gets a corresponding
 *	end tag Token. <p>
 *
 *	Note that an Input which is being used as an Enumeration may
 *	have to ``look ahead'' to ensure that <code>hasMoreElements</code>
 *	can return an accurate result. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 * 
 * @see crc.dps.Token
 * @see crc.dps.Processor
 * @see java.util.Enumeration
 * @see java.util.NoSuchElementException
 */

public interface Input extends Enumeration {

  /************************************************************************
  ** Token Operations:
  ************************************************************************/

  /** Returns the next Token from this source and advances to the
   *	next.  This is just the typed version of the Enumeration
   *	operation <code>nextElement</code>, except that it returns
   *	<code>null</code> at the end of the input rather than throwing
   *	NoSuchElementException.  <p>
   *
   * @return next Token, 
   *	or <code>null</code> if and only if no more tokens are available.
   */
  public Token nextToken();

  /** Returns true if it is known that no more Tokens are available.
   * 	Note that in some cases <code>nextToken</code> will return 
   *	<code>null</code> even after <code>atEnd</code> has returned 
   *	<code>false</code> This may happen if, for example, the end of
   *	a token can be determined without asking the input stream for
   *	the next character, or if the remainder of the input stream is
   *	ignorable.
   */
  public boolean atEnd();

}
