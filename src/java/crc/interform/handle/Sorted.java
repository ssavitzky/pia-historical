////// Sorted.java:  Handler for <sorted>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Tokens;
import crc.sgml.Text;

import crc.ds.List;
import crc.ds.Association;

/** Handler class for &lt;sorted&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;sorted [case][text][numeric][reverse]&gt;item, ...&lt;/sorted&gt;
 * <dt>Dscr:<dd>
 *	Test whether items in CONTENT are sorted.  Optionally 
 *	CASE (sensitive), TEXT, NUMERIC, REVERSE.
 */
public class Sorted extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<sorted [case][text][numeric][reverse]>item, ...</sorted>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Test whether items in CONTENT are sorted.  Optionally \n" +
    "CASE (sensitive), TEXT, NUMERIC, REVERSE.\n" +
    "\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {

    boolean numeric = it.hasAttr("numeric");
    boolean casesens= it.hasAttr("case");
    boolean text    = it.hasAttr("text");
    boolean reverse = it.hasAttr("reverse");

    boolean result = true;

    List list = Util.assocItems(it, numeric, casesens, text);
    
    for (int i = 1; i < list.nItems(); ++i) {
      int c = ((Association)list.at(i)).compareTo((Association)list.at(i-1));
      if ( (reverse && (c > 0)) || (!reverse && (c < 0)) ) {
	result = false;
	break;
      }
    }

    if (result) {
      ii.replaceIt(it.hasAttr("iftrue")? it.attr("iftrue") : new Text("1"));
    } else {
      ii.replaceIt(it.hasAttr("iffalse")? it.attr("iffalse") : null);
    }
  }
}
