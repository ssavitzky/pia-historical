/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 *  Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 7.9.97 - revised 02-06-98
 *
 */
package ricoh.rh;

import java.io.*;
//import RH_MainFrame;

public class RH_TopicKeyword implements Serializable {
  public double[] scores;
  public int[] matchLocation, // holds the location of matches in document
    wordLocation, 
    sentenceLocation,  // holds pointer location which refers to the document buffer where the find was made
    sentences;  // holds the pointer where the find was made to the RH_SentenceSummary structure
  private int defaultArraySize=RH_GlobalVars.RH_DefaultMatchVectorSize;
  private String[] phrase;
  private int phraseLen=0, arrayIncrementSize=20, sentencePtr=0;
  public int scoresPtr=0;

  RH_TopicKeyword(String[] newphrases) {
    int i=0;
    phrase=new String[newphrases.length];
    for (i=0;i<RH_MainFrame.maxNumberKeywords && i<newphrases.length && newphrases[i]!=null; i++)
      phrase[i]=newphrases[i];
    phraseLen=i;
    scores=new double[defaultArraySize];
    matchLocation=new int[defaultArraySize];
    wordLocation=new int[defaultArraySize];
    sentenceLocation=new int[defaultArraySize];
    sentences=new int[defaultArraySize];
    for (i=0;i<defaultArraySize;i++) {
      matchLocation[i]=wordLocation[i]=sentenceLocation[i]=sentences[i]=0;
      scores[i]=0;
    }
    scoresPtr=0;
  }
  public int getNumberKeywordStrings() {
    return phraseLen;
  }
  // returns the length of all keywords which is different from the length of the phrase when combined
  // becuase it is sans spaces.
  public int getTotalLengthOfKeywords() {
    int i=0,len=0;
    for (i=0;i<phraseLen;i++) len+=phrase[i].length()+1; // adding one to account for an assumed space between words
    return len;
  }
  public String getName() {
    String tmp="";
    StringBuffer tmp2=new StringBuffer();
    for (int i=0; i<phraseLen;i++)  //tmp=new String(tmp + " " + phrase[i]);
	tmp2.append(phrase[i]).append(" ");
    return tmp2.toString();
  }
  public void match(double score, int loc, int wordLoc, int sentenceNum, int sentencePtr) {
    int i=0;
    //System.out.println(scoresPtr+">["+defaultArraySize+"]Match for " + getName() + " score="+score+" ptr="+loc+" wordLoc="+wordLoc);
    // If we are at our limit, reallocate more space for scores and matching
    if (scoresPtr+1>=defaultArraySize) {
      System.out.println("&&&&&&&&&&&&&Reallocating Topics Space");
      double[] tmpScores=new double[scoresPtr];
      int[] tmpMatches= new int[scoresPtr], tmpWords=new int[scoresPtr], tmpSentences=new int[scoresPtr], tmpSentenceLocs=new int[scoresPtr];
      for (i=0;i<scoresPtr;i++) {
	tmpScores[i]=scores[i];
	tmpMatches[i]=matchLocation[i];
	tmpSentenceLocs[i]=sentenceLocation[i];
	tmpWords[i]=wordLocation[i];
	tmpSentences[i]=sentences[i];
      }
      defaultArraySize+=arrayIncrementSize;
      scores=new double[defaultArraySize];
      matchLocation=new int[defaultArraySize];
      wordLocation=new int[defaultArraySize];
      sentenceLocation=new int[defaultArraySize];
      sentences=new int[defaultArraySize];
      for (i=0;i<scoresPtr;i++) {
	scores[i]=tmpScores[i];
	matchLocation[i]=tmpMatches[i];
	sentenceLocation[i]=tmpSentenceLocs[i];
	wordLocation[i]=tmpWords[i];
	sentences[i]=tmpSentences[i];
      }
    }
    matchLocation[scoresPtr]=loc;
    wordLocation[scoresPtr]=wordLoc;
    sentenceLocation[scoresPtr]=sentenceNum;
    sentences[scoresPtr]=sentencePtr;
    scores[scoresPtr++]=score;
  }
  /**
   * This adds a score and match count to a topic that was matched upon by another topic in the activeConcept.match
   * routine. i.e. this rewards the topic which matched with the topic we are evaluating
   */
  public void addScore(int ptr, int loc, int wordLoc, double newscore) {
    if (ptr>=0 && newscore>scores[ptr]) {
      //System.out.println("......AddScore: "+getName()+" scoresPtr="+ptr+" was wordLoc="+wordLocation[ptr]+" score="+scores[ptr]);
      matchLocation[ptr]=loc;
      wordLocation[ptr]=wordLoc;
      scores[ptr]=newscore;
      //System.out.println("......................is wordLoc="+wordLocation[ptr]+" score="+scores[ptr]);
    }
  }
  public double getScore(int idx) {
    if (idx<scoresPtr) return scores[idx];
    else return -1;
  }

  public int getFrequency() {
    return scoresPtr;
  }
  /**
   * This method should only be used when deserializing objects; see RH_MainFrame.readLocalInfoFil()
   */
  public void setFrequency(int freq) {
    scoresPtr=freq;
  }
  public double getAverageScore() {
    double count=0;
    for (int i=0;i<scoresPtr;i++) count+=scores[i];
    if (count>0) return count/scoresPtr;
    else return 0;
  }
  public String[] getPhrase() {
    return phrase;
  }
  public void resetScores() {
    for (int i=0;i<scoresPtr;i++) scores[i]=0;
    scoresPtr=0;
  }
    /* 8.26.98 can't do this because all of the documents that i have already serialized will
     * no longer work in i add new methods to the class :-(  luckily the variables were already public. - jg
    public void setSentences(int[] s) {
	sentences=s;
    }
    public void setSentenceLocations(int[] s) {
	sentenceLocation=s;
    }
    public int[] getSentences() {
	return (sentences!=null ? sentences : null);
    }
    public int[] getSentenceLocations() {
	return (sentenceLocation!=null ? sentenceLocation : null);
    }
    public void setWordLocations(int[] s) {
	wordLocation=s;
    }
    public int[] getWordLocations() {
	return (wordLocation!=null ? wordLocation : null);
    }
    */
    
}


