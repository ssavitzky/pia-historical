/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: RH_CalendarDatum: data item for using in calendar
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 01.19.98 - revised 02-27-98
 *
 */
package ricoh.rhpm;


class RHCalendarDatum {
  private int day, score;
  private String name, concept, time;

  public RHCalendarDatum(int newday, String newname, String newconcept, String newtime, int newscore) {
    day=newday;
    name=newname;
    concept=newconcept;
    time=newtime;
    score=newscore;
  }
  public int getDay() {
    return day;
  }
  public String getName() {
    return name;
  }
  public String getConcept () {
    return concept;
  }
  public String getTime() {
    return time;
  }
  public int getScore() {
    return score;
  }

}
