////// ListUtil.java: List-Processing Utilities
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


package crc.dps.util;

import crc.dom.Node;
import crc.dom.Text;
import crc.dom.NodeList;
import crc.dom.NodeEnumerator;

import crc.dps.NodeType;
import crc.dps.active.*;
import crc.dps.output.*;

import crc.ds.Table;
import crc.ds.List;
import crc.ds.Association;

import java.util.Enumeration;

/**
 * List-processing utilities.
 *
 *	In most cases, a list result is returned as an Enumeration.
 *	This avoids constructing a NodeList when it's not needed.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see java.util.Enumeration
 */

public class ListUtil {

  /************************************************************************
  ** Value extraction:
  ************************************************************************/

  /** Return an enumeration of Text items that result from splitting a
   *	String on whitespace.
   */
  public static Enumeration getTextItems(String s) {
    List l = List.split(s);
    Enumeration e = l.elements();
    List r = new List();
    while (e.hasMoreElements())
      r.push(new ParseTreeText(e.nextElement().toString()));
    return r.elements();
  }


  /** Splits a string at whitespace and replaces spaces with
   *  separators.  Returns a node containing the new string.
   */
  public static Node joinTextItems(String s, String sep) {
    List l = List.split(s);
    Enumeration e = l.elements();
    List r = new List();
    String resultStr = new String();
    int count = 0;
    while (e.hasMoreElements()) {
      if(count > 0) {
	resultStr += sep;
      }
      resultStr += e.nextElement().toString();
      count++;
    }
    return new ParseTreeText(resultStr);
  }


  /** Return an enumeration of Text nodes.  Recursively descends into
   *	nodes with children, and splits text nodes containing whitespace.
   *	Obtains each whitespace- or markup-separated word in the list
   *	as a separate Text object.
   */
  public static Enumeration getTextItems(NodeList nl) {
    NodeEnumerator enum = nl.getEnumerator();
    List results = new List();
    for (Node n = enum.getFirst(); n != null; n = enum.getNext()) {
      if (n.hasChildren()) {
	results.append(getTextItems(n.getChildren()));
      } else if (n.getNodeType() == NodeType.TEXT) {
	Text t = (Text)n;
	String s = t.toString();
	if (s == null || s.equals("") || Test.isWhitespace(s)) continue;
	if (s.indexOf(" ") >= 0 || s.indexOf('\t') >= 0) {
	  results.append(getTextItems(s));
	} else {
	  results.push(t);
	}
      }
    }
    return results.elements();
  }




  /** Return the first ``word'' (blank- or markup-separated non-blank text) in
   *	a Node or its content. */
  public static String getFirstWord(Node n) {
    if (n.hasChildren()) {
      Enumeration e = getTextItems(n.getChildren());
      return e.hasMoreElements()? e.nextElement().toString() : null;
    } else if (n.getNodeType() == NodeType.TEXT) {
      Text t = (Text)n;
      String s = t.toString();
      if (s == null || s.equals("") || Test.isWhitespace(s)) return null;
      if (s.indexOf(" ") >= 0 || s.indexOf('\t') >= 0) {
	Enumeration e = getTextItems(s);
	return e.hasMoreElements()? e.nextElement().toString() : null;
      } else {
	return s;
      }
    } else {
      // === We have to assume that entities are already expanded. === 
      return null;
    }
  }

  /** Return an enumeration of Strings.  Recursively descends into
   *	nodes with children, and splits text nodes containing whitespace. */
  public static Enumeration getStringItems(NodeList nl) {
    NodeEnumerator enum = nl.getEnumerator();
    List results = new List();
    for (Node n = enum.getFirst(); n != null; n = enum.getNext()) {
      if (n.hasChildren()) {
	results.append(getTextItems(n.getChildren()));
      } else if (n.getNodeType() == NodeType.TEXT) {
	Text t = (Text)n;
	String s = t.toString();
	if (s == null || s.equals("") || Test.isWhitespace(s)) continue;
	if (s.indexOf(" ") >= 0 || s.indexOf('\t') >= 0) {
	  results.append(List.split(s));
	} else {
	  results.push(s);
	}
      }
    }
    return results.elements();
  }

  /** Return an enumeration of nodes.  Ignores whitespace and splits
   *	text nodes containing whitespace. 
   *
   * <p> This is probably the most useful utility for splitting up the
   *	 content of a node prior to sorting it. 
   */
  public static Enumeration getListItems(NodeList nl) {
    NodeEnumerator enum = nl.getEnumerator();
    List results = new List();
    for (Node n = enum.getFirst(); n != null; n = enum.getNext()) {
      if (n.getNodeType() == NodeType.TEXT) {
	Text t = (Text)n;
	String s = t.toString();
	if (s == null || s.equals("") || Test.isWhitespace(s)) continue;
	if (s.indexOf(" ") >= 0 || s.indexOf('\t') >= 0) {
	  results.append(getTextItems(s));
	} else {
	  results.push(t);
	}
      } else {
	results.push(n);
      }
    }
    return results.elements();
  }


  /** Return an enumeration of nodes.  Ignores whitespace and splits
   *	text nodes containing whitespace. Accumulates a string from
   *    all adjacent text nodes, and creates a single node.  Other
   *    types of nodes are added to the result list unchanged.  For
   *    example, text1 text2 markup1 text3 text4 markup2 would result
   *    in a list of four nodes:  accum_text1 markup1 accum_text2 markup2.
   *    Each accumulated text string has a separator added between each
   *    formerly separate string; e.g. accum_text1 has string: text1,text2
   *    where a comma is the separator.
   */
  public static Enumeration joinListItems(NodeList nl, String sep) {
    NodeEnumerator enum = nl.getEnumerator();
    List results = new List();
    String accumStr = null;
    int adjacent = 0;

    for (Node n = enum.getFirst(); n != null; n = enum.getNext()) {
      if (n.getNodeType() == NodeType.TEXT) {
	Text t = (Text)n;
	String s = t.toString();
	if (s == null || s.equals("") || Test.isWhitespace(s)) continue;
	if (s.indexOf(" ") >= 0 || s.indexOf('\t') >= 0) {
	  // If there's whitespace in string, split and join it into a
	  // a single node.
	  results.push(joinTextItems(s, sep));
	} 
	else {
	  // A single node without whitespace.  Just add to list
	  results.push(t);
	}
      } 
      else {
	results.push(n);
      }
    }
    return results.elements();
  }

  /************************************************************************
  ** Conversion:
  ************************************************************************/

  /** Convert a List (which is easy to manipulate) to a NodeList. */
  public static ParseNodeList toNodeList(List l) {
    return new ParseNodeList(l.elements());
  }

}
