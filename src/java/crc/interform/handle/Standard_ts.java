////// StandAlone_ts.java:  Initializer for Standard tagset
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

/** The Standard tagset.  This consists of the HTML syntax plus all
 *	actors that normally used <em>inside</em> the PIA. */
public class Standard_ts extends StandAlone_ts {

  static String emptyActors = "agent-criteria"
  + " agent-home agent-list agent-options agent-running agent-remove"
  + " agent-set-criterion agent-restore agent-save authenticate"
  + " pia-exit get.agent get.form get.pia get.trans";

  static String parsedActors = "agent-set-criteria agent-set-options"
  + " agent-install password-file-entry" 
  + " submit-forms trans-control set.agent set.form set.pia set.trans"; 

  public Standard_ts() {
    super("Standard", true);

    defActors(emptyActors, "empty", true);
    defActors(parsedActors, "parsed", true);
    lock();
  }

}

