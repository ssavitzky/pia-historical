// BasicComment.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * Implements w3c's Document interface. 
 */

package crc.dom;

import java.io.*;

public class BasicComment extends AbstractNode implements Comment {

  public BasicComment(){ comment = ""; }

  public BasicComment(String c){ comment = c; }

  public BasicComment(BasicComment bc){
    if( bc != null ){
      setParent( null );
      setPrevious( null );
      setNext( null );
      setData( bc.getData() );
      copyChildren( bc );
    }
  }


  public Object clone(){
    BasicComment n = (BasicComment)super.clone();
    n.setData( getData() );
    n.copyChildren( this );
    return n;
  }

  /**
   * implements DOMFactory interfaces
   */
  public int getNodeType(){ return NodeType.COMMENT; }
  public void setData(String data){ comment = data; }
  public String getData(){ return comment; }


  /** Return an empty string
   * 
   */
  public String startString(){
    return "";
  }
  
  /** Return the String equivalent of this node type.
   *  Subclasses are suppose to override this function
   *  to return appropriate content string.
   */
  public String contentString(){
    return getData();
  }

  /** Return an empty string
   *	
   */
  public String endString(){
    return "";
  }






  /**
   * actual comment
   */
  protected String comment;
}




