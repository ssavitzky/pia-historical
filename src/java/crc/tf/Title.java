// Title.java
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
   * @return the title of an HTML page, if it has one.
   * @return the URL if the content-type is not HTML.
   */
package crc.tf;

import crc.ds.UnaryFunctor;
import crc.util.regexp.MatchInfo;
import crc.util.regexp.RegExp;

public final class Title implements UnaryFunctor{
  private String getPage(){
      in = new DataInputStream( trans.getContentObj().source() );
      String line;
      try{
	StringBuffer buffer = new StringBuffer("");
	while(true){
	  line = in.readLine();
	  if(line == null )break;
	  buffer.append( line );
	}
      }catch(IOException e){
	System.err.println( e );
      }
      return new String( buffer );

  }

  public Object execute( Object trans ){
      DataInputStream in;

      if(!trans.isResponse()) return null;

      String url = trans.getRequestURL();
      if( !url ) return null;

      String type = trans.getContentType();
      if( !type ) return null;

      String ltype = type.toLowerCase();
      RegExp re = new RegExp("text/html");
      MatchInfo mi = re.match( ltype );
      if( !mi ){
	String path = url.getFile();
	if( !path ) 
	  return null;
	else
	  return path;
      }

      String page = getPage().toLowerCase();
      if( !page ) return null;

      String title;
      int pos = page.indexOf("<title>");
      int endPos = page.indexOf("</title>");
      if( pos != -1 && endPos != -1 )
	title = page.substring(pos+"<title>".length(), endPos);
      return title;
      
    }

}





