// BasicElement.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * Implements w3c's dom Element interface.  This object
 * stores element tag name and attribute list
 */

package crc.dom;

import java.io.*;

public class BasicElement extends AbstractNode implements Element {
  public BasicElement(){
    setParent( null );
    setPrevious( null );
    setNext( null );
    setTagName( "" );
  }

  public BasicElement( BasicElement e ){
    setParent( null );
    setPrevious( null );
    setNext( null );
    setTagName( e.getTagName() );
    copyChildren( e );
    AttributeList l = e.getAttributes();
    if( l != null )
      setAttributes( new AttrList( e.getAttributes() ) );
  }

  public BasicElement(Node myParent){
    if( myParent != null )
      setParent( (AbstractNode)myParent );
    else
      setParent( null );
    setPrevious( null );
    setNext( null );
    setTagName( "" );
  }

  /**
   *
   * Deep copy of this element.
   */
  public Object clone(){
    BasicElement n = (BasicElement)super.clone();
    n.setTagName( getTagName() );
    n.copyChildren( this );
    AttributeList l = getAttributes();
    if( l != null )
      n.setAttributes( new AttrList( l ) );
    return n;
  }

  /**
   * implementing Element methods
   */
  public int getNodeType() { return NodeType.ELEMENT; }

  /**
   * Set the element's name.
   * @param tagName Name of this element.
   */
  public void setTagName(String tagName){ this.tagName = tagName; }

  /**
   * This method returns the string that is the element's name.
   * Note that this is case-preserving.
   * @return Tag name.
   */
  public String getTagName(){ return tagName; }
  
  /**
   * Set a list of attributes for this element.
   * @param attributes Attribute list for this element.
   */
  public void setAttributes(AttributeList attributes)
  {
    attrList = attributes;
  }

  /**
   * @return return all attributes.
   */
  public AttributeList getAttributes(){ return attrList; }
  

  /**
   * Adds a new attribute/value pair to an Element node object. If an attribute by
   * that name is already present in the element, it's value is changed to be that
   * of the Attribute instance.
   * @param newAttr attribute/value pair.
   */
  public void setAttribute(Attribute newAttr)
  {
    //Report.debug(this, "setAttribute");
    if( newAttr == null ) return;
    //Report.debug(this, newAttr.getName());
    attrList.setAttribute( newAttr.getName(), newAttr );
  }

  /**
   *Produces an enumerator which iterates over all of the Element nodes that are
   *descendants of the current node whose tagName matches the given name. The
   *iteration order is a depth first enumeration of the elements as they occurred
   *in the original document. 
   */
  public NodeEnumerator getElementsByTagName(String name)
  {
    //Report.debug(this, "Get elements by tag name...");
    ArrayNodeList result = new ArrayNodeList();
    
    findAll( name, this, result );
    //Report.debug(this, "result size-->"+Integer.toString( (int)result.getLength() ));

    return result.getEnumerator();
    
  }


  /**
   * return start tag + content string + end tag
   * 
   */ 
  public String toString(){
    return startString() + "\n" + contentString() + "\n" + endString();
  }


  /**
   * printChildren depth first.
   * This should be call at the root node.
   */
  public void printChildren(String indent){
    AbstractNode child = null;
    
    Report.debug(indent+ "<" + toString() + printAttributes() + ">");
    if( hasChildren() ){
       NodeEnumerator enum = getChildren().getEnumerator();
       child =  (AbstractNode)enum.getFirst();
       while( child != null ) {
	 child.printChildren( indent + "    ");
	 child = (AbstractNode)enum.getNext();
       }
    }
    Report.debug(indent + "</" + toString()+ ">");
  }


  /**
   * Return a string of the form "<" <code>getTagName()</code> ">".
   *
   */
  public String startString(){
    return "<" + getTagName() + printAttributes() + ">";;
  }

  /** Return the String equivalent of all children concatenated
   *  in string form.
   */
  public String contentString(){
    StringBuffer sb = new StringBuffer();
    long len = 0;

    ChildNodeList cl = (ChildNodeList)getChildren();
    if( cl == null || (len = cl.getLength()) == 0 ) return new String( sb );

    try{
      Node a = null;
      for(int i = 0;  i < len; i++){ 
	a = cl.item( i );
	sb.append( a.toString() );
	sb.append("\n");
      }
    }catch(Exception ee){
    }
    return new String( sb );
  }




  /** Return a string of the form "</" <code>getTagName()</code> ">".
   *	
   */
  public String endString(){
    return "</" + getTagName() + ">";
  }

  protected String printAttributes(){
    StringBuffer sb = new StringBuffer();
    long len = 0;

    AttributeList l = getAttributes();
    if( l == null || (len = l.getLength())==0 ) return new String( sb );

    try{
      Node a = null;
      for(int i = 0;  i < len; i++){ 
	a = l.item( i );
	if( a instanceof AbstractNode ){
	  sb.append(" " + ((Attribute)a).getName() + "=" +a.toString());
	}
      }
    }catch(Exception ee){
    }
    return new String( sb );
  }

  
  protected void findAll( String tag, Element elem, EditableNodeList result){
    Element child = null;
    
    if( elem.hasChildren() ){
      NodeEnumerator enum = elem.getChildren().getEnumerator();
      
      child =  (Element)enum.getFirst();
      while( child != null ) {
	//Report.debug(this, "child name-->"+ child.getTagName());
	if( child.getTagName().equalsIgnoreCase( tag ) ){
	  try{
	    result.insert( result.getLength(), child );
	  }catch(NoSuchNodeException err){
	  }
	}
	findAll( tag, child, result );
	child = (Element)enum.getNext();
      }
      
    }
  }


  /* tag name */
  protected String tagName;

  /* attribute list */
  protected AttributeList attrList = new AttrList();

}




