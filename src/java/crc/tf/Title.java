// Title.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

 
package crc.tf;

import java.net.URL;
import crc.ds.UnaryFunctor;
import crc.pia.Transaction;
import crc.pia.Content;
import gnu.regexp.MatchInfo;
import gnu.regexp.RegExp;

public final class Title implements UnaryFunctor{
  private String getPage(Transaction trans){
    Content c = trans.contentObj();
    if( c != null )
      return c.toString();
    else
      return null;
  }

  /**
   * Returns the documentation title of this transaction.
   * @param o  Transaction 
   * @return Object title string; if transaction is of type "text/html" returns the file portion of
   * the transaction's url, get the title from the document page.
   */ 
  public Object execute( Object o ){
      Transaction trans = (Transaction)o;

      if(!trans.isResponse()) return "";

      URL url = trans.requestURL();
      if( url == null ) return "";

      String type = trans.contentType();
      if( type == null ) return "";

      String ltype = type.toLowerCase();
      RegExp re = null;
      MatchInfo mi = null;
      try{
	re = new RegExp("text/html");
	mi = re.match( ltype );
      }catch(Exception e){;}

      if( mi!=null ){
	String path = url.getFile();
	if( path == null ) 
	  return "";
	else
	  return path;
      }

      String mypage = getPage( trans );
      if( mypage == null ) return "";

      String page = mypage.toLowerCase();

      String title = null;
      int pos = page.indexOf("<title>");
      int endPos = page.indexOf("</title>");
      if( pos != -1 && endPos != -1 )
	title = mypage.substring(pos+"<title>".length(), endPos);
      return title;
      
    }

}













