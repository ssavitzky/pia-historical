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
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;protect-result [markup]&gt;content&lt;/protect-result&gt;
 * <dt>Dscr:<dd>
 *	Expand CONTENT and protect the result from further expansion.
 *	Optionally protect MARKUP by converting special characters to
 *	entities.
 *  </dl>
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
 
  /** The only reason for having &lt;protect&gt; and
   *	&lt;protect-result&gt; in separate classes is so each can have
   *	its own syntax description. */
  public void note() {};
}
