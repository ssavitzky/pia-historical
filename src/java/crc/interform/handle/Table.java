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


/** Handler class for &lt;dl&gt; 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;dl&gt;
 * <dt>Dscr:<dd>
 *	Tables become indexable data structures (transparently).
 *      if &amp;foo; is a table, then &amp;foo.bar; returns the row or column associated with
 *      th bar (if any).
 *  </dl>
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

