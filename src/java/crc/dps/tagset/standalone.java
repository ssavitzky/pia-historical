////// standalone.java:  Initializer for StandAlone tagset
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.dps.tagset;

/** The StandAlone tagset.  This consists of the HTML syntax plus all
 *	actors that are meaningful and useful outside the PIA. */
public class standalone extends basic {

  static String emptyActors = "file read get.env read.file read.href";
  static String parsedActors = "os-command os-command-output set.env" 
  + " write write.file write.href";

  public standalone() {
    this("StandAlone", true);
  }

  public standalone(String name, boolean emptyParagraphTags) {
    super(name, emptyParagraphTags);
    
    defActive(emptyActors, null, EMPTY);
    defActive(parsedActors, null, NORMAL);
  }

}

