// AppText.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;




public class AppText extends AbstractNode implements Text {

  public AppText(){
    data = "";
    ignoreWhiteSpc = false;
  }
  
  public void setData(String data){ this.data = data; }
  public String getData(){ return data; }

  public void setIsIgnorableWhitespace(boolean isIgnorableWhitespace){ ignoreWhiteSpc = isIgnorableWhitespace;}

  public boolean getIsIgnorableWhitespace(){ return ignoreWhiteSpc; }

  public int getNodeType(){ return NodeType.TEXT; }

  protected String data;
  protected boolean ignoreWhiteSpc;
};
