// AttributeList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

/**
 * Represents a collection of Attribute objects, indexed by attribute name. 
 */

public interface AttributeList extends NodeList {

  /**
   * Retrieve an Attribute instance from the list by its name. 
   *
   * @param name name of attribute.
   * @return attribute instance; <code>null</code> if no attribute with the
   *	given name is present.
   */
  Attribute getAttribute(String name);

  /** Add a new attribute to the end of the list and associate it with the
   *	given name. 
   *
   *	If the name already exists, the previous Attribute object is replaced,
   *	and returned. If no object of the same name exists, null is returned,
   *	and the named Attribute is added to the end of the AttributeList
   *	object; that is, it is accessible via the item method using the index
   *	one less than the value returned by getLength().
   */
  Attribute setAttribute(String name, Attribute attr);

  /**
   * Removes the Attribute instance named name from the list and returns it. 
   *
   *	If the name provided does not exist, the NoSuchAttributeException is
   *	thrown.
   */
  Attribute remove(String name) 
    throws NoSuchNodeException;

  /**
   * Returns the (zero-based) indexth Attribute item in the collection. 
   *
   *	If index is greater than or equal to the number of nodes in the list,
   *	a NoSuchAttributeException is thrown.
   */
  Node item(long index)
    throws NoSuchNodeException;

  /**
   *Returns the number of Attributes in the AttributeList instance. 
   */
  long getLength();

};
