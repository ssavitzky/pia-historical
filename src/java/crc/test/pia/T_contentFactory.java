// T_contentFactory.java
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


package crc.test.pia;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import crc.pia.ContentFactory;
import crc.pia.Content;

public class T_contentFactory
{
  /**
   * For testing.
   * 
   */ 
  public static void main(String[] args){
    if( args.length == 0 )
      System.out.println("Need file content filename.");

    String filename = args[0];

    System.out.println("Test creating a form content from the FormContent class.");
    System.out.println("Input is read from a file input/requestbody.txt.");
    System.out.println("Output is a dump of the created form content.\n\n");


    ContentFactory cf = new ContentFactory();

    try{
      InputStream in = (new BufferedInputStream
			(new FileInputStream (filename)));
    
      String ztype = "application/x-www-form-urlencoded";
      Content c = cf.createContent(ztype , in );
      System.out.println( c.toString() );
    }catch(Exception e ){
      System.out.println( e.toString() );
    }
    System.exit(0);
  }
  
  
}








