// BasicNamedNode.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dom;

import java.io.*;

/** 
 * Abstract base class for nodes with names. 
 */
public abstract class BasicNamedNode extends AbstractNode  {

  protected boolean isAssigned = false;
  public void setIsAssigned(boolean value) { isAssigned = value; }

  /** Has the node been explicitly assigned a value. */
  public boolean getIsAssigned() { return isAssigned; }

  /** Set the node's value.  If the value is <code>null</code>, 
   *	the value is ``un-assigned''.  Hence it is possible to 
   *	distinguish a null value (no value) from an empty one.
   */
  public void setValue(NodeList value) {
    if (value == null) {
      isAssigned = false;
      head = null; // clear old children
      return;
    } else {
      isAssigned = true;
    }
    long len = value.getLength();
    Node n = null;
    head = null; // clear old children
    for( long i = 0; i < len; i++ ){
      try{
	n = value.item( i );
	insertAtEnd((AbstractNode)n);
      }catch(NoSuchNodeException e){
      }
    }
  }

  public BasicNamedNode(String n) {
    setParent( null );
    setPrevious( null );
    setNext( null );
    setName( n );
  }

  public BasicNamedNode(String n, NodeList v) {
    setParent( null );
    setPrevious( null );
    setNext( null );
    setName( n );
    setValue( v );
  }

  public BasicNamedNode(Node myParent, String n){
    if( myParent != null )
      setParent( (AbstractNode)myParent );
    else
      setParent( null );
    setPrevious( null );
    setNext( null );
    setName( n );
  }

  /**
   * deep copy constructor.
   */
  public BasicNamedNode(BasicNamedNode attr){
    setPrevious( null );
    setNext( null );
    setName( attr.getName() );
    copyChildren( attr );
  }

  /**
   * Deep copy.
   */
  public Object clone(){
    BasicNamedNode n = (BasicNamedNode)super.clone();
    n.setName( getName() );
    n.copyChildren( this );
    return n;
  }

  /**
   * Set attribute name.
   * @param name attribute name.
   */
  public void setName(String name){ this.name = name; }

  /**
   * Returns the name of this attribute. 
   * @return attribute name.
   */
  public String getName(){ return name; }
  
  /* attribute name */
  protected String name;

}



