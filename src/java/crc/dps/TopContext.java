////// TopContext.java: Top Context interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;

import java.io.PrintStream;

/**
 * The interface for a top context.
 *
 *	A top context is the root of a document-processing Context stack. 
 *	As such, it contains the tagset, global entity table, and other
 *	global information. <p>
 *
 *	Note that there may be more than one top context in a stack; this may
 *	be done, for example, in order to insert a sub-document into the
 *	processing stream.  Even the ``root'' context may have a parent.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Processor
 * @see crc.dps.Context */

public interface TopContext extends Processor {

  /************************************************************************
  ** State accessors:
  ***********************************************************************/

  /** Obtain the current Tagset. */
  public Tagset getTagset();

  /** Set the current Tagset. */
  public void setTagset(Tagset bindings);

  /** Obtain the current ProcessorInput.  
   *
   *	When processing a stream, this will be a Parser, and it is possible
   *	(though slightly risky) to change the tagset being used to parse the
   *	document.  When processing a parse tree this may return null.
   */
  public ProcessorInput getProcessorInput();

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
  ** Message Reporting:
  ************************************************************************/

  public void setLog(PrintStream log);

}
