////// submitHandler.java: <submit> Handler implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.NodeEnumerator;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.util.*;
import crc.dps.process.ActiveDoc;

import crc.pia.Agent;
import crc.pia.agent.AgentMachine;
import crc.pia.Headers;
import crc.pia.BadMimeTypeException;
import crc.pia.FileAccess;
import crc.pia.InputContent;
import crc.pia.FormContent;
import crc.pia.Content;
import crc.pia.MultipartFormContent;
import crc.pia.Pia;

/**
 * Handler for &lt;submit&gt;....&lt;/&gt;  
 *
 * <p> The intent is for this to be equivalent to the old <submit-forms> tag. 
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class submitHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;submit&gt; node. */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, NodeList content) {
    ActiveDoc top = (ActiveDoc)cxt.getTopContext();

    if ("form".equalsIgnoreCase(in.getTagName())) {
      // Handle the case where this is the handler for an actual <form>
      submit(top.getAgent(),
	     in.getActive().asElement().editedCopy(atts, content), 
	     atts);
    } else {
      handleContent(top.getAgent(), content, atts);
    }
  }

  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public submitHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }

  submitHandler(ActiveElement e) {
    this();
    // customize for element.
  }

  /************************************************************************
  ** Utilities:
  ************************************************************************/

  /** Submit a form or request using an agent.
   * 	@param a	the agent submitting the form.
   *	@param form	the form to be submitted
   *	@param times	the timed-submission attributes.
   */
  protected void submit(Agent a, ActiveElement form,
			ActiveAttrList times) {
    if (!Forms.containsTimedSubmission(times)) times = null;

    if ( form.getTagName().equalsIgnoreCase("form") ){
      String url = form.getAttributeString("action");
      String method = form.getAttributeString("method");
      String encType = form.getAttributeString("encType");
      // Set default encoding type if not specified
      if (encType == null) {
	encType = "application/x-www-form-urlencoded";
      }

      // Initialize content type for submission
      String contentType;
      if (encType.equals("application/x-www-form-urlencoded")) {
	contentType = "application/x-www-form-urlencoded";
      } else if (encType.equals("multipart/form-data")) {
	contentType = "multipart/form-data; boundary="+Forms.multipartBoundary;
      } else {
	Pia.debug(this, "Unknown encoding specified in form: "+encType);
	return;
      }

      // Make a machine to handle the request
      AgentMachine m = new AgentMachine(a);

      // Create the HTTP request
      if (times == null) {
	Pia.debug(this,"Making request "+method+" "+url+" "+contentType);
	a.createRequest(m,method, url, formToEncoding(form,encType), contentType);
      } else { 
	a.createTimedRequest(method, url,
			     formToEncoding(form,encType).toString(),
			     contentType, Forms.getTimes(times));
      }

    } else if (form.hasTrueAttribute("href")) {

      // Make a machine to handle the request
      AgentMachine m = new AgentMachine(a);

      // Uncomment line below for debugging, it
      // will output to stdout everything returned
      // from the server which hosts the form
      // m.setCallback(new EchoCallback());

      String url = form.getAttributeString("href");
      if (times == null) 
	a.createRequest(m,"GET", url, (String) null, null);
      else {
	a.createTimedRequest("GET", url, (String) null, null,
			     Forms.getTimes(times));
      }
    }
  }

  /** Process forms in a NodeList
   */
  protected void handleContent(Agent a, NodeList forms,
			       ActiveAttrList times) {
    if (forms != null){
      NodeEnumerator nodes = forms.getEnumerator();
      for (Node n = nodes.getFirst(); n != null; n = nodes.getNext()) {
	if (n instanceof ActiveElement) handleNode(a, (ActiveElement)n, times);
      }
    }
  }

  /** Process an element.  
   *	If it is a form, submit it.  Otherwise, process its content.
   */
  protected void handleNode(Agent a, ActiveElement node,
			    ActiveAttrList times) {
    if (node.getTagName().equalsIgnoreCase("form") ) {
      submit(a, node, times);
    } else if (node.hasChildren()) {
      handleContent(a, node.getChildren(), times);
    }
  }

  /** Convert a form to either a query string
   *  or a multipart form encoding
   */
  protected static InputContent formToEncoding(ActiveElement it,
					       String encType) {
    if (encType.equals("application/x-www-form-urlencoded")) {
      return new FormContent(Forms.formToQuery(it));
    } else if (encType.equals("multipart/form-data")) {
      return new MultipartFormContent(Forms.formToMultipart(it));
    } else {
      // WHAT IS CORRECT ACTION HERE?
      //=== message Pia.debug(this, "Unknown encoding specified in form");
      return null;
    }
  }

}
