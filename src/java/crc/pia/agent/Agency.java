// Agency.java
// $Id$

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/


/**
 * This is the class for the ``Agency'' agent; i.e. the one that
 *	handles requests directed at agents.  It contains the specialized
 *	code that installs agents, and an <code>actOn</code> method that
 *	determines which agent should handle a request.
 */

package crc.pia.agent;
import java.io.FileInputStream;
import java.io.InputStream;

import java.util.Enumeration;

import java.net.URL;

import crc.ds.Table;
import crc.ds.List;
import crc.ds.Criterion;

import crc.pia.GenericAgent;
import crc.pia.Resolver;
import crc.pia.Agent;
import crc.pia.Pia;
import crc.pia.Transaction;
import crc.pia.Machine;
import crc.pia.HTTPRequest;

public class Agency extends Admin {

  /**
   * Act on a transaction that we have matched. 
   *
   * <p> Since the Agency matches all requests to agents, this means
   * 	 that we need to find the agent that should handle this request
   * 	 and push it onto the transaction.
   */
  public void actOn(Transaction trans, Resolver res){
    boolean isAgentRequest = trans.test("IsAgentRequest");
    if (!isAgentRequest) return; // sanity check -- it's an agent request.

    URL url = trans.requestURL();
    if (url == null) return;	 // sanity check -- there's a URL
    
    String path = url.getFile();
    Pia.debug(this, "actOn..." + path);

    // Ask the resolver for the correct agent.  
    Agent agent = res.agentFromPath(path);

    if (agent != null) {
      Pia.debug(this, "Agent found: " + agent.name());
      trans.toMachine( agent.machine() );
    } else if (isValidRootPath(path)) {
      Pia.debug(this, "Root path redirected to " + name());
      trans.toMachine(this.machine()); 
    } else {
      Pia.debug(this, "Agent not found");
    }
  }

  /** The directory (under Agency) in which we keep root files. */
  protected String rootPrefix = "ROOT";

  /** Test path to see whether it is a valid root path. */
  protected boolean isValidRootPath(String path) {
    // First see if it's a possibility.
    if (!isPossibleRootPath(path)) return false;
    return null != findInterform(rewriteRootPath(path));
  }

  /** Test path to see whether it is a possible root path. 
   *	This is indicated by the absence of a leading <code>/Agency</code>.
   *	Note that by this point we have already established (in Agency's
   *	<code>actOn</code> method) that either Agency or no valid agent
   *	owns the path.
   */
  protected boolean isPossibleRootPath(String path) {
    // We've already checked for an agent at this point, so we know that if
    // it starts with Agency it's not a root path.
    return !path.startsWith("/" + name());
  }

  /** Rewrite a root path.  
   *	Correctly handle the case where a legacy <code>ROOTindex.if</code>
   *	exists in the user's <code>.pia/Agents/Agency</code> directory.
   *	Otherwise, rewrite to <code>Agency/ROOT/<em>path</em></code>.
   */
  protected String rewriteRootPath(String path) {
    if (path.equals("/")) {
      // root index directory -- might be ROOTindex
      if (findInterform("ROOTindex") != null) return "/ROOTindex";
      return "/" + rootPrefix + "/index";
    } else {
      return "/" + rootPrefix + path;
    }
  }

  /** Perform any necessary rewriting on the given path. */
  protected String rewriteInterformPath(Transaction request, String path) {
    if (isPossibleRootPath(path)) {
      return rewriteRootPath(path);
    } else {
      return path;
    }
  }

  /**
   * Constructor.
   */
  public Agency(String name, String type){
    super(name, type);
  }

  /** Default constructor. */
  public Agency() {
    super();
  }

  /**
   * initialize 
   */
  public void initialize() {
    if (initialized) return;
    put("criteria", "Request Agent_request");
    //criteria().push(Criterion.toMatch("IsRequest", true));
    //criteria().push(Criterion.toMatch("IsAgentRequest", true));
    super.initialize();
  }

}





















