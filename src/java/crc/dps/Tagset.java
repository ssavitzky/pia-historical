////// Tagset.java: Node Handler Lookup Table interface
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


package crc.dps;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.AttributeList;

import java.util.Enumeration;

import crc.dps.active.*;

/**
 * The interface for a Tagset -- a lookup table for syntax. <p>
 *
 *	A Node's Handler provides all of the necessary syntactic and semantic
 *	information required for parsing, processing, and presenting the Node.
 *	A Tagset can be regarded as either a lookup table for syntactic
 *	information, or as a node factory for the documents so described.  <p>
 *
 *	Note that a Tagset can be used to construct either generic DOM Node's,
 *	or DPS ActiveNode's.  A Parser is free to use either.  In the current
 *	implementation, however, <em>all</em> documents produced in the DPS
 *	are Active parse trees, so the default is for the ``generic'' nodes
 *	produced by the standard tagsets are identical to the active ones.
 *	The only difference is the return type.  The specialized Output
 *	ToDocument should be used for conversion.  <p>
 *
 *	Note that this interface says little about the implementation.
 *	It is expected, however, that any practical implementation of
 *	Tagset will also be a Node, so that tagsets can be read and
 *	stored as documents or (better) DTD's.  <p>
 *
 * === 	need encoders/decoders for character entities, URLs, etc.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Processor
 * @see crc.dps.Active
 * @see crc.dps.active.ActiveNode
 * @see crc.dps.Input
 * @see crc.dps.Output
 * @see crc.dps.output.ToDocument
 * @see crc.dom.Node */

public interface Tagset  {

  /************************************************************************
  ** Context:
  ************************************************************************/

  /** Returns a Tagset which will handle defaults. 
   *	Note that it may or may not be used by the various lookup
   *	operations; it will usually be more efficient to duplicate the
   *	entries of the context.  However, lightweight implementations
   *	that define only a small number of tags may use it.
   */
  public Tagset getContext();

  /** Include definitions from a given tagset. 
   */
  public void include(Tagset ts);

  /** Include definitions, defaults, and other parameters from a given tagset. 
   */
  public void setParent(Tagset ts);

  /** Get the tagset's name. */
  public String getName();


  /************************************************************************
  ** Entity Bindings:
  ************************************************************************/

  /** Return a namespace with a given name.  Returns the entity table 
   *	associated with the tagset (possibly in the context chain) having 
   *	the given name.
   */
  public Namespace getNamespace(String name);

  /** Obtain the current Entity bindings. */
  public EntityTable getEntities();

  /** Set the current Entity bindings. */
  public void setEntities(EntityTable bindings);

  /** Get the binding (Entity node) of an entity, given its name. 
   * @return <code>null</code> if the entity is undefined.
   */
  public ActiveEntity getEntityBinding(String name);

  /** Bind an Entity.
   */
  public void setEntityBinding(String name, ActiveEntity ent);


  /************************************************************************
  ** Lookup Operations:
  ************************************************************************/

  /** Called during parsing to return a suitable Handler for a given
   *	tagname.  Returns <code>getHandlerForType(NodeType.ELEMENT)</code> 
   *	as a default.
   */
  public Handler getHandlerForTag(String tagname);

  public void setHandlerForTag(String tagname, Handler newHandler);

  /** Called during parsing to return a suitable Handler for a new Text
   *	node.  It is up to the Parser to determine whether the text consists
   *	only of whitespace.
   */
  public Handler getHandlerForText(boolean isWhitespace);

  /** Called during parsing to return a suitable Handler for a new
   *	entity reference.
   */
  public Handler getHandlerForEntity(String entityName);

  /** Called during parsing to return a suitable Token for a generic
   *	Node, given the Node's type.
   */
  public Handler getHandlerForType(int nodeType);

  public void setHandlerForType(int nodeType, Handler newHandler);

  /** Called during parsing to determine whether a Handler exists for a
   *	given attribute name.  Returns <code>null</code> as a default.
   */
  public Handler getHandlerForAttr(String attrName);

  public void setHandlerForAttr(String attrName, Handler newHandler);

  /************************************************************************
  ** Locking status:
  ************************************************************************/

   /** Test whether the Tagset is ``locked.''
   *
   *	A locked Tagset must be extended by creating a new Tagset with
   *	the locked Tagset as its context.
   */
  public boolean isLocked();

  /** Change the lock status. */
  public void setIsLocked(boolean value);

  /************************************************************************
  ** Parsing Operations:
  ************************************************************************/

  /** Return a Parser suitable for parsing a character stream
   *	according to the Tagset.  The Parser may (and probably will)
   *	know the Tagset's actual implementation class, so it can use
   *	specialized operations not described in the Tagset interface.
   */
  public Parser createParser();

  /** Creates an ActiveElement; otherwise identical to CreateElement. 
   *	<p>
   *
   *	This method is called at parse time to create a node in the parse
   *	tree under construction.  Normally it simply returns an instance
   *	of <code>ParseTreeElement</code>, but may return an instance of 
   *	a subclass instead. <p>
   *
   * @see crc.dps.active.ParseTreeElement
   */
  public ActiveElement createActiveElement(String tagname,
					   AttributeList attributes,
					   boolean hasEmptyDelim);

  /** Creates an ActiveNode of arbitrary type with (optional) data.
   */
  public ActiveNode createActiveNode(int nodeType, String data);

  /** Creates an ActiveNode of arbitrary type with name and (optional) data.
   */
  public ActiveNode createActiveNode(int nodeType, String name, String data);

  /** Creates an ActivePI node with name and data.
   */
  public ActivePI createActivePI(String name, String data);

  /** Creates an ActiveAttribute node with name and value.
   */
  public ActiveAttribute createActiveAttribute(String name, NodeList value);

  /** Creates an ActiveEntity node with name and value.
   */
  public ActiveEntity createActiveEntity(String name, NodeList value);

  /** Creates an ActivePI node.
   */
  public ActiveComment createActiveComment(String data);

  /** Creates an ActiveText node.  Otherwise identical to createText.
   */
  public ActiveText createActiveText(String text);

  /** Creates an ActiveText node.  Otherwise identical to createText.
   */
  public ActiveText createActiveText(String text,
				     boolean isIgnorableWhitespace);

  /** Creates an ActiveText node.  Includes the <code>isWhitespace</code>
   *	flag, which would otherwise have to be tested for.
   */
  public ActiveText createActiveText(String text,
				     boolean isIgnorableWhitespace,
				     boolean isWhitespace);


  /************************************************************************
  ** Syntactic Information:
  ************************************************************************/

  /** Does this Tagset treat uppercase and lowercase tagnames the same?
   */
  public boolean caseFoldTagnames();

  /** Convert a tagname to the cannonical case. */
  public String cannonizeTagname(String name);

  /** Does this Tagset treat uppercase and lowercase attribute names 
   *	the same?
   */
  public boolean caseFoldAttributes();

  /** Set the case folding parameters */
  public void setCaseFolding(boolean forTags, boolean forAttrs);


  /** Convert an attribute name to the cannonical case. */
  public String cannonizeAttribute(String name);


  /** Return the tag of the paragraph element, implicitly started
   *	when text appears inside an element that should not contain it. 
   */
  public String paragraphElementTag();

  /** Return the Tagset's DTD.  In some implementations this may be
   *	the Tagset itself.
   */
  //  public DocumentType getDocumentType();


  /************************************************************************
  ** Documentation Operations:
  ************************************************************************/

  /** Returns an Enumeration of the element names defined in this
   *	table.  Note that there is no good way to get the handlers for
   *	Node types other than Element unless the implementation gives
   *	them distinctive, generated names.
   */
  public Enumeration handlerNames();

  /** Returns an Enumeration of the element names defined in this table and
   *	its context, in order of definition (most recent last). */
  public Enumeration allHandlerNames();

  /************************************************************************
  ** Convenience Functions:
  ************************************************************************/

  /** Convenience function to define a tag with a given syntax. */
  public Handler defTag(String tag, String notIn, String parents, int syntax,
			String cname, NodeList content);

}
