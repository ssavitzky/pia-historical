/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: RH_StorageThread: implements a background thread which writes out information related to annotated file
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 03.02.98
 *
 */
package ricoh.rhpm;

import java.lang.*;
import java.util.*;
import java.io.*;

import ricoh.rh.RH_GlobalVars;

class RHStorageThread extends Thread {
    private RHPMAgent parent;
    private String key="", path="";
    private RHActiveConcept concept=null;
    private String rhInfoFileName=RH_GlobalVars.rhInfoFileName, rhHTMLFileName=RH_GlobalVars.rhHTMLFileName, rhIndexFileName=RH_GlobalVars.rhIndexFileName,
	rhInfoTempDir=RH_GlobalVars.rhInfoTempDir, rhGroupedFileName=RH_GlobalVars.rhGroupedFileName,  rhScoresFileName=RH_GlobalVars.rhScoresFileName,
	rhSummaryFileName=RH_GlobalVars.rhSummaryFileName, rhHTMLExtension=RH_GlobalVars.rhHTMLExtension, rhInfoFileExt=RH_GlobalVars.rhInfoFileExt;
    private String rhGroupedSummaryFile="", rhScoresSummaryFile="", rhSummaryFile="", rhPrivateFile="", rhPrivateInfoFile="", rhIndexFile="", currentURL="",
	documentTitle="", version="";
    private int threshold=0;
    
    RHStorageThread (RHPMAgent p, String newkey, RHActiveConcept newconcept) { 
	parent=p;
	key=newkey;
	concept=newconcept;
    }
    
    public void run (RHHistoryDB historyDB, RHCalendar rhCalendar, RHSimilarity rhSimilarity, RHStopWords sw, byte[] buffer, String newpath, 
		     String urlstr, String title, String ver, int thres) {
	System.out.println("==========> Running storage thread <===========");
	path=newpath;
	currentURL=urlstr;
	documentTitle=title;
	version=ver;
	threshold=thres;
	rhCalendar.setup(path,version,threshold);
	rhSimilarity.setup(path,version,threshold);
	//*** Write local document files
	writeLocalDocuments(historyDB,rhCalendar,rhSimilarity,buffer,path,urlstr,title,version);
	//*** Save History Database background thread
	historyDB.writeHistoryDB();
	//*** Save Link to calendar background thread
	if (concept!=null) rhCalendar.saveLink(key,concept.getShortName(),concept.getValue(),path,version,threshold);
	//***  Finally, determine if this document should go into the similarity document archive
	if (concept.getValue()>0) rhSimilarity.recordSimilarities(key,ver,threshold);
	yield();
	
	//** Generate lex file of words in document
	StringBuffer wordsfilename=null;
	wordsfilename=new StringBuffer().append(newpath).append(RH_GlobalVars.rhPathSeparator).append(RH_GlobalVars.rhDocumentDir).
	    append(RH_GlobalVars.rhPathSeparator).append(key).append(RH_GlobalVars.rhPathSeparator).
	    append(RH_GlobalVars.rhLexiconFileName).append(RH_GlobalVars.rhTextFileExtension);
	System.out.println("===> Generating lex:"+wordsfilename);
	RHDocumentLexicon documentLexicon=new RHDocumentLexicon(sw);
	documentLexicon.generateTextFile(wordsfilename);
	
	setToDone();
	System.out.println("==========> Storage thread Complete <===========");
    }
    
    private synchronized void setToDone() {
	notify();
    }


    //******** Write Various Documents Associated with Annotated DOcument ************

    public void writeLocalDocuments(RHHistoryDB historyDB, RHCalendar rhCalendar, RHSimilarity rhSimilarity, byte[] buffer, String path, 
				    String urlstr, String title, String version) {
	String newpath=path+RH_GlobalVars.rhPathSeparator+RH_GlobalVars.rhDocumentDir+RH_GlobalVars.rhPathSeparator;

	RHActiveConcept[] activeConcepts=parent.getActiveConcepts();
	RHSentenceSummary[] sentenceSummary=parent.getSentenceSummary();
	boolean success=false;

	System.out.println("===> Writing Documents: Path:"+path+" Key:"+key);
	rhGroupedSummaryFile=new String(rhGroupedFileName+rhHTMLExtension);
	rhScoresSummaryFile=new String(rhScoresFileName+rhHTMLExtension);
	rhSummaryFile=new String(rhSummaryFileName+rhHTMLExtension);
	rhPrivateFile=new String(rhHTMLFileName+rhHTMLExtension);
	rhIndexFile=new String(rhIndexFileName+rhHTMLExtension);
	rhPrivateInfoFile=new String(rhInfoFileName+rhInfoFileExt);
	// When donutment is done loading, generate summary documents and write local copy
	System.out.print("===>... info file...");
	if (writeLocalInfoCopy(key,urlstr,title,version,activeConcepts,sentenceSummary)) {
	    System.out.println("done");
	    System.out.print("===>... index file...");
	    success=writeIndexFile(key,newpath,rhCalendar);
	    if (success) System.out.println("done");
	    else System.out.println("FAILED");
	    System.out.print("===>... summary file...");
	    success=writeGroupedSummarySentences(key,newpath);
	    if (success) System.out.println("done");
	    else System.out.println("FAILED");
	    System.out.print("===>... scores file...");
	    success=writeScoresSummarySentences(key,newpath);
	    if (success) System.out.println("done");
	    else System.out.println("FAILED");
	    System.out.print("===>... straight summary file...");
	    success=writeStraightSummarySentences(key,newpath);
	    if (success) System.out.println("done");
	    else System.out.println("FAILED");
	    System.out.print("===>... local document...");
	    success=writeLocalCopy(key,buffer,newpath,title);      
	    if (success) System.out.println("done");
	    else System.out.println("FAILED");
	    
	    //*****TEST: write out a file containing sentence location information
	    //generateSentenceListing();
	    
 	    //System.out.println("===> Storage Task ready...");
	    //int idx=sortedConcepts[sortedConcepts.length-1].getIdx();
	    //	    RH_ActiveConcept concept=activeConcepts[idx];
	    //storageThread=new RH_StorageThread(this,currentDocumentKey,concept);
	    //storageThread.start();
	}
 	else System.out.println("FAILED");
	//System.out.println("===> DONE WRITING LOCAL DOCUMENTS");
    }

    /**
     * Write the local information files including a file which gives basic information on the document, the serialized version of
     * the activeConcepts and sentenceSummary arrays
     *
     *@param hashName name of document key
     *@param url url name of document we just annotated
     */
    private boolean writeLocalInfoCopy(String hashName, String url, String title, String version, RHActiveConcept[] activeConcepts,
				       RHSentenceSummary[] sentenceSummary) {
	boolean success=true;
	//System.out.println("=====> Writing LocalInfo:"+hashName+" "+url);
	FileOutputStream fp;
	int i=0;

	Date date=new Date();
	String newline=parent.getNewlineByte();
	StringBuffer objectFilename=null;
	StringBuffer buffer=new StringBuffer(RH_GlobalVars.rhInfoFileHeader).append(newline).append(title).append(newline)
	    .append(version).append(newline).append(url).append(newline).append(date.getTime()).append(newline)
	    .append(activeConcepts.length).append(newline).append(sentenceSummary.length).append(newline).append(parent.getAnnotationDurationStr()).append(newline);
	StringBuffer filename=new StringBuffer().append(path).append(RH_GlobalVars.rhPathSeparator).append(RH_GlobalVars.rhDocumentDir)
	    .append(RH_GlobalVars.rhPathSeparator).append(hashName).append(RH_GlobalVars.rhPathSeparator)
	    .append(RH_GlobalVars.rhInfoFileName).append(RH_GlobalVars.rhInfoFileExt);
	StringBuffer dirname=new StringBuffer().append(path).append(RH_GlobalVars.rhPathSeparator).append(RH_GlobalVars.rhDocumentDir)
	    .append(RH_GlobalVars.rhPathSeparator).append(hashName);
	File dir=null;
	//System.out.println("Saving local file: "+rhPrivateInfoFile);
	try {
	    // First, write the info.rhi file with basic info regarding this annotated document
	    //System.out.println("...creating directory: "+dirname.toString());
	    dir=new File(dirname.toString());
	    if (!dir.exists()) dir.mkdir();
	    //System.out.println("...creating directory WORKED:");
	    //else System.out.println("---Could not create directory for RHI:"+dir.toString());
	} catch (SecurityException exp) {
	    System.out.println("Security Exception: could not create directory"+dir.toString());
	    success=false;
	} catch (NullPointerException nexp) {
	    System.out.println("Null Exception: could not create directory"+nexp);
	    success=false;
	}
	if (success) {
	    try {
		fp=new FileOutputStream(filename.toString());
		fp.write(buffer.toString().getBytes());
		fp.close();
		//System.out.println("=====>>created "+ filename.toString());
	    } catch (IOException exp) {
		System.out.println("IO Exception: could not write file:"+filename.toString());
		success=false;
	    }
	    // Now write active concepts to flat file
	    try {
		objectFilename=new StringBuffer().append(path).append(RH_GlobalVars.rhPathSeparator).append(RH_GlobalVars.rhDocumentDir)
		    .append(RH_GlobalVars.rhPathSeparator).append(hashName).append(RH_GlobalVars.rhPathSeparator)
		    .append(RH_GlobalVars.rhInfoConceptsFileName).append(RH_GlobalVars.rhInfoFileExt);
		fp=new FileOutputStream(objectFilename.toString());
		buffer=new StringBuffer().append(activeConcepts.length).append(newline);
		for (i=0;i<activeConcepts.length;i++) {
		    buffer.append(activeConcepts[i].getShortName()+" ").append(activeConcepts[i].getValue()).append(newline);
		}
		fp.write(buffer.toString().getBytes());
		fp.close();
		//System.out.println("=====>>created "+ objectFilename.toString());
	    } catch (IOException exp) {
		System.out.println("IO Exception: could not write concepts object:"+objectFilename);
		success=false;
	    }
	    // Now serialize sentenceSummary so that when we reload this document, we will have the active sentence information
	    try {
		objectFilename=new StringBuffer().append(path).append(RH_GlobalVars.rhPathSeparator).append(RH_GlobalVars.rhDocumentDir)
		    .append(RH_GlobalVars.rhPathSeparator).append(hashName).append(RH_GlobalVars.rhPathSeparator).append(RH_GlobalVars.rhSSFileName)
		    .append(RH_GlobalVars.rhInfoFileExt);
		fp=new FileOutputStream(objectFilename.toString());
		buffer=new StringBuffer();
		//System.out.println(">>>Sentences to write: "+ sentenceSummary.length);
		for (i=0;i<sentenceSummary.length && sentenceSummary[i]!=null;i++) {
		    buffer.append(sentenceSummary[i].getSentence()).append(RH_GlobalVars.rhFieldSeparator).
			append(sentenceSummary[i].getConcept().getShortName()).append(RH_GlobalVars.rhFieldSeparator).
			append(sentenceSummary[i].getSentenceNumber()).append(RH_GlobalVars.rhFieldSeparator).
			append(sentenceSummary[i].getTopicNumber()).append(RH_GlobalVars.rhFieldSeparator).
			append(sentenceSummary[i].getOverallSentenceNumber()).append(RH_GlobalVars.rhFieldSeparator).
			append(sentenceSummary[i].getBegin()).append(RH_GlobalVars.rhFieldSeparator).
			append(sentenceSummary[i].getEnd()).append(RH_GlobalVars.rhFieldSeparator).
			append(newline);
		    //System.out.println(i+">>"+sentenceSummary[i].getSentenceNumber());
		}
		StringBuffer newbuf=new StringBuffer().append(i).append(newline).append(buffer);
		fp.write(newbuf.toString().getBytes());
		fp.close();
		//System.out.println("=====>>created "+ objectFilename.toString());
	    } catch (IOException ex) {
		System.out.println("IO Exception: could not write summary file:"+objectFilename);
		success=false;
	    }
	}
	//System.out.println("===> Done writing Local Info Copy");
	return success;
    }
    
    /**
     * Writes a local copy of the annotated file for the user and stores it in their private directory
     *
     *@param hashName name of document key
     *@param buffer byte buffer containing annotated document 
     */
    private boolean writeLocalCopy(String hashName,byte[] buffer, String path, String title) {
	StringBuffer filename=new StringBuffer(path).append(hashName).append(RH_GlobalVars.rhPathSeparator).append(rhHTMLFileName).append(rhHTMLExtension);
	//System.out.println("===> WritingLocalCopy..."+filename.toString());

	boolean success=true;
	FileOutputStream fp;
	StringBuffer titleString=new StringBuffer().append("[Document]:");
	byte[] titleBuffer=new byte[titleString.length()];
	int titleHeaderLocation=0;
	//Look for the title string in this document so can add identifier to the title
	// have to convert string to upper because you don't know what you'll get: Title, title, TITLE, tITLe, etc.
	int idx=new String(buffer,0,buffer.length).toUpperCase().indexOf(RH_GlobalVars.HTML_TITLE_Tag);
	if (idx>=0) {
	    titleHeaderLocation=idx+RH_GlobalVars.HTML_TITLE_Tag.length(); // add the tag length to position correctly
	    titleBuffer=titleString.toString().getBytes();
	}
	// Did not find the tag in upper case so ...; 12-22-97: note that if the doc has no title i do *NOTHING* - change this someday
	else titleHeaderLocation=-1;
	
	//System.out.println("Writing LOCAL COPY:"+rhPrivateFile);
	int len=buffer.length;
	try {
	    //fp=new FileOutputStream(rhPrivateFile);
	    fp=new FileOutputStream(filename.toString());
	    if (titleHeaderLocation>=0) {
		fp.write(buffer,0,titleHeaderLocation); // write upto the title
		fp.write(titleBuffer.toString().getBytes(),0,titleBuffer.toString().length()); // add the title identifier
		fp.write(buffer,titleHeaderLocation,len-titleHeaderLocation); // now write the rest of the buffer
	    }
	    else fp.write(buffer,0,len);
	    fp.close();
	} catch (IOException ex) {
	    success=false;
	    System.out.println("IO Exception: cold not open local URL file for writing");
	}
	//System.out.println("===> Done writing Local Copy");
	return success;
    }


    /**
     * Writes the index file which is provides the pointer to all summaries and the calendar for
     * an annotated document. This is the document shown when you select the summary button from
     * the toolbar.
     *
     *@param hashName is the document key
     */
    private boolean writeIndexFile(String hashName, String path, RHCalendar rhCalendar) {
	FileOutputStream fp;
	Calendar cal=new GregorianCalendar();
	int month=cal.get(Calendar.MONTH), year=cal.get(Calendar.YEAR);
	String monthstr=rhCalendar.getMonthString(month);
	// Make the mont url for use in the title
	StringBuffer filename=new StringBuffer().append(".."+RH_GlobalVars.rhPathSeparator).append(".."+RH_GlobalVars.rhPathSeparator)
	    .append(RH_GlobalVars.rhCalendarDir).append(RH_GlobalVars.rhPathSeparator).append(year).append(RH_GlobalVars.rhPathSeparator)
	    .append(monthstr).append(RH_GlobalVars.rhPathSeparator).append(RH_GlobalVars.rhIndexFileName).append(RH_GlobalVars.rhHTMLExtension);
	
	boolean success=true;
	int lastStart=0, sNum=1, idx=0;
	boolean firstTime=false;
	String newline=parent.getNewlineByte();
	StringBuffer buf=makeTitleString("[Index]:",filename.toString()).append(newline).append(newline).append(newline);
	try {
	    filename=new StringBuffer(path).append(hashName).append(RH_GlobalVars.rhPathSeparator).append(rhIndexFileName).append(rhHTMLExtension);
	    fp=new FileOutputStream(filename.toString());
	    fp.write(buf.toString().getBytes());
	    buf=new StringBuffer().append("<h4>Summary Documents</h4>"+newline+"<ul>").append(newline).append
		//*** Include summary types here ***
		("<li><a href=\"").append(rhGroupedSummaryFile).append("\">Grouped summary:</a>").append(RH_GlobalVars.RH_GroupedSummaryText).append(newline).append
		("<li><a href=\"").append(rhSummaryFile).append("\">Linear summary:</a>").append(RH_GlobalVars.RH_LinearSummaryText).append(newline).append
		("<li><a href=\"").append(rhScoresSummaryFile).append("\">Scores summary:</a>").append(RH_GlobalVars.RH_GroupedSummaryText).append(newline).append
		//*** End of summary types       ***
		("</ul>");
	    buf.append(makeEndSummaryTitle());
	    fp.write(buf.toString().getBytes());
	    //fp.write(makeEndSummaryTitle().toString().getBytes());
	    fp.close();
	} catch (IOException ex) {
	    success=false;
	    System.out.println("IO Exception: could not open file:"+rhIndexFileName);
	}
	return success;
    }


    /**
     * Writes the grouped (by concept) summary file for an annotated document
     *
     *@param hashName is the document key
     */
    private boolean writeGroupedSummarySentences(String hashName, String path) {
	FileOutputStream fp;
	boolean success=true;
	int lastStart=0, sNum=1, idx=0;
	boolean firstTime=false;
	String newline=parent.getNewlineByte();
	//System.out.println("$$$$$$Title:"+documentTitle);
	StringBuffer buf=makeSummaryTitleString(1,"[Grouped]:");

	RHSortableConcept[] sortedConcepts=parent.getSortedConcepts();
	RHActiveConcept[] activeConcepts=parent.getActiveConcepts();
	RHSentenceSummary[] sentenceSummary=parent.getSentenceSummary();

	try {
	    StringBuffer filename=new StringBuffer(path).append(hashName).append(RH_GlobalVars.rhPathSeparator).append(rhGroupedFileName).append(rhHTMLExtension);
	    fp=new FileOutputStream(filename.toString());
	    buf.append(newline).append(newline).append(newline);
	    fp.write(buf.toString().getBytes());
	    //fp.write(newline.getBytes());   fp.write(newline.getBytes());    fp.write(newline.getBytes());
	    for (int i=activeConcepts.length-1;i>=0;i--,sNum=1) {
		idx=sortedConcepts[i].getIdx();
		RHActiveConcept concept=activeConcepts[idx];
		if (activeConcepts[idx]!=null && concept.getValue()>0) {
		    buf=new StringBuffer().append("<h4><img src=\"").append("..").
		            append(RH_GlobalVars.rhPathSeparator).append(RH_GlobalVars.rhLocalGIFPath).append("/blue-ball-small.gif\">").
     		            append(concept.getName()).append("  -- Score: ").append(concept.getValue()).append("%</h4><ul><font>").append(newline);
		    fp.write(buf.toString().getBytes());
		    int numberTopics=activeConcepts[i].getLength();
		    // find all relevant sentences pertaining to the current concept
		    for (int j=0;j<sentenceSummary.length && sentenceSummary[j]!=null;j++) {
			RHSentenceSummary sentence=sentenceSummary[j];
			if (sentence.getConcept()==concept) {
			    buf=new StringBuffer().append("<li><a href=\"").append(rhPrivateFile).
			    append("#").append(RH_GlobalVars.RH_ANOH_Tag).append(sentence.getSentenceNumber()).append("\">[").append(sNum).append("] Sentence ").
			    append(j).append(" of ").append(parent.getSentenceSummaryLength()).append("</a>: ").
			    append(sentence.getSentence()).append("</li>"+newline);
			    fp.write(buf.toString().getBytes());
			    sNum++;
			}
		    }
		    fp.write(new String(newline+"</ul></font>"+newline+newline).getBytes());
		}
	    }
	    buf=new StringBuffer().append(newline).append(newline).append(newline).append(makeEndSummaryTitle().toString());
	    //fp.write(newline.getBytes());    fp.write(newline.getBytes());    fp.write(newline.getBytes());
	    fp.write(buf.toString().getBytes());
	    //fp.write(makeEndSummaryTitle().toString().getBytes());
	    fp.close();
	} catch (IOException ex) {
	    success=false;
	    System.out.println("IO Exception: could not open file:"+rhGroupedSummaryFile);
	}
	return success;
    }
    
    /**
     * Writes the scores summary (by keyword phrase) for the annotated document
     *
     *@param hashName is the document key
     */
    private boolean writeScoresSummarySentences(String hashName, String path) {
	FileOutputStream fp;
	boolean success=true;
	int lastStart=0, sNum=1, idx=0;
	boolean firstTime=false;

	RHSortableConcept[] sortedConcepts=parent.getSortedConcepts();
	RHActiveConcept[] activeConcepts=parent.getActiveConcepts();
	RHSentenceSummary[] sentenceSummary=parent.getSentenceSummary();
	
	String newline=parent.getNewlineByte();
	StringBuffer buf=makeSummaryTitleString(3,"[Scores]:").append(newline).append(newline);

	try {
	    StringBuffer filename=new StringBuffer().append(path).append(hashName).append(RH_GlobalVars.rhPathSeparator).append(rhScoresFileName).append(rhHTMLExtension);
	    fp=new FileOutputStream(filename.toString());
	    //System.out.println(">> Writing GROUPED SUMMARY: "+rhGroupedSummaryFile);
	    fp.write(buf.toString().getBytes());
	    //fp.write(newline.getBytes());
	    //fp.write(newline.getBytes());
	    for (int i=activeConcepts.length-1;i>=0;i--) {
		idx=sortedConcepts[i].getIdx();
		if (activeConcepts[idx]!=null && activeConcepts[idx].getValue()>0) {
		    buf=new StringBuffer().append("<h4><img src=\"").append("..").append(RH_GlobalVars.rhPathSeparator).append(RH_GlobalVars.rhLocalGIFPath)
			.append("/blue-ball-small.gif\">")
			.append(activeConcepts[idx].getName()).append("  -- Score: ").append(activeConcepts[idx].getValue()).append("%</h4><ul>")
			.append(newline);
		    fp.write(buf.toString().getBytes());
		    firstTime=true;
		    for (int j=0;j<activeConcepts[idx].topics.length;j++,firstTime=true) {
			for (int k=0;k<activeConcepts[idx].topics[j].scoresPtr;k++) {
			    if (activeConcepts[idx].topics[j].scores[k]>0) {
				if (firstTime) {
				    fp.write(new StringBuffer().append("<li><font>").append(activeConcepts[idx].topics[j].getName()).append(": ").toString().getBytes());
				    firstTime=false;
				}
				buf=new StringBuffer().append(newline).append("<a href=\"").append(rhPrivateFile).append("#").append(RH_GlobalVars.RH_ANOH_Tag)
				    .append(activeConcepts[idx].topics[j].sentences[k]).append("\">[").append((k+1)).append(" Score:")
				    .append((int)(activeConcepts[idx].topics[j].scores[k]*100)).append("]</a> |");
				fp.write(buf.toString().getBytes());
			    }
			}
			fp.write(new StringBuffer().append(newline).append("</li></font>").append(newline).toString().getBytes());
		    }
		    fp.write(new StringBuffer().append(newline).append("</ul>").append(newline).toString().getBytes());
		}
	    }
	    buf=new StringBuffer().append(newline).append(newline).append(newline).append(makeEndSummaryTitle().toString());
	    //fp.write(newline.getBytes());    fp.write(newline.getBytes());    fp.write(newline.getBytes());
	    //fp.write(makeEndSummaryTitle().toString().getBytes());
	    fp.write(buf.toString().getBytes());
	    fp.close();
	} catch (IOException ex) {
	    success=false;
	    System.out.println("IO Exception: could not open file:"+rhGroupedSummaryFile);
	}
	return success;
    }

    /**
     * Writes the straight or linear summary for the document
     */
    private boolean writeStraightSummarySentences(String hashName, String path) {
	FileOutputStream fp;
	boolean success=true;
	int lastStart=0, sNum=1, numSentences=parent.getSentenceSummaryLength();

	String newline=parent.getNewlineByte();
	StringBuffer buf=makeSummaryTitleString(2,"[Linear]:").append(newline).append(newline).append(newline);
	StringBuffer filename=null;
	RHSortableConcept[] sortedConcepts=parent.getSortedConcepts();
	RHActiveConcept[] activeConcepts=parent.getActiveConcepts();
	RHSentenceSummary[] sentenceSummary=parent.getSentenceSummary();

	try {
	    filename=new StringBuffer().append(path).append(hashName).append(RH_GlobalVars.rhPathSeparator).append(rhSummaryFileName).append(rhHTMLExtension);
	    fp=new FileOutputStream(filename.toString());
	    //System.out.println(">> Writing STRAIGHT SUMMARY: "+rhSummaryFile);
	    fp.write(buf.toString().getBytes());
	    //fp.write(newline.getBytes());	fp.write(newline.getBytes());	fp.write(newline.getBytes());
	    for (int i=0;i<numSentences;i++) {
		RHSentenceSummary s=sentenceSummary[i];
		// Prevents duplicate sentences when we have multiple matches in one sentence
		//System.out.println(i+"> ["+s.getSentenceNumber()+"]:"+s.getSentence());
		if (s.getSentence()!=null) {
		    buf=new StringBuffer().append("<li><font><a href=\"").append(rhPrivateFile).
			append("#").append(RH_GlobalVars.RH_ANOH_Tag).append(s.getSentenceNumber()).append("\">[").append(sNum).append("] ").
			append(s.getTopicNumber()).append(" of ").append(s.getConcept().topics[s.getTopicIdx()].getFrequency()).
			append(":").append(s.getConcept().getName()).append("</a>: ").append(s.getSentence()).append("</a></font></li>"+newline+newline);
		    
		    //"("+s.getConcept().topics[s.getTopicIdx()].getScore(s.getTopicNumber())+")"+
		    fp.write(buf.toString().getBytes());
		    lastStart=s.getBegin();
		    sNum++;
		} 
	    }
	    buf=new StringBuffer().append(newline).append("</ul><center>").append(newline).append(makeEndSummaryTitle().toString());
	    fp.write(buf.toString().getBytes());
	    
	    fp.close();
	} catch (IOException ex) {
	    success=false;
	    System.out.println("IO Exception: could not open file:" +filename.toString());
	} 
	return success;
    }
    
    /**
     * Makes the title header information generated when the system is writing a summary file
     *
     *@param label type of document this header is being created for
     *@param calurl URL pointing to the date when this document was annotated
     */
    private StringBuffer makeTitleString(String label, String calurl) {
	Date date=new Date();
	String newline=parent.getNewlineByte();
	return new StringBuffer().append(RH_GlobalVars.RH_HTML_Doctype_Header).append(newline)
	.append(RH_GlobalVars.HTML_HTML_Tag).append(RH_GlobalVars.HTML_HEAD_Tag).append(newline)
	.append("<").append(RH_GlobalVars.RH_SummaryHeader_BeginTag).append(" ").append(RH_GlobalVars.RH_DocumentHeader_URLTag).append("=\"")
	.append(currentURL).append("\" ").append(RH_GlobalVars.RH_DocumentHeader_VERTag).append("=\"").append(version).append("\">")
	.append("<"+RH_GlobalVars.RH_GroupSummaryHeader_EndTag+">")
	.append(newline).append(RH_GlobalVars.HTML_TITLE_Tag).append(label).append(documentTitle).append(RH_GlobalVars.HTML_TITLE_Tag_End)
	.append(RH_GlobalVars.HTML_HEAD_Tag_End)
	.append(newline).append(RH_GlobalVars.HTML_BODY_Tag).append(newline)
	.append("<center><h3>Title: ").append(documentTitle).append("<br><font size=-1>Date: <a href=\"")
	.append(calurl).append("\">").append(date.toString()).append("</a><br>").append(newline)
	.append("Time: ").append(parent.getAnnotationDurationStr()).append("</font></h3>")
	.append(newline).append("<h5>Goto: ").append(newline).append("<a href=\"")
	.append(rhPrivateFile).append("\">").append("Private Document</a> or").append(newline)
	.append("<a href=\"").append(currentURL).append("\">").append("Original Document</a></h5></center><hr>")
	.append(newline);
    }

    /**
     * Produces a header for each summary document
     */
    private StringBuffer makeSummaryTitleString(int newidx, String label) {
	Date date=new Date();
	String newline=parent.getNewlineByte();
	int index=0, grouped=1, linear=2, scores=3, document=4;
	String indexStr, groupedStr, linearStr, scoresStr, documentStr, summaryTitle="NoTitle";
	
	if (newidx==index) {
	    indexStr=new StringBuffer().append("Index |").toString();
	    summaryTitle="Index Summary";
	}
	else indexStr=new StringBuffer().append("<a href=\"").append(rhIndexFile).append("\">Index</a> |").toString();
	if (newidx==grouped) {
	    groupedStr=new StringBuffer().append("Grouped |").toString();
	    summaryTitle="Grouped Summary";
	}
	else groupedStr=new StringBuffer().append("<a href=\"").append(rhGroupedSummaryFile).append("\">Grouped</a> |").toString();
	if (newidx==linear) {
	    linearStr=new StringBuffer().append("Linear |").toString();
	    summaryTitle="Linear Summary";
	}
	else linearStr=new StringBuffer().append("<a href=\"").append(rhSummaryFile).append("\">Linear</a> |").toString();
	if (newidx==scores) {      
	    scoresStr=new StringBuffer().append("Scores |").toString();
	    summaryTitle="Scores Summary";
	}
	else scoresStr=new StringBuffer().append("<a href=\"").append(rhScoresSummaryFile).append("\">Scores</a> |").toString();
	if (newidx==document) {
	    documentStr=new StringBuffer().append("Document |").toString();
	    summaryTitle="Document";
	}
	else documentStr=new StringBuffer().append("<a href=\"").append(rhPrivateFile).append("\">Document</a> |").toString();
	
	return new StringBuffer().append(RH_GlobalVars.RH_HTML_Doctype_Header).append(newline).append(RH_GlobalVars.HTML_HTML_Tag).append(RH_GlobalVars.HTML_HEAD_Tag).append(newline)
	.append("<").append(RH_GlobalVars.RH_SummaryHeader_BeginTag).append(" ").append(RH_GlobalVars.RH_DocumentHeader_URLTag).append("=\"")
	//.append(pastAnnotatedURL).append("\" ").append(RH_GlobalVars.RH_DocumentHeader_VERTag).append("=\"").append(makeVersion()).append("\">")
	.append(currentURL).append("\" ").append(RH_GlobalVars.RH_DocumentHeader_VERTag).append("=\"").append(version).append("\">")
	.append("<"+RH_GlobalVars.RH_SummaryHeader_EndTag+">")
	.append(newline).append(RH_GlobalVars.HTML_TITLE_Tag).append(label).append(documentTitle)
	.append(RH_GlobalVars.HTML_TITLE_Tag_End).append(RH_GlobalVars.HTML_HEAD_Tag_End)
	.append(newline).append(RH_GlobalVars.HTML_BODY_Tag).append("<center><font size=-1>Goto:").append(newline)
	.append(indexStr).append(newline)
	.append(groupedStr).append(newline)
	.append(linearStr).append(newline)
	.append(scoresStr).append(newline)
	.append(documentStr).append(newline)
	.append("</font><p>").append(newline).append("<h4>").append(summaryTitle).append("-").append(date.toString())
	.append("<br>Title: ").append(documentTitle).append("</h4></center><hr>");
    }
    
    /**
     * Produces the footer on each document
     */
    private StringBuffer makeEndSummaryTitle() {
	String newline=parent.getNewlineByte();
	return new StringBuffer().append("<hr><center>This file automatically generated by the Reader's Helper<img src=\"").
	append("..").append(RH_GlobalVars.rhPathSeparator).append("..").append(RH_GlobalVars.rhPathSeparator).
	append(RH_GlobalVars.rhLocalGIFPath).append(RH_GlobalVars.rhPathSeparator).append(RH_GlobalVars.rhANOHGIFFileName).append("\"> Agent</center>").append(newline).
	append(newline).append(RH_GlobalVars.HTML_BODY_Tag_End).append(RH_GlobalVars.HTML_HTML_Tag_End).append(newline);
    }
    
}

