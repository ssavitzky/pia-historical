/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class:  RH_Similarity: determines and shows the similarity between concepts and documetn that have been
 * annotated in the past
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 01.20.98 - revised 02-27-98
 *
 */
package ricoh.rhpm;

import java.util.*;
import java.io.*;

import ricoh.rh.RH_GlobalVars;

public class RHSimilarity {
    
    private RHPMAgent parent;
    
    private String key="", version="", path="";
    private int simThreshold;
    
    public RHSimilarity(RHPMAgent p) {
	parent=p;
    }
    public void setup(String newpath, String ver, int thres) {
	path=newpath;
	version=ver;
	simThreshold=thres;
    }


  /**
   * determine which concepts this document should be classified with based on it's current performance; this method
   * creates a file or appends the existing file for each concept subdirectory where the concept has scored greater
   * than the current similarity threshold.  the file, similar.rhs, contains a log of documents that have scored well
   * with respect to a concept.  thus, we provide for the user a list of other documents that have also scored well 
   * for this concept.
   */
  public synchronized void recordSimilarities(String newkey, String ver, int thres) {
    key=newkey;
    //System.out.println("***RUNNING SIMILARITY THREAD...");
    boolean done=false;
    int idx=0;
    RHActiveConcept concept=null;
    String input="", filename="";
    Date date=new Date();
    String newline=parent.getNewlineByte();
    RHSortableConcept[] sortedConcepts=parent.getSortedConcepts();
    RHActiveConcept[] activeConcepts=parent.getActiveConcepts();

    version=ver;
    simThreshold=thres;

    for (int i=sortedConcepts.length-1;i>=0 && !done;i--) {
      idx=sortedConcepts[i].getIdx();
      concept=activeConcepts[idx];
      //** took out currentSimilarityThreshold because this can be used as filter when viewing; store all cases
      if (concept!=null && concept.getValue()>0) { 
	//*** Write an entry in each concept's similar file where the concept's value for this currentDocumentKey is > than the threshold
	try {
	  filename=new StringBuffer(path).append(RH_GlobalVars.rhPathSeparator).
	    append(RH_GlobalVars.rhSimilarDir).append(RH_GlobalVars.rhPathSeparator).append(concept.getShortName()).
	    append(RH_GlobalVars.rhPathSeparator).append(RH_GlobalVars.rhSimilarDir).append(RH_GlobalVars.rhSimilarExt).toString();
	  //commBus.consoleMsg(filename);
	  BufferedWriter fp=new BufferedWriter(new FileWriter(filename,true));
	  input=new StringBuffer().append(key).append(RH_GlobalVars.rhFieldSeparator).append(concept.getValue()).
	    append(RH_GlobalVars.rhFieldSeparator).append(date.toString()).append(newline).toString();
	  //commBus.consoleMsg(input);
	  fp.write(input,0,input.length());
	  fp.close();
	} catch (IOException ex) {
	  System.out.println("Could not create similar file:"+filename);
	}
      }
      else done=true;
    }
  }

  /*
  public void stop() {
  }
  */

  /**
   * This method will generate a set of similarly scoring documents given the name of a document from the user's
   * private archive.
   */
  public boolean generateSimilarities(String docName, String docTitle, String docURL) {
    System.out.println("---+> Similarities for doc:"+docName);
    boolean available=false;
    int idx=0, startidx=0, score=0, value=0, catcount=0;
    String shortname="", line="", name="", buffer="", titlebuffer="", scorebuffer="", localbuffer="";
    StringBuffer filename=null, simfile=null, filepath=null;
    Hashtable similarItems=new Hashtable();

    //RHSortableConcept[] sortedConcepts=parent.getSortedConcepts();
    RHActiveConcept[] activeConcepts=parent.getActiveConcepts();
    
    /*
    byte nl=(byte)'\n';
    byte[] newLine=new byte[1];
    newLine[0]=nl; 
    String newline=new String(newLine);
    */
    String newline=parent.getNewlineByte();
    filepath=new StringBuffer(path).append(RH_GlobalVars.rhPathSeparator).
      append(RH_GlobalVars.rhSimilarDir).append(RH_GlobalVars.rhPathSeparator);
    //** Gather list of topic concepts (exceeding currentSimilarityThreshold)
    for (int i=0;i<activeConcepts.length;i++, score=0, value=0) {
      shortname=activeConcepts[i].getShortName();
      value=activeConcepts[i].getValue();
      filename=new StringBuffer().append(filepath.toString()).append(shortname).append(RH_GlobalVars.rhPathSeparator).
	append(RH_GlobalVars.rhSimilarDir).append(RH_GlobalVars.rhSimilarExt);
      File file=new File(filename.toString());
      catcount=0;
      
      //** If value of concept is high enough and there is a similarity file, grab the contents
      if (value>=simThreshold && file.exists()) {
	try{
	  System.out.println("---->>Trying:"+activeConcepts[i].getShortName()+" value="+value);
	  BufferedReader input=new BufferedReader(new FileReader(filename.toString()));
	  while ((line=input.readLine())!=null) {
	    startidx=line.indexOf(RH_GlobalVars.rhFieldSeparator);
	    name=line.substring(0,startidx);
	    idx=line.indexOf(RH_GlobalVars.rhFieldSeparator,startidx+1);
	    score=Integer.parseInt(line.substring(startidx+1,idx));
	    if (!similarItems.containsKey(name) && score>=simThreshold && !name.equalsIgnoreCase(docName)) {
	      String newfile=new StringBuffer().append(".."+RH_GlobalVars.rhPathSeparator).append(name).append(RH_GlobalVars.rhPathSeparator).
		append(RH_GlobalVars.rhIndexFileName).append(RH_GlobalVars.rhHTMLExtension).toString();
	      String title=parent.getPrivateDocumentTitle(name);
	      //System.out.println("---->>Similar entry:"+name+" score="+score+": "+title);

	      titlebuffer=new StringBuffer().append(titlebuffer).
		  append("<TD>- <a href=\"").append(newfile).
		  append("\">").append(title).append("</a></TD>").append(newline).
		  append(localbuffer).append("<TD>").append(name).append("</TD>").append(newline).
		  append(scorebuffer).append("<TD>").append(score).append("%</TD></TR>").append(newline).append("<TR>").toString();
	      similarItems.put(name,titlebuffer);
	      catcount++;
	    }
	  }
	  buffer= new StringBuffer().append(buffer).append("<TR><TD VALIGN=TOP ROWSPAN=").append(catcount).append(">").
	      append("<b><font size=+1>").append(activeConcepts[i].getName()+":").
	    append("<br>Score: ").append(activeConcepts[i].getValue()+"%").append("</font></b></TD>").
	    append(newline).append(titlebuffer).append(newline).toString();
	  titlebuffer=localbuffer=scorebuffer="";  // reset buffers
	  input.close();
	} catch (IOException ex) {
	    System.out.println("Coulnd not open similar file:"+filename);
	}
      }
    }
    if (buffer!="") {
      try {
	//** Create a similarity file
	simfile=new StringBuffer(path).append(RH_GlobalVars.rhPathSeparator).
	  append(RH_GlobalVars.rhDocumentDir).append(RH_GlobalVars.rhPathSeparator).
	  append(docName).append(RH_GlobalVars.rhPathSeparator).append(RH_GlobalVars.rhSimilarDir).append(RH_GlobalVars.rhHTMLExtension);
	FileOutputStream fp=new FileOutputStream(simfile.toString());      
	System.out.println("---->>Writing file:"+simfile.toString());
	
	titlebuffer=new StringBuffer().append(RH_GlobalVars.RH_HTML_Doctype_Header).append(newline).append(RH_GlobalVars.HTML_HTML_Tag)
	  .append(RH_GlobalVars.HTML_HEAD_Tag).append(newline).append("<").append(RH_GlobalVars.RH_CalendarHeader_BeginTag)
	  .append(" ").append(RH_GlobalVars.RH_DocumentHeader_VERTag).append("=\"").append(version).append("\">")
	  .append("<"+RH_GlobalVars.RH_SummaryHeader_EndTag+">").append(newline).append(RH_GlobalVars.HTML_TITLE_Tag).append("Similar Documents")
	  .append(RH_GlobalVars.HTML_TITLE_Tag_End).append(RH_GlobalVars.HTML_HEAD_Tag_End).append(newline)
	  .append(RH_GlobalVars.HTML_BODY_Tag).append(newline)
	  .append("<center><h3>").append("Documents Similar in Content to:").append("<br><font size=-1>").append(newline)
	  .append("Title: "+docTitle).append("<br>")
	  .append("Local name: <a href=\"").append(RH_GlobalVars.rhIndexFileName).append(RH_GlobalVars.rhHTMLExtension)
	  .append("\">").append(docName).append("</a></font></h3></center><br>").append(newline)
	  .append("<TABLE BORDER=2 CELLPADDING=3 CELLSPACING=3 WIDTH=100%>").append(newline)
	  .append("<TR><TD width=15%><strong>Concept</strong></TD><TD width=70%><strong>Related Document</strong></TD>")
	  .append("<TD width=5%><strong>Score</strong></TD><TD width=5%><strong>Name</strong></TD></TR>").append(newline)
	  .append(buffer).toString();
	
	fp.write(titlebuffer.getBytes());
	titlebuffer= new StringBuffer().append(newline).append("</TABLE>").append(newline)
	  .append("<hr><center>This file automatically generated by the Reader's Helper")
	  .append("<img src=\".."+RH_GlobalVars.rhPathSeparator).append("..").append(RH_GlobalVars.rhPathSeparator)
	  .append(RH_GlobalVars.rhPathSeparator).append(RH_GlobalVars.rhLocalGIFPath).append(RH_GlobalVars.rhPathSeparator)
	  .append(RH_GlobalVars.rhANOHGIFFileName).append("\"> Agent using a "+simThreshold+"% document threshold.<br>")
	  .append("<font size=-1>Copyright (c) 1998 Ricoh Silicon Valley, Inc.<br>").append(newline).append("All rights reserved</font></center><p>")
	  .append(newline).append(RH_GlobalVars.HTML_BODY_Tag_End).append(RH_GlobalVars.HTML_HTML_Tag_End).append(newline).toString();
	fp.write(titlebuffer.getBytes());
	fp.close();
	available=true;
      } catch (IOException ex) {
	System.out.println("Coulnd not create similar file:"+simfile.toString());
      }
    }
    return available;
  }

}
