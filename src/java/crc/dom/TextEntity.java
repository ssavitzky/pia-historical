// TextEntity.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.
package crc.dom;


public class TextEntity extends AbstractEntity{
  /**
   * Set the entity's content.  For a null string,
   * value is set to a blank BasicText.
   */
  public void setContent(String content){
    if( content == null )
      setNullValue();
    else{
      try{
	Text al = new BasicText("AttributeList");
	al.insertBefore(new BasicText( content ), null );
	value = al.getChildren();
      }catch(Exception e){
      }
    }
  }
  
  /**
   * Return this entity content
   * @return this entity content
   */
  public String getContent(){
    return toString();
  }

  /**
   * Return this entity content in string form.
   * This implements getContent().
   */
  public String toString(){
    if( value == null ) return "";
    
  
    long len = value.getLength();
    StringBuffer sb = new StringBuffer();
    Node n = null;

    for( long i = 0; i < len; i++ ){
      try{
	n = value.item( i );
	if(n != null)
	  sb.append( " " + n );
	else
	  sb.append("");
      }catch(NoSuchNodeException e){
      }
    }

    return new String( sb );
  }


  /**
   * Set the entity's value.
   */
  protected void setValue(NodeList value){
    if( value == null )
      setNullValue();
    else
      this.value = value; 
  }

  /**
   * Return entity value
   * @return entity value.
   */
  protected NodeList getValue(){
    return value;
  }

  /**
   * Create a blank BasicText and assign it to 
   * value. Call from setValue when its parameter is null.
   */
  protected void setNullValue(){
    try{
      Text al = new BasicText("AttributeList");
      al.insertBefore(new BasicText(), null);
      value = al.getChildren();
    }catch(Exception e){
    }
  }

  /**
   * The content of TextEntity is in the form of NodeList.
   */
  protected NodeList value;
  
}
