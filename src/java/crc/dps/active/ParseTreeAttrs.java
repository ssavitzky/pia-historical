// ParseTreeAttrs.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dps.active;

import crc.dom.*;
import java.io.*;


/**
 * Implementing Attribute list.
 *
 */
public class ParseTreeAttrs extends crc.dom.AbstractNamedList
	implements ActiveAttrList 
{

  public ParseTreeAttrs(){
  }

  /**
   * Copy another Attribute list.
   * Deep copy.
   */
  public ParseTreeAttrs(AttributeList l){
    if( l != null )
      initialize( l );
  }

  /**
   * Returns the attribute specified by name.
   * @return attribute if exists otherwise null.
   */ 
  public Attribute getAttribute(String name)
  {
    Attribute n = (Attribute)getItem( name );
    return ( n != null ) ? (Attribute)n : null;
  }

  /**
   * Maps the name to the specified attribute.
   * @param name Name associated with a given attribute.
   * @param attr Attribute associated with the name.
   * @Return The previous attribute of the specified name, or null if it did not
   * have one.
   */
  public Attribute setAttribute(String name, Attribute attr)
  { 
    if( name == null || attr == null ) return null;
    Attribute n = (Attribute)setItem( name, attr );
    return ( n != null ) ? (Attribute)n : null;
  }
  
  /**
   * Remove attribute specified by name.
   */
  public Attribute remove(String name) 
       throws NoSuchNodeException
  {
    try{
      Attribute n = (Attribute)removeItem( name );
      return ( n != null ) ? (Attribute)n : null;
    }catch(NoSuchNodeException e){
      throw e;
    }
  }
       
  /**
   * return attribute at the indicated index.
   */
  public Node item(long index)
       throws NoSuchNodeException
  {
    try{
      Node n = (Node)itemAt( index );
      return ( n != null ) ? (Node)n : null;
    }catch(NoSuchNodeException e){
      throw e;
    }
  }
  
  
  /**
   * Deep copy of all attributes in the given list.
   * If an attribute is foreign -- not in crc/dom, just
   * refers to it.
   */
  protected void initialize(AttributeList l){
    if( l == null ) return;

    for (long i = 0; i < l.getLength(); ++i) try {
      Attribute attr = (Attribute)l.item( i );
      if ( attr != null ) {
	if (attr instanceof ActiveNode) 
	  //setItem( attr.getName(), ((ActiveNode)attr).deepCopy());
	  setItem( attr.getName(), attr );
	else if( attr instanceof AbstractNode)
	  setItem( attr.getName(), ((AbstractNode)attr).clone() );
	else
	  // If it is a foreign attribute, do nothing but refers to it
	  setItem( attr.getName(), attr );
      }
    }catch(NoSuchNodeException e){
    }
  }

  /**
   * Size of the attribute list.
   */
  public long getLength(){ return getItemListLength();}

  /**
   * @return node enumerator
   */
  public NodeEnumerator getEnumerator(){
    return getListEnumerator();
  }

  /** 
   * @return string corresponding to content
   */
  public String toString() {
    String result = "";
    long length = getLength();
    for (long i = 0; i < length; ++i) try {
      Attribute attr = (Attribute)item(i);
      result += attr.toString();
      if (i < length - 1) result += " ";
    }catch(NoSuchNodeException e){
    }
    return result;
  }

  /************************************************************************
  ** Attribute convenience functions:
  ************************************************************************/

  /** Convenience function: get an Attribute by name and return its value. */
  public NodeList getAttributeValue(String name) {
    Attribute attr = getAttribute(name);
    return (attr == null)? null : attr.getValue();
  }

  /** Convenience function: get an Attribute by name and return its value
   *	as a String.
   */
  public String getAttributeString(String name) {
    Attribute attr = getAttribute(name);
    NodeList value = (attr == null)? null : attr.getValue();
    return (value == null)? null : value.toString();
  }
	 
  public void setAttributeValue(String aname, NodeList value) {
    Attribute attr = new ParseTreeAttribute(aname, value);
    attr.setSpecified(value != null);
    setItem( aname, attr );
  }

  public void setAttributeValue(String name, Node value) {
    setAttributeValue(name, new ArrayNodeList(value));
  }

  public void setAttributeValue(String name, String value) {
    setAttributeValue(name, new ParseTreeText(value));
  }

  /** Append a new attribute.
   *	Can be more efficient than setAttribute.
   */
  public void addAttribute(String aname, NodeList value) {
    Attribute attr = new ParseTreeAttribute(aname, value);
    attr.setSpecified(value != null);
    setItem( aname, attr );
  }

}



