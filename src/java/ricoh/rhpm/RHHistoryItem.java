/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc. - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: RH_HistoryItem: class for holding history items
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 01.12.98- revised 02-26-98
 *
 */
package ricoh.rhpm;

import java.util.Date;

class RHHistoryItem {
  private int id;
  private String url, datestr;
  private long date;

  RHHistoryItem(String newurl, int newid, Date newdate) {
    id=newid;
    url=newurl;
    date=newdate.getTime();
    datestr=newdate.toString();
  }

  public int getID() {
    return id;
  }
  public String getURL() {
    return url;
  }
  public String getDateStr() {
    return datestr;
  }
  public long getDate() {
    return date;
  }
  public void setDate(Date newdate) {
    date=newdate.getTime();
    datestr=newdate.toString();
  }
}
