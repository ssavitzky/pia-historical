////// ListUtil.java: List-Processing Utilities
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
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 */

public class ListUtil {

  /************************************************************************
  ** Value extraction:
  ************************************************************************/

  

  /************************************************************************
  ** Conversion:
  ************************************************************************/

  /** Convert a List (which is easy to manipulate) to a NodeList. */
  public static ParseNodeList toNodeList(List l) {
    return new ParseNodeList(l.elements());
  }

}
