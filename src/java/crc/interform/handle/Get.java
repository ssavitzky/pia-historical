////// Get.java:  Handler for <get>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.ds.Index;

import crc.sgml.SGML;
import crc.sgml.TableElement;
import crc.sgml.DescriptionList;
import crc.sgml.AttrSGML;
import crc.sgml.Tokens;

import java.util.Enumeration;

/** Handler class for &lt;get&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;get [name="name"] 
 *	     [pia|agent|form|trans|env|element[tag=tag]|local|global
 *	     | [file="filename"|href="url"|[file|href] name="string" ] &gt;
 * <dt>Dscr:<dd>
 *	Get value of NAME, optionally in PIA, ENV, AGENT, FORM, 
 *	ELEMENT, TRANSaction, or LOCAL or GLOBAL entity context.
 *      Default is to start with the local entity table and move up the
 *      stack until name is found.  Returns "" if name does not exist in
 *      specified context.  Elements of complex data structures can be accessed
 *      using a dotted notation "foo.bar" returns the bar element of foo.
 *	If FILE or HREF specified, functions as &lt;read&gt;.
 *  </dl>
 */
public class Get extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<get [name=\"name\"] \n" +
    "[pia|agent|form|trans|env|element[tag=tag]|local|global\n" +
    "| [file=\"filename\"|href=\"url\"|[file|href] name=\"string\" ] >\n" +
    "[attr=attr | size | row=row col=col |rows=rows cols=cols | key=key | keys | values  ]\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Get value of NAME, optionally in PIA, ENV, AGENT, FORM, \n" +
    "ELEMENT, TRANSaction, or LOCAL or GLOBAL entity context.\n" +
    "Default is to start with the local entity table and move up the\n" +
     "stack until name is found.  Returns \"\" if name does not exist in\n" +
     "specified context.  Elements of complex data structures can be accessed\n" +
     "using a dotted notation \"foo.bar\" returns the bar element of foo.\n" +
    "Default is the generic lookup that includes paths.\n" +
    "If FILE or HREF specified, functions as <read>.\n" +
  "The last set of attributes applies to the retrieved SGML object.\n" +
  "SIZE returns number of elements in corresponding SGML, ATTR returns\n" +
  " the value of the named attribute.  KEY(s)/VALUES and ROW(s) COL(s) have\n" +
  " meanings only when the specified SGML object is a DL or Table respectively.\n" +
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
      String name = Util.getString(it, "name", null);
      if (ii.missing(ia, "name", name)) return;

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
	result = ii.getEntity(name); // start in local table and move up
      }
      
       result =  getQueryResults(result, it);
           
      ii.replaceIt(result);
    }
  }


  void debug (Object o,  String s){
    //    System.out.println(s);
    crc.pia.Pia.debug(o,s);
  }
      /************************************************************
      ** check for any special attributes of request
      ** processing done here could eventually include database interface
      ************************************************************/
  /**
   *   process the attributes of a get request and return the corresponding
   * data. @param context is the SGML object to do the lookup on
   * @param request is the (get) token with the request
   */
  public SGML getQueryResults( SGML context, SGML request){
    if(context == null) return context;
    debug(this, "getting "+ request + " in " + context.getClass().getName());
    if(request.hasAttr("attr"))
       return context.attr(Util.getString( request, "attr", ""));
    if(request.hasAttr("size") && context.content() != null)
       return crc.sgml.Util.toSGML(String.valueOf(context.content().nItems()));
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

    // check for row / columns of table
    if(context instanceof TableElement){
      TableElement table=(TableElement) context;
	
      if(request.hasAttr("row") || request.hasAttr("col")){
	int row=Util.getInt(request,"row",-1);
	int col=Util.getInt(request,"col",-1);
	return table.getRowColumn(row,col);
      }
      if(request.hasAttr("rows") || request.hasAttr("cols")){
	String rows=Util.getString(request,"rows","-");
	String cols=Util.getString(request,"cols","-");
	int rowstart=0,rowend=-1;
	int columnstart=0,columnend=-1;
	
	int i=rows.indexOf('-');
	if(i>0)
	  rowstart=Integer.parseInt(rows.substring(0,i));
	if(i>=0 && i + 1<rows.length())
	  rowend=Integer.parseInt(rows.substring(i + 1));
	
	i=cols.indexOf('-');
	if(i>0)
	  columnstart=Integer.parseInt(cols.substring(0,i));
	if(i>=0 && i + 1<cols.length())
	  columnend=Integer.parseInt(cols.substring(i + 1));
	debug(this," getting table " + rowstart +"-"+rowend+","+ columnstart + "-"+ columnend);
	return  table.getTable(rowstart,rowend,columnstart,columnend);
      }
    }
    // default is to return context
     return context;
    }

}
