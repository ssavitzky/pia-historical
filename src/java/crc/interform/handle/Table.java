////// Table.java:  Handler for <table>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Element;
import crc.sgml.TableElement;
import crc.sgml.Tokens;


/** Handler class for &lt;table&gt; 
 *  <p> See <a href="../../InterForm/tag_man.html#table">Manual
 *	Entry</a> for syntax and description.
 */
public class Table extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<table>[<tr> <td> ...]</table>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Tables are syntactic elements accessible as data structures\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {

    //  turn "it"  into an indexable element
    
    Element t = (Element) it;
    t = new TableElement(t);
    ii.replaceIt(t);
  }
}

