// Agent.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.pia;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;

import crc.pia.Transaction;
import crc.pia.Machine;
import crc.pia.Resolver;
import crc.pia.Content;


import crc.ds.Table;
import crc.ds.Criteria;
import crc.ds.Criterion;
import crc.ds.Registered;

import crc.sgml.SGML;
import crc.sgml.Attrs;

import crc.tf.UnknownNameException;

/**
 * An agent is an object which maintains state and context  (which is
 * why agents conform to the Attrs interface). <p>
 *
 * Agents can receive requests directly (http://Agency/AGENT_NAME/...), 
 * they can also operate on other transactions.  Direct requests are handled
 * by the respondToInterform methods.  To operate on other transactions,
 * Agents register with the resolver a set of
 * criteria for transactions they are interested in.  When the resolver
 * finds a matching transaction, the agents act_on method is called (and
 * the agent can modify the transaction.  The agent can completely
 * handle a transaction by putting itself on the transaction's list of
 * handlers --  which results in a call back to the agents handle method.
 */
public interface Agent extends Attrs, Registered {

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
   * Add a match criterion to our list of criteria;
   */
  public void matchCriterion(Criterion c);

  /**
   * Set a match criterion from a "name=value" string.
   */
  public void matchCriterion(String match);
  
  /**
   * Set a match criterion that exactly matches a given value.
   */
  public void matchCriterion(String feature, Object value);
  
  /**
   * Set a boolean match criterion.
   */
  public void matchCriterion(String feature, boolean test);
  
  /**
   * agents are associated with a virtual machine which is an
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
   *  They can also be handled by code or InterForm hooks.  
   *
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
   * returns the base url (as string) for this agent
   * optional path argument just for convenience--
   * returns full url for accessing that file
   */
  //public StringBuffer agentUrl();

  /**
   * Find an interform, using a simple search path which allows for user
   *	overrides of standard InterForms, and a crude kind of inheritance.  
   */
  public String findInterform( String path );

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
   */
  public void createRequest(String method, String url, String queryString);

  /**
   * Given a url string and content create a request transaction.
   *	@param m the Machine to which the response is to be sent.
   *	@param method (typically "GET", "PUT", or "POST").
   *	@param url the destination URL.
   *	@param queryString (optional) -- content for a POST request.
   */
  public void createRequest(Machine m, String method, String url,
			    String queryString);

  /**
   * Given a url string and content create a request transaction.
   *       The results are discarded.
   *	@param method (typically "GET", "PUT", or "POST").
   *	@param url the destination URL.
   *	@param queryStream (optional) -- content for a POST request.
   *    @param contentType MIME type of content
   */
  public void createRequest(String method, String url, 
			    ByteArrayOutputStream queryStream,
			    String contentType);

  /**
   * Given a url string and content create a request transaction.
   *	@param m the Machine to which the response is to be sent.
   *	@param method (typically "GET", "PUT", or "POST").
   *	@param url the destination URL.
   *	@param queryStream (optional) -- content for a POST request.
   *    @param contentType MIME type of content
   */
  public void createRequest(Machine m, String method, String url,
			    ByteArrayOutputStream queryStream,
			    String contentType);

  /**
   * Given a url string and content create a request transaction.
   *       The results are discarded.
   *	@param method (typically "GET", "PUT", or "POST").
   *	@param url the destination URL.
   *	@param queryString (optional) -- content for a POST request.
   *	@param itt an SGML object, normally an Element, with attributes
   *		that contain the timing information.
   */
  public void createTimedRequest(String method, String url,
				 String queryString, SGML itt);

  /**
   * Given a url string and content create a request transaction.
   *       The results are discarded.
   *	@param method (typically "GET", "PUT", or "POST").
   *	@param url the destination URL.
   *	@param queryStream (optional) -- content for a POST request.
   *    @param contentType MIME type of content
   *	@param itt an SGML object, normally an Element, with attributes
   *		that contain the timing information.
   */
  public void createTimedRequest(String method, String url,
				 ByteArrayOutputStream queryStream,
				 String contentType,
				 SGML itt);

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
   *  agents can register interest in content objects
   * (content.notifyWhen).  Content objects call the agent back using
   * contentUpdate.
   * @param object: arbitrary object specified by agent in original
   *                notifyWhen call
   */
   public void updateContent(Content content, String state, Object object);

}










