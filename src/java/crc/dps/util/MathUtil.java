////// MathUtil.java: Mathematical Utilities
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.util;

import crc.dom.Node;
import crc.dom.Element;
import crc.dom.NodeList;
import crc.dom.NodeEnumerator;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.Entity;

import crc.dps.NodeType;
import crc.dps.active.*;
import crc.dps.output.*;

import crc.ds.Table;
import crc.ds.List;
import crc.ds.Association;

import java.util.Enumeration;

/**
 * Mathematical utilities.
 *
 *	A list is normally handled as an Enumeration, and a number as
 *	an Association between a Node and its value.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.ds.Association
 * @see java.util.Enumeration
 */

public class MathUtil {

  /************************************************************************
  ** Attribute Conversion:
  ************************************************************************/

  public static Association getNumeric(ActiveAttrList atts, String name,
				       String dflt) {
    String v = atts.getAttributeString(name);
    if (v == null) {
      if (dflt == null) return null;
      else v = dflt;
    }
    Association a = Association.associateNumeric(v, v);
    return a.isNumeric()
      ? a
      : (dflt == null)? null : Association.associateNumeric(null, dflt);
  }

  public static int getInt(ActiveAttrList atts, String name, int dflt) {
    String v = atts.getAttributeString(name);
    if (v == null) return dflt;
    Association a = Association.associateNumeric(null, v);
    return a.isNumeric()? (int)a.longValue() : dflt;
  }

  public static long getLong(ActiveAttrList atts, String name, long dflt) {
    String v = atts.getAttributeString(name);
    if (v == null) return dflt;
    Association a = Association.associateNumeric(null, v);
    return a.isNumeric()? a.longValue() : dflt;
  }

  public static double getDouble(ActiveAttrList atts, String name,
				 double dflt) {
    String v = atts.getAttributeString(name);
    if (v == null) return dflt;
    Association a = Association.associateNumeric(null, v);
    return a.isNumeric()? a.doubleValue() : dflt;
  }

  /************************************************************************
  ** Input Conversion:
  ************************************************************************/

  /** Return a numeric Association between a node and its numeric value. 
   *	Return null if the node contains no numeric text.
   */
  public static Association getNumeric(Node n) {
    Association a = Association.associateNumeric(n, ListUtil.getFirstWord(n));
    return (a.isNumeric())? a : null;
  }

  /** Return a numeric Association between an object and its numeric value. 
   *	Return null if the object contains no numeric text.
   */
  public static Association getNumeric(Object o) {
    Association a = Association.associateNumeric(o, o.toString());
    return (a.isNumeric())? a : null;
  }

  /** Return a list of numeric Associations.  Recursively descends into
   *  	nodes with children, and splits text nodes containing whitespace. 
   *	Most useful for extracting <em>all</em> numbers from a piece of a 
   *	document.
   */
  public static Enumeration getNumbers(NodeList nl) {
    List l = new List();
    Enumeration items = ListUtil.getTextItems(nl);
    while (items.hasMoreElements()) {
      Association a = getNumeric((Node)items.nextElement());
      if (a != null) l.push(a);
    }
    return l.elements();
  }

  /** Return a list of numeric Associations.  Splits text nodes containing
   *	whitespace, but associates non-text markup with the numeric value of
   *	their first text item.  Most useful for sorting nodes numerically.
   */
  public static Enumeration getNumericList(NodeList nl) {
    List l = new List();
    Enumeration items = ListUtil.getListItems(nl);
    while (items.hasMoreElements()) {
      Association a = getNumeric((Node)items.nextElement());
      if (a != null) l.push(a);
    }
    return l.elements();
  }

  

  /************************************************************************
  ** Output Conversion:
  ************************************************************************/

  /** return the string representation with digits after the .*/
  public static String numberToString(double number, int digits)
  {
   if(digits < 0) return  java.lang.Double.toString(number);

  // round it off
     double factor = Math.pow(10,digits);
     long val = Math.round(number * factor);
     
     String s =  java.lang.Long.toString(val);
     while(s.length() < digits){
       s = "0"+s;
     }
     String result;
     
     int l = s.length();
     if(l>digits){
       result = s.substring(0,l-digits);
     }
     else{
       result = "0";
     }
     if(digits>0){
       result += "." + s.substring(l-digits);
     }
     return result;
  }



  

  /************************************************************************
  ** 
  ************************************************************************/


}
