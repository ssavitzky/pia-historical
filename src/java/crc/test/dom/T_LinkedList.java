// T_LinkedList.java
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

public class T_LinkedList{
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
    LinkedList a = new LinkedList();
    for (int i = 0; i < 10; i++){ 
      a.insertElementAt(new Integer(i), i);
      Report.debug("The size of a is-->"+Integer.toString( a.size() ));
    }

    Report.debug("\nTesting elementAt");
    int i = 0;
    try{
      for (; i <= 10; i++){ 
	Integer data = (Integer)a.elementAt( i );
	Report.debug("Element at["+Integer.toString(i)+"] is:" + data.toString());
      }
    Report.debug("End Testing elementAt");
    }catch(Exception err1){
      Report.debug("Element at["+Integer.toString(i)+"] does not exist.");
      Report.debug( err1.toString() );
    }

    try{
      Report.debug("\nTesting inserting at tail position.");


      a.insertElementAt(new Integer(777), 9);
      a.insertElementAt(new Integer(333), 0);
      a.insertElementAt(new Integer(555), 5);
      a.insertElementAt(new Integer(888), 10);

      a.insertElementAt(new Integer(999), 14);

      a.insertElementAt(new Integer(111), 16);
      Report.debug("End testing inserting at tail position.");
    }catch(Exception err1){
      Report.debug("Element at["+Integer.toString(i)+"] does not exist.");
      Report.debug( err1.toString() );
    }


    try{
      Report.debug("\nTesting removing added items.");
      a.removeElementAt( 0 );
      a.removeElementAt( 4 );
      a.removeElementAt( 8 );
      a.removeElementAt( 9 );
      a.removeElementAt( 10 );
      a.removeElementAt( 11 );
      Report.debug("End of testing removing added items.");
    }catch(Exception err2){
      Report.debug( "From testing removing items-->"+err2.toString() );
    }

    try{
      Report.debug("\nTesting replacing items.");
      a.setElementAt( new Integer("111"), 0 );
      a.setElementAt( new Integer("999"), 9 );

      a.setElementAt( new Integer("999"), 10 );
      Report.debug("End of testing replacing.");
    }catch(Exception err3){
      Report.debug( "From testing replacing items-->"+err3.toString() );
    }

    java.util.Enumeration e = a.elements();

    Object foo;
    Object n;
    
    e = a.elements();
    while (e.hasMoreElements())
      {
	n = e.nextElement();
	Report.debug("the next item is-->"+((Integer)n).toString());
        a.removeElementAt( a.indexOf( n ) );
      }
    

    e = a.elements();
    while (e.hasMoreElements())
      System.out.println(e.nextElement());
  }


 
  /**
  * For testing.
  * 
  */ 
  private static void test2( String filename){
  }
 

}










