////// textHandler.java: <text> Handler implementation
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
import crc.dom.Text;
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
import crc.dps.util.*;
import crc.util.*;

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
    if (dispatch(e, "decode"))   return text_decode.handle(e);
    if (dispatch(e, "encode"))   return text_encode.handle(e);
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
  protected String align = null;
  protected boolean left  = false;
  protected boolean right = false;
  protected boolean center = false;
  protected int width     = -1;
  
  public void action(Input in, Context aContext, Output out, 
		     ActiveAttrList atts, NodeList content) {

    NodeList nl = TextUtil.getText(content);
    Enumeration resultList = TextUtil.padListItems(content, true, left, right, 
						   center, width);
    putEnum(out, resultList);
  }

  public text_pad(ActiveElement e) {
    super(e);
    ActiveAttrList atts = (ActiveAttrList) e.getAttributes();
    align  = atts.getAttributeString("align");
    left   = "left".equalsIgnoreCase(align);
    right  = "right".equalsIgnoreCase(align);
    center  = "center".equalsIgnoreCase(align);
    width  = MathUtil.getInt(atts, "width", -1);
  
  }

  static Action handle(ActiveElement e) { return new text_pad(e); }

}


/** Split text at the whitespace and return a new node for each individual word.
    If the sep attribute has been specified, a new separator node is created and added
    to the node list after each text node.  A separator is not added after the last
    node on the list.  Marked up nodes are not split unless the extrat attribute is used.
    If the extract attribute is used, text is retrieved from marked up nodes and split.
*/
class text_split extends textHandler {

  protected boolean extract = false;
  protected String sep = null;
  
  public void action(Input in, Context aContext, Output out, 
		     ActiveAttrList atts, NodeList content) {

    Enumeration resultList = null;
    Enumeration tmpEnum = null;
    // Extract attribute extracts text and ignores mark up
    if(extract) {
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
    extract   = atts.hasTrueAttribute("extract");
  }

  static Action handle(ActiveElement e) { return new text_split(e); }

}


/** 
    Replaces whitespace in text nodes with a separator and combines
    separated text into a single node.  Unless the extract attribute is specified,
    marked up nodes are unchanged.  The result from text_join is a list of
    combined nodes and marked up nodes.  Separators are added between each node
    on the result list, as well as each item within the combined nodes.

    For example, if we start with nodes:  text1 text2 markup1 text3 text4 markup2
    and separator ";", the result would be a list of four nodes: 
    combined_text1;markup1;combined_text2;markup2.  combined_text1 looks like this:
    text1;text2.  

    Attributes are sep and extract.  If sep is not specified, space is
    used as the separator, otherwise the specified separator is placed
    between each word in a combined node and between any combined
    nodes and marked up nodes.  If the extract attribute is used,
    text is extracted from marked up nodes, split at whitespace and
    joined using the sep attribute.
*/
class text_join extends textHandler {

  protected String sep = null;
  protected boolean extract = false;
  
  public void action(Input in, Context aContext, Output out, 
		     ActiveAttrList atts, NodeList content) {

    Enumeration tmpEnum = null;
    List rList = new List();

    // Set default if separator not specified
    if(sep == null)
      sep = " ";

    // Extract attribute extracts text and ignores mark up
    if(extract) {
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
    sep  = atts.getAttributeString("sep");
    extract = atts.hasTrueAttribute("extract");
  }
  static Action handle(ActiveElement e) { return new text_join(e); }
}


/** Translate from URL, base64, or entity encoding to text.

   <H3>
   How to extend the decode handler</H3>
   The <TT>text_decode</TT> handler already decodes url, base64, and entity
   encoding. To add a new decoding type, the <TT>text_decode</TT> class needs
   to be extended as follows:&nbsp; add an attribute to the text tag, then
   extend the action associated with this tag.&nbsp; As an example, suppose
   we want to add an encrypt attribute to decode some encrypted text.&nbsp;
   Amend the existing code as follows.
   <UL>
   <LI>
   &nbsp; Add the new attribute</LI>
   </UL>

   <UL>
   <UL>
   <LI>
   First, add a variable to the <TT>text_decode</TT> class to flag&nbsp; when
   the attribute is being used</LI>

   <BR>&nbsp;<TT>class text_decode extends textHandler {</TT>
   <BR><TT>&nbsp;&nbsp; protected boolean url&nbsp;&nbsp;&nbsp;&nbsp; = false;</TT>
   <BR><TT>&nbsp;&nbsp; protected boolean base64&nbsp; = false;</TT>
   <BR><TT>&nbsp;&nbsp; protected boolean entity&nbsp; = false;</TT>
   <BR><TT>&nbsp;&nbsp; protected boolean encrypt = false;</TT>
   <BR><TT>&nbsp;&nbsp; ...</TT>
   <BR><TT>&nbsp;}</TT></UL>
   &nbsp;
   <UL>
   <LI>
   Set this variable in the <TT>text_decode</TT> constructor by calling the
   <TT>hasTrueAttribute</TT> method with the attribute name as its argument.&nbsp;&nbsp;&nbsp;
   The <TT>hasTrueAttribute</TT> method returns true if the attribute is present
   as a node in the document parse tree.</LI>
   </UL>
   <TT>&nbsp;</TT>
   <UL><TT>public text_decode(ActiveElement e) {</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp; super(e);</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp; ActiveAttrList atts = (ActiveAttrList)
   e.getAttributes();</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp; url&nbsp;&nbsp;&nbsp;&nbsp; = atts.hasTrueAttribute("url");</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp; base64&nbsp; = atts.hasTrueAttribute("base64");</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp; entity&nbsp; = atts.hasTrueAttribute("entity");</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp; encrypt = atts.hasTrueAttribute("encrypt");</TT>
   <BR><TT>&nbsp; }</TT></UL>

   <LI>
   Extend the text_decode action method.&nbsp; This function is triggered
   when the text tag is evaluated.&nbsp; Add an if clause to the existing
   action method to call the encryption routine that will be used to&nbsp;
   decrypt the string.</LI>
   </UL>

   <UL>This particular action method goes through all text nodes associated
   with the text tag (which may only be one node) and extracts the String
   value from each node.&nbsp; The appropriate routine is then called to decode
   each string.&nbsp; In the case of the new encrypt attribute, a new encryption
   routine will be called.&nbsp; The result of the decoding is concatenated
   to a&nbsp; result string, which is eventually written to output.
   <BR>&nbsp;<TT>public void action(Input in, Context aContext, Output out,</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ActiveAttrList atts,
   NodeList content) {</TT>

   <P><TT>&nbsp;&nbsp;&nbsp;&nbsp; String resultStr = "";</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp; String dStr&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   = null;</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp; String str&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   = null;</TT>

   <P><TT>&nbsp;&nbsp;&nbsp;&nbsp; NodeEnumerator enum = content.getEnumerator();</TT>

   <P><TT>&nbsp;&nbsp;&nbsp;&nbsp; for (Node n = enum.getFirst(); n != null;
   n = enum.getNext()) {</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; str = TextUtil.getTextString((ActiveNode)n);</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; if(url) {</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; dStr = Utilities.urlDecode(str);</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; resultStr
   += dStr;</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; }</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; else if(encrypt) {</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; dStr = Utilities.decrypt(str);</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; resultStr
   += dStr;</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; }</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   ...</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp; }</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp; out.putNode(newText(aContext, resultStr));</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp; }</TT>
   <LI>
   Write, or incorporate, a string decoding method.&nbsp; This method needs
   to take an encoded string as an argument, and return the decoded string.
   This routine can be added as a static method to the dps/aux/TextUtil class.</LI>
   </UL>

   <UL>
   <LI>
   Test the new attribute.&nbsp; Below is an example of a test file called
   <TT>decode.xh</TT>. The test code is run as follows:&nbsp;&nbsp; <TT>process
   -t standard decode.xh.</TT>&nbsp; The process program processes the test
   file and writes the results of the tag evaluation to standard out.&nbsp;
   The <TT>standard</TT> argument refers to the standard tagset.</LI>
   </UL>

   <UL><TT>&lt;!doctype html public "-//IETF//DTD HTML//EN//2.0"></TT>
   <BR><TT>&lt;html>&lt;head></TT>
   <BR><TT>&lt;link rev="made" href="mailto:pgage@rsv.ricoh.com"></TT>
   <BR><TT>&lt;/head>&lt;body></TT>
   <BR><TT>&lt;h1>Test file decode.xh&lt;/h1></TT><TT></TT>

   <P><TT>TEST 2: decode encrypt: &Ccedil;=&ETH;&deg;S></TT>
   <BR><TT>Should return:&nbsp; hello</TT>
   <BR><TT>&lt;text decode encrypt>&Ccedil;=&ETH;&deg;S>&lt;/text></TT>
   <BR><TT></TT>&nbsp;</UL>
   &nbsp;

   <P><TT>&lt;hr></TT>
   <BR><TT>&lt;b>Copyright &amp;copy; 1997 Ricoh Silicon Valley&lt;/b>&lt;br></TT>
   <BR><TT>&lt;!-- the following conditional keeps the id out of the results
   -->&lt;if></TT>
   <BR><TT>&lt;then>&lt;b>$Id$&lt;/b>&lt;br>&lt;/then>&lt;/if></TT>
   <BR><TT>&lt;/body>&lt;/html></TT></UL>
   &nbsp;
  */
class text_decode extends textHandler {

  protected boolean url    = false;
  protected boolean base64 = false;
  protected boolean entity = false;
  
  public void action(Input in, Context aContext, Output out, 
		     ActiveAttrList atts, NodeList content) {

    String resultStr = "";
    String dStr      = null;
    String str       = null;

    NodeEnumerator enum = content.getEnumerator();

    for (Node n = enum.getFirst(); n != null; n = enum.getNext()) {
      str = TextUtil.getTextString((ActiveNode)n);
      if(url) {
	dStr = Utilities.urlDecode(str);
	resultStr += dStr;
      }
      else if(base64) {
	byte bytes[] = Utilities.decodeBase64(str);
	dStr = new String(bytes);
	resultStr += dStr;
      }
      else if(entity) {
	dStr = TextUtil.decodeEntity(str);
	resultStr += dStr;
      }
      else {
	// Return text string unchanged
	resultStr += str;
      }
    }
    out.putNode(newText(aContext, resultStr));
  }

  public text_decode(ActiveElement e) {
    super(e);
    ActiveAttrList atts = (ActiveAttrList) e.getAttributes();
    url    = atts.hasTrueAttribute("url");
    base64 = atts.hasTrueAttribute("base64");
    entity = atts.hasTrueAttribute("entity");
  }

  static Action handle(ActiveElement e) { return new text_decode(e); }
}


/** Translate text to URL, base64, or entity encoding.

   <H3>
   How to extend the encode handler</H3>
   The text_encode handler already encodes url, base64, and entity characters.
   To add a new encoding type, the <TT>text_encode</TT> class needs to be
   extended as follows:&nbsp; add an attribute to the text tag, then extend
   the action associated with this tag.&nbsp; As an example, suppose we want
   to add an encrypt attribute to encrypt some text.&nbsp; Amend the existing
   code as follows.
   <UL>
   <LI>
   &nbsp;Add the new attribute</LI>

   <UL>
   <LI>
   &nbsp;&nbsp;&nbsp; First, add a variable to the <TT>text_encode</TT> class
   to flag when the attribute is being used</LI>

   <BR>&nbsp;<TT>class text_encode extends textHandler {</TT>
   <BR><TT>&nbsp;&nbsp; protected boolean url&nbsp;&nbsp;&nbsp;&nbsp; = false;</TT>
   <BR><TT>&nbsp;&nbsp; protected boolean base64&nbsp; = false;</TT>
   <BR><TT>&nbsp;&nbsp; protected boolean entity&nbsp; = false;</TT>
   <BR><TT>&nbsp;&nbsp; protected boolean encrypt = false;</TT>
   <BR><TT>&nbsp;&nbsp; ...</TT>
   <BR><TT>&nbsp;}</TT>
   <BR><TT></TT>&nbsp;
   <LI>
   Set this variable in the <TT>text_encode</TT> constructor by calling the
   <TT>hasTrueAttribute</TT> method with the attribute name as its argument.&nbsp;
   The <TT>hasTrueAttribute</TT> method returns <TT>true </TT>if the attribute
   is present as a node in the document parse tree.</LI>

   <BR><TT>public text_encode(ActiveElement e) {</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp; super(e);</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp; ActiveAttrList atts = (ActiveAttrList)
   e.getAttributes();</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp; url&nbsp;&nbsp;&nbsp;&nbsp; = atts.hasTrueAttribute("url");</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp; base64&nbsp; = atts.hasTrueAttribute("base64");</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp; entity&nbsp; = atts.hasTrueAttribute("entity");</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp; encrypt = atts.hasTrueAttribute("encrypt");</TT>
   <BR><TT>&nbsp; }</TT></UL>

   <LI>
   &nbsp;Extend the <TT>text_encode</TT> action method.&nbsp; This function
   is triggered when the text tag is evaluated.&nbsp; Add an if clause to
   the existing action method to call the encoding routine that will be used
   to encrypt the string.</LI>

   <BR>&nbsp;<TT>public void action(Input in, Context aContext, Output out,</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ActiveAttrList atts,
   NodeList content) {</TT><TT></TT>

   <P><TT>&nbsp;&nbsp;&nbsp;&nbsp; List resultList = new List();</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp; if(url) {</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; resultList.append(TextUtil.encodeURLListItems(content));</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp; }</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp; else if(encrypt) {</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; resultList = TextUtil.encryptListItems(content);</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp; }</TT>
   <BR><TT>&nbsp;&nbsp; ...</TT>
   <BR><TT>&nbsp;&nbsp;&nbsp;&nbsp; putEnum(out, resultList.elements());</TT>
   <BR><TT>&nbsp;&nbsp; }</TT>
   <LI>
   Write or incorporate a string encoding method. The function that does the
   encoding will be similar to that written for URL encoding:&nbsp; <TT>TextUtil.encodeURLListItems</TT>.&nbsp;
   The function should take</LI>

   <BR>the content node list (which is the list of nodes in the parse tree)
   as an argument, and return a list of encoded nodes.&nbsp; These nodes are
   then output using the <TT>putEnum</TT> convenience function.
   <BR>In general, the function should:
   <UL>
   <LI>
   &nbsp;Loop through the content node list.</LI>

   <LI>
   &nbsp;For each node that is of type NodeType.TEXT, extract its value, which
   is a String.</LI>

   <LI>
   &nbsp;Encode the string.</LI>

   <LI>
   &nbsp;Set the node value to the encoded string.</LI>

   <LI>
   &nbsp;Push the node onto a result list.</LI>
   </UL>

   <LI>
   Test the new attribute.&nbsp; Below is an example of a test file called
   <TT>encode.xh</TT>. The test code is run as follows:&nbsp;&nbsp; <TT>process
   -t standard encode.xh.</TT>&nbsp; The process program processes the test
   file and writes the results of the tag evaluation to standard out.&nbsp;
   The <TT>standard</TT> argument refers to the standard tagset.</LI>
   </UL>

   <UL><TT>&lt;!doctype html public "-//IETF//DTD HTML//EN//2.0"></TT>
   <BR><TT>&lt;html>&lt;head></TT>
   <BR><TT>&lt;link rev="made" href="mailto:pgage@rsv.ricoh.com"></TT>
   <BR><TT>&lt;/head>&lt;body></TT>
   <BR><TT>&lt;h1>Test file encode.xh&lt;/h1></TT><TT></TT>

   <P><TT>TEST 1: encode encrypt: hello</TT>
   <BR><TT>Should return:&nbsp; &Ccedil;=&ETH;&deg;Š></TT>
   <BR><TT>&lt;text encode encrypt>hello/text></TT><TT></TT>

   <P><TT>&lt;hr></TT>
   <BR><TT>&lt;b>Copyright &amp;copy; 1997 Ricoh Silicon Valley&lt;/b>&lt;br></TT>
   <BR><TT>&lt;!-- the following conditional keeps the id out of the results
   -->&lt;if></TT>
   <BR><TT>&lt;then>&lt;b>$Id$&lt;/b>&lt;br>&lt;/then>&lt;/if></TT>
   <BR><TT>&lt;/body>&lt;/html></TT></UL>
   &nbsp;
*/
class text_encode extends textHandler {

  protected boolean url    = false;
  protected boolean base64 = false;
  protected boolean entity = false;
  
  public void action(Input in, Context aContext, Output out, 
		     ActiveAttrList atts, NodeList content) {

    List resultList = new List();
    if(url) {
      resultList.append(TextUtil.encodeURLListItems(content));
    }
    else if(entity) {
      resultList = TextUtil.encodeEntityListItems(content, "&<>'\"");
    }
    else if(base64) {
      resultList.append(TextUtil.encodeBase64ListItems(content));
    }
    putEnum(out, resultList.elements());
  }

  /** Encode special characters as either url, base64 or entity. */
  public text_encode(ActiveElement e) {
    super(e);
    ActiveAttrList atts = (ActiveAttrList) e.getAttributes();
    url    = atts.hasTrueAttribute("url");
    base64 = atts.hasTrueAttribute("base64");
    entity = atts.hasTrueAttribute("entity");
  }
  static Action handle(ActiveElement e) { return new text_encode(e); }
}

