////// Set.java:  Handler for <set>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.AttrSGML;
import crc.sgml.Text;
import crc.sgml.Tokens;
import crc.sgml.Token;
import crc.sgml.Element;
import crc.sgml.DescriptionList;
import crc.sgml.TableElement;

import crc.ds.Index;

import java.util.Enumeration;

/** Handler class for &lt;set&gt tag 
 * this description is out of date please update from syntax
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;set name="name" [copy]
 *	     [ pia | agent [hook] | trans [feature] | env 
 *  	     | [element [tag=ident] | entity [local] ]&gt;...&lt;/set&gt;
 * <dt>Dscr:<dd>
 *	set NAME to CONTENT, optionally in PIA, AGENT, TRANSaction, 
 *	ENVironment, ELEMENT, or ENTITY context.  ENTITY may define
 *	a LOCAL or GLOBAL binding.   Default is to replace the lowest 
 *      current binding and create global binding if none exists.
 *      ELEMENT may have a TAG.  TRANSaction item
 *	may be FEATURE.  AGENT may be a HOOK (parsed InterForm) or string. 
 *	Optionally COPY content as result.
 *  </dl>
 */
public class Set extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<set name=\"name\" [copy] [attr=attr | insert=where [replace] ]\n" +
    "[ pia | agent [hook] | trans [feature] | env \n" +
    "| [element [tag=ident]] | entity [global | local] ]>...</set>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "replace or insert content or ATTR of NAMEd object with CONTENT. \n"+
  "context may be optionally in PIA, AGENT, TRANSaction, \n" +
    "ENVironment, ELEMENT, or ENTITY context.  ENTITY may define\n" +
    "a LOCAL or GLOBAL binding.   Default is to replace the lowest current binding\n" +
    "and create global binding if none exists.\n" +
    "ELEMENT may have a TAG.  TRANSaction item\n" +
    "may be FEATURE.  AGENT may be a HOOK (parsed InterForm) or string. \n" +
    "Optionally COPY content as result.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    if (it.hasAttr("pia")) dispatch("set.pia", ia, it, ii);
    else if (it.hasAttr("agent")) dispatch("set.agent", ia, it, ii);
    else if (it.hasAttr("form")) dispatch("set.form", ia, it, ii);
    else if (it.hasAttr("trans")) dispatch("set.trans", ia, it, ii);
    else if (it.hasAttr("env")) dispatch("set.env", ia, it, ii);
    else {
      /* The following are all in the Basic tagset,
       *     so it's cheaper not to dispatch on them.
        */
      String name = Util.getString(it, "name", null);
      if (ii.missing(ia, "name", name)) return;
    
      SGML value = it.isEmpty()? new Text("") : it.content().simplify();
      // at this point value may be token or tokens
      debug(this, " setting value of "+name+" to instance of "+value.getClass().getName());

      Index index = new Index(name);
      String key = index.shift();
      //  do we need an SGML context?
      boolean isComplexSet = ( index.size()>0 || 
			       it.hasAttr("attr") ||
			       it.hasAttr("insert") ||
			       it.hasAttr("row") || it.hasAttr("col") ||
			       it.hasAttr("key") ||
			       it.hasAttr("element"));

      if(isComplexSet){
	debug(this," doing complex set " + it);
	// do complex set
	doComplexSet(key, index, value , ia, it, ii);
      }  else {
	// do a simple set
	if (it.hasAttr("local")) {
	// is key already defined?
	  ii.defvar(name,  value);
	} else if (it.hasAttr("global")) {
	  ii.setGlobal(name, value);
	} else {
	ii.setvar(name, value);
	}
      }
  
      if (it.hasAttr("copy")) {
	ii.replaceIt(value);
      } else {
	ii.deleteIt();
      }
    }
  }

  void debug(Object o, String s){
    crc.pia.Pia.debug(o,s);
    //    System.out.println(s);
  }

  /************************************************************
  ** process complex requests
  ************************************************************/
  /**
   * first find the existing SGML object, then manipulate it
   */

  void doComplexSet(String key, Index index, SGML value,  Actor ia, SGML it, Interp ii){
	
      // the context to make modifications in
      SGML root = null;

      if (it.hasAttr("element")) {
	root = ii.getElementWithAttr(key, it.attr("tag").toString());
      } else if (it.hasAttr("local")) {
	// is key already defined?
	root=ii.getLocalBinding(key);
	if(root == null){
	  debug(this,"  defining top level var " + key);
	  root=new Element("");
	  ii.defvar(key, root);
	}
      } else if (it.hasAttr("global")) {
	root = ii.getGlobal(key); 
	if(root == null){
	  debug(this," creating new entity " + key);
	  root=new Element("");
	  ii.setGlobal(key, root);
	}
      } else {
	root = ii.getEntity(key);
	if(root == null) {
	  debug(this,"creating new entity " + key);
	  root=new Element("");
	  ii.setGlobal(key,root);
	}
      }
      if(root == null) {
	ii.error(ia,"SET FAILED" + it);
      }
      if(index.size()>0){
	try{
	  root = index.path(root);
	} catch (Exception e){
	  // path could not be created
	  ii.error(ia,"SET FAILED could not create path"+it);
	  root = null;
	}
      }
      // process all the attributes
      // if root is a tokens, perform set on each token 
      if( root instanceof Tokens){
	Enumeration e = ((Tokens) root).elements();
	while(e.hasMoreElements()){
	  setQueryRequest((SGML) e.nextElement(), value, it);
	}
      }else{
	setQueryRequest(root, value, it);
      }
  }



  /**
   * set the contents of @param context to @param value
   */

  public void setReplace(SGML context, SGML value){
    debug( this, " replacing "  + value  +" in "+ context);
    Util.setSGML( context,value);
  }

  public void setInsert(SGML context, SGML value, SGML  request){
    int  where;
    Tokens content =  context.content();
    if(content == null){
      // best we can do is trying to append
       context.append( value);
       return;
    }
    where = Util.getInt( request,"insert",content.nItems());
    debug(this, "inserting at " + where + " in " + content);


    if(where < 0 || where >  content.nItems()){
      // setting beyond end of list fails.. so just append
      // use push so that text does not get smashed
       content.push(value);
    } else {
      if( request.hasAttr("replace"))
	content.itemAt(where,value); 
      else content.insertAt(value,where);
    }
  }

  /**
   * find the appropriate part of @param context specified by @param request and set it to @param value
   */

  public void setQueryRequest(SGML context, SGML value, SGML request){
    debug(this,"Set class "+context.getClass().getName());
    if( request.hasAttr("attr")){
       String attr = Util.getString( request, "attr", "");
        context.attr(attr, value);
	 return;
    }

     boolean insert = request.hasAttr("insert");
     if( request.hasAttr("key")){
       String key = Util.getString( request, "key", "");
       if( context instanceof AttrSGML){
	 context.attr( key, value);
       }  else if(context instanceof DescriptionList){
	 DescriptionList dl = (DescriptionList) context;
	 // get the dd associated with this key   
	 if( ! insert ){
	   // remove any current value of key
	   dl.removeKey(key);
	 }
	 dl.at(key,value);
       }
       // nothing else uses keys
       return;
     }

     if(context instanceof TableElement && (request.hasAttr("row") ||  request.hasAttr("col"))){
       TableElement t =(TableElement) context;
       int row =(int) crc.sgml.Util.numValue( request.attr("row"));
       int col =(int) crc.sgml.Util.numValue(request.attr("col"));
       // this should be a td ... if it spans then replacement will span
       SGML cell = t.getRowColumn(row, col);
       if(! insert){
	  setReplace( cell, value);
       } else {
	  setInsert( cell, value, request);
       }
        return;
     }
	
     if( insert) {
        setInsert( context, value, request);
     }else{
       setReplace(context, value);
     }
  }

}
  
