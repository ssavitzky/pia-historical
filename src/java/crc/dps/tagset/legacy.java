////// legacy.java:  Initializer for Legacy tagset
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.dps.tagset;

import crc.util.*;
import crc.dps.handle.*;
import crc.interform.handle.*;

/** The Legacy tagset.  This consists of the HTML syntax plus those
 *	actors that were used in old (legacy) InterForms. <p>
 *
 *	This class includes a static cache for handler instances by class name
 *	(actually by ``<code>cname</code>'').  This greatly speeds up 
 *	initialization because all classes are identified at compile time. <p>
 */
public class legacy extends HTML_ts {

  static String emptyActors = "actor-attrs actor-dscr actor-doc actor-syntax"
  + " get tagset-exists tagset-load tagset-include element actor-names"
  + " calendar"
  /* from standalone */
  + " file read authenticate"
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

  /************************************************************************
  ** Handler cache:
  ************************************************************************/

  protected static crc.ds.Table handlerCache = new crc.ds.Table();
  protected static void defHandle(String cname, BasicHandler handler) {
    handlerCache.at(cname, handler);
  }

  protected static void defLegacy(String cname, crc.interform.Handler h) {
    handlerCache.at(cname, new LegacyHandler(h));
  }

  static {
    defHandle("else", new elseHandler());
    defHandle("elsf", new elsfHandler());
    defHandle("get", new getHandler());
    defHandle("if", new ifHandler());
    defHandle("repeat", new repeatHandler());
    defHandle("set", new setHandler());
    defHandle("subst", new substHandler());
    defHandle("test", new testHandler());
    defHandle("then", new thenHandler());

    // === need Table, Dl, etc. ===

    defLegacy("actor-attrs", new Actor_attrs());
    defLegacy("actor-dscr", new Actor_dscr());
    defLegacy("actor-doc", new Actor_doc());
    defLegacy("actor-syntax", new Actor_syntax());
    defLegacy("tagset-exists", new Tagset_exists());
    defLegacy("tagset-load", new Tagset_load());
    defLegacy("tagset-include", new Tagset_include());
    defLegacy("element", new Element());
    defLegacy("actor-names", new Actor_names());
    defLegacy("calendar", new Calendar());
    defLegacy("file", new File());
    defLegacy("read", new Read());
    defLegacy("authenticate", new Authenticate());
    defLegacy("agent-criteria", new Agent_criteria());
    defLegacy("agent-home", new Agent_home());
    defLegacy("agent-list", new Agent_list());
    defLegacy("agent-options", new Agent_options());
    defLegacy("agent-running", new Agent_running());
    defLegacy("agent-remove", new Agent_remove());
    defLegacy("agent-set-criterion", new Agent_set_criterion());
    defLegacy("agent-restore", new Agent_restore());
    defLegacy("agent-save", new Agent_save());
    defLegacy("pia-exit", new Pia_exit());
    defLegacy("add-markup", new Add_markup());
    defLegacy("difference", new Difference());
    defLegacy("equal", new Equal());
    defLegacy("expand", new Expand());
    defLegacy("pad", new Pad());
    defLegacy("product", new Product());
    defLegacy("protect-result", new Protect_result());
    defLegacy("quotient", new Quotient());
    defLegacy("sort", new Sort());
    defLegacy("sorted", new Sorted());
    defLegacy("subst", new Subst());
    defLegacy("sum", new Sum());
    defLegacy("tagset", new Tagset());
    defLegacy("text", new Text());
    defLegacy("trim", new Trim());
    defLegacy("user-message", new User_message());
    defLegacy("calendar-day", new Calendar_day());
    defLegacy("form", new Form());
    defLegacy("os-command", new Os_command());
    defLegacy("os-command-output", new Os_command_output());
    defLegacy("write", new Write());
    defLegacy("password-file-entry", new Password_file_entry());
    defLegacy("agent-set-criteria", new Agent_set_criteria());
    defLegacy("agent-set-options", new Agent_set_options());
    defLegacy("agent-install", new Agent_install());
    defLegacy("submit-forms", new Submit_forms());
    defLegacy("trans-control", new Trans_control());
    defLegacy("protect", new Protect());
    defLegacy("foreach", new Foreach());
    defLegacy("actor", new Actor());
    defLegacy("process", new crc.interform.handle.Process());

  }

  /** Instantiate an appropriate handler.  Uses a static cache for speed.
   */
  protected BasicHandler loadHandler(String cname) {
    BasicHandler h = (BasicHandler) handlerCache.at(cname);
    if (h == null) h = new BasicHandler();
    return h;
  }

  /** Instantiate an appropriate handler.  Uses a static cache for speed.
   */
  protected GenericHandler loadHandler(String tag, String cname) {
    if (cname == null) cname = tag;
    GenericHandler h = (GenericHandler) handlerCache.at(cname);
    if (h == null) h = new GenericHandler();
    return h;
  }

  /** Load an appropriate handler class and instantiate it. 
   *	Subclasses (e.g. legacyTagset) may need to override this.
   */
  protected GenericHandler old_loadHandler(String tag, String cname) {
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

}

