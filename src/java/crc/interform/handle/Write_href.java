////// Write_href.java:  Handler for <write.href>
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
import java.net.URL;

import crc.interform.Run;

import crc.content.text.StringContent;
import crc.pia.HTTPRequest;


/** Handler class for &lt;write.href&gt tag. 
 * <dl>
 * <dt>Syntax:<dd>
 *	&lt;write.href href="url" [post] [base="path"] [trim] [line]
 *	       [copy [protect [markup]]] &gt;content&lt;/write.href&gt;
 * <dt>Dscr:<dd>
 *	Output CONTENT to HREF, with optional BASE path. 
 *	Optionally POST.  Optionally TRIM
 *	leading and trailing whitespace. Optionally end LINE.
 *	Optionally COPY content to InterForm.
 *      Should provide  options for handling results codes.. currently does not
 *  </dl>
 */
public class Write_href extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<write.href href=\"url\" [method=post] [base=\"path\"] [trim] \n" +
    "[copy] >content</write.href>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Output CONTENT to HREF, with optional BASE path. \n" +
    "Optionally POST.  Optionally TRIM\n" +
    "leading and trailing whitespace. Optionally end LINE.\n" +
    "Optionally COPY content to InterForm.\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String href = it.attrString("href");
    if (href == null || "".equals(href)) {
      ii.error(ia, "must have non-null href attribute");
      return;
    }
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
      method = "PUT";
    }


    /**  get the agent
     */
    Run env = Run.environment(ii);
    Agent agent =env.agent;
    AgentMachine m = new AgentMachine(agent);

    String initString = "HTTP/1.0 "+ method +" "+ href;

    String s = (it.hasAttr("trim")) ? crc.sgml.Util.trimSpaces(it.content()).toString() : it.content().toString();
    StringContent c = new StringContent(s);
    // create a request and go get it    
    HTTPRequest  request = new HTTPRequest(m,c,false);
    request.protocolInitializationString = initString;
    request.setHeader("Version", agent.version());
    request.setContentType( "text/html" );
    request.setContentLength(s.length() );

    request.setMethod( method );
    request.setRequestURL( href );

    request.startThread();

    if (it.hasAttr("copy")) {
      ii.replaceIt(it.content());
    } else {
      ii.deleteIt();
    }
  }
}
