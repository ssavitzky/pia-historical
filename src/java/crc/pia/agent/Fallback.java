// Fallback.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.

/**
 * This is the class used for an Agency if we cannot find the Agency's
 *	InterForm files.  It can also be used as an alternative Agency in 
 *	very small appliances.  Use <code>./stringify.pl</code> to convert 
 *	web pages to string constants.
 */

package crc.pia.agent;

import java.io.InputStream;
import java.util.Enumeration;

import java.net.URL;

import crc.ds.Table;
import crc.ds.List;
import crc.pia.GenericAgent;
import crc.pia.Resolver;
import crc.pia.Agent;
import crc.pia.Pia;
import crc.pia.Transaction;
import crc.pia.Machine;
import crc.pia.HTTPRequest;
import crc.pia.agent.Agency;

import crc.interform.Run;

public class Fallback extends Agency {
  /**
   * Constructor.
   */
  public Fallback(String name, String type){
    super(name, type);
  }

  /** Default constructor. */
  public Fallback() {
    super();
  }

  public void respond(Transaction request, Resolver res)
       throws crc.pia.PiaRuntimeException{
    if (respondToInterform(request, res)) return;

    /* No InterForm found.  See if this a known fallback form. */

    URL url = request.requestURL();
    if( url == null )
      return;
    String path   = url.getFile();
    String myname = name();
    String mytype = type();
    Agent agnt	  = this;

    if (! path.startsWith("/"+myname+"/")) {
      if (!respondToInterform(request, res))
	respondNotFound(request, url);
      return;
    }

    path = path.substring(("/"+myname+"/").length());

    String page = null;

    if (path.equals("ROOTindex.if")) page = rootPage;
    else if (path.equals("home.if")) page = homePage;
    else if (path.equals("install_agent.if")) page = installPage;

    if (page == null) {
      respondNotFound(request, url);
    } else {
      InputStream in =  Run.interformString(this, path, page, request, res);
      sendStreamResponse(request, in);
    }
  }

  public String rootPage =
       "<h1>Fallback Agency Root Page</h1>";
  public String homePage =
       "<h1>Empty Home Page</h1>";
  public String installPage =
       "<install-agent>&urlQuery;</install-agent> installed."; 
}





















