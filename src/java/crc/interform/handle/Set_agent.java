////// Set.agent.java:  Handler for <set.agent>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Run;
import crc.interform.SecureAttrs;

import crc.sgml.SGML;
import crc.sgml.AttrWrap;

import crc.ds.Index;
import crc.sgml.Tokens;

/** Handler class for &lt;set.agent&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#set.agent">Manual
 *	Entry</a> for syntax and description.
 */
public class Set_agent extends Set {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<set.agent name=\"name\" [hook] [copy]>...</set.agent>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "set NAME to CONTENT in AGENT.  May set a HOOK (parsed InterForm) \n" +
    "or string value.  Optionally COPY content as result.  \n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    // get the appropriate index
    String name = getName(it);
    Index index = getIndex(it);
    if(index == null && name==null){
      ii.error(ia, " name or index attribute missing or null");
      return;
    }
      
    String key = (name == null) ? index.shift() : name; 
    
    
    //  do we need an SGML context?
    boolean isComplexSet = isComplex( index, it);

    SGML value = getValue(it);

    String aname= Util.getString(it, "agent", Run.getAgentName(ii));

    Run env = Run.environment(ii);

    crc.pia.Agent a = env.getAgent(aname);

    if (a == null) {
      ii.error(ia, "agent " + aname + " not running");
      doFinish(it,value,ii);
      return;
    }
    if(isComplexSet){
      debug(this," agent "+aname+" doing complex set " + it);
      // do complex set
      doComplexSet( key,index,new AttrWrap(new SecureAttrs(a, ii)),  value ,ia, it, ii);
    } else {
      // do the simple set
      if( key != null )
	// a.attr(key, value);	// security unimplemented! ===  
	a.put(key, value);	// security unimplemented! ===  
    }
    doFinish(it,value,ii);

  }
}

