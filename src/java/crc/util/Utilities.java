// Utilities.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

/**
 * This class contains only static functions; all are general-purpose 
 *	utilities, mainly file-handling and data-format conversion.
 */

package crc.util;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InvalidClassException;
import java.io.StreamCorruptedException;
import java.io.OptionalDataException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import java.lang.ClassNotFoundException;

import java.io.FileReader;
import java.io.FileWriter;

import crc.gnu.regexp.RegExp;

import crc.ds.List;
import crc.ds.Registered;
import crc.ds.Table;

public class Utilities {

  /************************************************************************
  ** String and Byte Array I/O:
  ************************************************************************/

  /**
   * Read an entire file and return it as a String.
   */
  public static String readStringFrom( String filename )
       throws NullPointerException, FileNotFoundException, IOException{

      byte[] fromFile = readFrom( filename );
      String data = new String ( fromFile );
      return data;
  }

  /**
   * Read from an InputStream and return it as a String.
   */
  public static String readStringFrom( InputStream stream )
       throws NullPointerException, FileNotFoundException, IOException
  {
    StringBuffer buffer = new StringBuffer();
    int i;

    // === very inefficient; should use InputStreamReader ===
    
    for ( i = stream.read(); i != -1; i = stream.read()) {
      buffer.append((char)i);
    }
    return buffer.toString();
  }

  /**
   * Read to the end of a file and return it as a byte array.
   */
  public static synchronized byte[] readFrom( String fileName )
       throws NullPointerException, FileNotFoundException, IOException
  {
    FileInputStream source = null;
    File f = null;
    byte[]tmp = new byte[1024];
    byte[]buffer = null;
    int bytesRead = -1;
    int total = 0;

    try {
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
   * Write a String to a file.
   */
  public static synchronized void writeTo( String fileName, String str )
       throws IOException{
    File f;
    FileWriter destination = null;

    if(str == null) return;
    try{
      f = new File(fileName);
      destination = new FileWriter( f );
      destination.write( str );
      destination.flush();
    }catch(IOException e1){
      // either from open or write.
      throw e1;
    }finally{
      if(destination!=null)
	destination.close();
    }
  }

  /**
   * Append a String to a file.
   *
   */
  public static synchronized  void appendTo( String fileName, String str )
       throws NullPointerException, IOException{
    RandomAccessFile f = null;

    if(str == null) return;

    try{
      f = new RandomAccessFile(fileName, "rw");
      long length = f.length();
      f.seek( length );
      f.write( str.getBytes() );
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


  /************************************************************************
  ** File copy and replace:
  ************************************************************************/

  /**
   * Copy a file.
   */
  public static synchronized void copyFile( String srcFileName,
					    String dstFileName )
       throws IOException
  {
    copyFile(new File(srcFileName), new File(dstFileName));
  }

  public static synchronized void copyFile( File src, File dst )
       throws IOException{

    System.err.println("Substituting "+src.getPath() + "->" + dst.getPath());

    FileWriter destination = null;
    FileReader source      = null;

    try{
      source      = new FileReader( src );
      destination = new FileWriter( dst );
      for (int i = source.read(); i != -1; i = source.read()) {
	destination.write((char)i);
      }
      destination.flush();
    }catch(IOException e1){
      // either from open or write.
      throw e1;
    }finally{
      if (source != null)      source.close();
      if (destination != null) destination.close();
    }
  }


  /** Copy a file, replacing keys in the <code>subst</code> table with their
   *	values.  Look for keys only after an instance of <code>beg</code>
   *	and end them with <code>end</code>. 
   */
  public static synchronized void substFile( File src, File dst,
					     crc.sgml.Attrs subst, char beg, char end)
       throws IOException
  {

    //System.err.println("Substituting "+src.getPath() + "->" + dst.getPath());
    //System.err.println("Table = " +subst.toString());

    FileWriter destination = null;
    FileReader source      = null;
    StringBuffer buf 	   = new StringBuffer(100);

    try{
      source      = new FileReader( src );
      destination = new FileWriter( dst );
      for (int i = source.read(); i != -1; i = source.read()) {
	if (i == (int)beg) {
	  buf.setLength(0);
	  for (i = source.read();
	       i != -1 && i != (int)end;
	       i = source.read()) {
	    buf.append((char)i);
	  } 
	  Object o = (buf.length() == 0)? null : subst.attr(buf.toString());
	  //System.err.println("`"+beg+buf.toString()+end+"'->"+o);
	  if (o != null) {
	    destination.write(o.toString());
	  } else {
	    destination.write(beg);
	    destination.write(buf.toString());
	    if (i != 0) destination.write((char)i);
	  }
	} else {
	  destination.write((char)i);
	}
      }
      destination.flush();
    }catch(IOException e1){
      // either from open or write.
      throw e1;
    }finally{
      if (source != null)      source.close();
      if (destination != null) destination.close();
    }
  }


  /************************************************************************
  ** Object I/O:
  ************************************************************************/

  /**
   * Read an Object from a file.  If the object implements the Registered
   *	interface it is registered.  If the object is a List, every object
   *	on the list is registered if necessary.
   *
   *	@see crc.ds.List
   *	@see crc.ds.Registered
   */
  public static synchronized Object readObjectFrom( String fileName )
       throws NullPointerException, FileNotFoundException, IOException
  {
    FileInputStream f = null;
    ObjectInputStream source = null;

    try {
      f = new FileInputStream( fileName );
      source = new ObjectInputStream(f);
      return register(source.readObject());
    }catch(NullPointerException e1){
      throw e1;
    }catch(FileNotFoundException e2){
      throw e2;
    }catch(ClassNotFoundException e){
      return null; //throw e;
    }catch(InvalidClassException e){
      return null; //throw e;
    }catch(StreamCorruptedException e){
      return null; //throw e;
    }catch(OptionalDataException  e){
      return null; //throw e;
    }catch(IOException e3){
      throw e3;
    }finally{
      try {
	if (source != null) source.close();
	else if (f != null) f.close();
      }catch(IOException e5){
	throw e5;
      }
    }
    
  }
  
  /**
   * Read an entire file into a List.  Any objects that implement the 
   *	Registered interface are registered.  Lists in the input file are
   *	quietly appended.
   *
   *	@see crc.ds.List
   *	@see crc.ds.Registered
   */
  public static synchronized List readObjectsFrom( String fileName )
       throws NullPointerException, FileNotFoundException, IOException
	 //, ClassNotFoundException, InvalidClassException, 
	 //StreamCorruptedException, OptionalDataException 
  {
    FileInputStream f = null;
    ObjectInputStream source = null;
    List list = new List();

    try {
      f = new FileInputStream( fileName );
      source = new ObjectInputStream(f);
      for ( ;; ) {
	Object o = source.readObject();
	if (o instanceof List) list.append((List)o);
	else		       list.push(o);
      }
    }catch(NullPointerException e1){
      throw e1;
    }catch(FileNotFoundException e2){
      throw e2;
    }catch(ClassNotFoundException e){
      report(e); //throw e;
    }catch(InvalidClassException e){
      report(e); //throw e;
    }catch(StreamCorruptedException e){
      report(e); //throw e;
    }catch(OptionalDataException  e){
      report(e); //throw e;
    }catch(java.io.EOFException e){
      if ( list.nItems() == 0 ) {
	report(e);
	throw e;
      } // expected.
    }catch(IOException e){
      if ( list.nItems() > 0 ) report(e);
      else throw e;
    }finally{
      try {
	if (source != null) source.close();
	else if (f != null) f.close();
      }catch(IOException e5){
	throw e5;
      }
    }
    register(list);
    return list;
  }
  
  /** 
   * Report an exception.
   */
  public static void report(Exception e) {
    System.err.println(e.toString() + " " + e.getMessage());
    e.printStackTrace();
  }

  /** 
   * Obtain a complete report on an exception as a String;
   */
  public static String reportString(Exception e) {
    java.io.StringWriter s = new java.io.StringWriter();
    s.write(e.toString() + " " + e.getMessage());
    e.printStackTrace(new java.io.PrintWriter(s));
    return s.toString();
  }

  /**
   * Register an object if it needs it.  
   *	If the object is a List, register every object in its contents.
   */
  public static Object register(Object obj) {
    if (obj instanceof List) {
      List list = (List) obj;
      for (int i = 0; i < list.nItems(); ++i) {
	Object o = list.at(i);
	if (o instanceof Registered) ((Registered)o).register();
      }
      return list;
    } else {
      if (obj instanceof Registered) ((Registered)obj).register();
      return obj;
    }
  }

  /**
   * Write an Object to a file.
   */
  public static synchronized void writeObjectTo( String fileName, Object o )
       throws IOException{

    FileOutputStream f = null;

    if (o == null) return;
    try{
      f = new FileOutputStream( fileName );
      ObjectOutputStream destination = new ObjectOutputStream(f);
      destination.writeObject( o );
      destination.flush();
      destination.close();
      f.flush();
      f.close();
      f = null;
    }catch(IOException e1){
      throw e1;
    }finally{
      if(f!=null) f.close();
    }
  }

  /**
   * Append an Object to a file.
   *
   */
  public static synchronized  void appendObjectTo( String fileName, Object o )
       throws NullPointerException, IOException{
    RandomAccessFile f = null;

    if (o==null) return;

    try{
      f = new RandomAccessFile(fileName, "rw");
      long length = f.length();
      f.seek( length );
      FileOutputStream ff = new FileOutputStream(f.getFD());
      ObjectOutputStream destination = new ObjectOutputStream(ff);
      destination.writeObject( o );
      destination.flush();
      destination.close();
      f = null;
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


  /************************************************************************
  ** Conversion:
  ************************************************************************/

  /**
   * Conversion Utilities:
   * convert str to HTML by properly escaping &, <, and >.
   */
  public static synchronized String protect_markup(String str){
    if( str!= null ){
      try{
	RegExp re = new RegExp("&");
	String amp = re.substitute(str,"&amp;", true);

	re = new RegExp("<");
	String ampLeft = re.substitute(amp, "&lt;", true);

	re = new RegExp(">");
	String ampLeftRight = re.substitute(ampLeft, "&gt;", true);

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

    crc.pia.Pia.debug( "The input string-->"+s);

	StringBuffer digitBuf = null;
	int hb = -1;
	int lb = -1;
	StringBuffer sbuf = new StringBuffer () ;
	int len  = s.length() ;
	crc.pia.Pia.debug( "The string len is-->"+ Integer.toString( len ));
	int ch = -1 ;

	for (int i = 0 ; i < len ; i++) {
	  
	  digitBuf = new StringBuffer();
	  switch (ch = s.charAt(i)) {
	  case '%':
	      if( i <= len - 3 ){
		digitBuf.append( s.charAt(++i) );
		digitBuf.append( s.charAt(++i) );
		
		if( digitBuf.length() > 0 ){
		  int foo = Integer.parseInt( new String( digitBuf ), 16 );
		  sbuf.append( (char)foo );
		}
	      } else {
		sbuf.append((char)ch);
	      }
	      break ;
	  case '+':
	    sbuf.append (' ') ;
	    break ;
	  default:
	    sbuf.append ((char) ch) ;
	  }
	}
	crc.pia.Pia.debug( "The out string-->"+sbuf.toString());
	return sbuf.toString() ;
  }
  

  public static void main(String[] args){
    String fn = args[0];
    try{
      Utilities.writeTo( fn, "Hello world." );
      Utilities.appendTo( fn, " Baguette." );
    }catch(Exception e){
      System.out.println( e.toString() );
    }
  }


}












