////// TextUtil.java: Text-Processing Utilities 
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.aux;

import crc.dom.Node;
import crc.dom.Element;
import crc.dom.NodeList;
import crc.dom.ArrayNodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.DOMFactory;
import crc.dom.Entity;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.output.*;
import crc.dps.input.*;

import crc.ds.Table;
import crc.ds.List;
import crc.ds.Association;

import java.util.Enumeration;

/**
 * Text-processing utilities.
 *
 *	Many of these utilities operate on Text nodes in NodeLists, as well
 *	as (or instead of) on strings. 
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 */

public class TextUtil {

  /************************************************************************
  ** Standard Character Entities:
  ************************************************************************/

  static BasicEntityTable charEnts = new BasicEntityTable();

  static protected void dc(char c, String name) {
    charEnts.addBinding(name, new ParseTreeEntity(name, c));
  }

  static {
    dc('&', "amp");
    dc('<', "lt");
    dc('>', "gt");
  }

  /************************************************************************
  ** Value extraction:
  ************************************************************************/

  /** Convert a node to a string, ignoring markup.
   */
  public static String getTextString(ActiveNode n) {
    if (n.getNodeType() == NodeType.TEXT
	|| n.getNodeType() == NodeType.ENTITY)
      return n.toString();

    ToString out = new ToString();
    Copy.copyNode(n, FromParseTree(n), new FilterText(out));
    return out.getString();
  }

  /** Extract text from a nodelist.
   */
  public static NodeList getText(NodeList nl) {
    ToNodeList out = new ToNodeList();
    Copy.copyNodes(nl, new FilterText(out));
    return out.getList();
  }

  /** Return an Association between a node and its text content. */
  public static Association getTextAssociation(ActiveNode n) {
    return Association.associate(n, getTextString(n));
  }

  /** Return a list of text Associations.  Splits text nodes containing
   *	whitespace, but associates non-text markup with its concatenated text
   *	content.  Most useful for sorting nodes lexically.
   */
  public static Enumeration getTextList(NodeList nl) {
    List l = new List();
    Enumeration items = ListUtil.getListItems(nl);
    while (items.hasMoreElements()) {
      Association a = getTextAssociation((ActiveNode)items.nextElement());
      if (a != null) l.push(a);
    }
    return l.elements();
  }


  /************************************************************************
  ** Trimming and padding:
  ************************************************************************/

  /************************************************************************
  ** Inserting and deleting markup:
  ************************************************************************/

  /** Protect markup in a string by converting &lt;, &gt;, and &amp; to
   *	the corresponding entities. */
  public static final String protectMarkup(String s) {
    if (s == null || s.length() < 1) return s;
    String n = "";
    for (int i = 0; i < s.length(); ++i) {
      if (s.charAt(i) == '&') n += "&amp;";
      else if (s.charAt(i) == '>') n += "&gt;";
      else if (s.charAt(i) == '<') n += "&lt;";
      else n += s.charAt(i);
    }
    return n;    
  }

  /** Replace character entities in a NodeList with their values. */
  public static final String expandCharacterEntities(NodeList nl) {
    ToString out = new ToString(charEnts);
    Copy.copyNodes(nl, out);
    return out.getString();
  }

  /** Add markup to a String, using commonly-accepted text
   *	conventions.  Things that look like tags are boldfaced; things
   *	that look like attributes are italicized, and so on.
   *
   *	The string remains a string.
   */
  public static final String addMarkup(String s) {

    // === at some point addMarkup needs to be parametrized ===

    if (s == null || s.length() < 1) return "";
    String n = "";
    boolean inUC = false;	// inside string of uppercase chars.
    boolean inIT = false;	// inside italics  (_..._)
    boolean inBF = false;	// inside boldface (*...*)
    boolean inAN = false;	// inside attr. name
    boolean inTAG = false;	// inside tag
    boolean inAV = false;	// inside attr. value
    char AVend = 0;		// quote for attr. value
    for (int i = 0; i < s.length(); ++i) {
      char c = s.charAt(i);
      char nc = (i+1 >=  s.length())? 0 : s.charAt(i+1);
      if (c == '&') n += "&amp;";
      else if (c == '>') n += "&gt;";
      else if (c == '<') {
	n += "&lt;";
	if (isIDchar(nc) || nc == '/') {
	  inTAG = true;
	  n += "<b>";
	} 
      } else if (inTAG && (isIDchar(c) || c == '/')) {
	n += c;
	if (! isIDchar(nc)) {
	  inTAG = false;
	  n += "</b>";
	}
      }
      else if (Character.isUpperCase(c) && ! inUC &&
	       Character.isUpperCase(nc)) {
	inUC = true;
	n += "<tt>";
	n += Character.toLowerCase(c);
      } else if (Character.isUpperCase(c) && inUC) {
	n += Character.toLowerCase(c);
	if (! Character.isUpperCase(nc)) {
	  n += "</tt>";
	  inUC = false;
	}
      }
      else if (c == '=' && isIDchar(nc)) {
	inAV = true;
	AVend = 0;
	n += c;
	n += "<i>";
      }
      else if (c == '=' && (nc == '\'' || nc == '"')) {
	inAV = true;
	AVend = nc;
	n += c;
	n += "<i>";
	n += nc;
	i ++;
      }
      else if (inAV) {
	n += c;
	if (AVend != 0 && nc == AVend) {
	  inAV = false;
	  n += nc;
	  n += "</i>";
	  i ++;
	} else if (AVend == 0 && ! isIDchar(nc)) {
	  inAV = false;
	  n += "</i>";
	}
      }
      else n += s.charAt(i);
    }
    return n;
  }

  public static boolean isIDchar(char c) {
    return (Character.isLetterOrDigit(c) || c == '-' || c == '.');
  }


  /** Add markup to a String, using commonly-accepted text
   *	conventions.  Things that look like tags are boldfaced; things
   *	that look like attributes are italicized, and so on.
   *
   *	Markup is sent to an Output. === should produce real nodes ===
   */
  public static final void addMarkup(String s, Output out) {
    out.putNode(new ParseTreeText(addMarkup(s)));
  }

}
