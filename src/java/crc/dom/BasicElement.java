// BasicElement.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


package crc.dom;

import java.io.*;

/**
 * Implements w3c's dom Element interface.  This object
 * stores element tag name and attribute list
 */
public class BasicElement extends AbstractNode implements Element {

  /************************************************************************
  ** Construction:
  ************************************************************************/

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
      setAttributes( new AttrList( l ) );
  }

  public BasicElement( BasicElement e, AttributeList atts ){
    setParent( null );
    setPrevious( null );
    setNext( null );
    setTagName( e.getTagName() );
    copyChildren( e );
    if( atts != null )
      setAttributes( new AttrList( atts ) );
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

  /************************************************************************
  ** Element interface:
  ************************************************************************/

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
   * @return all attributes.
   */
  public AttributeList getAttributes(){ return attrList; }
  

  /**
   * Adds a new attribute/value pair to an Element node object. 
   *
   * If an attribute by that name is already present in the element, it's
   * value is changed to be that of the Attribute instance. 
   *
   * @param newAttr attribute/value pair.
   */
  public void setAttribute(Attribute newAttr)
  {
    //Report.debug(this, "setAttribute");
    if( newAttr == null ) return;
    if (attrList == null) attrList = new AttrList();
    //Report.debug(this, newAttr.getName());
    attrList.setAttribute( newAttr.getName(), newAttr );
  }

  /** 
   * Produces an enumerator which iterates over all of the Element nodes that
   * are descendants of the current node whose tagName matches the given
   * name. The iteration order is a depth first enumeration of the elements as
   * they occurred in the original document.
   */
  public NodeEnumerator getElementsByTagName(String name)
  {
    //Report.debug(this, "Get elements by tag name...");
    ArrayNodeList result = new ArrayNodeList();
    
    findAll( name, this, result );
    //Report.debug(this, "result size-->"+Integer.toString( (int)result.getLength() ));

    return result.getEnumerator();
    
  }


  /************************************************************************
  ** Additional Semantic Operations:
  ************************************************************************/

  /** Convenience function: get an Attribute by name. */
  public Attribute getAttribute(String name) {
    if (attrList == null || name == null) return null;
    return attrList.getAttribute(name);
  }

  /** Convenience function: get an Attribute by name and return its value. */
  public NodeList getAttributeValue(String name) {
    Attribute attr = getAttribute(name);
    return (attr == null)? null : attr.getValue();
  }

  /** Convenience function: get an Attribute by name and return its value
   *	as a String.
   */
  public String getAttributeString(String name) {
    NodeList v = getAttributeValue(name);
    return (v == null) ? null : v.toString();
  }

  /** Convenience function: Set an attribute's value to a NodeList. */
  public void setAttribute(String name, NodeList value) {
    Attribute attr = getAttribute(name);
    if (attr != null) attr.setValue(value);
    else setAttribute(new BasicAttribute(name, value));
  }

  /** Convenience function: Set an attribute's value to a Node. */
  public void setAttribute(String name, Node value) {
    if (value.getNodeType() == NodeType.ATTRIBUTE) {
      Attribute attr = (Attribute)value;
      attr.setName(name);
      setAttribute((Attribute)value);
    } else
      setAttribute(name, new ArrayNodeList(value));
  }

  /** Convenience function: Set an attribute's value to a String. */
  public void setAttribute(String name, String value) {
    setAttribute(name, new BasicText(value));
  }

  /************************************************************************
  ** Additional Syntactic Operations:
  ************************************************************************/

  protected boolean isEmptyElement = false;
  protected boolean hasEmptyDelim = false;
  protected boolean implicitEnd = false;

  /** Returns <code>true</code> if the Element has no content. 
   *
   *	This flag is redundant given a valid DTD; it exists to take care
   *	of the common case where the DTD is unknown or incomplete.  Also
   *	it can greatly speed up many operations that would otherwise require
   *	knowledge of the DTD.
   */
  public boolean isEmptyElement() { return isEmptyElement; }

  /** Sets the internal flag corresponding to isEmptyElement. */
  public void setIsEmptyElement(boolean value) { isEmptyElement = value; }

  /** Returns <code>true</code> if the Element has an XML-style 
   *	``<code>/</code>'' denoting an empty element.
   *
   *	This flag is redundant given a valid DTD; it exists to take care of
   *	the common case where the DTD is unknown or incomplete; for example,
   *	where XML extensions are mixed in with HTML.  Also it can greatly
   *	speed up many operations that would otherwise require knowledge of the
   *	DTD.
   */
  public boolean hasEmptyDelimiter() { return hasEmptyDelim; }

  /** Sets the internal flag corresponding to hasEmptyDelim. */
  public void setHasEmptyDelimiter(boolean value) { hasEmptyDelim = value; }

  /** Returns true if the Token corresponds to an Element which has content
   *	but no end tag, because the end tag can be deduced from context.
   *
   *	This flag is redundant given a valid DTD; it exists to take care
   *	of the common case where the DTD is unknown or incomplete, or where
   *	an effort needs to be made to preserve exact input formatting in
   *	the parse tree. <p>
   *
   * === Strictly speaking it has to be turned off if a Text node is inserted
   *	 following this Element.
   */
  public boolean implicitEnd() 		   { return implicitEnd; }

  /** Sets the internal flag corresponding to implicitEnd. */
  public void setImplicitEnd(boolean flag) { implicitEnd = flag; }


  /************************************************************************
  ** Presentation Operations:
  ************************************************************************/

  /**
   * return start tag + content string + end tag
   * 
   */ 
  public String toString() {
    return startString() + contentString() + endString();
  }


  /**
   * Return a string of the form "<" <code>getTagName()</code> ">".
   *
   */
  public String startString() {
    String s = "<" + (tagName == null ? "" : tagName);
    AttributeList attrs = getAttributes();
    if (attrs != null && attrs.getLength() > 0) {
      s += " " + attrs.toString();
    }
    return s + (hasEmptyDelimiter() ? "/" : "") + ">";
  }

  /** Return the String equivalent of all children concatenated
   *  in string form.
   */
  public String contentString() {
    StringBuffer sb = new StringBuffer();
    long len = 0;

    ChildNodeList cl = (ChildNodeList)getChildren();
    if( cl == null || (len = cl.getLength()) == 0 ) return new String( sb );

    try{
      Node a = null;
      for(int i = 0;  i < len; i++){ 
	a = cl.item( i );
	sb.append( a.toString() );
      }
    }catch(Exception ee){
    }
    return new String( sb );
  }




  /** Return a string of the form "</" <code>getTagName()</code> ">".
   *	
   */
  public String endString(){
    return (implicitEnd() || isEmptyElement()) ? ""
      : "</" + (getTagName() == null ? "" : getTagName()) + ">";
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




