////// HTMLParser.java: HTML-specific Parser interface
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


package crc.dps.parse;

import crc.dps.NodeType;
import crc.dps.Parser;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.BitSet;

import java.io.Reader;
import java.io.IOException;

/**
 * A Parser specialized for HTML and HTML extended with InterForm and
 *	XML constructs. <p>
 *
 *	HTMLParser does not expect the document it is parsing to have a valid
 *	DTD; it is assumed to be HTML, possibly with InterForm and/or XML
 *	extensions.  At early stages of the implementation it will use
 *	hard-coded information about content models; it is hoped that
 *	eventually HTMLParser will use the DTD for everything. <p>
 *
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com 
 * @see crc.dps.Parser

 */

public class HTMLParser extends BasicParser {

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public HTMLParser() {
    super();
  }

  public HTMLParser(java.io.InputStream in) {
    super(in);
  }

  public HTMLParser(Reader in) {
    super(in);
  }

}
