// CrontabEntry.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.pia;

/** Crontab table entry for timed operations.
 *	The Crontab gets its name from the Unix <code>crontab</code> table,
 *	and has similar capabilities.
 */

import crc.pia.Transaction;
import crc.pia.Agent;
import crc.pia.Resolver;

import crc.ds.Registered;

import crc.sgml.SGML;
import crc.sgml.Attrs;

import java.io.Serializable;

public class CrontabEntry implements Serializable {

  /************************************************************************
  ** Request Data:
  ************************************************************************/

  /** The agent making the request. */
  Agent agent;

  /** The request method. */
  String method;

  /** The request URL. */
  String url;

  /** The request content (for POST or PUT). */
  String content;

  /** The minute at which to make the request.  Wildcard if negative. */
  int minute = -1;

  /** The hour at which to make the request.  Wildcard if negative. */
  int hour = -1;

  /** The day at which to make the request.  Wildcard if negative. */
  int day = -1;

  /** The month in which to make the request.  Wildcard if negative. */
  int month = -1;

  /** The year in which to make the request.  Wildcard if negative. */
  int year = -1;

  /** The day of the week (Sunday = 0) at which to make the request.  
   *  Wildcard if negative. */
  int weekday = -1;

  /** The number of times to repeat the request (infinite if negative). */
  int repeat = 1;

  /** The hour at which to stop. */
  int untilHour = -1;

  /** The day of the month on which to stop. */
  int untilDay = -1;

  /** The month in which to stop. */
  int untilMonth = -1;


  /************************************************************************
  ** Creating and Submitting Requests:
  ************************************************************************/

  public CrontabEntry() {}

  /**
   * Construct a Crontab entry.
   *	@param agent the Agent submitting the request.
   *	@param method (typically "GET", "PUT", or "POST").
   *	@param url the destination URL.
   *	@param queryString (optional) -- content for a POST request.
   *	@param itt an SGML object, normally an Element, with attributes
   *		that contain the timing information.
   */
  public CrontabEntry(Agent agent, String method, String url,
		      String queryString, SGML itt) {
    this();			// initialize the times to wildcards

    this.agent 	 = agent;
    this.method  = method;
    this.url 	 = url;
    this.content = queryString;

    hour 	= entry(itt, "hour");
    minute 	= entry(itt, "minute");
    day		= entry(itt, "day");
    month	= entry(itt, "month");
    year	= entry(itt, "year");
    weekday	= entry(itt, "weekday");

    /* Even if the year or weekday is a wildcard, don't repeat unless a
     *	"repeat" attribute is explicitly present.
     */
    if (itt.hasAttr("repeat")) {
      repeat = entry(itt, "repeat");
    } else if (hour >= 0 && minute >= 0 && day >= 0 && month >= 0) {
      repeat = 1;
    } else {
      repeat = -1;
    }

    // === until not supported yet ===
  }


  private int entry(SGML itt, String attr) {
    if (itt.hasAttr(attr)) {
      if (itt.attr(attr) == crc.sgml.Token.empty) return -1;
      else return (int)itt.attr(attr).toText().intValue();
    } else {
      return -1;
    }
  }

  private int weekDayEntry(SGML itt, String attr) {
    String s = itt.attrString(attr);

    if (s == null || "".equals(s)) return -1;
    if (s.charAt(0) >= '0' && s.charAt(0) <= '9') {
      return (int)itt.attr(attr).toText().intValue();
    }
    return 0;    // === day name lookup not supported ===
  }


  /** Submit the timed request.
   */
  public void submitRequest() {
    agent.createRequest(method, url, content);
  }
}
