/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 *  Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 6.20.97 - revised 02-06-98
 *
 */
package ricoh.rhpm;

import java.io.*;
import java.util.*;

import ricoh.rh.RH_GlobalVars;


public class RHActiveConcept {
  private int numTopics, value;
  // This was used to hold a collection of topic arrays (RH_TopicKeywords) but I have recently
  // changed my method of concept (and subconcept) representation such that I currently only
  // use 1 RH_TopicKeyword object for each concept.  This object contains all keyword phrases assoc.
  // with the main concept.  In the future this may change, and i may expand the use of topics.
  // For now (11-26-97), the array "topics" is singular and thus, the small number (5) for maxTopics. jmg
  private int maxTopics=5, documentSentenceCount=0;
  public RHTopicKeyword[] topics=new RHTopicKeyword[maxTopics];
  private double prior=0.1, priorNot=0, posterior=0.0, prob=0, probNot=0;
  private String name, shortname;
  private boolean active=false, satisfied=false;
  private boolean useSLoc=false;
  private Vector sentences;


  public RHActiveConcept (String newname, String sname) {
    name=newname;
    shortname=sname;

    priorNot=1-prior;
  }
  public RHActiveConcept (String newname, String sname, double newprior) {
    name=newname;
    shortname=sname;
    prior=newprior;
    priorNot=1-prior;
  }
   
  public void addKeywords (String newname, String[][] topicstrings, int num) {
    topics = new RHTopicKeyword[num];
    //System.out.println("Concept:"+name);
    for (numTopics=0;numTopics<num;numTopics++) {
      topics[numTopics] = new RHTopicKeyword(topicstrings[numTopics]);
      //System.out.println(numTopics+">.........Topic:" + topics[numTopics].getName());
    }
  }
  public void addKeywords (String[][] topicstrings, int num) {
    topics = new RHTopicKeyword[num];
    //System.out.println("Concept:"+name);
    for (numTopics=0;numTopics<num;numTopics++) {
      topics[numTopics] = new RHTopicKeyword(topicstrings[numTopics]);
      //System.out.println(numTopics+">.........Topic:" + topics[numTopics].getName());
    }
  }

    public void addKeywordStrings(String[] keyphrases) {
	topics = new RHTopicKeyword[keyphrases.length];
	String[] phrases=null;
	StringTokenizer tokens=null;
	int j=0;
	for (numTopics=0, j=0;numTopics<topics.length;numTopics++, j=0) {
	    tokens=new StringTokenizer(keyphrases[numTopics]);
	    phrases=new String[tokens.countTokens()];
	    while (tokens.hasMoreTokens()) phrases[j++]=(String)tokens.nextToken();
	    topics[numTopics] = new RHTopicKeyword(phrases);
	}
    }

    public String[] getTopicKeywordStrings() {
	String[] buf=new String[numTopics];
	for (int i=0;i<numTopics;i++) buf[i]=topics[i].getName();
	return buf;
    }

  public RHTopicKeyword getTopic(int i) {
    if (i<numTopics && topics[i]!=null) return topics[i];
    else return null;
  }
  public int getLength () {
    return numTopics;
  }
  public String getName() {
    return name;
  }
  public String getShortName() {
    return shortname;
  }

    public void setShortName(String nm) {
	shortname=nm;
    }
    public void setName(String nm) {
	name=nm;
    }
  // Satisfied, means that we have a match with this concept
  public boolean satisfied() {
    return satisfied;
  }
  public void setSatisfied (boolean set) {
    satisfied=set;
  }
  
  // Active means that the user wants the system to look for this concept
  public boolean isActive() {
    return active;
  }
  public void setActive(boolean set) {
    active=set;
  }

  public double getPrior() {
    return prior;
  }
    public void setPrior(double newp) {
	prior=newp;
    }
  public double getPosterior() {
    return posterior;
  }
  public int getNumberTopicPhrases () {
    int phrases=0,i=0,j=0;
    for (i=0;i<numTopics;i++)
      phrases+=topics[i].getNumberKeywordStrings();
    return phrases;
  }

  public RHTopicKeyword findTopic (String name) {
    int i=0;
    for (i=0;i<numTopics;i++)
      if (topics[i].getName().equalsIgnoreCase(name)) return topics[i];
    return null;
  }

  public void printAnnotation(String topicName, int number) {
    RHTopicKeyword topic=findTopic(topicName);
    if (topic!=null) {
      System.out.println("...Topic: " + topic.getName() + " Hits=" + topic.getFrequency());
      System.out.println(".....Num_____Score_____MLoc_____WLoc_____SLoc_____SNum");
      for (int i=0;i<topic.getFrequency();i++) {
	if (i==number-1) 
	  System.out.println("......"+i+"______"+topic.scores[i]+"______"+topic.matchLocation[i]+"______"+topic.wordLocation[i]+
			     "______"+topic.sentenceLocation[i]+"______"+topic.sentences[i]+"***");
	else
	  System.out.println("......"+i+"______"+topic.scores[i]+"______"+topic.matchLocation[i]+"______"+topic.wordLocation[i]+
			     "______"+topic.sentenceLocation[i]+"______"+topic.sentences[i]);
      }
    }
  }

  public double match (RHTopicKeyword topic, int ptrLoc, int wordLoc, int sentenceLoc, int sentencePtr) {
    int i=0, j=0, bestLoc=0,bigNum=320000, len=0, holdLoc=0, phraseLen=0,wordLocPtr=0;
    double score=0;
    //System.out.print(wordLoc+">MATCH: topic=" + topic.getName());
    // Find the shortest distance between this new find and a previous find from the topic set
    //if (topics[0].getFrequency()>0) bestLoc=topics[0].getFrequency();
    for (i=0; i<numTopics; i++) {
      len=topics[i].getFrequency();
      // check the last match for a topic in the set
      if (len>=1 && topics[i].wordLocation[len-1]!=0 && topics[i].wordLocation[len-1]>bestLoc) {
	holdLoc=i;  // hold the position of the find
	bestLoc=topics[i].wordLocation[len-1];
	phraseLen=topics[i].getNumberKeywordStrings();
	wordLocPtr=len-1;
      }
    }
    // ***** Here is where I generate a score for the found topic; 
    if (bestLoc>0) {
      // Get the difference between the two lcoations and see what kind of proximity value we get
      bestLoc=wordLoc-bestLoc-phraseLen;
      if (bestLoc<=RH_GlobalVars.RH_Prox_ReallyGood) score=RH_GlobalVars.RH_Prox_ReallyGood_Score;
      else if (bestLoc<=RH_GlobalVars.RH_Prox_VeryGood) score=RH_GlobalVars.RH_Prox_VeryGood_Score;
      else if (bestLoc<=RH_GlobalVars.RH_Prox_Good) score=RH_GlobalVars.RH_Prox_Good_Score;
      else if (bestLoc<=RH_GlobalVars.RH_Prox_OK) score=RH_GlobalVars.RH_Prox_OK_Score;
      else score=RH_GlobalVars.RH_Prox_Poor_Score;
      //System.out.println("....is "+bestLoc+" away from: " + topics[holdLoc].getName()+"("+holdLoc+") score="+score);
      topics[holdLoc].addScore(wordLocPtr,ptrLoc, wordLoc, score);
    }
    //else System.out.println(" -- no relevant topics in proximity");
    topic.match(score,ptrLoc,wordLoc,sentenceLoc,sentencePtr);
    //if (bestLoc>0) printAnnotation(topics[holdLoc].getName(),topics[holdLoc].getFrequency());
    return score;
  }

  public void computePosterior (int sentenceCount, boolean useSentenceLocation) {
    int topicFreq=0, i=0, nonZeroCount=0;
    double topicAvg=0, topicProx=0, topicSent=0, topicFreqProb=0, topicFreqNotProb=0, topicProxProb=0, topicProxNotProb=0,
      topicSentProb=0, topicSentNotProb=0;
    boolean prn=false;
    // initialize
    useSLoc=useSentenceLocation;
    documentSentenceCount=sentenceCount;
    prob=1;
    probNot=1;
    if (prn) {
      System.out.println("==============================================");
      System.out.println("Compute for " + name + ": ");
    }
    // Get total frequency count for all topics
    for (i=0; i<numTopics;i++) {
      if (topics[i].getFrequency()>0) nonZeroCount++;
      topicFreq+=topics[i].getFrequency();
      topicAvg+=topics[i].getAverageScore();
      if (prn) 
	System.out.println("---->" + topics[i].getName() + "[" + topicFreq +"]:"+topics[i].getAverageScore());
    }
    if (nonZeroCount>0) topicProx=topicAvg/(double)nonZeroCount;
    else topicProx=0;
    if (prn) 
      System.out.println("Topic Avg=" + topicAvg + " TopicProxAvg="+ topicProx + " Topic Freq=" + topicFreq);

    if (useSLoc) topicSent=(double)getValidSentences()/(double)sentences.size();
    //System.out.println("==========Name:"+name+" TopicSent:"+topicSent);
    
    // Caltegorize the frequency value for the topics
    if (topicFreq>=RH_GlobalVars.RH_VeryMany_Topics) {
      if (prn) System.out.print(i+">VeryMany("+topicFreq+") ");
      topicFreqProb=RH_GlobalVars.RH_VeryMany_Prob;
      topicFreqNotProb=RH_GlobalVars.RH_VeryMany_Not_Prob;
    }
    else if (topicFreq>=RH_GlobalVars.RH_Many_Topics) {
      if (prn) System.out.print(i+">Many("+topicFreq+") ");
      topicFreqProb=RH_GlobalVars.RH_Many_Prob;
      topicFreqNotProb=RH_GlobalVars.RH_Many_Not_Prob;
      prob*=RH_GlobalVars.RH_Many_Prob;
      probNot*=RH_GlobalVars.RH_Many_Not_Prob;
    }
    else if (topicFreq>=RH_GlobalVars.RH_Few_Topics) {
      if (prn) System.out.print(i+">Few("+topicFreq+") ");
      topicFreqProb=RH_GlobalVars.RH_Few_Prob;
      topicFreqNotProb=RH_GlobalVars.RH_Few_Not_Prob;
    }
    else if (topicFreq>=RH_GlobalVars.RH_MoreThanOne_Topics) {
      if (prn) System.out.print(i+">MTO("+topicFreq+") ");
      topicFreqProb=RH_GlobalVars.RH_MoreThanOne_Prob;
      topicFreqNotProb=RH_GlobalVars.RH_MoreThanOne_Not_Prob;
    }
    else if (topicFreq==RH_GlobalVars.RH_One_Topics) {
      if (prn) System.out.print(i+">One("+topicFreq+") ");
      topicFreqProb=RH_GlobalVars.RH_One_Prob;
      topicFreqNotProb=RH_GlobalVars.RH_One_Not_Prob;
    }
    else {
      topicFreqProb=RH_GlobalVars.RH_None_Prob;
      topicFreqNotProb=RH_GlobalVars.RH_None_Not_Prob;
      if (prn) System.out.print(i+">None("+topicFreq+") ");
    }

    //***-- Set the Proximity Value
    if (topicProx>=RH_GlobalVars.RH_Prox_ReallyGood_Score) {
      if (prn) System.out.print(i+">ReallyGood("+topicProx+") ");
      topicProxProb=RH_GlobalVars.RH_Prox_ReallyGood_Prob;
      topicProxNotProb=RH_GlobalVars.RH_Prox_ReallyGood_Not_Prob;
    }
    else if (topicProx>=RH_GlobalVars.RH_Prox_VeryGood_Score) {
      if (prn) System.out.print(i+">VeryGood("+topicProx+") ");
      topicProxProb=RH_GlobalVars.RH_Prox_VeryGood_Prob;
      topicProxNotProb=RH_GlobalVars.RH_Prox_VeryGood_Not_Prob;
    }
    else if (topicProx>=RH_GlobalVars.RH_Prox_Good_Score) {
      if (prn) System.out.print(i+">Good("+topicProx+") ");
      topicProxProb=RH_GlobalVars.RH_Prox_Good_Prob;
      topicProxNotProb=RH_GlobalVars.RH_Prox_Good_Not_Prob;
    }
    else if (topicProx>=RH_GlobalVars.RH_Prox_OK_Score) {
      if (prn) System.out.print(i+">OK("+topicProx+") ");
      topicProxProb=RH_GlobalVars.RH_Prox_OK_Prob;
      topicProxNotProb=RH_GlobalVars.RH_Prox_OK_Not_Prob;
    }
    else {
      if (prn) System.out.print(i+">Poor("+topicProx+") ");
      topicProxProb=RH_GlobalVars.RH_Prox_Poor_Prob;
      topicProxNotProb=RH_GlobalVars.RH_Prox_Poor_Not_Prob;
    }

    //***-- Set the Sentence Location Value
    if (useSLoc) {
      if (topicSent>=RH_GlobalVars.RH_Sent_ReallyGood_Score) {
	if (prn) System.out.print(i+">ReallyGood("+topicProx+") ");
	topicSentProb=RH_GlobalVars.RH_Sent_ReallyGood_Prob;
	topicSentNotProb=RH_GlobalVars.RH_Sent_ReallyGood_Not_Prob;
      }
      else if (topicSent>=RH_GlobalVars.RH_Sent_VeryGood_Score) {
	if (prn) System.out.print(i+">VeryGood("+topicSent+") ");
	topicSentProb=RH_GlobalVars.RH_Sent_VeryGood_Prob;
	topicSentNotProb=RH_GlobalVars.RH_Sent_VeryGood_Not_Prob;
      }
      else if (topicSent>=RH_GlobalVars.RH_Sent_Good_Score) {
	if (prn) System.out.print(i+">Good("+topicSent+") ");
	topicSentProb=RH_GlobalVars.RH_Sent_Good_Prob;
	topicSentNotProb=RH_GlobalVars.RH_Sent_Good_Not_Prob;
      }
      else if (topicSent>=RH_GlobalVars.RH_Sent_OK_Score) {
	if (prn) System.out.print(i+">OK("+topicSent+") ");
	topicSentProb=RH_GlobalVars.RH_Sent_OK_Prob;
	topicSentNotProb=RH_GlobalVars.RH_Sent_OK_Not_Prob;
      }
      else {
	if (prn) System.out.print(i+">Poor("+topicSent+") ");
	topicSentProb=RH_GlobalVars.RH_Sent_Poor_Prob;
	topicSentNotProb=RH_GlobalVars.RH_Sent_Poor_Not_Prob;
      }
    }
   
    if (topicFreq>0) {
      if (useSLoc) 
	posterior=(topicFreqProb*topicProxProb*topicSentProb*prior)/((topicFreqProb*topicProxProb*topicSentProb*prior) + (topicFreqNotProb*topicProxNotProb*topicSentNotProb*priorNot));
      else posterior=(topicFreqProb*topicProxProb*prior)/((topicFreqProb*topicProxProb*prior) + (topicFreqNotProb*topicProxNotProb*priorNot));
      if (prn) {
	System.out.println("");
	System.out.println(topicFreqProb*topicProxProb*prior);
	System.out.println("----------------------- =" + posterior);
	System.out.println((topicFreqProb*topicProxProb*prior) + (topicFreqNotProb*topicProxNotProb*priorNot));
      
	System.out.println("");
	System.out.println("TopicsFreqProb" + topicFreqProb + "  TopicsFreqProbNot" + topicFreqNotProb);
	System.out.println("TopicsProxProb" + topicProxProb + "  TopicsProxProbNot" + topicProxNotProb);
      }
      value=(int)(posterior*100);
    }
    else value=0;
    if (prn) {
      System.out.println("Posterior=" + posterior + " which is " + (int)(posterior*100));
      System.out.println("==============================================");
    }

  }
  public int getValue() {
    return value;
  }
  /**
   * This is used when reading the info file which only contains the overall value for the concept given the 
   * current document.  so i set the value but all topic values are not set!
   */
  public void setValue(int val) {
    value=val;
  }
  public void resetValues () {
    posterior=0; value=0;
    for (int i=0; i<numTopics; i++)
      topics[i].resetScores();
  }

  public int getIconValue() {
    double diff=12.5; 
    int incr=2;

    if (value>=(100-diff)) return 8;
    else if (value>=(100-(incr++*diff))) return 7;
    else if (value>=(100-(incr++*diff))) return 6;
    else if (value>=(100-(incr++*diff))) return 5;
    else if (value>=(100-(incr++*diff))) return 4;
    else if (value>=(100-(incr++*diff))) return 3;
    else if (value>=(100-(incr++*diff))) return 2;
    else if (value>0) return 1;
    else return 0;
  }

    public void pushSentence(int num) {
	if (sentences==null) sentences=new Vector();
	sentences.addElement(new Integer(num));
    }

    public void setSentenceVector(Vector v) {
	sentences=v;
    }
    public Vector getSentenceVector() {
	return (sentences!=null ? sentences : null);
    }

  /**
   * Returns a number representing the number of sentences which occur in the
   * RH_GlobalVars.topSentenceRangePercentage and RH_GlobalVars.bottomSentenceRangePercentage range;
   */
  public int getValidSentences() {
    double topRange=(double)documentSentenceCount*RH_GlobalVars.topSentenceRangePercentage;
    double bottomRange=(double)documentSentenceCount-(documentSentenceCount*RH_GlobalVars.bottomSentenceRangePercentage);
    Integer value=null;
    int count=0;
    if (sentences!=null) {
      Enumeration enum=sentences.elements();
      while(enum.hasMoreElements()) {
	value=(Integer)enum.nextElement();
	if (value.intValue()<=topRange || value.intValue()>=bottomRange) count++;
      }
      return count;
    }
    else return 0;
  }

}
