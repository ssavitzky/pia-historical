// DOMFactory.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;

/**
 * The methods on the DOMFactory interface allow DOM clients to create new DOM
 * objects.
 *
 * An application developer who needed to create an entire document object
 * model programmatically would use the methods on a DOMFactory object to
 * build the individual objects that comprise the object model, and use the
 * operations on the objects themselves to connect the objects into an overall
 * document object model.
 */
public interface DOMFactory {

  /**
   *  Create and return a new empty Document object. 
   */
  Document          createDocument();

  /**
   * Create and return a new DocumentContext. 
   */
  DocumentContext   createDocumentContext();

  /**
   * Create an element based on the tagName. Note that the instance returned
   * may implement an interface derived from Element. The attributes parameter
   * can be null if no attributes are specified for the new Element.
   */
  Element           createElement(String tagName, AttributeList attributes);

  /**
   * Create a Text node given the specified string. 
   */
  Text              createTextNode(String data);

  /**
   * Create a Comment node given the specified string. 
   */
  Comment           createComment(String data);

  /**
   * Create a PI node with the specified name and data string. 
   */
  PI                createPI(String name, String data);

  /**
   * Create an Attribute of the given name and specified value. Note that the
   *  Attribute instance can then be set on an Element using the setAttribute 
   *  method.
   */
  Attribute         createAttribute(String name, NodeList value);

};
