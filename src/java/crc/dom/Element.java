// Element.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

/**
 * By far the vast majority (apart from text) of node types that authors will
 * generally encounter when traversing a document will be Element nodes. These objects
 * represent both the element itself, as well as any contained nodes.
 *
 * For example (in XML):
 *
 * <elementExample id="demo">
 *   <subelement1/>
 *    <subelement2>
 *        <subsubelement/>
 *    </subelement2>
 * </elementExample>
 *
 * When represented using DOM, the top node would be "elementExample", which contains
 * two child Element nodes (and some space), one for "subelement1" and one for
 * "subelement2". "subelement1" contains no child nodes of its own. 
 */


public interface Element extends Node {

  void setTagName(String tagName);

  /**
   *This method returns the string that is the element's name. For example, in: 
   *
   *    <elementExample id="demo">
   *       ...
   *    </elementExample>
   *
   *    This would have the value "elementExample". Note that this is case-preserving, as
   *    are all of the operations of the DOM. See Name case in the DOM for a
   *    description of why the DOM preserves case. 
   */
  String getTagName();

  /**
   * AttributeList attributes 
   * The attributes for this element. In the elementExample example above, the
   * attributes list would consist of the id attribute, as well as any attributes
   * which were defined by the document type definition for this element which have
   * default values. 
   */
  void setAttributes(AttributeList attributes);
  AttributeList getAttributes();

  /**
   * Adds a new attribute/value pair to an Element node object. If an attribute by
   * that name is already present in the element, it's value is changed to be that
   * of the Attribute instance. 
   */
  void setAttribute(Attribute newAttr);

  /**
   * Produces an enumerator which iterates over all of the Element nodes that are
   * descendants of the current node whose tagName matches the given name. The
   * iteration order is a depth first enumeration of the elements as they occurred
   * in the original document. 
   */
  NodeEnumerator getElementsByTagName(String name);

};
