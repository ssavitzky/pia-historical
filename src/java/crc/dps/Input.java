////// Input.java: Depth-first enumerator for parse trees
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;

import crc.dom.Node;
import crc.dom.Attribute;
import crc.dom.Element;

/**
 * The interface for depth-first enumeration of a document or parse tree.
 *
 *	The Processor maintains a stack of Input objects from which
 *	input is obtained as needed.  An Input may have to be
 *	provided with a reference back to the Processor it is sending
 *	input to, in case it needs additional information (e.g. for
 *	parsing).  This is done during initialization and is not part
 *	of the interface. <p>
 *
 *	Note that although an Input superficially resembles the DOM's
 *	TreeIterator, the two interfaces are quite different.  A 
 *	TreeIterator is able to traverse a Document tree in any direction,
 *	whereas an Input is unidirectional and so is able to handle
 *	trees that are generated on-the-fly and that need not be retained.  <p>
 *	
 *	In addition, an Input has machinery for handling attribute lists,
 *	allowing attributes to be treated similarly to children. <p>
 *
 *	With the use of <code>retainTree</code>, an Input parsing a document
 *	on the fly can also be forced to generate a complete Document (parse
 *	tree) as a side effect. <p>
 *
 *	Note that an Input which is being used as an Enumeration may
 *	have to ``look ahead'' to ensure that <code>hasMoreElements</code>
 *	can return an accurate result. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 * 
 * @see crc.dps.Token
 * @see crc.dps.Processor
 * @see java.util.Enumeration
 * @see java.util.NoSuchElementException */

public interface Input {

  /************************************************************************
  ** State Accessors:
  ************************************************************************/

  /** Returns the current Node. <p>
   *
   * @return  <code>null</code> if and only if no more nodes are
   *	available at this level. 
   */
  public Node getNode();

  /************************************************************************
  ** Navigation Operations:
  ************************************************************************/

  /** Returns the next node from this source and makes it current.  
   *	May descend or ascend levels.  This can be detected by tracking
   *	the depth with <code>getDepth()</code>. <p>
   *
   * @return  <code>null</code> if and only if no more nodes are
   *	available from this source. 
   */
  public Node toNextNode();

  /** Returns the next sibling of the current node and makes it current.  
   *	May require traversing all of the (old) current node if its
   *	children have not yet been seen. <p>
   *
   * @return  <code>null</code> if and only if no more nodes are
   *	available at this level. 
   */
  public Node toNextSibling();

  /** Returns the parent of the current Node.
   *	After calling <code>toParent</code>, <code>toNextNode</code> will
   *	return the parent's next sibling.
   */
  public Node toParent();

  /** Returns the parent Element of the current attribute list.
   */
  public Element toParentElement();
 
  /** Returns the first child of the current Node. 
   *	A subsequent call on <code>toNextNode</code> will return the 
   *	second child, if any.
   */
  public Node toFirstChild();

  /** Returns the first attribute of the current Node. 
   *	A subsequent call on <code>toNextNode</code> or 
   *	<code>toNextAttribute</code> will return the second attribute.
   */
  public Attribute toFirstAttribute();

  /** Returns the next attribute of the current Node. 
   *	A subsequent call on <code>toNextNode</code> or 
   *	<code>toNextAttribute</code> will return the second attribute.
   */
  public Attribute toNextAttribute();


  /** Returns <code>true</code> if and only if the current node has no
   *	previous siblings.  This occurs only with a node returned by
   *	<code>toFirstChild</code>.
   */
  public boolean atFirst();

  /** Returns <code>true</code> if and only if the current node has no next
   *	sibling.  A subsequent call on <code>toNextNode</code> will return
   *	<code>null</code>
   */
  public boolean atLast();

  /** Test whether the current node is expected to have children.
   *
   *	Note that this is <em>not</em> the same as calling
   *	<code>hasChildren</code> on the current node!  The parse tree may be
   *	being constructed on the fly, and nodes already seen may no longer
   *	be accessible.  What <em>is</em> true is that <em>if</em> calling
   *	<code>hasChildren</code> on a node returns true, all of its
   *	children will have been seen. <p>
   *
   * @return <code>true</code> if the current node has children.
   */
  public boolean hasChildren();


  /** Test whether the current node has attributes.
   *
   * @return <code>true</code> if the current node has attributes.
   */
  public boolean hasAttributes();


  /** Returns <code>true</code> if and only if <code>getParent</code> will
   *	return <code>null</code>. <p>
   *
   *	Note that <code>atTop</code> may be <code>true</code> even if the
   *	current node actually has a parent, if the Input is iterating over
   *	a subtree or fragment of a document. <p>
   */
  public boolean atTop();

  /************************************************************************
  ** Processing Operations:
  ************************************************************************/

  /** Returns the action handler, if known, for the current node. 
   */
  public Action getAction();

  /** Ensures that all descendents of the current node will be appended to
   *	it as they are traversed.  
   */
  public void retainTree();
  
  /** Ensures that all descendents of the current node have been seen
   *	and appended to it.  May be expensive.  
   */
  public Node getTree();


}
