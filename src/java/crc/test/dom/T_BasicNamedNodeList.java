// T_BasicNamedNodeList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.test.dom;

import crc.dom.*;

public class T_BasicNamedNodeList{
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
  private static void test1(String filename){
    BasicNamedNodeList nl = new BasicNamedNodeList();
    Text t = null;
    String n = null;
    
    for (int i = 0; i < 10; i++){ 
      n = "Hello"+Integer.toString(i);
      t = new BasicText( n, true );
      nl.setNode(n, t);
    }

    Report.debug("\nTesting item");
    int i = 0;
    try{
      for (; i <= 10; i++){ 
	Text data = (Text)nl.item( i );
	Report.debug("Element at["+Integer.toString(i)+"] is:" + data.getData());
      }
      Report.debug("End Testing item");
    }catch(Exception err1){
      Report.debug("Element at["+Integer.toString(i)+"] does not exist.");
      Report.debug( err1.toString() );
    }

    Report.debug("\nTesting retreiving using name");
    for (int j = 0; j < 10; j++){ 
      n = "Hello"+Integer.toString(j);
      t = (Text)nl.getNode(n);
      Report.debug("Element at["+ n +"] is:" + t.getData());
    }
    Report.debug("End testing retreiving by name");


    Report.debug("\nTesting copying list...");
    BasicNamedNodeList nnl = new BasicNamedNodeList( nl );
    Report.debug("End testing copying list");


    Report.debug("\nTesting removing");
    try{
      nl.remove( "Hello0" );
      nl.remove( "Hello9" );
    }catch(Exception err2){
      Report.debug( err2.toString() );
    }
    Report.debug("End testing removing.");

    Report.debug("\nTesting enumeration of old list");
    NodeEnumerator ne = nl.getEnumerator();
    t = (Text)ne.getFirst();
    while( t != null ){
      Report.debug(t.getData());
      t = (Text)ne.getNext();
    }
    Report.debug("End testing enumeration of old list");

    Report.debug("\nTesting enumeration of new list");
    ne = nnl.getEnumerator();
    printForward( ne );
    Report.debug("End testing enumeration of new list");


    Report.debug("\nTesting enumeration of new list backward");
    ne = nnl.getEnumerator();
    printBackward( ne );
    Report.debug("End testing enumeration of new list");
  }

  /**
  * For printing forward
  * 
  */ 
  private static void printForward( NodeEnumerator ne ){
    Text t = (Text)ne.getFirst();
    while( !ne.atEnd() ){
      Report.debug(t.getData());
      t = (Text)ne.getNext();
    }
    Report.debug(t.getData());
  }

 
  /**
  * For printing backward
  * 
  */ 
  private static void printBackward( NodeEnumerator ne ){
    Text t = (Text)ne.getLast();
    while( !ne.atStart() ){
      Report.debug(t.getData());
      t = (Text)ne.getPrevious();
    }
    Report.debug(t.getData()); 
  }



  /**
  * For testing.
  * 
  */ 
  private static void test2( String aclass){
  }
 

}










