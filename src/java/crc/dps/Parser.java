////// Parser.java: Parser interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;

import java.io.Reader;

/**
 * The interface for an Input that converts a character stream (Reader) into 
 *	a Token stream.  <p>
 *
 *	Being a ProcessorInput, the Parser gets all of the syntactic
 *	information and parse-stack state it needs from the Processor.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com */

public interface Parser extends ProcessorInput {

  /************************************************************************
  ** Reader Access:
  ************************************************************************/

  /** Returns the Reader from which this Parser is obtaining input. */
  public Reader getReader();

  /** Sets the Reader from which this Parser will obtain input. */
  public void setReader(Reader aReader);

}
