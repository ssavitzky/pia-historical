////// ProcessorInput.java: ProcessorInput interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;

/**
 * The interface for an Input that knows about the Processor it is providing
 *	input to.  This permits the ProcessorInput to make use of the
 *	Processor's state, for example its parse stack and Tagset.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com */

public interface ProcessorInput extends Input {

  /************************************************************************
  ** Processor Access:
  ************************************************************************/

  /** Returns the Processor for which this ProcessorInput is providing input.
   */
  public Processor getProcessor();

  /** Sets the Processor for which this ProcessorInput is providing input.
   */
  public void setProcessor(Processor aProcessor);

  /************************************************************************
  ** Access to Bindings:
  ************************************************************************/

  /** Returns the Tagset being used by the ProcessorInput. */
  public Tagset getTagset();

  /** Sets the Tagset being used by the ProcessorInput. */
  public void setTagset(Tagset aTagset);

  /** Returns the character entity table to be used by the ProcessorInput.
   *	Entities in the table are quietly replaced by their values;
   *	they should correspond only to single characters.  This entity
   *	table is used for things like <code>&amp;amp;</code>.  */
  public EntityTable getEntities();

  /** Sets the Tagset being used by the ProcessorInput. */
  public void setEntities(EntityTable anEntityTable);
}
