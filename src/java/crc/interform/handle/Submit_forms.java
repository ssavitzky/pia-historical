////// submit-forms.java:  Handler for <submit-forms>
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

import crc.ds.TernFunc;
import crc.pia.agent.AgentMachine;

import crc.pia.Agent;
import crc.pia.Headers;
import crc.pia.BadMimeTypeException;
import crc.pia.FileAccess;
import crc.pia.Content;

import java.util.Enumeration;
import crc.util.Utilities;

import w3c.www.mime.MimeType;

import java.io.*;

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
 
  /**
   * Boundary required for multipart form encoding
   */
  //protected final static String multipartBoundary = "PIABound";
  protected final static String multipartBoundary = "----------------------------7841312236133";

  public void handle(Actor ia, SGML it, Interp ii) {
    String name = Util.getString(it, "agent", null);
    SGML itt = containsTimedSubmission(it)? it : null;

    crc.pia.Agent a = Run.getAgent(ii, name);

    if (it.tag().equalsIgnoreCase("form") || it.hasAttr("href") ){
      submit(a, it, itt, ii);
    } else {
      handleContent(a, it, itt, ii);
    }
  }

  /** Submit a form or request using an agent.
   * 	@param a	the agent submitting the form.
   *	@param it	the form to be submitted
   *	@param itt	a token with timed-submission attributes.
   */
  protected void submit(Agent a, SGML it, SGML itt, Interp ii) {
    if ( it.tag().equalsIgnoreCase("form") ){
      String url = it.attrString("action");
      String method = it.attrString("method");
      String encType = it.attrString("encType");
      // Set default encoding type if not specified
      if (encType == null) {
	encType = "application/x-www-form-urlencoded";
      }

      // Initialize content type for submission
      String contentType;
      if (encType.equals("application/x-www-form-urlencoded")) {
	contentType = "application/x-www-form-urlencoded";
      } else if (encType.equals("multipart/form-data")) {
	contentType = "multipart/form-data; boundary="+multipartBoundary;
      } else {
	crc.pia.Pia.debug(this, "Unknown encoding specified in form: "+encType);
	return;
      }

      // Make a machine to handle the request
      AgentMachine m = new AgentMachine(a);
      // Uncomment line below for debugging, it
      // will output to stdout everything returned
      // from the server which hosts the form
      // m.setCallback(new EchoCallback());

      // Create the HTTP request
      if (itt == null) {
	crc.pia.Pia.debug(this,"Making request "+method+" "+url+" "+contentType);
	a.createRequest(m,method, url, formToEncoding(it,encType), contentType);
      } else { 
	a.createTimedRequest(method, url, formToEncoding(it,encType), contentType, itt);
      }

    } else if (it.hasAttr("href")) {

      // Make a machine to handle the request
      AgentMachine m = new AgentMachine(a);

      // Uncomment line below for debugging, it
      // will output to stdout everything returned
      // from the server which hosts the form
      // m.setCallback(new EchoCallback());

      String url = it.attrString("href");
      if (itt == null) 
	a.createRequest(m,"GET", url, null);
      else 
	a.createTimedRequest("GET", url, null, itt);
    }
  }

  /** Look for forms in the content.  Recursively enumerates the content,
   *	processing any forms it finds.  Probably confused by forms that
   *	submit themselves via the <code>submit</code> attribute.
   */
  protected void handleContent(Agent a, SGML it, SGML itt, Interp ii) {
    if ( it.tag().equalsIgnoreCase("form") ){
      submit(a, it, itt, ii);
    } else { 
      Tokens content = it.content();
      if (content != null){
	Enumeration tokens = content.elements();
	while( tokens.hasMoreElements() ){
	  try{
	    SGML e = (SGML)tokens.nextElement();
	    handleContent(a, e, itt, ii);
	  }catch(Exception excep){}
	}
      }
    }
  }

  /** Convert a form to either a query string
   *  or a multipart form encoding
   */

  protected ByteArrayOutputStream formToEncoding(SGML it, String encType) {
    if (encType.equals("application/x-www-form-urlencoded")) {
      return trimQuery(formToQuery(it));
    } else if (encType.equals("multipart/form-data")) {
      return formToMultipart(it);
    } else {
      // WHAT IS CORRECT ACTION HERE?
      crc.pia.Pia.debug(this, "Unknown encoding specified in form");
      return null;
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

      // Marko removed this line, seems to be wrong
      // query = query.toLowerCase();

      query += "=";
      query += java.net.URLEncoder.encode(it.attrString("value"));
      query += "&";		// in case there's a next one.
    } else if ("select".equalsIgnoreCase(it.tag())) {
      // === select unimplemented
    } else if ("textarea".equalsIgnoreCase(it.tag())) {
      // === textarea untested
      query = it.attrString("name");
      if (query == null || "".equals(query)) return "";

      // Marko removed this line, seems to be wrong
      // query = query.toLowerCase();

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


  /** Convert a form to a multipart encoding
   * Currently can send only a single file
   *
   * As specified in RFC1867
   * http://info.internet.isi.edu:80/in-notes/rfc/files/rfc1867.txt
   */
  public ByteArrayOutputStream formToMultipart(SGML it) {

    // Initialize a stream to allow sending bytes directly,
    ByteArrayOutputStream partsByte = new ByteArrayOutputStream();

    // Add data from form into the stream
    partsByte = accumulateFormToMultipart(it, partsByte);

    // Finish off with a multipart boundary
    // (Note the end boundary has an extra -- at the end)
    PrintWriter partsString = new PrintWriter((OutputStream)partsByte);
    partsString.write("\r\n--" + multipartBoundary + "--\r\n");
    partsString.close();

    try {
      partsByte.flush();
    } catch (IOException e) {
      crc.pia.Pia.debug(this,"Failed to flush");
    }

    return partsByte;

  }

  protected ByteArrayOutputStream accumulateFormToMultipart(SGML it,
							    ByteArrayOutputStream partsByte)

    {

      // Initialize a PrintWriter to allow sending strings
      PrintWriter partsString = new PrintWriter((OutputStream)partsByte);
      
      if ("input".equalsIgnoreCase(it.tag())) {

	// Check what kind of input this is
	String inputType = it.attrString("type");
	
	// If the input type is not specified the default
	// assumption is that it is text
	if (inputType == null) {
	  inputType = "text";
	}

	
	// Get the name, value fields
	String name = it.attrString("name");

	// Do not consider unnamed input tags
	if (name == null || "".equals(name)) {
	  // Submit/Reset tags are often unnamed, but this is OK
	  // as we can ignore them anyway in that instance
	  return partsByte;
	}

	String value = it.attrString("value");
	
	// The only type which needs to be treated
	// differently is "file"
	if (inputType.equalsIgnoreCase("file")) {
	  
	  // Stream for reading in files
	  BufferedInputStream B;

	  crc.pia.Pia.debug(this, "Trying to read file "+value);
	    
	  // The filename is in the value field
	  java.io.File UploadFile;
	  try {
	    UploadFile = new java.io.File(value);
	  } catch (NullPointerException e) {
	    // If no file name was supplied, do not write anything
	    crc.pia.Pia.debug(this, "Could not find file name");
	    return partsByte;
	  }
	  if (UploadFile.exists() && UploadFile.canRead()) {

	    // Initialize a stream to read the file
	    try {
	      B = new BufferedInputStream(new FileInputStream(UploadFile));
	    } catch (IOException e) {
	      crc.pia.Pia.debug(this, "Could not open file "+value);
	      return partsByte;
	    }
	  } else {
	    crc.pia.Pia.debug(this, "Could not read file "+value);
	    // Do not write anything
	    return partsByte;
	  }
	  

	  crc.pia.Pia.debug(this,"Read the file "+value);
	  
	  // Try to guess the MIME type
	  // If cannot be determined, used default value of application/octet-stream
	  // as specified in the RFC
	  String MimeType = FileAccess.contentType(value,"application/octet-stream");
	  crc.pia.Pia.debug(this, "Mime type of file set to "+MimeType);
	  
	  // Write out header
	  partsString.write("--" + multipartBoundary + "\r\n");


	  // Transmit the filename if supplied (but not the path, following
	  // Netscape 3)
	  String UploadFileName = UploadFile.getName();

	  String ContentDispositionHeader = "form-data; name=\"" + name + "\"";
	  if (UploadFileName != null) {
	    ContentDispositionHeader += "; filename=\"" + UploadFileName + "\"";
	  }

	  // Write these headers out explicitly as the programmers at PhotoNet
	  // seem to depend on this particular ordering (!!)

	  partsString.write("Content-Disposition: "+ContentDispositionHeader+"\r\n");
	  partsString.write("Content-Type: " + MimeType +"\r\n\r\n");
	  
	  // Then append file
	  if (MimeType.startsWith("text/")) {
	    // as text
	    partsString.write("\n");
	    // TO DO
	    
	    partsString.close();

	  } else {
	    // as binary
	    partsString.close();
	    int b;
	    while (true) {
	      try {
		b = B.read();
		if (b == -1) {
		  break;
		} else {
		  partsByte.write(b);
		}
	      } catch (IOException e) {
		break;
	      }
	    }

	    // Close up the file
	    try {
	      B.close();
	    } catch (IOException e) {
	      crc.pia.Pia.debug(this, "Could not close file "+value);
	      return partsByte;
	    }


	    crc.pia.Pia.debug(this,"Written out binary file");
	  }

	} else {
	 
	  String inputTypeLower = inputType.toLowerCase();
	  if (inputTypeLower.equals("text") ||
	      inputTypeLower.equals("checkbox") ||
	      inputTypeLower.equals("radio") ||
	      inputTypeLower.equals("submit") ||
	      inputTypeLower.equals("reset") ||
	      inputTypeLower.equals("hidden") ||
	      inputTypeLower.equals("password")) {
	      
	    // For the input types listed above, it makes sense
	    // to send the value field as text
	    
	    crc.pia.Pia.debug(this,"Writing out " + inputType + " field: "+name+" "+value);
	    partsString.write("--" + multipartBoundary + "\r\n");
	    String ContentDispositionHeader = "form-data; name=\"" + name + "\"";
	    partsString.write("Content-Disposition: "+ContentDispositionHeader+"\r\n\r\n");
	    partsString.write(value + "\r\n");
	  } else {
	    crc.pia.Pia.debug(this,"Cannot handle INPUT field of type "+inputType);
	    // Write nothing
	  }	    
	  partsString.close();
	}

      } else {
	Tokens content = it.content();
	if (content == null) return partsByte;
	Enumeration tokens = content.elements();
	while (tokens.hasMoreElements()) {
	  try {
	    partsByte = accumulateFormToMultipart((SGML)tokens.nextElement(),
						  partsByte);
	  } catch (Exception e) {
	    crc.pia.Pia.debug(this,"Exception in accumulateFormToMultipart "+e.toString());
	  }
	}
      }
      return partsByte;
    }

  
  /** trim an extraneous &amp; from the end of a query string. */
  protected static ByteArrayOutputStream trimQuery(String query) {
    if (query.endsWith("&")) 
      query = query.substring(0, query.length()-1);
    crc.pia.Pia.debug("Trimmed query "+query);
    return Utilities.StringToByteArrayOutputStream(query);
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

/**
 * Useful for debugging:
 * Simple class to act as a callback for AgentMachine
 * Its only job is to send all output to stdout
 */
class EchoCallback implements TernFunc
{
  public Object execute(Object content, Object transaction, Object agent)
    {
      try {
	((Content)content).writeTo(System.out);
      } catch (Exception e) {
	// Do nothing
      }
      return null;
    }
}

