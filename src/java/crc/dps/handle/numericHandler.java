////// numericHandler.java: <numeric> Handler implementation
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


package crc.dps.handle;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.Element;

import crc.ds.SortTree;
import crc.ds.List;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.util.*;

import crc.ds.Association;
import java.util.Enumeration;

/**
 * Handler for &lt;numeric&gt;....&lt;/&gt;  <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class numericHandler extends GenericHandler {

  protected boolean integerOp  = false;
  protected boolean extendedOp = false;
  protected int     digits     = -1;
  protected long    modulus    = 0;

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;numeric&gt; node. */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    // Actually do the work. 
  }

  /** This does the parse-time dispatching. <p>
   *
   *	Action is dispatched (delegated) to a subclass if the string
   *	being passed to <code>dispatch</code> is either the name of an
   *	attribute or a period-separated suffix of the tagname. <p>
   */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "sum")    ) 	 return numeric_sum.handle(e);
    if (dispatch(e, "difference")) 	 return numeric_difference.handle(e);
    if (dispatch(e, "product")) 	 return numeric_product.handle(e);
    if (dispatch(e, "quotient")) 	 return numeric_quotient.handle(e);
    if (dispatch(e, "remainder")) 	 return numeric_remainder.handle(e);
    if (dispatch(e, "sort"))    	 return numeric_sort.handle(e);
    return this;
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public numericHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }

  numericHandler(ActiveElement e) {
    this();
    ActiveAttrList atts = (ActiveAttrList) e.getAttributes();
    digits       = MathUtil.getInt(atts, "digits", -1);
    extendedOp   = atts.hasTrueAttribute("extended");
    long modulus = MathUtil.getLong(atts, "modulus", 0);

    integerOp    = extendedOp || (modulus != 0)
      		   || atts.hasTrueAttribute("integer");

    // customize for element. integer, extended, modulus
  }
}

class numeric_sum extends numericHandler {
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    Enumeration args = MathUtil.getNumbers(content);
    double fresult = 0;
    long   iresult = 0;
    boolean intOp  = true;

    Association a;
    while (args.hasMoreElements()) {
      a = (Association)args.nextElement();
      if (!integerOp && ! a.isIntegral()) intOp = false;
      fresult += a.doubleValue();
      if (intOp) {
	iresult += a.longValue();
	if (modulus != 0) iresult %= modulus;
      }
    }
    if (intOp) {
      putText(out, cxt, "" + iresult);
    } else {
      putText(out, cxt, MathUtil.numberToString(fresult, digits));
    }
  }
  public numeric_sum(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new numeric_sum(e); }
}

class numeric_difference extends numericHandler {
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    Enumeration args = MathUtil.getNumbers(content);
    double fresult = 0;
    long   iresult = 0;
    boolean intOp  = true;

    Association a;
    if (args.hasMoreElements()) {
      a = (Association)args.nextElement();
      if (!integerOp && ! a.isIntegral()) intOp = false;
      fresult = a.doubleValue();
      if (intOp) {
	iresult = a.longValue();
	if (modulus != 0) iresult %= modulus;
      }
    }
    while (args.hasMoreElements()) {
      a = (Association)args.nextElement();
      if (!integerOp && ! a.isIntegral()) intOp = false;
      fresult -= a.doubleValue();
      if (intOp) {
	iresult -= a.longValue();
	if (modulus != 0) iresult %= modulus;
      }
    }
    if (intOp) {
      putText(out, cxt, "" + iresult);
    } else {
      putText(out, cxt, MathUtil.numberToString(fresult, digits));
    }
  }
  public numeric_difference(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new numeric_difference(e); }
}

class numeric_product extends numericHandler {
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    Enumeration args = MathUtil.getNumbers(content);
    double fresult = 1.0;
    long   iresult = 1;
    boolean intOp  = true;

    Association a;
    while (args.hasMoreElements()) {
      a = (Association)args.nextElement();
      if (!integerOp && ! a.isIntegral()) intOp = false;
      fresult *= a.doubleValue();
      if (intOp)  {
	iresult *= a.longValue();
	if (modulus != 0) iresult %= modulus;
      }
    }
    if (intOp) {
      putText(out, cxt, "" + iresult);
    } else {
      putText(out, cxt, MathUtil.numberToString(fresult, digits));
    }
  }
  public numeric_product(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new numeric_product(e); }
}

class numeric_quotient extends numericHandler {
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    Enumeration args = MathUtil.getNumbers(content);
    double fresult = 1.0;
    long   iresult = 1;
    boolean intOp  = integerOp;

    Association a;
    if (args.hasMoreElements()) {
      a = (Association)args.nextElement();
      if (!integerOp && ! a.isIntegral()) intOp = false;
      fresult = a.doubleValue();
      if (intOp) {
	iresult = a.longValue();
	if (modulus != 0) iresult %= modulus;
      }
    }
    while (args.hasMoreElements()) {
      a = (Association)args.nextElement();
      if (!integerOp && ! a.isIntegral()) intOp = false;
      fresult /= a.doubleValue();
      if (intOp) {
	iresult /= a.longValue();
	if (modulus != 0) iresult %= modulus;
      }
    }
    if (intOp) {
      putText(out, cxt, "" + iresult);
    } else {
      putText(out, cxt, MathUtil.numberToString(fresult, digits));
    }
  }
  public numeric_quotient(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new numeric_quotient(e); }
}


class numeric_power extends numericHandler {
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    Enumeration args = MathUtil.getNumbers(content);
    double fresult = 1.0;
    long   iresult = 1;
    boolean intOp  = true;

    Association a;
    if (args.hasMoreElements()) {
      a = (Association)args.nextElement();
      if (!integerOp && ! a.isIntegral()) intOp = false;
      fresult = a.doubleValue();
      if (intOp) {
	iresult = a.longValue();
	if (modulus != 0) iresult %= modulus;
      }
    }
    while (args.hasMoreElements()) {
      a = (Association)args.nextElement();
      if (!integerOp && ! a.isIntegral()) intOp = false;
      fresult = Math.pow(fresult, a.doubleValue());
      if (intOp) {
	iresult = (long) Math.pow(iresult, a.longValue());
	if (modulus != 0) iresult %= modulus;
      }
    }
    if (intOp) {
      putText(out, cxt, "" + iresult);
    } else {
      putText(out, cxt, MathUtil.numberToString(fresult, digits));
    }
  }
  public numeric_power(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new numeric_power(e); }
}


class numeric_remainder extends numericHandler {
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    Enumeration args = MathUtil.getNumbers(content);
    double fresult = 0;
    long   iresult = 0;
    boolean intOp  = true;

    Association a;
    if (args.hasMoreElements()) {
      a = (Association)args.nextElement();
      if (!integerOp && ! a.isIntegral()) intOp = false;
      fresult = a.doubleValue();
      if (intOp) {
	iresult = a.longValue();
	if (modulus != 0) iresult %= modulus;
      }
    }
    while (args.hasMoreElements()) {
      a = (Association)args.nextElement();
      if (!integerOp && ! a.isIntegral()) intOp = false;
      fresult = Math.IEEEremainder(fresult, a.doubleValue());
      if (intOp) {
	iresult %= a.longValue();
	if (modulus != 0) iresult %= modulus;
      }
    }
    if (intOp) {
      putText(out, cxt, "" + iresult);
    } else {
      putText(out, cxt, MathUtil.numberToString(fresult, digits));
    }
  }
  public numeric_remainder(ActiveElement e) { super(e); }
  static Action handle(ActiveElement e) { return new numeric_remainder(e); }
}

class numeric_sort extends numericHandler {
  protected boolean reverse  = false;
  protected boolean caseSens = false;
  protected boolean pairs    = false;

  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    Enumeration args = MathUtil.getNumbers(content);

    SortTree sorter = new SortTree();
    Association a;
    while(args.hasMoreElements()) {
      a = (Association)args.nextElement();
      sorter.insert(a, true);
    }
    List resultList = new List();
    if(reverse)
      sorter.descendingValues(resultList);
    else
      sorter.ascendingValues(resultList);

    putEnum(out, resultList.elements());
  }

  public numeric_sort(ActiveElement e) {
    super(e);
    ActiveAttrList atts = (ActiveAttrList) e.getAttributes();
    reverse  = atts.hasTrueAttribute("reverse");
    caseSens = atts.hasTrueAttribute("case");
    pairs    = atts.hasTrueAttribute("pairs");
  }
  static Action handle(ActiveElement e) { return new numeric_sort(e); }
}

