////// Processor.java: Document Processor interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;
import crc.dom.Node;
import crc.dom.NodeList;

/**
 * The interface for a document Processor. 
 *	A Processor maintains two stacks: 
 *	<ol><li> an `input stack' of Input objects that are a source of Token
 *		 objects to be processed.
 *	    <li> a `parse stack' consisting of a parse tree under construction
 *		 along with additional processor state.
 *	</ol>
 *
 *	There are three ways in which an object can interface to a 
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
 *
 *	    <li> ``Parse mode'' -- the Processor constructs a complete
 *		 Document, which the object requests.
 *	</ol>
 *
 *	A Processor will normally ensure that any element started inside
 *	an Input is ended when that Input is popped off the stack.<p>
 *
 * === The ideal thing would be for a Processor to build documents
 *	using Token only for the nodes that will have to be
 *	executable, and objects out of crc.dom for everything else.
 *	Tricky.  Wonder whether the different ``ways'' above correspond
 *	to different extensions of Processor...
 *
 * === NOTE: Both Parser and Processor need DTD and parse stack info. ===
 * === it's up to the Parser to associate Handler, etc. with Token. ===
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Token
 * @see crc.dps.Input */

public interface Processor extends Context {

  /************************************************************************
  ** Input and Output
  ************************************************************************/

  /** Get the Processor's Input object.
   */
  public Input getInput();

  /** Registers an Input object for the Processor.  
   */
  public void setInput(Input anInput);

  /** Get the Processor's Output object.
   */
  public Output getOutput();

  /** Registers an Output object for the Processor.  
   */
  public void setOutput(Output anOutput);

  /************************************************************************
  ** Processing:
  ************************************************************************/

  /** Run the Processor, pushing a stream of Token objects at its
   *	registered Output, until the Output's <code>nextToken</code>
   *	method returns <code>false</code>.
   */
  public void run();

  /** Test whether the Processor is ``running''.
   */
  public boolean isRunning();

  /** Turn off the Processor's ''running'' flag. */
  public void stop();

}
