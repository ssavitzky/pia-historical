////// Action.java: Active Node action handler interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;
import crc.dom.Node;
import crc.dom.Element;
import crc.dom.NodeList;
import crc.dom.AttributeList;
import crc.dom.DOMFactory;

import crc.dps.active.*;

/**
 * The interface for a Node's ``Action'' (semantic handler). 
 *
 *	A Node's Action provides all of the semantic actions required for
 *	processing (including presenting) a Node.  <p>
 *
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Processor
 * @see crc.dps.Input 
 * @see crc.dom.Node */

public interface Action {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Returns an action code to a Processor.
   *
   *	The current node is the one obtainable from the Input via 
   *	<code>getNode</code>.  The action routine is free to make any 
   *	necessary assumptions about the type of the node, and call
   *	the appropriate access function to obtain it.  This is much
   *	more efficient than copying the input node into yet another
   *	cursor to operate on it. <p>
   *
   * @return integer ``action code'' indicating what additional action to take:
   */
  public int actionCode(Input in, Processor p);

  /** Action code: <code>actionCode</code> has completed the action. */
  public static final int COMPLETED   = -1;

  /** Action code: copy the node and its contents. */
  public static final int COPY_NODE   =  0;

  /** Action code: expand entities in the node's attributes; perform
   *	processing actions in its content. 
   */
  public static final int EXPAND_NODE =  1;

  /** Action code: expand entities in the node's attributes; blindly copy
   *	its content. 
   */
  public static final int EXPAND_ATTS =  2;

  /** Action code: put the node on the output.  Its content has either 
   *	already been parsed, or (more likely) does not exist. 
   */
  public static final int PUT_NODE    =  3;
  public static final int PUT_VALUE   =  4;

  public static final String actionNames[] = { 
    "COMPLETED", "COPY_NODE", "EXPAND_NODE", "EXPAND_ATTS",
    "PUT_NODE", "PUT_VALUE" };

  /** Performs the action associated with the current Node in a given Context.
   *	Calling this instead of calling <code>actionCode</code> should always
   *	produce correct results. 
   */
  public void action(Input in, Context aContext, Output out);

  /** Returns the value associated with the given Node in the given context.
   *	The node need not be the current one, but it must be the one to which
   *	this Action applies.
   *
   * === getValue is currently unused; it is expected that it will 
   *	 eventually be used for expanding, e.g., Entities.
   */
  public NodeList getValue(Node aNode, Context aContext);

  /** Returns the value associated with the given name in a given Node and
   *	context.  The node need not be the current one, but it must be the one
   *	to which this Action applies.
   */
  public NodeList getValue(String aName, Node aNode, Context aContext);

  /************************************************************************
  ** Processing Control Flags:
  ************************************************************************/

  /** If <code>true</code>, the content is expanded (processed). 
   *	Otherwise, it is simply copied.
   */
  public boolean expandContent();

  /************************************************************************
  ** Presentation Operations:
  ************************************************************************/

  /** Converts the Node to a String. <p>
   *
   *	Note that a Nodewould be quite capable of doing this using the 
   *	standard defaults; passing it off to the Handler means that
   *	we can give the same Document different physical representations
   *	if necessary.<p>
   *
   *	<b>Implementation Note:</b> It is important that the Handler's
   *	<code>convertToString</code> method <em>not</em> call the Token's
   *	<code>toString</code>, method, since that will normally call the
   *	Handler and produce an infinite recursion.  Use
   *	<code>basicToString</code> instead. <p>
   *
   * === Not clear where entity, url encoding and decoding is done. ===
   */
  public String convertToString(ActiveNode n);

  /** Converts the Node to a String according to the given syntax. <p>
   *
   *  The string corresponding to a node is:
   *	<pre>
   *	     convertToString(t, -1) + 
   *	     convertToString(t,  0) +
   *	     convertToString(t,  1)
   *	</pre>
   */
  public String convertToString(ActiveNode n, int syntax);

}
