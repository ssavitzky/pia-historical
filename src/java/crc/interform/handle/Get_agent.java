////// Get_agent.java:  Handler for <get.agent>
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

/** Handler class for &lt;get-agent&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;get-agent [agent="agent-name"] name="name"&gt;
 * <dt>Dscr:<dd>
 *	Get value of NAME, in the AGENT context (i.e. as an option).
 *  </dl>
 */
public class Get_agent extends Get {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<get-agent [agent=\"agent-name\"] name=\"name\">\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Get value of NAME, in the AGENT context (i.e. as an option).\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = getName(it);
    Index index = getIndex(it);
    if(index == null && name == null){
      ii.error(ia, " no name or index attribute specified");
      return;
    }
    
    String aname= Util.getString(it, "agent", Run.getAgentName(ii));

    Run env = Run.environment(ii);

    crc.pia.Agent a = env.getAgent(aname);
    if (a == null) {
      ii.error(ia, "agent " + aname + " not running");
      ii.deleteIt();
    }
    SGML result = getValue(new SecureAttrs(a, ii), name, index);
    result = processResult(result, it);
    ii.replaceIt(result);
      
  }
}
