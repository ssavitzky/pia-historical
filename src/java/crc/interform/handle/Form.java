////// Form.java:  Handler for <form>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Text;
import crc.sgml.Element;
import crc.sgml.Token;

/** Handler class for &lt;form&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;form [submit] [copy] [if-processed="repl"]&gt;
 *	  ...  &lt;process&gt;...&lt;/process&gt;&lt;/form&gt;
 * <dt>Dscr:<dd>
 *	HTML &lt;form&gt; tag.  Process or COPY content if submitted,
 *	and contains &lt;process&gt; element.  Optional ID. Optionally SUBMIT.
 *	If processed, replace with value of IF-PROCESSED if present.
 *	Increment &amp;forms; entity when finished.
 *  </dl>
 */
public class Form extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<form [submit] [copy] [if-processed=\"repl\"]>\n" +
    " ...  <process>...</process></form>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "HTML <form> tag.  Process or COPY content if submitted,\n" +
    "and contains <process> element.  Optional ID. Optionally SUBMIT.\n" +
    "If processed, replace with value of IF-PROCESSED if present.\n" +
    "Increment &forms; entity when finished.\n" +
"";
 
  /** The implementation relies on the &lt;process&gt; actor to set the
   *	<code>processed</code> attribute. 
   */
  public void handle(Actor ia, SGML it, Interp ii) {
    if (it.hasAttr("processed")) {
      if (! it.hasAttr("copy")) ii.deleteIt();
      if (it.hasAttr("if-processed")) {
	ii.replaceIt(it.attr("if-processed"));
      }
    }
    if (it.hasAttr("id")) {
      // Either the user or a <submit> tag wants us to put out an ID
      Element id = new Element("input");
      id.attr("name", "id");
      id.attr("value", getFormId(it, ii));
      id.attr("type", "hidden");
      ((Element)it).addItem(id);
    }
    if (it.hasAttr("submit")) {
      dispatch("submit-forms", ia, it, ii);
    }
  
    // otherwise, the form element just gets passed along.

    // Now that we're done, increment the &forms; counter

    Text forms = Text.valueOf(ii.entities().at("forms"));
    ii.entities().at("forms", new Text(forms.intValue()+1));
    
  }

  public static String getFormId(SGML it, Interp ii) {
    Element form = (Element)it;
    Text forms = Text.valueOf(ii.entities().at("forms"));

    SGML idAttr = form.attr("id");
    String id = null;
    if (idAttr != null && idAttr != Token.empty)
      id = form.attrString("id");
    if (id == null && ! form.hasAttr("noid")) {
      id = "form-" + forms.intValue();
    }
    if (id != null) form.attr("id", id);
    return id;
  }

}
