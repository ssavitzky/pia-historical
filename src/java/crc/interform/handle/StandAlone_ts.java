////// StandAlone_ts.java:  Initializer for StandAlone tagset
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

/** The StandAlone tagset.  This consists of the HTML syntax plus all
 *	actors that are meaningful and useful outside the PIA. */
public class StandAlone_ts extends Basic_ts {

  static String emptyActors = "read get.env read.file read.href";
  static String parsedActors = "os-command os-command-output set.env" 
  + " write write.file write.href";

  public StandAlone_ts() {
    this("StandAlone", true);
  }

  public StandAlone_ts(String name, boolean emptyParagraphTags) {
    super(name, emptyParagraphTags);
    
    defActors(emptyActors, "empty", true);
    defActors(parsedActors, "parsed", true);
  }

}

