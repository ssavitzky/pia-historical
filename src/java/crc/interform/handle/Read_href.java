////// Read_href.java:  Handler for <read.href>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;

import crc.pia.Agent;
import crc.pia.agent.AgentMachine;

import crc.sgml.SGML;
import crc.sgml.Element;

import crc.interform.Run;

import crc.interform.Tagset;
import crc.ds.TernFunc;
import crc.pia.Content;
import crc.pia.Agent;
import crc.pia.Transaction;
import crc.content.text.ParsedContent;
import crc.util.NullOutputStream;

import java.net.URL;
import java.io.ByteArrayOutputStream;

/** Handler class for &lt;read-href&gt tag 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;read.href href="url" [method="get|head"] [query="query-string"]
 *	              [base="path"] [process [tagset="name"]][wait="time"]&gt;
 * <dt>Dscr:<dd>
 *	Input from HREF, with optional BASE path.  
 *	Optionally PROCESS with optional TAGSET.
 *	Use GET (default) or HEAD HTTP method.
 *  </dl>
 */
public class Read_href extends Get {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<read.href href=\"url\" [method=get|head|put] [query=\"query-string\"]\n"+
    "      [base=\"baseurl\"] [process [tagset=\"name\"]] [wait=\"time\"]>\n"+
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Input from HREF, with optional BASE path.  \n" +
    "Optionally process with TAGSET.\n" +
    "Query is the query string, ? will be replaced by & \n" +
    "(URLs cannot be processed as part of this document).\n" +
    "Use GET or HEAD or POST method.  \n" +
    "WAIT time seconds for response and return SGML element with content of response \n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String href = Util.getString(it, "href", null);
    if (ii.missing(ia, "href", href)) return;
    // href must be fully qualified or supplied base
    if(it.hasAttr("base")){
      String base = Util.getString(it, "base", null);
      try{
	href = new URL(new URL(base),href).toString();
      } catch ( Exception e){
	//  could not generate absolute url
      }
    }

    // this will hold the resulting data as determine by the callback
    // however by the time result is populated, it may be too late
    // callback should notify us when it is ready in case we are waiting
    Element result = new Element();
    
    String method;
    
    if(it.hasAttr("method")){
      method = it.attr("method").toString();  // should verify get / head
    } else{
      method = "GET";
    }
    boolean process= false;
    String tsname = null;
    tsname = it.attrString("tagset");
    if (tsname == null) {
	tsname = "HTML";  // default tagset for processing
      }
    if (it.hasAttr("process") ||it.hasAttr("tagset") || it.hasAttr("findall") || it.hasAttr("name") ) {
      process= true;
     }


    // should we wait for the result?
    long waitTime = 0;
    if(it.hasAttr("wait")){
	// default is wait 10 minutes
	String time = Util.getString(it,"wait","600");
	// time specified in seconds
	if(time != null) try {
	  waitTime = Long.parseLong(time) * 1000;
	} catch( Exception e) {
	  // number format  bad use default
	  waitTime = 600 * 1000;
	}
    }
	
    // also some tags imply waiting
    if(waitTime == 0 && isComplex(it)){
      waitTime = 30 * 1000;// wait 60 seconds by default
    }


    /**  get the agent
     */
    Run env = Run.environment(ii);
    Agent agent =env.agent;
    AgentMachine m = new AgentMachine(agent);

    // callback which will notify us when result has been populated with content
    ContentToSGMLConverter cb =  new ContentToSGMLConverter(result, ii, process, tsname);
    m.setCallback(cb);
    
    // set query string if any -- null is OK
    String query = it.attrString("query");
    if(query != null) query = query.replace('?','&');
    query = "?"+query;
    
    // create a request and go get it    
    agent.createRequest(m, method, href,  query);


    // if we are supposed to wait, do so now
    // since other interforms may need this handler
    // have the ii wait
    
    
    if(waitTime>0) synchronized(ii){
      try{
	ii.wait( waitTime);
      }
      catch( Exception e){
	//interrupted... in any case just continue
      }
    }

    SGML  results = result;
    // if name exists, use as an index
    //if(it.hasAttr("name")){
    results = getValue(result,it);
      //}
    results=processResult(results,it);
    ii.replaceIt(results);
      

  }
}




/**
 *  callback for converting content   into SGML
 */
class ContentToSGMLConverter  implements TernFunc 
{

  SGML result;
  
  Interp ii;  boolean process; String tsname;
  Tagset tags;
  
// constructor
  ContentToSGMLConverter(SGML result, Interp ii,  boolean process, String tsname)
  {
    this.result = result;
    this.ii =ii;
    this.process = process;
    this.tsname = tsname;
    if(process && tsname != null) tags = Tagset.tagset(tsname);
  }
  

// implement the callback

public Object execute  (Object content, Object transaction, Object agent)
  {
   SGML result = convert((Content)  content, (Transaction) transaction, (Agent) agent);
   synchronized(ii){ii.notify();}
   return result;
  }
  
 SGML convert(Content c, Transaction t, Agent a)
  {
    if(process && (c  instanceof ParsedContent )){
      ParsedContent p = (ParsedContent) c;
      p.setTagset(tags);
      result.append(p.getParseTree());
      return result;
      
    } else {
       // convert to  string or just set a image tag
      String type=t.contentType();
      if( type.indexOf("text") < 0){
	// insert as image tag
	URL u = t.requestURL();
	if(u != null){
	  Element img = new Element("img");
	  img.attr("href",u.toString());
	  result.append(img);
	} 
      } else {
	// insert as text
	  ByteArrayOutputStream b =  new ByteArrayOutputStream();
	  try{
	    c.writeTo(b);
	  } catch ( Exception e){}
	  // add string to result
	   result.append(b.toString());
      }
      
    }
    
    return result;
  }
  
}
