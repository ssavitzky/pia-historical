////// Index.java: Utilities for handling index expressions
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
import crc.dom.NodeEnumerator;

import crc.dps.NodeType;
import crc.dps.Context;
import crc.dps.EntityTable;
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
      return getValue(c, index.substring(0, i+1), null);
    } else if (i >= 0) {
      return getValue(c, index.substring(0, i+1), index.substring(i+1));
    } else {
      EntityTable ents = c.getEntities();
      return ents.getEntityValue(index, false);
    }
  }

  public static void setIndexValue(Context c, String index, NodeList value) {
    EntityTable ents = c.getEntities();
    ents.setEntityValue(index, value, false);
  }

  /** Get a value using a name and namespace. 
   *
   * @param c the context in which to do the lookup.
   * @param space the name of the namespace (ending with colon!)
   * @param name  the name within the namespace.  If name is null,
   *	the entire namespace is returned.
   */
  public static NodeList getValue(Context c, String space, String name) {
    EntityTable ents = c.getEntities();
    NodeList nl = ents.getEntityValue(space, false);

    // If there's nothing there or we want the whole space, return it.
    if (nl == null || name == null) return nl;

    // There are three possibilities at this point: 
 
    //	1. the value of the entity _is_ the namespace, e.g. an AttributeList
    if (nl instanceof ActiveAttrList) {
      return ((ActiveAttrList)nl).getAttributeValue(name);
    }

    //  2. the value of the entity _contains_ a namespace.
    NodeEnumerator ne = nl.getEnumerator();
    for (Node n = ne.getFirst(); n != null; n = ne.getNext()) {
      if (n instanceof EntityTable) {
	return ((EntityTable)n).getEntityValue(name, false);
      }
    }

    //  3. we're out of luck.
    return null;
  }

  public static void setValue(Context c, String space, String name,
			      NodeList value) {
    EntityTable ents = c.getEntities();
    NodeList nl = ents.getEntityValue(space, false);
    //ents.setEntityValue(index, value, false);
  }

  /************************************************************************
  ** Auxiliary Methods:
  ************************************************************************/


}
