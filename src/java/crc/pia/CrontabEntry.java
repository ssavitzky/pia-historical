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
import crc.ds.List;
import crc.ds.Tabular;

// import crc.sgml.SGML;
// import crc.sgml.Element;
// import crc.sgml.Attrs;
// import crc.sgml.Util;

import crc.util.Utilities;

import java.io.Serializable;
import java.io.OutputStream;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.DateFormat;

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

  /** The request content type (for POST or PUT). */
  String contentType;

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

  public CrontabEntry() {  }

  /**
   * Construct a Crontab entry.
   *	@param agent the Agent submitting the request.
   *	@param method (typically "GET", "PUT", or "POST").
   *	@param url the destination URL.
   *	@param queryString (optional) -- content for a POST request.
   *    @param contentType MIME type for the request content.
   *	@param times a Tabular that contains the timing information.
   */
  public CrontabEntry(Agent agent, String method, String url,
		      String queryString, Tabular times) {
    // Convert query string to byte array
    // Assume default content type
    this(agent, method, url, queryString,
	 "application/x-www-urlencoded", times);
  }

  
  /**
   * Construct a Crontab entry.
   *	@param agent the Agent submitting the request.
   *	@param method (typically "GET", "PUT", or "POST").
   *	@param url the destination URL.
   *	@param queryString (optional) -- content for a POST request.
   *	@param times  a Tabular containing the timing information
   */
  public CrontabEntry(Agent agent, String method, String url,
		      String queryString,
		      String contentType, Tabular itt) {
    this();			// initialize the times to wildcards
    
    this.agent 	 = agent;
    this.method  = method;
    this.url 	 = url;
    this.content = queryString;
    this.contentType = contentType;

    hour 	= entry(itt, "hour");
    minute 	= entry(itt, "minute");
    day		= entry(itt, "day");
    month	= entry(itt, "month");
    year	= entry(itt, "year");
    weekday	= entry(itt, "weekday");

    /* Even if the year or weekday is a wildcard, don't repeat unless a
     *	"repeat" attribute is explicitly present.
     */
    if (itt.get("repeat") != null) {
      repeat = entry(itt, "repeat");
    } else if (hour >= 0 && minute >= 0 && day >= 0 && month >= 0) {
      repeat = 1;
    } else {
      repeat = -1;
    }

    if (itt.get("until") != null) {
      List until = List.split(itt.get("until").toString(), "-", false);
      untilMonth = untilEntry(until, 0);
      untilDay   = untilEntry(until, 1);
      untilHour  = untilEntry(until, 2);
    }
  }

  /** Convert a list item to an ``until'' value.  Null items, missing items,
   *	and asterisks are converted to -1, the wildcard value. */
  private int untilEntry(List until, int index) {
    String s = (until.nItems() <= index)? "*" : until.at(index).toString();
    if ("".equals(s) || "*".equals(s)) return -1;
    else try {
      return Integer.valueOf(s).intValue();
    } catch (Exception e) {
      return -1;
    }
  }

  private int entry(Tabular itt, String attr) {
    Object v = itt.get(attr);
    if (v == null || "".equals(v.toString())) return -1;
    else return Integer.valueOf(v.toString()).intValue();
  }

  private int weekDayEntry(Tabular itt, String attr) {
    Object o = itt.get(attr);
    String s = (o == null)? null : o.toString();

    if (s == null || "".equals(s)) return -1;
    if (s.charAt(0) >= '0' && s.charAt(0) <= '9') {
      return entry(itt, attr);
    }
    return 0;    // === day name lookup not supported ===
  }


  /** Submit the timed request.  Decrement the repeat count if positive.
   *	@return true as a convenience for handleRequest.
   *	@see #handleRequest
   */
  public boolean submitRequest() {
    Pia.verbose("Submitting CrontabEntry " + method + " " + url);
    agent.createRequest(method, url, content, contentType);
    if (repeat > 0) {
      if (--repeat == 0) {
	Pia.verbose("--Maximal repeat count reached.");
      } 
    }
    return true;
  }

  /** Perform all processing necessary for the given times.  Because
   *	of the way we are called, it is safe to assume that
   *	<code>previousTime</code> and <code>nextTime</code> fall into
   *	different 1-minute intervals measured from time=0 (i.e. they
   *	differ when divided by 60,000 -- 1 minute in milliseconds).
   *	It is <em>not</em> safe to assume that they are anywhere close
   *	to one minute apart.
   *
   *	@param previousTime the last time this crontab was processed.
   *	@param thisTime the current time.
   *	@return true if a request was made <em>or</em> if the entry has 
   *	 expired.
   */
  public boolean handleRequest(long previousTime, long thisTime) {

    if (repeat == 0) return true;

    // First determine the _current_ minute, hour, day, month, year,
    // and weekday.

    Date date = new Date(thisTime);
    Calendar today = new GregorianCalendar();

    int y 	  = today.get(Calendar.YEAR);
    int m	  = today.get(Calendar.MONTH) + 1;
    int d	  = today.get(Calendar.DAY_OF_MONTH);
    int h	  = today.get(Calendar.HOUR_OF_DAY);
    int min	  = today.get(Calendar.MINUTE);
    int wd	  = (today.get(Calendar.DAY_OF_WEEK)- Calendar.SUNDAY + 7) % 7;
    // We want Sunday = 0.  This handles any reasonable value of SUNDAY;
    
    // Check the expiration date. 
    if (checkExpired(min, h, d, m, y, wd)) {
      Pia.verbose("Expired CrontabEntry " + method + " " + url);
      repeat = 0;
      return true;
    }

    // Now check for match.
    if (checkMatch(min, h, d, m, y, wd)) {
      return submitRequest();
    }

    return false;
  }

  /** Test whether the entry has expired.  
   *	@return true if <code>repeat == 0</code>.
   */
  public boolean expired() {
    return repeat == 0;
  }

  /** Test whether the entry matches the current date and time. */
  public boolean checkMatch(int min, int h, int d, int m, int y, int wd) {
    if ((minute < 0 || minute == min) && (hour    < 0 || hour    == h) &&
	(day    < 0 || day    == d)   && (month   < 0 || month   == m) &&
	(year   < 0 || year   == y)   && (weekday < 0 || weekday == wd)   )
	return true;
    else
      return false;
  }

  /** Test whether the entry has passed its expiration date. */
  public boolean checkExpired(int min, int h, int d, int m, int y, int wd) {

    // There's probably a better way using a Calendar.

    if (untilHour < 0 && untilDay < 0 && untilMonth < 0) return false;

    //System.err.println("h=" + h + " d=" + d + " m="+m);
    //System.err.println("uh="+untilHour+" ud="+untilDay+" um="+untilMonth);

    if (untilMonth  > 0 && untilMonth  < m) return true;
    if ((untilMonth < 0 || untilMonth == m) &&
	(untilDay  >= 0 && untilDay    < d)   ) return true;
    if ((untilMonth < 0 || untilMonth == m) && 
	(untilDay   < 0 || untilDay   == d) && untilHour < h) return true;

    return false;
  }
}
