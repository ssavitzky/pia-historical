////// basic.java:  Initializer for Basic tagset
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.dps.tagset;

import crc.ds.Table;		// for cache
import crc.dps.handle.*;	// for cache initialization

/** The Basic tagset.  This consists of the HTML syntax plus those
 *	actors that have no effect outside the file being processed.
 *	It is somewhat inefficient to extend HTML_ts rather than
 *	including it; we may want to revisit this decision later. <p>
 *
 *	This class includes a static cache for handler instances by class name
 *	(actually by ``<code>cname</code>'').  This greatly speeds up 
 *	initialization because all classes are identified at compile time. <p>
 */
public class basic extends HTML_ts {

  static String emptyActors = ""
  + " get tagset-exists tagset-load tagset-include element actor-names"
  + " calendar";
  static String parsedActors = "add-markup difference equal expand if"
  + " pad product protect-result quotient set sort sorted subst sum"
  + " tagset test text trim user-message calendar-day form";
  static String quotedActors = "protect repeat foreach actor process";

  public basic() {
    this("Basic", true);
  }

  basic(String name, boolean emptyParagraphTags) {
    super(name, emptyParagraphTags);
    
    defActive(emptyActors, null, EMPTY);
    defActive(parsedActors, null, NORMAL);
    defActive(quotedActors, null, QUOTED);

    // -foreach- needs a match.
    //Actor a = new Actor("-foreach-", null, "quoted", "foreach_");
    //a.attr("match", "foreach");
    //a.initMatch();
    //define(a);

    defActive("then", "test else", QUOTED);
    defActive("else else-if", "test then else-if", QUOTED);
  }

  /************************************************************************
  ** Handler cache:
  ************************************************************************/

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
  }

}

