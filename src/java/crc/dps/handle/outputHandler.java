////// outputHandler.java: <output> Handler implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.Element;

import java.io.OutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.util.*;
import crc.dps.output.ToWriter;

/**
 * Handler for &lt;output&gt;....&lt;/&gt;  <p>
 *
 *	
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
    OutputStream stm = null;

    // === at this point we should consider checking for file= and href=
    if (url == null) {
      reportError(in, cxt, "No DST document specified.");
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
