////// Read.java:  Handler for <read>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;

/* Syntax:
 *	<read [ file="name" [interform [agent="agentName"]] 
 *	        [info|head|directory [links] [tag=tag] [all|match="regexp"]] 
 *	      | href="url" [resolve] ] 
 *	      [base="path"] [process [tagset="name"]] >
 * Dscr:
 *	Input from FILE or HREF, with optional BASE path.  FILE may be
 *	looked up as an INTERFORM in current or other AGENT.
 *	Optionally read only INFO or HEAD.  For DIRECTORY, read names
 *	or LINKS, and return TAG or ul.  DIRECTORY can read ALL names
 *	or those that MATCH; default is all but backups.  Optionally
 *	PROCESS with optional TAGSET.  HREF can optionally RESOLVE in
 *	pia.  */

/** Handler class for &lt;read&gt tag */
public class Read extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {
    // === href + resolve should dispatch to read.href.resolve ===
    // === file + interform should dispatch to read.file.interform
    if (it.hasAttr("href") && it.hasAttr("file")) 
      ii.error(ia, "href and file attributes both specified");
    else if (it.hasAttr("href")) dispatch("read.href", ia, it, ii);
    else if (it.hasAttr("file")) dispatch("read.file", ia, it, ii);
    else ii.error(ia, "must have file or href attribute");

    // === could do read.file in place.
  }
}

