////// LegacyHandler.java: Node Handler legacy implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.Element;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.util.*;

/**
 * Wrapper for legacy handlers from <code>crc.interform.handle</code>.
 *
 *	This is a sample implementation of a specialized subclass of
 *	GenericHandler for the ``legacy'' case.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class LegacyHandler extends GenericHandler {

  public crc.interform.Handler wrapped = null;

  /************************************************************************
  ** Parse-Time Operations:
  ************************************************************************/

  public Action getActionForNode(ActiveNode n) {
    Action a = wrapped.getActionForNode(n, this);
    return (a == null)? this : a;
  }

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Perform the operation by passing it off to the wrapped legacy 
   *	handler.  It's unfeasible to use the old action routines, because
   *	the paradigms are so different, so instead we just re-implement
   *	the action as needed.
   */
  public void action(Input in, Context aContext, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    String tag = in.getTagName();
    if (wrapped.action(aContext, out, tag, atts, content,
		       ((content == null)? null : content.toString()))) {
      return;
    }
    aContext.message(-1, "unimplemented action for <" + tag + ">", 0, true);
    super.action(in, aContext, out, atts, content);
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public LegacyHandler(crc.interform.Handler h) {
    wrapped = h;
  }

  /** Utility to wrap a different handler, for dispatching. */
  public LegacyHandler wrap(crc.interform.Handler h) {
    return new LegacyHandler(h);
  }

}
