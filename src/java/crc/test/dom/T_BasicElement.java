// T_BasicElement.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.test.dom;

import crc.dom.*;

public class T_BasicElement{
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
  private static void test1(String foo){
    NodeEnumerator ne = null;

    BasicElement be = new BasicElement();
    be.setTagName( "html" );

    Element t2 = new BasicElement();
    t2.setTagName( "item2" );

    Attribute attr = new BasicAttribute( t2 );
    attr.setName("size");
    t2.setAttribute( attr );

    Element t1 = new BasicElement();
    t1.setTagName( "foobar" );

    Element foobar2 = new BasicElement();
    foobar2.setTagName( "foobar" );


    try{
      Report.debug( "appending..." );
      // appending
      be.insertBefore( t2, null  );

      // insert at start
      be.insertBefore( t1, t2 );

      // appending
      t1.insertBefore( foobar2, null  );

      // original list
      ne = be.getChildren().getEnumerator();
      printChildNodeList( ne );

      Report.debug("Printing children...");
      be.printChildren("");
      Report.debug("End printing children.");

      NodeEnumerator ce = be.getElementsByTagName( "foobar" );

      Report.debug("Here is the find all list.");
      printChildNodeList( ce );

      BasicElement copyEle = new BasicElement( be );
      copyEle.setTagName("Clone Parent");
      Report.debug("Here is the new list.");
      copyEle.printChildren( "" );

      Report.debug("Here is the original list.");
      be.printChildren( "" );



    }catch(NotMyChildException e){
      Report.debug(e.toString());
    }


  }

  private static void printChildNodeList( NodeEnumerator ne ){
    Node n = ne.getFirst();
    
    while( n != null ){
      if( n instanceof Element )
	Report.debug( ((Element)n).getTagName() );
      else
	Report.debug( Integer.toString( n.getNodeType() ) );
      n = ne.getNext();
    }
  }

  private static void printAttributeList( Element e ){
    AttributeList l = e.getAttributes();
    Attribute attr = null;

    long i = 0;
    try{
      attr = (Attribute)l.item( i );
      while( attr != null ){
	Report.debug( "<" + attr.getName()+"/>" );
	attr = (Attribute)l.item( ++i );
      }
    }catch(NoSuchNodeException ee){
    }
  }

  private static void doTree( Element elem, String indent ){
    Element child;

    Report.debug(indent + "<" + elem.getTagName() + ">");
    printAttributeList( elem );
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
  private static void test2( String foobar ){
  }
 





}










