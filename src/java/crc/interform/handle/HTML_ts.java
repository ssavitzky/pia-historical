////// HTML_ts.java:  Initializer for HTML tagset
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

/** The HTML tagset.  It is a subclass of Tagset rather than an
 *    instance to facilitate initialization.  We load and initialize a
 *    class this way because it is significantly faster than reading
 *    and iterpreting an InterForm file. */
public class HTML_ts extends Tagset {

  /** Determine whether the &lt;p&gt; tag is empty. */
  boolean emptyP = true;	// false

  static String emptyTags = "hr br link img input"; // meta?

  static String phraseTags = "cite code em kbd samp strong var b i u tt" +
  "a img br hr wbr nobr center blink small big font basefont table";
  
  static String listTags 	= "ul ol dir menu dl";
  static String tableTags 	= "tr td th caption";
  static String tableRowTags 	= "td th caption";
  static String formTags 	= "input select option textarea";
  static String notInParagraph 	= "h1 h2 h3 h4 h5 h6 pre textarea";
  static String notInList 	= "h1 h2 h3 h4 h5 h6";

  public HTML_ts() {
    this("HTML", true);
  }

  public HTML_ts(String name, boolean emptyParagraphTags) {
    super(name);
    emptyP = emptyParagraphTags;

    if (emptyP) emptyTags += " p";
    defEmpty(emptyTags);

    defTags(listTags, notInList);
    defTags("dd dt", "dd dt");
    defTags("li", "li");
    defTags("option", "option textarea input");
    defTags("textarea select", formTags);
    defTags(tableRowTags, tableRowTags);
    defTags("tr", tableTags);
    defTags("p", phraseTags + " p " + notInParagraph);
    if (! emptyP) defTags(notInParagraph, "p");
  }

}

