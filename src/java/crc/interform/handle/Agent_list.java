////// Agent_list.java:  Handler for <agent-list>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Run;

import crc.sgml.SGML;
import crc.sgml.Tokens;

import crc.pia.Agent;
import crc.pia.Resolver;

import java.util.Enumeration;

/** Handler class for &lt;agent-list&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#agent-list">Manual Entry</a> 
 *	for syntax and description.
 */
public class Agent_list extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<agent-list [type=type][subs]>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "List the agents with given TYPE. Possibly SUBS only.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    Resolver resolver = Run.getResolver(ii);
    String type = it.attrString("type");
    boolean subs = it.hasAttr("subs");

    Tokens list = new Tokens(" ");

    Enumeration names = resolver.agentNames();
    while (names.hasMoreElements()) {
      String name = names.nextElement().toString();
      Agent agent = resolver.agent(name);
      if (subs && name.equals(agent.type())) continue;
      if (type == null || type.equals(agent.type())) list.push(name);
    }

    ii.replaceIt(list.toString());
  }

  /** Legacy action. */
  public boolean action(crc.dps.Context aContext, crc.dps.Output out,
			String tag, crc.dps.active.ActiveAttrList atts,
			crc.dom.NodeList content, String cstring) {
    crc.dps.InterFormProcessor env = getInterFormContext(aContext);
    if (env == null) return legacyError(aContext, tag, "PIA not running");

    String type = atts.getAttributeString("type");
    boolean subs = atts.hasTrueAttribute("subs");

    Resolver resolver = env.getResolver();

    Tokens list = new Tokens(" "); // === bogus but we don't return it, so OK.

    Enumeration names = resolver.agentNames();
    while (names.hasMoreElements()) {
      String name = names.nextElement().toString();
      Agent agent = resolver.agent(name);
      if (subs && name.equals(agent.type())) continue;
      if (type == null || type.equals(agent.type())) list.push(name);
    }

    // === should really return list of agent names as a list ===
    return putText(out, list.toString());
  }
}
