////// Set.agent.java:  Handler for <set.agent>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Run;

import crc.sgml.SGML;


/** Handler class for &lt;set.agent&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;set.agent name="name" [hook] [copy]&gt;...&lt;/set.agent&gt;
 * <dt>Dscr:<dd>
 *	set NAME to CONTENT in AGENT.  May set a HOOK (parsed InterForm) 
 *	or string value.  Optionally COPY content as result.  
 *  </dl>
 */
public class Set_agent extends crc.interform.Handler {
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
    String name = Util.getString(it, "name", null);
    if (ii.missing(ia, "name", name)) return;
    String aname= Util.getString(it, "agent", Run.getAgentName(ii));

    Run env = Run.environment(ii);
    SGML value = it.content().simplify();

    crc.pia.Agent a = env.getAgent(aname);

    if (a == null) {
      ii.error(ia, "agent " + aname + " not running");
    } else if (it.hasAttr("hook")) {
      ii.debug("setting hook "+name+" on agent "+a.name()+"\n");
      a.attr(name, value);	// security unimplemented! ===
    } else {
      a.attr(name, value.toString());
    }

    if (it.hasAttr("copy")) {
      ii.replaceIt(value);
    } else {
      ii.deleteIt();
    }
  }
}

