// T_Node.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.test.dom;

import crc.dom.*;

public class T_Node{
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
    else
      if( args[0].equals ("3") ) 
	test3( "" );
    else
      if( args[0].equals ("4") ) 
	test4( "" );

    System.exit( 1 );
  }


  /**
  * For testing.
  * 
  */ 
  private static void test1(String foo){
    Report.debugToFile( true );
    Report.setDebugFilePath("logfile");
    Report.debug("Hello world");

    NodeEnumerator ne = null;

    Element be = new BasicElement();

    Element t2 = new BasicElement();
    t2.setTagName( "I am second." );

    Element t1 = new BasicElement();
    t1.setTagName( "I am first." );

    Element t3 = new BasicElement();
    t3.setTagName( "I am third." );

    Element t4 = new BasicElement();
    t4.setTagName( "I am fourth." );

    Element t5 = new BasicElement();
    t5.setTagName( "I am in the middle." );
   
    Element mid = new BasicElement();
    mid.setTagName( "I am before second." );

    try{
      Report.debug("Testing hasChildren...");
      if( be.hasChildren() )
	Report.debug("I have children...");
      else
	Report.debug("I don't have any children...");

      Report.debug( "appending..." );
      // appending
      be.insertBefore( t2, null  );

      // insert at start
      be.insertBefore( t1, t2 );

      // insert in the middle
      be.insertBefore(mid, t2);

      // appending
      be.insertBefore( t3, null  );

      // original list
      ne = be.getChildren().getEnumerator();
      printChildNodeList( ne );

      Report.debug("replace at front...");
      be.replaceChild( t1, t4 );
      printChildNodeList( ne );

      Report.debug("replace last...");
      be.replaceChild( t3, t1 );
      printChildNodeList( ne );

      Report.debug("replace middle...");
      be.replaceChild( t2, t5 );
      printChildNodeList( ne );

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
    Report.debugToFile( true );
    Report.setDebugFilePath("logfile");

    NodeEnumerator ne = null;

    Element be = new BasicElement();

    Element t2 = new BasicElement();
    t2.setTagName( "I am second." );

    Element t1 = new BasicElement();
    t1.setTagName( "I am first." );

    Element t3 = new BasicElement();
    t3.setTagName( "I am third." );

    try{
      Report.debug( "appending..." );
      // appending
      be.insertBefore( t2, null  );

      // insert at start
      be.insertBefore( t1, t2 );

      // appending
      be.insertBefore( t3, null  );

      // original list
      ne = be.getChildren().getEnumerator();
      printChildNodeList( ne );

      Report.debug( "Removing first Child...");
      be.removeChild( t1 );
      printChildNodeList( ne );

      Report.debug( "Removing last Child...");
      be.removeChild( t3 );
      printChildNodeList( ne );

      Report.debug( "Removing lonely Child...");
      be.removeChild( t2 );
      printChildNodeList( ne );
      Report.debug("End of test...");
      Report.closeTraceFile();

    }catch(NotMyChildException e){
      Report.debug(e.toString());
    }


  }
 
  /**
  * For testing.
  * 
  */ 
  private static void test3( String foobar ){
    NodeEnumerator ne = null;

    BasicElement be = new BasicElement();
    be.setTagName( "I am parent" );

    Element t2 = new BasicElement();
    t2.setTagName( "I am second." );

    Element t2prime = new BasicElement();
    t2prime.setTagName( "r2d2 is a t2 child." );
    
    Attribute attr = new BasicAttribute( t2 );
    attr.setName("I am attribute.");
    t2.setAttribute( attr );
    
    

    Element t1 = new BasicElement();
    t1.setTagName( "I am first." );

    Element t3 = new BasicElement();
    t3.setTagName( "I am third." );

    try{
      Report.debug( "appending..." );
      // appending
      t2.insertBefore( t2prime, null );
      be.insertBefore( t2, null  );

      // insert at start
      be.insertBefore( t1, t2 );

      // appending
      be.insertBefore( t3, null  );

      // original list
      ne = be.getChildren().getEnumerator();
      printChildNodeList( ne );

      BasicElement copyEle = new BasicElement( be );
      copyEle.setTagName("Clone Parent");
      NodeEnumerator ce = copyEle.getChildren().getEnumerator();
      doTree( copyEle, "" );

      Report.debug("Here is the original list.");
      doTree( be, "" );

    }catch(NotMyChildException e){
      Report.debug(e.toString());
    }


  }
 


  /**
  * For testing.
  * 
  */ 
  private static void test4( String foobar ){
    NodeEnumerator ne = null;

    BasicElement be = new BasicElement();
    be.setTagName( "html" );

    Element t2 = new BasicElement();
    t2.setTagName( "item2" );

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

      NodeEnumerator ce = be.getElementsByTagName( "foobar" );

      Report.debug("Here is the find all list.");
      printChildNodeList( ce );

    }catch(NotMyChildException e){
      Report.debug(e.toString());
    }


  }
 





}










