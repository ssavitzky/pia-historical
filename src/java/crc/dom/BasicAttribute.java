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
    if( v != null )
      setValue( v );
    else
      setNullValue();

    // explicitly assigned value
    setIsAssigned( true );

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

  /**
   * Deep copy constructor.
   */
  public BasicAttribute(BasicAttribute attr){
    AbstractNode a = null;

    setPrevious( null );
    setNext( null );
    setName( attr.getName() );
    setValue( new ChildNodeList( attr.getValue() ) );
    copyChildren( attr );
    setSpecified( attr.getSpecified() );
    setIsAssigned( attr.getIsAssigned() );
  }

  /**
   * Deep copy.
   */
  public Object clone(){
    BasicAttribute n = (BasicAttribute)super.clone();
    n.setName( getName() );
    setValue( new ChildNodeList( getValue() ));
    n.copyChildren( this );
    n.setSpecified( getSpecified() );
    n.setIsAssigned( getIsAssigned() );
    return n;
  }

  /**
   * Return NodeType.ATTRIBUTE.
   * @return NodeType.ATTRIBUTE.
   */
  public int getNodeType() { return NodeType.ATTRIBUTE; }

  /**
   * Set attribute name.
   * @param name attribute name.
   */
  public void setName(String name){ this.name = name; }

  /**
   * Returns the name of this attribute. 
   * @return attribute name.
   */
  public String getName(){ return name; }
  
  /**
   * Set the attribute's value.
   * @param value The effective value of this attribute. (The attribute's effective value is
   * determined as follows: if this attribute has been explicitly assigned any
   * value, that value is the attribute's effective value; otherwise, if there is a
   * declaration for this attribute, and that declaration includes a default value,
   * then that default value is the attribute's effective value; otherwise, the
   * attribute has no effective value.)Note, in particular, that an effective value
   * of the null string would be returned as a Text node instance whose toString()
   * method will return a zero length string (as will toString() invoked directly on
   * this Attribute instance).If the attribute has no effective value, then this
   * method will return null. Note the toString() method on the Attribute instance can
   * also be used to retrieve the string version of the attribute's value(s).  
   */ 
  public void setValue(NodeList value){
    setIsAssigned( true );
    if( value == null )
      setNullValue();
    else
      this.value = value; 
  }
  
  /**
   * Return attribute value
   * @return attribute value.
   */
  public NodeList getValue(){ return value; }
  
  /**
   * Set specified value.If this attribute was explicitly given a value in the original document, this
   * will be true; otherwise, it will be false. 
   */
  public void setSpecified(boolean specified){ this.specified = specified; }

  /**
   * Return whether value is specified.
   */
  public boolean getSpecified(){return specified;}

  /**
   * Returns the value of the attribute as a string. Character and general entity
   * references will have been replaced with their values in the returned string. 
   */
  public String toString(){
    if( value == null) return null;

    long len = value.getLength();
    StringBuffer sb = new StringBuffer();
    Node n = null;
    String data = null;
    String result = null;

    for( long i = 0; i < len; i++ ){
      try{
	n = value.item( i );
	if( n instanceof Text ){
	  // At this junction getData would already done character substitution.
	  data = ((Text)n).getData();

	  // Now do general entity substitution if any.
	  result = genEntSub( data );

	  sb.append( " " + result );
	}
      }catch(NoSuchNodeException e){
      }
    }

    return new String( sb );
  }

  /**
   * General entity substitution. By searching for the Document object through parent, get DTD
   * from the Document object's getDocumentType.  From the DocumentType object, get generalEntities object.
   * Now, do substitution.
   */
  protected String genEntSub(String data){
    Document doc = null;

    doc = findDoc(this);
    if( doc == null ) return data;

    DocumentType dtd = (DocumentType)doc.getDocumentType();
    if( dtd == null ) return data;

    NamedNodeList ge = dtd.getGeneralEntities();
    if( ge == null ) return data;

    return doGenEntSub(ge, data);
  }

  /**
   * Given the list of general entities and data.
   * Now, do general entities substitution.
   *
   */
  protected String doGenEntSub(NamedNodeList ge, String data){
    String ent;
    String result;

    EntityScanner es = new EntityScanner( data );
    ent = es.nextEntity();
    while( ent != null ){
      if( ge.getNode( ent ) != null ){
	//result = subEntity(ent, ge, data);
	//data = result;
      }
      ent = es.nextEntity();
    }

    return data;
  }


  /**
   * substitute entity within data and return a new string.
   */
  protected String subEntity(String et, NamedNodeList ge, String s){
    int spos=0;
    int epos=0;
    int fpos=0;
    boolean found = false;
    StringBuffer sb = new StringBuffer();
    Node gEntity= null;

    String ent = "&"+et+";";
    fpos = s.indexOf( ent );
    while( fpos != -1 ){
      found = true;
      Report.debug("spos is-->"+Integer.toString(spos));
      Report.debug("fpos is-->"+Integer.toString(fpos));
      Report.debug("The substring is-->"+s.substring(spos, fpos));
      sb.append(s.substring(spos, fpos));
      spos = fpos + ent.length();
      
      gEntity = ge.getNode(et);
      //if( gEntity instanceof InternalEntity ) 
      //	sb.append( ((InternalEntity)gEntity).getContent() );
      //   else if ( gEntity instanceof ExternalTextEntity )
      //	sb.append( ((ExternalTextEntity)gEntity).getContent() );
      fpos = s.indexOf( ent, fpos+1 );
    }
    if( !found ) return s;
    
    if( spos != 0 )
      sb.append(s.substring(spos, s.length()));

    return new String( sb );
  }


  /**
   * Find doc from parent.
   */
  protected Document findDoc(Node n){
    Node p = n.getParentNode();
    if( p == null ) return null;

    if( !(p instanceof Document) ){
      p = findDoc( p ); 
    }
    return (Document)p;
  }
  

  protected void setNullValue(){
    try{
      Text al = new BasicText("AttributeList");
      al.insertBefore(new BasicText(), null);
      value = al.getChildren();
    }catch(Exception e){
    }
  }

   protected void setIsAssigned(boolean what){
     isAssigned = what;
   }

   protected boolean getIsAssigned(){
     return isAssigned;
   }

  /* attribute name */
  protected String name;

  /* list of values */
  protected NodeList value;

  /* whether value is specified in the original document */
  protected boolean specified;

  /* whether value is assigned explicitly */
  protected boolean isAssigned = false;
}


class EntityScanner{

  EntityScanner(String data){ this.data = data; }

  /**
   * Scan for general entity.  If one is found, it will be
   * returned; otherwise, null.
   */
  protected String nextEntity(){
    int spos = 0;
    int epos = 0;

    if( start >= data.length() ) return null;

    spos = data.indexOf( '&', start );
    if( spos != -1 ){
      // some thing is found
      if( spos+1 >= data.length() ) return null;

      epos = data.indexOf( ';', spos+1 );
      if( epos != -1 ){
	start = epos + 1;
	return data.substring( spos+1, epos );
      }
      else
	return null;
    }
    else return null;
  }

  String data;
  int start = 0;


}
