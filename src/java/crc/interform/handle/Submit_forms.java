////// Submit-forms.java:  Handler for <submit-forms>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.Util;
import crc.interform.Run;

import crc.sgml.SGML;
import crc.sgml.Tokens;
import crc.sgml.Text;
import crc.sgml.Element;

import crc.pia.Agent;
import java.util.Enumeration;

/** Handler class for &lt;submit-forms&gt tag 
 *  <p> See <a href="../../InterForm/tag_man.html#submit-forms">Manual
 *	Entry</a> for syntax and description.
 */
public class Submit_forms extends crc.interform.Handler {
  public String syntax() { return syntaxStr; }
  static String syntaxStr=
    "<submit-forms [hour=hh] [minute=mm] [day=dd]\n" +
    "[month=[\"name\"|mm]] [weekday=[\"name\"|n]]\n" +
    "[repeat=count] [until=mm-dd-hh]>\n" +
    "<a href=\"query\">...</a>|...form...</submit-forms>\n" +
"";
  public String dscr() { return dscrStr; }
  static String dscrStr=
    "Submit a form or link ELEMENT or every form (not links) in CONTENT.  \n" +
    "Optionally submit at HOUR, MINUTE, DAY, MONTH, WEEKDAY. \n" +
    "Optionally REPEAT=N times (missing hour, day, month, weekday \n" +
    "are wildcards).  \n" +
    "Optionally UNTIL=MM-DD-HH time when submissions are halted.\n" +
    "Use options interform of agent to delete repeating entries.\n" +
"";
  public String note() { return noteStr; }
  static String noteStr=
    "The following InterForm code makes <form> active:\n" +
    "	<actor name=form handle=\"submit_forms\"></actor>\n" +
"";
 
  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "agent", null);
    SGML itt = containsTimedSubmission(it)? it : null;

    Run env = Run.environment(ii);
    crc.pia.Agent a = (name == null)? env.agent : env.getAgent(name);

    if (it.tag().equalsIgnoreCase("form") || it.hasAttr("href") ){
      submit(a, it, itt);
    } else {
      handleContent(a, it, itt);
    }
  }

  /** Submit a form or request using an agent.
   * 	@param a	the agent submitting the form.
   *	@param it	the form to be submitted
   *	@param itt	a token with timed-submission attributes.
   */
  protected void submit(Agent a, SGML it, SGML itt) {
    if ( it.tag().equalsIgnoreCase("form") ){
      String url = it.attrString("action");
      String method = it.attrString("method");
      if (itt == null) 
	a.createRequest(method, url, trimQuery(formToQuery(it)));
      else 
	a.createTimedRequest(method, url, trimQuery(formToQuery(it)), itt);
    } else if (it.hasAttr("href")) {
      String url = it.attrString("href");
      if (itt == null) 
	a.createRequest("GET", url, null);
      else 
	a.createTimedRequest("GET", url, null, itt);
    }
  }

  /** Look for forms in the content.  Recursively enumerates the content,
   *	processing any forms it finds.  Probably confused by forms that
   *	submit themselves via the <code>submit</code> attribute.
   */
  protected void handleContent(Agent a, SGML it, SGML itt) {
    if ( it.tag().equalsIgnoreCase("form") ){
      submit(a, it, itt);
    } else { 
      Tokens content = it.content();
      if (content != null){
	Enumeration tokens = content.elements();
	while( tokens.hasMoreElements() ){
	  try{
	    SGML e = (SGML)tokens.nextElement();
	    handleContent(a, e, itt);
	  }catch(Exception excep){}
	}
      }
    }
  }

  /** Convert a form to a query string.
   */
  public String formToQuery(SGML it) {
    String query = "";

    if ("input".equalsIgnoreCase(it.tag())) {
      // generate query string for input
      query = it.attrString("name");
      if (query == null || "".equals(query)) return "";
      query = query.toLowerCase();
      query += "=";
      query += java.net.URLEncoder.encode(it.attrString("value"));
      query += "&";		// in case there's a next one.
    } else if ("select".equalsIgnoreCase(it.tag())) {
      // === select unimplemented
    } else if ("textarea".equalsIgnoreCase(it.tag())) {
      // === textarea untested
      query = it.attrString("name");
      if (query == null || "".equals(query)) return "";
      query = query.toLowerCase();
      query += "=";
      query += java.net.URLEncoder.encode(it.contentString());
      query += "&";		// in case there's a next one.
    } else {
      Tokens content = it.content();
      if (content == null) return query;
      Enumeration tokens = content.elements();
      while (tokens.hasMoreElements()) {
	try {
	  query += formToQuery((SGML)tokens.nextElement());
	} catch (Exception e) {}
      }
    }
    return query;
  }

  /** trim an extraneous &amp; from the end of a query string. */
  protected static String trimQuery(String query) {
    if (query.endsWith("&")) 
      query = query.substring(0, query.length()-1);
    return query;
  }

  /** Attributes that determine a timed submission */
  protected static String timeAttrs[] = {
    "repeat", "until", "hour", "minute", "day", "month", "weekday" 
  };

  /** Determine whether a form is timed. */
  protected static boolean containsTimedSubmission(SGML it) {
    for (int i = 0; i < timeAttrs.length; ++i) {
      if (it.hasAttr(timeAttrs[i])) return true;
    }
    return false;
  } 
}





