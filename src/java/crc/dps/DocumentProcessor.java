////// DocumentProcessor.java: Top-level Document Processor class
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;

import java.io.PrintStream;

/**
 * A top-level Processor, implementing the TopContext and Processor
 *	interfaces.
 *
 *	A top context is the root of a document-processing Context stack. 
 *	As such, it contains the tagset, global entity table, and other
 *	global information. <p>
 *
 *	Note that there may be more than one top context in a stack; this
 *	may be done in order to insert a sub-document into the processing
 *	stream.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Processor
 * @see crc.dps.Context */

public class DocumentProcessor extends BasicProcessor
				   implements TopContext
{
  protected Tagset tagset;

  /************************************************************************
  ** State accessors:
  ***********************************************************************/

  /** Obtain the current Tagset. */
  public Tagset getTagset() 		 { return tagset; }

  /** Set the current Tagset. */
  public void setTagset(Tagset bindings) { tagset = bindings; }

  /************************************************************************
  ** Input and Output
  ************************************************************************/

  /** Registers an Input object for the Processor.  
   */
  public void setInput(Input anInput)    { input = anInput; }

  /** Registers an Output object for the Processor.  
   */
  public void setOutput(Output anOutput) { output = anOutput; }

  /************************************************************************
  ** Message Reporting:
  ************************************************************************/

  public void setLog(PrintStream log) 	 { this.log = log; }

}
