////// standard.java:  Initializer for Standard tagset
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.dps.tagset;

/** The Standard tagset.  This consists of the HTML syntax plus all
 *	actors that normally used <em>inside</em> the PIA. */
public class standard extends standalone {

  static String emptyActors = "agent-criteria"
  + " agent-home agent-list agent-options agent-running agent-remove"
  + " agent-set-criterion agent-restore agent-save"
  + " pia-exit get.agent get.form get.pia get.trans";

  static String parsedActors = "agent-set-criteria agent-set-options"
  + " agent-install" 
  + " submit-forms trans-control set.agent set.form set.pia set.trans"; 

  public standard() {
    super("Standard", true);

    defActive(emptyActors, null, EMPTY);
    defActive(parsedActors, null, NORMAL);
  }

}

