// Crontab.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.pia;

/** Registry for timed operations.
 *	The Crontab gets its name from the Unix <code>crontab</code> table,
 *	and has similar capabilities.
 *
 *	=== Crontab and CrontabEntry should descend from Element for 
 *	=== easy printability.
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

  /************************************************************************
  ** Registry:
  ************************************************************************/


  public void addRequest(CrontabEntry entry) {

  }

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
   *	be submitted every minute will only run once when the PIA comes
   *	up after being down for an hour).
   */
  public void handleRequests(Agent agent) {
    
  }
}
