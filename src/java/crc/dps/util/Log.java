////// Log.java: log utilities
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.util;

import java.io.PrintStream;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Element;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.Text;

import crc.dps.*;
import crc.dps.active.*;

/**
 * Logging utilities (static methods) for a Document Processor. 
 *
 *	This class contains static methods used for maintaining and 
 *	formatting a message log.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 * 
 * @see crc.dps.Context
 */
public class Log {
  public static String node(Node aNode) {
    switch (aNode.getNodeType()) {
    case crc.dom.NodeType.ELEMENT:
      Element e = (Element)aNode;
      AttributeList atts = e.getAttributes();
      return "<" + e.getTagName()
	+ ((atts != null && atts.getLength() > 0)? " " + atts.toString() : "")
	+ ">" + (e.hasChildren()? "..." : "");

    case crc.dom.NodeType.TEXT: 
      Text t = (Text)aNode;
      return t.getIsIgnorableWhitespace()
	? "ignorable" : Test.isWhitespace(t.getData()) ? "whitespace"
	: ("text: '" + string(t.getData()) + "'");

    default: 
      return aNode.toString();      
    }
  }

  public static String string(String s, int length) {
    if (s == null) return "null";
    String o = "";
    int i = 0;
    for ( ; i < s.length() && i < length; ++i) {
      char c = s.charAt(i);
      switch (c) {
      case '\n': o += "\\n"; break;
      default: o += c;
      }
    }
    if (i < s.length()) o += "..."; 
    return o;
  }

  public static String string(String s) {
    return string(s, 15);
  }
}
