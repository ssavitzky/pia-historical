/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: RH_DocLex: creates a lexicon from the document words
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 03.11.98
 *
 */
package ricoh.rhpm;

import java.io.*;
import java.util.*;
import java.lang.*;

import ricoh.rh.RH_GlobalVars;

class RHDocumentLexicon implements Serializable {
  private Hashtable words;
    //private transient RHLex rhLex;
  private transient RHStopWords stopwords;
  int wordCount=0;

  RHDocumentLexicon (RHStopWords sw) {
      //rhLex=parent;
      //stopwords=rhLex.stopwords;
      stopwords=sw;
    words=new Hashtable();
    wordCount=1;
  }
  public void addWord(String word, int sent) {
    RHLexEntry entry=null;
    String trimword=word.trim(),newword=trimword.toUpperCase();
    boolean stop=stopwords.stopWord(newword);
    if (!stop && (entry=(RHLexEntry)words.get(newword))!=null) entry.incrementCounter(sent,wordCount++);
    else if (!stop) words.put(newword, new RHLexEntry(trimword,false,"",wordCount++,sent));
  }

  /**
   * I use this method to add words in the special lexicon because i want the location (word count) information sent to this
   * lex from the main lex which is keeping accurate track.
   */
  public String addWord(String word, int newcount, int sent) {
    wordCount=newcount;
    RHLexEntry entry=null;
    String trimword=word.trim(), newword=trimword.toUpperCase();
    boolean stop=stopwords.stopWord(newword);
    if (!stop && (entry=(RHLexEntry)words.get(newword))!=null) entry.incrementCounter(sent,wordCount++);
    else if (!stop) words.put(newword, new RHLexEntry(trimword,false,"",wordCount++,sent));
    return (!stop ? trimword : "");
  }

  public void addEntry(RHLexEntry entry) {
    words.put(entry.getWord(),entry);
  }

  public RHLexEntry getWord(String word) {
    String trimword=word.trim(), newword=trimword.toUpperCase();
    return (RHLexEntry)words.get(newword);
  }
  public RHLexEntry getExactWord(String word) {
    return (RHLexEntry)words.get(word);
  }
  /**
   * Updates the word to relfect a match

  public void wordMatched(String word, int sent) {
    RHLexEntry entry=null;
    String trimword=word.trim(), newword=trimword.toUpperCase();
    if ((entry=(RHLexEntry)words.get(newword))!=null)  entry.setMatched(true,sent);
  }
  */

  public int getWordCount() {
    return wordCount-1;  // return minus 1 because we increment this counter after using each time (see above)
  }

  public void flushWords() {
    Enumeration enum = words.elements();
    // Do i really need to do this?  maybe because the gc is needs the help ????
    while (enum.hasMoreElements()) {
      RHLexEntry entry=(RHLexEntry)enum.nextElement();
      entry=null;
    }
    words.clear();
  }

  public void generateHTMLFile(StringBuffer file, String docName) {
    StringBuffer buf=null;
    byte nl=(byte)'\n';
    byte[] newLine=new byte[1];
    newLine[0]=nl;
    String newline=new String(newLine), separator=RH_GlobalVars.rhFieldSeparator, subseparator=RH_GlobalVars.rhFieldSubSeparator;
    try {
      FileOutputStream fp=new FileOutputStream(file.toString());      
      System.out.println("---->>Writing file:"+file.toString());
      buf=new StringBuffer().append(RH_GlobalVars.RH_HTML_Doctype_Header).append(newline).append(RH_GlobalVars.HTML_HTML_Tag)
	.append(RH_GlobalVars.HTML_HEAD_Tag).append(newline).append("<").append(RH_GlobalVars.RH_CalendarHeader_BeginTag)
	.append(" ").append(RH_GlobalVars.RH_DocumentHeader_VERTag).append("=\"").append("").append("\">")
	.append("<"+RH_GlobalVars.RH_SummaryHeader_EndTag+">").append(newline).append(RH_GlobalVars.HTML_TITLE_Tag).append("Similar Documents")
	.append(RH_GlobalVars.HTML_TITLE_Tag_End).append(RH_GlobalVars.HTML_HEAD_Tag_End).append(newline)
	.append(RH_GlobalVars.HTML_BODY_Tag).append(newline)
	.append("<center><h3>").append("Document [").append(docName).append("] Lexicon: ").append(words.size()).append("</h3><br>")
	.append(newline).append("<hr></center>").append(newline);
	
	fp.write(buf.toString().getBytes());

	Enumeration enum = words.elements();
	int count=1;
	RHLexEntry entry=null;
	while (enum.hasMoreElements()) {
	  entry=(RHLexEntry)enum.nextElement();
	  buf=new StringBuffer("<li>").append(count++).append(": ");
	  if (entry.getMatched()) buf.append("<u><b>[").append(entry.getWord()).append("]</b></u>");
	  else buf.append("[").append(entry.getWord()).append("]");
	  buf.append(" Freq:").append(entry.getCount());

	  Integer aint=null;
	  //** Grab the sentence locations and separate each location with a subseparator delimiter
	  Enumeration senum=entry.getSentences().elements();
	  buf.append(" ").append(separator);
	  while(senum.hasMoreElements()) {
	    aint=(Integer)senum.nextElement();
	    buf.append(aint);
	    if (senum.hasMoreElements()) buf.append(subseparator);
	  }

	  //** Grab the word locations and separate each location with a subseparator delimiter
	  senum=entry.getLocations().elements();
	  buf.append(" ").append(separator);
	  while(senum.hasMoreElements()) {
	    aint=(Integer)senum.nextElement();
	    buf.append(aint);
	    if (senum.hasMoreElements()) buf.append(subseparator);
	  }
	  buf.append(separator);
	  
	  //** If this word has been used ina match, grab the matchlocations and write them as well
	  if (entry.getMatched() && entry.getMatchLocations()!=null) {
	    senum=entry.getMatchLocations().elements();
	    Enumeration menum=entry.getMatchConcepts().elements();
	    String str=null;
	    buf.append(" ").append(entry.getMatchLocations().size()).append(separator).append("<u><b>");
	    while(senum.hasMoreElements()&&menum.hasMoreElements()) {
	      aint=(Integer)senum.nextElement();
	      str=(String)menum.nextElement();
	      buf.append(aint).append("-").append(str);
	      if (senum.hasMoreElements()) buf.append(subseparator);
	    }
	    buf.append("</b></u>");
	  }
	  buf.append("</li>").append(newline);
	  fp.write(buf.toString().getBytes());
	}
	buf=new StringBuffer("<hr></body></html>");
	fp.write(buf.toString().getBytes());
	fp.close();
    } catch (IOException ex) {
      System.out.println("Could not create LEXICON HTML file:"+file.toString());
    }
  }

    /**
     * Creates the document lexicon containing the frequency, weight (TFIDF) and pointers
     * to sentences the word occurred in
     */
  public void generateTextFile(StringBuffer file) {
      //System.out.println("=====> Generating text file for "+file.toString());
    StringBuffer buf=null;
    byte nl=(byte)'\n';
    byte[] newLine=new byte[1];
    newLine[0]=nl;
    String newline=new String(newLine);
    byte tabbyte=(byte)'\t';
    byte[] tabbuf=new byte[1];
    tabbuf[0]=tabbyte;
    String tab=new String(tabbuf);
    int num=1;
    String separator=RH_GlobalVars.rhFieldSeparator; //tab;
    String subseparator=RH_GlobalVars.rhFieldSubSeparator;
    String str=null;
    Enumeration senum=null, menum=null;
    Integer aint=null;
    try {
      FileOutputStream fp=new FileOutputStream(file.toString());      
      //System.out.println("=====> Writing file:"+file.toString());
      Enumeration enum = words.elements();
      int count=1;
      RHLexEntry entry=null;
      while (enum.hasMoreElements()) {
	try {
	  entry=(RHLexEntry)enum.nextElement();
	  //** This is word first then frequency
	  buf=new StringBuffer(entry.getWord()).append(separator).append(entry.getCount()).append(separator).append(entry.getWeight());

	  //** Place a flag stating whether this term was used in a concept match
	  if (entry.getMatched()) buf.append(separator).append(RH_GlobalVars.RH_MatchSymbol);
	  else buf.append(separator).append(RH_GlobalVars.RH_NoMatchSymbol);

	  //** Grab the sentence locations and separate each location with a subseparator delimiter
	  senum=entry.getSentences().elements();
	  buf.append(separator);
	  while(senum.hasMoreElements()) {
	    aint=(Integer)senum.nextElement();
	    buf.append(aint);
	    if (senum.hasMoreElements()) buf.append(subseparator);
	  }

	  //** Grab the word locations and separate each location with a subseparator delimiter
	  senum=entry.getLocations().elements();
	  buf.append(separator);
	  while(senum.hasMoreElements()) {
	    aint=(Integer)senum.nextElement();
	    buf.append(aint);
	    if (senum.hasMoreElements()) buf.append(subseparator);
	  }
	  buf.append(separator);
	  
	  //** If this word has been used ina match, grab the matchlocations and write them as well
	  if (entry.getMatched() && entry.getMatchLocations()!=null) {
	    senum=entry.getMatchLocations().elements();
	    menum=entry.getMatchConcepts().elements();	    
	    buf.append(entry.getMatchLocations().size()).append(separator);
	    while(senum.hasMoreElements() && menum.hasMoreElements()) {
	      aint=(Integer)senum.nextElement();
	      str=(String)menum.nextElement();
	      buf.append(aint).append(subseparator).append(str);
	      if (senum.hasMoreElements()) buf.append(subseparator);
	    }
	    buf.append(separator);
	  }

	  buf.append(newline);
	  //System.out.print((num++)+">"+buf.toString());
	  fp.write(buf.toString().getBytes());
	} catch (ClassCastException noex) {
	  System.out.println("***NoClassFound Exception: could not create class");
	}
      }
      fp.close();
    } catch (IOException ex) {
      System.out.println("Could not create LEXICON Text file:"+file.toString());
    }
  }

  public void generateTabFile(StringBuffer file, RHLexEntry[] entries, int maxLength) {
    System.out.println(">..Generating tab file for "+file.toString());
    StringBuffer buf=null;
    byte nl=(byte)'\n';
    byte[] newLine=new byte[1];
    newLine[0]=nl;
    String newline=new String(newLine); 
    byte tabbyte=(byte)'\t';
    byte[] tabbuf=new byte[1];
    tabbuf[0]=tabbyte;
    String tab=new String(tabbuf);
    int idx=0;
    String separator=RH_GlobalVars.rhFieldSeparator; //tab;
    String subseparator=RH_GlobalVars.rhFieldSubSeparator;
    try {
      FileOutputStream fp=new FileOutputStream(file.toString());      
      System.out.println(">..Writing file:"+file.toString());
      Enumeration enum = words.elements();
      int count=1;
      RHLexEntry entry=null;
      //System.out.println("***Entries len="+entries.length+" maxLength="+maxLength);
      for (int i=entries.length-1;i>=entries.length-maxLength && i>=0;i--) {
	buf=new StringBuffer().append(entries[i].getWord()).append(tab).append(entries[i].getWeight()).append(newline);
	//if (i>40) System.out.println("**"+i+">"+entries[i].getWord()+" w:"+entries[i].getWeight());
	fp.write(buf.toString().getBytes());
      }

      fp.close();
    } catch (IOException ex) {
      System.out.println("Could not create LEXICON Text file:"+file.toString());
    }
  }


  public boolean readTextFile(StringBuffer filename) {
    boolean status=true;
    //System.out.print("**Reading text file for "+file.toString());
    File file=new File(filename.toString());
    StringBuffer buf=null;
    int num=1;
    BufferedReader dataInput=null;
    flushWords();
    String line=null, word=null, matchedStr="", newsep="", str=null;
    int startidx=0, idx=0, freq=0, vectoritem=0, numberMatched=0;
    Double weight=null;
    Vector sentences, locations, matchLocations, matchConcepts;
    boolean matched=false;
    String separator=RH_GlobalVars.rhFieldSeparator, subseparator=RH_GlobalVars.rhFieldSubSeparator;
    try {
      dataInput=new BufferedReader(new FileReader(file));
      RHLexEntry entry=null;
      while ((line=dataInput.readLine())!=null) {
	//System.out.println("========================");
	//** Get word
	startidx=line.indexOf(separator);
	word=line.substring(0,startidx);
	//System.out.println("word:"+word+" startidx="+startidx);
	//** Get frequency
	idx=line.indexOf(separator,startidx+1);
	freq=Integer.parseInt(line.substring(startidx+1,idx));
	//System.out.println("freq:"+freq+" startidx="+startidx+" idx="+idx);
	//** Get weight
	startidx=line.indexOf(separator,idx+1);
	weight=new Double((String)line.substring(idx+1,startidx));
	//System.out.println("weight:"+weight.doubleValue()+" startidx="+startidx+" idx="+idx);
	//** Get Match flag
	idx=line.indexOf(separator,startidx+1);
	matchedStr=line.substring(startidx+1,idx);
	sentences=new Vector(); locations=new Vector(); matchLocations=new Vector(); matchConcepts=new Vector();
	//System.out.println("**About to make vectors...startidx="+startidx+" idx="+idx);
	//System.out.println("---:"+line.substring(startidx+1,line.length()));
	//** Get Sentence locations
	startidx=idx;
	
	for (int i=0, tmp=0; i<freq;i++) {
	  if (i<freq-1) newsep=subseparator;
	  else newsep=separator;
	  idx=line.indexOf(newsep,startidx+1);
	  vectoritem=Integer.parseInt(line.substring(startidx+1,idx));
	  sentences.addElement(new Integer(vectoritem));
	  startidx=idx;
	}
	//System.out.println("**Sentences:"+sentences);
	//** Get Word locations
	for (int i=0, tmp=0; i<freq;i++) {
	  if (i<freq-1) newsep=subseparator;
	  else newsep=separator;
	  idx=line.indexOf(newsep,startidx+1);
	  vectoritem=Integer.parseInt(line.substring(startidx+1,idx));
	  locations.addElement(new Integer(vectoritem));
	  startidx=idx;
	}
	//System.out.println("**Locations:"+locations+" startidx="+startidx+" idx="+idx);
	if ((matched=matchedStr.equals(RH_GlobalVars.RH_MatchSymbol))) {
	  idx=line.indexOf(separator,startidx+1);
	  numberMatched=Integer.parseInt(line.substring(startidx+1,idx));
	  //System.out.println("***"+word+" Matched:"+numberMatched+" startidx="+startidx+" idx="+idx);
	  startidx=idx;
	  //** Get match locations
	  for (int i=0; i<numberMatched;i++) {
	    if (i<numberMatched-1) newsep=subseparator;
	    else newsep=separator;
	    //-- get the sentence location
	    idx=line.indexOf(subseparator,startidx+1);
	    vectoritem=Integer.parseInt(line.substring(startidx+1,idx));
	    //System.out.println("--item:"+vectoritem+" idx:"+idx+" startidx:"+startidx);
	    matchLocations.addElement(new Integer(vectoritem));
	    //--Get the concept name
	    startidx=line.indexOf(newsep,idx+1);
	    //System.out.println("--idx:"+idx+" startidx:"+startidx);
	    str=line.substring(idx+1,startidx);
	    matchConcepts.addElement(new String(str));
	    //startidx=idx;
	  }
	}
	//System.out.println("**MatchLocations:"+matchLocations+" concepts:"+matchConcepts);
	entry=new RHLexEntry(word,matched,freq,sentences,locations,matchLocations,matchConcepts,weight);
	//System.out.println("***Adding:"+entry.getWord()+" ["+entry.getMatched()+"] Freq:"+entry.getCount()+" S:"+entry.getSentences());
	addEntry(entry);
      }
      dataInput.close();
      //System.out.println("***DONE ... "+words.size()+" entries");
    } catch (IOException ex) {
      status=false;
      System.out.println("***ERROR: Could not READ LEXICON Text file:"+file.toString());
    }
    return status;
  }


    public void generateSimFile(StringBuffer file, Vector[] simArray, double percent) {
	//System.out.println(">..Generating Simialrity file for "+file.toString());
	StringBuffer buf=null, lenbuf=null;
	byte nl=(byte)'\n';
	byte[] newLine=new byte[1];
	newLine[0]=nl;
	String newline=new String(newLine);
	int count=0, i=0;
	String separator=RH_GlobalVars.rhFieldSeparator; //tab;
	String subseparator=RH_GlobalVars.rhFieldSubSeparator;
	double hival=0, lowval=0, per=0;
	try {
	    FileOutputStream fp=new FileOutputStream(file.toString());      
	    //System.out.println(">..Writing file:"+file.toString());
	    Vector vec=null;
	    Double val=null;
	    //** Grab the highest value
	    val=(Double)simArray[simArray.length-1].elementAt(1);
	    hival=val.doubleValue();
	    //** Create the lowest value and save only values that exceed (or equal) the lowest value
	    //lowval=hival*percent;
	    buf=new StringBuffer();
	    System.out.print("[hi="+hival+", @"+((int)(percent*100))+"%, ");
	    for (i=simArray.length-1;i>=0;i--) {
		vec=simArray[i];
		val=(Double)vec.elementAt(1);
		//** Do not allow negative numbers and check to see if value is good enough to keep
		if (val.doubleValue()>=percent) {
		    //System.out.print("{"+vec.elementAt(0)+","+val.doubleValue()+"}");
		    buf.append((String)vec.elementAt(0)).append(separator).append(val.doubleValue()).append(separator).
			append((int)(val.doubleValue()*100)).append(separator).append(newline);
		    count++;
		}
	    }
	    System.out.print(count+" matches]-"); 
	    lenbuf=new StringBuffer().append(count).append(newline);
	    lenbuf.append(buf);
	    fp.write(lenbuf.toString().getBytes());
	    fp.close();
	    //System.out.println(">..Done: "+i+" elements written to file");
	} catch (IOException ex) {
	    System.out.println("Could not create SIMILARITY Text file:"+file.toString());
	}
    }

    public Vector[] readSimFile(StringBuffer filename) {
	boolean status=true;
	System.out.print("**Reading SIM file for "+filename.toString());
	File file=new File(filename.toString());
	StringBuffer buf=null;
	BufferedReader dataInput=null;
	String line=null, key=null, matchedStr="";
	int size=0, startidx=0, idx=0, count=0, value=0;
	Double score=null;
	Vector[] simArray=null;
	Vector vec=null;
	boolean matched=false;
	String separator=RH_GlobalVars.rhFieldSeparator, subseparator=RH_GlobalVars.rhFieldSubSeparator;
	try {
	    dataInput=new BufferedReader(new FileReader(file));
	    RHLexEntry entry=null;
	    //** Grab number of entries
	    line=dataInput.readLine();
	    size=Integer.parseInt(line);
	    simArray=new Vector[size];
	    //System.out.println("*****Simfile contains "+size+" entries");
	    while ((line=dataInput.readLine())!=null && count<size) {
		//System.out.println("========================");
		//** Get document key
		startidx=line.indexOf(separator);
		key=line.substring(0,startidx);
		//System.out.print("key:"+key+" startidx="+startidx);
		//** Get score
		idx=line.indexOf(separator,startidx+1);
		score=new Double((String)line.substring(startidx+1,idx));
		//System.out.print(" score:"+score.doubleValue()+" idx="+idx);
		//** Get simialrity value
		startidx=line.indexOf(separator,idx+1);
		value=Integer.parseInt(line.substring(idx+1,startidx));
		//System.out.println(" value:"+value+" startidx="+startidx);
		vec=new Vector();
		vec.addElement(new String(key));
		vec.addElement(score);
		vec.addElement(new Integer(value));
		simArray[count++]=vec;
	    }
	    dataInput.close();
	    //System.out.println("***DONE ... "+words.size()+" entries");
	} catch (IOException ex) {
	    status=false;
	    System.out.println("***ERROR: Could not READ SIMILARITY Text file:"+file.toString());
	}
	System.out.println("*****SimArray contains "+count+" entries");
	return simArray;
    }


  public int getSize() {
    return words.size();
  }
  public Enumeration getElements() {
    return words.elements();
  }
  public Hashtable getWords() {
    return words;
  }
}

