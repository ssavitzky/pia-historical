////// extractHandler.java: <extract> Handler implementation
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
import crc.ds.SortTree;

import gnu.regexp.RegExp;
import gnu.regexp.MatchInfo;

import java.util.Enumeration;

/**
 * Handler for &lt;extract&gt;....&lt;/&gt;  <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */
public class extractHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;extract&gt; node. */
  public void action(Input in, Context cxt, Output out) {
    String tag = in.getTagName();
    ActiveAttrList atts = Expand.getExpandedAttrs(in, cxt);
    String sep = (atts == null)? null : atts.getAttributeString("sep");

    BasicEntityTable ents = new BasicEntityTable(tag);
    ToNodeList collect = new ToNodeList();
    NodeList currentSet = null;
    boolean terminateExtract = false;

    // === strictly speaking, this should use the tagset to create the entity.
    ParseTreeEntity extracted = new ParseTreeEntity("list");
    ents.setBinding("list", extracted);
    Processor process = cxt.subProcess(in, collect, ents);

    for (Node item = in.toFirstChild();
	 item != null;
	 item = in.toNextSibling()) {
      if (terminateExtract) continue;

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
	    currentSet = extractTextItem(items.nextElement().toString(),
					extracted.getValue());
	    if (currentSet.getLength() == 0) terminateExtract = true;
	    extracted.setValue(currentSet);
	  }

	  // === really ought to have something in the handler that tells the
	  // === parser whether to split text content into words or not.

	}
	break;

      default:
	process.processNode();
	currentSet = collect.getList();
	if (currentSet.getLength() == 0) terminateExtract = true;
	extracted.setValue(currentSet);
	collect.clearList();
      }
    }
    in.toParent();

    putList(out, extracted.getValue(), sep);
  }

  /** This does the parse-time dispatching. <p>
   *
   *	Action is dispatched (delegated) to a subclass if the string
   *	being passed to <code>dispatch</code> is either the name of an
   *	attribute or a period-separated suffix of the tagname. <p>
   */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    //    if (dispatch(e, "")) 	 return extract_.handle(e);
    return this;
  }

  /************************************************************************
  ** Utilities:
  ************************************************************************/

  /** Returns the current set of extracted nodes.  
   *	Works by looking up the appropriate entity in the given context.
   *	Used by sub-element handlers.
   */
  public NodeList getExtracted(Context cxt) {
    return cxt.getEntityValue("list", true);
  }

  /** Handle a numeric item. */
  public NodeList  extractNumericItem(int n, NodeList extracted) {
    List items = new List(ListUtil.getListItems(extracted));
    ActiveNode item = null;
    if (n >= 0) {
      item = (ActiveNode) items.at(n);
    } else {
      item = (ActiveNode) items.at(items.nItems() + n);
    }
    return new ParseNodeList(item);
  }

  /** Handle a text item (either name, type, or number). */
  public NodeList  extractTextItem(String item, NodeList extracted) {
    Association n = MathUtil.getNumeric(item);
    if (n != null) return extractNumericItem((int)n.longValue(), extracted);
    else if (item.startsWith("#")) return extractTypeItem(item, extracted);
    else return extractNameItem(item, extracted);
  }

  public void extractByType(String item, NodeList extracted, Output out) {
    NodeEnumerator e = extracted.getEnumerator();
    if (item.startsWith("#")) item = item.substring(1);
    int nodeType = NodeType.getType(item);
    if (nodeType == NodeType.ALL) putList(out, extracted);

    for (Node node = e.getFirst(); node != null; node = e.getNext()) 
      if (node.getNodeType() == nodeType) out.putNode(node);
  }

  public NodeList extractTypeItem(String tname, NodeList extracted) {
    ToNodeList out = new ToNodeList();
    extractByType(tname, extracted, out);
    return out.getList();
  }

  public NodeList extractNameItem(String name, NodeList extracted) {
    ToNodeList out = new ToNodeList();
    extractByName(name, extracted, out);
    return out.getList();
  }

  /** Extract items by name and put them to an Output. */
  public void extractByName(String name, NodeList extracted, Output out) {
    if (extracted == null) return;
    NodeEnumerator e = extracted.getEnumerator();
    for (Node node = e.getFirst(); node != null; node = e.getNext()) {
      String nn = getNodeName(node, true);
      if (nn != null && nn.equals(name)) out.putNode(node);
    }
  }

  /** Return a node's name. */
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

  /** Return a node's ID (name or id attribute). */
  public String getNodeId(Node node, boolean caseSens) {
    // === This is kludgy, but will be fixed in the new DOM
    // === not handling node type yet!
    String name = null;
    if (node instanceof ParseTreeNamed) {
      ParseTreeNamed nnode = (ParseTreeNamed)node;
      name = nnode.getName();
    } else if (node instanceof ActiveElement) {
      ActiveElement elt = (ActiveElement)node;
      name = elt.getAttributeString("id");
      if (name == null) name = elt.getAttributeString("name");
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
  public extractHandler() {
    /* Expansion control: */
    expandContent = false;	// true		Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }

  extractHandler(ActiveElement e) {
    this();
    // customize for element.
  }

  /** This constructor is used by sub-elements to control their syntax. */
  extractHandler(boolean expand, boolean text) {
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
 * Parent Handler for &lt;extract&gt; sub-elements.   <p>
 *
 *	Sub-element handlers do not inherit directly from
 *	<code>extractHandler</code> because essentially all (perhaps all)
 *	sub-elements will want to use the five-argument <code>action</code>
 *	method.  <code>extractHandler</code> itself does not, however, so we
 *	need to restore the three-argument action to its original
 *	functionality.
 */
class extract_subHandler extends extractHandler {

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

  public extract_subHandler() { this(true, false); }

  /** This constructor is used by sub-elements to control their syntax. */
  extract_subHandler(boolean expand, boolean text) {
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
class fromHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    content = new ParseNodeList(ListUtil.getListItems(content));
    putList(out, content);
  }
  fromHandler() { super(true, false); }
}

class inHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    Enumeration items = ListUtil.getListItems(content);
    while (items.hasMoreElements()) {
      Node item = (Node) items.nextElement();
      if (item.getNodeType() == NodeType.TEXT) {
        Node binding = aContext.getEntityBinding(item.toString(), false);
	if (binding != null) out.putNode(binding);
      } else {
	out.putNode(item);
      }
    }
  }

  /** Content is text only, because all we want is the name */
  inHandler() { super(true, true); } // text only
}

/** &lt;child&gt;<em>n</em>&lt;/&gt; extracts the <em>n</em>th child
 *	of each extracted node. */
class childHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList extracted = getExtracted(aContext);
    NodeEnumerator items = extracted.getEnumerator();
    Enumeration terms = ListUtil.getTextItems(content);
    int termsHandled = 0;

    while (terms.hasMoreElements()) {
      ++termsHandled;
      String term = terms.nextElement().toString();
      for (Node item = items.getFirst(); item != null; item = items.getNext()) {
	if (item.hasChildren()) {
	  putList(out, extractTextItem(term, item.getChildren()));
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

/** &lt;nodes&gt;<em>term</em>*&lt;/&gt; extracts each node matching a term. */
class nodesHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList extracted = getExtracted(aContext);
    Enumeration terms = ListUtil.getTextItems(content);
    int termsHandled = 0;

    while (terms.hasMoreElements()) {
      String term = terms.nextElement().toString();
      putList(out, extractTextItem(term, extracted));
    }
  }
  nodesHandler() { super(true, false); }
}

/** &lt;name&gt;<em>n</em>&lt;/&gt; extracts every node with a matching name.
 */
class nameHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList extracted = getExtracted(aContext);
    String name = content.toString();
    if (name == null) {
      extractNames(extracted, out); 
    } else if (name.startsWith("#")) {
      extractByType(name, extracted, out);
    } else extractByName(name, extracted, out);
  }
  nameHandler() { super(true, false); }
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "recursive")) 	return new name_recursive();
    if (dispatch(e, "all")) 	 	return new name_recursive();
    return this;
  }
  protected void extractNames(NodeList extracted, Output out) {
    NodeEnumerator items = extracted.getEnumerator();
    for (Node item = items.getFirst(); item != null; item = items.getNext()) {
      int ntype = item.getNodeType();
      if (ntype == NodeType.ELEMENT) {
	Element elt = (Element) item;
	out.putNode(new ParseTreeText(elt.getTagName()));
      } else if (ntype == NodeType.ATTRIBUTE) {
	Attribute attr = (Attribute) item;
	out.putNode(new ParseTreeText(attr.getName()));
      } else if (ntype == NodeType.ENTITY) {
	Entity ent = (Entity) item;
	out.putNode(new ParseTreeText(ent.getName()));
      }
    }
  }
}

/** &lt;name recursive&gt;<em>n</em>&lt;/&gt; 
 *	extracts every node with a matching name; recurses into content.
 */
class name_recursive extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList extracted = getExtracted(aContext);
    boolean caseSens = atts.hasTrueAttribute("case");
    boolean all = atts.hasTrueAttribute("all");
    String key = content.toString();
    if (key != null && !caseSens) key = key.toLowerCase();
    if (key != null && key.startsWith("#")) {
      int ntype = NodeType.getType(key.substring(1));
      extractByType(extracted, ntype, all, out);
    } else {
      extract(extracted, key, caseSens, all, out);
    }
  }
  protected void extract(NodeList extracted, String key, 
			boolean caseSens, boolean all, Output out) {
    NodeEnumerator items = extracted.getEnumerator();
    for (Node item = items.getFirst(); item != null; item = items.getNext()) {
      String name = getNodeName(item, caseSens);
      if (key == null || (name != null && key.equals(name))) {
	out.putNode(item);
      } else if (!all && item.hasChildren()) {
	extract(item.getChildren(), key, caseSens, all, out);
      }
      if (all && item.hasChildren()) {
	extract(item.getChildren(), key, caseSens, all, out);
      }
    }
  }
  protected void extractByType(NodeList extracted, int ntype,
			      boolean all, Output out) {
    NodeEnumerator items = extracted.getEnumerator();
    for (Node item = items.getFirst(); item != null; item = items.getNext()) {
      if (ntype == NodeType.ALL || ntype == item.getNodeType()) {
	out.putNode(item);
      } else if (!all && item.hasChildren()) {
	extractByType(item.getChildren(), ntype, all, out);
      }
      if (all && item.hasChildren()) {
	extractByType(item.getChildren(), ntype, all, out);
      }
    }
  }
  name_recursive() { super(true, false); }
}

/** &lt;key&gt;<em>k</em>&lt;/&gt; extracts every node with a matching key. */
class keyHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList extracted = getExtracted(aContext);
    boolean caseSens = atts.hasTrueAttribute("case");
    String sep = atts.getAttributeString("sep");
    String key = content.toString();
    if (key != null && !caseSens) key = key.toLowerCase();
    NodeEnumerator items = extracted.getEnumerator();
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
 *	extracts every node with a matching key; recurses into content.
 */
class key_recursive extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList extracted = getExtracted(aContext);
    boolean caseSens = atts.hasTrueAttribute("case");
    String  sep = atts.getAttributeString("sep");
    boolean all = atts.hasTrueAttribute("all");
    String  key = content.toString();
    if (key != null && !caseSens) key = key.toLowerCase();
    extract(extracted, key, caseSens, sep, all, out);
  }
  protected void extract(NodeList extracted, String key, boolean caseSens,
			String sep, boolean all, Output out) {
    NodeEnumerator items = extracted.getEnumerator();
    for (Node item = items.getFirst(); item != null; item = items.getNext()) {
      String k = getNodeKey(item, caseSens, sep);
      if (key == null || (k != null && key.equals(k))) {
	out.putNode(item);
      } else if (!all && item.hasChildren()) {
	extract(item.getChildren(), key, caseSens, sep, all, out);
      }
      if (all && item.hasChildren()) {
	extract(item.getChildren(), key, caseSens, sep, all, out);
      }
    }
  }
  key_recursive() { super(true, false); }
}

/** &lt;id&gt;<em>k</em>&lt;/&gt; extracts every node with a matching id. */
class idHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList extracted = getExtracted(aContext);
    boolean caseSens = atts.hasTrueAttribute("case");
    String id = content.toString();
    if (id != null && !caseSens) id = id.toLowerCase();
    NodeEnumerator items = extracted.getEnumerator();
    for (Node item = items.getFirst(); item != null; item = items.getNext()) {
      String k = getNodeId(item, caseSens);
      if (id == null || id.equals(k))	out.putNode(item);
    }    
  }
  idHandler() { super(true, true); } // text only
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "recursive")) 	return new id_recursive();
    if (dispatch(e, "all")) 	 	return new id_recursive();
    return this;
  }
}

/** &lt;id recursive&gt;<em>n</em>&lt;/&gt; 
 *	extracts every node with a matching id; recurses into content.
 */
class id_recursive extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList extracted = getExtracted(aContext);
    boolean caseSens = atts.hasTrueAttribute("case");
    boolean all = atts.hasTrueAttribute("all");
    String  id = content.toString();
    if (id != null && !caseSens) id = id.toLowerCase();
    extract(extracted, id, caseSens, all, out);
  }
  protected void extract(NodeList extracted, String id, boolean caseSens,
			boolean all, Output out) {
    NodeEnumerator items = extracted.getEnumerator();
    for (Node item = items.getFirst(); item != null; item = items.getNext()) {
      String k = getNodeId(item, caseSens);
      if (id == null || (k != null && id.equals(k))) {
	out.putNode(item);
      } else if (!all && item.hasChildren()) {
	extract(item.getChildren(), id, caseSens, all, out);
      }
      if (all && item.hasChildren()) {
	extract(item.getChildren(), id, caseSens, all, out);
      }
    }
  }
  id_recursive() { super(true, false); }
}

/** &lt;attr&gt;<em>n</em>&lt;/&gt; extracts every Attribute with matching name.
 */
class attrHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList extracted = getExtracted(aContext);
    boolean caseSens = atts.hasTrueAttribute("case");
    String name = content.toString();
    
    NodeEnumerator items = extracted.getEnumerator();
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

/** &lt;match&gt;<em>re</em>&lt;/&gt; extracts every node matching re. */
class matchHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList extracted = getExtracted(aContext);
    boolean caseSens = atts.hasTrueAttribute("case");
    String match = content.toString();
    if (!caseSens) match = match.toLowerCase();
    NodeEnumerator items = extracted.getEnumerator();
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

/** &lt;xptr&gt;<em>xp</em>&lt;/&gt; extracts using an XPointer. */
class xptrHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList extracted = getExtracted(aContext);
    boolean caseSens = atts.hasTrueAttribute("case");
    unimplemented(in, aContext);		// === xptr
  }
  xptrHandler() { super(true, true); }
}

/** &lt;parent&gt; extracts every node's parent. */
class parentHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList extracted = getExtracted(aContext);
    NodeEnumerator items = extracted.getEnumerator();
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

/** &lt;content&gt; extracts every node's content (children). */
class contentHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList extracted = getExtracted(aContext);
    NodeEnumerator items = extracted.getEnumerator();
    for (Node item = items.getFirst(); item != null; item = items.getNext()) {
      if (item.hasChildren()) { putList(out, item.getChildren()); }
    }
  }
  contentHandler() { super(true, false); syntaxCode = EMPTY; }
}

/** &lt;next&gt; extracts every node's successor. */
class nextHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList extracted = getExtracted(aContext);
    NodeEnumerator items = extracted.getEnumerator();
    for (Node item = items.getFirst(); item != null; item = items.getNext()) {
      Node n = item.getNextSibling();
      if (n != null) out.putNode(n);
    }
  }
  nextHandler() { super(true, false); syntaxCode = EMPTY; }
}

/** &lt;prev&gt; extracts every node's predecessor. */
class prevHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList extracted = getExtracted(aContext);
    NodeEnumerator items = extracted.getEnumerator();
    for (Node item = items.getFirst(); item != null; item = items.getNext()) {
      Node n = item.getPreviousSibling();
      if (n != null) out.putNode(n);
    }
  }
  prevHandler() { super(true, false); syntaxCode = EMPTY; }
}

/** &lt;eval&gt; extracts every node's value. */
class evalHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList extracted = getExtracted(aContext);
    NodeEnumerator items = extracted.getEnumerator();
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
	v = ent.getValueNodes(aContext);
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

/** &lt;replace&gt; replace every node's value or content. */
class replaceHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList extracted = getExtracted(aContext);
    NodeEnumerator items = extracted.getEnumerator();
    String name = atts.getAttributeString("name");
    boolean caseSens = atts.hasTrueAttribute("case");
    for (Node item = items.getFirst(); item != null; item = items.getNext()) {
      ActiveNode a = (ActiveNode)item;
      switch (item.getNodeType()) {
      case NodeType.ATTRIBUTE:
	Attribute att = a.asAttribute();
	if (name != null 
	     && !(caseSens && name.equals(att.getName())
		  || !caseSens && name.equalsIgnoreCase(att.getName())))
	  continue;
	att.setValue(content);
	break;

      case NodeType.ENTITY:
	ActiveEntity ent = a.asEntity();
	if (name != null 
	     && !(caseSens && name.equals(ent.getName())
		  || !caseSens && name.equalsIgnoreCase(ent.getName())))
	  continue;
	ent.setValueNodes(aContext, content);
	break;

      case NodeType.ELEMENT:
	ActiveElement elt = (ActiveElement)item;
	if (name != null) {
	    elt.setAttributeValue(name, content);
	  
	} else {
	  for (Node child = a.getFirstChild(); child != null; 
	       child = a.getFirstChild()) {
	    try { a.removeChild(child); } catch (Exception e) {}
	  }
	  Copy.appendNodes(content, a);
	  // putList(out, extracted);
	  // === attributes in content should become attributes of a ===

	}
	break;
      }
    }
  }
    replaceHandler() { super(true, false); }
}

/** &lt;remove&gt; remove every extracted node.
 * <p> === unreliable until new DOM implementation in place.
 */
class removeHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList extracted = getExtracted(aContext);
    NodeEnumerator items = extracted.getEnumerator();
    for (Node item = items.getFirst(); item != null; item = items.getNext()) {
      Node parent = item.getParentNode();
      if (parent != null)
	try { parent.removeChild(item); } catch (Exception e) {}
    }
  }
  removeHandler() { super(true, false); syntaxCode = EMPTY; }
}

/** &lt;unique&gt; remove every duplicate text extracted node.
 * <p> === unreliable until new DOM implementation in place.
 */
class uniqueHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    NodeList extracted = getExtracted(aContext);
    List textAssoc = TextUtil.getTextList(extracted, false);
    Enumeration textAssocEnum = textAssoc.elements();

    SortTree sorter = new SortTree();
    Association a;
    
    while(textAssocEnum.hasMoreElements()) {
      a = (Association)textAssocEnum.nextElement();
      sorter.insert(a, false);
    }

    List tmpList = new List();
    List resultList = new List();
    sorter.ascendingValues(tmpList);
    String prev;
    String cur;
    for(int i = 0; i < tmpList.nItems(); i++) {
	if(i == 0) {
	    resultList.push(tmpList.at(i));
	}
	else {
	    cur = tmpList.at(i).toString();
	    prev = tmpList.at(i - 1).toString();
	    if(cur.equals(prev)) {
		;
	    }
	    else {
		resultList.push(tmpList.at(i));
	    }
	}  
    }
    ParseNodeList selList = ListUtil.toNodeList(resultList);
    putList(out, selList);
    
    // putEnum(out, resultList.elements());
  }
  uniqueHandler() { super(true, false); syntaxCode = EMPTY; }
}


/** &lt;append&gt; Append nodes to extraction or children of extraction.
 * <p> === unreliable until new DOM implementation in place.
 */
class appendHandler extends extract_subHandler {
  protected void action(Input in, Context aContext, Output out, 
			ActiveAttrList atts, NodeList content) {
    boolean children = atts.hasTrueAttribute("children");

    NodeList extracted = getExtracted(aContext);
    NodeEnumerator items = extracted.getEnumerator();
    Node item = null;
    ActiveNode parent = null;
    for (item = items.getFirst(); item != null; item = items.getNext()) {
      if (children) {
	  parent = (ActiveNode)item;
	  Copy.appendNodes(content, parent);	
	  // out.putNode(parent);
      } else {
	ActiveNode p = (ActiveNode)item.getParentNode();
	  if (p != null && p != parent) {
	    parent = p;
	    Copy.appendNodes(content, parent);
	  }
	  // putList(out, extracted);
	  // putList(out, content);
      }
    }
  }
  appendHandler() { super(true, false); }
}
