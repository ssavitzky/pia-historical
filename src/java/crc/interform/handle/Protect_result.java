////// Protect_result.java:  Handler for <protect_result>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Text;



/** Handler class for &lt;protect-result&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#protect-result">Manual
 *	Entry</a> for syntax and description.
 */
public class Protect_result extends Protect {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<protect-result [markup]>content</protect-result>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Expand CONTENT and protect the result from further expansion.\n" +
    "Optionally protect MARKUP by converting special characters to\n" +
    "entities.\n" +
"";
 
  public String note() { return noteStr; }
  static String noteStr=
     "The only reason for having &lt;protect&gt; and\n" +
     "&lt;protect-result&gt; in separate classes is so each can have\n" +
     "its own syntax description.\n" +
"";

  /** Legacy action.  Inherited from protect: this fakes out the reporting. */
  //public boolean action(
}

