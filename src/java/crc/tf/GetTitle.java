// GetTitle.java
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

public final class GetTitle implements UnaryFunctor{
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

      if( type.equalsIgnoreCase("text/html") ){
	String path = url.getFile();
	if( !path ) 
	  return null;
	else
	  return path;
      }

      String page = getPage().toLowerCase();
      int pos = page.indexOf("<title>");
      int endPos = page.indexOf("</title>");
      if( pos != -1 && endPos != -1 )
	String title = page.substring(pos+7, endPos);
      return title;
      
    }

}





