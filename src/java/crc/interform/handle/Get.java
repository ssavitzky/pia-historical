////// Get.java:  Handler for <get>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.ds.Index;
import crc.ds.List;

import crc.sgml.SGML;
import crc.sgml.TableElement;
import crc.sgml.DescriptionList;
import crc.sgml.AttrSGML;
import crc.sgml.Tokens;

import java.util.Enumeration;
import java.util.Hashtable;

/** Handler class for &lt;get&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;get [name="name" | index="index"] 
 *	     [pia|agent|form|trans|env|element[tag=tag]|local|global
 *	     | [file="filename"|href="url"|[file|href] name="string" ] 
 *           [attr=attr | size | row=row col=col |rows=rows cols=cols | key=key | keys | values | findAll=tag ]&gt;
 * <dt>Dscr:<dd>
 Get value of NAME, optionally in PIA, ENV, AGENT, FORM, 
 * ELEMENT, TRANSaction, or LOCAL or GLOBAL entity context.
 * Default is to start with the local entity table and move up the
 * stack until name is found.  Returns \"\" if name does not exist in
 * specified context.  Elements of complex data structures can be accessed
 * using an INDEX -- dotted notation \"foo.bar\" returns the bar elements of
 * foo.  While \"foo.bar-3-5\"  returns the 3rd, 4th, and 5th bar  elements.
 * If FILE or HREF specified, functions as <read>.
 * The last set of attributes applies to the retrieved SGML object.
 * SIZE returns number of elements retrieved.
 * ATTR returns the value of the named attribute (can be tag). 
 * FINDALL returns a list of all the objects within the SGML object which
 * a tag equal to FTAG. 
 * KEY(s)/VALUES and ROW(s) COL(s) have
 *  meanings only when the specified SGML object is a DL or Table respectively.
 * If the retrieval specifies more than one SGML object, these attributes apply
 *  to all of the retrieved objects and a list will be returned
 *  </dl>
 * subclasses are used to retrieve objects in the  AGENT FORM TRANS contexts.  
 * PIA and ENV are handled by separate classes and do not return SGML objects
 * (hence  cannot be used with the special attributes).
 */
public class Get extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<get [name=\"name\" | index=\"index\"] \n" +
    "[pia|agent|form|trans|env|element[tag=tag]|local|global\n" +
    "| [file=\"filename\"|href=\"url\"|[file|href] name=\"string\" ] >\n" +
    "[attr=attr | size | row=row col=col |rows=rows cols=cols | key=key | keys | values | findAll=ftag  ]\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Get value of NAME, optionally in PIA, ENV, AGENT, FORM, \n" +
    "ELEMENT, TRANSaction, or LOCAL or GLOBAL entity context.\n" +
    "Default is to start with the local entity table and move up the\n" +
     "stack until name is found.  Returns \"\" if name does not exist in\n" +
     "specified context.  \n" +
     "Elements of complex data structures can be accessed\n" +
  "using an INDEX -- dotted notation \"foo.bar\" returns the bar elements of\n" +
  "foo.  While \"foo.bar-3-5\"  returns the 3rd, 4th, and 5th bar.\n" +
    "If FILE or HREF specified, functions as <read>.\n" +
  "The last set of attributes applies to the retrieved SGML object.\n" +
  "SIZE returns number of elements retrieved.\n" +
  "ATTR returns the value of the named attribute (can be tag).\n" + 
   "FINDALL returns a list of all the objects within the SGML object which \n"+
   "a tag equal to FTAG.\n" + 
  "KEY(s)/VALUES and ROW(s) COL(s) have\n" +
  " meanings only when the specified SGML object is a DL or Table respectively.\n" +
  "If the retrieval specifies more than one SGML object, these attributes apply\n" +
  " to all of the retrieved objects and a list will be returned\n" +
"";
 
  /** Handle for &lt;get&gt.  The dispatching really should be in 
   *	actOn; we're faking it for now. === */
  public void handle(Actor ia, SGML it, Interp ii) {
    if (it.hasAttr("pia")) dispatch("get.pia", ia, it, ii);
    else if (it.hasAttr("agent")) dispatch("get.agent", ia, it, ii);
    else if (it.hasAttr("form")) dispatch("get.form", ia, it, ii);
    else if (it.hasAttr("trans")) dispatch("get.trans", ia, it, ii);
    else if (it.hasAttr("env")) dispatch("get.env", ia, it, ii);
    else if (it.hasAttr("href")) dispatch("read.href", ia, it, ii);
    else if (it.hasAttr("file")) dispatch("read.file", ia, it, ii);
    else {
      /* The following are all in the Basic tagset,
       *     so it's cheaper not to dispatch on them.
       */
      String name = getName(it);
      Index i = getIndex(it);
      if(name == null && i == null) {
	ii.error(ia, "no name or index specified");
	return;
      }
      if(name == null){
	// use first part of index as name -- does not get parsed
	 name = i.shift();
      }

      // first get the SGML object
      SGML result = null;

      if (it.hasAttr("element")) {
	if (it.hasAttr("tag")) {
	  result = ii.getAttr(name, it.attr("tag").toString());
	} else {
	  result = ii.getAttr(name, null);
	}
      } else if (it.hasAttr("local")) {
	result = ii.getvar(name);  // look only in local table
      } else if (it.hasAttr("global")) {
	result = ii.getGlobal(name); // look only in global table
      } else {
	result = ii.getvar(name); // start in local table and move up
	if(result == null) result = ii.getGlobal(name);
      }

      // do rest of path processing
      if(i != null) {
	result = getValue(result,i);
      }

      // do any other attributes
      result = processResult(result,it);
      ii.replaceIt(result);
    }
  }

  /************************************************************
  ** utility functions
  ************************************************************/


  /**
   * return the name identifier specified by request token
   */
  protected String getName(SGML it){
    String name = Util.getString(it, "name", null);
     return name;
  }

  /**
   * return the index object specified by request token
   */
  protected Index getIndex(SGML it){
    String name = Util.getString(it, "index", null);
    if(name == null || "".equals(name)) return null;
    return new Index(name);
  }
  
  /**
   * use the index interface to find value
   */

  protected SGML getValue(SGML  context,Index i){
    if(i == null) return context;
    if(i.size()>0){
      try{
	return i.lookup(context);
      } catch(Exception e){
	// if lookup fails assume value is null
	return null;
      }
    }
    return  context;
  }
  

  /**
   * look up name / index in context -- use name first if any
   */
  SGML getValue(SGML context, String name, Index i){
    if( context == null) return context;
     SGML local = context;
     if (name != null) {
         local = context.attr(name);
     }
     if (i != null) {
        local = getValue( local, i);
     }
      return local;
  }

  /**
   * look up it in context -- name takes precedence over index
   */

  protected SGML getValue(SGML  context,SGML it){
    String name = getName(it);
    Index i = getIndex(it);
    return  getValue( context, name, i);
  }

  void debug (Object o,  String s){
    //    System.out.println(s);
    crc.pia.Pia.debug(o,s);
  }


  /**
   * return true if we recognize any of the attributes
   */
  public boolean isComplex(SGML it){
     return (it.hasAttr("index") ||
	     it.hasAttr("findall") ||
	     it.hasAttr("size") ||
	     it.hasAttr("keys") ||
	     it.hasAttr("attr") ||
	     it.hasAttr("row") || it.hasAttr("rows") ||
	     it.hasAttr("col") || it.hasAttr("cols"));
  }

      /************************************************************
      ** check for any special attributes of request
      ** processing done here could eventually include database interface
      ************************************************************/

  /**
   * if process result given the attributes of the request
   */

  protected SGML processResult(SGML result, SGML it) {
    if( result == null) return result;
    
    result = result.simplify();
    if(it.hasAttr("size")){
      Tokens  content = result.content();
      int size = (content == null)? 0: content.nItems();
      result = crc.sgml.Util.toSGML(String.valueOf(size));
    } else {
      if(result  instanceof Tokens){
	// repeat for all items
	Enumeration e = ((Tokens) result).elements();
	result = new Tokens();
	while(e.hasMoreElements()){
	  result.append(getQueryResults((SGML) e.nextElement(), it));
	}
      } else {
	result =  getQueryResults(result, it);
      }
    }
    return result;
  }

  /**
   *   process the attributes of a get request and return the corresponding
   * data. @param context is the SGML object to do the lookup on
   * @param request is the (get) token with the request
   */
  public SGML getQueryResults( SGML context, SGML request){
    //context should always be a Token
    if(context == null) return context;
    debug(this, "getting "+ request + " in " + context.getClass().getName());

    // take attributes in order -- return after processing first matching attribute

    // attr case
    if(request.hasAttr("attr")){
      String s =Util.getString( request, "attr", "");
      if(s.equalsIgnoreCase("tag")){// return the tag of context
	 return  Util.toSGML(context.tag());
      }
      return context.attr(s);
    }

    // key case
    if(request.hasAttr("key")){
      if(context instanceof AttrSGML)
	return context.attr(Util.getString( request, "key", ""));
      if(context instanceof DescriptionList)
	try{
	return new Index(Util.getString( request, "key", "")).lookup(context);
      } catch (Exception e){
	return null;
      }
      // key has no meeting for other items
       return null;
    }

    // keys case
    if(request.hasAttr("keys")){
      if(context instanceof AttrSGML){
	AttrSGML a= (AttrSGML) context;
	return new Tokens(a.attrs());
      }	
      if(context instanceof DescriptionList)
	try{
	return new Index("KEYS").lookup(context);
      } catch (Exception e){
	return null;
      }
      // keys has no meeting for other items
      return null;
    }
    
    // values case
    if(request.hasAttr("values")){
      if(context instanceof AttrSGML){
	SGML result = new Tokens();
	AttrSGML a = (AttrSGML) context;
	Enumeration e =   a.attrs();
	while(e.hasMoreElements()){
	result.append( context.attr((String)e.nextElement()));
	}
	return result;
      }
      if(context instanceof DescriptionList)
	try{
	return new Index("VALUES").lookup(context);
      } catch (Exception e){
	return null;
      }
      //  values has no meeting for other items
       return null;
    }

    // rows/cols case
    // check for row / columns of table
    if(context instanceof TableElement){
      TableElement table=(TableElement) context;
	
      if(request.hasAttr("row") || request.hasAttr("col")){
	int row=Integer.parseInt(Util.getString(request,"row","-1"));
	int col=Integer.parseInt(Util.getString(request,"col","-1"));
	return table.getRowColumn(row,col);
      }
      if(request.hasAttr("rows") || request.hasAttr("cols")){
	String rows=Util.getString(request,"rows","-");
	String cols=Util.getString(request,"cols","-");
	int rowstart=1,rowend=-1;
	int colstart=1,colend=-1;
	
	if(!rows.equals("-")){
	  int i=rows.indexOf('-');
	  if(i<0){ // just a start
	    rowstart=Integer.parseInt(rows);
	    rowend=rowstart;
	  } 
	  if(i>0)
	    rowstart=Integer.parseInt(rows.substring(0,i));
	  if(i>=0 && i + 1<rows.length())
	    rowend=Integer.parseInt(rows.substring(i + 1));	      
	}

	if(!cols.equals("-")){
	  int i=cols.indexOf('-');
	  if(i<0){ // just a start
	    colstart=Integer.parseInt(cols);
	    colend=colstart;
	  } 
	  if(i>0)
	    colstart=Integer.parseInt(cols.substring(0,i));
	  if(i>=0 && i + 1<cols.length())
	    colend=Integer.parseInt(cols.substring(i + 1));	      
	}

	debug(this," getting table " + rowstart +"-"+rowend+","+ colstart + "-"+ colend);
	return  table.getTable(rowstart,rowend,colstart,colend);
      }
    }

    // traversal case
    if(request.hasAttr("findAll")){
       String tag = Util.getString(request,"findAll","");
        debug( this, "Finding all "+ tag);
      // descend context looking for tokens with this tag -- default is text
       List stack = new List();
       stack.push(context);
       Hashtable visited = new Hashtable();
       Tokens  result = new Tokens();
       while(!stack.isEmpty()){
	 SGML  item = (SGML) stack.shift();
	 if(tag.equalsIgnoreCase(item.tag())){
	    result.push(item);
	 }
	 Tokens rest = item.content();
	 if( rest != null){
	   Enumeration e = rest.elements();
	   while(e.hasMoreElements()){
	     Object o = e.nextElement();//really SGML
	     if(! visited.containsKey(o)){
	       visited.put(o, item);
	       stack.push(o);
	     }
	   }
	 }
       }
       return result;
    }
    // default is to return context
     return context;
    }

}
