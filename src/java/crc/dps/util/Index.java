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
    EntityTable ents = c.getEntities();
    return ents.getEntityValue(index, false);
  }

  public static void setIndexValue(Context c, String index, NodeList value) {
    EntityTable ents = c.getEntities();
    ents.setEntityValue(index, value, false);
  }

  /************************************************************************
  ** Auxiliary Methods:
  ************************************************************************/


}
