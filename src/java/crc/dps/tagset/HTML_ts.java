////// HTML_ts.java: Tagset for HTML
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.tagset;

import java.util.Enumeration;


import crc.dps.handle.BasicHandler;
import crc.dps.handle.GenericHandler;

import crc.ds.Table;
import crc.ds.List;

/**
 * A Tagset initialized to parse for HTML. <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Processor
 * @see crc.dps.Token
 * @see crc.dps.Input 
 * @see crc.dom.Node */

public class HTML_ts extends BasicTagset {

  /************************************************************************
  ** Initialization:
  ************************************************************************/


  /** Determine whether the &lt;p&gt; tag is empty. */
  boolean emptyP = true;	// false

  static String emptyTags = "hr br link base img input"; // meta?

  /** Tags that create special data structures */
  static String dataTags = "dl table";
  

  static String phraseTags = "cite code em kbd samp strong var b i u tt " +
  "a img br hr wbr nobr center blink small big font basefont table";
  
  static String listTags 	= "ul ol dir menu";
  static String tableTags 	= "tr td th caption";
  static String tableRowTags 	= "td th caption";
  static String formTags 	= "input select option textarea";
  static String notInParagraph 	= "h1 h2 h3 h4 h5 h6 pre textarea";
  static String notInList 	= "h1 h2 h3 h4 h5 h6";

  public initializeHTML(boolean emptyParagraphTags) {
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

    // defined actors to create data structures
    // === defActors(dataTags, "parsed", true);
    

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public html() {
    initializeHTML(true);
  }

  public BasicTagset(Tagset previousContext) {
    super(previousContext);
    initialize();
  }

  public BasicTagset(DOMFactory f) {
    super(f);
    initialize();
  }
}
