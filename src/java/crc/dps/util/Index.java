////// Index.java: Utilities for handling index expressions
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
import crc.dom.Element;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.Entity;
import crc.dom.NodeEnumerator;

import crc.dps.NodeType;
import crc.dps.Context;
import crc.dps.EntityTable;
import crc.dps.Namespace;
import crc.dps.Tagset;
import crc.dps.active.*;
import crc.dps.output.*;

import crc.ds.Table;
import crc.ds.Association;

import java.util.Enumeration;

/**
 * Index Expression Utilities.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 */
public class Index {

  /************************************************************************
  ** Front End:
  ************************************************************************/

  /** Get a value using an index. */
  public static NodeList getIndexValue(Context c, String index) {
    int i = index.indexOf(':');
    if (i == index.length() -1) {
      return getValue(c, index.substring(0, i), null);
    } else if (i >= 0) {
      return getValue(c, index.substring(0, i), index.substring(i+1));
    } else {
      return c.getEntityValue(index, false);
    }
  }

  public static void setIndexValue(Context c, String index, NodeList value) {
    int i = index.indexOf(':');
    if (i == index.length() -1) {
      setValue(c, index.substring(0, i), null, value);
    } else if (i >= 0) {
      setValue(c, index.substring(0, i), index.substring(i+1), value);
    } else {
      c.setEntityValue(index, value, false);
    }
  }

  /** Get a value using a name and namespace. 
   *
   * @param c the context in which to do the lookup.
   * @param space the name of the namespace (ending with colon!)
   * @param name  the name within the namespace.  If name is null,
   *	the entire namespace is returned.
   */
  public static NodeList getValue(Context c, String space, String name) {
    Namespace ns = c.getNamespace(space);

    // If there's nothing there, return null.
    if (ns == null) return null;

    // If we wanted the whole space, return its list of bindings.
    if (name == null) return new ParseNodeList(ns.getBindings());
    else return ns.getValueNodes(c, name);
  }

  public static void setValue(Context c, String space, String name,
			      NodeList value) {
    Namespace ns = c.getNamespace(space);
    Tagset ts = c.getTopContext().getTagset();

    if (ns != null) {
      ns.setValueNodes(c, name, value);
    } else {
      // If there's nothing there, make a namespace and populate it.
      BasicEntityTable ents = new BasicEntityTable(space);
      ents.setEntityValue(c, name, value, ts);
    }
  }

}
