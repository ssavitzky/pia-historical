////// Set.agent.java:  Handler for <set.agent>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Run;
import crc.interform.SecureAttrs;

import crc.sgml.SGML;

import crc.ds.Index;
import crc.sgml.Tokens;

/** Handler class for &lt;set.agent&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;set.agent name="name" [hook] [copy]&gt;...&lt;/set.agent&gt;
 * <dt>Dscr:<dd>
 *	set NAME to CONTENT in AGENT.  May set a HOOK (parsed InterForm) 
 *	or string value.  Optionally COPY content as result.  
 *  </dl>
 */
public class Set_agent extends Set {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<set.agent name=\"name\" [hook] [copy]>...</set.agent>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "set NAME to CONTENT in AGENT.  May set a HOOK (parsed InterForm) \n" +
    "or string value.  Optionally COPY content as result.  \n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    // get the appropriate index
    Index index = getIndex(it);
    if(index == null){
      ii.error(ia, " name attribute missing or null");
      return;
    }

    //  do we need an SGML context?
    boolean isComplexSet = isComplex( index, it);

    SGML value = getValue(it);

    String aname= Util.getString(it, "agent", Run.getAgentName(ii));

    Run env = Run.environment(ii);

    crc.pia.Agent a = env.getAgent(aname);

    if (a == null) {
      ii.error(ia, "agent " + aname + " not running");
      doFinish(it,value,ii);
      return;
    }
    if(isComplexSet){
      debug(this," agent "+aname+" doing complex set " + it);
      // do complex set
      doComplexSet(index,new SecureAttrs(a, ii),  value ,ia, it, ii);
    } else {
      // do the simple set
      a.attr(name, value);	// security unimplemented! ===  
    }
    doFinish(it,value,ii);

  }
}

