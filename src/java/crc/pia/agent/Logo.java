// Logo.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

/**
 * This is the class for the ``Logo'' agent, which synthesizes scaled images
 *	in the PIA's specialized logo font. <p>
 *
 *	For the moment, we are likely to punt and transfer control to a
 *	slightly-simplified version of the original PERL code.
 */

package crc.pia.agent;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.DataInputStream;
import java.io.StringBufferInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.IOException;

import java.net.URL;
import java.net.MalformedURLException;

import crc.pia.PiaRuntimeException;
import crc.pia.GenericAgent;
import crc.pia.FormContent;
import crc.pia.Resolver;
import crc.pia.Agent;
import crc.pia.Pia;
import crc.pia.Transaction;
import crc.pia.Machine;
import crc.pia.HTTPResponse;
import crc.pia.Content;
import crc.pia.ByteStreamContent;
import crc.pia.FileAccess;

import crc.util.Utilities;

import w3c.www.http.HTTP;

public class Logo extends GenericAgent {
  /**
   * Respond to a request. 
   * 	Figure out whether it's for an image or an interform.
   */
  public void respond(Transaction request, Resolver res)
       throws PiaRuntimeException{
    Transaction reply = null;
    String replyString = null;
    Pia.debug(this, "Inside Logo respond...");

    if( !request.isRequest() ) return;
    URL url = request.requestURL();
    if( url == null ) return;

    /* First try an InterForm.  If one exists, we're done. */
    if (respondToInterform( request, res ) ) return;

    String path   = url.getFile();

    if (true) {
      /* === extreme hack!! call a PERL program! === */
      String cgi = findInterform("Logo.cgi");
      execProgram(request, "perl " + cgi+" -cgi "+path);
      
    } else {
      /* === EVEN MORE extreme hack!! Redirect to PERL PIA!!! === */

      String redirUrlString = "http://gelion:8001"+path;
      String msg = "Extreme kludge: see "+redirUrlString;
      Content ct = new ByteStreamContent( new StringBufferInputStream(msg));
      Transaction response = new HTTPResponse( Pia.instance().thisMachine(),
					       request.fromMachine(),
					       ct, false);
      response.setHeader("Location", redirUrlString);
      response.setStatus(HTTP.MOVED_PERMANENTLY);
      response.setContentLength( msg.length() );
      response.startThread();
    }
  }
    

  /************************************************************************
  ** Construction:
  ************************************************************************/

  /**
   * name and type needs to be set after this
   */
  public Logo(){
    super();
  }

  private void execProgram(Transaction request, String cmd) { 
    Runtime rt = Runtime.getRuntime();
    Process process = null;
    InputStream in;
    PrintStream out;
    String cmdArray[] = {"/bin/sh", "-c", cmd};


    try{
      process = rt.exec( cmdArray );
      in = process.getInputStream();
      Transaction response = new HTTPResponse( request, new Machine(in));
      
    }catch(Exception ee){
      String msg = "can not exec :"+cmd;
      throw new PiaRuntimeException (this, "execProgram", msg) ;
    }
  }

}






















































