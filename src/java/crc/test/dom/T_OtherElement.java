// T_OtherElement.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.test.dom;

import crc.dom.*;

public class T_OtherElement{
  private static void printusage(){
    System.out.println("Needs to know what kind of test");
    System.out.println("For test 1, here is the command -->");
  }


  public static void main(String[] args){
    if( args.length < 1 ){
      printusage();
      System.exit( 1 );
    }
    
    if( args[0].equals ("1") )
	test1( "" );
    else
      if( args[0].equals ("2") ) 
	test2( "" );
    System.exit( 1 );
  }


  /**
  * For testing.
  * 
  */ 
  private static void test1(String dummy){
    Report.debug("\nTesting Basic Comment, PI, Text, and Document...");

    Report.debug("\nOoo Ooo. What is that smell...  Can you smell that smell.");
    Text st = new BasicText("Ooo Ooo. What is that smell...  Can you smell that smell.");
    Report.debug("\nText data is -->"+ st.getData());


    Report.debug("\nCreating text Rain in Spain...");
    Text t = new BasicText("Spain&apos;s rain is both &lt;romantic&gt; &amp; sad. &quot;The rain keep you sane.&quot;someone said.");
    Report.debug("\nText data is -->"+ t.getData());
    Report.debug("\nCloning Rain in Spain...");
    Text t2 = new BasicText( (BasicText)t );
    Report.debug("\nCloned text data is -->"+ t2.getData());


    Report.debug("\nCreating comment");
    Comment c = new BasicComment("To comment or not to comment.");
    Report.debug("\nText data is -->"+ c.getData());
    Report.debug("\nCloning comment...");
    Comment c2 = new BasicComment( (BasicComment)c );
    Report.debug("\nCloned text data is -->"+ c2.getData());
    Report.debug("End testing basic stuff...");

    Report.debug("\nTesting creating customer document...");
    Document cd = createCustomerDocument( CRCDOM.getFactory(DOM.BASICFACTORY) );
    doTree( cd.getDocumentElement(), "" );
    Report.debug("End testing customer document...");

    Report.debug("\nTesting cloning document...");
    Document nd = new BasicDocument( (BasicDocument)cd );
    doTree( nd.getDocumentElement(), "" );
    Report.debug("End testing cloning document");


  }

 private static Document createCustomerDocument(DOMFactory df)
  {
    Document cusDoc  = df.createDocument();

    Element customer  = df.createElement("Customer", null);
    Element name      = df.createElement("Name", null);
    Element lName     = df.createElement("LastName", null);
    Element fName     = df.createElement("FirstName", null);



    try{
      Text vl           = df.createTextNode("");
      Text onev         = df.createTextNode("1.0");
      Text twov         = df.createTextNode("hello");

      vl.insertBefore(twov, null);
      vl.insertBefore(onev, twov);

      Report.debug("value list length-->"+Integer.toString((int)(vl.getChildren().getLength())));
      Attribute version = df.createAttribute("Version", vl.getChildren() );

      name.insertBefore(lName, null);
      name.insertBefore(fName, lName);
      
      name.setAttribute( version );
      customer.insertBefore(name, null);
    }catch(Exception e){
    }
    cusDoc.setDocumentElement( customer );
    return cusDoc;
  }

  private static String printNodeList( Attribute a ){
    NodeList nl = a.getValue();
    long len    = nl.getLength();
    Node n      = null;
    StringBuffer sb = new StringBuffer();

    Report.debug("the attribute list is-->"+a.toString());
    try{
      for(long i = 0; i < len; i++ ){
	n = nl.item( i );
	if( n instanceof Text )
	  sb.append(" " + ((Text)n).getData() );
	else
	  sb.append(" " + Integer.toString( n.getNodeType() ) );
      }
    }catch(Exception e){
    }

    return new String( sb );
  }

  private static void printAttributeList( Element e, String indent ){
    AttributeList l = e.getAttributes();
    Attribute attr = null;

    long i = 0;
    try{
      attr = (Attribute)l.item( i );
      while( attr != null ){
	Report.debug( indent + "[" + attr.getName() + "=" + printNodeList( attr ) + "]" );
	attr = (Attribute)l.item( ++i );
      }
    }catch(NoSuchNodeException ee){
    }
  }




  private static void doTree( Element elem, String indent ){
    Element child;

    Report.debug(indent + "<" + elem.getTagName() + ">");
    printAttributeList( elem, indent );
    if(elem.hasChildren()){
      NodeEnumerator enum = elem.getChildren().getEnumerator();

      child =  (Element)enum.getFirst();
      while( child != null ) {
	doTree( child, indent + "    ");
	child = (Element)enum.getNext();
      }
    }
    Report.debug(indent + "</" + elem.getTagName()+ ">");
  } 
 
 
 
  /**
  * For testing.
  * 
  */ 
  private static void test2( String dummy){
  }
 

}










