////// outputHandler.java: <output> Handler implementation
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


package crc.dps.handle;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.Element;

import java.io.OutputStream;
import java.io.IOException;
import java.io.File;
import java.io.OutputStreamWriter;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.util.*;
import crc.dps.output.ToWriter;

/**
 * Handler for &lt;output&gt;....&lt;/&gt;  <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class outputHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;output&gt; node. */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    TopContext top  = cxt.getTopContext();
    String     url  = atts.getAttributeString("dst");
    boolean append  = atts.hasTrueAttribute("append");
    boolean directory = atts.hasTrueAttribute("directory");

    OutputStream stm = null;

    // === at this point we should consider checking for file= and href=
    if (url == null) {
      reportError(in, cxt, "No DST document specified.");
      return;
    }

    if (directory) {
      File dir = top.locateSystemResource(url, true);
      if (! dir.exists()) dir.mkdirs();
      return;
    }

    // Try to open the stream.  Croak if it fails. 

    try {
      stm = top.writeExternalResource(url, append, true, false);
    } catch (IOException e) {
      out.putNode(new ParseTreeComment(e.getMessage()));
      return;
    }

    if (stm == null) {
      out.putNode(new ParseTreeComment("Cannot open " + url));
      return;
    }

    OutputStreamWriter writer = new OutputStreamWriter(stm);
    Output output = new ToWriter(writer);
    Copy.copyNodes(content, output);

    try {
      writer.close();
      stm.close();
    } catch (IOException e) {}

  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public outputHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }

  outputHandler(ActiveElement e) {
    this();
    // customize for element.
  }
}
