////// defineHandler.java: <define> Handler implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;
import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.Attribute;
import crc.dom.AttributeList;
import crc.dom.Element;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.util.*;
import crc.dps.tagset.TagsetProcessor;
import crc.dps.handle.Loader;

import java.util.Enumeration;

/**
 * Handler for &lt;define&gt;....&lt;/&gt;  <p>
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class defineHandler extends GenericHandler {

  protected static Class valueHandlerClass = new valueHandler().getClass();
  protected static Class actionHandlerClass = new actionHandler().getClass();

  /** The name of the attribute that holds the name of the construct being
   *	defined.  For example, in &lt;define element=foo&gt; this would have
   *	the value "element".
   */
  protected String attrName = null;

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** This will normally be the only thing to customize. */
  public void action(Input in, Context aContext, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    // Actually do the work.  In this case a naked define is an error.
    reportError(in, aContext, "Nothing being defined");
  }

  /** This does the parse-time dispatching. <p>
   *
   *	Action is dispatched (delegated) to a subclass if the string
   *	being passed to <code>dispatch</code> is either the name of an
   *	attribute or a period-separated suffix of the tagname. <p>
   */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    if (dispatch(e, "element")) 	 return define_element.handle(e);
    if (dispatch(e, "entity"))   	 return define_entity.handle(e);
    if (dispatch(e, "attribute")) 	 return define_attribute.handle(e);
    if (dispatch(e, "word"))    	 return define_word.handle(e);
    return this;
  }
   
  /************************************************************************
  ** Content and attribute handling:
  ************************************************************************/

  protected ActiveElement getAction(NodeList content) {
    ActiveNode n;
    for (long i = 0; i < content.getLength(); ++i) {
      try {
	n = (ActiveNode) content.item(i);
      } catch (Exception e) { continue; }
      if (n.getSyntax().getClass() == actionHandlerClass) return n.asElement();
    }
    return null;
  }

  protected ActiveElement getValue(NodeList content) {
    ActiveNode n;
    for (long i = 0; i < content.getLength(); ++i) {
      try {
	n = (ActiveNode) content.item(i);
      } catch (Exception e) { continue; }
      if (n.getSyntax().getClass() == valueHandlerClass) return n.asElement();
    }
    return null;
  }

  protected String getHandlerClassName(ActiveAttrList atts, String dflt) {
    Attribute handler = atts.getAttribute("handler");
    return (handler == null)     ? null
      : (handler.getSpecified()) ? handler.getValue().toString()
      : dflt;
  }


  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public defineHandler() {
    /* Expansion control: */
    expandContent = true;	// false 	Expand content?
    textContent = false;	// true 	extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }

  public defineHandler(String aname) {
    this();
    attrName = aname;
  }
}

/** Define an element.  Corresponds to &lt!ELEMENT ...&gt; */
class define_element extends defineHandler {
  /** cache for the legacy tagset in case we need it. */
  protected static Tagset legacyTS = null; 

  /** The actual action routine. */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    // Analyze the attributes: === could do this in one scan.
    String tagname = atts.getAttributeString(attrName);
    String handlerClass = getHandlerClassName(atts, tagname);
    Tagset ts = cxt.getTopContext().getTagset();
    String parents = atts.getAttributeString("parent");
    String notIn   = atts.getAttributeString("implicitly-ends");

    // Determine the syntax:
    int syntax = 
      (atts.hasTrueAttribute("quoted"))? Syntax.QUOTED :
      (atts.hasTrueAttribute("empty")) ? Syntax.EMPTY  :
      Syntax.NORMAL;

    // Get the action, if any.
    ActiveElement action = getAction(content);
    NodeList newContent  = (action == null)? null : action.getChildren();
    // Get the value, if any.
    ActiveElement value = getValue(content);
    if (value != null) unimplemented(in, cxt, "element with value");

    if (action == null && handlerClass == null) {
      // No action, no handler: it's an ordinary non-active Element
      ts.defTag(tagname, notIn, parents, syntax, null, null);
    } else if (handlerClass == null) {
      ts.defTag(tagname, notIn, parents, syntax, null, newContent);
    } else  {
      // Handler: load the class
      GenericHandler h = null;
      if (handlerClass.startsWith("legacy:")) {
	handlerClass = handlerClass.substring("legacy:".length());
	if (legacyTS == null)
	  legacyTS = crc.dps.tagset.Loader.loadTagset("legacy");
	try {
	  h = (GenericHandler) legacyTS.getHandlerForTag(handlerClass);
	}  catch (Exception ex) {}
      } else {
	h = (GenericHandler) Loader.loadHandler(tagname, handlerClass,
						syntax, false);
      }
      if (h == null) {
	cxt.message(-1, (((parents == null)? "" : "  ") 
			 + "Cannot load handler class " + handlerClass),
		    0, true);
	h = new GenericHandler(syntax);
      }

      // Handle an implicitly-ends attribute
      if (notIn != null) {
	Enumeration nt = new java.util.StringTokenizer(notIn);
	while (nt.hasMoreElements()) {
	  h.setImplicitlyEnds(nt.nextElement().toString());
	}
      }

      // Handle a parent attribute
      if (parents != null) {
	Enumeration nt = new java.util.StringTokenizer(parents);
	while (nt.hasMoreElements()) {
	  h.setIsChildOf(nt.nextElement().toString());
	}
      }

      // If there's an action, it becomes the children.
      if (newContent != null) Copy.appendNodes(content, h);

      // === need to set parent nodes in handler. ===
      ts.setHandlerForTag(tagname, h);
    }
  }

  define_element(String aname) { super(aname); }
  static define_element handle = new define_element("element");
  static define_element handle_name = new define_element("name");
  static Action handle(ActiveElement e) { 
    return e.hasTrueAttribute("name")? handle_name : handle;
  }
}

/** Define an entity.  Corresponds to &lt!ENTITY ...&gt; */
class define_entity extends defineHandler {
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    TopContext  top  = cxt.getTopContext();
    Tagset	ts   = top.getTagset();
    String      name = atts.getAttributeString(attrName);

    // General attributes.  === currently unimplemented ===
    boolean parameter= atts.hasTrueAttribute("parameter");
    boolean   retain = atts.hasTrueAttribute("retain");

    // Attributes for external entities.
    String   sysName = atts.getAttributeString("system");
    String   pubName = atts.getAttributeString("public");
    String	mode = atts.getAttributeString("mode");
    String writeMode = atts.getAttributeString("write-mode");
    String    method = atts.getAttributeString("method");

    NodeList newContent = null;
    // Get the action, if any.
    ActiveElement action = getAction(content);
    newContent  = (action == null)? null : action.getChildren();
    // Get the value, if any.
    ActiveElement value = getValue(content);
    newContent  = (value == null)? null : value.getChildren();

    if (action != null && value != null)
      unimplemented(in, cxt, "entity with both action and value");
    if (action != null) unimplemented(in, cxt, "entity with action");
    // === with action or passive entity, need to set up a binding. ===

    TagsetProcessor tproc = (top instanceof TagsetProcessor)
      ? (TagsetProcessor) top : null;

    ActiveEntity ent = null;
    if (sysName != null) {
      // External 
      ParseTreeExternal ext = new ParseTreeExternal(name, sysName, null);
      ext.method = (method == null)? "GET" : method.toUpperCase();
      // === punt on the write stuff for now. ===

      // === use tagset to set the handler. 
      Handler h = ts.getHandlerForType(NodeType.ENTITY);
      ext.setHandler(h);
      ext.setAction(h.getActionForNode(ext));
      if (newContent != null) ext.setValue(new ParseNodeList(newContent));
      ent = ext;
    } else {
      ent = ts.createActiveEntity(name, newContent);
    }
    
    if (hasNamespace(name)) {
      // Have to worry about namespace. 
      cxt.setEntityBinding(name, ent, false);
    } else if (tproc != null ) {
      // If we're in a tagset, define it there.
      tproc.getTagset().setEntityBinding(name, ent);
    } else {
      // Otherwise, define it in the document's entity table. 
      top.setEntityBinding(name, ent, false);
    }
  }
  define_entity(String aname) { super(aname); }
  static define_entity handle = new define_entity("entity");
  static Action handle(ActiveElement e) { return handle; }

  protected boolean hasNamespace(String name) {
    return name.indexOf(":") >= 0;
  }
}

// -----------------------------------------------------------------------

class define_attribute extends defineHandler {
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    // not really unimplemented(in, cxt); -- more like not needed yet
    // === eventually define_attribute sets up a dispatch table.
    
  }
  define_attribute(String aname) { super(aname); }
  static define_attribute handle = new define_attribute("attribute");
  static Action handle(ActiveElement e) { return handle; }
}

class define_word extends defineHandler {
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    unimplemented(in, cxt);	// define word -- maybe not needed yet.
  }
  define_word(String aname) { super(aname); }
  static define_word handle = new define_word("word");
  static Action handle(ActiveElement e) { return handle; }
}
