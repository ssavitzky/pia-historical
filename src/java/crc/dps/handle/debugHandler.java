////// debugHandler.java: <debug> Handler implementation
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.handle;

import crc.dom.Node;
import crc.dom.NodeList;
import crc.dom.NodeEnumerator;
import crc.dom.Comment;
import crc.dom.Text;
import crc.dom.Attribute;
import crc.dom.Declaration;
import crc.dom.PI;
import crc.dom.Entity;
import crc.dom.Element;

import crc.ds.List;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.util.*;
import crc.dps.output.*;
import crc.dps.NodeType;

import java.util.Enumeration;
import java.lang.String;
import java.lang.StringBuffer;

// Removed dispatch code, plus debug_ subclass

/**
 * Handler for &lt;typical&gt;....&lt;/&gt;  
 *
 * <p>	
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 */

public class debugHandler extends GenericHandler {

  /************************************************************************
  ** Semantic Operations:
  ************************************************************************/

  /** Action for &lt;debug&gt; node.  Recursively
    * prints the content NodeList as a tree.
   */
  public void action(Input in, Context cxt, Output out, 
  		     ActiveAttrList atts, NodeList content) {

    // Actually do the work. 
    OutputTree trOut = new OutputTree(out);
    // System.out.println("Length of content: " + content.getLength());
    
    NodeEnumerator enum = content.getEnumerator();
    for (Node n = enum.getFirst(); n != null; n = enum.getNext()) {
      // Print the node tree
      printTree(n, cxt, out, 0);

      // Output the page as well
      out.putNode(n);
    }

  }

  /** Print the children node as a tree.
    */
  protected void printTree(Node node, Context cxt, Output out, int indentNum) {
    // System.out.println("printTree Node: " + node.toString());
    if(node == null)
      return;

    String nType = NodeType.getName(node.getNodeType());
    String nContent = null;

    switch(node.getNodeType()) {
    case NodeType.COMMENT:
      nContent = ((Comment)node).getData();
      break;
    case NodeType.TEXT:
      Text tn = ((Text)node);
      if(Test.isWhitespace(tn.getData()))
	nContent = "<whitespace>";
      else
	nContent = tn.getData();
      break;
    case NodeType.DECLARATION:
      nContent = ((Declaration)node).getName();
      break;
    case NodeType.PI:
      nContent = ((PI)node).getName();
      break;
    case NodeType.ENTITY:
      nContent = ((Entity)node).getName();
      break;
    case NodeType.ELEMENT:
      nContent = "<" + ((Element)node).getTagName() + ">";
      break;
    case NodeType.ATTRIBUTE:
      nContent = ((Attribute)node).getName();
      break;
    case NodeType.ENDTAG:
      nContent = ((Attribute)node).getName();
      break;
    default:
      // DOCUMENT falls here
      System.out.println("default: " + node.toString());
      break;
    }

    String printStr = nType + " " + nContent;

    String indStr = indentString(printStr, indentNum);
    System.out.println(indStr);
    int newIndent = indentNum + 3;
    NodeList nl = node.getChildren();
    if(nl == null)
      return;
    NodeEnumerator childEnum = nl.getEnumerator();
    for (Node n = childEnum.getFirst(); n != null; n = childEnum.getNext()) {
      printTree(n, cxt, out, newIndent);
    }
  }

  /** Return an indented string */
  protected String indentString(String indentStr, int num) {
    StringBuffer sb = new StringBuffer();
    for(int i = 0; i < num; i++) {
      sb.append(" ");
    }
    sb.append(indentStr+"\n");
    return(sb.toString());
  }


  /************************************************************************
  ** Constructor:
  ************************************************************************/

  /** Constructor must set instance variables. */
  public debugHandler() {
    /* Expansion control: */
    expandContent = true;	// false	Expand content?
    textContent = false;	// true		extract text from content?

    /* Syntax: */
    parseElementsInContent = true;	// false	recognize tags?
    parseEntitiesInContent = true;	// false	recognize entities?
    syntaxCode = NORMAL;  		// EMPTY, QUOTED, 0 (check)
  }

  debugHandler(ActiveElement e) {
     this();
     // customize for element.
     expandContent = true;

  }
}

