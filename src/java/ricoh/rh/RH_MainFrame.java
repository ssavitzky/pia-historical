/** 
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.icoh.com
 * All Rights Reserved
 *
 * RH_MainFrame Class: The main frame container within the ReadersHelper class
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 4.24.97 - revised 02-06-98
 *
 */
package ricoh.rh;

import java.io.*;

import java.lang.*;
import java.util.*;
import java.net.*;
import java.rmi.*;

import java.awt.*;
import java.awt.event.*; 

import javax.swing.*;

//import ricoh.portal.PortalConnect_Stub;
//import ricoh.portal.PortalConnect;

//import ricoh.rhps.PSConnect_Stub;
//import ricoh.rhps.PSConnect;

/**
 * This is the main window frame used in RH.  It contains many methods that are necessary when understanding 
 * how the system works.  It contains almost all of the GUI objects (except the toolbar) used in the system.
 *
 * @author <a href="mailto:jamey@rsv.ricoh.com">Jamey Graham</a>
 * @version 4.24.97 -revised 02-06-98
 */
public class RH_MainFrame extends Panel {
  public RH_GlobalVars globalVars=new RH_GlobalVars();   // not sure if i really even need to do this
  public ReadersHelper parent;
  public RH_CommBus commBus;
  public RH_MainToolbar mainToolbar;
  public RH_Profile profile;
  public RH_Location locationControl;
  public RH_Document documentControl;
  public RH_ConceptControl conceptControl;
  public RH_StatusControl statusControl;
  public RH_ThumbarControl thumbarControl;
    //public RH_HistoryDB rhHistoryDB;
    //public RH_Calendar rhCalendar;
    //public RH_Similarity rhSimilarity;
    //public RH_StorageThread storageThread;
    //public PortalConnect_Stub portalConnection;
    //public PSConnect_Stub psConnection;
  //** The documentLexiocn holds ALL words from the document; the documentSpecialLexicon is a test and holds only the woirds which
  //** are also part of the collections of words which make up the concept topics.
    //public RH_DocumentLexicon documentLexicon, documentSpecialLexicon;
  //public RH_UserLexicon userLexicon;
    //public RH_StopWords stopwords;

  //************* VARIABLES ***************

  //** COLORS
  public Color mainBackColor=Color.gray, mainTextColor=Color.white, mainHighlightColor=Color.lightGray,
    mainShadowColor=Color.black, mainShadowColor2=Color.gray;
  /* COLOR SCHEME 4:  */
  public final static Color defaultOverViewLineColor=Color.gray;
  public Color linkColor = Color.blue,
    textColor = Color.black,
    annotationColor = Color.black,
    lineAnnotationColor = Color.red,
    lineSentenceTagColor = defaultOverViewLineColor,
    lineEmailTagColor = Color.cyan,
    lineURLTagColor = Color.cyan,
    overviewBoldLineColor = Color.black,
    //highlightColor = Color.yellow, //new Color(240,225,255), // a cool looking green: new Color(51,204,153),
    highlightColor = new Color(255,255,128), //Color.yellow,
    boldUnderlineColor= Color.red,
    highlightShadowColor=Color.gray,
    backgroundColor = Color.lightGray, // default background of document
    sentenceColor=highlightColor; //Color(255,255,150); //new Color(240,225,255);

  //** HOME URL
  public String homeURL = "",  defaultHomeURL="", rhPreviousURL="", pastAnnotatedURL="";
  //** PATHS
  public String mainPath="", rhGIFPath="", privateANOHDir="";
  //** MISC Strings
  public String titleString=RH_GlobalVars.titleString, documentTitle="", pastVersion="",annotationDurationStr="",
    currentDocumentKey="", lastDocumentKey="", rhPrivateFile="", rhPrivateInfoFile="", rhIndexFile="", rhGroupedSummaryFile="", rhSummaryFile="", 
    rhScoresSummaryFile="",  rhInfoFileHeader=RH_GlobalVars.rhInfoFileHeader, rhReinstatedURL="";
  public final static String rhPathSeparator=RH_GlobalVars.rhPathSeparator;
  public final static String httpFileTypeTag=RH_GlobalVars.httpFileTypeTag, rhDefaultHomeURLName=RH_GlobalVars.rhDefaultHomeURLName,
    rhDocumentDir=RH_GlobalVars.rhDocumentDir,  rhCalendarDir=RH_GlobalVars.rhCalendarDir, rhSimilarDir=RH_GlobalVars.rhSimilarDir,
    rhInfoFileExt=RH_GlobalVars.rhInfoFileExt, rhHTMLExtension=RH_GlobalVars.rhHTMLExtension, rhSHTMLExtension=RH_GlobalVars.rhSHTMLExtension,
    rhCalendarExtension=RH_GlobalVars.rhCalendarExtension, rhSimilarExt=RH_GlobalVars.rhSimilarExt, NoHighConcept=RH_GlobalVars.NoHighConcept,
    rhFieldSeparator=RH_GlobalVars.rhFieldSeparator, rhHTMLIncludeExtention=RH_GlobalVars.rhHTMLIncludeExtention, 
    rhLocalGIFPath=RH_GlobalVars.rhLocalGIFPath, rhWeekViewFileName=RH_GlobalVars.rhWeekViewFileName, 
    rhWordBufferFileName=RH_GlobalVars.rhWordBufferFileName, rhHistoryDBFileName=RH_GlobalVars.rhHistoryDBFileName,
    rhInfoFileName=RH_GlobalVars.rhInfoFileName, rhHTMLFileName=RH_GlobalVars.rhHTMLFileName, rhIndexFileName=RH_GlobalVars.rhIndexFileName,
    rhInfoTempDir=RH_GlobalVars.rhInfoTempDir, rhGroupedFileName=RH_GlobalVars.rhGroupedFileName,  rhScoresFileName=RH_GlobalVars.rhScoresFileName,
    rhSummaryFileName=RH_GlobalVars.rhSummaryFileName, rhLexiconFileName=RH_GlobalVars.rhLexiconFileName,
    rhUserLexiconFileName=RH_GlobalVars.rhUserLexiconFileName,
    rhStopwordsFileName=RH_GlobalVars.rhStopwordsFileName, rhSimFileName=RH_GlobalVars.rhSimFileName,
    rhInfoConceptsFileName=RH_GlobalVars.rhInfoConceptsFileName, 
    rhCalendarDataFileName=RH_GlobalVars.rhCalendarDataFileName, rhTimeLineFileName=RH_GlobalVars.rhTimeLineFileName, 
    rhANOHGIFFileName=RH_GlobalVars.rhANOHGIFFileName, rhSSFileName=RH_GlobalVars.rhSSFileName;

  //** BOOLEANS
  public boolean forceLoad=false, hasBeenAnnotated=false, highlightWholeSentence=true, boldInHighlight=true, highlightDropShadow=true,
      underlineInHighlight=false,
    preAnnotated=false, locationListChanged=false, justAnnotatedThisDocument=false, keepRunning=false, useProximityMeasure=true,
      privateInfoFileExists=false, privateFileExists=false, privateGroupedSummaryFileExists=false, privateSummaryFileExists=false,
      privateIndexFileExists=false, privateScoresSummaryFileExists=false, needToReinstateConcepts=false, useUserLexicon=false;
  // **Flag for using sentenceLocation in probability measure; true turns it on; false off
  public boolean useSentenceLocation=true, usePortal=true, useLexicon=true;

  //** INTEGERS
  public int currentMode=RH_GlobalVars.plainTextMode, titleHeaderLocation=0, historyPointer=0;
  public int rhFileType=RH_GlobalVars.RH_FILETYPE_NULL, lastFileType=rhFileType, numSentences=0, numAllConcepts=0, numConcepts=0, currentSensitivity=0;
  public final static int MaxHistorySize=25, 
      //** Currently this is the maximum number of concepts a user can have
      maxConcepts=RH_GlobalVars.maxNumberConcepts,
      maxNumberKeywords=RH_GlobalVars.maxNumberKeywords,
      maxSentencesInSummary=RH_GlobalVars.maxSentencesInSummary;
  private int documentAnnotationType=RH_GlobalVars.RH_Annotate_Highlight;
  public int urlBufferSize, newPtr=0, annotationBufferSize=0,  currentSimilarityThreshold=0, documentSentenceCount=0;  

  //** LONGS
  private long annotationDuration=0;

  //** CONTAINERS & COLLECTIONS
  public RH_Concept[] allConcepts=new RH_Concept[maxConcepts], activeConcepts=null;
  public RH_SortableConcept[] sortedConcepts;
  public RH_LocationItem[] locationList, historyList;
  public RH_SentenceSummary[] sentenceSummary=new RH_SentenceSummary[maxSentencesInSummary];

  //** BYTE BUFFERS
  public byte[] urlBuffer, annotationBuffer;

  //** OTHER STUFF
  public final static String RH_HistoryDirectoryName=RH_GlobalVars.RH_HistoryDirectoryName;  
  private Hashtable docInfoCache;
    private StringBuffer activeSentencesBuffer=null;

  //** This is the URL for adding urls to RAWHO
  public static StringBuffer rhRAWHOHomeURLString=new StringBuffer().append("ookami.crc.ricoh.com:8888/RAWHO/");
  public static StringBuffer rhRAWHOAddURLString=new StringBuffer().append("add_url.if?id=addurl");
  public static StringBuffer rhRAWHOURLString=new StringBuffer().append("http://").append(rhRAWHOHomeURLString.toString())
       .append(rhRAWHOAddURLString.toString());

  //** This is the URL for adding urls to IM^3
  public static StringBuffer rhIM3HomeURLString=new StringBuffer().append("salmon.crc.ricoh.com:8001/");
  public static StringBuffer rhIM3AddURLString=new StringBuffer().append("cgi-bin/imws.cgi?");
  public static StringBuffer rhIM3UsernameString=new StringBuffer().append("username=");
  public static StringBuffer rhIM3SrcURLString=new StringBuffer().append("src_url=");

  public final static String profileFilename="profile.rh", conceptsFilename="concepts.rh",
    locationsFilename="locations.rh", groupsFilename="groups.rh";

  //************* END VARIBABLES ***********


  public RH_MainFrame (RH_CommBus bus, ReadersHelper newParent, RH_SplashScreen splash, boolean useProxy) {
    super();
    parent=newParent;
    commBus=bus;
    commBus.mainFrame=this;

    // Read user profile
    splash.newMsg("User profile");
    System.out.println("Reading User Profile...");

    /**
     * PIA 1.6.99 - grab contents of files to be passed to RH_Profile as string;  this will change when PIA integration takes place
     RH_FileContents fileContents=new RH_FileContents(this);
     String profileString=fileContents.grabFileContents("./"+profileFilename);
     String conceptsString=fileContents.grabFileContents("./"+conceptsFilename);
     String groupsString=fileContents.grabFileContents("./"+groupsFilename);
     String locationsString=fileContents.grabFileContents("./"+locationsFilename);
   */

      //** Create profile, loads proxy information
      String profileString=null, conceptsString=null, groupsString=null, locationsString=null, thresString=null;
      profile=new RH_Profile(this);
      if (!profile.successfulLoad()) {
	  System.out.println("****MAJOR PROBLEM: Profile files (profile.rh, locations.rh, or concepts.rh) not found...exiting");
	  System.exit(0);
      }

      if (useProxy) {
	  //** Check for the proxy and if it's not there, we cannot start up
	  if (commBus.requestProxyContent(RH_GlobalVars.piaProxyMsgPing)!=null) {
	      profileString=commBus.requestProxyContent(RH_GlobalVars.piaProxyMsgGetProfile);
	      locationsString=commBus.requestProxyContent(RH_GlobalVars.piaProxyMsgGetLocations);
	      groupsString=commBus.requestProxyContent(RH_GlobalVars.piaProxyMsgGetGroups);
	      conceptsString=commBus.requestProxyContent(RH_GlobalVars.piaProxyMsgGetConcepts);
	      //System.out.println("===================================================================");
	      //System.out.println(conceptsString);
	      //System.out.println("===================================================================");
	      //** if proxy laoded sucessfully, load the rest of the profile
	      profile.loadProfile(profileString, conceptsString, groupsString, locationsString);
	      profileString=conceptsString=groupsString=locationsString=null;
	      if (!profile.successfulLoad()) {
		  System.out.println("****MAJOR PROBLEM: Profile files (profile.rh, locations.rh, or concepts.rh) not found...exiting");
		  System.exit(0);
	      }
	      //thresString=commBus.requestProxyContent(RH_GlobalVars.piaProxyMsgPutThreshold);
	  }
	  else {
	      System.out.println("****MAJOR PROBLEM: Proxy Server not reachable - exiting...");
	      System.exit(0);
	  }
      }
      else {
	  RH_FileContents fc=new RH_FileContents();
	  String profilepath="c:/pia/Agents/RHPMAgent/profiles/"+profile.getProxyUserName()+"/";
	  profileString=fc.grabFileContents(profilepath+"profile.rh");
	  locationsString=fc.grabFileContents(profilepath+"locations.rh");
	  groupsString=fc.grabFileContents(profilepath+"groups.rh");
	  conceptsString=fc.grabFileContents(profilepath+"concepts.rh");
	  //** if proxy laoded sucessfully, load the rest of the profile
	  profile.loadProfile(profileString, conceptsString, groupsString, locationsString);
	  profileString=conceptsString=groupsString=locationsString=null;
	  if (!profile.successfulLoad()) {
	      System.out.println("****MAJOR PROBLEM: Profile files (profile.rh, locations.rh, or concepts.rh) not found...exiting");
	      System.exit(0);
	  }
      }
    setMotif(profile.getMotifNumber());
    mainPath=profile.getPath();
    rhGIFPath=profile.getGifsPath();
    //defaultImageFilename=rhGIFPath+"/image.gif";
    homeURL=profile.getHomeURL();
    privateANOHDir=profile.getPrivateDir();
    currentSimilarityThreshold=profile.getSimilarityThreshold();
    currentSensitivity=profile.getSensitivitySetting();

    setLayout(new BorderLayout());

    int document_w=commBus.getPreferredWidth()-100, document_h=commBus.getPreferredHeight()-40, docview_w=80, docview_h=document_h, doc_y=40,
      conceptView_w = 110, conceptView_h = document_h;

    mainToolbar = new RH_MainToolbar(commBus,150,20); 
    mainToolbar.setBackground(Color.lightGray);
    add(mainToolbar,BorderLayout.NORTH);
    commBus.mainToolbar=mainToolbar;

    statusControl = new RH_StatusControl(commBus,24);
    add(statusControl,BorderLayout.SOUTH);
    commBus.statusControl=statusControl;

    JPanel centralPanel = new JPanel();
    centralPanel.setLayout(new BorderLayout());
    add(centralPanel,BorderLayout.CENTER);

    conceptControl = new RH_ConceptControl(commBus,conceptView_w,conceptView_h);
    centralPanel.add(conceptControl,BorderLayout.EAST);
    commBus.conceptControl=conceptControl;

    JPanel docPanel = new JPanel();
    docPanel.setLayout(new BorderLayout());
    centralPanel.add(docPanel,BorderLayout.CENTER);
    
    locationControl= new RH_Location(commBus,"Views",document_w-100,30);
    commBus.locationControl=locationControl;
    docPanel.add(locationControl,BorderLayout.NORTH);

    //*** Create the History Database
    //rhHistoryDB=new RH_HistoryDB(this, rhHistoryDBFileName);
    //commBus.historyDB=rhHistoryDB;
    //commBus.updateHistorySizeLabel();
  
    documentControl = new RH_Document(commBus, this, docview_w, doc_y, document_w, document_h);
    docPanel.add(documentControl,BorderLayout.CENTER);

    // note: this need to come after viewbar because it paints itself immediately and calls for info from viewbar
    thumbarControl = new RH_ThumbarControl(commBus,docview_h);
    centralPanel.add("West",thumbarControl);
    commBus.thumbarControl=thumbarControl;

    System.out.println("Running Version: "+makeVersion()+" - "+parent.getSystemVersion());


    // Setup History
    historyList=new RH_LocationItem[0]; 
    /*
      if (profile.getAutoLoadHomeURL()) {
      historyList[0]=new RH_LocationItem(homeURL,"Reader's Helper HTML Documents");
      }
      else {
      defaultHomeURL=new StringBuffer().append(httpFileTypeTag).append(mainPath).append(privateANOHDir).
      append(rhPathSeparator).append(rhDefaultHomeURLName).toString();
      historyList[0]=new RH_LocationItem(defaultHomeURL,"Readers Helper Home");
      }
      // Setup HOME as first history item: this is a kludge but not a high priority item either ...
    historyList[0].setNumber(0);
    parent.addHistoryMenuItem(historyList[0]);
    historyPointer=0;
    */
    if (!profile.getAutoLoadHomeURL()) profile.setHomeURL(defaultHomeURL);

    //splash.newMsg("Stopwords");
    //*** Setup supplemental hashtabel cache for info regarding cached documents
    docInfoCache=new Hashtable(profile.getDocumentCacheSize());

    //rhCalendar=new RH_Calendar(this);
    //rhSimilarity=new RH_Similarity(this);

    //stopwords=new RH_StopWords(this);
    //commBus.stopwords=stopwords;

    //documentLexicon=new RH_DocumentLexicon(this);
    //documentSpecialLexicon=new RH_DocumentLexicon(this);
    //commBus.documentLexicon=documentLexicon;

    //** I use this for initializing the whole calendar filetree structure
    //for (int f=1;f<=12;f++) rhCalendar.writeCalendarMonth(1998,f);
    //rhCalendar.writeCalendarMonth(1998,1);

    setVisible(true);
  }

    /*
    private boolean establishRHPSConnection() {
	boolean success=true;
	String servername=parent.getRMIServerName(), hostname=parent.getRMIHostname();
	int port=parent.getRMIPortName();
	String naming="//"+hostname+":"+port+"/"+"Ricoh_RHPS";
	try {
	    System.out.println("***looking up: "+ naming);
	    psConnection = (PSConnect_Stub)Naming.lookup(naming);
	    commBus.psConnection=psConnection;
	    return success;
	} catch (Exception e) {
	    System.out.println("RHPS exception: " +e.getMessage());
	    e.printStackTrace();
	    return success=false;
	}
    }
     */


    private boolean establishPortalConnection() {
	/*
	  boolean success=true;
	  String servername=parent.getRMIServerName(), hostname=parent.getRMIHostname();
	  int port=parent.getRMIPortName();
	  String naming="//"+hostname+":"+port+"/"+servername;
	  try {
	  System.out.println("***HelloApp looking up: "+ naming);
	  portalConnection = (PortalConnect_Stub)Naming.lookup(naming);
	  commBus.portalConnection=portalConnection;
	  return success;
	  } catch (Exception e) {
	  System.out.println("HelloApplet exception: " +e.getMessage());
	  e.printStackTrace();
	  return success=false;
	  }
	*/
	return false;
    }

  /**
   * Sets the current sentence count for the document just annotated
   */
  public void setDocumentSentenceCount(int count) {
    documentSentenceCount=count;
  }


  /**
   * Calls the RH_MatchConcepts object with current buffer to perform pattern matching on using 
   * user's profile concepts.
   *
   *@param buffer HTML document buffer stream just read from source
   *@param size size of buffer
   */
  public byte[] matchConcepts(byte[] buffer, int size) {
      documentSentenceCount=0;
      System.out.println("$$$$$$$$DO Annotation");
      commBus.setCalendarButton(false);  // turn off calendar button until processing done;  this gets turned on in calendar
      //** Update duration label
      commBus.setDurationLabel(RH_GlobalVars.defaultDurationStr);
      preAnnotated=false;
      Date starttime=new Date();

      annotationBuffer=commBus.requestProxyContent(RH_GlobalVars.piaProxyMsgProcessContent,buffer).getBytes();
      //** Now ask for the values for each concept
      String str=commBus.requestProxyContent(RH_GlobalVars.piaProxyMsgGetConceptInfo);
      //System.out.println(">>>"+str);
      commBus.updateConceptValues(str);

      Date endtime=new Date();
      //Calendar ecal=new GregorianCalendar();
      //System.out.println("***Ending:"+ecal.get(Calendar.HOUR)+":"+ecal.get(Calendar.MINUTE)+":"+ecal.get(Calendar.SECOND));
      annotationBufferSize=annotationBuffer.length;
      //System.out.println("SUMMARY:"+numSentences+" set JUSTANNOTATED="+justAnnotatedThisDocument);
      //System.out.println("DIFFTIME: "+(annotationDurationStr=calculateAnnotationTime(starttime,endtime)));
      annotationDurationStr=calculateAnnotationTime(starttime,endtime);
      //** Update duration label
      commBus.setDurationLabel(annotationDurationStr);
      justAnnotatedThisDocument=true;

      //** Generate a new document key since we just annotated this document; if this is a reannotation of a
      //** previously annotated document, reuse key and update the record
      String urlstr=commBus.getCurrentURL().toString();

      //int key=rhHistoryDB.checkHistory(urlstr);
      //** If key is >=0 then we already have this item in our database
      //if (key>=0) {
      //currentDocumentKey=rhHistoryDB.generateNewDocumentKey(key);
      //rhHistoryDB.updateRecord(urlstr.toString());  // this updates the record since we just reannotated the document
      //}
      //      else {
      //currentDocumentKey=rhHistoryDB.addRecord(urlstr.toString());
      //      }
      
      System.out.println("******DONE ANOHing Size:"+annotationBuffer.length+" annotationBufferSize="+annotationBufferSize+" preAnnotated="+preAnnotated);
      return annotationBuffer;
  }
    

    /**
     * This method receives a string of information from the pia proxy like:
     * "Agents 20 Interface 80 Java 30 NLP 0 ..."  which represents the individual scores computed
     * by the pattern matcher for each concept.  I use thi sroutine to update the gui
     */
    public void updateConceptValues(String conceptInfo) {
	StringTokenizer tokens=new StringTokenizer(conceptInfo," ");
	int size=tokens.countTokens(), val=0;
	String name=null, valstr=null;
	RH_Concept concept=null;
	if (size>0) {
	    while (tokens.hasMoreTokens()) {
		name=(String)tokens.nextToken();
		valstr=(String)tokens.nextToken();
		val=Integer.parseInt(valstr);
		
		concept=findConcept(name);
		if (concept!=null) {
		    concept.setValue(val);
		    concept=null;
		}
	    }
	    commBus.updateConceptMeters();
	}
    }
    
  /**
   * Returns the buffer we just processed which might possibly contain annotations
   */
  public byte[] getAnnotationBuffer() {
    return annotationBuffer;
  }
    public void setAnnotationBuffer(byte[] buf) {
	annotationBuffer=buf;
    }
  /**
   * Returns the size of the newly annotaed buffer
   */
  public int getAnnotationBufferSize() {
    // for now i'd like to return the annotationBufferSize because this is the actual number of bytes in the buffer (==newPtr)
    // whereas the annotationBuffer.length is the size of the buffer
    return annotationBufferSize; //annotationBuffer.length;
  }

  /**
   * Reset all the values in the topics for each concept; do this before processing any file!! so that the accumulators
   * don't countinously count
   */
  public void resetConcepts() {
    //System.out.println("%%%%%%%%% Reseting concepts");
      //for (int i=0;i<numConcepts;i++) if (activeConcepts[i]!=null) activeConcepts[i].resetValues();
  }

  private String calculateAnnotationTime(Date starttime, Date endtime) { //Calendar scal, Calendar ecal) {
    //turn new StringBuffer().append(endtime.getTime()-starttime.getTime()).toString();
    int seconds, minutes, hours, max_seconds=60;
    long secs=(endtime.getTime()-starttime.getTime())/1000, secs_n_hour=max_seconds*max_seconds;
    annotationDuration=secs;
    StringBuffer minbuf, secbuf, hrbuf;

    hours=(int)(secs/secs_n_hour); //floor(secs/secs_n_hour);
    minutes=(int)secs/max_seconds;  // grabs whole number for minutes, e.g. 2451secs/60secs=40.85, minutes=40
    seconds=(int)(secs-(minutes*max_seconds)); // grab remainder, seconds=51

    //System.out.println("secs="+secs+" secs_n_hour="+secs_n_hour+" hours="+hours+" min="+minutes+" secs="+seconds);
    //** Setup hour buffer
    if (hours>9) hrbuf=new StringBuffer().append(hours); //sprintf(hrbuf,"%1.0f",hours);
    else hrbuf=new StringBuffer().append("0").append(hours); //printf(hrbuf,"0%1.0f",hours);
    //** Setup minutes buffer
    if (minutes>9) minbuf=new StringBuffer().append(minutes); //sprintf(minbuf,"%1.0f",minutes);
    else minbuf=new StringBuffer().append("0").append(minutes); //sprintf(minbuf,"0%1.0f",minutes);
    //** Setup seconds buffer
    if (seconds>9) secbuf=new StringBuffer().append(seconds); //sprintf(secbuf,"%1.0f",seconds);
    else secbuf=new StringBuffer().append("0").append(seconds); //sprintf(secbuf,"0%1.0f",seconds);

    //** create final buffer
    return new StringBuffer().append(hrbuf.toString()).append(":").append(minbuf.toString()).append(":").append(secbuf.toString()).toString();
  }

  public void finalize() throws Throwable {
    System.out.println("****Running MainFrame Finalize Method:");
  }

  public void setWaitCursor() {
    parent.setCursor(new Cursor(Cursor.WAIT_CURSOR));
  }

  public void setDefaultCursor() {
    parent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }

  public String getUserFontName() {
    return profile.getDocumentFontName();
  }
  public int getUserFontSize() {
    return profile.getDocumentFontSize();
  }

  /**
   * Do this whenever a new document is visited; when a link is selected in a document, etc.
   */
  public void newURLLocation(String url) {
    commBus.setTextURL(url);
  }


  /**
   * Sets up the documentKey based on a url string which could be the current url or a pastAnnotatedURL
   *
   *@param urlstr the current URL string used to generate a unique key
   public void setupDocumentKey(String urlstr) {
   System.out.println("---Setting up document key:"+urlstr);
   //*** Check to see if we have previously annotated this document; if so, setup concepts, etc.
	 int key=rhHistoryDB.checkHistory(urlstr);
	 if (key>=0) {
	 System.out.println("oooooooooooooooooooTHIS DOC IN HISTORY LIST:"+urlstr+ "key:"+key);
	 hasBeenAnnotated=true;
	 currentDocumentKey=rhHistoryDB.generateNewDocumentKey(key);
	 
	 }
	 else {
	 hasBeenAnnotated=false;
	 currentDocumentKey="";
	 }
	 }
  */
  /** 
   * Used when repopulatig the scores of the concepts given a INFO file that exists from a previous time when the
   * user annotated the document.
   *
   *@param hashName name of document
   */
  public void populateConceptScores(String hashName) {
      //** NEED TO FIGURE OUT WHAT TO DO HERE - 1.29.99 jg
      /*      
      if (readLocalInfoFile(hashName)) {
	  System.out.println(">>>>>Updating gui-concepts...");
	  conceptControl.updateConcepts();
	  if (commBus.getShowPortal()) conceptControl.updateFurtherReadingList(hashName);
	  commBus.updateConceptMeters(); 
	  //** 8.26.98 new jg: added this because it stopped displaying annotations in the thumbar when
	  //** a already-annotated document was loaded into the browser.
	  commBus.refreshDocument(false);
      }
      else System.out.println("ERROR: could not read annotated doc's info file");
      */
  
    //commBus.updateNumberPhrasesLabel();
  }

  public boolean diffDocNames(String newDocName) {
    System.out.println("=]=]=]DIFF:"+currentDocumentKey+" vs. "+lastDocumentKey);
    if (lastDocumentKey.equalsIgnoreCase(newDocName)) return false;
    else return true;
  }

    /**
     * Set to done method completes the loding of a document for this class.
     */
  public void setToDone () {
    String hashName="";
    int key=-1;
    File file=null;
    boolean previousFlag=false;

    if (pastAnnotatedURL!="" && currentDocumentKey=="") {
	//System.out.println("**cha-cha..."+pastAnnotatedURL);
	//key=rhHistoryDB.checkHistory(pastAnnotatedURL);
	//if (key>=0) {
	//System.out.println("**Doing the cha-cha...");
	//hasBeenAnnotated=true;
	//currentDocumentKey=rhHistoryDB.generateNewDocumentKey(key);
	//}
    }
    System.out.println("->->->->->->->->->-START-<-<-<-<-<-<-<-<-<-<-<");
    System.out.println("->->->SetToTDone: key:"+currentDocumentKey+" lastKey:"+lastDocumentKey+" preAnnotated:"+preAnnotated);
    //System.out.println("->->->pastURL:"+pastAnnotatedURL+" hasBeen:"+hasBeenAnnotated+" just:"+justAnnotatedThisDocument);

    //*** Update the info cache with latest document information
    if (commBus.getCurrentURL()!=null) putDocInCache(commBus.getCurrentURL());

    rhPrivateFile=rhPrivateInfoFile=rhSummaryFile="";
    privateFileExists=privateIndexFileExists=privateInfoFileExists=false;
    StringBuffer tmpIdxFile=null, tmpIFile=null, tmpFile=null;

    //*** Check to see if the annotated "document" html file exists
    tmpFile=new StringBuffer().append(mainPath).append(privateANOHDir).append(rhPathSeparator).append(rhDocumentDir).append(rhPathSeparator).
      append(currentDocumentKey).append(rhPathSeparator).append(rhHTMLFileName).append(rhHTMLExtension);
    file = new File(tmpFile.toString());
    if (file.exists()) {
      rhPrivateFile=tmpFile.toString();
      privateFileExists=true;
    }
    else privateFileExists=false;
    //*** Check to see if the "info" file exists
    tmpIFile=new StringBuffer().append(mainPath).append(privateANOHDir).append(rhPathSeparator).append(rhDocumentDir).append(rhPathSeparator)
      .append(currentDocumentKey).append(rhPathSeparator).append(rhInfoFileName).append(rhInfoFileExt);
    file = new File(tmpIFile.toString());
    if (file.exists()) {
      rhPrivateInfoFile=tmpIFile.toString();
      privateInfoFileExists=true;
    }
    else privateInfoFileExists=false;
    //*** Check to see if the "index" html file exists
    tmpIdxFile=new StringBuffer().append(mainPath).append(privateANOHDir).append(rhPathSeparator).append(rhDocumentDir).append(rhPathSeparator).
      append(currentDocumentKey).append(rhPathSeparator).append(rhIndexFileName).append(rhHTMLExtension);
    file = new File(tmpIdxFile.toString());
    System.out.println("+++Private file:"+tmpIdxFile.toString());
    if (file.exists()) {
      rhIndexFile=tmpIdxFile.toString();
      privateIndexFileExists=true;
    }
    else privateIndexFileExists=false;

    //System.out.println("->->->Current File:"+commBus.getCurrentURL());
    //System.out.println("->->->Info:"+privateInfoFileExists+" Index:"+privateIndexFileExists+" Document:"+privateFileExists);

    if (rhFileType==RH_GlobalVars.RH_FILETYPE_ANOH || rhFileType==RH_GlobalVars.RH_FILETYPE_ANOH_SUMMARY) {
      //System.out.println("->->->ANOH File");
      if (rhFileType==RH_GlobalVars.RH_FILETYPE_ANOH) currentMode=RH_GlobalVars.annotationMode;
      else currentMode=RH_GlobalVars.summaryMode;

      //*** If we have the private files available, we indicate this to the user by populating the concepts and activating 
      //*** summary button.
      if (privateInfoFileExists && privateIndexFileExists && privateFileExists) {
	statusControl.message1("Loading a previously annotated document...");
	System.out.println("->->->Info file found!: just ANoh:"+justAnnotatedThisDocument);
	previousFlag=commBus.setPreviouslyAnnotatedIcon(true);
	//if (diffHashNames(hashName) && !justAnnotatedThisDocument) {
	if ((diffDocNames(currentDocumentKey) && !justAnnotatedThisDocument) || 
	    (!diffDocNames(currentDocumentKey) && !commBus.getPopulateConcepts() && !justAnnotatedThisDocument)) {
	    //System.out.println("->->->PRIVATE: Populate Concepts: ANOH file");
	  // Load serialized concepts so that we can show document annotate from when this doc was annotated
	  populateConceptScores(currentDocumentKey);
	  commBus.setSimilarButton(false);

	  // Now activate the sentences which have relevant topics in them
	  //setActiveSentences();  
	}
	//else if (justAnnotatedThisDocument) setActiveSentences();  
	//*** Now set the past url to be this one since it could possibily be used by the summary files
	if (pastAnnotatedURL=="") pastAnnotatedURL=commBus.getCurrentURL();
      }
      else {
	//System.out.println("->->->Info file NOT Found");
	previousFlag=commBus.setPreviouslyAnnotatedIcon(false);
      }
      //lastHashName=hashName;
      lastDocumentKey=currentDocumentKey;
    }
    else if (rhFileType==RH_GlobalVars.RH_FILETYPE_HTML) {
      //System.out.println("->->->HTML File");
      currentMode=RH_GlobalVars.plainTextMode;
      //*** Look for an associated private files; if found, populate the concepts
      if (privateInfoFileExists && privateIndexFileExists && privateFileExists) {
	System.out.println("->->->Info file found - HTML!");
	//** Do not set previous flag when not deserializing concepts
	if (commBus.getPopulateConcepts()) previousFlag=commBus.setPreviouslyAnnotatedIcon(true);
	else previousFlag=false;
	//if (diffHashNames(hashName)) {
	if (diffDocNames(currentDocumentKey)) {
	    //System.out.println("->->->HTML: Populate Concepts: ANOH file");
	    // Load serialized concepts so that we can show document annotate from when this doc was annotated
	    if (commBus.getPopulateConcepts()) populateConceptScores(currentDocumentKey);
	    commBus.setSimilarButton(true);
	    //**03-23-98 do i really need to reinstate concepts?  why not just leave it up to the user to make sure they reset stuff??
	    //needToReinstateConcepts=true;
	}
	//*** Now set the past url to be this one since it could possibily be used by the summary files
	if (pastAnnotatedURL=="") pastAnnotatedURL=commBus.getCurrentURL();
      }
      //*** If last filetype is of ANOH and this is an HTML then i'll need to reinstate the default concept states
      else if (lastFileType==RH_GlobalVars.RH_FILETYPE_ANOH || lastFileType==RH_GlobalVars.RH_FILETYPE_ANOH_SUMMARY) {
	//**03-23-98 do i really need to reinstate concepts?  why not just leave it up to the user to make sure they reset stuff??
	//needToReinstateConcepts=true;
	previousFlag=commBus.setPreviouslyAnnotatedIcon(false);
	commBus.resetConcepts();

	if (needToReinstateConcepts) {
	  populateConceptScores(rhInfoTempDir);
	  commBus.setSimilarButton(false);
	  System.out.println("****Reinstating previous concept state:"+rhReinstatedURL);
	  needToReinstateConcepts=false;
	  commBus.selectDefaultGroup();
	}
      }
      else {
	previousFlag=commBus.setPreviouslyAnnotatedIcon(false);
       
	commBus.resetConcepts();
	if (needToReinstateConcepts) {
	  populateConceptScores(rhInfoTempDir);
	  commBus.setSimilarButton(false);
	  //System.out.println("****Reinstating previous concept state:"+rhReinstatedURL);
	  needToReinstateConcepts=false;
	}
      }
      //lastHashName=hashName;
      lastDocumentKey=currentDocumentKey;
    }
    else if (rhFileType==RH_GlobalVars.RH_FILETYPE_TEXT) {
      //System.out.println("->->->TEXT File");
      //needToReinstateConcepts=false;
      //lastHashName=hashName;
      lastDocumentKey=currentDocumentKey;
      previousFlag=commBus.setPreviouslyAnnotatedIcon(false);
    }
    else if (rhFileType==RH_GlobalVars.RH_FILETYPE_IMAGE) {
      //System.out.println("->->->IMAGE  File");
      //needToReinstateConcepts=false;
      //lastHashName=hashName;
      lastDocumentKey=currentDocumentKey;
      previousFlag=commBus.setPreviouslyAnnotatedIcon(false);
    }
    else {
      //System.out.println("->->->NULL File:"+rhFileType);
      //needToReinstateConcepts=false;
      privateInfoFileExists=false;
      //lastHashName="";
      lastDocumentKey="";
      previousFlag=commBus.setPreviouslyAnnotatedIcon(false);
    }
    //*** Save the last filetype so i'll know if i need to reinstate concepts
    lastFileType=rhFileType;
    //if (needToReinstateConcepts) System.out.println("->->->NEED to reinstate concepts");

    commBus.setHighlightControls(previousFlag);
    //statusControl.message1("Done");
    System.out.println("->->->->->->->->->-END:"+pastAnnotatedURL+"["+currentMode+"]-<-<-<-<-<-<-<-<-<-<-<");

  }

  /**
   * Sets the document title in the document title label
   */
  public String setDocumentTitle(String str) {
    documentTitle=str;
    String combo=profile.getUserFirstName()+"'s "+titleString+documentTitle;
    parent.setTitle(combo); //viewToolBar.setTitleLabel(str);
    return combo;
  }

  /**
   * I use this to return a string with information regarding the current system, like build info.  this is only useful for
   * the developer which might want todisplay a flag or something in the about window
   */
  public String getOtherInfoString() {
    return new String("SLoc:"+useSentenceLocation);
  }

    public String makeVersion() {
	String ver=parent.getVersion(), bver=commBus.getBrowserVersion();
	return "RH:"+ver+"_HTML"+bver;
    }

  public String getDocumentTitle() {
    return documentTitle;
  }

  /**
   * This updates the title by grabbing the exact title currently in the browser
   */
  public String updateDocumentTitle() {
    return setDocumentTitle(commBus.getCurrentTitle());
  }

  public String getHomeURL() {
    return homeURL;
  }

  public Color getBackgroundColor() {
    return backgroundColor;
  }
  public Color getTextColor() {
    return textColor;
  }
  public Color getLinkColor() {
    return profile.getOverviewLinkColor();
  }
  public Color getLineAnnotationColor() {
    return profile.getOverviewANOHColor(); //lineAnnotationColor;
  }
  public Color getLineSentenceTagColor() {
    return profile.getOverviewANOHColor(); //lineSentenceTagColor;
  }
  public Color getLineEmailTagColor() {
    return lineEmailTagColor;
  }
  public Color getLineURLTagColor() {
    return lineURLTagColor;
  }
  public Color getOverviewBoldLineColor() {
    return overviewBoldLineColor;
  }
  public Color getAnnotationColor() {
    return annotationColor;
  }
  public Color getSentenceAnnotationColor() {
    return sentenceColor;
  }
  public Color getHighlightColor() {
    return highlightColor;
  }
  public Color getBoldUnderlineColor() {
    return boldUnderlineColor;
  }
  public Color getHighlightShadowColor() {
    return highlightShadowColor;
  }
  public Color getOverviewWindowColor() {
    return profile.getOverviewWindowColor();
  }
  
  /**
   * Request that the browser simply repaint the document canvas
   */
  public void refreshDocument(boolean redoLayout) {
      //setActiveSentences();

    //commBus.refreshDocument();
  }

  /**
   * Sets the basic colors used by all classes for gui objects
   */
  public void setMotif(int num) {
    if (num==1) {
      mainBackColor=Color.gray;
      mainTextColor=Color.white;
      mainHighlightColor=Color.lightGray;
      mainShadowColor=Color.black;
      mainShadowColor2=Color.gray;
    }
    else {
      mainBackColor=Color.lightGray;
      mainTextColor=Color.black;
      mainHighlightColor=Color.white;
      mainShadowColor=Color.gray;
      mainShadowColor2=Color.black;
    }
  }

  /**
   * What needs to be reset each time we laod a new url
   */
  public void loadingNewDocument(String urlstr) {
    rhFileType=RH_GlobalVars.RH_FILETYPE_NULL;  // reset
    pastAnnotatedURL=currentDocumentKey="";
    commBus.setPreviouslyAnnotatedIcon(false);
    annotationDurationStr=RH_GlobalVars.defaultDurationStr;
    annotationDuration=0;
    commBus.setSimilarButton(false);  
    commBus.setDurationLabel(annotationDurationStr);
    justAnnotatedThisDocument=false; 
    //setupDocumentKey(urlstr); /// was in updateURL() - 02-27-98
  }

  public void revisitingDocument(String urlstr) {
    justAnnotatedThisDocument=false; 
    RH_InfoCache info=(RH_InfoCache)docInfoCache.get(urlstr);
    if (info!=null) {
      pastAnnotatedURL=info.getPastURL();
      String url=info.getURL();
      rhFileType=info.getType();
      annotationDurationStr=info.getDuration();
      //*** must also set the mode for the cached document
      if (rhFileType==RH_GlobalVars.RH_FILETYPE_ANOH) currentMode=RH_GlobalVars.annotationMode;
      else if (rhFileType==RH_GlobalVars.RH_FILETYPE_ANOH_SUMMARY) currentMode=RH_GlobalVars.summaryMode;
      else currentMode=RH_GlobalVars.plainTextMode;
      System.out.println("---->Revisiting:"+url+" past:"+pastAnnotatedURL+" type="+rhFileType+" currentMode="+currentMode+" dur:"+annotationDurationStr);
      commBus.setDurationLabel(annotationDurationStr);
    }
    else System.out.println("---->Could not Revisit:"+urlstr);
  }

  public void browserURL_Stop() {
    commBus.browserURL_Stop();
  }

  public boolean getUseCacheDocuments() {
    return profile.getUseCacheDocuments();
  }
  public boolean getUseCacheImages() {
    return profile.getUseCacheImages();
  }
  public int getDocumentCacheSize() {
    return profile.getDocumentCacheSize();
  }

  public boolean getUseLoadImages() {
    return commBus.getUseLoadImages();
  }

  /**
   * Check to see if the document we have in the browser has been annotated
   */
  public boolean usingAnnotatedDocument() {
    return hasBeenAnnotated;
  }

  /**
   * This method is run when a document is read and found to be an RH.ANNOTATION document; do what you need to do
   * with documents that have been pre-annotaed here
   */
    public boolean setUsingAnnotatedDocument(String pasturl, String ver) {
	if (pasturl!=null && pasturl.length()>0) {
	    System.out.println("***USING ANNOTATED DOCUMENT***");
	    //** If loading images is on, set document base so can load images from the net
	    if (profile.getUseLoadImages()) documentControl.setDocumentBase(pasturl); 
	    pastAnnotatedURL=pasturl;
	    preAnnotated=true;
	    pastVersion=ver;
	    commBus.setAnnotateMode();
	    setHtmlAnohFileType();
	    //setupDocumentKey(pastAnnotatedURL);
	    return hasBeenAnnotated=true;
	}
	else return false;
    }
  public boolean setUsingAnnotatedIndex(String pasturl, String ver) {
    if (pasturl.length()>0) {
      pastAnnotatedURL=pasturl;
      pastVersion=ver;
      commBus.setAnnotateMode();
      setHtmlAnohSummaryFileType();
      //setupDocumentKey(pastAnnotatedURL);
      return hasBeenAnnotated=true;
    }
    else return false;
  }

  public boolean setUsingAnnotatedSummary(String orgurl, String ver) {
    if (orgurl.length()>0) {
      pastAnnotatedURL=orgurl;
      pastVersion=ver;
      preAnnotated=true;
      setHtmlAnohSummaryFileType();
      //setupDocumentKey(pastAnnotatedURL);
      commBus.setAnnotateMode();
      return hasBeenAnnotated=true; 
    }
    else return false;
  }
  /**
   * Initializes the processing of a document;  this is called in Parser.run()
   */
  public boolean setUsingHtmlDocument() {
    pastAnnotatedURL="";
    preAnnotated=false;
    pastVersion="";
    setHtmlFileType();
    return true;
  }
  /**
   * Sets the file type of the current file being processed in ProcessURL
   */
  public void setHtmlFileType() {
    rhFileType=RH_GlobalVars.RH_FILETYPE_HTML;
  }
  public void setHtmlAnohFileType() {
    rhFileType=RH_GlobalVars.RH_FILETYPE_ANOH;
  }
  public void setHtmlAnohSummaryFileType() {
    rhFileType=RH_GlobalVars.RH_FILETYPE_ANOH_SUMMARY;
  }
  public void setTextFileType() {
    rhFileType=RH_GlobalVars.RH_FILETYPE_TEXT;
  }
  public void setImageFileType() {
    rhFileType=RH_GlobalVars.RH_FILETYPE_IMAGE;
  }
  /**
   * Checks to see if any concepts are active
   */
  public boolean conceptsActive() {
    boolean found=false;
    for (int i=0;i<numConcepts && activeConcepts[i]!=null && !(found=activeConcepts[i].isActive());i++);
    return found;
  }

  /**
   * Returns the current state of a concept
   */
  public boolean isConceptActive(String conceptStr) {
    RH_Concept concept=findConcept(conceptStr);
    if (concept!=null && concept.getValue()>=currentSensitivity) return concept.isActive();
    else return false;
  }

  /**
   * Returns the current state of the sentence
   */
  public boolean isSentenceActive(int loc) {
    //System.out.print("***Checking Sentence["+loc+"]");
    if (numSentences>0 && loc>=0 && loc<numSentences && sentenceSummary!=null) {
      //System.out.println("numSentences="+numSentences+" active="+sentenceSummary[loc].isActive());
      //System.out.println("S:"+sentenceSummary[loc].getSentence());
      if (sentenceSummary[loc]!=null) return sentenceSummary[loc].isActive();
      else return false;
    }
    else return false;
  }

    /**
     * Returns a string which contains a list of integers each representing a sentence in the current document which has
     * an active concept in it.
     */
    public String getActiveSentences() {
	return (activeSentencesBuffer!=null ? activeSentencesBuffer.toString() : "");
    }
    public String getActiveConcepts() {
	StringBuffer buffer=new StringBuffer();
	int count=0;
	for (int i=0; i<numConcepts; i++) {
	    if (activeConcepts[i]!=null && activeConcepts[i].isActive() && activeConcepts[i].getValue()>=currentSensitivity) {
		if (count>0) buffer.append(", ");
		buffer.append(activeConcepts[i].getShortName()+":").append(activeConcepts[i].getValue()+"%");
		count++;
	    }
	}
	return (buffer.length()>0 ? buffer.toString() : makeVersion());
    }


    public String generateActiveConceptsList() {
	StringBuffer buffer=new StringBuffer();
	for (int i=0; i<numConcepts; i++) {
	    if (activeConcepts[i]!=null && activeConcepts[i].isActive()) {
		buffer.append(activeConcepts[i].getShortName()+" ");
	    }
	}
	return (buffer.length()>0 ? buffer.toString() : "");
    }
    /*
      public String generateActiveSentences() {
      activeSentencesBuffer=new StringBuffer();
      Enumeration enum=null;
      Vector vector=null, holder=new Vector();
      Integer loc=null;
      for (int i=0;i<numConcepts;i++) {
      if (activeConcepts[i].isActive() && (vector=activeConcepts[i].getSentenceVector())!=null) {
      enum=vector.elements();
      while (enum.hasMoreElements()) {
      loc=(Integer)enum.nextElement();
      if (!holder.contains(loc)) holder.addElement(loc);
      }
      }
      }
      enum=holder.elements();
      while (enum.hasMoreElements()) activeSentencesBuffer.append(enum.nextElement()+" ");
      System.out.println("***ActiveS: "+activeSentencesBuffer.toString());
      return activeSentencesBuffer.toString();
      }
    */

  public void changeColorScheme(int num) {
    // Highlighting topics only
    
    // move these - do not eed to set them each time 8-18-97
    linkColor = Color.blue;
    textColor = Color.black;
    annotationColor = Color.black;
    lineAnnotationColor = Color.red;
    lineSentenceTagColor = defaultOverViewLineColor;
    lineEmailTagColor = Color.cyan;
    lineURLTagColor = Color.cyan;
    boldUnderlineColor= Color.red;
    highlightShadowColor=Color.gray;
    backgroundColor = Color.lightGray;
    
    if (num>=0 && num<profile.numHighlights) {
      highlightColor=new Color(profile.highlightStyles[num].getRed(),profile.highlightStyles[num].getGreen(),
			       profile.highlightStyles[num].getBlue()); 
      annotationColor=new Color(profile.highlightStyles[num].getForeRed(),profile.highlightStyles[num].getForeGreen(),
				profile.highlightStyles[num].getForeBlue());
      if (profile.highlightStyles[num].getBold()==1) boldInHighlight=true;
      else boldInHighlight=false;
      if (profile.highlightStyles[num].getUnder()==1) underlineInHighlight=true;
      else underlineInHighlight=false;
      if (profile.highlightStyles[num].getShadow()==1) highlightDropShadow=true;
      else highlightDropShadow=false;
      if (profile.highlightStyles[num].getWhole()==1) highlightWholeSentence=true;
      else highlightWholeSentence=false;
      sentenceColor=highlightColor;
      /*
	// If whole sentence is true, set flag but use highlight style
	if (profile.highlightStyles[num].getType()==RH_GlobalVars.RH_WholeSentenceType) {
	System.out.println("***Setting up whole setnece highlighting");
	documentAnnotationType=RH_GlobalVars.RH_Annotate_Highlight;
	highlightWholeSentence=true;
	boldInHighlight=true;
	highlightDropShadow=true;
	}
	else if (profile.highlightStyles[num].getType()==RH_GlobalVars.RH_Annotate_BoldUnderline) {
	documentAnnotationType=RH_GlobalVars.RH_Annotate_BoldUnderline;
	highlightWholeSentence=false;
	boldInHighlight=true;
	highlightDropShadow=true;
	}
	else if (profile.highlightStyles[num].getType()==RH_GlobalVars.RH_Annotate_Outline) {
	documentAnnotationType=RH_GlobalVars.RH_Annotate_Outline;
	highlightWholeSentence=false;
	boldInHighlight=true;
	highlightDropShadow=false;
	}
	//** Otherwise just phrase annotation with a colored box surrounding the phrase
	     else {
	     documentAnnotationType=profile.highlightStyles[num].getType(); //RH_Annotate_Highlight;
	     highlightWholeSentence=false;
	     boldInHighlight=true;  // set to false if you do not want bolding in phrase highlighting
	     highlightDropShadow=false;  // this should be a user selectable items - 12-4-97
	     }
      */
    }
    else System.out.println("ERROR: problem with highlight styles: out of range: " + num);
  }

  public int getDocumentAnnotationType() {
    return documentAnnotationType;
  }


  /**
   * Searches current concepts for concept matching given long or short name; if found returns concept
   */
  public RH_Concept findConcept(String name) {
    int i=0;
    for (i=0; i<numAllConcepts; i++)
      if (allConcepts[i].getName().equalsIgnoreCase(name) || allConcepts[i].getShortName().equalsIgnoreCase(name))
	  return allConcepts[i];
    return null;
  }
  /**
   * Searches current concepts for concept matching given long or short name; if found returns index
   */
  public int findConceptIndex(String name) {
    int i=0;
    for (i=0; i<numAllConcepts; i++)
      if (allConcepts[i].getName().equalsIgnoreCase(name) || allConcepts[i].getShortName().equalsIgnoreCase(name))
	return i;
    return -1;
  }

  public void useProximity() {
    useProximityMeasure=true;
  }
  public void dontUseProximity() {
    useProximityMeasure=false;
  }


  public void putDocInCache(String newurl) {
    String url="", pasturl="";
    // This should get set in setUsingAnnotatedDocument
    pasturl=pastAnnotatedURL;
    url=newurl;
    System.out.println("=+=+=+:Put doc:"+url+" type="+rhFileType);
    try {
      if (docInfoCache.size()<profile.getDocumentCacheSize()-1) 
	docInfoCache.put(url,new RH_InfoCache(url,pasturl,rhFileType,annotationDurationStr));
    } catch (NullPointerException ex) {
      System.out.println("***ERROR: could not put doc info in cache:"+url);
    }
  }

  /**
   * Clear the internal document information cache; shouldonly be done when the actual document.browser.docCache gets cleared
   */
  public void clearInfoCache() {
    Enumeration enum = docInfoCache.elements();
    // First, i nullify all docus in the cache so the GC will act on them appropriately
    while (enum.hasMoreElements()) {
      RH_InfoCache item=(RH_InfoCache)enum.nextElement();
      item=null;
    }
    // then i clear the cache
    docInfoCache.clear();
  }

  public void purgingDocuments(boolean set) {
    if (set) {
      documentControl.setWaitCursor();
      statusControl.message2("Cleaning...");
    }
    else {
      documentControl.setDefaultCursor();
      statusControl.message2("Done");
    }
  }

  /**
   * Sets up the summary mode, showing the summary html file 
   */
  public void showSummary() {
    //---*** This calls request the display of the summary file; the final "true" sets preAnnotated=true so 
    //---*** we start with a preAnnotated flag on
    StringBuffer file=new StringBuffer().append(httpFileTypeTag).append(mainPath).append(privateANOHDir).append(rhPathSeparator).
      append(rhDocumentDir).append(rhPathSeparator).append(currentDocumentKey).append(rhPathSeparator).append(rhIndexFileName+rhHTMLExtension);
    System.out.println(">>>>>>>IndexFile:"+file.toString());
    commBus.URL_Process(file.toString(),false,true);
    commBus.statusControl.message1(file.toString()); 
  }

    
    public void setUseSentenceLocation(boolean set) {
	useSentenceLocation=set;
    }
    public boolean getUseSentenceLocation() {
	return useSentenceLocation;
    }
    public void setUseLexicon(boolean set) {
	useLexicon=set;
    }

    
  public String getNewlineByte() {
    byte nl=(byte)'\n';
    byte[] newLine=new byte[1];
    newLine[0]=nl;
    return new String(newLine);
  }

  /**
   * Shutdown method: put al things that must happen at shutdown in here
   */
  public void shutdown() {
    System.out.println("****SHUTTING DOWN SYSTEM...");
    //rhHistoryDB.writeHistoryDB();
    System.out.println("****DONE WITH SHUTDOWN");
  }

}	



