////// TextUtil.java: Text-Processing Utilities 
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.aux;

import crc.dom.Node;
import crc.dom.Element;
import crc.dom.Text;
import crc.dom.NodeList;
import crc.dom.NodeEnumerator;
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

import crc.util.*;

import java.util.Enumeration;
import java.util.Hashtable;
import java.net.*;

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
  public static BasicEntityTable getCharacterEntities() { return charEnts; }

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
    // First, see if n is a text node.  If it is, just convert it to a string.
    if (n.getNodeType() == NodeType.TEXT
	|| n.getNodeType() == NodeType.ENTITY)
      return n.toString();

    // If that doesn't work, we have to go through its entire contents and
    // filter out the text.  We do it by cascading a FilterText (a kind of
    // Output) into a ToString (another kind of Output).

    ToString out = new ToString();
    Copy.copyNode(n, new FromParseTree(n), new FilterText(out));
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
  public static Association getTextAssociation(ActiveNode n, boolean caseSens) {

    String str = getTextString(n);
    
    if(!caseSens) {
      str = str.toLowerCase();
    }
    return Association.associate(n, str);
  }

  /** Return a list of text Associations.  Splits text nodes containing
   *	whitespace, but associates non-text markup with its concatenated text
   *	content.  Most useful for sorting nodes lexically.
   */
  public static List getTextList(NodeList nl, boolean caseSens) {
    List l = new List();
    Enumeration items = ListUtil.getListItems(nl);
    while (items.hasMoreElements()) {
      Association a = getTextAssociation((ActiveNode)items.nextElement(), caseSens);
      if (a != null) l.push(a);
    }
    return l;
  }


  /************************************************************************
  ** Trimming and padding:
  ************************************************************************/

  /** Trim the blank spaces from a String. */
  public static final String trimLeading(String s) {
    s += '.';
    s = s.trim();
    return s.substring(0, s.length()-1);
  }

  /** Trim the trailing blanks from a String. */
  public static final String trimTrailing(String s) {
    s = "." + s;
    s = s.trim();
    return s.substring(1);
  }

  /** Trim leading and trailing whitespace.  Return an 
    * enumeration of nodes with whitespace removed.
    */
  public static Enumeration trimListItems(NodeList nl) {
    NodeEnumerator enum = nl.getEnumerator();
    List results = new List();
    String rStr;

    boolean seenFirstTextNode = false;
    int lastTextIndex = -1;

    // Get last text item
    Node firstTextNode = null;
    Node lastTextNode = null;
    
    // Get first and last text nodes in list
    for (Node n = enum.getFirst(); n != null; n = enum.getNext()) {
      if(n.getNodeType() == NodeType.TEXT) {
	if(seenFirstTextNode == false) {
	  firstTextNode = n;
	  seenFirstTextNode = true;
	}
	else {
	  // Only a first text node, this won't get set
	  lastTextNode = n;
	}
      }
    }
    
    if(lastTextNode == null)
      lastTextNode = firstTextNode;

    for (Node n = enum.getFirst(); n != null; n = enum.getNext()) {
      if(n.getNodeType() == NodeType.TEXT) {
	Text tNode = (Text)n;
	if(n == firstTextNode) {
	  String s = tNode.toString();
	  rStr = trimLeading(s);
	  tNode.setData(rStr);
	  results.push(tNode);
	  seenFirstTextNode = true;
	}
	else if(n == lastTextNode) {
	  String s = tNode.toString();
	  rStr = trimTrailing(s);
	  tNode.setData(rStr);
	  results.push(tNode);
	}
	else {
	  // Push all other text nodes
	  results.push(tNode);
	}
      }
      else {
	// push other node type unchanged
	results.push(n);
      }
    }
    return results.elements();
  }


  /** Pad the text to the specified width with the specified
    * alignment. The default is left, meaning that spaces are
    * added to the right.
    */
  public static Enumeration padListItems(NodeList nl, boolean align, boolean left,
					  boolean right, boolean center, int width) {

    List results = new List();

    NodeEnumerator enum = nl.getEnumerator();    
    int strLen = 0;

    // Extract string length from each node and push node onto
    // results list.
    for(Node n = enum.getFirst(); n != null; n= enum.getNext()) {
      String nStr = getTextString((ActiveNode)n);
      strLen += nStr.length();
      results.push(n);
    }
    
    if(strLen < width) {
      // Pad amount specified
      int padLen = (width - strLen);
      // Create a node full of spaces
      ParseTreeText pNode = null;
      if(right) {
	pNode = createPadNode(padLen);
	results.insertAt(pNode, 0);
      }
      else if(center) {
	int extraSpace = 0;
	if((padLen % 2) != 0)
	  extraSpace = 1;
	padLen = padLen / 2;
	// Add new node of spaces to end of list
	pNode = createPadNode(padLen + extraSpace);
	results.insertAt(pNode, results.nItems());
	// Add new node of spaces to front of list
	pNode = createPadNode(padLen);
	results.insertAt(pNode, 0);

      }
      else {
	pNode = createPadNode(padLen);
	results.insertAt(pNode, results.nItems());
      }
    }
    return results.elements();
  }

  public static final ParseTreeText createPadNode(int width) {
    
    String nodeStr = "";
    for(int i = 0; i < width; i++) {
      nodeStr += " ";
    }
    ParseTreeText ptt = new ParseTreeText(nodeStr);
    return ptt;
  }
  

  /************************************************************************
  ** Inserting and deleting markup:
  ************************************************************************/

  /** Protect markup in a string by converting &lt;, &gt;, and &amp; to
   *	the corresponding entities.  The string remains a string.
   * === should handle &amp;#... character entities!
   */
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

  /** === There should be a corresponding insertCharacterEntities that actually
   *	outputs a NodeList.
   */

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


  /************************************************************************
  ** Encoding/Decoding
  ************************************************************************/


  /** Find each occurrence of encoded html special characters
    * in a text string and replace with their text
    * equivalents.
    * @param text The string contain the encoded character(s).
    */
  public static String decodeEntity(String text) {

    String d     = subEntity("&amp;", text);
    String dd    = subEntity("&lt;", d);
    String ddd   = subEntity("&gt;", dd);
    String dddd  = subEntity("&apos;", ddd);
    String ddddd = subEntity("&quot;", dddd);
    return ddddd; 
  }

  /** Loop through string and replace each occurrence of
      markup entity with its ascii equivalent.  For example,
      replace &lt; with <.
      @param ent The entity to be replaced.
      @param text The string containing the entity.
  */ 
  protected static String subEntity(String ent, String text){
    int spos=0;
    int epos=0;
    int fpos=0;
    boolean found = false;
    StringBuffer sb = new StringBuffer();
    
    fpos = text.indexOf(ent);

    while( fpos != -1 ){
      found = true;
      // System.out.println("spos is-->"+Integer.toString(spos));
      // System.out.println("fpos is-->"+Integer.toString(fpos));
      // System.out.println("The substring is-->"+text.substring(spos, fpos));
      sb.append(text.substring(spos, fpos));
      spos = fpos + ent.length();
      sb.append((String)predefEntityTab.get(ent));
      fpos = text.indexOf( ent, fpos+1 );
    }
    if( !found ) 
      return text;
    
    if( spos != 0 )
      sb.append(text.substring(spos, text.length()));
    
    return new String( sb );
  }

  /** Lookup table for html entities and their ascii equivalents */
  protected static Hashtable predefEntityTab;

  static{
    predefEntityTab = new Hashtable();
    predefEntityTab.put("&lt;", "<");
    predefEntityTab.put("&gt;", ">");
    predefEntityTab.put("&amp;", "&");
    predefEntityTab.put("&apos;", "'");
    predefEntityTab.put("&quot;", "\"");
  }


  /** Encode text node strings using URL encoding. */
  public static List encodeURLListItems(NodeList nl) {
    NodeEnumerator enum = nl.getEnumerator();
    List results = new List();
    
    for (Node n = enum.getFirst(); n != null; n = enum.getNext()) {
      if(n.getNodeType() == NodeType.TEXT) {
	Text tNode = (Text)n;
	String bStr = URLEncoder.encode(tNode.toString());
	tNode.setData(bStr);
	results.push(tNode);
      }
      else {
	// push other node type unchanged
	results.push(n);
      }
    }
    return results;
  }

  /** Encode text node contents in base64 */
  public static List encodeBase64ListItems(NodeList nl) {
    NodeEnumerator enum = nl.getEnumerator();
    List results = new List();
    
    for (Node n = enum.getFirst(); n != null; n = enum.getNext()) {
      if(n.getNodeType() == NodeType.TEXT) {
	Text tNode = (Text)n;
	String s = tNode.toString();
	String bStr = Utilities.encodeBase64(s.getBytes());
	tNode.setData(bStr);
	results.push(tNode);
      }
      else {
	// push other node type unchanged
	results.push(n);
      }
    }
    return results;
  }

  /** Returns an List of string tokens which include text plus tokens for
    * markup characters as well as any accompanying text.
    */
  public static List encodeEntityListItems(NodeList nl, String markupChars) {
    NodeEnumerator enum = nl.getEnumerator();
    List tokenList = new List();
    List resultList = new List();
    
    for (Node n = enum.getFirst(); n != null; n = enum.getNext()) {
      if(n.getNodeType() == NodeType.TEXT) {
	Text tNode = (Text)n;
	String s = tNode.toString();

	// true means return delimiters as tokens
	tokenList.append(List.split(s, markupChars, true));
      }
      else {
	// push other node type unchanged
	tokenList.push(n);
      }
    }
    // Convert each string token to the correct node type
    Enumeration tlEnum = tokenList.elements();
    while(tlEnum.hasMoreElements()) {
      String tmpStr = (String)tlEnum.nextElement();
      // Get token type
      if(markupChars.indexOf(tmpStr) == -1) {
	// Not a markup token
	ParseTreeText ptt = new ParseTreeText(tmpStr);
	resultList.push(ptt);
      }
      else {
	// Markup token:  do a table lookup to get encoding
	String tStr = (String)TextUtil.encodeEntityTab.get(tmpStr);
	ParseTreeEntity pte = new ParseTreeEntity(tStr);
	resultList.push(pte);
      }
    }
    return resultList;
  }


  /** Lookup table for text characters that are encoded as entities.
      ParseTreeEntity adds &; to encoding.
  */
  public static Hashtable encodeEntityTab;

  static{
    encodeEntityTab = new Hashtable();
    encodeEntityTab.put("<", "lt");
    encodeEntityTab.put(">", "gt");
    encodeEntityTab.put("&", "amp");
    encodeEntityTab.put("'", "apos");
    encodeEntityTab.put("\"", "quot");
  }

  /************************************************************************
  ** Debugging
  ************************************************************************/

  // Print contents of a NodeList
  static public void printNodeList(NodeList nl) {
    NodeEnumerator enum = nl.getEnumerator();
    for (Node n = enum.getFirst(); n != null; n = enum.getNext()) {
      System.err.println(n.toString());
    }
  }


  // Print contents of a List
  static public void printList(List list) {
    
    System.out.println("List size: " + list.size());
    Enumeration enum = list.elements();
    while(enum.hasMoreElements()) {
      System.err.println(enum.nextElement());
    }
  }
}
