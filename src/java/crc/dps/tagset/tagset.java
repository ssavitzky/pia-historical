////// tagset.java:  Initializer for tagset tagset implementation class
//	$Id$

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/


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

