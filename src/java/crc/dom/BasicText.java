// BasicText.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;




public class BasicText extends AbstractNode implements Text {

  public BasicText(){
    ignoreWhiteSpc = false;
  }

  public BasicText(String d){
    data = d;
    ignoreWhiteSpc = false;
  }


  public BasicText(String d, boolean iws)
  {
    data = d;
    ignoreWhiteSpc = iws; 
  }

  public BasicText(BasicText bt)
  {
    if( bt != null ){
      setParent( null );
      setPrevious( null );
      setNext( null );
      setIsIgnorableWhitespace( false );
      setData( bt.getData() );
      copyChildren( bt );
    }
  }

  public Object clone(){
    BasicText n = (BasicText)super.clone();
    n.setIsIgnorableWhitespace( false );
    n.setData( getData() );
    n.copyChildren( this );
    return n;
  }
  
  public void setData(String data){ this.data = data; }
  public String getData(){ return data; }

  public void setIsIgnorableWhitespace(boolean isIgnorableWhitespace){ ignoreWhiteSpc = isIgnorableWhitespace;}

  public boolean getIsIgnorableWhitespace(){ return ignoreWhiteSpc; }

  public int getNodeType(){ return NodeType.TEXT; }

  protected String data = "";
  protected boolean ignoreWhiteSpc;
};
