// test T_indexPath.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.test.ds;

import crc.ds.List; 
import crc.ds.Table;
import crc.sgml.SGML;
import crc.sgml.Util;
import crc.sgml.AttrWrap;
import crc.sgml.Text;
import crc.sgml.Token;
import crc.sgml.Tokens;
import crc.sgml.Element;

import crc.sgml.DescriptionList;

import java.util.Enumeration;
import java.util.Vector;
import java.util.StringTokenizer;

import crc.pia.Pia;
import crc.ds.Index;

public class T_indexPath{
  private static void printusage(){
    System.out.println("Needs to know what kind of doc# and path");
    System.out.println("Here is the command for interactive--> java crc.pia.T_index doc# path");
  }

  public static SGML makeHtml0(){
    SGML foo = new Element();
    
    SGML u1 = new Element("ul");
    SGML u2 = new Element("ul"); 
    SGML u3 = new Element("ul"); 
    
    SGML c1 = new Element("li");
    c1.append("Hello world");
    
    SGML c2 = new Element("li");
    c2.append("I love u. U love me");
    
    SGML c3 = new Element("li");
    c3.append("Buddy love");


    u1.append( c1 );
    u2.append( c2 );
    u3.append( c3 );
    foo.append( u1 );
    foo.append( u2 );
    foo.append( u3 );

    return foo;
  }
  
  public static void main(String[] argv){
    String s = null;
    Pia.debug( true );
    if( argv.length == 0 || argv.length > 2  ){
      printusage();
      System.exit( 1 );
    }

    SGML foo = new Element("");
    int what = 0;

    try{
      what = Integer.parseInt( argv[0] );
    }catch(Exception e){}
    
    switch( what ){
    case 0:
      foo = makeHtml0();
      break;
    default:
      foo = new Element("table", "");
      break;
    }
	
    System.out.println("original foo-->"+foo.toString());
    try{
      SGML result;
      if( argv.length != 1 ){
	Index i = new Index( argv[1] );
	result = i.path( foo );
	System.out.println("Here is the result-->"+result.toString());
      }
      else if (what == 0) // testing normal retrieval
	makePath( foo );
      else
	buildHtml();
      

    }catch(Exception e){
    } 

  }

  public static void makePath(SGML foo){
    SGML result;
    
    try{
      Index i = new Index( ".table" );
      result = i.path( foo );
      System.out.println("Here is the result-->"+result.toString());
      System.out.println("========================================");
      i = new Index( "ul-2-2.li-2-2" );
      result = i.path( foo );
      System.out.println("Here is the result after putting another ul-->"+foo.toString());

    }catch(Exception e){
      e.printStackTrace();
    }

    
  }

  public static void buildHtml(){
    SGML foo;
    System.out.println("In buldHtml");
    try{
      foo = new Element("");
      Index i = new Index( "ul" );
      i.path( foo );
      
      i = new Index( "ul-2-2" );
      i.path( foo );

      i = new Index( "ul-1-1.li" );
      i.path( foo );

      i = new Index( "ul-2-2.li" );
      i.path( foo );

      System.out.println("Here is foo-->"+foo.toString());
      
    }catch(Exception e){
      e.printStackTrace();
    }

  }
  
}
