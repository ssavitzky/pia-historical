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
 *	&lt;set name="name" [copy] [attr=attr | insert=where [replace] ]
 *	     [ pia | agent | trans [feature] | env 
 *  	     | [element [tag=ident] | [global | local] ]&gt;...&lt;/set&gt;
 * <dt>Dscr:<dd>
 *	set NAME to CONTENT, optionally in PIA, AGENT, TRANSaction, 
 *	ENVironment, ELEMENT, or ENTITY context.  Entity may be
 *	a LOCAL or GLOBAL binding.   Default is to replace the lowest 
 *      current binding or create a global binding if none exists.
 *      NAME may be a path, e.g. \"foo.bar\" sets the bar item of foo.
 *      Intermediate objects on the path are created if they don't exist.
 *      If WHERE is \"-1\" then the CONTENT will get appended to the specified object.
 *      ELEMENT may have a TAG.  TRANSaction item
 *	may be FEATURE.  AGENT may be a HOOK (parsed InterForm) or string. 
 *	Optionally COPY content as result.
 *  </dl>
 * Subclasses are used to set items in the PIA AGENT TRANS ENV  contexts.
 */
public class Set extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<set name=\"name\" [copy] [attr=attr | insert=where [replace] ]\n" +
    "[ pia | agent | trans [feature] | env \n" +
    "| [element [tag=ident]] | entity [global | local] ]>...</set>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "REPLACE or INSERT content or ATTR of NAMEd object with CONTENT. \n"+
  "Context may be optionally in PIA, AGENT, TRANSaction, \n" +
    "ENVironment, ELEMENT, or ENTITY context.  ENTITY may define\n" +
    "a LOCAL or GLOBAL binding.   Default is to replace the lowest current binding\n" +
    "or create a global binding if none exists.\n" +
  "NAME may be a path, e.g. \"foo.bar\" sets the bar item of foo.\n" +
  "Intermediate objects on the path are created if they don't exist.\n" +
  "If WHERE is \"-1\" then the CONTENT will get appended to the specified object.\n" +
    "ELEMENT may have a TAG.  TRANSaction item\n" +
    "may be FEATURE.  \n" +
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

      // get the appropriate index
      Index index = getIndex(it);
      if(index == null){
	ii.error(ia, " name attribute missing or null");
	 return;
      }
    
      SGML value = getValue(it);
      // at this point value may be token or tokens


      //  do we need an SGML context?
      boolean isComplexSet = isComplex( index, it);

      // get the first item for finding the correct table
      String key = index.shift();
      debug(this, " setting value of "+key+" to instance of "+value.getClass().getName());

      if(isComplexSet){
	debug(this," doing complex set " + it);
	// do complex set
	doComplexSet(key, index, value , ia, it, ii);
      }  else {
	// do a simple set
	// these could be dispatch to set.local
	if (it.hasAttr("local")) {
	// is key already defined?
	  ii.defvar( key,  value);
	} else if (it.hasAttr("global")) {
	  ii.setGlobal( key, value);
	} else {
	ii.setvar( key, value);
	}
      }
      doFinish(it,value,ii);
    }
  }

  /************************************************************
  ** utility functions 
  ************************************************************/

  /**
   * replace content with value if necessary otherwise delete it
   */
  protected void doFinish(SGML it, SGML value, Interp ii){
    if (it.hasAttr("copy")) {
      ii.replaceIt(value);
    } else {
      ii.deleteIt();
    }
  }


  /**
   * return the index derived from name attribute
   */

  protected Index getIndex(SGML it){
    String name = Util.getString(it, "name", null);
    if(name == null || "".equals(name)) return null;
    return new Index(name);
  }

  /**
   * return the contents of it
   */
  protected SGML getValue(SGML it){
    return it.isEmpty()? new Text("") : it.content().simplify();
  }

  /**
   * return true return true if index has more than one item or 
   * attributes which affect the specified item
   */
  protected boolean isComplex(Index index, SGML it){
    // 
    return ( index.size()>1 || 
	     it.hasAttr("attr") ||
	     it.hasAttr("insert") ||
	     it.hasAttr("row") || it.hasAttr("col") ||
	     it.hasAttr("key") ||
	     it.hasAttr("element"));
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
    doComplexSet( index, root, value, ia ,it, ii);
  }
  

  /**
   * given the root SGML object, do the complex set
   */
  protected void doComplexSet(Index index, SGML root, SGML value, Actor ia, SGML it, Interp ii){
    if(index==null || root == null || it == null) return;
    if(index.size()>0){
      try{
	root = index.path(root);
      } catch (Exception e){
	// path could not be created
	debug(this,"SET FAILED could not create path"+it);
	ii.error(ia,"SET FAILED could not create path"+it);
	//root = null;
	return;
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
    if(context == null) return context;
    
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
  
