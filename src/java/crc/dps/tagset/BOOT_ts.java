////// BOOT_ts.java:  Initializer for BOOT tagset
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.dps.tagset;

import crc.ds.Table;		// for cache
import crc.dps.handle.*;	// for cache initialization
import crc.dps.NodeType;

/** The BOOT tagset consists of NOTHING except what is needed to bootstrap
 *	a tagset. <p>
 *
 *	This class includes a static cache for handler instances by class name
 *	(actually by ``<code>cname</code>'').  This greatly speeds up 
 *	initialization because all classes are identified at compile time. <p>
 */
public class BOOT_ts extends BasicTagset {

  public BOOT_ts() {
    this("BOOT");
  }

  BOOT_ts(String name) {
    super(name);
    
    defTag("tagset", null, NORMAL, "tagset");
    defTag("define", null, NORMAL, "define");
    /**/defTag("action", "action doc value", QUOTED, "action");
    /**/defTag("value",  "action doc value", QUOTED, "value");
    /**/defTag("doc",    "action doc value", QUOTED, "Skippable");
  }

}

