////// includeHandler.java: <include> Handler implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.Element;

import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.util.*;
import crc.dps.input.FromParseNodes;

/**
 * Handler for &lt;include&gt;....&lt;/&gt;  <p>
 *
 *	
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class includeHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;include&gt; node. */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    TopContext top  = cxt.getTopContext();
    String     url  = atts.getAttributeString("src");
    String  tsname  = atts.getAttributeString("tagset");
    String entname  = atts.getAttributeString("entity");
    boolean quoted  = atts.hasTrueAttribute("quoted");

    Tagset      ts  = top.loadTagset(tsname);	// correctly handles null
    TopContext proc = null;
    InputStream stm = null;
    ActiveEntity ent= null;

    // Check the entity.  If it's already defined, we can just use its value

    if (entname != null) {
      ent = cxt.getEntityBinding(entname, false);
      if (ent != null) {
	proc = top.subDocument(ent.getValueInput(cxt), cxt, out, ts);
	proc.run();
	return;
      }
    }

    // === at this point we should consider checking for file= and href=
    if (url == null) {
      reportError(in, cxt, "No SRC document specified.");
      return;
    }

    // Try to open the stream.  Croak if it fails. 

    try {
      stm = top.readExternalResource(url);
    } catch (IOException e) {
      out.putNode(new ParseTreeComment(e.getMessage()));
      return;
    }

    if (stm == null) {
      out.putNode(new ParseTreeComment("Cannot open " + url));
      return;
    }

    if (ts == null) {
      out.putNode(new ParseTreeComment("Cannot open tagset " + tsname));
      return;
    }

    // Create a Parser and TopProcessor to process the stream.  

    Parser p  = ts.createParser();
    p.setReader(new InputStreamReader(stm));
    proc = top.subDocument(p, cxt, out, ts);

    // If we're caching in an entity, tell the parser to save the tree in it.

    if (entname != null) {
      ent = ts.createActiveEntity(entname, null);
      p.setDocument(ent);
    }

    // Crank away.
    if (quoted) proc.copy(); else proc.run();

    if (ent != null) cxt.setEntityBinding(entname, ent, false);
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public includeHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }

  includeHandler(ActiveElement e) {
    this();
    // customize for element.
  }
}
