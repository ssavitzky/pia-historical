////// tagset.java:  Initializer for tagset tagset implementation class
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.dps.tagset;

import crc.ds.Table;		// for cache
import crc.dps.handle.*;	// for cache initialization

/** The <code>tagset</code> tagset contains what is <em>normally</em> needed
 *	for defining a tagset. <p>
 *
 */
public class tagset extends HTML_ts {

  public tagset() {
    this("tagset");
  }

  tagset(String name) {
    super(name, true);
    
    defTag("tagset", null, NORMAL, "tagset");
    defTag("define", null, NORMAL, "define");
    /**/defTag("action", "action doc value", QUOTED, "action");
    /**/defTag("value",  "action doc value", QUOTED, "value");
    /**/defTag("doc",    "action doc value", QUOTED, "Skippable");
  }

}

