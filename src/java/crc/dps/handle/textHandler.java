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
    if (dispatch(e, "split"))    return text_split.handle(e);
    if (dispatch(e, "join"))     return text_join.handle(e);
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

/** Sorts a list of strings in ascending order.  If the reverse
  * attribute is set, sorts in descending order.
  */
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
  protected int width     = -1;
  
  public void action(Input in, Context aContext, Output out, 
		     ActiveAttrList atts, NodeList content) {

    NodeList nl = TextUtil.getText(content);
    Enumeration resultList = TextUtil.trimListItems(content);
    putEnum(out, resultList);
  }

  public text_trim(ActiveElement e) {
    super(e);
    ActiveAttrList atts = (ActiveAttrList) e.getAttributes();
  }

  static Action handle(ActiveElement e) { return new text_trim(e); }

}


/** Pad text to the specified width with the specified alignment.
    The default is left, meaning that spaces are added to the
    right.  If text alignment is right, spaces are added to the
    left.  Center means that padding is added to the left and right
    so that the text is centered within the specified width.
*/
class text_pad extends textHandler {

  protected boolean pad  = false;
  protected boolean align = false;
  protected boolean left  = false;
  protected boolean right = false;
  protected boolean center = false;
  protected int width     = -1;
  
  public void action(Input in, Context aContext, Output out, 
		     ActiveAttrList atts, NodeList content) {

    NodeList nl = TextUtil.getText(content);
    Enumeration resultList = TextUtil.padListItems(content, align, left, right, 
						   center, width);
    putEnum(out, resultList);
  }

  public text_pad(ActiveElement e) {
    super(e);
    ActiveAttrList atts = (ActiveAttrList) e.getAttributes();
    align  = atts.hasTrueAttribute("align");
    left   = atts.hasTrueAttribute("left");
    right  = atts.hasTrueAttribute("right");
    center  = atts.hasTrueAttribute("center");
    width  = MathUtil.getInt(atts, "width", -1);
  
  }

  static Action handle(ActiveElement e) { return new text_pad(e); }

}


/** Split text at the whitespace and return a new node for each individual word.
    If the sep attribute has been specified, a new separator node is created and added
    to the node list after each text node.  A separator is not added after the last
    node on the list.  Marked up nodes are not split unless the text attribute is used.
    If the text attribute is specified, text is retrieved from marked up nodes and split.
*/
class text_split extends textHandler {

  protected boolean text = false;
  protected String sep = null;
  
  public void action(Input in, Context aContext, Output out, 
		     ActiveAttrList atts, NodeList content) {

    Enumeration resultList = null;
    Enumeration tmpEnum = null;
    // Text attribute extracts text and ignores mark up
    if(text) {
      NodeList nl = TextUtil.getText(content);
      tmpEnum = ListUtil.getListItems(nl);
    }
    else {
      tmpEnum = ListUtil.getListItems(content);
    }
    // Handle separator.  For each list item except the
    // last one, create a separator node and add to node list
    List rList = null;
    if(sep != null) {
      int count = 0;
      rList = new List();
      while (tmpEnum.hasMoreElements()) {
	if(count > 0) {
	  Node sepNode = new ParseTreeText(sep);
	  rList.push(sepNode);
	}
	rList.push((Node)tmpEnum.nextElement());
	count++;
      }
      resultList = rList.elements();
    } 
    else {
      resultList = tmpEnum;
    }
    putEnum(out, resultList);
  }

  public text_split(ActiveElement e) {
    super(e);
    ActiveAttrList atts = (ActiveAttrList) e.getAttributes();
    sep    = atts.getAttributeString("sep");
    text   = atts.hasTrueAttribute("text");
  }

  static Action handle(ActiveElement e) { return new text_split(e); }

}


/** 
    Replaces whitespace in text nodes with a separator and combines
    separated text into a single node.  Unless the text attribute is specified,
    marked up nodes are unchanged.  The result from text_join is a list of
    combined nodes and marked up nodes.  Separators are added between each node
    on the result list, as well as each item within the combined nodes.

    For example, if we start with nodes:  text1 text2 markup1 text3 text4 markup2
    and separator ";", the result would be a list of four nodes: 
    combined_text1;markup1;combined_text2;markup2.  combined_text1 looks like this:
    text1;text2.  

    Attributes are sep and text.  If sep is not specified, space is
    used as the separator, otherwise the specified separator is placed
    between each word in a combined node and between any combined
    nodes and marked up nodes.  If the text attribute is specified,
    text is extracted from marked up nodes, split at whitespace and
    joined using the sep attribute.
*/
class text_join extends textHandler {

  protected String sep = null;
  protected boolean text = false;
  
  public void action(Input in, Context aContext, Output out, 
		     ActiveAttrList atts, NodeList content) {

    Enumeration tmpEnum = null;
    List rList = new List();

    // Set default if separator not specified
    if(sep == null)
      sep = " ";

    // Text attribute extracts text and ignores mark up
    if(text) {
      NodeList nl = TextUtil.getText(content);
      tmpEnum = ListUtil.joinListItems(nl, sep);
    }
    else {
      tmpEnum = ListUtil.joinListItems(content, sep);
    }
    int count = 0;
    while (tmpEnum.hasMoreElements()) {
      if(count > 0) {
	Node sepNode = new ParseTreeText(sep);
	rList.push(sepNode);
      }
      rList.push((Node)tmpEnum.nextElement());
      count++;
    }
    putEnum(out, rList.elements());
  }

  public text_join(ActiveElement e) {
    super(e);
    ActiveAttrList atts = (ActiveAttrList) e.getAttributes();
    sep   = atts.getAttributeString("sep");
    text   = atts.hasTrueAttribute("text");
  }

  static Action handle(ActiveElement e) { return new text_join(e); }

}
