// DOM.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;


/**
 * The "DOM" interface provides a number of methods for performing operations that are
 * independent of any particular instance of the document object model. The only
 * operations currently supported is to retrieve the factory object. It is expected,
 * however, that other operations such as querying for the version number of a
 * particular DOM implementation, or asking about the versions of HTML or XML
 * supported by a particular DOM implementation would also be present on this
 * interface. Although IDL does not provide a mechanism for expressing the concept,
 * the methods supplied by the DOM interface will be implemented as "static", or
 * instance independent, methods. This means that a client application using the DOM
 * does not have to locate a specific instance of the DOM object; rather, the methods
 * are will be available directly on the DOM class itself and so are directly
 * accessible from any execution context. 
 *
 */
public interface DOM {

  /**
   * Returns an object that implements the DOMFactory interface. Note that by
   * providing an accessor function for retrieving the factory object, DOM
   * implementations are empowered to return different factory instances under
   * different conditions. 
   *
   * Note that in the future it is expected that there will be additional static
   * methods on the DOM itself to allow for specification of which factory object to
   * be returned. 
   */
  DOMFactory  getFactory();
};
