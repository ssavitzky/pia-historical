// T_DOM.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.test.dom;

import crc.dom.*;

public class T_DOM{
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
    Report.debug("\nTesting getting BasicDOMFactory...");
    DOMFactory df = CRCDOM.getFactory(DOM.BASICFACTORY);
    Document doc = df.createDocument();
    Text t = df.createTextNode("To wong foo. Thanks for everything.");
    Comment c = df.createComment("To comment or not to comment.");
    PI bpi = df.createPI("desertstorm", "use video game.");
    Report.debug("End testing basicdomfactory...");

    Report.debug("\nTesting creating customer document...");
    Document cd = createCustomerDocument( df );
    doTree( cd.getDocumentElement(), "" );
    Report.debug("End testing customer document...");
  }
  
  private static Document createCustomerDocument(DOMFactory df)
  {
    Document cusDoc  = df.createDocument();

    Element customer  = df.createElement("Customer", null);
    Element name      = df.createElement("Name", null);
    Element lName     = df.createElement("LastName", null);
    Element fName     = df.createElement("FirstName", null);



    Element al        = df.createElement("attibutelist", null);
    Text vt           = df.createTextNode("1.0");

    try{
      al.insertBefore(vt, null);
      Attribute version = df.createAttribute("Version", al.getChildren() );

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










