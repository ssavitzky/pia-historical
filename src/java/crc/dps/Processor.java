////// Processor.java: Document Processor interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;
import crc.dom.Node;
import crc.dom.NodeList;

/**
 * The interface for a document Processor. 
 *
 *  <p>	A Processor has an Input, an Output, and a Context; the latter is a
 *	dynamic link up the processing context stack.  Processor
 *	<em>extends</em> Context, which means that it also has an entity
 *	table.
 *
 *  <p>	A TopContext (q.v.) is a specialized Processor with links to its
 *	current Tagset and, possibly, Parser.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Context
 * @see crc.dps.Input 
 * @see crc.dps.Output
 * @see crc.dps.TopContext
 */

public interface Processor extends Context {

  /************************************************************************
  ** Input and Output
  ************************************************************************/

  /** Registers an Input object for the Processor.  
   */
  public void setInput(Input anInput);

  /** Registers an Output object for the Processor.  
   */
  public void setOutput(Output anOutput);

  /************************************************************************
  ** Processing:
  ************************************************************************/

  /** Run the Processor, obtaining nodes from its Input and performing
   *	their actions, usually involving copying to the Output.
   *
   * @return <code>true</code> if processing ran to completion, 
   *	<code>false</code> if <code>stop()</code> was called.
   */
  public boolean run();

  /** Test whether the Processor is ``running''.
   */
  public boolean isRunning();

  /** Turn off the Processor's ''running'' flag. */
  public void stop();

  /** Copy nodes from the input to the output. */
  public void copy();

  /** Process the current Node */
  public void processNode();

  /** Process the current Node by expanding its attributes,
   *	then processing its children. */
  public void expandCurrentNode();

  /** Process the children of the current Node.
   *
   * @return <code>true</code> if processing ran to completion, 
   *	<code>false</code> if <code>stop()</code> was called.
   */
  public boolean processChildren();

}
