// IsPrxReq.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

 
  /**
   *Feature Computers.
   *All take a transaction as their argument, and most return a
   *boolean.  Feature computers may use the utility method
   *transaction->assert(name,value) to set additional features. 
   *
   *By convention, a feature computer "is_foo" computes a feature
   *named "foo". 
   *
   *Default Features: 
   *	These are computed by default when a transaction is created;
   *	they may have to be recomputed if the transaction is modified.
   *
   */
package crc.tf;

import crc.ds.UnaryFunctor;
import java.net.URL;

public final class IsPrxReq implements UnaryFunctor{

  /**
   * 
   * @param object A transaction 
   * @return object boolean
   */
    public Object execute( Object trans ){
      Object zfalse = new Boolean( false );
      Object ztrue  = new Boolean( true );

      if( !trans.isRequest() ) return zfalse;
      URL url = trans.getRequestURL();
      if( !url ) return zfalse;
      
      String host = url.getHost();
      if( host ) 
	String lhost = host.toLowerCase();
      else
	lhost = "";
      String lport = url.getPort().toString();
      
      if( lhost.startsWith("agency") || lhost == "" )
	return zfalse;
      
      if( Pia.getInstance().getPort() == lport && Pia.getInstance().getHost().startsWith( lhost ) )
	return zfalse;

      return ztrue;

      /*
      boolean expected = false;
      String path = url.getFile();
      if( path ) 
	String lpath = path.toLowerCase();
      expected = lpath.startsWith("/http://");

      if( !expected ) return zfalse;
      StreamParser sp = new StreamParser( new StringBufferInputStream( lpath.substring(7) ) );

      try{
	Object o = sp.nextToken();  // whatever comes after /http://
	if( o instanceof String )
	  String zhost = ((String)o).toLowerCase();
	String zport = "80";
	
	if( lpath.startsWith("http://"+zhost+":") ){
	  int lstcolon = lpath.lastIndexOf(":");
	  String sport = lpath.substring(lstcolon+1);
	  try{
	    int portnum = Integer.parseInt( sport );
	    zport = sport;
	  }catch(NumberFormatException e){
	  }
	}

	if( zhost.startsWith("agency") || zhost == "" )
	  return zfalse;
	if( Pia.getInstance().getPort() == zport && Pia.getInstance().getHost().startsWith( zhost ) )
	  return zfalse;

	if( zhost ) return ztrue;
	return zfalse;

      }catch(IOException e1){
	  e.printStackTrace();
	  return zfalse;
      }catch(NoSuchElementException e2){
	return zfalse;
      }
      */


    }
}


