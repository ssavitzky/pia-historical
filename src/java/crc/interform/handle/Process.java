////// Process.java:  Handler for <process>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Run;

import crc.sgml.SGML;
import crc.sgml.Text;
import crc.sgml.Token;
import crc.sgml.Tokens;
import crc.sgml.Element;

import crc.ds.Table;

/** Handler class for &lt;process&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;process [tagset=tsname] [copy] [anyway]&gt;content&lt;/process&gt;
 * <dt>Dscr:<dd>
 *	Process CONTENT with optional TAGSET, then either skip or COPY 
 *	the result.  Inside form, only process if submitted or ANYWAY,
 *	else expand to ident field.
 *  </dl>
 */
public class Process extends Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<process [tagset=tsname] [copy] [anyway]>content</process>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Process CONTENT with optional TAGSET, then either skip or COPY\n" +
    "the result.  Inside form, only process if submitted or ANYWAY,\n" +
    "else expand to ident field.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    Element form = (Element)ii.enclosingElement("form");
    if (form != null && ! it.hasAttr("anyway")) {
      // We are inside a <form> element.

      String id = Form.getFormId(form, ii);
      Run env = Run.environment(ii);
      Table query = (env == null)? null : env.transaction.getParameters();
      if (query != null && (id == null || query.at("id") != null)) {
	// The form is being processed.  Mark it.
	form.attr("processed", Token.empty);
	if (it.hasAttr("copy") && ! form.hasAttr("copy")) {
	  Element hack = new Element("set");
	  ((Element)it).attr("anyway", Token.empty);
	  hack.content(new Tokens(it));
	  hack.attr("element", Token.empty);
	  hack.attr("tag", "form");
	  hack.attr("name", "if-processed");
	  ii.pushInto(hack);
	  ii.deleteIt();
	  return;
	}
      } else {
	// The form is not being processed.  Don't execute it.
	ii.deleteIt();
	return;
      }
    }

    String tsname = it.attrString("tagset");
    if (tsname != null) 
      ii.useTagset(tsname);
    if (! it.hasAttr("copy")) ii.setSkipping();
    ii.pushInto(it.content());

    ii.deleteIt();
  }
}
