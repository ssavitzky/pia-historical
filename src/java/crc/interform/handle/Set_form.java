////// Set_form.java:  Handler for <set.form>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Run;

import crc.sgml.SGML;

/* Syntax:
 *	<set.form name="name" [copy]>...</set.form>
 * Dscr:
 *	set NAME to CONTENT in the form table. 
 *	Optionally COPY content as result.
 */

/** Handler class for &lt;set.form&gt tag */
public class Set_form extends Set {
  public void handle(Actor ia, SGML it, Interp ii) {
    // get the appropriate index
    Index index = getIndex(it);
    if(index == null){
      ii.error(ia, " name attribute missing or null");
      return;
    }
    
    SGML value = getValue();

    //  do we need an SGML context?
    boolean isComplexSet = isComplex( index, it);
    // these may not be the original form parameters -- but
    // use this SGML object for consistency 
    SGML form = ii.getEntity("FORM");
    if(form == null){
      ii.error(ia,"No  form context");
    } else {
      if(isComplexSet) {
	 doComplexSet( index, form, value, ia, it, ii);
      } else {
	String key = index.shift();
	form.attr(key,value);
      }
    }

    doFinish(it,value,ii);
  }
}

