// Utilities.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.util;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.io.DataOutputStream;
import crc.util.regexp.RegExp;

public class Utilities{

public static synchronized StringBuffer readFrom( String fileName, StringBuffer str ){
    File f;
    FileInputStream source = null;
    StringBuffer s = new StringBuffer("");
    byte[]buffer = new byte[1024];
    int bytesRead;
    String readString;

    try{
      f = new File(fileName);
      source = new FileInputStream( f );
      while(true){
	bytesRead = source.read( buffer, 0, 1024 );
	readString = new String( buffer, 0, 0, bytesRead );

	if(bytesRead == -1) break;
        if(str!=null)
	  str.append( readString );
	else
	  s.append( readString );
      }
      return (str!=null) ? str : s;
      
    }catch(NullPointerException e){
      System.out.println("Invalid file name\n");
    }finally{
      if(source!=null)
	try {
	  source.close();
	}catch(IOException e){
	  System.out.println("Exception from readFrom" + e + "\n");
	}
      return (str!=null) ? str : s; 
    }
  }
  

 /**
   * Given a string, write to file.
   *
   */
  public static synchronized void writeTo( String fileName, String str ){
    File f;
    FileOutputStream fileStream;
    DataOutputStream destination = null;

    if(str == null) return;

    try{
      f = new File(fileName);
      fileStream = new FileOutputStream( f );
      destination = new DataOutputStream( fileStream );
      destination.writeChars( str );
    }catch(Exception e){
      System.out.println("Invalid file name\n");
    }finally{
      if(destination!=null)
	try {
	    destination.close();
	}catch(IOException e){
	   System.out.println("Exception from writeTo" + e + "\n");
        }
    }
  }

  /**
   * Given a string, append to file.
   *
   */
  public static synchronized  void appendTo( String fileName, String str ){
    RandomAccessFile f = null;

    if(str==null) return;

    try{
      f = new RandomAccessFile(fileName, "w");
      long length = f.length();
      f.seek( length );
      f.writeChars( str );
    }catch(NullPointerException e){
      System.out.println("Invalid file name\n");
    }catch(IOException e2){
      System.out.println("Exception occurs in appendTo: " + e2 + "\n");
    }
    finally{
      if(f!=null)
	try {
	     f.close();
	}catch(IOException e){
	   System.out.println("Exception from appendTo" + e + "\n");
        }
    }

  }


  /**
   * Conversion Utilities:
   * convert str to HTML by properly escaping &, <, and >.
   */
  public static synchronized String protect_markup(String str){
    if( str!= null ){
      try{
	RegExp re = new RegExp("&");
	String amp = re.substitute(str,"&amp", true);

	re = new RegExp("<");
	String ampLeft = re.substitute(amp, "&lt", true);

	re = new RegExp(">");
	String ampLeftRight = re.substitute(ampLeft, "&gt", true);

	return ampLeftRight;
      }catch(Exception e){ return null;}
    }else return null;
    
  }

   /**
   * Unescape a HTTP escaped string
   * @param s The string to be unescaped
   * @return the unescaped string.
   */

  public static synchronized String unescape (String s) {
	StringBuffer digitBuf = null;
	int hb = -1;
	int lb = -1;
	StringBuffer sbuf = new StringBuffer () ;
	int l  = s.length() ;
	int ch = -1 ;

	for (int i = 0 ; i < l ; i++) {
	  digitBuf = new StringBuffer();
	    switch (ch = s.charAt(i)) {
	      case '%':
		ch = s.charAt (++i) ;
		digitBuf.append( (char)ch );

		ch = s.charAt (++i) ;
		digitBuf.append( (char)ch );
		
		if( digitBuf.length() > 0 ){
		  int foo = Integer.parseInt( new String( digitBuf ), 16 );
		  sbuf.append( (char)foo );
		}
		break ;
	      case '+':
		sbuf.append (' ') ;
		break ;
	      default:
		sbuf.append ((char) ch) ;
	    }
	}
	return sbuf.toString() ;
    }


}












