////// Agent_set_criteria.java:  Handler for <agent-set-criteria>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Run;

import crc.sgml.SGML;
import crc.sgml.Token;
import crc.sgml.Tokens;
import crc.sgml.Text;

import crc.ds.List;
import crc.ds.Criterion;


/** Handler class for &lt;agent-set-criteria&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#agent-set-criteria">Manual
 *	Entry</a> for syntax and description.
 */
public class Agent_set_criteria extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<agent-set-criteria name=\"agent-name\">query_string</agent-set-criteria>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Sets CONTENT as criteria for agent NAME.\n" +
    "Each item is 'feature', 'feature-', 'f=value' or 'f-=value'.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String s = it.contentText().toString();
    String aname= Util.getString(it, "agent", Run.getAgentName(ii));
    String value= Util.getString(it, "value", null);

    Run env = Run.environment(ii);
    crc.pia.Agent a = env.getAgent(aname);

    List l = Util.split(s);
    for (int i = 0; i < l.nItems(); ++i) {
      a.matchCriterion(l.at(i).toString());
    }

    ii.deleteIt();
  }

  /** Legacy action. */
  public boolean action(crc.dps.Context aContext, crc.dps.Output out,
			String tag, crc.dps.active.ActiveAttrList atts,
			crc.dom.NodeList content, String cstring) {
    crc.dps.process.ActiveDoc env = getInterFormContext(aContext);
    if (env == null) return legacyError(aContext, tag, "PIA not running.");

    String name = env.getAgentName(atts.getAttributeString("agent"));
    crc.pia.Agent a = env.getAgent(name);

    List l = Util.split(cstring); // should really be contentText...
    for (int i = 0; i < l.nItems(); ++i) {
      a.matchCriterion(l.at(i).toString());
    }
    return true;
  }
}
