////// ToWriter.java: Token output Stream to Writer
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


package crc.dps.output;

import crc.dps.*;
import crc.dps.util.*;
import crc.dom.*;

import java.util.NoSuchElementException;
import java.io.Writer;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Output a Token stream to a Writer (character output stream). <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Output
 * @see crc.dps.Processor
 */

public class ToWriter extends ToExternalForm {

  /************************************************************************
  ** State:
  ************************************************************************/

  protected Writer destination = null;


  /************************************************************************
  ** Internal utilities:
  ************************************************************************/

  protected void write(String s) {
    try {
      destination.write(s);
    } catch (IOException e) {}
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /** Construct an Output given a destination Writer */
  public ToWriter(Writer dest) {
    destination = dest;
  }

  /** Construct an Output given a destination filaname.  Opens the file. */
  public ToWriter(String filename) throws java.io.IOException {
    destination = new FileWriter(filename);
  }
}
