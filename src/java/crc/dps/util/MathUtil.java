////// MathUtil.java: Mathematical Utilities
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.aux;

import crc.dom.Node;
import crc.dom.Element;
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
 * Mathematical utilities.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 */

public class MathUtil {

  /************************************************************************
  ** Input Conversion:
  ************************************************************************/

  /** Return a numeric Association between a node and its numeric value. 
   *	Return null if the node contains no numeric text.  <p>
   *
   *	Should really ignore markup.
   */
  public static Association getNumeric(Node n) {
    String cstring = n.toString();
    Association a = Association.associateNumeric(n, cstring);
    return (a.isNumeric())? a : null;
  }

  /** Return a list of numeric Associations. */
  public static List getNumbers(NodeList nl) {
    List l = new List();
    NodeEnumerator enum = nl.getEnumerator();
    for (Node n = enum.getFirst(); n != null; n = enum.getNext()) {
      Association a = getNumeric(n);
      if (a != null && a.isNumeric()) l.push(a);
    }
    return l;
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
