////// ListUtil.java: List-Processing Utilities
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

  /** Return an enumeration of Text nodes.  Recursively descends into
   *	nodes with children, and splits text nodes containing whitespace. */
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

  /************************************************************************
  ** Conversion:
  ************************************************************************/

  /** Convert a List (which is easy to manipulate) to a NodeList. */
  public static ParseNodeList toNodeList(List l) {
    return new ParseNodeList(l.elements());
  }

}
