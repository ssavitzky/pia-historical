////// SkippableHandler.java: Skippable Node Handler implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.NodeType;
import crc.dom.DOMFactory;

import crc.dps.*;
import crc.dps.active.*;

import crc.ds.Table;

/**
 * Handler for skippable nodes. <p>
 *
 *	No processing is done for the node or its children -- they 
 *	just disappear. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.handle.GenericHandler
 * @see crc.dps.Processor
 * @see crc.dps.Tagset
 * @see crc.dps.BasicTagset
 * @see crc.dps.Input 
 * @see crc.dps.Output
 * @see crc.dom.Node
 */

public class SkippableHandler extends AbstractHandler {

  /************************************************************************
  ** Standard handlers:
  ************************************************************************/

  /** The default SkippableHandler.  
   *	Its <code>getActionForNode</code> method should be capable of
   *	returning the correct handler. 
   */
  public static final SkippableHandler DEFAULT  = new SkippableHandler();

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Since we know what has to be done, it's cheaper to actually perform 
   *	the expansion if the skippable is active. 
   *
   *	=== Eventually should return a code that implies <code>getValue</code>
   */
  public int actionCode(Input in, Processor p) {
    action(in, p, p.getOutput());
    return Action.COMPLETED;
  }

  /** Process the content, but do nothing with the node itself. */
  public void action(Input in, Context aContext, Output out) {
    skipNode(null, in, out);
  }

  static final void skipNode(Node n, Input in, Output out) {
    if (n == null) n = in.getNode();
    if (in.hasChildren() && ! n.hasChildren()) {
      skipChildren(in, out);
    }
  }

  /** Copy the children of the input's current Node
   */
  static final void skipChildren(Input in, Output out) {
    for (Node n = in.toFirstChild(); n != null; n = in.toNextSibling()) {
      skipNode(n, in, out);
    }
    in.toParent();
  }


  /************************************************************************
  ** Construction:
  ************************************************************************/

  public SkippableHandler() {}

}

