////// EntityHandler.java: Entity Node Handler implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.NodeType;
import crc.dom.DOMFactory;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.aux.Index;
import crc.dps.aux.Copy;

import crc.ds.Table;

/**
 * Handler for active or passive Entity nodes. <p>
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

public class EntityHandler extends AbstractHandler {

  /************************************************************************
  ** Standard handlers:
  ************************************************************************/

  /** The default EntityHandler.  
   *	Its <code>getActionForNode</code> method should be capable of
   *	returning the correct handler. 
   */
  public static final EntityHandler DEFAULT  = new EntityHandler(true, 0);

  /** An EntityHandler for active, non-indexed entities. */
  public static final EntityHandler ACTIVE  = new EntityHandler(true, -1);

  /** An EntityHandler for active, indexed entities. */
  public static final EntityHandler INDEXED = new EntityHandler(true, 1);

  /** An EntityHandler for passive entities, which should never be 
   *	replaced by their values during processing.
   */
  public static final EntityHandler PASSIVE = new EntityHandler(false, 0);

  /************************************************************************
  ** State:
  ************************************************************************/

  protected boolean active = true;
  protected int indexed = 0;	// 1: index; -1: simple.

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Since we know what has to be done, it's cheaper to actually perform 
   *	the expansion if the entity is active. 
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
    ActiveEntity n = in.getActive().asEntity();
    if (!active) {
      out.putNode(n);
      return;
    }
    String name = n.getName();
    NodeList value;
    if (indexed > 0) value = Index.getIndexValue(aContext, name);
    else if (indexed < 0) value = aContext.getEntityValue(name, false);
    else if (name.indexOf('.') >= 0)
      value = Index.getIndexValue(aContext, name);
    else 
      value = aContext.getEntityValue(name, false);

    // aContext.debug("&" + name + "; => " + value + "\n");

    if (value == null) {
      out.putNode(n);
    } else {
      Copy.copyNodes(value, out);
    }
  }

  /************************************************************************
  ** Parsing Operations:
  ************************************************************************/

  /** Called to determine the correct Handler for a given Token.
   *	Does dispatching on the name to determine which of the canned
   *	handlers to return.
   *
   *	Eventually this needs to check for character entities and return
   *	PASSIVE for them.
   */
  public Action getActionForNode(ActiveNode n) {
    ActiveEntity ent = n.asEntity();
    String name = ent.getName();
    return (name.indexOf('.') >= 0)? INDEXED : ACTIVE;
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

  public EntityHandler() {}

  /** Construct an EntityHandler
   *
   * @param active <code>true</code> (default) if the entity should ever be
   *	expanded, <code>false</code> otherwise.
   * @param indexed <code>0</code> (default) if we have to check the name for
   *	periods at run-time; <code>1</code> if the name is an index; 
   *	<code>-1</code> if the name is not an index.
   */
  public EntityHandler(boolean active, int indexed) {
    this.active  = active;
    this.indexed = indexed;
  }

}

