////// Basic_ts.java:  Initializer for Basic tagset
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Tagset;
import crc.interform.Util;
import crc.interform.Actor;

import crc.sgml.SGML;

import crc.ds.Table;
import crc.ds.List;

import java.util.Enumeration;

/** The Basic tagset.  This consists of the HTML syntax plus those
 *	actors that have no effect outside the file being processed.
 *	It is somewhat inefficient to extend HTML_ts rather than
 *	including it; we may want to revisit this decision later. */
public class Basic_ts extends HTML_ts {

  static String emptyActors = "actor-attrs actor-dscr actor-doc actor-syntax"
  + " get tagset-include element actor-names";
  static String parsedActors = "add-markup difference equal expand if"
  + " pad product protect-result quotient set sort sorted subst sum"
  + " tagset test text trim user-message";
  static String quotedActors = "protect repeat actor";

  public Basic_ts() {
    this("Basic", true);
  }

  Basic_ts(String name, boolean emptyParagraphTags) {
    super(name, emptyParagraphTags);
    
    defActors(emptyActors, "empty", true);
    defActors(parsedActors, "parsed", true);
    defActors(quotedActors, "quoted", true);

    // -foreach- needs a match.
    Actor a = new Actor("-foreach-", null, "quoted", "foreach");
    a.attr("match", "foreach");
    a.initMatch();
    define(a);

    // ... or could do defActors("then else", "quoted", true);
    define(new Actor("then", "quoted"));
    define(new Actor("else", "quoted"));
    defTags("then else", "then else");
  }

}

