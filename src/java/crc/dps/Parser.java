////// Parser.java: Parser interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import java.io.Reader;

/**
 * The interface for an Input that converts a character stream (Reader) into 
 *	a Token stream.  
 *
 *	Normally a Parser would need to maintain a parse stack to keep
 *	track of element nesting.  However, a Processor already does
 *	this, so all the Parser needs is a link to the Processor.
 *	Note that some forms of SGML (for example XML) can be parsed
 *	without a stack.  <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com */

public interface Parser extends Input {

  /************************************************************************
  ** Processor Access:
  ************************************************************************/

  /** Returns the Processor for which this Parser is providing input.
   */
  public Processor getProcessor();

  /************************************************************************
  ** Reader Access:
  ************************************************************************/

  /** Returns the Reader from which this Parser is obtaining input. */
  public Reader getReader();

  /** Sets the Reader from which this Parser will obtain input. */
  public void setReader(Reader aReader);

  /************************************************************************
  ** Access to Bindings:
  ************************************************************************/

  /** Returns the Tagset being used by the Parser. */
  public Tagset getTagset();

  /** Returns the character entity table to be used by the Parser.
   *	Entities in the table are quietly replaced by their values;
   *	they should correspond only to single characters.  This entity
   *	table is used for things like <code>&amp;amp;</code>.  */
  public EntityTable getEntities();

}
