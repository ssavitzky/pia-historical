// Utilities.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.util;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.io.DataOutputStream;
import crc.util.regexp.RegExp;

public class Utilities{

public static synchronized byte[] readFrom( String fileName ) throws NullPointerException, FileNotFoundException, IOException{
    File f = null;
    FileInputStream source = null;
    byte[]tmp = new byte[1024];
    byte[]buffer = null;
    int bytesRead = -1;
    int total = 0;

    try{
      f = new File( fileName );
      long len = f.length();
      buffer = new byte[ (int)len ];

      source = new FileInputStream( f );
      for(;;){
	bytesRead = source.read( tmp, 0, 1024 );

	if(bytesRead == -1) break;
	System.arraycopy(tmp, 0, buffer, total, bytesRead);
	total += bytesRead;
      }

      return buffer;  
      
    }catch(NullPointerException e1){
      throw e1;
    }catch(FileNotFoundException e2){
      throw e2;
    }catch(IOException e3){
      throw e3;
    }catch(Exception e4){
      //ArrayindexOutOfBoundsException, ... -- for arraycopy
      return null;
    }finally{
      if( source != null )
	try {
	source.close();
      }catch(IOException e5){
	throw e5;
      }
    }
    
  }
  

  /**
   * Given a string, write to file.
   *
   */
  public static synchronized void writeTo( String fileName, String str )throws IOException{
    File f;
    FileOutputStream fileStream;
    DataOutputStream destination = null;

    if(str == null) return;

    try{
      f = new File(fileName);
      fileStream = new FileOutputStream( f );
      destination = new DataOutputStream( fileStream );
      destination.writeChars( str );
    }catch(IOException e1){
      //either from writeChars or Fileoutputstream creation
      throw e1;
    }finally{
      if(destination!=null)
	try {
	destination.close();
      }catch(IOException e2){
	throw e2;
      }
    }
  }

  /**
   * Given a string, append to file.
   *
   */
  public static synchronized  void appendTo( String fileName, String str ) throws NullPointerException, IOException{
    RandomAccessFile f = null;

    if(str==null) return;

    try{
      f = new RandomAccessFile(fileName, "w");
      long length = f.length();
      f.seek( length );
      f.writeChars( str );
    }catch(NullPointerException e1){
      // bad file name
      throw e1;
    }catch(IOException e2){
      throw e2;
    }
    finally{
      if(f!=null)
	try {
	f.close();
      }catch(IOException e3){
	throw e3;
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












