/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 *  Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 4.24.97 - revided 02-11-98
 *
 */
package ricoh.rh;

class RH_SentenceMatchList {
  private int conceptIdx, begin, topicIdx, number, sentenceNumber;
  RH_SentenceMatchList(int c_idx, int beginLoc, int t_idx, int num, int snum) {
    conceptIdx=c_idx;
    begin=beginLoc;
    topicIdx=t_idx;
    number=num;
    sentenceNumber=snum;
  }
  public int getIdx() {
    return conceptIdx;
  }
  public int getBegin() {
    return begin;
  }
  public int getTopicIdx() {
    return topicIdx;
  }
  public int getNumber() {
    return number;
  }
  public int getSentenceNumber() {
    return sentenceNumber;
  }
}
