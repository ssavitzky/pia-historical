// BasicDOMFactory.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * Implements w3c's DOMFactory interface. 
 */

package crc.dom;

import java.io.*;

public class BasicDOMFactory extends AbstractDOMFactory {

  public BasicDOMFactory(){}

  /**
   * implements DOMFactory interfaces
   */
  public Document createDocument(){
    //Report.debug(this,"createDocument");
    return new BasicDocument();
  }

  public DocumentContext   createDocumentContext(){ return null; }

  /**
   *Create an element based on the tagName. Note that the instance returned may
   *implement an interface derived from Element. The attributes parameter can be
   *null if no attributes are specified for the new Element. 
   */
  public Element           createElement(String tagName, AttributeList attributes){
    //Report.debug(this,"createElement...");
    if( tagName == null ) return null;
    Element e = new BasicElement();
    e.setTagName( tagName );
    if ( attributes != null ) 
      e.setAttributes( attributes );
    return e;
  }

  /**
   *  Create a Text node given the specified string. 
   */
  public Text              createTextNode(String data){
    //Report.debug(this,"createTextNode...");
    return new BasicText( data ); 
  }

  /**
   *  Create a Comment node given the specified string. 
   */
  public Comment           createComment(String data){
    //Report.debug(this,"createComment...");
    return new BasicComment( data );
  }

  /**
   *  Create a PI node with the specified name and data string.
   */
  public PI                createPI(String name, String data){
    //Report.debug(this,"createPI...");
    return new BasicPI( name, data );
  }

  /**
   *Create an Attribute of the given name and specified value. Note that the
   *Attribute instance can then be set on an Element using the setAttribute method.
   */
  public Attribute         createAttribute(String name, NodeList value){
    //Report.debug(this,"createAttribute...");
    if( name == null ) return null;
    return new BasicAttribute( name, value );
  }

  /**
   * deep copy
   */
  public Node makeDeepCopy(Node src){
    Node des = doCopy( src );
    if( des != null ){
      copyChildren( src, des );
      return des;
    }
    else
      return null;
  }

  /**
   * copy a node
   */
  public Node doCopy(Node org){
    Node anode = null;

    switch( org.getNodeType() ){
    case NodeType.DOCUMENT:

      Document oldDoc = (Document) org;
      Element root = oldDoc.getDocumentElement();
      Element newDocRoot = (Element)doCopy( root );
      Document newDoc = createDocument();
      newDoc.setDocumentElement( newDocRoot );

      anode =  newDoc;
      break;
    case NodeType.ELEMENT:

      Element e = (Element)org;
      AttributeList attrList = new AttrList();
      AttributeList ol = e.getAttributes();
      Attribute a = null;
      Attribute newAttr = null;

      long len = ol.getLength();
      for(long i=0; i < len; i++){
	try{
	  a = (Attribute)ol.item( i );
	  newAttr = (Attribute)doCopy( a );
	  if( newAttr != null )  
	    attrList.setAttribute(a.getName(), newAttr);
	}catch(NoSuchNodeException ee){}
      }

      anode = createElement( e.getTagName(), attrList );
      break;
    case NodeType.ATTRIBUTE:

      Attribute attr = (Attribute)org;
      EditableNodeList enl = new ArrayNodeList();
      NodeList nl = attr.getValue();
      Node n = null;
      Node child = null;
      
      long length = nl.getLength();
      for(long i=0; i < length; i++){
	try{
	  n = nl.item( i );
	  child = doCopy( n );
	  if( child != null )  
	    enl.insert(enl.getLength(), child);
	}catch(NoSuchNodeException ee){}
      }
      
      anode = createAttribute( attr.getName(), enl );
      break;
    case NodeType.PI:
      PI p = (PI)org;
      anode = createPI( p.getName(), p.getData() );
      break;
    case NodeType.COMMENT:
      Comment c = (Comment)org;
      anode = createComment( c.getData() );
      break;
    case NodeType.TEXT:
      Text t =(Text)org;
      anode = createTextNode( t.getData() );
      break;
    case NodeType.ENTITY:
      break;
    default:
      return null;
    }

    return anode;
  }

  protected void copyChildren(Node src, Node des){
    Node elem = null;
    Node child = null;


    if( src.hasChildren() ){
      NodeEnumerator enum = src.getChildren().getEnumerator();
      
      elem = enum.getFirst();
      while( elem != null ){
	child = doCopy( elem );
	try{
	  des.insertBefore( child, null );
	}catch(NotMyChildException e){
	  Report.debug(e.toString());
	}
      }
      copyChildren( elem, child );
      elem = enum.getNext();
    }

  }

}




