// Agent.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.pia;

import crc.pia.Transaction;
import crc.pia.Machine;
import crc.pia.Resolver;
import crc.pia.Content;
import java.net.URL;


import crc.ds.Features;
import crc.ds.Table;
import crc.ds.Criteria;
import crc.ds.Criterion;

import crc.sgml.SGML;
import crc.sgml.Attrs;

import crc.tf.UnknownNameException;

public interface Agent extends Attrs {
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

  /**
   *  returns a directory that we can write data into.
   *  creates one if necessary, starts with agent_directory,
   *  then if_root, USR_ROOT/$name, PIA_ROOT/$name, /tmp/$name
   */
  public String agentDirectory();

  /**
   * returns a directory that we can write InterForms into
   * creates one if necessary, starts with if_root, then
   * USR_ROOT/$name, PIA_ROOT/$name, /tmp/$name
   */
  //public StringBuffer agentIfRoot();


  /**
   * returns the base url (as string) for this agent
   * optional path argument just for convenience--
   * returns full url for accessing that file
   */
  //public StringBuffer agentUrl();

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
   * Options are strings stored in attributes.  Options may have
   * corresponding features derived from them, which we compute on demand.
   */
  public void option(String key, String value) throws NullPointerException;

  /**
   * Return an option's value 
   *
   */
  public String optionAsString(String key);

  /**
   * Return an option's value 
   *
   */
  public boolean optionAsBoolean(String key);

  /**
   * Set options with a hash table
   *
   */
  public void parseOptions(Table hash);

  /**
   * Find an interform, using a simple search path and a crude kind
   * of inheritance.  Allow for the fact that the user may be trying
   * to override the interform by putting it in piaUsrAgentsStr/name/.
   */
  public String findInterform( URL url, boolean noDefault );

  /**
   * Find an interform starting with a string pathname.
   */
  public String findInterform( String path, boolean noDefault );

  /**
   * Respond to a request directed at one of an agent's interforms.
   * The InterForm's url may be passed separately, since the agent may
   * need to modify the URL in the request.
   *
   * @return false if file not found.
   */
  public boolean respondToInterform(Transaction t, URL path, Resolver res);


  /**
   * Respond to a request directed at an agent.
   * The InterForm's url may be passed separately, since the agent may
   * need to modify the URL in the request.  It can pass either a full
   * URL or a path.
   */
  public void respond(Transaction trans, Resolver res) throws PiaRuntimeException;

  public Object computeFeature(String featureName) throws UnknownNameException;

  /*
   * Given a url string and content create a request transaction
   * Note: content is used for POST request.
   *       Also, the from machine is default to the agent's machine
   */
  public void createRequest(String method, String url, String queryString);

  /*
   * Given a url string and content create a request transaction
   * Note: content is used for POST request.
   *       User must specify from machine.
   */
  public void createRequest(Machine m, String method, String url, String queryString);
}










