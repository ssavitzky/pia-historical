// T_AttrList.java
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

public class T_AttrList{
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
    AttrList nl = new AttrList();
    Attribute t = null;
    String n = null;
    
    for (int i = 0; i < 10; i++){ 
      n = "Hello"+Integer.toString(i);
      t = new BasicAttribute( n, null );
      nl.setAttribute(n, t);
    }

    Report.debug("\nTesting item");
    int i = 0;
    try{
      for (; i <= 10; i++){ 
	Attribute data = (Attribute)nl.item( i );
	Report.debug("Element at["+Integer.toString(i)+"] is:" + data.getName());
      }
      Report.debug("End Testing item");
    }catch(Exception err1){
      Report.debug("Element at["+Integer.toString(i)+"] does not exist.");
      Report.debug( err1.toString() );
    }

    Report.debug("\nTesting retreiving using name");
    for (int j = 0; j < 10; j++){ 
      n = "Hello"+Integer.toString(j);
      t = (Attribute)nl.getAttribute(n);
      Report.debug("Element at["+ n +"] is:" + t.getName());
    }
    Report.debug("End testing retreiving by name");


    Report.debug("Testing stomping previous elements...");
    n = "Hello"+Integer.toString(5);
    Text vl = new BasicText("valuelist");
    try{
      vl.insertBefore(new BasicText("1.0"), null);
    }catch(Exception e){
    }

    t = new BasicAttribute( n, vl.getChildren() );
    nl.setAttribute(n, t);
    Report.debug("printing attribute list...");
    printAttributeList( nl, "    " );
    Report.debug("End testing stomping previous elements...");


    Report.debug("Testing copying list...");
    AttrList al = new AttrList( nl );
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
    try{
      for (int j = 0; j <= 10; j++){ 
	Attribute data = (Attribute)nl.item( j );
	Report.debug("Element at["+Integer.toString(j)+"] is:" + data.getName());
      }
    }catch(Exception err4){
    }
    Report.debug("End testing enumeration of old list");

    Report.debug("\nTesting enumeration of new list");
    try{
      for (int k = 0; k <= 10; k++){ 
	Attribute data = (Attribute)al.item( k );
	Report.debug("Element at["+Integer.toString(k)+"] is:" + data.getName());
      }
    }catch(Exception err4){
    }
    Report.debug("End testing enumeration of new list");
    Report.debug("printing attribute list of new attributes...");
    printAttributeList( al, "    " );
  }



  private static String printNodeList( Attribute a ){
    NodeList nl = a.getValue();
    long len    = nl.getLength();
    Node n      = null;
    StringBuffer sb = new StringBuffer();

    //Report.debug("the attribute list is-->"+a.toString());
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

  private static void printAttributeList(AttributeList l, String indent ){
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



 
  /**
  * For testing.
  * 
  */ 
  private static void test2( String aclass){
  }
 

}










