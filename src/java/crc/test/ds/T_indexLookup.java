// test T_indexLookup.java
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

public class T_indexLookup{
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
  
  public static SGML makeHtml1(){
    SGML foo = new Element();
    SGML dl = new DescriptionList(new Element("dl"));
    
    SGML dt1 = new Element("dt", "aaa");
    SGML dt2 = new Element("dt", "bbb"); 
    
    SGML dd1 = new Element("dd", "no food");
    SGML dd2 = new Element("dd", "in nirvana");
    
    dl.append( dt1 );
    dl.append( dd1 );
    dl.append( dt2 );
    dl.append( dd2 );

    foo.append( dl );

    return foo;
  }

  public static SGML makeHtml2(){
    SGML foo = new Element();
    SGML dl = new DescriptionList(new Element("dl"));
    
    SGML dt1 = new Element("dt", "aaa");
    SGML dt2 = new Element("dt", "bbb"); 
    
    SGML dd1 = new Element("dd", "no food");
    SGML dd2 = new Element("dd", "in nirvana");
    
    //dl.append( dt1 );
    dl.append( dd1 );
    dl.append( dt2 );
    dl.append( dd2 );

    foo.append( dl );

    return foo;
  }

  public static SGML makeHtml3(){
    SGML foo = new Element();
    SGML dl = new DescriptionList(new Element("dl"));
    
    SGML dt1 = new Element("dt", "aaa");
    SGML dt2 = new Element("dt", "bbb"); 
    
    SGML dd1 = new Element("dd", "no food");
    SGML dd2 = new Element("dd", "in nirvana");
    
    dl.append( dt1 );
    //dl.append( dd1 );
    dl.append( dt2 );
    dl.append( dd2 );

    foo.append( dl );

    return foo;
  }


  public static SGML makeHtml4(){
    SGML foo = new Element();
    SGML dl = new DescriptionList(new Element("dl"));
    
    SGML dt1 = new Element("dt", "aaa");
    SGML dt2 = new Element("dt", "bbb"); 
    
    SGML dd1 = new Element("dd", "no food");
    SGML dd2 = new Element("dd", "in nirvana");
    SGML dd3 = new Element("dd", "Barney is a dork");
    
    dl.append( dt1 );
    dl.append( dd1 );
    dl.append( dd2 );
    dl.append( dt2 );
    dl.append( dd3 );

    foo.append( dl );

    return foo;
  }
  
  public static SGML makeHtml5(){
    SGML foo = new Element();
    SGML dl = new DescriptionList(new Element("dl"));
    
    SGML dt1 = new Element("dt", "aaa");
    SGML dt2 = new Element("dt", "aaa"); 
    
    SGML dd1 = new Element("dd", "no food");
    SGML dd2 = new Element("dd", "in nirvana");
    
    dl.append( dt1 );
    dl.append( dd1 );
    dl.append( dt2 );
    dl.append( dd2 );

    foo.append( dl );

    return foo;
  }


  public static void main(String[] argv){
    String s = null;
    Pia.debug( true );
    if( argv.length == 0 || argv.length > 2  ){
      printusage();
      System.exit( 1 );
    }

    SGML foo = new Text("");
    int what = 0;

    try{
      what = Integer.parseInt( argv[0] );
    }catch(Exception e){}
    
    switch( what ){
    case 0:
      foo = makeHtml0();
      break;
    case 1 :
      foo = makeHtml1();
      break;
    case 2:
      foo = makeHtml2();
      break;
    case 3:
      foo = makeHtml3();
      break;
    case 4:
      foo = makeHtml4();
      break;
    case 5:
      foo = makeHtml5();
      break;
    default:
      foo = new Element("table", "");
      break;
    }
	
    System.out.println("original foo-->"+foo.toString());
    try{
      SGML result;
      if( argv.length != 1 ){
	result = Index.get(argv[1], foo);
	System.out.println("Here is the result-->"+result.toString());
      }
      else if (what == 0) // testing normal retrieval
	runStatic( foo );
      else
	runDltest();
      

    }catch(Exception e){
    } 

  }

  public static void runDltest(){
    SGML result;
    SGML foo;

    try{
      foo = makeHtml1();      
      System.out.println("original foo-->"+foo.toString());

      System.out.println("\n\n----------running case 1--------------------");
      System.out.println("Testing getting aaa, bbb, keys, and values");
      result = Index.get("dl.aaa", foo);
      System.out.println("Here is the result-->"+result.toString());

      System.out.println("\n+++++++++++\n");
      result = Index.get("dl.bbb", foo);
      System.out.println("Here is the result-->"+result.toString());

      System.out.println("\n+++++++++++\n");
      result = Index.get("dl.keys", foo);
      System.out.println("Here is the result-->"+result.toString());

      System.out.println("\n+++++++++++\n");
      result = Index.get("dl.values", foo);
      System.out.println("Here is the result-->"+result.toString());

      System.out.println("---------------end of running case 1-----------------");


      foo = makeHtml2();      
      System.out.println("original foo-->"+foo.toString());

      System.out.println("\n\n----------running case 2--------------------");
      System.out.println("Testing getting 1st dd, bbb, keys, and values");
      result = Index.get("dl.dd-1-1", foo);
      System.out.println("Here is the result-->"+result.toString());

      System.out.println("\n+++++++++++\n");
      result = Index.get("dl.bbb", foo);
      System.out.println("Here is the result-->"+result.toString());

      System.out.println("\n+++++++++++\n");
      result = Index.get("dl.keys", foo);
      System.out.println("Here is the result-->"+result.toString());

      System.out.println("\n+++++++++++\n");
      result = Index.get("dl.values", foo);
      System.out.println("Here is the result-->"+result.toString());

      System.out.println("---------------end of running case 2-----------------");

      foo = makeHtml3();      
      System.out.println("original foo-->"+foo.toString());

      System.out.println("\n\n----------running case 3--------------------");
      System.out.println("Testing getting aaa (should be null), bbb, keys, and values");
      result = Index.get("dl.aaa", foo);
      System.out.println("Here is the result-->"+result.toString());

      System.out.println("\n+++++++++++\n");
      result = Index.get("dl.bbb", foo);
      System.out.println("Here is the result-->"+result.toString());

      System.out.println("\n+++++++++++\n");
      result = Index.get("dl.keys", foo);
      System.out.println("Here is the result-->"+result.toString());

      System.out.println("\n+++++++++++\n");
      result = Index.get("dl.values", foo);
      System.out.println("Here is the result-->"+result.toString());

      System.out.println("---------------end of running case 3-----------------");

      foo = makeHtml4();      
      System.out.println("original foo-->"+foo.toString());

      System.out.println("\n\n----------running case 4--------------------");
      System.out.println("Testing getting aaa (should be dups), bbb, keys, and values");
      result = Index.get("dl.aaa", foo);
      System.out.println("Here is the result-->"+result.toString());

      System.out.println("\n+++++++++++\n");
      result = Index.get("dl.keys", foo);
      System.out.println("Here is the result-->"+result.toString());

      System.out.println("\n+++++++++++\n");
      result = Index.get("dl.values", foo);
      System.out.println("Here is the result-->"+result.toString());

      System.out.println("---------------end of running case 4-----------------");


      foo = makeHtml5();      
      System.out.println("original foo-->"+foo.toString());

      System.out.println("\n\n----------running case 5--------------------");
      System.out.println("Testing getting aaa (dup keys--return all contents), bbb, keys, and values");
      result = Index.get("dl.aaa", foo);
      System.out.println("Here is the result-->"+result.toString());

      System.out.println("---------------end of running case 5-----------------");

    }catch(Exception e){
      e.printStackTrace();
    }

    
  }

  public static void runStatic(SGML foo){
    SGML result;
    System.out.println("original foo-->"+foo.toString());
    try{
      System.out.println("\n\n--------------------------------");
      System.out.println("Testing xxx");
      result = Index.get("ul", foo);
      System.out.println("Here is the result-->"+result.toString());
      System.out.println("--------------------------------");
      
      System.out.println("\n\n--------------------------------");
      System.out.println("Testing xxx-");
      result = Index.get("ul-", foo);
      System.out.println("Here is the result-->"+result.toString());
      System.out.println("--------------------------------");
      
      System.out.println("\n\n--------------------------------");
      System.out.println("Testing xxx--");
      result = Index.get("ul--", foo);
      System.out.println("Here is the result-->"+result.toString());
      System.out.println("--------------------------------");
      
      System.out.println("\n\n--------------------------------");
      System.out.println("Testing xxx-1-");
      result = Index.get("ul-1-", foo);
      System.out.println("Here is the result-->"+result.toString());
      System.out.println("--------------------------------");
      
      System.out.println("\n\n--------------------------------");
      System.out.println("Testing xxx-start#-end#");
      result = Index.get("ul-2-3", foo);
      System.out.println("Here is the result-->"+result.toString());
      System.out.println("--------------------------------");
      
      System.out.println("\n\n--------------------------------");
      System.out.println("Testing xxx-start#");
      result = Index.get("ul-2", foo);
      System.out.println("Here is the result-->"+result.toString());
      System.out.println("--------------------------------");
      
      System.out.println("\n\n--------------------------------");
      System.out.println("Testing xxx--end#");
      result = Index.get("ul--3", foo);
      System.out.println("Here is the result-->"+result.toString());
      System.out.println("--------------------------------");
      
      System.out.println("\n\n--------------------------------");
      System.out.println("Testing xxx-start#-");
      result = Index.get("ul-2-", foo);
      System.out.println("Here is the result-->"+result.toString());
      System.out.println("--------------------------------");
      
      System.out.println("\n\n--------------------------------");
      System.out.println("Testing xxx.1");
      System.out.println("Same as ul.any-1-; that is a list of first Lis");
      result = Index.get("ul.1", foo);
      System.out.println("Here is the result-->"+result.toString());
      System.out.println("--------------------------------");
      
      System.out.println("\n\n--------------------------------");
      System.out.println("Testing xxx..");
      System.out.println("Same as ul.any-1- and get the first one");
      result = Index.get("ul.1..1", foo);
      System.out.println("Here is the result-->"+result.toString());
      System.out.println("--------------------------------");
      
      System.out.println("\n\n--------------------------------");
      System.out.println("Testing xxx.");
      System.out.println("Same as current content with ul tags");
      result = Index.get("ul.1..1", foo);
      System.out.println("Here is the result-->"+result.toString());
      System.out.println("--------------------------------");
      
      System.out.println("\n\n--------------------------------");
      System.out.println("Testing xxx.1..1.Text");
      System.out.println("Same as ul.any-1- and get the first one and then its text");
      result = Index.get("ul.1..1.Text", foo);
      System.out.println("Here is the result-->"+result.toString());
      System.out.println("--------------------------------");
    }catch(Exception e){
      e.printStackTrace();
    }
  }
  
}
