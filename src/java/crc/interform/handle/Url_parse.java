////// Subst.java:  Handler for <subst>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Attrs;
import crc.sgml.AttrWrap;
import crc.sgml.AttrTable;
import crc.sgml.Text;

import java.net.URL;


/** Handler class for &lt;url-parse&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#subst">Manual
 *	Entry</a> for syntax and description.
 */
public class Url_parse extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<url-parse url=\"url\" [base=\"base\"]>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Create a table containing the parts of the URL (host,port,path,query,protocol,full(fully qualified form)).\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String url = it.attrString("url");
    String base = it.attrString("base");
    if (ii.missing(ia, "url", url)) return;

    URL u,b;
    
    try{
    if (ii.missing(ia, "base", base)) {
      u = new URL(url);
    } else {
       b = new URL(base);
       u = new URL(b,url);
    }
    }
    catch(Exception e){
      crc.pia.Pia.debug(this,"malformed URL");
      return;
    }
    
    AttrWrap result = new AttrWrap();
    result.attr("host",new Text(u.getHost()));
    result.attr("port",new Text(u.getPort()));
    String p = u.getFile();
    int qs = p.indexOf('?');
    if(qs < 0 ) {
      result.attr("path",new Text(p));
    }
    else{
      try{
	result.attr("path",new Text(p.substring(0,qs)));
        AttrTable t = new AttrTable(p.substring(qs+1));
	
	result.attr("query",new AttrWrap(t));
	
      }
      catch (Exception e)
	{
	  crc.pia.Pia.debug(this,"bad query string");
	}
    }
    
    result.attr("protocol",new Text(u.getProtocol()));
    result.attr("full",new Text(u.toExternalForm()));

    ii.replaceIt(result);
  }
}
