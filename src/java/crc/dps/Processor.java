////// Processor.java: Document Processor interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

/**
 * The interface for a document Processor. 
 *	A Processor maintains two stacks: 
 *	<ol><li> an `input stack' of Input objects that are a source of Token
 *		 objects to be processed.
 *	    <li> a `parse stack' consisting of a parse tree under construction
 *		 along with additional processor state.
 *	</ol>
 *
 *	There are two ways in which an object can interface to a 
 *	Processor:
 *
 *	<ol>
 *	    <li> ``Pull mode'' -- the object requests each individual
 *		 Token.  This treats the Processor as an Input.  Nothing
 *		 special has to be done in this case.
 *
 *	    <li> ``Push mode'' -- an Output registers itself with the
 *		 Processor, which then feeds (pushes) Token objects
 *		 to the Output as they become available.  In this case
 *		 the Processor runs ``to completion,'' which is somewhat
 *		 more efficient.
 *	</ol>
 *
 * === NOTE: Both Parser and Processor need DTD and parse stack info. ===
 * === it's up to the Parser to associate Handler, etc. with Token. ===
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Token
 * @see crc.dps.Input */

package crc.dps;

public interface Processor extends Input {

  /************************************************************************
  ** Getting Output from the Processor:
  ************************************************************************/

  /** Returns the next processed Token. 
   *	The Processor will process input until either a Token is generated
   *	or the end of input is reached.  Note that it is possible for nextToken
   *	to return <code>null</code> even if <code>endInput</code> has returned
   *	false. 
   *
   * @return next Token, 
   *	or <code>null</code> if and only if processing is complete. 
   */
  public Token nextToken();

  /** Returns true if processing is complete. 
   */
  public boolean atEnd();

  /** Registers an Output object for the Processor.  The Processor 
   *	will call the Output's <code>nextToken</code> method with
   *	each Token as it becomes available, and finally call the 
   *	Output's <code>endOutput</code> function.
   */
  public void setOutput(Output anOutput);

  /** Run the Processor, pushing a stream of Token objects at its
   *	registered Output, until the Output's <code>nextToken</code>
   *	method returns <code>false</code>.
   */
  public void run();


  /************************************************************************
  ** Context Operations:
  ************************************************************************/

  /** Obtain the Handler for a given tag. */
  public Syntax getHandlerForTag(String tag);

  /** Obtain the Handler for a given Node. */
  public Syntax getHandlerForNode(Node aNode);

  /** Obtain the value associated with a given entity. */
  public NodeList getEntityValue(String name);

  /************************************************************************
  ** State Operations:
  ************************************************************************/

  /************************************************************************
  ** Control Operations:
  ************************************************************************/

  /************************************************************************
  ** Operations Used by Handlers:
  ************************************************************************/


}
