////// StandAlone_ts.java:  Initializer for Standard tagset
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Tagset;
import crc.interform.Util;
import crc.interform.Actor;

import crc.ds.Table;
import crc.ds.List;

import java.util.Enumeration;

/** The Standard tagset.  This consists of the HTML syntax plus all
 *	actors that normally used <em>inside</em> the PIA. */
public class Standard_ts extends StandAlone_ts {

  static String emptyActors = "actor-dscr agent-criteria agent-install"
  + " agent-home agent-list agent-options agent-running agent-remove"
  + " agent-set-criterion pia-exit get.agent get.form get.pia get.trans";

  static String parsedActors = "agent-set-criteria agent-set-options" 
  + " submit-forms trans-control set.agent set.pia set.trans"; 

  public Standard_ts() {
    super("Standard", true);

    defActors(emptyActors, "empty", true);
    defActors(parsedActors, "parsed", true);
  }

}

