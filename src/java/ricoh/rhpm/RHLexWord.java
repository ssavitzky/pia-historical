/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: RH_LexWord: lexicon word in user's document collection lexicon;
 * These are the objects used in the RH_UserLexicon hashtable to store all
 * words ever processed as RH_LexWord objects.  Each RH_LexWord object contains
 * a hashtable of RH_LexEntry objects when point to the individual sentences
 *  (& locations) in a document where the word occurred.
 *
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 3.13.98
 *
 */
package ricoh.rhpm;
import java.io.*;
import java.util.*;

class RHLexWord implements Serializable {
  private String word=null;
  private int count=0, frequency=0;
  private Hashtable keys;
  private double score1, score2;

  RHLexWord (String newword, String documentKey, int wordcount) {
    word=newword;
    keys=new Hashtable();
    Vector vector = new Vector();
    vector.addElement(documentKey);
    vector.addElement(new Integer(wordcount));
    keys.put(documentKey,vector);
    count=1;
    score1=score2=0;
  }
  /**
   * Used primarily when reading info from file and reconstructing lexicon
   */
  RHLexWord (String newword, Double newscore1, Double newscore2, Hashtable newkeys) {
    word=newword;
    keys=newkeys;
    score1=newscore1.doubleValue();
    score2=newscore2.doubleValue();
    count=keys.size();
    getFrequency();
  }
  /**
   * Increments the counter and adds a new entry to the hashtable at the sentence location using the
   * counter as the storage item.  thus i can produce a list which shows all sentences the word occurred in.
   */
  public void incrementCounter(String documentKey, int wordcount) {
    //keys.put(documentKey,new RHLexEntry(w,m,concept,loc,sent,documentKey));
    Vector vector = new Vector();
    vector.addElement(documentKey);
    vector.addElement(new Integer(wordcount));
    keys.put(documentKey,vector);
    count++;
  }
  /*
  public void incrementCounter(String documentKey, RHLexEntry entry) {
    entry.setDocumentKey(documentKey);
    keys.put(documentKey,entry);
    count++;
  }
  */
  /**
   * In this case the count represents the number of documents this owrd occurs in but not the overall frequency
   */
  public int getCount() {
    return count;
  }
  /**
   * Represents the overall frequency of occurance this word has in all documents in corpus
   */
  public int getFrequency() {
    Enumeration enum=keys.elements();
    frequency=0;
    while (enum.hasMoreElements()) {
      Vector entry=(Vector)enum.nextElement();
      //** Grab the second element which is the count
      Integer f=(Integer)entry.elementAt(1);
      frequency+=f.intValue();
    }
    return frequency;
  }
  /**
   * returns a list of the form: |dockey-1,frequency|dockey-2,frequency|...
   * for use in constructing the user's lexicon file
   */
  public void getDocFrequencies() {
     }
  
  public String getWord() {
    return word;
  }
  public Hashtable getKeys() {
    return keys;
  }

  public double getScore1() {
    return score1;
  }
  public double getScore2() {
    return score2;
  }

  public void setScore1(double score) {
    score1=score;
  }
  public void setScore2(double score) {
    score2=score;
  }

}

