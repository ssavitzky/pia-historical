////// Set.java:  Handler for <set>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.sgml.SGML;
import crc.sgml.Text;
import crc.sgml.Tokens;
import crc.sgml.Token;
import crc.sgml.Element;
import crc.sgml.DescriptionList;
import crc.sgml.TableElement;

 import crc.ds.Index;


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
    "<set name=\"name\" [copy] [attr=attr | insert=where]\n" +
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
    
      Index index = new Index(name);
      String key = index.shift();
      //  do we need an SGML context?
      boolean isPath = ( index.size()>0 || it.hasAttr("attr") || it.hasAttr("insert") )?  true: false;
      if(isPath) System.out.println("i size is" + index.size());
      
      // the context to make modifications in
      SGML root = null;
      SGML value = it.isEmpty()? new Text("") : it.content().simplify();
      // at this point value may be token or tokens

    
      //This is ugly because we have to check for existing tokens if we need an SGML context
      if (it.hasAttr("element")) {
	root = ii.getElementWithAttr(key, it.attr("tag").toString());
      } else if (it.hasAttr("local")) {
	// is key already defined?
        if(isPath && ii.getLocalBinding(key) != null) 
	  root=ii.getLocalBinding(key);
	else {
	  if(!isPath) ii.defvar(name,  value);
	  else {
	     root=new Element("");
	     ii.defvar(key, root);
	  }
	}
      } else if (it.hasAttr("global")) {
	if(ii.entities().has(key) && isPath) root = ii.getGlobal(key); 
	else {
	  if(!isPath)  ii.setGlobal(name, value);
	  else{
	    root=new Element("");
	    ii.setGlobal(key, root);
	  }
	}
      } else {
	// default set anything that we find
	if(!isPath)
	ii.setvar(key, value);
	else {
	  root = ii.getvar(key);
	  if(root == null) {
	    root=new Element("");
	    ii.setGlobal(key,root);
	  }
	}
      }
      // do path processing
      if(isPath){
	if(root == null) {
	  ii.error(ia,"SET FAILED" + it);
	}else {
	  try{
	    SGML toModify = index.path(root);
	    if(it.hasAttr("attr")){
	      toModify.attr(Util.getString(it, "attr", null),value);
	    } else if( it.hasAttr("insert")){
	      int  where;
	      where = (int) crc.sgml.Util.numValue(it.attr("insert"));
	      if(where < 0 || where>toModify.content().nItems()){
		// setting beyond end of list fails.. so just append
		toModify.append(value);
	      } else {
		toModify.content().itemAt(where,value);
	      }
	    } else if(it.hasAttr("key")){
	      // try to set value of key in dl context
	      if(toModify instanceof DescriptionList){
		toModify.append(new Element("dt",it.attr("key")));
		toModify.append(new Element("dt",(SGML)it.content()));
	    }else{
	      //SET failed.... add an element with key as tag
	      toModify.append(new Element("key",(SGML)it.content()));
	    }
	    } else if(it.hasAttr("row") || it.hasAttr("col")){
	      TableElement t =(TableElement) toModify;
	    int row =(int) crc.sgml.Util.numValue(it.attr("row"));
	    int col =(int) crc.sgml.Util.numValue(it.attr("col"));
	    t.setRowColumn(row, col, value);
	    } else {
	      // default is to replace contents with value
	      Util.setSGML( toModify,value);
	      //toModify.append( value);
	    }
	  } catch(Exception e)
	    {ii.error(ia,"SET  -- path lookup FAILED" + it);
	    }
	}
      }
	  
      if (it.hasAttr("copy")) {
	ii.replaceIt(value);
      } else {
	ii.deleteIt();
      }
    }
  }
}
  
