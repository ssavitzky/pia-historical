// T_ChildNodeList.java
// $Id$

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/


package crc.test.dom;

import crc.dom.*;

public class T_ChildNodeList{
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

      ChildNodeList cnl = (ChildNodeList)be.getChildren();
      Report.debug("\nList length is -->"+ cnl.getLength());
      Report.debug("\nTesting item...");
      for(int j=0; j<=10;j++){
	try{
	  Node an = cnl.item( j );
	  Report.debug("Node type is: "+ Integer.toString(an.getNodeType()));
	}catch(Exception err1){
	  Report.debug("index out of bound");
	}
      }
      Report.debug("End testing item...");
      

      Report.debug("\nTesting ChildNodeList enumerator print forward....");
      ne = be.getChildren().getEnumerator();
      printChildNodeListF( ne );
      Report.debug("End testing enumerator.");

      Report.debug("\nTesting ChildNodeList enumerator print backward...");
      ne = be.getChildren().getEnumerator();
      printChildNodeListB( ne );
      Report.debug("End testing enumerator.");


    }catch(NotMyChildException e){
      Report.debug(e.toString());
    }

    Report.debug("Testing cloning another list");
    ChildNodeList nl = new ChildNodeList( be.getChildren() );
    Report.debug("The new node list has this many children-->"+Integer.toString((int)(nl.getLength())));

    Report.debug("Here is the old list...");
    printChildNodeListF( be.getChildren().getEnumerator() );
    Report.debug("End of old list.");

    Report.debug("Here is the new list...");
    printChildNodeListF( nl.getEnumerator() );
    Report.debug("End of new list.");
    Report.debug("End cloning another list");

  }

  private static void printChildNodeListF( NodeEnumerator ne ){
    Node n = ne.getFirst();
    
    while( !ne.atEnd() ){
      if( n instanceof Element )
	Report.debug( ((Element)n).getTagName() );
      else
	Report.debug( Integer.toString( n.getNodeType() ) );
      n = ne.getNext();
    }

    if( n instanceof Element )
      Report.debug( ((Element)n).getTagName() );
    else
      Report.debug( Integer.toString( n.getNodeType() ) );
  }

  private static void printChildNodeListB( NodeEnumerator ne ){
    Node n = ne.getLast();
    
    while( !ne.atStart() ){
      if( n instanceof Element )
	Report.debug( ((Element)n).getTagName() );
      else
	Report.debug( Integer.toString( n.getNodeType() ) );
      n = ne.getPrevious();
    }

    if( n instanceof Element )
      Report.debug( ((Element)n).getTagName() );
    else
      Report.debug( Integer.toString( n.getNodeType() ) );

  }

  private static void test2( String foobar ){
  }
 
  private static void test3( String foobar ){
  }
 
  private static void test4( String foobar ){
  }
 





}










