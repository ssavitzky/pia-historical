/** 
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: RH_UserLexicon: creates a lexicon for complete document collection
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 03.13.98
 *
 */
package ricoh.rhpm;

import java.io.*;
import java.util.*;
import java.lang.*;

import ricoh.rh.RH_GlobalVars;

class RHUserLexicon implements Serializable {
  private Hashtable words;
  // The number of words in the lexicon
  private int entries=0;

  RHUserLexicon () {
    words=new Hashtable();
  }
  /**
   * This is called after the lexicon is deserialized because the pointer to the RH_MainFrame object
   * is no longer valid
   */
  public void updateParent() {
  }

  public void addWord(String documentKey, RHLexEntry entry) {
    RHLexWord aword=null;
    if ((aword=(RHLexWord)words.get(entry.getWord()))==null) words.put(entry.getWord(), new RHLexWord(entry.getWord(), documentKey, entry.getCount()));
    else aword.incrementCounter(documentKey,entry.getCount());
  }

  /**
   * USed for adding words when reading information from lex text file
   */
  public void addWord(String newword, Double s1, Double s2, Hashtable keys) {
    words.put(newword, new RHLexWord(newword,s1,s2,keys));
  }

  /*
  public void generateTabDelimitedFile(StringBuffer file) {
    StringBuffer buf=null;
    String newline=mainFrame.getNewlineByte();
    byte tabbyte=(byte)'\t';
    byte[] tabbuf=new byte[1];
    tabbuf[0]=tabbyte;
    String tab=new String(tabbuf);
    try {
      FileOutputStream fp=new FileOutputStream(file.toString());      
      //System.out.println("---->>Writing file:"+file.toString());
	Enumeration enum = words.elements();
	int count=1;
	RHLexWord entry=null;
	int foo=0;
	while (enum.hasMoreElements()) {
	  entry=(RHLexWord)enum.nextElement();
	  buf=new StringBuffer().append(entry.getWord()).append(tab).append(entry.getCount()).append(tab);

	  Enumeration keyenum=entry.getKeys().elements();
	  while (keyenum.hasMoreElements()) {
	    RHLexEntry item=(RHLexEntry)keyenum.nextElement();
	    buf.append(item.getDocumentKey()).append(tab);
	  }
	  buf.append(newline);
	  fp.write(buf.toString().getBytes());
	}
	fp.close();
    } catch (IOException ex) {
      System.out.println("Could not create LEXICON TAB file:"+file.toString());
    }
  }
  */
  public boolean generateTextFile(StringBuffer file) {
    StringBuffer buf=null, subbuf=null;
    boolean success=true;
    String newline=getNewlineByte();
    String separator=RH_GlobalVars.rhFieldSeparator;
    String subseparator=RH_GlobalVars.rhFieldSubSeparator, dockey=null;
    int count=0;
    Integer doccount=null;
    Vector item=null;
    Enumeration keyenum=null;
    Enumeration enum = words.elements();
    RHLexWord entry=null;
    int foo=0;
    try {
	FileOutputStream fp=new FileOutputStream(file.toString());      
	//System.out.println("---->>Writing file:"+file.toString());
	buf=new StringBuffer().append(words.size()).append(newline);
	fp.write(buf.toString().getBytes());
	while (enum.hasMoreElements()) {
	  entry=(RHLexWord)enum.nextElement();
	  //** the "0" here is a place holder for a future mutual information score or ???
	  buf=new StringBuffer().append(entry.getWord()).append(separator).append(entry.getScore1()).append(separator).
	    append(entry.getScore2()).append(separator).
	    append(entry.getFrequency()).append(separator);

	  keyenum=entry.getKeys().elements();
	  subbuf=new StringBuffer();
	  count=0;
	  while (keyenum.hasMoreElements()) {
	    item=(Vector)keyenum.nextElement();
	    //** Each vector contains two items: documentKey and word frequency for this term in that document
	    dockey=(String)item.elementAt(0);
	    doccount=(Integer)item.elementAt(1);
	    //** Skip null words
	    if (dockey!=null) {
		subbuf.append(dockey).append(subseparator).append(doccount).append(separator);
		count++;
	    }
	  }
	  buf.append(count).append(separator).append(subbuf).append(newline);
	  fp.write(buf.toString().getBytes());
	}
	enum=null; item=null; keyenum=null;
	fp.close();
	System.gc();
    } catch (IOException ex) {
      success=false;
      System.out.println("Could not create LEXICON SEPARATOR file:"+file.toString());
    }
    return success;
  }

  public boolean readLexFile(StringBuffer filename) {
    File file=new File(filename.toString());
    boolean status=true;
    StringBuffer buf=null;
    int num=1;
    BufferedReader dataInput=null;
    flushWords();
    String line=null, word=null, matchedStr="", doc="";
    int gcRate=50;  // means every X words, call GC
    int startidx=0, idx=0, freq=0, vectoritem=0, numberMatched=0, numdocs=0, docfreq=0, count=0, interations=0;
    Double score1=null, score2=null;
    double percent=0;
    Vector sentences, locations, matchLocations, matchConcepts,docvector;
    boolean matched=false;
    String separator=RH_GlobalVars.rhFieldSeparator, subseparator=RH_GlobalVars.rhFieldSubSeparator, newsep="";
    boolean eightp=true, fivep=true, threep=true, tenp=true;
    Hashtable dochash=null;
    String[] doclines=null;
    try {
	dataInput=new BufferedReader(new FileReader(file));
	entries=Integer.parseInt(dataInput.readLine());
	doclines=new String[entries];
	idx=0;
	while ((line=dataInput.readLine())!=null) {
	    //System.out.println(".")
	    doclines[idx++]=line;
	}
	dataInput.close();
    } catch (IOException ex) {
	status=false;
	System.out.println("***ERROR: Could not Read LEXICON Text file:"+file.toString());
    }
    //Enumeration docenum=doclines.elements();
    System.out.println("***Starting to parse lines...len="+doclines.length);
    words=new Hashtable(doclines.length);
    System.gc();
    StringTokenizer strt=null, docptrs=null;
    for (int i=0;i<idx;i++) {
	strt=new StringTokenizer(doclines[i],separator);
	//System.out.println("========================");
	//System.out.println(i+":LN:"+doclines[i]+" tokens:"+ strt.countTokens());
	while (strt.hasMoreTokens()) {
	    word=(String)strt.nextToken();
	    //System.out.println("---"+word);
	    score1=new Double((String)strt.nextToken());
	    //System.out.println("---"+score1);
	    score2=new Double((String)strt.nextToken());
	    //System.out.println("---"+score2);
	    freq=Integer.parseInt((String)strt.nextToken());
	    //System.out.println("---"+freq);
	    numdocs=Integer.parseInt((String)strt.nextToken());
	    //System.out.println("---"+numdocs);
	    dochash=new Hashtable();
	    if (numdocs>0) {
		for (int j=0;j<numdocs;j++) {
		    doc=(String)strt.nextToken();
		    //System.out.println("--->"+doc);
		    docptrs=new StringTokenizer(doc,",");
		    doc=(String)docptrs.nextElement();
		    //System.out.println("---"+doc);
 		    docfreq=Integer.parseInt((String)docptrs.nextToken());
		    //System.out.println("---"+docfreq);
		    docvector=new Vector();
		    docvector.addElement(doc);
		    docvector.addElement(new Integer(docfreq));
		    dochash.put(doc,docvector);
		    //if (word.equalsIgnoreCase("sites") || word.equalsIgnoreCase("control")) System.out.print("["+i+":"+doc+","+docfreq+"] ");
		    //idx++;
		}
	    }
	}
	//if (numdocs>0) System.out.println("");
	addWord(word,score1,score2,dochash);
	count++;
	dochash=null;
	word=null;
	line=null;
	interations++;
	//** Every X words call the GC; if you call it everytime it takes forever to read the file
	if (count>=gcRate) { 
	    //System.gc(); 
	    count=0; 
	    percent=(double)interations/entries;
	    if (eightp && percent>=.8 && percent<=.81) { 
		System.out.print(".."+(int)(percent*100)+"%"); eightp=false;
		System.out.print("("+i+")");}
	    else if (fivep && percent>=.50 && percent<=.51) {
		System.out.print(".."+(int)(percent*100)+"%"); fivep=false;
		System.out.print("("+i+")");}
	    else if (threep && percent>=.30 && percent<=.31) {
		System.out.print(".."+(int)(percent*100)+"%"); threep=false;
		System.out.print("("+i+")");}
	    else if (tenp && percent>=.10 && percent<=.11) {
		System.out.print(".."+(int)(percent*100)+"%");
		System.out.print("("+i+")");
		tenp=false;
	    }
	}
    }
    System.out.println("-->DONE Making objects...");
    System.gc();
    return status;
  }
  
  public int getSize() {
    return words.size();
  }

  public void flushWords() {
    Enumeration enum = words.elements();
    // Do i really need to do this?  maybe because the gc is needs the help ????
    while (enum.hasMoreElements()) {
      RHLexWord entry=(RHLexWord)enum.nextElement();
      entry=null;
    }
    words.clear();
  }

  public RHLexWord checkWord(String wordstr) {
    RHLexWord aword=null;
    if ((aword=(RHLexWord)words.get(wordstr))!=null) return aword;
    else return null;
  }

    public void setWordLexSize(int s) {
	words=new Hashtable(s);
    }

    public String getNewlineByte() {
	byte nl=(byte)'\n';
	byte[] newLine=new byte[1];
	newLine[0]=nl;
	return new String(newLine);
    }

}





		  /**
		     //** Get word
			  startidx=line.indexOf(separator);
			  word=line.substring(0,startidx);
			  //System.out.println("word:"+word+" startidx="+startidx);
			  //** Get score
			       idx=line.indexOf(separator,startidx+1);
			       score1=new Double((String)line.substring(startidx+1,idx));
			       //** Get overall frequency
				    startidx=line.indexOf(separator,idx+1);
				    score2=new Double((String)line.substring(idx+1,startidx));
				    //System.out.println("score1:"+score1+" score2:"+score2+" startidx="+startidx+" idx="+idx);
				    
				    //** Get overall frequency
					 idx=line.indexOf(separator,startidx+1);
					 freq=Integer.parseInt(line.substring(startidx+1,idx));
					 //System.out.println("freq:"+freq+" startidx="+startidx+" idx="+idx);
					 //** Get number of documents to process
					      startidx=line.indexOf(separator,idx+1);
					      numdocs=Integer.parseInt(line.substring(idx+1,startidx));
					      //System.out.println("numdocs:"+numdocs+" startidx="+startidx+" idx="+idx);
					      idx=startidx;
	   */

		  /*
		    startidx=line.indexOf(subseparator,idx+1);
		    doc=line.substring(idx+1,startidx);
		    idx=line.indexOf(separator,startidx+1);
		    docfreq=Integer.parseInt(line.substring(startidx+1,idx));
		  */
