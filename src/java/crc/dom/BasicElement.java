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

  public void setTagName(String tagName){ this.tagName = tagName; }
  public String getTagName(){ return tagName; }
  
  public void setAttributes(AttributeList attributes)
  {
    attrList = attributes;
  }
  public AttributeList getAttributes(){ return attrList; }
  
  public void setAttribute(Attribute newAttr)
  {
    Report.debug(this, "setAttribute");
    if( newAttr == null ) return;
    Report.debug(this, newAttr.getName());
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
    Report.debug(this, "Get elements by tag name...");
    ArrayNodeList result = new ArrayNodeList();
    
    findAll( name, this, result );
    Report.debug(this, "result size-->"+Integer.toString( (int)result.getLength() ));

    return result.getEnumerator();
    
  }
    
  protected void findAll( String tag, Element elem, EditableNodeList result){
    Element child = null;
    
    if( elem.hasChildren() ){
      NodeEnumerator enum = elem.getChildren().getEnumerator();
      
      child =  (Element)enum.getFirst();
      while( child != null ) {
	Report.debug(this, "child name-->"+ child.getTagName());
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




