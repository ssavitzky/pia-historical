////// Handler.java: Node Handler interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

/**
 * The interface for a Node Handler. 
 *
 *	A Node's Handler provides all of the necessary syntactic and
 *	semantic information required for parsing, processing, and
 *	presenting a Node and its start tag and end tag Token.  (A
 *	Handler does <em>not</em> include the additional traversal
 *	information that distinguishes a Token: an Input will
 *	associate the same Handler with a Node's start tag and end
 *	tag). <p>
 *
 *	Note that this interface says little about the implementation.
 *	It is expected, however, that any practical implementation of
 *	Handler will also be a Node, so that sets of Handlers (also
 *	called <em>tagsets</em>) can be read and stored as documents
 *	or (better) DTD's.  
 *
 *	(We may eventually make Handler an extension of Node.)
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Processor
 * @see crc.dps.Token
 * @see crc.dps.Input */

package crc.dps;

public interface Handler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /************************************************************************
  ** Parsing Operations:
  ************************************************************************/

  /************************************************************************
  ** Presentation Operations:
  ************************************************************************/

  /************************************************************************
  ** Documentation Operations:
  ************************************************************************/


}
