// T_ArrayNodeList.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1998.

package crc.test.dom;

import crc.dom.*;

public class T_ArrayNodeList{
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
    ArrayNodeList a = new ArrayNodeList();
    try{
      for (int i = 0; i < 10; i++){ 
	a.insert(i, new BasicText(  Integer.toString(i), true  ) );
	Report.debug("The size of a is-->"+Integer.toString((int) a.getLength() ));
      }
    }catch(Exception err0){
    }

    Report.debug("\nTesting item");
    int i = 0;
    try{
      for (; i <= 10; i++){ 
	BasicText data = (BasicText)a.item( i );
	Report.debug("Element at["+Integer.toString(i)+"] is:" + data.getData());
      }
    Report.debug("End Testing item");
    }catch(Exception err1){
      Report.debug("Element at["+Integer.toString(i)+"] does not exist.");
      Report.debug( err1.toString() );
    }

    try{
      Report.debug("\nTesting inserting at tail position.");


      a.insert(9, new BasicText(  Integer.toString(777), true) );
      a.insert(0, new BasicText(  Integer.toString(333), true ) );
      a.insert(5, new BasicText(  Integer.toString(555),  true) );
      a.insert(10, new BasicText(  Integer.toString(888), true ) );

      a.insert(14, new BasicText(  Integer.toString(999),  true) );

      a.insert(16, new BasicText(  Integer.toString(111),  true) );
      Report.debug("End testing inserting at tail position.");
    }catch(Exception err1){
      Report.debug("Element at["+Integer.toString(i)+"] does not exist.");
      Report.debug( err1.toString() );
    }

    Report.debug("\nAfter insert the list looks like");
    NodeEnumerator e = a.getEnumerator();
    printNodeForward( e );
    Report.debug("End of dump");

    try{
      Report.debug("\nTesting removing added items.");
      a.remove( 0 );
      a.remove( 4 );
      a.remove( 8 );
      a.remove( 9 );
      a.remove( 10 );
      a.remove( 11 );
      Report.debug("End of testing removing added items.");
    }catch(Exception err2){
      Report.debug( "From testing removing items-->"+err2.toString() );
    }

    Report.debug("\nAfter remove the list looks like");
    e = a.getEnumerator();
    printNodeForward( e );
    Report.debug("End of dump");

    try{
      Report.debug("\nTesting replacing items.");
      a.replace(0, new BasicText(  Integer.toString(111),  true) );
      a.replace(9, new BasicText(  Integer.toString(999), true) );

      a.replace(10, new BasicText(  Integer.toString(999), true) );
      Report.debug("End of testing replacing.");
    }catch(Exception err3){
      Report.debug( "From testing replacing items-->"+err3.toString() );
    }

    Report.debug("\nAfter replace the list looks like");
    e = a.getEnumerator();
    printNodeForward( e );
    Report.debug("End of dump");


    Report.debug("\nRemoving even positioned items.");
    BasicText n;
    int j = 0;
    for (; j<=10; j++ )
      {
	try{
	  n =(BasicText)a.remove( j );
	  Report.debug("the removed item is-->"+((BasicText)n).getData());  
	}catch(Exception err0){
	}
      }
    Report.debug("End of dump\n\n");

    Report.debug("Testing enumerator -- printing forward.");
    e = a.getEnumerator();
    printNodeForward( e );
    Report.debug("End testing enumerator");

    Report.debug("Testing enumerator -- printing backward.");
    printNodeBackward( e );
    Report.debug("End testing enumerator");

    Report.debug("Testing cloning another list");
    ArrayNodeList nl = new ArrayNodeList( a );

    Report.debug("Here is the old list...");
    printNodeForward( a.getEnumerator() );
    Report.debug("End of old list.");

    Report.debug("Here is the new list...");
    printNodeForward( nl.getEnumerator() );
    Report.debug("End of new list.");
    Report.debug("End cloning another list");
  }


  private static void printNodeForward(NodeEnumerator e)
  {
    BasicText n = (BasicText)e.getFirst();
    while ( !e.atEnd() ){
      Report.debug("the next item is-->"+((BasicText)n).getData());
	n = (BasicText)e.getNext();
    }
    Report.debug("the next item is-->"+((BasicText)n).getData());
  }

  private static void printNodeBackward(NodeEnumerator e)
  {
    BasicText n = (BasicText)e.getLast();
    while ( !e.atStart() ){
      Report.debug("the next item is-->"+((BasicText)n).getData());
	n = (BasicText)e.getPrevious();
    }
    Report.debug("the next item is-->"+((BasicText)n).getData());

  }

 
  /**
  * For testing.
  * 
  */ 
  private static void test2( String filename){
  }
 

}










