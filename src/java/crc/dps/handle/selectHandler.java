////// selectHandler.java: <select> Handler implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.NodeEnumerator;
import crc.dom.Element;
import crc.dom.Entity;
import crc.dom.Text;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.util.*;
import crc.dps.output.ToNodeList;

import crc.ds.Association;
import crc.ds.List;

import crc.gnu.regexp.RegExp;
import crc.gnu.regexp.MatchInfo;

import java.util.Enumeration;

/**
 * Handler for &lt;select&gt;....&lt;/&gt;  <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */
public class selectHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;select&gt; node. */
  public void action(Input in, Context cxt, Output out) {
    String tag = in.getTagName();
    ActiveAttrList atts = Expand.getExpandedAttrs(in, cxt);
    String sep = (atts == null)? null : atts.getAttributeString("sep");

    BasicEntityTable ents = new BasicEntityTable(tag);
    ToNodeList collect = new ToNodeList();
    NodeList currentSet = null;
    boolean terminateSelect = false;

    // === strictly speaking, this should use the tagset to create the entity.
    ParseTreeEntity selected = new ParseTreeEntity("selected");
    ents.setBinding("selected", selected);
    Processor process = cxt.subProcess(in, collect, ents);

    for (Node item = in.toFirstChild();
	 item != null;
	 item = in.toNextSibling()) {
      if (terminateSelect) continue;

      switch (item.getNodeType()) {
      case NodeType.COMMENT:
      case NodeType.PI:
      case NodeType.DECLARATION:
	break;

      case NodeType.TEXT:
	ActiveText t = in.getActive().asText();
	if (! t.getIsWhitespace()) {
	  Enumeration items = ListUtil.getTextItems(t.getData());

	  while (items.hasMoreElements()) {
	    currentSet = selectTextItem(items.nextElement().toString(),
					selected.getValue());
	    if (currentSet.getLength() == 0) terminateSelect = true;
	    selected.setValue(currentSet);
	  }

	  // === really ought to have something in the handler that tells the
	  // === parser whether to split text content into words or not.

	}
	break;

      default:
	process.processNode();
	currentSet = collect.getList();
	if (currentSet.getLength() == 0) terminateSelect = true;
	selected.setValue(currentSet);
	collect.clearList();
      }
    }
    in.toParent();

    putList(out, selected.getValue(), sep);
  }

  /** This does the parse-time dispatching. <p>
   *
   *	Action is dispatched (delegated) to a subclass if the string
   *	being passed to <code>dispatch</code> is either the name of an
   *	attribute or a period-separated suffix of the tagname. <p>
   */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    //    if (dispatch(e, "")) 	 return select_.handle(e);
    return this;
  }

  /************************************************************************
  ** Utilities:
  ************************************************************************/

  /** Returns the current set of selected nodes.  
   *	Works by looking up the appropriate entity in the given context.
   *	Used by sub-element handlers.
   */
  public NodeList getSelected(Context cxt) {
    return cxt.getEntityValue("selected", true);
  }

  /** Handle a numeric item. */
  public NodeList  selectNumericItem(int n, NodeList selected) {
    List items = new List(ListUtil.getListItems(selected));
    ActiveNode item = null;
    if (n >= 0) {
      item = (ActiveNode) items.at(n);
    } else {
      item = (ActiveNode) items.at(items.nItems() + n);
    }
    return new ParseNodeList(item);
  }

  /** Handle a text item (either name, type, or number). */
  public NodeList  selectTextItem(String item, NodeList selected) {
    Association n = MathUtil.getNumeric(item);
    if (n != null) return selectNumericItem((int)n.longValue(), selected);
    else if (item.startsWith("#")) return selectTypeItem(item, selected);
    else return selectNameItem(item, selected);
  }

  public void selectByType(String item, NodeList selected, Output out) {
    NodeEnumerator e = selected.getEnumerator();
    if (item.startsWith("#")) item = item.substring(1);
    int nodeType = NodeType.getType(item);
    if (nodeType == NodeType.ALL) putList(out, selected);

    for (Node node = e.getFirst(); node != null; node = e.getNext()) 
      if (node.getNodeType() == nodeType) out.putNode(node);
  }

  public NodeList selectTypeItem(String tname, NodeList selected) {
    ToNodeList out = new ToNodeList();
    selectByType(tname, selected, out);
    return out.getList();
  }

  public NodeList selectNameItem(String name, NodeList selected) {
    ToNodeList out = new ToNodeList();
    selectByName(name, selected, out);
    return out.getList();
  }

  /** Select items by name and put them to an Output. */
  public void selectByName(String name, NodeList selected, Output out) {
    if (selected == null) return;
    NodeEnumerator e = selected.getEnumerator();
    for (Node node = e.getFirst(); node != null; node = e.getNext()) {
      String nn = getNodeName(node, true);
      if (nn != null && nn.equals(name)) out.putNode(node);
    }
  }

  /** Match a node's name. */
  public String getNodeName(Node node, boolean caseSens) {
    // === This is kludgy, but will be fixed in the new DOM
    // === not handling node type yet!
    String name = null;
    if (node instanceof ParseTreeNamed) {
      ParseTreeNamed nnode = (ParseTreeNamed)node;
      name = nnode.getName();
    } else if (node instanceof Element) {
      Element elt = (Element)node;
      name = elt.getTagName();
    }
    if (name != null && !caseSens) name = name.toLowerCase();
    return name;
  }

  /** Return a key string from a node. */
  public String getNodeKey(Node item, boolean caseSens, String sep) {
    String key = null;
    if (!caseSens && sep != null) sep = sep.toLowerCase();
    if (item.getNodeType() == NodeType.ATTRIBUTE) {
      Attribute n = (Attribute)item;
      key = n.getName();
    } else if (item.getNodeType() == NodeType.ENTITY) {
      Entity n = (Entity)item;
      key = n.getName();
    } else if (item.getNodeType() == NodeType.TEXT) {
      Text t = (Text)item;
      key = (t.getIsIgnorableWhitespace())? null : t.getData();
    } else {
      key = TextUtil.getTextString((ActiveNode)item);
      if (key != null) key = key.trim();
    }
    if (!caseSens && key != null) key = key.toLowerCase();
    if (sep != null && key != null) {
      if (key.indexOf(sep)>=0) {
	key = key.substring(0, key.indexOf(sep));
      } else {
	key = null;
      }
    }
    return key;
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public selectHandler() {
    /* Expansion control: */
    expandContent = false;	// true		Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }

  selectHandler(ActiveElement e) {
    this();
    // customize for element.
  }

  /** This constructor is used by sub-elements to control their syntax. */
  selectHandler(boolean expand, boolean text) {
    /* Expansion control: */
    expandContent = expand;
    textContent = text;

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }
}


/************************************************************************
** Sub-Elements:
***********************************************************************/

/**
 * Parent Handler for &lt;select&gt; sub-elements.   <p>
 *
 *	Sub-element handlers do not inherit directly from
 *	<code>selectHandler</code> because essentially all (perhaps all)
 *	sub-elements will want to use the five-argument <code>action</code>
 *	method.  <code>selectHandler</code> itself does not, however, so we
 *	need to restore the three-argument action to its original
 *	functionality.
 */
class select_subHandler extends selectHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Restore the default action. */
  public void action(Input in, Context aContext, Output out) {
    defaultAction(in, aContext, out);
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  public select_subHandler() { this(true, false); }

  /** This constructor is used by sub-elements to control their syntax. */
  select_subHandler(boolean expand, boolean text) {
    /* Expansion control: */
    expandContent = expand;
    textContent = text;

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }
}

/** &lt;from&gt; simply sets the current set to its (expanded) content. */
class fromHandler extends select_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    putList(out, content);
  }
  fromHandler() { super(true, false); }
}

class inHandler extends select_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    unimplemented(in, aContext);
  }

  /** Content is text only, because all we want is the name */
  inHandler() { super(true, true); }
}

/** &lt;child&gt;<em>n</em>&lt;/&gt; selects the <em>n</em>th child
 *	of each selected node. */
class childHandler extends select_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList selected = getSelected(aContext);
    NodeEnumerator items = selected.getEnumerator();
    Enumeration terms = ListUtil.getTextItems(content);
    int termsHandled = 0;

    while (terms.hasMoreElements()) {
      ++termsHandled;
      String term = terms.nextElement().toString();
      for (Node item = items.getFirst(); item != null; item = items.getNext()) {
	if (item.hasChildren()) {
	  putList(out, selectTextItem(term, item.getChildren()));
	}
      }
    }
    if (termsHandled == 0) {
      // no terms.  Return all the children.
      for (Node item = items.getFirst(); item != null; item = items.getNext()) {
	if (item.hasChildren()) {
	  putList(out, item.getChildren());
	}
      }
    }
  }
  childHandler() { super(true, false); }
}

/** &lt;nodes&gt;<em>term</em>*&lt;/&gt; selects each node matching a term. */
class nodesHandler extends select_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList selected = getSelected(aContext);
    Enumeration terms = ListUtil.getTextItems(content);
    int termsHandled = 0;

    while (terms.hasMoreElements()) {
      String term = terms.nextElement().toString();
      putList(out, selectTextItem(term, selected));
    }
  }
  nodesHandler() { super(true, false); }
}

/** &lt;name&gt;<em>n</em>&lt;/&gt; selects every node with a matching name.
 */
class nameHandler extends select_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList selected = getSelected(aContext);
    String name = content.toString();
    if (name != null && name.startsWith("#"))
      selectByType(name, selected, out);
    else selectByName(name, selected, out);
  }
  nameHandler() { super(true, false); }
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "recursive")) 	return new name_recursive();
    if (dispatch(e, "all")) 	 	return new name_recursive();
    return this;
  }
}

/** &lt;name recursive&gt;<em>n</em>&lt;/&gt; 
 *	selects every node with a matching name; recurses into content.
 */
class name_recursive extends select_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList selected = getSelected(aContext);
    boolean caseSens = atts.hasTrueAttribute("case");
    boolean all = atts.hasTrueAttribute("all");
    String key = content.toString();
    if (key != null && !caseSens) key = key.toLowerCase();
    if (key != null && key.startsWith("#")) {
      int ntype = NodeType.getType(key.substring(1));
      selectByType(selected, ntype, all, out);
    } else {
      select(selected, key, caseSens, all, out);
    }
  }
  protected void select(NodeList selected, String key, 
			boolean caseSens, boolean all, Output out) {
    NodeEnumerator items = selected.getEnumerator();
    for (Node item = items.getFirst(); item != null; item = items.getNext()) {
      String name = getNodeName(item, caseSens);
      if (key == null || (name != null && key.equals(name))) {
	out.putNode(item);
      } else if (!all && item.hasChildren()) {
	select(item.getChildren(), key, caseSens, all, out);
      }
      if (all && item.hasChildren()) {
	select(item.getChildren(), key, caseSens, all, out);
      }
    }
  }
  protected void selectByType(NodeList selected, int ntype,
			      boolean all, Output out) {
    NodeEnumerator items = selected.getEnumerator();
    for (Node item = items.getFirst(); item != null; item = items.getNext()) {
      if (ntype == NodeType.ALL || ntype == item.getNodeType()) {
	out.putNode(item);
      } else if (!all && item.hasChildren()) {
	selectByType(item.getChildren(), ntype, all, out);
      }
      if (all && item.hasChildren()) {
	selectByType(item.getChildren(), ntype, all, out);
      }
    }
  }
  name_recursive() { super(true, false); }
}

/** &lt;key&gt;<em>k</em>&lt;/&gt; selects every node with a matching key. */
class keyHandler extends select_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList selected = getSelected(aContext);
    boolean caseSens = atts.hasTrueAttribute("case");
    String sep = atts.getAttributeString("sep");
    String key = content.toString();
    if (key != null && !caseSens) key = key.toLowerCase();
    NodeEnumerator items = selected.getEnumerator();
    for (Node item = items.getFirst(); item != null; item = items.getNext()) {
      String k = getNodeKey(item, caseSens, sep);
      if (key == null || key.equals(k))	out.putNode(item);
    }    
  }
  keyHandler() { super(true, true); } // text only
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "recursive")) 	return new key_recursive();
    if (dispatch(e, "all")) 	 	return new key_recursive();
    return this;
  }
}

/** &lt;key recursive&gt;<em>n</em>&lt;/&gt; 
 *	selects every node with a matching key; recurses into content.
 */
class key_recursive extends select_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList selected = getSelected(aContext);
    boolean caseSens = atts.hasTrueAttribute("case");
    String  sep = atts.getAttributeString("sep");
    boolean all = atts.hasTrueAttribute("all");
    String  key = content.toString();
    if (key != null && !caseSens) key = key.toLowerCase();
    select(selected, key, caseSens, sep, all, out);
  }
  protected void select(NodeList selected, String key, boolean caseSens,
			String sep, boolean all, Output out) {
    NodeEnumerator items = selected.getEnumerator();
    for (Node item = items.getFirst(); item != null; item = items.getNext()) {
      String k = getNodeKey(item, caseSens, sep);
      if (key == null || (k != null && key.equals(k))) {
	out.putNode(item);
      } else if (!all && item.hasChildren()) {
	select(item.getChildren(), key, caseSens, sep, all, out);
      }
      if (all && item.hasChildren()) {
	select(item.getChildren(), key, caseSens, sep, all, out);
      }
    }
  }
  key_recursive() { super(true, false); }
}

/** &lt;attr&gt;<em>n</em>&lt;/&gt; selects every Attribute with matching name.
 */
class attrHandler extends select_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList selected = getSelected(aContext);
    boolean caseSens = atts.hasTrueAttribute("case");
    String name = content.toString();
    
    NodeEnumerator items = selected.getEnumerator();
    for (Node item = items.getFirst(); item != null; item = items.getNext()) {
      if (item.getNodeType() == NodeType.ATTRIBUTE) {
	Attribute n = (Attribute)item;
	if (name == null
	    || (caseSens && name.equals(n.getName()))
	    || (!caseSens && name.equalsIgnoreCase(n.getName())))
	  out.putNode(item);
      } else if (item.getNodeType() == NodeType.ELEMENT) {
	Element e = (Element)item;
	AttributeList atl = e.getAttributes();
	Attribute a = (atl == null)? null : atl.getAttribute(name);
	if (a != null) out.putNode(a);
      }
    }    
  }
  attrHandler() { super(true, true); } // text only.
}

/** &lt;match&gt;<em>re</em>&lt;/&gt; selects every node matching re. */
class matchHandler extends select_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList selected = getSelected(aContext);
    boolean caseSens = atts.hasTrueAttribute("case");
    String match = content.toString();
    if (!caseSens) match = match.toLowerCase();
    NodeEnumerator items = selected.getEnumerator();
    for (Node item = items.getFirst(); item != null; item = items.getNext()) {
      String k = getNodeKey(item, caseSens, null);
      try {
	RegExp re = new RegExp(match);
	MatchInfo mi = re.match(k);
	if (mi != null && mi.end() >= 0) out.putNode(item);
      } catch (Exception ex) {
	// === ii.error(ia, "Exception in regexp: "+ex.toString());
      }
    }
  }
  matchHandler() { super(true, true); }
}

/** &lt;xptr&gt;<em>xp</em>&lt;/&gt; selects using an XPointer. */
class xptrHandler extends select_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList selected = getSelected(aContext);
    boolean caseSens = atts.hasTrueAttribute("case");
    unimplemented(in, aContext);		// === xptr
  }
  xptrHandler() { super(true, true); }
}

/** &lt;parent&gt; selects every node's parent. */
class parentHandler extends select_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList selected = getSelected(aContext);
    NodeEnumerator items = selected.getEnumerator();
    Node previous = null;
    for (Node item = items.getFirst(); item != null; item = items.getNext()) {
      Node p = item.getParentNode();
      if (p != null && p != previous ) {
	out.putNode(p);
	previous = p;
      }
    }
  }
  parentHandler() { super(true, false); syntaxCode = EMPTY; }
}

/** &lt;content&gt; selects every node's content (children). */
class contentHandler extends select_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList selected = getSelected(aContext);
    NodeEnumerator items = selected.getEnumerator();
    for (Node item = items.getFirst(); item != null; item = items.getNext()) {
      if (item.hasChildren()) { putList(out, item.getChildren()); }
    }
  }
  contentHandler() { super(true, false); syntaxCode = EMPTY; }
}

/** &lt;next&gt; selects every node's successor. */
class nextHandler extends select_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList selected = getSelected(aContext);
    NodeEnumerator items = selected.getEnumerator();
    for (Node item = items.getFirst(); item != null; item = items.getNext()) {
      Node n = item.getNextSibling();
      if (n != null) out.putNode(n);
    }
  }
  nextHandler() { super(true, false); syntaxCode = EMPTY; }
}

/** &lt;prev&gt; selects every node's predecessor. */
class prevHandler extends select_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList selected = getSelected(aContext);
    NodeEnumerator items = selected.getEnumerator();
    for (Node item = items.getFirst(); item != null; item = items.getNext()) {
      Node n = item.getPreviousSibling();
      if (n != null) out.putNode(n);
    }
  }
  prevHandler() { super(true, false); syntaxCode = EMPTY; }
}

/** &lt;eval&gt; selects every node's value. */
class evalHandler extends select_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList selected = getSelected(aContext);
    NodeEnumerator items = selected.getEnumerator();
    for (Node item = items.getFirst(); item != null; item = items.getNext()) {
      ActiveNode a = (ActiveNode)item;
      NodeList v = null;	
      switch (item.getNodeType()) {
      case NodeType.ATTRIBUTE:
	v = a.asAttribute().getValue();
	if (v != null) putList(out, v);
	break;

      case NodeType.ENTITY:
	ActiveEntity ent = a.asEntity();
	v = ent.getValue();
	if (v == null) v = Index.getIndexValue(aContext, ent.getName());
	if (v != null) putList(out, v);
	break;

      case NodeType.ELEMENT:
      case NodeType.TEXT:
	out.putNode(item);
	break;
      }
    }
  }
  evalHandler() { super(true, false); syntaxCode = EMPTY; }
}

