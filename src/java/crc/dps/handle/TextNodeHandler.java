////// TextNodeHandler.java: Text Node Handler implementation
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
import crc.dom.NodeType;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.util.Index;
import crc.dps.util.Copy;

import crc.ds.Table;

/**
 * Handler for active or passive Text nodes. <p>
 *
 *	<p>
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

public class TextNodeHandler extends AbstractHandler {

  /************************************************************************
  ** Standard handlers:
  ************************************************************************/

  /** The default TextNodeHandler.  
   *	Its <code>getActionForNode</code> method should be capable of
   *	returning the correct handler. 
   */
  public static final TextNodeHandler DEFAULT  = new TextNodeHandler(false);

  /** An TextNodeHandler for active entities. */
  public static final TextNodeHandler ACTIVE  = new TextNodeHandler(true);

  /** An TextNodeHandler for passive entities, which should never be 
   *	replaced by their values during processing.
   */
  public static final TextNodeHandler PASSIVE = new TextNodeHandler(false);

  /************************************************************************
  ** State:
  ************************************************************************/

  protected boolean active = false;

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Since we know what has to be done, it's cheaper to actually perform 
   *	the expansion if the text is active. 
   *
   *	=== Eventually should return a code that implies <code>getValue</code>
   */
  public int actionCode(Input in, Processor p) {
    if (active) {
      action(in, p, p.getOutput());
      return Action.COMPLETED;
    } else return Action.PUT_NODE;
  }

  /** This sort of action has no choice but to do the whole job.
   *	=== eventually this should use <code>getValue(node, context)</code>.
   */
  public void action(Input in, Context aContext, Output out) {
    ActiveText n = in.getActive().asText();
    if (!active) {
      out.putNode(n);
    } else {
      // === This is actually an error at the moment. ===
      aContext.message(-1, "Active text with no action defined.", 0, true); 
      out.putNode(n);
    }
  }

  /************************************************************************
  ** Parsing Operations:
  ************************************************************************/

  /** Called to determine the correct Handler for a given Token.
   *	The default action is to return <code>this</code>.
   */
  public Action getActionForNode(ActiveNode n) {
    return this;
  }

  /************************************************************************
  ** Presentation Operations:
  ************************************************************************/

  /** Converts the Node to a String. 
   *	=== eventually need to check for replacement on output ===
   */
  public String convertToString(ActiveNode n) {
    return n.startString() + n.contentString() + n.endString();
  }

  /** Converts the Node to a String. 
   *	=== eventually need to check for replacement on output ===
   */
  public String convertToString(ActiveNode n, int syntax) {
    return n.startString() + n.contentString() + n.endString();
  }


  /************************************************************************
  ** Construction:
  ************************************************************************/

  public TextNodeHandler() {}

  /** Construct a TextNodeHandler
   *
   * @param active <code>true</code> (default) if the text should ever be
   *	expanded, <code>false</code> otherwise.
   */
  public TextNodeHandler(boolean active) {
    this.active = active;
  }

}

