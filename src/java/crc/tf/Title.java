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

import java.net.URL;
import crc.ds.UnaryFunctor;
import crc.pia.Transaction;
import crc.pia.Content;
import crc.util.regexp.MatchInfo;
import crc.util.regexp.RegExp;

public final class Title implements UnaryFunctor{
  private String getPage(Transaction trans){
    Content c = trans.contentObj();
    if( c != null )
      return c.toString();
    else
      return null;
  }

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













