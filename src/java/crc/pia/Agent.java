// Agent.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.pia;
import java.util.Hashtable;
import java.util.Vector;
import crc.pia.Transaction;
import crc.pia.Machine;
import crc.pia.Resolver;
import crc.pia.Content;
import java.net.URL;

import crc.ds.Features;
import crc.tf.UnknownNameException;
public interface Agent {
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
   * the features themselves are maintained by a FEATURES object
   * attached to each transaction.
   */
  public Vector criteria();

  /**
   * Set a match criterion.
   * feature is string naming a feature
   * value is 0,1 (exact match--for don't care, omit the feature)
   * code is a functor object takes transaction as argument returns Boolean
   * @return criteria table
   */
  public Vector matchCriterion(String feature, Object value);
  
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
  public void parseOptions(Hashtable hash);


  /**
   * Respond to a request directed at one of an agent's interforms.
   * The InterForm's url may be passed separately, since the agent may
   * need to modify the URL in the request.  It can pass a URL.
   */
  public String respondToInterform(Transaction t, URL path, Resolver res);



  /**
   * Respond to a request directed at one of an agent's interforms.
   * The InterForm's url may be passed separately, since the agent may
   * need to modify the URL in the request.  It can pass either a full
   * URL or a path.
   */
  public String respondToInterformPut();

  public void respond(Transaction trans, Resolver res) throws PiaRuntimeException;

  public Object computeFeature(String featureName) throws UnknownNameException;

  public void setFeatures( Features feature );
}








