/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: RH_LexEntry: lexicon entry in lexicon
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 3.11.98
 *
 */
package ricoh.rhpm;
import java.io.*;
import java.util.*;

class RHLexEntry implements Serializable {
  private String word=null;
  private boolean matched=false;  // whether or not the word is relevant to a concept or not
  private int count=0;
  private Vector sentences, location, matchLocations, matchConcepts;
  private String relevantConcept, documentKey;
  private double weight=0;

  RHLexEntry (String w, boolean m, String concept, int loc, int sent) {
    word=w;
    matched=m;
    count=1;
    relevantConcept=concept;
    sentences=new Vector(); //Hashtable();
    //sentences.put(new StringBuffer().append(count).toString(),new Integer(sent));
    sentences.addElement(new Integer(sent));
    location=new Vector(); //Hashtable();
    //location.put(new StringBuffer().append(count).toString(),new Integer(loc));
    location.addElement(new Integer(loc));
    matchLocations=null;
    matchConcepts=null;
    weight=0;
  }
  RHLexEntry (String w, boolean m, int freq, Vector sent, Vector loc, Vector match, Vector concepts, Double wgh) {
    word=w;
    matched=m;
    count=freq;
    sentences=sent;
    location=loc;
    matchLocations=match;
    matchConcepts=concepts;
    weight=wgh.doubleValue();
  }
  //** Used for adding entries when using the UserLexicon (not the document lexicon)
  RHLexEntry (String w, boolean m, String concept, int loc, int sent, String key) {
    word=w;
    matched=m;
    count=1;
    documentKey=key;  
    relevantConcept=concept;
    System.out.println("***Saving DocKey in Entry:"+word+" ->"+documentKey+" ->"+relevantConcept);
    sentences=new Vector(); //Hashtable();
    //sentences.put(new StringBuffer().append(count).toString(),new Integer(sent));
    sentences.addElement(new Integer(sent));
    location=new Vector(); //Hashtable();
    //location.put(new StringBuffer().append(count).toString(),new Integer(loc));
    location.addElement(new Integer(loc));
    matchLocations=null;
    matchConcepts=null;
    weight=0;
  }
  /**
   * Increments the counter and adds a new entry to the hashtable at the sentence location using the
   * counter as the storage item.  thus i can produce a list which shows all sentences the word occurred in.
   */
  public void incrementCounter(int sent,int loc) {
    //sentences.put(new StringBuffer().append(++count).toString(),new Integer(sent));
    //location.put(new StringBuffer().append(count).toString(),new Integer(loc));
    ++count;
    sentences.addElement(new Integer(sent));
    location.addElement(new Integer(loc));
  }
  public int getCount() {
    return count;
  }
  public String getWord() {
    return word;
  }
  public String getRelevantConceptName() {
    return relevantConcept;
  }
  public boolean getMatched() {
    return matched;
  }
  public void setMatched(boolean set, int sent, String concept) {
    matched=set;
    if (matchLocations==null) matchLocations=new Vector();
    if (matchConcepts==null) matchConcepts=new Vector();
    matchLocations.addElement(new Integer(sent));
    matchConcepts.addElement(new String(concept));
  }
  public String getDocumentKey() {
    return documentKey;
  }
  public void setDocumentKey(String key) {
    documentKey=key;
  }
  public Vector getSentences() {
    return sentences;
  }
  public Vector getLocations() {
    return location;
  }
  public Vector getMatchLocations() {
    return matchLocations;
  }
  public Vector getMatchConcepts() {
    return matchConcepts;
  }
  public void setWeight(double w) {
    weight=w;
  }
  public double getWeight() {
    return weight;
  }

    /**
     * This is used when creating a word with no value to be used in the similarity matching
     */
    public void resetCount() {
	count=0;
    }
}

