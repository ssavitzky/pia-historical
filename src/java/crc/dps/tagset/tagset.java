////// tagset.java:  Initializer for tagset tagset implementation class
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.dps.tagset;

import crc.ds.Table;		// for cache
import crc.dps.handle.*;	// for cache initialization

/** The BOOT tagset consists of NOTHING except what is needed to bootstrap
 *	a tagset. <p>
 *
 *	This class includes a static cache for handler instances by class name
 *	(actually by ``<code>cname</code>'').  This greatly speeds up 
 *	initialization because all classes are identified at compile time. <p>
 */
public class tagset extends HTML_ts {

  public tagset() {
    this("tagset");
  }

  tagset(String name) {
    super(name, false);
    
    defTag("tagset", null, NORMAL, "tagset");
    defTag("define", null, NORMAL, "define");
    /**/defTag("action", "action doc value", QUOTED, "define_action");
    /**/defTag("value",  "action doc value", QUOTED, "define_value");
    /**/defTag("doc",    "action doc value", QUOTED, "Ignorable");
  }

}

