// Agent.java
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

package crc.pia;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import crc.pia.Transaction;
import crc.pia.Machine;
import crc.pia.Resolver;
import crc.pia.Content;


import crc.ds.Table;
import crc.ds.Criteria;
import crc.ds.Criterion;
import crc.ds.Registered;
import crc.ds.Tabular;

// import crc.sgml.SGML;
// import crc.sgml.Attrs;

import crc.tf.UnknownNameException;

/**
 * An agent is an object which maintains state and context and corresponds
 *	to a URL in the PIA server. 
 *
 * <p> Agents can receive requests directly (http://pia/AGENT_NAME/...);
 *	they can also operate on other transactions.  Direct requests are
 *	handled by the <code>respond</code> methods.  To operate on other
 *	transactions, Agents register with the resolver a set of criteria for
 *	transactions they are interested in.  When the resolver finds a
 *	matching transaction, the agents act_on method is called (and the
 *	agent can modify the transaction.  The agent can completely handle a
 *	transaction by putting itself on the transaction's list of handlers --
 *	which results in a call back to the agents handle method.
 */
public interface Agent extends Tabular {

  /**
   * Default initialization; implementors may override
   */
  public void initialize();

  /**
   * @return name of agent
   */
  public String name();

  /**
   * set name of agent
   */
  public void name(String name);

  /**
   * @return type of agent
   */
  public String type();

  /**
   * set type of agent
   */
  public void type(String type);

  /**
   * @return version
   */
  public String version();

  /**
   * set version
   */
  public void version(String version);

  /************************************************************
  ** operations for working with resolver
  ************************************************************/

  /**
   * Agents maintain a list of feature names and expected values;
   * the features themselves are maintained by a Features object
   * attached to each transaction.
   */
  public Criteria criteria();
  
  /**
   * Agents are associated with a virtual machine which is an
   * interface for actually getting and sending transactionss.  Posts
   * explicitly to an agent get sent to the agent's machine (then to
   * the agent's interform_request method). Other requests can be
   * handled implicitly by the agent.  If one does not exist,
   * create a pia.agent.Machine
   * @return virtual machine
   */
  public Machine machine();

  /**
   * Setting the virtual machine.  
   */
  public void machine( Machine vmachine);

  /**
   * This method is called from the Resolver when an Agent's Criteria list
   * matches a Transaction's Features set.
   */
  public void actOn(Transaction ts, Resolver res);

  /**
   * Handle a transaction matched by an act_on method. 
   * Requests directly _to_ an agent are handled by its Machine;
   * the "handle" method is used only by agents like "cache" that
   * may want to intercept a transaction meant for somewhere else.
   */
  public boolean handle(Transaction ts, Resolver res);

  /**
   * Set options with a hash table
   *
   */
  public void parseOptions(Table hash);

  /************************************************************
  ** interform specific operations
  ** these probably should move to generic agent, since agents
  ** can be based on things other than interforms
  ************************************************************/


  /**
   *  returns a path to a directory that we can write data into.
   *  Creates one if necessary, starting with agent_directory,
   *  then if_root, USR_ROOT/$name, PIA_ROOT/$name, /tmp/$name
   */
  public String agentDirectory();


  /**
   * returns a path to a directory that we can write InterForms into
   * Creates one if necessary, starting with
   * USR_ROOT/Agents/name, PIA_ROOT/Agents/type, /tmp/Agents/name
   */

  public String agentIfDir();

  /**
   * Find an interform, using a simple search path which allows for user
   *	overrides of standard InterForms, and a crude kind of inheritance.  
   */
  public String findInterform( String path );

  /**
   * Find an interform, using an optional suffix search list and adjusting
   *	the directory search path according to whether writing is required.
   */
  public String findInterform( String path, String suffixSearch[], 
			       boolean forWriting );

  /**
   * Respond to a request directed at one of an agent's interforms.
   *
   * @return false if file not found.
   */
  public boolean respondToInterform(Transaction t, Resolver res);


  /**
   * Respond to a request directed at one of an agent's interforms,
   *	with a (possibly-modified) path.
   *
   * @return false if file not found.
   */
  public boolean respondToInterform(Transaction t, String path, Resolver res);


  /**
   * Respond to a request directed at an agent.
   * The InterForm's url may be passed separately, since the agent may
   * need to modify the URL in the request.  It can pass either a full
   * URL or a path.
   */
  public void respond(Transaction trans, Resolver res) throws PiaRuntimeException;

  /**
   * Given a url string and content create a request transaction.
   *       The results are discarded.
   *	@param method (typically "GET", "PUT", or "POST").
   *	@param url the destination URL.
   *	@param queryString (optional) -- content for a POST request.
   *	@param contentType (optional) -- content type for a POST request.
   */
  public void createRequest(String method, String url,
			    String queryString, String contentType);

  /**
   * Given a url string and content create a request transaction.
   *	@param m the Machine to which the response is to be sent.
   *	@param method (typically "GET", "PUT", or "POST").
   *	@param url the destination URL.
   *	@param content content object for a POST or PUT request.
   *	@param contentType (optional) content type for a POST or PUT request.
   */
  public void createRequest(Machine m, String method, String url,
			    InputContent content, String contentType);

  /**
   * Given a url string and content create a request transaction.
   *	@param m the Machine to which the response is to be sent.
   *	@param method (typically "GET", "PUT", or "POST").
   *	@param url the destination URL.
   *	@param queryString (optional) -- content for a POST request.
   *	@param contentType (optional) -- content type for a POST request.
   */
  public void createRequest(Machine m, String method, String url,
			    String queryString, String contentType);

  /**
   * Given a url string and content create a request transaction.
   *       The results are discarded.
   *	@param method (typically "GET", "PUT", or "POST").
   *	@param url the destination URL.
   *	@param queryString (optional) -- content for a POST request.
   *	@param contentType (optional) -- content type for a POST request.
   *	@param times a Tabular containing the timing information
   */
  public void createTimedRequest(String method, String url, String queryString,
				 String contentType, Tabular times);


  /** 
   * Handle timed requests.
   */
  public void handleTimedRequests(long time);

  /** 
   * Send an error message that includes the agent's name and type.
   */
  public void sendErrorResponse( Transaction req, int code, String msg );

  /**
   * Send error message for not found interform file
   */
  public void respondNotFound( Transaction req, URL url);

  /**
   * Send error message for not found interform file
   */
  public void respondNotFound( Transaction req, String path);

  /**
   * Respond to a transaction with a stream of HTML.
   */
  public void sendStreamResponse ( Transaction trans, InputStream in );

  /************************************************************
  ** interface to content objects
  ************************************************************/
  /**
   * Agents can register interest in content objects using
   *	<code>content.notifyWhen</code>.  Content objects call the agent 
   *	back using <code>updateContent</code>.
   *
   * @param object: arbitrary object specified by agent in original
   *                notifyWhen call
   */
   public void updateContent(Content content, String state, Object object);

}










