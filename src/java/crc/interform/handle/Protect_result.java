////// Protect_result.java:  Handler for <protect_result>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Text;

/* Syntax:
 *	<protect-result [markup]>content</protect-result>
 * Dscr:
 *	Expand CONTENT and protect the result from further expansion.
 *	Optionally protect MARKUP by converting special characters to
 *	entities.
 */


/** Handler class for &lt;protect-result&gt tag */
public class Protect_result extends Protect {
  /** The only reason for having &lt;protect&gt; and
   *	&lt;protect-result&gt; in separate classes is so each can have
   *	its own syntax description. */
  public void note() {};
}
