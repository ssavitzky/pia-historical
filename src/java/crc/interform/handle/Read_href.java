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
import crc.interform.Environment;

import crc.interform.Tagset;
import crc.ds.TernFunc;
import crc.pia.Content;
import crc.pia.Agent;
import crc.pia.Transaction;
import crc.content.text.ParsedContent;
import crc.util.NullOutputStream;


import java.net.URL;
import java.io.OutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PipedOutputStream;
import java.io.PipedInputStream;

/** Handler class for &lt;read.href&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#read.href">Manual
 *	Entry</a> for syntax and description.
 */
public class Read_href extends Get {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<read.href href=\"url\" [method=get|head|put] [query=\"query-string\"]\n"+
    "      [base=\"baseurl\"] [process [tagset=\"name\"] | into=filename] [wait=\"time\"]>\n"+
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Input from HREF, with optional BASE path.  \n" +
    "Optionally process with TAGSET.\n" +
    "Query is the query string, ? will be replaced by & \n" +
    "(URLs cannot be processed as part of this document).\n" +
    "Use GET or HEAD or POST method.  \n" +
    "WAIT time seconds for response and return SGML element with content of response \n" +
" or places content into FILENAME.\n";
 
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
    if (it.hasAttr("process") ||it.hasAttr("tagset") || isComplex(it) ) {
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
	  // number format  bad use default 10 min
	  waitTime = 600 * 1000;
	}
    }
	
    // also some tags imply waiting
    if(waitTime == 0 && process){
      waitTime = 30 * 1000;// wait 30 seconds by default
    }

    /**  get the agent
     */
    Run env = Run.environment(ii);
    Agent agent =env.agent;
    AgentMachine m = new AgentMachine(agent);
    // set up the callback for the agent machine
    ContentToSGMLConverter cb;
    
    // This will hold the resulting data.  For processed data, the interpreter
    // must wait to be notified by the callback before proceeding.  If the 
    // data is going INTO a file, then this result hold a IMG link to 
    // that location.
    SGML result = new Element();
    result.attr("href",it.attr("href"));  // propagate attribute
    String localUrl = "";


    /** set up the file if into is specified */
    OutputStream pipeOut = null;
    PipedInputStream pipeIn = null;
    

    if(it.hasAttr("into")){
      String file =  Util.getString(it, "into", "");
      String fbase="";
      String home     = System.getProperty("user.home");
      String fileSep  = System.getProperty("file.separator");
      
      if (file.startsWith("~"+fileSep)) {
        file = file.substring(2);
        fbase = home;
      } else if (file.startsWith(fileSep)) {
        fbase = "";
      } else if (fbase.equals("")) {
        if (ii.environment != null) fbase = ii.environment.baseDir(it);
	// directory local to agent -- create appropriate url
	localUrl = "/" + agent.name() + "/~/" + file;
      }
      if (! fbase.equals("") && ! fbase.endsWith(fileSep)) {
        fbase += fileSep;
      }
      if (file.equals("")) {
        ii.error(ia, "into attribute must have name of file to place content into.");
        ii.deleteIt();
        return;
      }
      file = fbase + file;
      if( localUrl == "") localUrl = "file:"+file;
      File outfile = new File(file);
      File parent = (outfile.getParent()!=null)? new File(outfile.getParent()) : null;

      try {
	if (parent != null && ! parent.exists()) {
	  if (! parent.mkdirs()) ii.error(ia, "Cannot make parent directory");
	}
	pipeOut=new FileOutputStream(outfile);
      } catch (Exception e) {
        ii.error(ia, "Write failed on " + file);
        ii.deleteIt();
        return;
      }

      result.attr("localurl",  new crc.sgml.Text(localUrl));
    }
    else {
      // set up pipes to get the resulting stream back to us
      PipedOutputStream pOut = new PipedOutputStream();
      pipeOut=pOut;
      pipeIn = new PipedInputStream();
      try{
	pOut.connect(pipeIn);
      } catch (Exception e){
	System.out.println(" piping problem ");
	crc.pia.Pia.debug(this, " failed to connect pipes");
      }
      
    }

    // callback which will notify us when result has been populated with content
    cb =  new ContentToSGMLConverter(result, ii, process, tsname, pipeOut);
    if(waitTime>0) cb.notifyCaller = true;
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
      // process our results
      if(process)
	result = processInputStream(pipeIn, result,tsname);
    }

    SGML  results = result;

    // process any "get" attrs, like name, attr, tag, key, ...
    results = getValue(result,it);
    results=processResult(results,it);

    ii.replaceIt(results);

  }

  /**
   * do any needed parsing to convert this thing into something useful.
   * the callback may process the input instead -- if so result should have
   * a completed attribute and the proper contents
   */

  protected SGML processInputStream(PipedInputStream in, SGML result, String tsname){
     if( result.hasAttr("completed") || in == null) return result;
     Environment env = new Environment();
     crc.sgml.Tokens tree = env.parseStream(in,tsname);
     result.append(tree);
     debug(this, " processed " + tree.nItems() + " from href");
     return result;
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
  OutputStream pipeOut; // the stream to write the contents onto
  public boolean notifyCaller=false;
  


  // constructor
  ContentToSGMLConverter(SGML result, Interp ii,  boolean process, String tsname,OutputStream pipeOut)
  {
    this.result = result;
    this.ii =ii;
    this.process = process;
    this.tsname = tsname;
    this.pipeOut = pipeOut;
    if(process && tsname != null) tags = Tagset.tagset(tsname);
  }
  

  // implement the callback

  public Object execute  (Object content, Object transaction, Object agent)
  {
    SGML result = convert((Content)  content, (Transaction) transaction, (Agent) agent);
    if(notifyCaller) synchronized(ii){ii.notify();}
    return result;
  }
  

  /**
   * given the content, transaction, and agent objects
   *  convert the content into an SGML or just notify the sender
   * and send the data out the pipe
   */

 SGML convert(Content c, Transaction t, Agent a)
  {
    // should handle redirections and errors here
    int code = t.statusCode();
    result.attr("responsecode",Util.toSGML(String.valueOf(t.statusCode())));

    //  return content as result if parsed ( currently not used )
    // otherwise return an IMG tag for non text items saved locally

    if(process && (c  instanceof ParsedContent )){
      ParsedContent p = (ParsedContent) c;
      p.setTagset(tags);
      result.append(p.getParseTree());
      result.attr("completed"); // no more processing needs to be done
      return result;
    }
    // wake up our listener before pumping data to avoid deadlock
    if(notifyCaller) synchronized(ii){ii.notify();}
    // send to pipe for caller to process
    if(pipeOut != null) 
      try{
      c.writeTo(pipeOut);

      pipeOut.flush();
      pipeOut.close();
    } catch (Exception e){
      crc.pia.Pia.debug(this," problem writing or closing output pipe");
    }
    result.attr("completed");
    return result;
  }
  
}
