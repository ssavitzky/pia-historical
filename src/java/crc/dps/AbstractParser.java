////// AbstractParser.java: abstract implementation of the Parser interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import java.io.Reader;

/**
 * An abstract implementation of the Parser interface. 
 *
 *	  <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Parser
 */

public abstract class AbstractParser extends AbstractInputFrame
				     implements Parser
{

  /************************************************************************
  ** Processor Access:
  ************************************************************************/

  protected Processor processor;

  /** Returns the Processor for which this AbstractParser is providing input.
   */
  public Processor getProcessor() { return processor; }

  /************************************************************************
  ** Reader Access:
  ************************************************************************/

  protected Reader in = null;

  public Reader getReader() { return in; }
  public void setReader(Reader aReader) { in = aReader; }

  /************************************************************************
  ** Access to Bindings:
  ************************************************************************/

  protected Tagset tagset;
  protected EntityTable entities; 

  /** Returns the Tagset being used by the AbstractParser. */
  public Tagset getTagset() { return tagset; }

  /** Returns the character entity table to be used by the AbstractParser.
   *	Entities in the table are quietly replaced by their values;
   *	they should correspond only to single characters.  This entity
   *	table is used for things like <code>&amp;amp;</code>.  */
  public EntityTable getEntities() { return entities; }

  /************************************************************************
  ** Scanning Utilities:
  ************************************************************************/



  /************************************************************************
  ** Parsing Utilities:
  ************************************************************************/



  /************************************************************************
  ** Construction:
  ************************************************************************/
}
