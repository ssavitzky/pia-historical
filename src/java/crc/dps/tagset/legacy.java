////// legacy.java:  Initializer for Legacy tagset
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.dps.tagset;

import crc.util.*;
import crc.dps.handle.*;

/** The Legacy tagset.  This consists of the HTML syntax plus those
 *	actors that were used in old (legacy) InterForms. */
public class legacy extends HTML_ts {

  static String emptyActors = "actor-attrs actor-dscr actor-doc actor-syntax"
  + " get tagset-exists tagset-load tagset-include element actor-names"
  + " calendar"
  /* from standalone */
  + " file read get.env read.file read.href authenticate"
  /* from standard */
  + " agent-criteria agent-home agent-list agent-options agent-running"
  + " agent-remove agent-set-criterion agent-restore agent-save"
  + " pia-exit get.agent get.form get.pia get.trans";
  static String parsedActors = "add-markup difference equal expand if"
  + " pad product protect-result quotient set sort sorted subst sum"
  + " tagset test text trim user-message calendar-day form"
  /* from standalone */
  + " os-command os-command-output set.env write write.file write.href"
  + " password-file-entry"
  /* from Standard */
  + " agent-set-criteria agent-set-options agent-install" 
  + " submit-forms trans-control set.agent set.form set.pia set.trans";

  static String quotedActors = "protect repeat foreach actor process";

  public legacy() {
    this("Legacy", true);
  }

  /** Load an appropriate handler class and instantiate it. 
   *	Subclasses (e.g. legacyTagset) may need to override this.
   */
  protected GenericHandler loadHandler(String tag, String cname) {
    GenericHandler h = null;
    String name = (cname == null)? tag : cname;
    // First shoot for a non-legacy handler that does the same thing.
    Class c = NameUtils.loadClass(name, "crc.dps.handle.");
    if (c == null) {
      c = NameUtils.loadClass(name+"Handler", "crc.dps.handle.");
    }
    try {
      if (c == null) {
	c = NameUtils.loadClass(NameUtils.javaName(name),
				"crc.interform.handle.");
	if (c != null) {
	  h = new LegacyHandler((crc.interform.Handler)c.newInstance());
	}
      }
      if (c != null) h = (GenericHandler)c.newInstance();
    } catch (Exception e) {}
    if (h == null) h = new GenericHandler();
    return h;
  }

  legacy(String name, boolean emptyParagraphTags) {
    super(name, emptyParagraphTags);
    
    defActive(emptyActors, null, EMPTY);
    defActive(parsedActors, null, NORMAL);
    defActive(quotedActors, null, QUOTED);

    // -foreach- needs a match.
    //Actor a = new Actor("-foreach-", null, "quoted", "foreach_");
    //a.attr("match", "foreach");
    //a.initMatch();
    //define(a);

    // ... or could do defActors("then else", "quoted", true);
    defActive("then else", "then else", QUOTED);
  }

}

