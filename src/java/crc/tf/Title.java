// Title.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

 
package crc.tf;

import java.net.URL;
import crc.ds.UnaryFunctor;
import crc.pia.Transaction;
import crc.pia.Content;

import crc.tf.TFComputer;

public final class Title extends TFComputer {
  private String getPage(Transaction trans){
    Content c = trans.contentObj();
    if( c != null )
      return c.toString();
    else
      return null;
  }

  /**
   * Returns the title element of this transaction's content document.
   *	Returns null if the transaction is not a response, and the
   *	document's path if it is not text.
   */ 
  public Object computeFeature(Transaction trans) {

      if(!trans.isResponse()) return "";

      /* we will use the path as a fallback. */

      URL url = trans.requestURL();
      if( url == null ) return "";
      String path = url.getFile();
      if( path == null ) path = "";

      String type = trans.contentType();
      if( type == null ) return "";

      String ltype = type.toLowerCase();
      if (! ltype.startsWith("text/html")) return path;

      String mypage = getPage( trans );
      if( mypage == null ) return path;

      String page = mypage.toLowerCase();

      String title = null;
      int pos = page.indexOf("<title>");
      int endPos = page.indexOf("</title>");
      if( pos != -1 && endPos != -1 )
	title = mypage.substring(pos+"<title>".length(), endPos);
      return title;
      
    }

}













