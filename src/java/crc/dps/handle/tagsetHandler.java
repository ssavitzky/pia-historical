////// tagsetHandler.java: <tagset> Handler implementation
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
import crc.dps.tagset.BasicTagset;
import crc.dps.output.DiscardOutput;
import crc.dps.tagset.TagsetProcessor;

/**
 * Handler for &lt;tagset&gt;....&lt;/&gt;  <p>
 *
 *	
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class tagsetHandler extends GenericHandler {

  /************************************************************************
  ** Parse-time Operations:
  ************************************************************************/

  /** In this case we actually create a Tagset object. */
  public ActiveElement createElement(String tagname, AttributeList attributes,
				     boolean hasEmptyDelim) {

    // We should check at this point to see if we need a different base class.

    BasicTagset ts = new BasicTagset();
    ts.setTagName(tagname);
    ts.setAttributes(attributes);
    ts.setHandler(this);	// can't forget this part!

    if (hasEmptyDelim) ts.setHasEmptyDelimiter(hasEmptyDelim);
    ts.setIsEmptyElement(hasEmptyDelim);
    ts.setAction(getActionForNode(ts));

    ts.setName(ts.getAttributeString("name"));

    // === It doesn't matter, because the tagset we're defining/using is
    // === not going to be in the parse tree.  (Except that if we were going
    // === to build a tagset local to this document, we would want it there.
    // === Maybe in the Document node. )

    return ts;

  }

  /* === This is very twisty. ===
    
     The element we create at parse time with createElement becomes part of
     the parse tree.  We probably don't want that to become the ``official''
     tagset, although it's possible we might.  At least at first, then,
     although we create a BasicTagset node, we might not end up using it.

     It makes a difference whether the Document _is_ a tagset, or whether this
     node is refering to one.  We may want a separate ``use_tagset'' handler
     for the latter case, especially since we may need to hack action routines
     on the Input.

     What we _do_ need to do, though, is put a reference to the tagset into
     the document parse tree under construction, because we're going to need
     it for looking up entities when we go to re-use the parse tree.

     When we get around to processing we need to make a Tagset, or track down
     an old one, and put it into a new TopContext where it can be hacked on.
     The parser is still using the old one.

     When we want a recursive Tagset, though, we really need to hack the one
     the parser is using.  DTD's and PI's may also need to do this (e.g. the
     doctype and the XML case- and space-handling pragmas).

     A recursive Tagset _really_ only changes the language used in <value> and
     <action> tags, not otherwise.  Basically, a recursive tagset leaves the
     parser using the processor's current tagset, while a non-recursive one
     replaces the one the _processor_ is using, by switching to a new
     TopProcessor.  If the Parser's tagset is locked, though, we have to build
     a new one.

     But that's only when encountering a <tagset> element in the normal course
     of processing.  When _defining_ a tagset, or when specifying a tagset in
     a PI or DTD, we _still_ have to be able to get from the processor to the
     parser.
     
   */

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** This will get a little tricky.  
   *	There are really two distinct possibilities here -- are we 
   *	<em>using</em> a tagset, or <em>defining</em> one?  
   */
  public void action(Input in, Context cxt, Output out) {
    ActiveElement n  = in.getActive().asElement();
    TopContext   top = cxt.getTopContext();
    BasicTagset newTagset = new BasicTagset();
    newTagset.setName(n.getAttributeString("name"));
    newTagset.setAttributes(n.getAttributes());

    // Check the TopProcessor.  If it's a TagsetProcessor, we're processing a
    // tagset file in the Loader (figures).  Otherwise, we're just referencing
    // one.

System.err.println("Looking for TopProcessor");

    TagsetProcessor tproc = (top instanceof TagsetProcessor)
      ? (TagsetProcessor) top : null;

    // If the tagset is non-recursive, just replace the one in 
    //	the TopProcessor.   

    //  If it's recursive, the parser and TopProcessor are already using the
    //  same one, but it might be locked.  In that case, we have to replace
    //  them both with one we can modify.

    if (tproc != null) {
      // We're loading a Tagset, so we don't need output.  Go through the
      // input and quietly discard the results.
      // === at this point it's a little unclear whether we need a new node...
      BasicTagset inTagsetNode = (BasicTagset) n;
      if (tproc.getNewTagset() == null) tproc.setNewTagset(newTagset);
      cxt.debug("Initializing tagset " + tproc.getNewTagset().getName() + "\n");
      cxt.subProcess(in, new DiscardOutput()).processChildren();
      cxt.debug("Done with " + tproc.getNewTagset().getName() + "\n");
    } else {
      // We're using or defining a Tagset in an ordinary document. 
      // For this we really need a new TopProcessor. 
      unimplemented(in, cxt, "using a sub-tagset");
      cxt.subProcess(in, out).processChildren();
    }
  }

  /** Action for &lt;tagset&gt; node. */
  public void action(Input in, Context cxt, Output out, String tag, 
  		     ActiveAttrList atts, NodeList content, String cstring) {
    // === Do we need to go through the content at this point?
  }

  /** This does the parse-time dispatching. <p>
   *
   *	Action is dispatched (delegated) to a subclass if the string
   *	being passed to <code>dispatch</code> is either the name of an
   *	attribute or a period-separated suffix of the tagname. <p>
   */
  public Action getActionForNode(ActiveNode n) {
    ActiveElement e = n.asElement();
    return this;
  }
   
  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public tagsetHandler() {
    /* Expansion control: */
    stringContent = false;	// true 	want content as string?
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }
}

