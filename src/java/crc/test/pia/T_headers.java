// T_header.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

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







