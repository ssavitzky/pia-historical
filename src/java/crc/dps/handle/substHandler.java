////// Subst.java:  Handler for <subst>
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
import crc.dom.Element;
import crc.dom.NodeList;
import crc.dom.AttributeList;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.util.*;

import gnu.regexp.RegExp;


/** Handler class for &lt;subst&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#subst">Manual
 *	Entry</a> for syntax and description.
 */
public class substHandler extends GenericHandler {
 
  public void action(Input in, Context aContext, Output out) {
    ActiveAttrList atts = Expand.getExpandedAttrs(in, aContext);
    String text = Expand.getProcessedContentString(in, aContext);
      
    String match = atts.getAttributeString("match");
    //if (ii.missing(ia, "match", match)) return;

    String repl = atts.getAttributeString("result");
    //System.err.println("*** match = " + match + ", result = " + repl
    //		         + " in " + atts.toString());

    try {
      RegExp re = new RegExp(match);
      text = re.substitute(text + (char)0, repl, true);
      text = text.substring(0, text.length()-1);
    } catch (Exception ex) {
      //ii.error(ia, "Exception in regexp: "+e.toString());
    }

    //System.err.println("result: " +text);
    out.putNode(new ParseTreeText(text));
  }


  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public substHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = QUOTED;  		// EMPTY, QUOTED, 0 (check)
  }

  substHandler(ActiveElement e) {
    this();
    // customize for element.
    if (e.hasTrueAttribute("result")) syntaxCode=NORMAL;
  }
}

