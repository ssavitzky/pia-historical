////// DL.java:  Handler for <dl>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Element;
import crc.sgml.DescriptionList;
import crc.sgml.Tokens;


/** Handler class for &lt;dl&gt; 
 *  <p> See <a href="../../InterForm/tag_man.html#dl">Manual
 *	Entry</a> for syntax and description.
 */
public class Dl extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<dl>[<dt> <dd> ...]</dl>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "DescriptionLists are syntactic elements accessible as data structures\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {

    //  turn "it"  into an indexable element
    Element myit = (Element) it;
    
    Element t = new DescriptionList(myit);
    ii.replaceIt(t);
  }
}

