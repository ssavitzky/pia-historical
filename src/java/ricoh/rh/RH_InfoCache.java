/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: RH_InfoCache
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 01.05.98 - revised 02-27-98
 * Desc: Container for document information when caching docs.  Each object contains info
 * regarding state, url, etc. of original document
 *
 */
package ricoh.rh;

public class RH_InfoCache {
  private String url="", pasturl="", duration="";
  private int type=-1;

  public RH_InfoCache(String newurl, String past, int newtype, String dur) {
    url=newurl;
    pasturl=past;
    duration=dur;
    type=newtype;
  }
  public String getURL() {
    return url;
  }
  public String getPastURL() {
    return pasturl;
  }
  public int getType() {
    return type;
  }
  public String getDuration() {
    return duration;
  }
}

