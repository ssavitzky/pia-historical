// AppComment.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.


/**
 * Implements w3c's Document interface. 
 */

package crc.dom;

import java.io.*;

public class AppComment extends AbstractNode implements Comment {

  public AppComment(){ comment = ""; }

  /**
   * implements DOMFactory interfaces
   */
  public int getNodeType(){ return NodeType.COMMENT; }
  public void setData(String data){ comment = data; }
  public String getData(){ return comment; }
  protected String comment;
}




