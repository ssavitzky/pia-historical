////// AbstractInput.java: Token Stream base class
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

/**
 * The abstract base class for a source or stream of Token objects.
 *
 *	All AbstractInput really does is provide a shim between the
 *	Input operations <code>nextToken</code> and <code>atEnd</code>
 *	and the corresponding Enumeration operations
 *	<code>nextElement</code> and <code>hasMoreElements</code>. <p>
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

public abstract class AbstractInput implements Input {

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
  public abstract Token nextToken();

  /** Returns true if it is known that no more Tokens are available.
   * 	Note that in some cases <code>nextToken</code> will return 
   *	<code>null</code> even after <code>atEnd</code> has returned 
   *	<code>false</code> This may happen if, for example, the end of
   *	a token can be determined without asking the input stream for
   *	the next character, or if the remainder of the input stream is
   *	ignorable.
   */
  public abstract boolean atEnd();

  /************************************************************************
  ** Enumeration Operations:
  ************************************************************************/

  public Object nextElement() throws NoSuchElementException {
    Object o = nextToken();
    if (o == null) throw new NoSuchElementException();
    else return o;
  }

  /** Are there more elements waiting to be returned?  
   *	Note that some subclasses of AbstractInput may have to ``look
   *	ahead'' to ensure that <code>hasMoreElements</code> can return
   *	an accurate result.  This implementation assumes that
   *	<code>atEnd</code> is accurate.
   */
  public boolean hasMoreElements() { return ! atEnd(); }
}
