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
import crc.sgml.AttrWrap;
import crc.sgml.AttrTable;

import crc.ds.Table;
import crc.ds.Index;

/** Handler class for &lt;get-form&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;get.form [name="name"]&gt;
 * <dt>Dscr:<dd>
 *	Get value of NAME, in the FORM context.  
 *	With no name, returns entire form. 
 *  </dl>
 */
public class Get_form extends Get {
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

    // get the form ... should be same as env.transaction.getParameters();
    //should be same as env.transaction.getParameters();
    // but possibly not if someone changed the entity 
    // -- if you  need the real original
    // form parameters, first set the FORM entity to null
    SGML form = ii.getEntity("FORM");
    if(form == null){
      Run env = Run.environment(ii);
      form =new AttrWrap(new AttrTable(env.transaction.getParameters()));
    }

    if (form == null) {
      ii.deleteIt();
      return;
    } 

    //getValue returns form if name and index both null
    SGML result = getValue(form, it); 
    result =  processResult(result, it);
      
    ii.replaceIt( result);
  }
}
