// Crontab.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.pia;

/** Registry for timed operations.
 *	The Crontab gets its name from the Unix <code>crontab</code> table,
 *	and has similar capabilities.  Entries are made in the Crontab
 *	by the &lt;submit-forms&gt; actor.<p>
 *
 *  A Crontab is associated with each Agent that needs one.  That means that
 *	an Agent's Crontab will be checkpointed along with it.<p>
 */

import crc.pia.Transaction;
import crc.pia.Agent;
import crc.pia.Resolver;

import crc.ds.Registered;

import crc.sgml.SGML;
import crc.sgml.Element;
import crc.sgml.Attrs;

import java.io.Serializable;

public class Crontab extends Element implements Serializable {

  /** The last time the crontab was run, as given by
   *	System.currentTimeMillis().
   */ 
  public long lastTime = 0;

  /************************************************************************
  ** Registry:
  ************************************************************************/


  public void addRequest(CrontabEntry entry) {
    addItem(entry);
  }

  /** Remove the earliest entry that matches the one given. */
  public void removeRequest(CrontabEntry entry) {

  }

  /************************************************************************
  ** Creating and Submitting Requests:
  ************************************************************************/

  /**
   * Given a url string, content, and timing information, create a
   *	Crontab entry.
   *
   *	@param agent the Agent submitting the request.
   *	@param method (typically "GET", "PUT", or "POST").
   *	@param url the destination URL.
   *	@param queryString (optional) -- content for a POST request.
   *	@param itt an SGML object, normally an Element, with attributes
   *		that contain the timing information.
   *
   *	@see crc.pia.CrontabEntry
   */
  public void makeEntry(Agent agent, String method, String url,
			String queryString, SGML itt) {
    addRequest(new CrontabEntry(agent, method, url, queryString, itt));
  }

  /**
   * Make any requests that have come due since the last time.
   *	Each request is only submitted once, no matter how many times
   *	it may have matched the current time (for example, requests to
   *	be submitted every minute will run at most once when the PIA comes
   *	up after being down for an hour).  It is possible that requests
   *	that come due while the PIA is down will not get run at all. <p>
   *
   *	The repeat count of each request is decremented; any request that
   *	``expires'' with a repeat count of zero is removed.<p>
   */
  public void handleRequests(Agent agent, long time) {
    long previousTime = lastTime;
    lastTime = time;

    /* Loop through the items.  Start at the end and work down to prevent
     *	getting confused when an expired entry is removed. */

    for (int i = nItems(); --i >= 0; ) {
      CrontabEntry entry = (CrontabEntry)itemAt(i);
      if (entry.handleRequest(previousTime, lastTime)
	  && entry.expired()) content().remove(entry);
    }
  }


  /************************************************************************
  ** Construction:
  ************************************************************************/

  public Crontab() {
    super("crontab");		// crontab tag.
  }

}
