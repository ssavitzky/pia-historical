/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 1.8.99
 *
 */
package ricoh.rhpm;

import crc.pia.PiaRuntimeException;
import crc.pia.GenericAgent;
import crc.pia.FormContent;
import crc.pia.Resolver;
import crc.pia.Agent;
import crc.pia.Pia;
import crc.pia.Transaction;
import crc.pia.Machine;
import crc.pia.Content;
import crc.pia.FileAccess;
import crc.pia.Headers;
import crc.pia.HTTPResponse;
import crc.content.GenericContent;
import crc.content.text.html;
import crc.content.text.annotatedhtml;

import crc.ds.Criterion;

import java.net.*;
import java.net.MalformedURLException;

import java.io.*;
import java.util.*;
import java.lang.String;

import w3c.www.http.HTTP;

import ricoh.rh.RH_GlobalVars;

public class RHPMAgent extends GenericAgent {
    private int documentCounter=0;
    private RHPatternMatcher patternMatcher;
    private RHStopWords stopWords;
    private RHHistoryDB historyDB;
    private RHSimilarity rhSimilarity;
    private RHCalendar rhCalendar;
    private boolean annotationMode=false;
    //private static String proxyTopLevelSymbol="c:"; // this is nada when using unix; use the drive name under windows
    // changed pg
    private static String proxyTopLevelSymbol=""; 
    // private static String proxyTopLevelDir="pia/Agents/RHPMAgent";  // name of top level path where profiles directory will be location
    // changed pg
    private static String proxyTopLevelDir="/home/sun_home/pia/src/java/ricoh";  // name of top level path where profiles directory will be location
    private static String proxyProfilesDir="profiles"; // name of subdirectory for profile information (each subdir inthis directory will be named for a user, e.g. "jamey")
    private static String rhprofileFile="profile.rh";
    private static String rhlocationsFile="locations.rh";
    private static String rhconceptsFile="concepts.rh";
    private static String rhgroupsFile="groups.rh";
    private String proxyfilepath=proxyTopLevelSymbol+RH_GlobalVars.rhPathSeparator+proxyTopLevelDir+RH_GlobalVars.rhPathSeparator+proxyProfilesDir,
	rhHistoryDBFileName=RH_GlobalVars.rhHistoryDBFileName, proxyName="", proxyPortStr="", proxyAgent="", proxyPath="",
	currentDocumentKey="", currentUser=null, currentPath="", userPath="", activeSentenceBuffer=null;
    private int proxyPort=8888;
    private byte[] annotationBuffer;

    // changed pg.  Made this global so I could return it
    public byte[] newbuf;
    
    public RHPMAgent() {
	super();
	String args[]=new String[0];   
	patternMatcher=new RHPatternMatcher();  // create new set of concepts based on a string passed from the client
	System.out.println("");
	stopWords=new RHStopWords(proxyfilepath+RH_GlobalVars.rhPathSeparator);
	//frame=new PSFrame(args);

	documentCounter=0;
	System.out.println("***");
	System.out.println("***-RHPM Agent starting..:"+proxyfilepath+" stopwords:"+stopWords.size());
	rhCalendar=new RHCalendar(this);
	rhSimilarity=new RHSimilarity(this);
	System.out.println("***-Ready-*");
	System.out.println("***");
    }

    /**
     * as input.
     * Test routine for running agent outside of PIA
     * 
     *@param currentUser the name of the user (login name, e.g. "jamey")
     *@param documentFileName the name of the HTML file you want to annotate, e.g. /foobar/home/jamey/mydocument.html
     *@param documentURL the url for the document.  i only require this for the test method; it really doesn't mean much here so make something up
     *@param conceptsList the list of concepts (use their shortnames), e.g. "NLP Agents Java ExpSys"
     *@param version the version of the s/w calling this method; not really used here so don't worry about the contents
     */
    public byte[] testMethod(String currentUser, byte[] documentContents, String documentURL, String conceptsList, String version) {
	System.out.println("=======================================================");
	System.out.println("=== STARTING TEST METHOD: "+documentURL);
	System.out.println("===");

	currentPath=proxyfilepath+RH_GlobalVars.rhPathSeparator+currentUser+RH_GlobalVars.rhPathSeparator+RH_GlobalVars.rhPrivateDirName;
	int similarityThreshold=50;
	
	/**
	 * Create the calendar and similarity objects; 
	 * - calendar is responsible for recording timeline transactions, i.e. the date when a document was annotated
	 * - simialrity is responsible for recording results of annotation in "similar" file tree; results for each concept go in concept-named file
	 */
	rhCalendar=new RHCalendar(this);
	rhSimilarity=new RHSimilarity(this);

	/**
	 * Create the history object which takes care of "remembering" what files we have already annotated.  This object
	 * uses the file "history.rh" in the user's private directory to maintain the list previously annotated documents.
	 */
 	historyDB=new RHHistoryDB(rhHistoryDBFileName,currentPath);
	
	//** Manually read concepts file using FileContents object which grabs the contents of a file and returns it as a string
	RHFileContents fc=new RHFileContents();
	//System.out.println("===> reading concepts: ");

	System.out.println("grab1: " + proxyfilepath+RH_GlobalVars.rhPathSeparator+currentUser+RH_GlobalVars.rhPathSeparator+rhconceptsFile); 
	String concepts=fc.grabFileContents(proxyfilepath+RH_GlobalVars.rhPathSeparator+currentUser+RH_GlobalVars.rhPathSeparator+rhconceptsFile); 
	//** Set concepts in pattern matcher (which holds the master copy of concepts in RHActiveConcept[] activeConcepts
	patternMatcher.setConcepts(concepts);

	//** Grab the contents of the HTML file you're testing this with

	// System.out.println("grab2: " + documentFileName);
	// String htmlBuffer=fc.grabFileContents(documentFileName);

	/**
	 * Here's the fun part: we start pattern matching by sending the contents of the html buffer, the list of concepts that are active
	 * the version and the url.  The result is a new byte buffer containing annotations.
	 */
	// byte[] newbuf=patternMatcher.matchConcepts(htmlBuffer.getBytes(),conceptsList,version,documentURL);
	
	// changed pg
	 newbuf=patternMatcher.matchConcepts(documentContents,conceptsList,version,documentURL);

	/**
	 * here we generate a new "key" (unique symbol) to associate with the annotated document.  this key is used to create
	 * the new subdirectory in the "documents" directory which will hold all files associated with this document.  
	 * e.g. key = "rhd24"
	 * If key is >=0 then we already have this item in our database
	 */
	int key=historyDB.checkHistory(documentURL);
	if (key>=0) {
	    currentDocumentKey=historyDB.generateNewDocumentKey(key);
	    historyDB.updateRecord(documentURL.toString());  // this updates the record since we just reannotated the document
	}
	else currentDocumentKey=historyDB.addRecord(documentURL.toString());       

	//** Call the setup routines for this objects to set the path, version and the similarity threshold value
	//rhCalendar.setup(currentPath,version,similarityThreshold);
	//rhSimilarity.setup(currentPath,version,similarityThreshold);

	//** For now, reinitialize the calendar each time through
	//System.out.println("===> Reinitalizing Calendar...");
	//for (int f=1;f<=12;f++) rhCalendar.writeCalendarMonth(1999,f);
	//rhCalendar.writeCalendarMonth(1999,1);
	
	//** Sort the concepts so they are in order and we can grab the one that did the best
	sortConcepts();
	System.out.println("===");
	//** Now grab the number 1 scoring concept so we can use it to generate some of the summary and calendar files
	RHActiveConcept concept=patternMatcher.getNumberOneConcept();
	if (concept!=null) System.out.println("===> #1 Concept: ["+concept.getName()+"] score="+concept.getValue()+"%");
	else System.out.println("====>> #1 Concept IS NULL <<===");
	System.out.println("===");

	//** Create the storage thread object which takes care of writing stuff off to disk after we are done processing
	RHStorageThread storage=new RHStorageThread(this,currentDocumentKey,concept);
	//** Start thread
	storage.run(historyDB,rhCalendar,rhSimilarity,stopWords,newbuf,currentPath,documentURL,"some title",version,similarityThreshold);
	System.out.println("===");
	System.out.println("=== TEST METHOD COMPLETED");
	System.out.println("=======================================================");
	// changed pg
	return newbuf;

    }

    /** NOTE:  THIS VERSION IS CALLED FROM RHPMMain.  It is standalone and uses a document file */
    public byte[] testMethod(String currentUser, String documentFileName, String documentURL, String conceptsList, String version) {
	System.out.println("=======================================================");
	System.out.println("=== STARTING TEST METHOD: "+documentFileName);
	System.out.println("===");

	currentPath=proxyfilepath+RH_GlobalVars.rhPathSeparator+currentUser+RH_GlobalVars.rhPathSeparator+RH_GlobalVars.rhPrivateDirName;
	int similarityThreshold=50;
	
	/**
	 * Create the calendar and similarity objects; 
	 * - calendar is responsible for recording timeline transactions, i.e. the date when a document was annotated
	 * - similarity is responsible for recording results of annotation in "similar" file tree; results for each concept go in concept-named file
	 */
	rhCalendar=new RHCalendar(this);
	rhSimilarity=new RHSimilarity(this);

	/**
	 * Create the history object which takes care of "remembering" what files we have already annotated.  This object
	 * uses the file "history.rh" in the user's private directory to maintain the list previously annotated documents.
	 */
 	historyDB=new RHHistoryDB(rhHistoryDBFileName,currentPath);
	
	//** Manually read concepts file using FileContents object which grabs the contents of a file and returns it as a string
	RHFileContents fc=new RHFileContents();
	//System.out.println("===> reading concepts: ");

	System.out.println("grab1: " + proxyfilepath+RH_GlobalVars.rhPathSeparator+currentUser+RH_GlobalVars.rhPathSeparator+rhconceptsFile); 
	String concepts=fc.grabFileContents(proxyfilepath+RH_GlobalVars.rhPathSeparator+currentUser+RH_GlobalVars.rhPathSeparator+rhconceptsFile); 
	//** Set concepts in pattern matcher (which holds the master copy of concepts in RHActiveConcept[] activeConcepts
	patternMatcher.setConcepts(concepts);

	//** Grab the contents of the HTML file you're testing this with

	System.out.println("grab2: " + documentFileName);
	String htmlBuffer=fc.grabFileContents(documentFileName);

	/**
	 * Here's the fun part: we start pattern matching by sending the contents of the html buffer, the list of concepts that are active
	 * the version and the url.  The result is a new byte buffer containing annotations.
	 */
	// byte[] newbuf=patternMatcher.matchConcepts(htmlBuffer.getBytes(),conceptsList,version,documentURL);
	
	// changed pg
	 newbuf=patternMatcher.matchConcepts(htmlBuffer.getBytes(),conceptsList,version,documentURL);
	//String bar=new String(newbuf,0,newbuf.length);

	/**
	 * here we generate a new "key" (unique symbol) to associate with the annotated document.  this key is used to create
	 * the new subdirectory in the "documents" directory which will hold all files associated with this document.  
	 * e.g. key = "rhd24"
	 * If key is >=0 then we already have this item in our database
	 */
	int key=historyDB.checkHistory(documentURL);
	if (key>=0) {
	    currentDocumentKey=historyDB.generateNewDocumentKey(key);
	    historyDB.updateRecord(documentURL.toString());  // this updates the record since we just reannotated the document
	}
	else currentDocumentKey=historyDB.addRecord(documentURL.toString());       

	//** Call the setup routines for this objects to set the path, version and the similarity threshold value
	//rhCalendar.setup(currentPath,version,similarityThreshold);
	//rhSimilarity.setup(currentPath,version,similarityThreshold);

	//** For now, reinitialize the calendar each time through
	//System.out.println("===> Reinitalizing Calendar...");
	//for (int f=1;f<=12;f++) rhCalendar.writeCalendarMonth(1999,f);
	//rhCalendar.writeCalendarMonth(1999,1);
	
	//** Sort the concepts so they are in order and we can grab the one that did the best
	sortConcepts();
	System.out.println("===");
	//** Now grab the number 1 scoring concept so we can use it to generate some of the summary and calendar files
	RHActiveConcept concept=patternMatcher.getNumberOneConcept();
	if (concept!=null) System.out.println("===> #1 Concept: ["+concept.getName()+"] score="+concept.getValue()+"%");
	else System.out.println("====>> #1 Concept IS NULL <<===");
	System.out.println("===");

	//** Create the storage thread object which takes care of writing stuff off to disk after we are done processing
	RHStorageThread storage=new RHStorageThread(this,currentDocumentKey,concept);
	//** Start thread
	storage.run(historyDB,rhCalendar,rhSimilarity,stopWords,newbuf,currentPath,documentURL,"some title",version,similarityThreshold);
	System.out.println("===");
	System.out.println("=== TEST METHOD COMPLETED");
	System.out.println("=======================================================");
	// changed pg
	return newbuf;

    } // End of testMethod called by RHPMMain

    
    // So far, this is not being called.  Leave where it is to see what happens....
 public void respond(Transaction request, Resolver res) throws PiaRuntimeException {
	System.out.println("***RHPM AGENT Responding...");
	String rhUserHeader=RH_GlobalVars.rhContentTypeUser, rhPrinterHeader=RH_GlobalVars.rhContentTypePrinter, rhBufferHeader="X-RH-Buffer", 
	    rhMsgHeader=RH_GlobalVars.rhContentTypeMsg, rhBufferLenHeader=RH_GlobalVars.rhContentTypeBufferLen, 
	    rhURLHeader=RH_GlobalVars.rhContentTypeURL, rhTitleHeader=RH_GlobalVars.rhContentTypeTitle,rhThresholdHeader=RH_GlobalVars.rhContentTypeThreshold,
	    rhConceptsHeader=RH_GlobalVars.rhContentTypeConcepts, rhVersionHeader=RH_GlobalVars.rhContentTypeClientVersion,
	    pingMsg=RH_GlobalVars.piaProxyMsgPing, matchMsg=RH_GlobalVars.piaProxyMsgMatchContent, sendprofileMsg=RH_GlobalVars.piaProxyMsgGetProfile, 
	    receiveprofileMsg=RH_GlobalVars.piaProxyMsgPutProfile, annotationOnMsg=RH_GlobalVars.piaProxyMsgAnnotationOn, 
	    annotationOffMsg=RH_GlobalVars.piaProxyMsgAnnotationOff,processContentMsg=RH_GlobalVars.piaProxyMsgProcessContent,
	    sendconceptsMsg=RH_GlobalVars.piaProxyMsgGetConcepts, receiveConceptsMsg=RH_GlobalVars.piaProxyMsgPutConcepts,
	    sendgroupsMsg=RH_GlobalVars.piaProxyMsgGetGroups, receivegroupsMsg=RH_GlobalVars.piaProxyMsgPutGroups, 
	    sendlocationsMsg=RH_GlobalVars.piaProxyMsgGetLocations, receivelocationsMsg=RH_GlobalVars.piaProxyMsgPutLocations,
	    sendConceptInfoMsg=RH_GlobalVars.piaProxyMsgGetConceptInfo,  receivethresholdMsg=RH_GlobalVars.piaProxyMsgPutThreshold,
	    storeresultsMsg=RH_GlobalVars.piaProxyMsgStoreResult, receivesensitivityMsg=RH_GlobalVars.piaProxyMsgPutSensitivity,
	    returnMsg="nada";
	int status=-1;

	if( !request.isRequest() ) return;
	URL url = request.requestURL();
	if( url == null ) return;
	
	String msg = null;
	String path   = url.getFile();
	String myname = name();
	String mytype = type();
	Agent agnt	  = this;
	int bufferLen=-1, start=0, end=0, coversheet=0, hardcopy=0, threshold=0, sensitivity=0;
	String user=null, printer=null, buffer=null, bufferLenStr=null, title=null, urlstr=null, concepts=null, 
	    hliteStyle=null, activeSentences=null, startpage=null, endpage=null, coversheetstr=null, hardcopystr=null;
	System.out.println("-----------ALL HEADERS:");
	System.out.println(request.headersAsString());
	System.out.println("-----------END HEADERS:");
	System.out.println("respond rhheader: " + rhUserHeader + " request header: " + request.hasHeader(rhUserHeader));
	if (request.hasHeader(rhUserHeader)) {
	    user=request.header(rhUserHeader);
	    msg=request.header(rhMsgHeader);

	    currentUser=user;
	    bufferLenStr=request.header(rhBufferLenHeader);
	    bufferLen=Integer.parseInt(bufferLenStr);
	    status=HTTP.ACCEPTED;
	    System.out.println("...myname-->"+user+" msg-->"+msg);
	    try {
		ByteArrayOutputStream output=new ByteArrayOutputStream(bufferLen);
		request.contentObj().writeTo(output);
		byte[] outputArray=output.toByteArray();
		output.close();
		System.out.println("...DONE Making new buffer...size "+output.size());

		//** What does the client want us to do?
		if (msg.equalsIgnoreCase(pingMsg)) {
		    version=request.header(rhVersionHeader);
		    proxyName=request.header(RH_GlobalVars.rhContentTypeProxyName);
		    proxyPortStr=request.header(RH_GlobalVars.rhContentTypeProxyPort);
		    proxyAgent=request.header(RH_GlobalVars.rhContentTypeProxyAgent);

		    proxyfilepath=proxyTopLevelSymbol+RH_GlobalVars.rhPathSeparator+proxyTopLevelDir+RH_GlobalVars.rhPathSeparator+proxyProfilesDir;
		    userPath=proxyfilepath+RH_GlobalVars.rhPathSeparator+currentUser+RH_GlobalVars.rhPathSeparator;
		    currentPath=userPath+RH_GlobalVars.rhPrivateDirName;
		    proxyPath=proxyProfilesDir+RH_GlobalVars.rhPathSeparator+currentUser+RH_GlobalVars.rhPathSeparator+RH_GlobalVars.rhPrivateDirName;
		    System.out.println("-->Received ping msg: user->"+proxyfilepath);
		    
		    //** Create history object for this user
		    historyDB=new RHHistoryDB(rhHistoryDBFileName,currentPath);
		    returnMsg="pong";
		}
		else if (msg.equalsIgnoreCase(processContentMsg)) {
		    //** if in annotaitonMode, process the document and return the results
		    if (annotationMode) {
			String conceptsList=request.header(rhConceptsHeader);
			urlstr=request.header(rhURLHeader);
			version=request.header(rhVersionHeader);
			title=request.header(rhTitleHeader);
			threshold=Integer.parseInt(request.header(rhThresholdHeader));
			sensitivity=Integer.parseInt(request.header(RH_GlobalVars.rhContentTypeSensitivity));
			System.out.println("-->Received match msg");
			System.out.println("-->"+urlstr);
			annotationBuffer=patternMatcher.matchConcepts(outputArray,conceptsList,version,urlstr);
			activeSentenceBuffer=patternMatcher.setActiveSentences(sensitivity);
			returnMsg=new String(annotationBuffer,0,annotationBuffer.length);
		    }
		    //** otherwise do nothing and send the buffer back
		    else returnMsg=new String(outputArray,0,outputArray.length);
		} 
		else if (msg.equalsIgnoreCase(receivesensitivityMsg)) {
		    sensitivity=Integer.parseInt(request.header(RH_GlobalVars.rhContentTypeSensitivity));
		    System.out.println("-->Received put sensitivity msg == "+sensitivity);
		    urlstr=request.header(rhURLHeader);
		    System.out.println("-->Checking URL: "+urlstr);
		    int key=historyDB.checkHistory(urlstr);
		    if (key>=0) {
			System.out.println("-->...Found document!");
			currentDocumentKey=historyDB.generateNewDocumentKey(key);
			// read concept info from key directry
			String tmppath=currentPath+RH_GlobalVars.rhPathSeparator+RH_GlobalVars.rhDocumentDir+RH_GlobalVars.rhPathSeparator+
			    currentDocumentKey+RH_GlobalVars.rhPathSeparator;
			//System.out.println("-->tmppath:"+tmppath);
			patternMatcher.readSentenceDataFile(tmppath);
			System.out.println("-->created sentence data...");
			returnMsg=patternMatcher.createConceptSentenceData();
			//System.out.println("-->Result: "+returnMsg);
		    }
		    else {
			System.out.println("-->...Document not found -- returning nada");
			returnMsg="";
		    }
		}
		else if (msg.equalsIgnoreCase(sendConceptInfoMsg)) {
		    System.out.println("-->Received send concept info msg");
		    returnMsg=patternMatcher.getConceptInfo();
		}
		else if (msg.equalsIgnoreCase(RH_GlobalVars.piaProxyMsgGetAnohFileConcepts)) {
		    System.out.println("-->Received ANOH concepts msg");
		    urlstr=request.header(rhURLHeader);
		    System.out.println("-->Checking URL: "+urlstr);
		    int key=historyDB.checkHistory(urlstr);
		    if (key>=0) {
			System.out.println("-->...Found document!");
			currentDocumentKey=historyDB.generateNewDocumentKey(key);
			// read concept info from key directry
			String tmppath=currentPath+RH_GlobalVars.rhPathSeparator+RH_GlobalVars.rhDocumentDir+RH_GlobalVars.rhPathSeparator+
			    RH_GlobalVars.RH_HistoryDirectoryName+key+RH_GlobalVars.rhPathSeparator;
			//System.out.println("-->tmppath:"+tmppath);
			returnMsg=patternMatcher.readConceptDataFile(tmppath);
			patternMatcher.activateConceptsWithScores(returnMsg);
			//System.out.println("-->return:"+returnMsg);
		    }
		    else {
			System.out.println("-->...Document not found -- returning nada");
			returnMsg="";
		    }
		}
		else if (msg.equalsIgnoreCase(storeresultsMsg)) {
		    System.out.println("-->Received store results msg");
		    urlstr=request.header(rhURLHeader);
		    version=request.header(rhVersionHeader);
		    title=request.header(rhTitleHeader);
		    threshold=Integer.parseInt(request.header(rhThresholdHeader));

		    //** If key is >=0 then we already have this item in our database			
		    int key=historyDB.checkHistory(urlstr);
		    if (key>=0) {
			currentDocumentKey=historyDB.generateNewDocumentKey(key);
			historyDB.updateRecord(urlstr.toString());  // this updates the record since we just reannotated the document
		    }
		    else {
			currentDocumentKey=historyDB.addRecord(urlstr.toString());
		    }
		    //** Sort the concepts so they are in order and we can grab the one that did the best
		    sortConcepts();
		    System.out.println("--");
		    //** Now grab the number 1 scoring concept so we can use it to generate some of the summary and calendar files
		    RHActiveConcept concept=patternMatcher.getNumberOneConcept();
		    if (concept!=null) System.out.println("--> #1 Concept: ["+concept.getName()+"] score="+concept.getValue()+"%");
		    else System.out.println("-->> #1 Concept IS NULL <<--");
		    System.out.println("--");
		    
		    //** Create the storage thread object which takes care of writing stuff off to disk after we are done processing
		    RHStorageThread storage=new RHStorageThread(this,currentDocumentKey,concept);
		    //** Start thread
		    System.out.println("-->Title: "+title);
		    storage.run(historyDB,rhCalendar,rhSimilarity,stopWords,annotationBuffer,currentPath,urlstr,title,version,threshold);

		    //** Should I do this?  I think i'm done with it...
		    annotationBuffer=null;
		}
		else if (msg.equalsIgnoreCase(sendprofileMsg)) {
		    System.out.println("-->Received send profile msg: "+userPath+rhprofileFile);
		    RHFileContents fc=new RHFileContents();

		    System.out.println("grab3: " + userPath+rhprofileFile);
		    returnMsg=fc.grabFileContents(userPath+rhprofileFile);
		    //currentPath=proxyfilepath+RH_GlobalVars.rhPathSeparator+currentUser+RH_GlobalVars.rhPathSeparator+RH_GlobalVars.rhPrivateDirName;
		}
		else if (msg.equalsIgnoreCase(receivethresholdMsg)) {
		    System.out.println("-->Received put simThreshold msg");
		    version=request.header(rhVersionHeader);
		    threshold=Integer.parseInt(request.header(rhThresholdHeader));
		    System.out.println("-->Setting up calendar and similarity (thres="+threshold+") objects...");
		}
		else if (msg.equalsIgnoreCase(receiveprofileMsg)) {
		    System.out.println("-->Received receive profile msg");
		    returnMsg="got your profile";
		}
		else if (msg.equalsIgnoreCase(sendconceptsMsg)) {
		    System.out.println("-->Received send concepts msg");
		    RHFileContents fc=new RHFileContents();

		    System.out.println("grab4: " + userPath+rhconceptsFile);
		    returnMsg=fc.grabFileContents(userPath+rhconceptsFile);
		    System.out.print("-->Setting up internal concept structure...");
		    patternMatcher.setConcepts(returnMsg);
		    System.out.println(patternMatcher.getNumberConcepts()+" initialized");
		    //System.out.println(returnMsg);
		}
		else if (msg.equalsIgnoreCase(sendgroupsMsg)) {
		    System.out.println("-->Received send groups msg");
		    RHFileContents fc=new RHFileContents();
		    System.out.println("grab5: " + userPath+rhgroupsFile);
		    returnMsg=fc.grabFileContents(userPath+rhgroupsFile);
		}
		else if (msg.equalsIgnoreCase(sendlocationsMsg)) {
		    System.out.println("-->Received send locations msg");
		    RHFileContents fc=new RHFileContents();

		    System.out.println("grab6: " + userPath+rhlocationsFile);
		    returnMsg=fc.grabFileContents(userPath+rhlocationsFile);
		}
		else if (msg.equalsIgnoreCase(annotationOnMsg)){
		    System.out.println("-->Received annotation ON msg");
		    annotationMode=true;
		}
		else if (msg.equalsIgnoreCase(annotationOffMsg)){
		    System.out.println("-->Received annotation OFF msg");
		    annotationMode=false;
		}
		else if (msg.equalsIgnoreCase(RH_GlobalVars.piaProxyMsgGetCalendar)) {
		    System.out.println("-->Received get calendar msg");

		    Calendar cal=new GregorianCalendar();
		    int month=cal.get(Calendar.MONTH), year=cal.get(Calendar.YEAR);
		    String monthstr=rhCalendar.getMonthString(month);
		    // http://caliente:8888/RHPMAgent/
		    StringBuffer filename=new StringBuffer(RH_GlobalVars.httpTypeTag).append(RH_GlobalVars.rhPathSeparator+RH_GlobalVars.rhPathSeparator).
			append(proxyName+":"+proxyPortStr).append(RH_GlobalVars.rhPathSeparator).append(proxyAgent).append(RH_GlobalVars.rhPathSeparator).
			append(proxyPath).append(RH_GlobalVars.rhPathSeparator).append(RH_GlobalVars.rhCalendarDir).
			append(RH_GlobalVars.rhPathSeparator).
			append(year).append(RH_GlobalVars.rhPathSeparator).append(monthstr).append(RH_GlobalVars.rhPathSeparator).
			append(RH_GlobalVars.rhIndexFileName).append(RH_GlobalVars.rhHTMLExtension);
		    System.out.println("-->Url: "+filename.toString());
		    //RHFileContents fc=new RHFileContents();
		    returnMsg=filename.toString();
		}
		else if (msg.equalsIgnoreCase(RH_GlobalVars.piaProxyMsgGetSimilar)) {
		    System.out.println("-->Received get similar msg");
		}
		else if (msg.equalsIgnoreCase(RH_GlobalVars.piaProxyMsgGetSummary)) {
		    System.out.println("-->Received get summary msg");
		}
		    /*
		  commBus.setLoadingNewIceDocument(urlstr);
		  //if (end==0) end=commBus.getNumPages();
		  if (coversheet==1) commBus.setUpCoverSheet(true);
		  else commBus.setUpCoverSheet(false);
		  if (hardcopy==1) commBus.setPrintMode(true,start,end);
		  else commBus.setPrintMode(false,start,end);
		  commBus.setConceptFooter(concepts);
		  //System.out.println("...Starting to process..."); 
		  commBus.startingToProcess(urlstr);
		*/
		documentCounter++;
		output.close();
		System.out.println("========================RHPMAgent: "+documentCounter+" requests processed=================");
		//commBus.loadDocAsByteBuffer(output.toByteArray());
	    } catch (crc.pia.ContentOperationUnavailable un) {
		System.out.println("***Err: Content unavailable");
	    }
	    catch (IOException ex) {
		System.out.println("***Err: Could not read stream");
	    }
	    
	    Content ct = new crc.content.text.StringContent(""+returnMsg);
	    Transaction response = new HTTPResponse( Pia.instance().thisMachine(),
						     request.fromMachine(),
						     ct, false);
	    //response.setHeader("Location", "torres:8888");
	    response.setStatus(status);
	    response.setContentLength( returnMsg.length() );
	    response.startThread();
	}
	else {
	    System.out.println("respond final else clause");
	    status=HTTP.NO_CONTENT;
	    msg="no content";
	    super.respond(request,res);
	}       
	
    }

    public void actOn(Transaction ts, Resolver res) throws PiaRuntimeException {

	// Just deal with responses
	if( ts.isRequest() ) {
	    System.out.println(" actOn type is Request...returning");
	    return;
	}

	// Do not want to annotate any local responses, such as
	// error messages
	if(ts.statusCode() != 200) {
	    System.out.println("actOn status not 200: " + ts.statusCode());
	    return;
	}

	// Get current content object from the transaction
	Content cont = ts.contentObj();

	// Only want to annotate documents whose content is a
	// subclass of html
	if(cont instanceof html) {

	    // Create a content object that will annotate the content
	    crc.content.text.annotatedhtml act = new crc.content.text.annotatedhtml(cont);
	    Content ct = act;

	    // Set the transaction's agent to this one
	    act.setAgent(this);

	    // Set the transaction's content object to the annotated
	    // content.  This will do the annotation.
	    ts.setContentObj(ct);
	}
    }
    
    /**
     * should we ignore this request?
     * for now ignore unless file exists
     */
    protected boolean ignore( Transaction request ){
	return false;
    }


    public RHActiveConcept findConcept(String name) {
	return patternMatcher.findConcept(name);
    }
    public int findConceptIndex(String name) {
	return patternMatcher.findConceptIndex(name);

    }
    public void sortConcepts() {
	patternMatcher.sortConcepts();
    }
    public RHSortableConcept[] getSortedConcepts() {
	return patternMatcher.getSortedConcepts();
    }
    public RHActiveConcept[] getActiveConcepts() {
	return patternMatcher.getActiveConcepts();
    }
    

    public String getNewlineByte() {
	byte nl=(byte)'\n';
	byte[] newLine=new byte[1];
	newLine[0]=nl;
	return new String(newLine);
    }

    /**
     * This method opens a private document's info.rhi file and grabs the title, returning it to the caller
     */
    public String getPrivateDocumentTitle(String name) {
	String doctitle="";
	StringBuffer infofile=new StringBuffer(currentPath).
	    append(RH_GlobalVars.rhPathSeparator).
	    append(RH_GlobalVars.rhDocumentDir).append(RH_GlobalVars.rhPathSeparator).append(name).append(RH_GlobalVars.rhPathSeparator).
	    append(RH_GlobalVars.rhInfoFileName).append(RH_GlobalVars.rhInfoFileExt);
	//System.out.println("DocumentTitle Path:"+infofile.toString());
	try {
	    BufferedReader input=new BufferedReader(new FileReader(infofile.toString()));
	    input.readLine();
	    doctitle=input.readLine(); // second line contains document title
	    input.close();
	} catch (IOException ex) {
	    System.out.println("Could not open info file:"+infofile);
	    doctitle="Error: could not open file";
	}
	return doctitle;
    }

    public String getAnnotationDurationStr() {
	return patternMatcher.getAnnotationDurationStr();
    }

    public RHSentenceSummary[] getSentenceSummary() {
	return  patternMatcher.getSentenceSummary();
    }
    public int getSentenceSummaryLength() {
	return patternMatcher.getSentenceSummaryLength();
    }

    /** Set matching criteria */
    public void initialize() {
	Pia.debug(this, "RHPMAgent initialized");
	// Criterion.toMatch("IsAgentRequest", false);
	Criterion.toMatch("IsAgentResponse", true);
	// Criterion.toMatch("IsHtml", true);
	super.initialize();
    }
}


