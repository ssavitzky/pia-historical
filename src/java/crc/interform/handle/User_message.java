////// User_message.java:  Handler for <user-message>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.sgml.SGML;


/** Handler class for &lt;user-message&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#user-message">Manual
 *	Entry</a> for syntax and description.
 */
public class User_message extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<user-message>content</user-message>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Display CONTENT as a message to the user.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    ii.message(it.contentString());
    ii.deleteIt();
  }
}
