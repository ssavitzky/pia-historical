// Registered.java -- interface for objects registered in hashtables
// 	$Id$
// 	(c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.ds;

/** This is the interface for objects in the PIA that are ``registered'' 
 *	in a hash table or some other kind of registry object. <p>
 *
 *	The main reason to have such an interface is so that objects
 *	can be registered after being restored from an ObjectStream.<p>
 *
 * <b>Note:</b>
 *	It would be a <em>very bad</em> thing for a Registered object
 *	to contain a pointer back to its registry; if it did, every other
 *	object in the registry would get dragged in and out with this one.
 *	Don't even <em>think</em> about it!
 *
 */
public interface Registered {
  /** Register the object (in whatever registry is appropriate) */
  public void register();

  /** Remove the object from its registry */
  public void unregister();
}

