// T_BasicElement.java
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
      Report.debug(be.contentString());
      Report.debug("End printing children.");

      NodeEnumerator ce = be.getElementsByTagName( "foobar" );

      Report.debug("Here is the find all list.");
      printChildNodeList( ce );

      BasicElement copyEle = new BasicElement( be );
      copyEle.setTagName("Clone Parent");
      Report.debug("Here is the new list.");
      Report.debug(copyEle.toString());

      Report.debug("Here is the original list.");
      Report.debug(be.toString());



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

      Report.debug( be.toString() );

    }catch(NotMyChildException e){
      Report.debug(e.toString());
    }
  }
}










