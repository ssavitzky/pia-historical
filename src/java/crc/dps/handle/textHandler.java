////// textHandler.java: <text> Handler implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.NodeEnumerator;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.Element;

import crc.ds.SortTree;
import crc.ds.List;
import crc.ds.Association;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.aux.*;

import java.util.Enumeration;

/**
 * Handler for &lt;text&gt;....&lt;/&gt;  <p>
 *
 *	
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class textHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;text&gt; node. */
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
    if (dispatch(e, "sort")) 	 return text_sort.handle(e);
    if (dispatch(e, "trim")) 	 return text_trim.handle(e);
    if (dispatch(e, "pad"))      return text_pad.handle(e);
    return this;
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public textHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }

  textHandler(ActiveElement e) {
    this();
    ActiveAttrList atts = (ActiveAttrList) e.getAttributes();
    // customize for element.
  }
}

class text_sort extends textHandler {

  protected boolean reverse   = false;
  protected boolean caseSens  = false;
  protected boolean pairs     = false;
  
  public void action(Input in, Context aContext, Output out, 
  		     ActiveAttrList atts, NodeList content) {

    List args = TextUtil.getTextList(content, caseSens);
    Enumeration argsEnum = args.elements();

    SortTree sorter = new SortTree();
    Association a;
    
    while(argsEnum.hasMoreElements()) {
      a = (Association)argsEnum.nextElement();
      sorter.insert(a, false);
    }

    List resultList = new List();
    if(reverse) {
      sorter.descendingValues(resultList);
    }
    else
      sorter.ascendingValues(resultList);
    
    putEnum(out, resultList.elements());

  }

  public text_sort(ActiveElement e) {
    super(e);
    ActiveAttrList atts = (ActiveAttrList) e.getAttributes();
    reverse  = atts.hasTrueAttribute("reverse");
    caseSens = atts.hasTrueAttribute("case");
    pairs    = atts.hasTrueAttribute("pairs");
  }

  static Action handle(ActiveElement e) { return new text_sort(e); }
}

/** Eliminate leading and trailing (optionally ALL) whitespace
    from CONTENT.  Whitespace inside markup is not affected.
*/

class text_trim extends textHandler {

  protected boolean trim  = false;
  protected boolean align = false;
  protected boolean left  = false;
  protected boolean right = false;
  protected int width     = -1;
  
  public void action(Input in, Context aContext, Output out, 
		     ActiveAttrList atts, NodeList content) {

    NodeList nl = TextUtil.getText(content);

    // Just retrieves text, no markup
    // printNodeList(nl);
    
    List list = TextUtil.getTextList(content, false);
    // printList(list);

    Enumeration resultList = TextUtil.trimListItems(content);
    
    putEnum(out, resultList);
  }

  public text_trim(ActiveElement e) {
    super(e);
    ActiveAttrList atts = (ActiveAttrList) e.getAttributes();
  }

  static Action handle(ActiveElement e) { return new text_trim(e); }

}


/** Eliminate leading and trailing (optionally ALL) whitespace
    from CONTENT.  Whitespace inside markup is not affected.
*/
class text_pad extends textHandler {

  protected boolean pad  = false;
  protected boolean align = false;
  protected boolean left  = false;
  protected boolean right = false;
  protected int width     = -1;
  
  public void action(Input in, Context aContext, Output out, 
		     ActiveAttrList atts, NodeList content) {

    NodeList nl = TextUtil.getText(content);
    Enumeration resultList = TextUtil.padListItems(content, align, left, right, width);
    putEnum(out, resultList);
  }

  public text_pad(ActiveElement e) {
    super(e);
    ActiveAttrList atts = (ActiveAttrList) e.getAttributes();
    align  = atts.hasTrueAttribute("align");
    left   = atts.hasTrueAttribute("left");
    right  = atts.hasTrueAttribute("right");
    width  = MathUtil.getInt(atts, "width", -1);
  
  }

  static Action handle(ActiveElement e) { return new text_pad(e); }

}




