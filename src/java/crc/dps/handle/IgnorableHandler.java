////// IgnorableHandler.java: Ignorable Node Handler implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.NodeType;
import crc.dom.DOMFactory;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.util.Index;
import crc.dps.util.Copy;

import crc.ds.Table;

/**
 * Handler for ignorable nodes.  
 *
 *	<p> The children, if any, are processed, but nothing is done with
 *	    the node itself.  
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

public class IgnorableHandler extends AbstractHandler {

  /************************************************************************
  ** Standard handlers:
  ************************************************************************/

  /** The default IgnorableHandler.  
   *	Its <code>getActionForNode</code> method should be capable of
   *	returning the correct handler. 
   */
  public static final IgnorableHandler DEFAULT  = new IgnorableHandler();

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Since we know what has to be done, it's cheaper to actually perform 
   *	the expansion if the ignorable is active. 
   *
   *	=== Eventually should return a code that implies <code>getValue</code>
   */
  public int actionCode(Input in, Processor p) {
    action(in, p, p.getOutput());
    return Action.COMPLETED;
  }

  /** Process the content, but do nothing with the node itself. */
  public void action(Input in, Context aContext, Output out) {
    if (in.hasChildren()) { 
      aContext.subProcess(in, out).processChildren();
    }
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public IgnorableHandler() {}

}

