// Utilities.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

package crc.pia;
import java.io.StreamTokenizer;
import java.io.IOException;

public class Utilities{

public StringBuffer readFrom( String fileName, StringBuffer str ){
    File f;
    FileInputStream source;
    StringBuffer s = "";
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
        if(str)
	  str.append( readString );
	else
	  s.append( readString );
      }
      return (str) ? str : s;
      
    }catch(NullPointerException e){
      System.out.println("Invalid file name\n");
    }finally{
      if(source!=null)
	try {
	  source.close();
	}catch(IOException e){
	  System.out.println("Exception from readFrom" + e + "\n");
	}
      return (str) ? str : s; 
    }
  }
  

 /**
   * Given a string, write to file.
   *
   */
  public void writeTo( String fileName, String str ){
    File f;
    FileOutputStream fileStream;
    DataOutputStream destination;

    if(!str) return;

    try{
      f = new File(fileName);
      fileStream = new FileOutputStream( f );
      destination = new DataOutputStream( fileStream );
      destination.writeChars( str );
    }catch(NullPointerException e){
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
  public void appendTo( String fileName, String str ){
    RandomAccessFile f;

    if(!str) return;

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
  * make a form
  *
  */
  public void makeForm(){
    //help
  }

  /**
   * Conversion Utilities:
   * convert str to HTML by properly escaping &, <, and >.
   */
  public StringBuffer protect_markup(String str){
    if( str ){
      StringBuffer s = new StringBuffer( str );
      substitute( s, '&', "&amp", 'g' );
      substitute( s, '<', "&lt", 'g' );
      substitute( s, '>', "&gt", 'g' );
      return s;
    }else return new StringBuffer("");
    
  }
  
  public void substitue(StringBuffer str, char replaceCh, String replaceWith, char modifier){
    boolean DONE   = false;
    boolean GLOBAL = false;
    int index = -1;
    String orgString;

    if( modifier == 'g' ) GLOBAL = true;

    if (str){
      while( !DONE ){
	orgString = new String( str );
	index= orgString.indexOf(replaceCh, index+1);
	if (index == -1){
	  DONE = true;
	  next;
	}
	str.insert(index, replaceWith);
	if( !GLOBAL ) DONE = true;
      }
    }
  }

}












