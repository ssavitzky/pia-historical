// BasicAttribute.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.dom;

import java.io.*;

public class BasicAttribute extends AbstractNode implements Attribute {

  public BasicAttribute(String n, NodeList v){
    setParent( null );
    setPrevious( null );
    setNext( null );
    setName( n );
    setValue( v );
    /* true if attribute is given a value in the original doc */
    setSpecified( true );
  }

  public BasicAttribute(Node myParent){
    if( myParent != null )
      setParent( (AbstractNode)myParent );
    else
      setParent( null );
    setPrevious( null );
    setNext( null );
    setName( "" );
    setValue( null );
    setSpecified( false );
  }


  public BasicAttribute(BasicAttribute attr){
    AbstractNode a = null;

    setPrevious( null );
    setNext( null );
    setName( attr.getName() );
    setValue( new ChildNodeList( attr.getValue() ) );
    copyChildren( attr );
    setSpecified( attr.getSpecified() );
  }

  public Object clone(){
    BasicAttribute n = (BasicAttribute)super.clone();
    n.setName( getName() );
    setValue( new ChildNodeList( getValue() ));
    n.copyChildren( this );
    n.setSpecified( getSpecified() );
    return n;
  }

  public int getNodeType() { return NodeType.ATTRIBUTE; }

  public void setName(String name){ this.name = name; }
  public String getName(){ return name; }
  
  public void setValue(NodeList value){ this.value = value; }
  public NodeList getValue(){ return value; }
  
  public void setSpecified(boolean specified){ this.specified = specified; }
  public boolean getSpecified(){return specified;}

  // provides a connection to the DTD 
  // attribute Node definition;

  public String toString(){
    // not implemented yet
    return "";
  }

  /* attribute name */
  protected String name;

  /* list of values */
  protected NodeList value;

  /* whether value is specified */
  protected boolean specified;
}


