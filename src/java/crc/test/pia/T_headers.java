// T_header.java
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

import crc.pia.Headers;

public class T_headers {
  public static void main(String[] args){
    System.out.println("Test creating a header from the Header class.");
    System.out.println("Input is done by using set operators(setHeader, setContentType,...).");
    System.out.println("Output is a dump of the created header.\n\n");

    try{
      Headers h = new Headers();
      h.setHeader("Host", "napa.crc.ricoh.com:9999");
      h.setContentType("text/html");
      h.setContentLength( 555 );
      h.setHeader("Content-Type", "image/gif");
      System.out.println( h.toString() );
    }catch(Exception e){
      System.out.println( e.toString() );
    }
  }

}







