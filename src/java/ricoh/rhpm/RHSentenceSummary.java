/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 *  Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 7.9.97
 *
 */
package ricoh.rhpm;

import java.io.*;
//import RH_ActiveConcept;


public class RHSentenceSummary implements Serializable{
  private int begin=0, end=0, s_number, t_number, topicIdx, overallSentenceNumber;
  private String sentence;
  private RHActiveConcept concept;
  private boolean active=false;

  RHSentenceSummary(RHActiveConcept s_con, int beginLoc, int endLoc, int t_idx, int t_num, int s_num, int sent) {
    concept=s_con;
    begin=beginLoc;
    end=endLoc;
    topicIdx=t_idx;  // topic number
    t_number=t_num;
    s_number=s_num; // sentence number
    active=false;
    overallSentenceNumber=sent;
  }
  RHSentenceSummary(RHActiveConcept s_con, int beginLoc, int endLoc, int t_idx, int t_num, int s_num, boolean act, int sent) {
    concept=s_con;
    begin=beginLoc;
    end=endLoc;
    topicIdx=t_idx;  // topic number
    t_number=t_num;
    s_number=s_num; // sentence number
    active=act;
    overallSentenceNumber=sent;
  }

  RHSentenceSummary(RHActiveConcept s_con, int beginLoc, String s, int t_idx, int t_num, int s_num, int sent) {
    concept=s_con;
    begin=beginLoc;
    end=0;
    sentence=s;
    topicIdx=t_idx;  // topic number
    t_number=t_num;
    s_number=s_num; // sentence number
    active=false;
    overallSentenceNumber=sent;
  }

  public void addSentence(String sent) {
    sentence=sent;
  }
  public RHActiveConcept getConcept() {
    return concept;
  }
  public int getBegin() {
    return begin;
  }
  public int getEnd() {
    return end;
  }
  public String getSentence() {
    return sentence;
  }
  public int getSentenceNumber() {
    return s_number;
  }
  public int getTopicNumber() {
    return t_number;
  }
  public int getTopicIdx() {
    return topicIdx;
  }
  public boolean isActive() {
    return active;
  }
  public void setActive(boolean act) {
    active=act;
  }
  public int getOverallSentenceNumber() {
    return overallSentenceNumber;
  }
}
