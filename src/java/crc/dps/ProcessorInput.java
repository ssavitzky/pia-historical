////// ProcessorInput.java: ProcessorInput interface
//	$Id$

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/


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
