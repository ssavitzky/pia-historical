////// Action.java: Active Node action handler interface
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;
import crc.dom.Node;
import crc.dom.Element;
import crc.dom.NodeList;
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

  /** Performs the action associated with the current Node in a given Context. 
   *
   *	The current node is the one obtainable from the Input via 
   *	<code>getNode</code>.  The action routine is free to make any 
   *	necessary assumptions about the type of the node, and call
   *	the appropriate access function to obtain it.  This is much
   *	more efficient than copying the input node into yet another
   *	cursor to operate on it. <p>
   *
   * @return integer indicating what additional action to take:
   *	<dl compact>
   *	   <dt> -1 <dd> Copy node without expansion.
   *	   <dt>  0 <dd> No additional action required.
   *	   <dt>  1 <dd> Copy node with expansion.
   */
  public int action(Input in, Context c, Output out);


  /** === probably want start, end actions for elements. === */

  /************************************************************************
  ** Processing Control Flags:
  ************************************************************************/

  /** If <code>true</code>, the content is expanded (processed). 
   *	Otherwise, a parse tree is built out of Token nodes.
   */
  public boolean expandContent();

  /** If <code>true</code>, begin constructing a parse tree even if the parent
   *	is not building one. If <code>expandContent</code> is true, a
   *	<em>processed</em> parse tree will be produced; otherwise, an
   *	<em>unprocessed</em> parse tree of Token nodes will be produced.
   */
  public boolean parseContent();

  /** If <code>true</code>, the element is passed to the output while being
   *	processed.  In general this will be <code>true</code> for passive
   *	elements and <code>false</code> for active ones.
   */
  public boolean passElement();

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

  /** Converts the Token to a String according to the given syntax. <p>
   *
   *	Note that the <code>syntax</code> code has a different meaning
   *	than it does in the Token itself: <em>in all cases</em> a Node
   *	is converted to a String with:
   *	<pre>
   *	     convertToString(t, -1) + 
   *	     convertToString(t,  0) +
   *	     convertToString(t,  1)
   *	</pre>
   */
  public String convertToString(ActiveNode n, int syntax);


}
