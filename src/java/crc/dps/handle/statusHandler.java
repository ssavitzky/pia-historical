////// statusHandler.java: <status> Handler implementation
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

import crc.dom.NodeList;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.util.*;

import java.io.File;
import java.net.URL;

/**
 * Handler for &lt;status&gt;....&lt;/&gt;  
 *
 * <p>	Determine the status of a resource. 
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class statusHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;status&gt; node. */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    String entityName = atts.getAttributeString("entity");
    String srcURL     = atts.getAttributeString("src");
    if (srcURL != null) getStatusForURL(srcURL, cxt, out, atts);
    if (entityName != null) getStatusForEntity(entityName, cxt, out, atts);
  }

  /** This does the parse-time dispatching. <p>
   *
   *	Action is dispatched (delegated) to a subclass if the string
   *	being passed to <code>dispatch</code> is either the name of an
   *	attribute or a period-separated suffix of the tagname. <p>
   */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "src")) 	 return status_src.handle(e);
    if (dispatch(e, "entity")) 	 return status_entity.handle(e);
    return this;
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public statusHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = EMPTY;  		// NORMAL, QUOTED, 0 (check)
  }

  statusHandler(ActiveElement e) {
    this();
    // customize for element.
  }

  /************************************************************************
  ** Actually doing the work:
  ************************************************************************/

  /** Get the status for an entity that may be connected to an external
   *	resource.
   */
  void getStatusForEntity(String name, Context cxt, Output out, 
			  ActiveAttrList atts) {
    String item = atts.getAttributeString("item");
    ActiveEntity entity = cxt.getEntityBinding(name, false);
    putList(out, Status.getStatusItem(entity, item));
  }

  /** Get the status for a URL that refers to an external resource. */
  void getStatusForURL(String url, Context cxt, Output out,
		       ActiveAttrList atts) {
    String item = atts.getAttributeString("item");
    TopContext top = cxt.getTopContext();
    if (url.indexOf(":") < 0 || url.startsWith("file:") ||
	url.indexOf("/") >= 0 && url.indexOf(":") > url.indexOf("/")) {
      File local = top.locateSystemResource(url, true);
      // === specify forWriting in case it's a data file
      if (local != null) {
	putList(out, Status.getStatusItem(local, item));
      }
    } else {
      URL remote = top.locateRemoteResource(url, false);
      putList(out, Status.getStatusItem(remote, item));
    }
  }
}

class status_src extends statusHandler {
  public void action(Input in, Context cxt, Output out,
  		     ActiveAttrList atts, NodeList content) {
    String srcURL     = atts.getAttributeString("src");
    getStatusForURL(srcURL, cxt, out, atts);
  }
  public status_src(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new status_src(e); }
}

class status_entity extends statusHandler {
  public void action(Input in, Context cxt, Output out,
  		     ActiveAttrList atts, NodeList content) {
    String entityName = atts.getAttributeString("entity");
    getStatusForEntity(entityName, cxt, out, atts);
  }
  public status_entity(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new status_entity(e); }
}

