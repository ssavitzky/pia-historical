////// HTML_ts.java: Tagset for HTML
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

import crc.dps.Tagset;

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

  static String emptyTags[] = { "hr", "br", "link", "base", "img",
				"input", "meta"};

  /** Tags that create special data structures */
  static String dataTags[] = {"dl", "table"};
  

  static String phraseTags[] = {
    "cite", "code", "em", "kbd", "samp", "strong", "var", "b", "i", "u", "tt",
    "a", "img", "br", "hr", "wbr", "nobr", "center", "blink", "small", "big",
    "font", "basefont", "table"};
  
  static String listTags[] 	= { "ul", "ol", "dir", "menu"};
  static String tableTags[] 	= { "tr", "td", "th", "caption"};
  static String tableRowTags[] 	= { "td", "th", "caption"};
  static String formTags[] 	= { "input", "select", "option", "textarea"};
  static String formContainers[]= { "select", "textarea"};
  static String notInParagraph[]= { "h1", "h2", "h3", "h4", "h5", "h6",
				    "pre", "textarea"};
  static String notInList[] 	= { "h1", "h2", "h3", "h4", "h5", "h6"};
  static String literal[]	= { "script", "style"};
  static String p[]		= { "p" };
  static String tr[]		= { "tr" };
  static String li[]		= { "li" };
  static String option[]	= { "option" };
  static String optionNotIn[]	= { "option", "textarea", "input"};
  static String dlItems[]	= { "dd", "dt" };

  public void initializeHTML(boolean emptyParagraphTags) {
    caseFoldTagnames = true;
    caseFoldAttributes = true;

    emptyP = emptyParagraphTags;

    defTags(emptyTags, null, EMPTY);
    if (emptyP) defTag("p", null, EMPTY);

    defTags(listTags, notInList, NORMAL);
    defTags(dlItems, dlItems, NORMAL);
    defTags(li, li, NORMAL);
    defTags(option, optionNotIn, NORMAL);
    defTags(formContainers, formTags, NORMAL);
    defTags(tableRowTags, tableRowTags, NORMAL);
    defTags(tr, tableTags, NORMAL);
    defTags(p, p, 0);
    defTags(p, phraseTags, 0);
    defTags(p, notInParagraph, 0);
    defTags(dataTags, null, NORMAL);
    defTags(literal, null, LITERAL);
    if (! emptyP) defTags(notInParagraph, p, 0);
  }    

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public HTML_ts() {
    super("HTML");
    initializeHTML(true);
  }

  public HTML_ts(String name, boolean emptyParagraphTags) {
    super(name);
    initializeHTML(emptyParagraphTags);
  }

  public HTML_ts(Tagset previousContext) {
    super(previousContext);
    initializeHTML(true);
  }

}
