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
import crc.dps.aux.*;
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
  public void action(Input in, Context aContext, Output out, String tag, 
  		     ActiveAttrList atts, NodeList content, String cstring) {
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
    stringContent = false;	// true  	want content as string?
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

class define_element extends defineHandler {
  public void action(Input in, Context cxt, Output out, String tag, 
  		     ActiveAttrList atts, NodeList content, String cstring) {
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
      GenericHandler h =
	(GenericHandler) Loader.loadHandler(tagname, handlerClass,
					    syntax, false);
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

class define_attribute extends defineHandler {
  public void action(Input in, Context cxt, Output out, String tag, 
  		     ActiveAttrList atts, NodeList content, String cstring) {
    // not really unimplemented(in, cxt); -- more like not needed 
    
  }
  define_attribute(String aname) { super(aname); }
  static define_attribute handle = new define_attribute("attribute");
  static Action handle(ActiveElement e) { return handle; }
}

class define_entity extends defineHandler {
  public void action(Input in, Context cxt, Output out, String tag, 
  		     ActiveAttrList atts, NodeList content, String cstring) {
    String name = atts.getAttributeString(attrName);
    // Might have to handle both action and value

    // Have to worry about namespace. 

    // === don't worry about handler at this point. ===
  }
  define_entity(String aname) { super(aname); }
  static define_entity handle = new define_entity("entity");
  static Action handle(ActiveElement e) { return handle; }
}

class define_word extends defineHandler {
  public void action(Input in, Context cxt, Output out, String tag, 
  		     ActiveAttrList atts, NodeList content, String cstring) {
    unimplemented(in, cxt);	// define word -- maybe not needed.
  }
  define_word(String aname) { super(aname); }
  static define_word handle = new define_word("word");
  static Action handle(ActiveElement e) { return handle; }
}
