////// Get_form.java:  Handler for <get.form>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Run;

import crc.sgml.SGML;
import crc.sgml.Element;

import crc.ds.Table;


/** Handler class for &lt;get-form&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;get.form [name="name"]&gt;
 * <dt>Dscr:<dd>
 *	Get value of NAME, in the FORM context.  
 *	With no name, returns entire form. 
 *  </dl>
 */
public class Get_form extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<get.form [name=\"name\"]>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Get value of NAME, in the FORM context.  \n" +
    "With no name, returns entire form. \n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "name", null);

    Run env = Run.environment(ii);
    Table form = env.transaction.getParameters();
    if (form == null) {
      ii.deleteIt();
      return;
    } else if (name == null || "".equals(name)) {
      // return the whole form
      ii.replaceIt(Util.queryResult(it, new crc.sgml.AttrTable(form))); 
    }
    ii.replaceIt(Util.toSGML(form.at(name)));
  }
}
