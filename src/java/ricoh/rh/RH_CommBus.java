/**  
 *
 * Copyright (C) 1997, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: RH_CommBus: the communication bus for all objects; use this to communicate amongst the objects in RH
 * @author <a href="mailto:jamey@rsv.ricoh.com">Jamey Graham</a>
 * @version 4.24.97 -revised 02-06-98
 *
 */

//package ice.htmlbrowser;
package ricoh.rh;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.awt.Component;
import java.awt.Frame;
import java.awt.*;
import java.net.*;
import java.rmi.RemoteException;

import jclass.bwt.JCButton;
//import ricoh.portal.PortalConnect_Stub;
//import ricoh.portal.PortalConnect;

//import ricoh.rhps.PSConnect_Stub;
//import ricoh.rhps.PSConnect;

import ricoh.rh.RH_Document;
/**
 * The RH_CommBus class is the communication class between all other class in the Reader's Helper
 * package (ricoh.rh).  This class contains a variable for all relevant classes within this
 * package.  This class will also typically contain a wrapper method for other class methods
 * to provide an interface between the classes.  The 1programmer should use te methods contained
 * in this class to invoke the various class methods.
 *
 * @author <a href="mailto:jamey@rsv.ricoh.com">Jamey Graham</a>
 * @version 4.24.97 -revised 02-06-98
 */
public class RH_CommBus {

    // Dimitri: the maximum number of boxes in a linked list passed to RMI
  public static final int MAX_NUM_BOXES = 500; 

  public static final int ERROR_TYPE_MSG=0, NORMAL_TYPE_MSG=1;
  // Set this to true when you want to view special messages in the console; false otherwise
  private boolean viewConsoleMessages=true;
    int historyLoad=-1; 
  //private boolean historyLoad=false;
  public boolean reloadLoad=false;
  public ReadersHelper parent;
  public RH_Profile profile;
  public RH_MainFrame mainFrame;
  public RH_Document documentControl;
  public RH_MainToolbar mainToolbar;
  public RH_Location locationControl;
  public RH_StatusControl statusControl;
  public RH_ConceptControl conceptControl;
  public RH_HliteControl hliteControl;
  public RH_ThumbarControl thumbarControl;
    //public RH_HistoryDB historyDB;
    //public RH_Calendar calendar;
    //public RH_Similarity similarity;
    //public RH_DocumentLexicon documentLexicon;
    //public RH_UserLexicon userLexicon;
    //public RH_StopWords stopwords;
    //public PortalConnect_Stub portalConnection;
    //public PSConnect_Stub psConnection;

  private String workingLabel="Working...", doneLabel="Done", loadingLabel="Loading: ";

  public RH_CommBus () { }

  /**************************************************************************************
   * Communication Methods: these methods will be used to communicate between the various
   * GUI objects.  For instance, when the sensitivity slider value is changed, a message
   * is set to the comm bus that the value is changed.  It is up to the programmer to 
   * implement the proper method to handle this event (i.e. to update the search engine and
   * display).
   *
   **************************************************************************************/

  /**
   * Do everything you need to do here before exiting
   */
  public void exitSystem() {
    if (mainFrame!=null) mainFrame.shutdown();
    profile.shutdown();
    System.exit(0);
  }

    /**
     * This is the main routine for processing a URL.  Every RH mehod that requests the loading of a URL goes through
     * this method.
     *
     * @param url url string to load
     */
  public void URL_Process (String url) {
    URL_Process(url,false,false);
  }
    /**
     * This is the main routine for processing a URL.  Every RH mehod that requests the loading of a URL goes through
     * this method.
     *
     *@param url url string name
     *@param reload is this a reload of the same document
     *@param summary no in use anymore
     */
  public void URL_Process (String url, boolean reload, boolean summary) {

    //----**** SETUP HISTORY FLAG: 
    //----**** Now initialize variables before asking the browser to process this URL
    //initializeBeforeLoading(reload,summary);
    reloadLoad=reload;

    //System.out.println("CommBus: trying to goto: "+url);

    //----****TELL BROWSER TO PROCESS DOCUMENT URL
    documentControl.gotoLocation(url);
 
    //----****ITEMS BELOW DO NOT GET EXECUTED UNTIL AFTER DOCUMENT HAS BEEN PROCESSED*****
    //----****(including annotation)
    //documentControl.browser.setNewDocumentFlag(false); 
    //if (documentControl.browser.fwdHistoryAvailable()) mainToolbar.moreFwdDocuments();
    //else mainToolbar.noMoreFwdDocuments();
    //if (mainFrame.historyList.length>1) mainToolbar.moreBackDocuments();
    //if (documentControl.browser.backHistoryAvailable()) mainToolbar.moreBackDocuments();
    //else mainToolbar.noMoreBackDocuments();
  }

    public void gotoHomeURL() {
	String homeURL=getHomeURL();
	setPlainTextMode();
	documentControl.setHistoryLoad(true);
	documentControl.gotoLocation(homeURL);
	statusMsg1(homeURL); 
    }

    /**
     * Go back to previous page
     */
  public void browserGoBack() {
    documentControl.goBack();
  }
    /** Go forward to page last visited
     */
  public void browserGoForward() {
    documentControl.goForward();
  }

  /** 
   * When an item from the history list has been selected, this is what we do ...
   *
   *@param label name of the link in the history list (the title)
   *@param list list of all history items so far
   */
    public void gotoHistoryURL (String urlstr, int idx) {
	//System.out.println("***FOOMANCHU CALLED");
	historyLoad=idx;  
	mainFrame.historyList[mainFrame.historyPointer].setBold(false);
	mainFrame.historyPointer=idx;
	mainFrame.historyList[mainFrame.historyPointer].setBold(true);
	documentControl.gotoLocation(urlstr);
	documentControl.setNewDocumentFlag(false); 
    }
    /**
     * Returns the current URL 
     */
  public String getCurrentURL() {
    if (documentControl!=null) return documentControl.getCurrentLocation();
    else return "";
  }

    /**
     * Returns the current document title
     */
  public String getCurrentTitle() {
      return (documentControl!=null ? documentControl.getCurrentTitle() : "");
  }

    /**
     * Sets the URL string in the RH_Location class which is the text entry point for entering
     * URLS
     */
  public void setTextURL(String newurl) {
    //System.out.println("---Setting text url:"+newurl);
    if (locationControl!=null) locationControl.setTextURL(newurl);
  }

    /**
     * Reload the current document
     */
  public void reloadURL () {
      System.out.println("***Reloading document:"+getCurrentURL());
      mainFrame.forceLoad=true;
      reloadLoad=true;
      documentControl.setDocumentBase(getCurrentURL());
      documentControl.reload();
  }
  public void oldreloadURL () {
    String url=getCurrentURL();
    //resetConcepts();
    statusMsg1(loadingLabel + url);   
    setPlainTextMode();
    mainFrame.forceLoad=true;
    URL_Process(url,true,false);
    // Get the actual file name of the URL from browser
    statusMsg1(url); 
  }
    /**
     * Reload and annotation the current document
     */
  public void anohReloadURL () {
    String url=getCurrentURL();
    System.out.println("***AnohReloading document:"+url);
    resetConcepts();
    statusMsg1(loadingLabel + url);   
    setAnnotateMode();
    mainFrame.forceLoad=true;
    documentControl.setDocumentBase(getCurrentURL());
    documentControl.reload();
    // Get the actual file name of the URL from browser
    statusMsg1(url); 
  }
    /**
     * Reload and annotation the current document
     */
  public void oldanohReloadURL () {
    String url=getCurrentURL();
    resetConcepts();
    statusMsg1(loadingLabel + url);   
    setAnnotateMode();
    mainFrame.forceLoad=true;
    URL_Process(url,true,false);
    // Get the actual file name of the URL from browser
    statusMsg1(url); 
  }

    /**
     * Requests that the browser stop loading the current document
     */
  public void browserURL_Stop() {
    mainFrame.forceLoad=false;
    mainToolbar.stop();
    documentControl.stopLoading();
  }

    /**
     * Notifcation to other classes that the main windows frame has just been resize
     */
  public void frameResized() {
      //System.out.println(":::::: CommBus: Resizing...");
    if (thumbarControl!=null) thumbarControl.documentResized();
    //documentControl.browser.frameResized();
  }
    /**
     * Not in use ??? 6.11.98
     */
  public void redoLayout() {
    //documentControl.browser.redoLayout();
  }


  // Run when loading a new document to reset the ThumbarParent display
  /*
  public void loadingNewDocument() {
    this.loadingNewDocument(getCurrentURL());
  }
  public void loadingNewDocument(String url) {
    System.out.println("***Loading new doc...:"+url+"  reload="+reloadLoad);
    if (mainToolbar!=null) {
      //initializeBeforeLoading(false);
      mainFrame.loadingNewDocument(url);
      locationControl.setTextURL(url.toString());
      mainToolbar.loadingNewDocument();
    }
    if (thumbarControl!=null) thumbarControl.loadingNewDocument();
  }
  */

  /**
   * Run this when done loading document - called by Browser (not in use ?? 6.11.98)
   */
  public void doneProcessing() {
    //if (mainToolbar!=null)  mainToolbar.setToDone();
  }

  /**
   * Run this when starting to process a document -- called by RH_Document or 
   * ice.htmlbrowser.Document
   */
    public void startingToProcess(String urlstr) {
	setWaitCursor();
	if (thumbarControl!=null) thumbarControl.setThumbarStatusStart();
	statusControl.resetProgress();
	statusMsg1("Loading: "+urlstr);
	statusMsg2(workingLabel);
	//System.out.println("***Starting to process:"+urlstr);
	setTextURL(urlstr);
	//** Set title of document to be url at first until title arrives (see setToDone)
	mainFrame.setDocumentTitle(urlstr);
	if (mainToolbar!=null) {
	    mainFrame.loadingNewDocument(urlstr);
	    locationControl.setTextURL(urlstr);
	    mainToolbar.loadingNewDocument();
	}
	if (thumbarControl!=null) thumbarControl.loadingNewDocument();

	//System.out.println("***DONE Starting to process:");
    }

    public void loadingBackHistoryDocument() {
	//System.out.println("------>BEFORE:HistoryPointer: "+mainFrame.historyPointer);
	historyLoad=++mainFrame.historyPointer;
	startingToProcess(mainFrame.historyList[historyLoad].getURL()); //getFirstBackDocument());
	//startingToProcess(mainFrame.historyList[++mainFrame.historyPointer].getURL());
	parent.updateHistoryMenu(mainFrame.historyPointer-1,mainFrame.historyPointer);
	//System.out.println("------>AFTER:HistoryPointer: "+mainFrame.historyPointer);
    }
    public void loadingFwdHistoryDocument() {
	//System.out.println("------>BEFORE:HistoryPointer: "+mainFrame.historyPointer);
	historyLoad=--mainFrame.historyPointer;
	if (mainFrame.historyPointer>0) startingToProcess(mainFrame.historyList[historyLoad].getURL());
	//startingToProcess(mainFrame.historyList[--mainFrame.historyPointer].getURL());
	parent.updateHistoryMenu(mainFrame.historyPointer+1,mainFrame.historyPointer);
	//System.out.println("------>AFTER:HistoryPointer: "+mainFrame.historyPointer);
    }
    public void loadingHistoryDocument(int idx) {
	int oldPtr=mainFrame.historyPointer;
	//System.out.println("------>BEFORE:HistoryPointer: "+mainFrame.historyPointer);
	historyLoad=mainFrame.historyPointer=idx;
	startingToProcess(mainFrame.historyList[idx].getURL()); //getFirstBackDocument());
	parent.updateHistoryMenu(oldPtr,mainFrame.historyPointer);
	//System.out.println("------>AFTER:HistoryPointer: "+mainFrame.historyPointer);
    }

  /**
   * Do anything that should be done after loading a document in this method
   */
  public void setToDone() {
    //** I put this in again here because the browser resets the cursor to default when done loading/displaying...
    parent.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    if (documentControl!=null) documentControl.setWaitCursor();

    System.out.println("****COMMBUS: IN setToDone...");
    if (mainToolbar!=null)  mainToolbar.setToDone();
    mainFrame.rhPreviousURL=getCurrentURL();
    //** 8.26.98 jg: moving until after ther deserialization process just in case we deserialize a document and the thumbar needs updating
    if (thumbarControl!=null) thumbarControl.setToDone();  
    reloadLoad=false;

    if (historyLoad<0) {
	//System.out.println("==============Updating HISTORY: "+getCurrentURL()+" :"+documentControl.getDocumentTitle());
	updateHistory(getCurrentURL(),documentControl.getDocumentTitle());
    }

    //System.out.println("**STUFF: "+mainFrame.currentMode+" vs. "+RH_GlobalVars.annotationMode+" |"
    //		       +mainFrame.rhFileType+" vs. "+RH_GlobalVars.RH_FILETYPE_ANOH+
    //	       " just Annotated:"+mainFrame.justAnnotatedThisDocument);
    //---*** When we ar finally done with the loading of a document, and the load included ANOHing the
    //---*** document, check to see if there are entries in the cache for summaries assoc. with this document.
    //---*** If there are, remove them; they are no longer valid since the annotation of the doc was updated.
    //System.out.println("*-*-*-*:JustAnoh:"+mainFrame.justAnnotatedThisDocument+" filetype="+mainFrame.rhFileType);
    if (mainFrame.currentMode==RH_GlobalVars.annotationMode && mainFrame.rhFileType==RH_GlobalVars.RH_FILETYPE_ANOH && 
	mainFrame.justAnnotatedThisDocument) { 

      //** Now request that the server store the results (this has to come after the document has been parsed 
      //** because the title (for instance) is needed for constructing the index and summary files.
      String str=requestProxyContent(RH_GlobalVars.piaProxyMsgStoreResult);
      str=requestProxyContent(RH_GlobalVars.piaProxyMsgPutSensitivity);
      //System.out.println("***:"+str);
      updateConceptsWithSentenceData(str);
    }
    else if (mainFrame.preAnnotated && mainFrame.currentMode==RH_GlobalVars.annotationMode) {
	System.out.println ("***SETTING UP CONCEPTS FOR ANOH DOC***");
	String conceptsString=requestProxyContent(RH_GlobalVars.piaProxyMsgGetAnohFileConcepts);
	//System.out.println("***:"+conceptsString);
	updateConceptValues(conceptsString);
	conceptsString=requestProxyContent(RH_GlobalVars.piaProxyMsgPutSensitivity);
	//System.out.println("***:"+conceptsString);
	updateConceptsWithSentenceData(conceptsString);
    }
    mainToolbar.setMemoryToolTip();
    mainFrame.setToDone();
    mainToolbar.summaryAvailable(mainFrame.privateIndexFileExists);  // this gets set in mainFrame.setToDone
    cacheNotEmpty();

    statusMsg2(doneLabel);

    parent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    if (documentControl!=null) documentControl.setDefaultCursor();
    if (thumbarControl!=null) thumbarControl.setLensLocation(documentControl.getBrowserScrollerLocation());
    statusControl.setToDone();
    historyLoad=-1;

    if (mainFrame.historyPointer!=0) mainToolbar.moreFwdDocuments();
    else mainToolbar.noMoreFwdDocuments();
    if (mainFrame.historyPointer<mainFrame.historyList.length-1) mainToolbar.moreBackDocuments(); //documentControl.backHistoryAvailable
    else mainToolbar.noMoreBackDocuments();

    //** 8.26.98 jg: moved here form above - please test
    //if (thumbarControl!=null) thumbarControl.setToDone();  
    //** Reset historyLoad flag
    documentControl.setHistoryLoad(false);
    System.out.println("==================COMMBUS: Done Loading Doc==============");
  }

    /**
     * Refreshes all necessary classes
     */
  public void refreshDocument() {
    this.refreshDocument(false);
  }
    /**
     * Refreshes all necessary classes
     *
     *@param redoLayout requests that the layout be updated as well (not done by default)
     */
  public void refreshDocument(boolean redoLayout) {
    documentControl.refreshDocument(redoLayout);
    mainFrame.refreshDocument(redoLayout);
    if (thumbarControl!=null) thumbarControl.refreshDocument();
    statusMsg2(doneLabel);
  }

    /**
     * Returns the document window size
     */
  public Dimension getDocumentWindowSize() {
    return documentControl.getSize();
  }
    /**
     * Profile wrapper: returns the user's first name
     */
  public String getUserFirstName() {
    return profile.getUserFirstName();
  }
    /**
     * Profile wrapper: returns the user's last name
     */
  public String getUserLastName() {
    return profile.getUserLastName();
  }
    /**
     * Profile wrapper: returns the user's login name
     */
  public String getUserAccountName() {
    return profile.getUserAccountName();
  }
    /**
     * Profile wrapper: returns the home path for the user's profile 
     */
  public String getPath() {
    return profile.getPath();
  }
    /**
     * Profile wrapper: returns the last modified date in the profile.rh file
     */
  public String getLastModified() {
    return profile.getLastModified();
  }
    /**
     * Profile wrapper: returns the highlighting styles specified in profile.rh
     */
  public RH_HighlightStyle[] getHighlightStyles() {
    return profile.getHighlightStyles();
  }
    /**
     * Profile wrapper: returns the default highlighting style
     */
  public int getDefaultHliteStyle() {
    return profile.getDefaultHliteStyle();
  }
    /**
     * Profile wrapper: returns the meter styles fromthe profile
     */
  public RH_MeterStyle[] getMeterStyles() {
    return profile.getMeterStyles();
  }
    /**
     * Profile wrapper: returns the default sensitivity value
     */
  public int getSensitivitySetting() {
    return profile.getSensitivitySetting();
  }
    /**
     * Profile wrapper: true if using large meters; false otherwise
     */
  public boolean getLargeMeters() {
    return profile.getLargeMeters();
  }
    /**
     * Profile wrapper: returns the color used for the meters
     */
  public Color getDefaultMeterColor() {
    return profile.getDefaultMeterColor();
  }
    /**
     * Profile wrapper: returns the color used for composite meters
     */
  public Color getCompositeMeterColor() {
    return profile.getCompositeMeterColor();
  }
    /**
     * Profile wrapper: returns the color of the text in the location object
     */
  public Color getLocationTextColor() {
    return profile.getLocationTextColor();
  }
    /**
     * Profile wrapper: returns the background color of the location object
     */
  public Color getLocationBackColor() {
    return profile.getLocationBackColor();
  }
    /**
     * Profile wrapper: not in use
     */
  public Color getModeTextColor() {
    return profile.getModeTextColor();
  }
    /**
     * Profile wrapper: not in use
     */
  public Color getModeBackColor() {
    return profile.getModeBackColor();
  }
    /**
     * Profile wrapper: if true, use a bold font in location object
     */
  public boolean getLocationUseBold() {
    return profile.getLocationUseBold();
  }
    /**
     * Profile wrapper: not in use
     */
  public boolean getModeUseBold() {
    return profile.getModeUseBold();
  }
    /**
     * Profile wrapper: returns the size of the font used in the location object
     */
  public int getLocationFontSize() {
    return profile.getLocationFontSize();
  }
    /**
     * Profile wrapper: not in use
     */
    public int getModeFontSize() {
    return profile.getModeFontSize();
  }
    /**
     * Profile wrapper: returns the name of the font used in the location object
     */
    public String getLocationFontName() {
	return profile.getLocationFontName();
    }
    /**
     * Profile wrapper: returns the name of the user's private directory
     */
    public String getPrivateDir() {
	return profile.getPrivateDir();
    }

    /**
     * Profile wrapper: returns the host name defining the PIA server RH will interact with
     */
    public String getPiaHost() {
	return profile.getPiaHost();
    }
    /**
     * Profile wrapper: defines port for pia server
     */
    public int getPiaPort() {
	return profile.getPiaPort();
    }
    /**
     * Profile wrapper: returns the name of the pia agent 
     */
    public String getPiaAgentName() {
	return profile.getPiaAgentName();
    }
    public String getPiaPrinterName() {
	return profile.getPiaPrinterName();
    }
    /**
     * Profile wrapper: returns the main color used in the background
     */
    public Color getMainBackColor() {
	return mainFrame.mainBackColor;
    }
    /**
     * Profile wrapper: returns the main text color used by main frame
     */
  public Color getMainTextColor() {
    return mainFrame.mainTextColor;
  }
    /**
     * Profile wrapper: returns the main highlight color
     */
  public Color getMainHighlightColor() {
    return mainFrame.mainHighlightColor;
  }
    /**
     * Profile wrapper: returns main color used for shadows
     */
  public Color getMainShadowColor() {
    return mainFrame.mainShadowColor;
  }
    /**
     * Profile wrapper: return ssecondary shadow color
     */
  public Color getMainShadowColor2() {
    return mainFrame.mainShadowColor2;
  }
    /**
     * Profile wrapper: returns motif number.  motif is 1 or zero and provides two styles
     * of interface color, one based on a gray background (the default) and the other on
     * a lightgray background.
     */
  public int getMotifNumber() {
    return profile.getMotifNumber();
  }
    /**
     * Profile wrapper: returns user's home page URL
     */
  public String getHomeURL() {
    return profile.getHomeURL();
  }
    /**
     * Profile wrapper: returns the reduction size of the thumbar specified as a ratio of the 
     * size of the actual display.  For instance, 6 means 1/6th the size of the document area.
     */
  public int getLensViewFraction() {
    return profile.getLensViewFraction();
  }
    /**
     * Profile wrapper: returns the similalrity threshold value to use as the default
     */
  public int getSimilarityThreshold() {
      return (profile!=null ? profile.getSimilarityThreshold() : 0);
  }
    /**
     * Profile wrapper: returns the preferred width of the main window
     */
  public int getPreferredWidth() {
    return profile.getPreferredWidth();
  }
    /**
     * Profile wrapper: returns the preferred height of the main window
     */
  public int getPreferredHeight() {
    return profile.getPreferredHeight();
  }
    /**
     * Profile wrapper: returns the preferred x coordinate location of the main window
     */
  public int getPreferredX() {
    return profile.getPreferredX();
  }
    /**
     * Profile wrapper: returns the preferred y coordinate of the main window
     */
  public int getPreferredY() {
    return profile.getPreferredY();
  }
    /**
     * Profile wrapper: returns the document font name
     */
  public String getDocumentFontName() {
    return profile.getDocumentFontName();
  }
    /**
     * Profile wrapper: returns the document font size
     */
  public int getDocumentFontSize() {
    return profile.getDocumentFontSize();
  }
    /**
     * Profile wrapper: returns the user font name 
     */
  public String getUserFontName() {
    return profile.getDocumentFontName();
  }
    /**
     * Profile wrapper: returns the user font size 
     */
  public int getUserFontSize() {
    return profile.getDocumentFontSize();
  }
    /**
     * Profile wrapper: returns the color used in background of thumbar
     */
  public Color getOverviewWindowColor() {
    return profile.getOverviewWindowColor();
  }
    /**
     * Profile wrapper: returns the default color used for lines in thumbar 
     */
  public Color getOverviewWindowLineColor() {
    return profile.getOverviewWindowLineColor();
  }
    /**
     * Profile wrapper: returns the color used in the backgruond of the lens
     */
  public Color getOverviewLensColor() {
    return profile.getOverviewLensColor();
  }
    /**
     * Profile wrapper: returns the color used to specify annotations in the thumbar 
     */
  public Color getOverviewANOHColor() {
    return profile.getOverviewANOHColor();
  }
    /**
     * Profile wrapper: return sht elink color in the thumbar 
     */
  public Color getOverviewLinkColor() {
    return profile.getOverviewLinkColor();
  }
    /**
     * Profile wrapper: returns the color of lines in the lens
     */
  public Color getOverviewLensLineColor() {
    return profile.getOverviewLensLineColor();
  }

    /**
     * Profile wrapper: returns true if loading images (currently not in use)
     */
  public boolean getUseLoadImages() {
    return profile.getUseLoadImages();
  }

    public void setUserFirstName(String n) {
	profile.setUserFirstName(n);
    }	
    public void setUserLastName(String n) {
	profile.setUserLastName(n);
}
    public void setUserAccountName(String n) {
	profile.setUserAccountName(n);
    }
    public void setPath(String n) {
	profile.setPath(n);
    }
    public void setGifsPath(String n) {
	profile.setGifsPath(n);
    }
    public void setPrivateDir(String n) {
	profile.setPrivateDir(n);
    }
    public void setCacheDir(String n) {
	profile.setCacheDir(n);
    }
    public void setUseCacheDocuments(int n) {
	profile.setUseCacheDocuments(n);
    }
    public void setUseCacheImages(int n) {
	profile.setUseCacheImages(n);
    }
    public void setDocumentCacheSize(int n) {
	//System.out.println("Setting Cache Size to:"+ n);
	profile.setDocumentCacheSize(n);
	documentControl.setCacheSize(n);
    }
    public void setAnimateLogo(int n) {
	profile.setAnimateLogo(n);
    }
    public boolean getAnimateLogo() {
	return profile.getAnimateLogo();
    }
    public void setAutoLoadHomeURL(int n) {
	profile.setAutoLoadHomeURL(n);
    }
    public void setUseAnohDoubleLine(int n) {
	profile.setUseAnohDoubleLine(n);
    }
    public int getUseAnohDoubleLine() {
	return profile.getUseAnohDoubleLine();
    }
    public void setUseLinkDoubleLine(int n) {
	profile.setUseLinkDoubleLine(n);
    }
    public int getUseLinkDoubleLine() {
	return profile.getUseLinkDoubleLine();
    }
    /**
     * Profile wrapper: sets the flag for loading images in the document or not (currently does
     * not do anything).
     *
     *@param n set to true if you want to have browser load images
     */
    public void setLoadImages(boolean n) {
	profile.setLoadImages(n);
    }
    /**
     * Profile wrapper: sets the last modified date
     */
    public void setLastModified(String n) {
	profile.setLastModified(n);
    }
    public void setPopulateConcepts(int n) {
	profile.setPopulateConcepts(n);
    }
    public void setUseBetterImageScalingMethod(int n) {
	profile.setUseBetterImageScalingMethod(n);
    }
    public void setHighlightStyles(RH_HighlightStyle[] n) {
	profile.setHighlightStyles(n);
    }
    public void setDefaultHliteStyle(int n) {
	profile.setDefaultHliteStyle(n);
    }
    public void setSensitivitySetting(int n) {
	profile.setSensitivitySetting(n);
    }
    public void setLargeMeters(int n) {
	profile.setLargeMeters(n);
    }
    public void setLocationTextColor(Color n) {
	profile.setLocationTextColor(n);
    }
    public void setLocationBackColor(Color n) {
	profile.setLocationBackColor(n);
    }
    public void setModeTextColor(Color n) {
	profile.setModeTextColor(n);
    }
    public void setModeBackColor(Color n) {
	profile.setModeBackColor(n);
    }
    public void setLocationFontSize(int n) {
	profile.setLocationFontSize(n);
    }
    public void setLocationFontName(String n) {
	profile.setLocationFontName(n);
    }
    public void setMotifName(int n) {
	profile.setMotifName(n);
    }
    public void setHomeURL(String n) {
	profile.setHomeURL(n);
    }
    public void setOverviewWindowColor(Color n) {
	profile.setOverviewWindowColor(n);
    }
    public void setOverviewWindowLineColor(Color n) {
	profile.setOverviewWindowLineColor(n);
    }
    public void setOverviewLensColor(Color n) {
	profile.setOverviewLensColor(n);
    }
    public void setOverviewANOHColor(Color n) {
	profile.setOverviewANOHColor(n);
    }
    public void setOverviewLinkColor(Color n) {
	profile.setOverviewLinkColor(n);
    }
    public void setOverviewLensLineColor(Color n) {
	profile.setOverviewLensLineColor(n);
    }
    public void setLensViewFraction(int n) {
	profile.setLensViewFraction(n);
    }
    public void setSimilarityThreshold(int n) {
	profile.setSimilarityThreshold(n);
    }
    public void setPreferredWidth(int n) {
	profile.setPreferredWidth(n);
    }
    public void setPreferredHeight(int n) {
	profile.setPreferredHeight(n);
    }
    public void setPreferredX(int n) {
	profile.setPreferredX(n);
    }
    public void setPreferredY(int n) {
	profile.setPreferredY(n);
    }
    public void setDocumentFontName(String n) {
	profile.setDocumentFontName(n);
    }
    public void setDocumentFontSize(int n) {
	profile.setDocumentFontSize(n);
    }
    public void setPiaHost(String n) {
	profile.setPiaHost(n);
    }
    public void setPiaPort(int n) {
	profile.setPiaPort(n);
    }
    public void setPiaAgentName(String n) {
	profile.setPiaAgentName(n);
    }
    public void setPiaPrinterName(String n) {
	profile.setPiaPrinterName(n);
    }
    /**
     * Sets the default group index
     *
     *@param n default index number
     */
    public void setDefaultGroup(int n) {
	profile.setDefaultGroup(n);
    }
    public void setUseLensLogo(int n) {
	profile.setUseLensLogo(n);
    }

    /**
     * Method associated with the Open Document Collection toolbar button.  Currently has not
     * been implemented.
     */
  public void openDocumentCollection() {
    // willhave dialog box with access to personal document hierarchy
  }
    /**
     * Used to debug the linked list */
    public void printBoxes(Vector v) {
	
	System.out.println("*** Printing linked list...");

	for(int i=0; i < v.size();i++) 

	    System.out.println("Element "+i+"  "+v.elementAt(i));
	    
    }



    /**
     * Returns the current path for image directory
     */
  public String getGifsPath() {
    return profile.getGifsPath();
  }
    /**
     * Returns true is we are using the animation in the logo icon on the toolbar
     */
  public boolean animateLogo() {
    return profile.getAnimateLogo();
  }
    /**
     * Returns true if we are auto-loading the user's homepage. false means that we load a 
     * predefined HTML document contained in the currentpath/private_dir/images directory.
     */
  public boolean getAutoLoadHomeURL() {
    return profile.getAutoLoadHomeURL();
  }
    /**
     * Returns the size of the document cache
     */
  public int getDocumentCacheSize() {
    return profile.getDocumentCacheSize();
  }
    /**
     * Determines whether to populate the concept scores when a user browses to a document that has
     * previously been annotated and is part of their privat ecollection.  If this is false, then the 
     * we do not populate the concepts thus saving a bit of time needed to deserialize the concepts
     * and update the conecpt meters.  When the user selects the summary button, the population will
     * occur.
     */
  public boolean getPopulateConcepts() {
      return profile.getPopulateConcepts();
  }
    /**
     * This is set to true when using the enhanced image scaling methods for the thumbar.  When true
     * the images rendered int he thumbar look much better than before.  however, they take longer 
     * to render.
     */
  public boolean getUseBetterImageScalingMethod() {
      return profile.getUseBetterImageScalingMethod();
  }
    /**
     * Returns the RH_ConceptGroup object given the number specified.  The index number is based on
     * the groups.rh file's ordering and definition of the groups.
     *
     *@param num index for group definitions
     */
  public RH_ConceptGroup getGroup(int num) {
    return profile.getGroup(num);
  } 
    /**
     * Returns the whole collection of groups - RH_ConceptGroup[]
     */
  public RH_ConceptGroup[] getConceptGroups() {
    return profile.getConceptGroups();
  }

    public void addGroup(String name, int num, String tooltip, String[] newconcepts) {
	profile.addGroup(name,num,tooltip,newconcepts);
    }
    public void removeGroup(int idx) {
	profile.removeGroup(idx);
    }

    public void setGroupName(int idx, String newname) {
	profile.setGroupName(idx,newname);
    }
    public void setGroupToolTipString(int idx, String tip) {
	profile.setGroupToolTipString(idx,tip);
    }
    public void setGroupConcepts(int idx, String[] newconcepts) {
	profile.setGroupConcepts(idx,newconcepts);
    }

    /**
     * Returns the user defined default group index number
     */
  public int getDefaultGroup() {
    return profile.getDefaultGroup(); 
  }
    /**
     * Request that the RH_ConceptControl object select the default group radio button
     */
  public void selectDefaultGroup() {
    conceptControl.selectDefaultGroup();
  }
  public void enableRetroGroup() {
    conceptControl.enableRetroGroup();
  }
  public void disableRetroGroup() {
    conceptControl.disableRetroGroup();
  }
  public void selectRetroGroup(String[] names) {
    conceptControl.selectRetroGroup(names);
  }


    public int getNumberGroups() {
	return profile.getNumberGroups();
    }

    public int getUseLensLogo() {
	return profile.getUseLensLogo();
    }

  public boolean loadIsReload() {
    return reloadLoad;
  }

  /**
   * Display any message while debugging via this method;  basicvally, this provides the interface for System.out.println
   * however, because i use a flag, i can turn off these messages anytime
   */
  public void consoleMsg(String str) {
    consoleMsg(str,NORMAL_TYPE_MSG);
  }
  public void consoleMsg(String str, int msgtype) {
    String errorhdr="***ERROR:";
    String normalhdr="***";
    if (viewConsoleMessages) {
      if (msgtype==ERROR_TYPE_MSG) System.out.println(errorhdr+str);
      else System.out.println(normalhdr+str);
    }
  }

    /**
     * Sends a message to the status message area.  Area 1 is the largest area on the bottom left
     * side of the display.
     * 
     *@param msg - message
     */
  public void statusMsg1(String msg) {
    statusControl.message1(msg);
  }
    /**
     * Sends a message to the status message area.  Area 2 is the smaller area in the middle
     * of the display.
     * 
     *@param msg - message
     */
  public void statusMsg2(String msg) {
    statusControl.message2(msg);
  }

  public String getBrowserVersion() {
      return (documentControl!=null ? documentControl.getVersion() : "");
  }

  public void addCurrentLocation() {
    String url=getCurrentURL(), title=documentControl.getDocumentTitle();
    parent.addCurrentLocation(url,title);
  }

  public void setupLocationList(RH_LocationItem[] items) {
    parent.setupLocationList(items);
  }

    /**
     * This method is run after user releases the sensitivity meter. It update the necessary
     * objects and refreshes the screen.
     */
  public void newSensitivityValue(int val) {
    setWaitCursor();
    // Do not update if the value is not different from the current value
    if (val!=mainFrame.currentSensitivity) {
      mainFrame.currentSensitivity=val;
      // no need to refresh when not view annotations (i think)
      if (mainFrame.currentMode==RH_GlobalVars.annotationMode ||
	  mainFrame.currentMode==RH_GlobalVars.summaryMode || mainFrame.usingAnnotatedDocument()) {
	statusControl.message2(workingLabel);
	statusControl.message1("Changing the Sensitivity Threshold...Please Wait");
	refreshDocument(true);
	statusControl.message1("");
	statusControl.message2(doneLabel);
	//refreshDocument(false);  //3.29.98 not sure why i had second refresh; seems to work without it
      }
    }
    setDefaultCursor();
  }

  //************* BROWSER HOOK METHODS ***************


  /**
   * shows the URL location of the link currently pointed to by the mouse pointer
   */
  public void showLinkLocation (String urlstr) {
    if (statusControl!=null) statusMsg1(urlstr);
    //if (conceptControl!=null) conceptControl.showLinkLocation(urlstr,fetchALink(urlstr));
  }

    public void showLinkInPortal(String urlstr) {
	/*
	  if (portalConnection!=null) {
	  try {
	  portalConnection.sayThis(urlstr);
	  System.out.println("PortalConnection Replies with: "+portalConnection.sayHello());
	  } catch (Exception e) {
	  System.out.println("PortalConnection exception: " +e.getMessage());
	  }
	  }
	  else System.out.println("***ERR: portalConnection is NULL");
	*/
    }

    /**
     * Updates the load progress bar with the new value.
     *
     *@param val - new value
     */
  public void updateURLLoadProgress(int val) {
    statusControl.updateProgress(val);
  }
    /**
     * Reset load progress 
     */
  public void resetURLProgress() {
    if (statusControl!=null) statusControl.resetProgress();
  }

  /**
   * Get the current size of the document cache from the browser
   */
  public int getDocCacheSize() {
      // FIX THIS: 8.10.98 should now return number of items in cache
      return 0 ; //documentControl.getDocCacheSize();
  }

  public String[] getDocCacheItems() {
      // FIX THIS: 8.10.98 should now items in cache
      return null; // documentControl.getDocCacheItems();
  }
  public void showCacheList() {
      RH_CacheListDialog dialog=new RH_CacheListDialog(mainFrame);
  }

  public void showStatusMsg (String msg) {
    statusControl.message2(msg);
  }

  public void cacheNotEmpty() {
    if (profile.getUseCacheDocuments() || profile.getUseCacheImages()) {
      //System.out.println("------------Cache not empty");
      mainToolbar.cacheNotEmpty();
    }
    //mainFrame.setActiveSentences();
  }

  public void purgingDocuments(boolean set) {
    if (set) {
      setWaitCursor();
      statusControl.message2("Cleaning...");
    }
    else {
      setDefaultCursor();
      statusControl.message2("Done");
    }
  }

  public boolean getUseCacheDocuments() {
    return profile.getUseCacheDocuments();
  }
  public boolean getUseCacheImages() {
    return profile.getUseCacheImages();
  }

    /**
     * Clears the document cache.  This method is invoked by the cache button on the toolbar
     */
  public void clearDocCache() {
      setWaitCursor();
      statusMsg1("Clearing document cache...");
      statusMsg2(workingLabel);
      if (profile.getUseCacheDocuments() || profile.getUseCacheImages()) {
	  documentControl.clearDocCache();
	  mainToolbar.cacheEmpty();
	  mainFrame.lastDocumentKey=""; // reset this so that activeConcepts can be deserialized again
      }
      //mainFrame.setActiveSentences();
      mainToolbar.setMemoryToolTip();
      statusMsg1("");
      statusMsg2("");
      setDefaultCursor();
  }

  /**
   * Clear the internal document information cache; shouldonly be done when the actual document.browser.docCache gets cleared
   */
  public void clearInfoCache() {
    /*
    Enumeration enum = docInfoCache.elements();
    // First, i nullify all docus in the cache so the GC will act on them appropriately
    while (enum.hasMoreElements()) {
      RH_InfoCache item=(RH_InfoCache)enum.nextElement();
      item=null;
    }
    // then i clear the cache
    docInfoCache.clear();
    */
   }

  /**
   * Returns true if document is part of the user's collection of private documents
   */
  public boolean docIsPrivateDocument() {
    return (mainFrame.hasBeenAnnotated && !reloadLoad);
  }

  /**
   * Return either true or false depending whether we are in annotation mode or not
   */
  public boolean doAnnotation() {
    //System.out.println("+++HasBeenANOHed:"+hasBeenAnnotated+  " ConceptsActive:"+conceptsActive()+" preAnnotated="+preAnnotated);
    if (mainFrame.currentMode==RH_GlobalVars.annotationMode && mainFrame.currentMode!=RH_GlobalVars.summaryMode && mainFrame.conceptsActive()) {
      System.out.println("---REQUESTING ANNOTATION LOAD");
      return true; 
    }
    else {
      System.out.println("---REQUESTING NORMAL LOAD");
      return false; 
    }
  }

  public int getAnnotationBufferSize() {
    return mainFrame.getAnnotationBufferSize();
  }
    public void setAnnotationBuffer(byte[] buf) {
	mainFrame.setAnnotationBuffer(buf);
    }

  public byte[] matchConcepts(byte[] buffer, int len) {
    return mainFrame.matchConcepts(buffer,len);
  }

  /**
   * This method is run when a document is read and found to be an RH.ANNOTATION document; do what you need to do
   * with documents that have been pre-annotaed here
   */
  public boolean setUsingAnnotatedDocument(String pasturl, String ver) {
    return mainFrame.setUsingAnnotatedDocument(pasturl,ver);
  }
  public boolean setUsingAnnotatedIndex(String pasturl, String ver) {
    return mainFrame.setUsingAnnotatedIndex(pasturl,ver);
  }
  public boolean setUsingAnnotatedSummary(String orgurl, String ver) {
    return mainFrame.setUsingAnnotatedSummary(orgurl,ver);
  }
  public boolean setUsingHtmlDocument() {
    return mainFrame.setUsingHtmlDocument();
  }

  /**
   * Tells whether to highlight the whole sentence the phrase is in or not
   */
  public boolean useHighlightWholeSentences() {
    return mainFrame.highlightWholeSentence;
  }
  /**
   * Tells whether using bold/underline mthod of annotation
   */
  public boolean useUnderlineHighlight() {
    return mainFrame.underlineInHighlight;
  }
  /**
   * Tells whether using bold/underline mthod of annotation
   */
  public boolean useBoldOutlineHighlight() {
    return (mainFrame.getDocumentAnnotationType()==RH_GlobalVars.RH_Annotate_Outline ? true : false);
  }

  /**
   * Tells whether to use a bold font in the highlighting schemes or not;
   */
  public boolean useBoldFontInHighlight() {
    return mainFrame.boldInHighlight;
  }

  /**
   * Tells whether to use a drop shadow for the highlighted words to enhance their viewability
   */
  public boolean useHighlightDropShadow() {
    return mainFrame.highlightDropShadow;
  }

  /**
   * Returns the current state of a concept
   */
  public boolean isConceptActive(String conceptStr) {
    return mainFrame.isConceptActive(conceptStr);
  }
  /**
   * Returns the current state of the sentence
   */
  public boolean isSentenceActive(int loc) {
    return mainFrame.isSentenceActive(loc);
  }
  public Color getBackgroundColor() {
    return mainFrame.backgroundColor;
  }
  public Color getTextColor() {
    return mainFrame.textColor;
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
    return mainFrame.lineEmailTagColor;
  }
  public Color getLineURLTagColor() {
    return mainFrame.lineURLTagColor;
  }
  public Color getOverviewBoldLineColor() {
    return mainFrame.overviewBoldLineColor;
  }
  public Color getAnnotationColor() {
    return mainFrame.annotationColor;
  }
  public Color getSentenceAnnotationColor() {
    return mainFrame.sentenceColor;
  }
  public Color getHighlightColor() {
    return mainFrame.highlightColor;
  }
  public Color getBoldUnderlineColor() {
    return mainFrame.boldUnderlineColor;
  }
  public Color getHighlightShadowColor() {
    return mainFrame.highlightShadowColor;
  }
  
  /**
   * This method receives a line of text from the browser as it processes a new document.  This information
   * represents the size, x,y and color information of a word in the document.  This information is
   * passed to the thumbar and rendered in a reducted form (using the user defined reduction ratio).  
   * This is basically the pipeline of information sent fromthe browser to the thumbar each time a
   * new document is rendered or the current document is re-rendered (refreshed, resized, etc.)
   *
   *@param line actual text from document
   *@param x x coordinate
   *@param y y coordinate
   *@param h height 
   *@param w width
   *@param boxnum box number in linked list of boxes
   *@param fontType type of font specified for this text (bold, underline, etc.)
   */
  public void receiveLineString(String line, int x, int y, int h, int w, int boxnum, int fontType) {
    if (thumbarControl!=null) thumbarControl.receiveLineString(line, x, y, h, w, boxnum,fontType);
  }
    /**
   * This method receives a line of text from the browser as it processes a new document.  This information
   * represents the size, x,y and color information of a word in the document.  This information is
   * passed to the thumbar and rendered in a reducted form (using the user defined reduction ratio).  
   * This is basically the pipeline of information sent fromthe browser to the thumbar each time a
   * new document is rendered or the current document is re-rendered (refreshed, resized, etc.)
   *
   *@param line actual text from document
   *@param x x coordinate
   *@param y y coordinate
   *@param h height 
   *@param w width
   *@param color color of line
   *@param boxnum box number in linked list of boxes
   *@param fontType type of font specified for this text (bold, underline, etc.)
   */
  public void receiveLineString(String line, int x, int y, int h, int w, Color color, int boxnum,int fontType) {
    if (thumbarControl!=null) thumbarControl.receiveLineString(line, x, y, h, w, color, boxnum,fontType);
  }
    /**
   * This method receives a line of text from the browser as it processes a new document.  This information
   * represents the size, x,y and color information of a word in the document.  This information is
   * passed to the thumbar and rendered in a reducted form (using the user defined reduction ratio).  
   * This is basically the pipeline of information sent fromthe browser to the thumbar each time a
   * new document is rendered or the current document is re-rendered (refreshed, resized, etc.)
   *
   *@param image image to render
   *@param x x coordinate
   *@param y y coordinate
   *@param h height 
   *@param w width
   *@param color color of line
   *@param boxnum box number in linked list of boxes
   *@param fontType type of font specified for this text (bold, underline, etc.)
   */
  public void receiveLineString(Image image, int x, int y, int h, int w, Color color, int boxnum,int fontType) {
    if (thumbarControl!=null) thumbarControl.receiveLineString(image, x, y, h, w, color, boxnum,fontType);
  }
    /**
   * This method receives a line of text from the browser as it processes a new document.  This information
   * represents the size, x,y and color information of a word in the document.  This information is
   * passed to the thumbar and rendered in a reducted form (using the user defined reduction ratio).  
   * This is basically the pipeline of information sent fromthe browser to the thumbar each time a
   * new document is rendered or the current document is re-rendered (refreshed, resized, etc.)
   *
   *@param image image to render
   *@param x x coordinate
   *@param y y coordinate
   *@param h height 
   *@param w width
   *@param boxnum box number in linked list of boxes
   *@param fontType type of font specified for this text (bold, underline, etc.)
   */
  public void receiveLineString(Image image, int x, int y, int h, int w, int boxnum,int fontType) {
    if (thumbarControl!=null) thumbarControl.receiveLineString(image, x, y, h, w, boxnum,fontType);
  }
    /**
   * This method receives a line of text from the browser as it processes a new document.  This information
   * represents the size, x,y and color information of a word in the document.  This information is
   * passed to the thumbar and rendered in a reducted form (using the user defined reduction ratio).  
   * This is basically the pipeline of information sent fromthe browser to the thumbar each time a
   * new document is rendered or the current document is re-rendered (refreshed, resized, etc.)
   *
   *@param image to render
   *@param x x coordinate
   *@param y y coordinate
   *@param h height 
   *@param w width
   *@param color color of image
   *@param boxnum box number in linked list of boxes
   */
  public void receiveLineString(Image image, int x, int y, int h, int w, Color color, int boxnum) {
    if (thumbarControl!=null) thumbarControl.receiveLineString(image, x, y, h, w, color, boxnum);
  }

  public int getBrowserCanvasHeight() {
    return documentControl.getBrowserCanvasHeight();
  }
  public int getBrowserCanvasWidth() {
    //System.out.println("Trying to get browser width...");
    return documentControl.getBrowserCanvasWidth();
  }
  /*
  public FontMetrics getBrowserFontMetrics(int font_type) {
    return documentControl.getBrowserFontMetrics(font_type);
  }
  */
  public int getBrowserScrollerLocation() {
    return documentControl.getBrowserScrollerLocation();
  }

  public void showDocViewArea(int y) {
    documentControl.showDocViewArea(y);
  }

  /**
   * Sends the buffer sizeof the new document to docview
   */
  public void sendDocumentBufferSize(int bufferSize,boolean reset) {
   if (thumbarControl!=null) thumbarControl.sendDocumentBufferSize(bufferSize,reset);
  }

  /**
   * Sends a message to DocviewArea stating the last Y coordinate for the current document
   */
  public void sendBottomCoordinate(int lasty) {
    if (thumbarControl!=null) thumbarControl.sendBottomCoordinate(lasty);
  }

  /**
   * Sends a message to the thumbar telling it where the document has just been scrolled to in terms
   * of a line number.
   *
   *@param lineNum line number
   */
  public void showBrowserArea(int lineNum) {
    if (thumbarControl!=null) thumbarControl.showBrowserArea(lineNum);
  }

    /**
     * Returns the current Y position of the document in the browser
     */
  public int getScrollPositionY() {
    return documentControl.getScrollPositionY();
  }
    /**
     * Returns the current size of the document in terms of pixels
     */
  public Dimension getDocumentSize() {
    return documentControl.getDocumentSize();
  }
    /**
     * Returns the current size of the document that is visible on the screen
     */
  public Dimension getDocVisSize() {
    return documentControl.getDocVisSize();
  }

  /**
   * do somthing when an annotation is selected
   */
  public void annotationSelected (String conceptName,String topic, int number) {
    RH_Concept concept=mainFrame.findConcept(conceptName);
    if (concept!=null) {
	//System.out.println("Concept: "+concept.getName());
	//concept.printAnnotation(topic,number);
    }
  }
  public void annotationInfoRequested(String conceptName,String topic, int number, int x, int y) {
    RH_Concept concept=mainFrame.findConcept(conceptName);
    if (concept!=null) {
	//RH_KeywordsDialog dialog=new RH_KeywordsDialog(mainFrame,concept,x,y);
    }
  }

  //*********** END BROWSER HOOK METHODS *************

  public String getNewlineByte() {
    return mainFrame.getNewlineByte();
  }

  public void setSummaryMode() {
    mainFrame.currentMode=RH_GlobalVars.summaryMode;
    //modeControl.setModeLabel(RH_MainFrame.summaryLabel);
    mainFrame.showSummary();
    conceptControl.setAnnotateMode(true);
  }
  public void setPlainTextMode() {
    //modeControl.setModeLabel(RH_MainFrame.plainLabel);
      //mainToolbar.activatePlainText();
    mainFrame.currentMode=RH_GlobalVars.plainTextMode;
    conceptControl.setAnnotateMode(false);
  }
  public void setAnnotateMode() {
    //if (mainFrame.currentMode!=RH_GlobalVars.annotationMode) {
    //modeControl.setModeLabel(RH_MainFrame.annotateLabel);
    mainToolbar.activateAnnotation();
    mainFrame.currentMode=RH_GlobalVars.annotationMode;
    conceptControl.setAnnotateMode(true);
  }

  public Dimension getLocationControlSize() {
    if (locationControl!=null) return locationControl.getSize();
    else return new Dimension(0,0);
  }

  public void setupThumbar() {
    if (thumbarControl!=null) thumbarControl.setupThumbar();
  }
    /**
     * Returns the max number of visible lines the thumbar can display
     */
  public int getMaxNumberVisibleLines() {
    return (thumbarControl!=null ? thumbarControl.getMaxNumberVisibleLines() : 0);
  }

  public String getThumbarVersion() {  return (thumbarControl!=null ? thumbarControl.getVersion() : ""); }


 /**
   * Updates the history stack after each new document is loaded
   */
  public void updateHistory(String url, String title) {
      //System.out.println("****COMMBUS: UPDATE HISTORY");
    parent.updateHistory(url,title);
    //mainToolbar.moreBackDocuments();
  }

  /**
   * Returns the tooltip string associated with a highlighting style and defined int he profile.rh file
   */
  public String getHighlightStyleToolTip(int num) {
    return (num<profile.highlightStyles.length ? profile.highlightStyles[num].getTip() : "");
  }

    /**
     * Changes the current highlighting scheme in the annotated document
     *
     *@param num index number pointing to color scheme defined in profile.rh
     */
  public void changeColorScheme(int num) {
    if (mainFrame.currentMode==RH_GlobalVars.annotationMode ||
	mainFrame.currentMode==RH_GlobalVars.summaryMode || mainFrame.usingAnnotatedDocument()) {
      parent.setCursor(new Cursor(Cursor.WAIT_CURSOR));
      statusControl.message1("Changing highlighting and color scheme - Please wait");
      //System.out.println("***WholeSentences:"+mainFrame.highlightWholeSentence);
      statusControl.message2(workingLabel);
      mainFrame.changeColorScheme(num);
      refreshDocument(false);
      statusControl.message1("");
      statusControl.message2(doneLabel);
      parent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
  }

  public void updateConceptMeters() {
    conceptControl.updateConcepts();
  }

  public void setCalendarButton(boolean set) {
    mainToolbar.setCalendarButton(set);
  }
  public void setSimilarButton(boolean set) {
    mainToolbar.setSimilarButton(set);
  }

  public void resetConcepts() {
    mainFrame.resetConcepts();
    if (conceptControl!=null) {
	updateConceptMeters();
	conceptControl.resetFurtherReadingList();
    }
  }

  /**
   * turns an icon on when the file the user just pointed the browser to is a file that was once previously annotated and
   * is in the user's hsitory database
   */
  public boolean setPreviouslyAnnotatedIcon(boolean set) {
    //System.out.println("****COMMBUS SETANOHFLAG:"+ set);
    statusControl.setPreviouslyAnnotatedIcon(set);
    return set;
  }
  public void setDurationLabel(String str) {
    statusControl.setDurationLabel(str);
  }

  /**
   * enables or disables the highlighting controls
   */
  public void setHighlightControls(boolean set) {
    conceptControl.setHighlightControls(set);
  }


  /**
   * Updates the number of phrases (i.e. queries currently active for searching) label
   */
  public void updateNumberPhrasesLabel() {
    //System.out.println("ssssssssss> Number of Phrases:"+matchObject.conceptHashSize());
    //if (matchObject!=null) conceptControl.setPhrasesLabel(matchObject.conceptHashSize());
  }

  /**
   * This method opens a private document's info.rhi file and grabs the title, returning it to the caller
   */
  public String getPrivateDocumentTitle(String name) {
    String doctitle="";
    StringBuffer infofile=new StringBuffer().append(mainFrame.mainPath).append(mainFrame.privateANOHDir).append(mainFrame.rhPathSeparator).
      append(mainFrame.rhDocumentDir).append(mainFrame.rhPathSeparator).append(name).append(mainFrame.rhPathSeparator)
      .append(mainFrame.rhInfoFileName).append(mainFrame.rhInfoFileExt);
    try {
      BufferedReader input=new BufferedReader(new FileReader(infofile.toString()));
      input.readLine();
      doctitle=input.readLine(); // second line contains document title
      input.close();
    } catch (IOException ex) {
      consoleMsg("Could not open info file:"+infofile,RH_CommBus.ERROR_TYPE_MSG);
      doctitle="Error: could not open file";
    }
    return doctitle;
  }

  /**
   * This will eventually be the text search routine
   */
  public void searchText() {
      mainFrame.getActiveSentences();
      //long mem=Runtime.getRuntime().freeMemory();
      //statusControl.updateMemLabel(mem,"free");
      /*
      String str=null;
      boolean thumbardone=thumbarControl.thumbarDone();
      boolean check=thumbarControl.checkThumbarLoading();
      int stat=thumbarControl.getThumbarImageStatus();
      boolean checkThumbarAlive=documentControl.checkThumbarAlive();
      boolean checkParseAlive=documentControl.checkParseAlive();
      str=new String("Thumbardone:"+thumbardone+" check:"+check+" stat:"+stat+" TAlive:"+checkThumbarAlive+
		     " PAlive:"+checkParseAlive);
      statusMsg1(str);
      */
  }

    /**
     * Show the calendar interface: always loads the current month;  from there you can get to other months
     */
    public void viewCalendarInterface() {
	String buffer=requestProxyContent(RH_GlobalVars.piaProxyMsgGetCalendar);
	if (buffer!=null) {
	    statusControl.message1(loadingLabel + " calendar");   
	    statusControl.message2(loadingLabel);   
	    setPlainTextMode();
	    mainFrame.forceLoad=true;
	    URL_Process(buffer,false,true);
	}
	
	setSimilarButton(false);
	statusControl.message1("");   
	statusControl.message2(doneLabel);   
    }

    /**
     * Performs the "simple" document similarity gathering procedure using the files generated  and stored in the
     * private/similar subdirectory.  Presents user with document contained a table of similar documents to the 
     * document they were currently viewing.
     */
  public void showSimilarites() {
    statusControl.message1("Generating document similarity file...Please Wait");
    statusControl.message2(workingLabel);
    setWaitCursor();
    /*
      boolean available=similarity.generateSimilarities(mainFrame.currentDocumentKey,documentControl.getDocumentTitle(),getCurrentURL());
      if (available) {
      StringBuffer filename=new StringBuffer().append(mainFrame.httpFileTypeTag).append(mainFrame.mainPath).append(mainFrame.privateANOHDir)
      .append(mainFrame.rhPathSeparator).append(mainFrame.rhDocumentDir).append(mainFrame.rhPathSeparator)
      .append(mainFrame.currentDocumentKey).append(mainFrame.rhPathSeparator)
      .append(mainFrame.rhSimilarDir).append(mainFrame.rhHTMLExtension);
      
      setPlainTextMode();
      mainFrame.forceLoad=true;
      URL_Process(filename.toString(),false,true);
      statusControl.message2(doneLabel);
      statusControl.message1("");
      setSimilarButton(false);
      }
      else {
      statusControl.message2(doneLabel);
      statusControl.message1("No similar documents found in private archive");
      }
    */
    setDefaultCursor();
  }

    /**
     * Method associated with print button on toolbar.  Invokes print facility
     */
  public boolean printDocument() {
      RH_PrintDialog dialog=new RH_PrintDialog(this, documentControl.getDocumentTitle(), getCurrentURL());
    return true;
  }


    public void sendMail() {
	//RH_PrintDialog dialog=new RH_PrintDialog(this, documentControl.getDocumentTitle(), getCurrentURL());
    }


  /**
   * Adds the current link to the RAWHO server (and, for now, to a private file)
   */
  public void addRAWHOLink() {
    parent.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    setWaitCursor();
    statusControl.message1("Sending URL to RA~WHO...Please wait");
    statusControl.message2(workingLabel);
    //** Temp function for saving urls to a file that i can then use in the future
    String newline=mainFrame.getNewlineByte();
    StringBuffer errorMsg=new StringBuffer().append("FAILED To send URL to RAWHO - Reason: ");
    Date date=new Date();
    //** Make string for private/bucket.html
    StringBuffer inputString=new StringBuffer().append("<li><a href=\"").append(getCurrentURL()).append("\">").
      append(documentControl.getDocumentTitle()).append("</a> <font size=-1>(").append(date.toString()).append(")</font>").append(newline);
    StringBuffer filename=new StringBuffer().append(mainFrame.mainPath).append(mainFrame.privateANOHDir).append(mainFrame.rhPathSeparator).
      append("rawho.html");
    try {
      BufferedWriter fp=new BufferedWriter(new FileWriter(filename.toString(),true));
      fp.write(inputString.toString(),0,inputString.length());
      fp.close();
    } catch (IOException ex) {
      consoleMsg("Could not add to buck file:"+filename,RH_CommBus.ERROR_TYPE_MSG);
    }

    //** Now create the RAWHO Link and send the information
    //http://ookami:8888/RAWHO/add_url.if?id=addurl&title=Foo&url=http://jlkjlk/.ljlk
    String encodedURL=URLEncoder.encode(getCurrentURL()), encodedTitle=URLEncoder.encode(documentControl.getDocumentTitle()), encodedCategory="";
    StringBuffer url=new StringBuffer().append(mainFrame.rhRAWHOURLString.toString()).append("&title=")
      .append(encodedTitle).append("&url=").append(encodedURL).append("&contributor=").append(profile.getUserAccountName());
    
    System.out.println("ENCODED:"+url.toString());
    try {
      URL rawhoURL=new URL(url.toString());
      System.out.println("...URL OK");
      try {
	URLConnection connect=rawhoURL.openConnection();
	System.out.println("...URL Connected");
	connect.setDoOutput(true);
	System.out.println("...Writing output");
	try {
	  InputStream inputStream = connect.getInputStream();
	  BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));
	  String type = connect.getContentType();
	  System.out.println("Message Sent: content type="+type);
	  String line="";
	  //** This captures the returning URL that the PIA sends back when you add a link.  I do not care about this right now.
	  /*
	  while ((line=input.readLine())!=null) {
	    //System.out.println(">>"+line);
	    output.write(line,0,line.length());
	  }
	  output.flush();
	  output.close();
	  */
	  input.close();
	  inputStream.close();
	} catch (UnknownHostException e) {
	  System.out.println("Unknown host : "+e.getMessage());
	  statusControl.message1(errorMsg.toString()+"Unknown host");
	  return; 
	} catch(NoRouteToHostException e) {
	  System.out.println("An error occurred while connecting to "+
			     url.toString()+" The server responded with:"+e.getMessage());
	  statusControl.message1(errorMsg.toString()+"URL exception");
	  return;
	} catch(IOException e) {
	  System.out.println("IO exception "+e);
	  e.printStackTrace();
	  System.out.println("Cannot connect to "+url.toString());
	  statusControl.message1(errorMsg.toString()+"URL exception");
	  return; 
	}
      } catch  (IOException ioex) {
	System.out.println("***Could not connect to RAWHO URL:"+url.toString());
	statusControl.message1(errorMsg.toString()+"IO exception");
      }
    } catch  (MalformedURLException mex) {
      System.out.println("***Could not create RAHOW URL:"+url.toString());
      statusControl.message1(errorMsg.toString()+"URL exception");
    }
    statusControl.message2(doneLabel);
    statusControl.message1("");
    setDefaultCursor();
  }

    /**
     * Adds the current link to a location file called bucket.html where i keep potentially interesting links.
     */
  public void addBucketLink() {
    //** Temp function for saving urls to a file that i can then use in the future
    String newline=mainFrame.getNewlineByte();
    Date date=new Date();
    //** Make string for private/bucket.html
    StringBuffer inputString=new StringBuffer().append("<li><a href=\"").append(getCurrentURL()).append("\">").
      append(documentControl.getDocumentTitle()).append("</a> <font size=-1>(").append(date.toString()).append(")</font>").append(newline);
    StringBuffer filename=new StringBuffer().append(mainFrame.mainPath).append(mainFrame.privateANOHDir).append(mainFrame.rhPathSeparator).
      append("bucket.html");
    try {
      BufferedWriter fp=new BufferedWriter(new FileWriter(filename.toString(),true));
      fp.write(inputString.toString(),0,inputString.length());
      fp.close();
    } catch (IOException ex) {
      consoleMsg("Could not add to buck file:"+filename,RH_CommBus.ERROR_TYPE_MSG);
    }
  }

  /**
   * Adds the current link to the RAWHO server (and, for now, to a private file)
   */
  public void addIM3Link() {
    Dialog dialog=new Dialog(parent,"Adding "+getCurrentURL()+" to IM^3...");
    dialog.show();
    parent.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    setWaitCursor();
    statusControl.message1("Sending URL to IMP...Please wait (this takes about 1 minute to complete)");
    statusControl.message2(workingLabel);
    //** Temp function for saving urls to a file that i can then use in the future
    String newline=mainFrame.getNewlineByte();
    StringBuffer errorMsg=new StringBuffer().append("FAILED To send URL to IM3 - Reason: ");
    StringBuffer im3String=new StringBuffer().append("http://").append(mainFrame.rhIM3HomeURLString.toString()).
      append(mainFrame.rhIM3AddURLString.toString()).append(mainFrame.rhIM3UsernameString.toString()).
      append(profile.getUserAccountName()).append("&").append(mainFrame.rhIM3SrcURLString.toString()).
      append(getCurrentURL());
    Date date=new Date();
    //** Now create the IM3 Link and send the information
    System.out.println("IM3 URL:"+im3String.toString());
    try {
      URL im3URL=new URL(im3String.toString());
      System.out.println("...URL OK");
      try {
	URLConnection connect=im3URL.openConnection();
	System.out.println("...URL Connected");
	connect.setDoOutput(true);
	System.out.println("...Writing output");
	try {
	  InputStream inputStream = connect.getInputStream();
	  BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));
	  String type = connect.getContentType();
	  System.out.println("Message Sent: content type="+type);
	  String line="";
	  //** This captures the returning URL sent back by the IM3 CGI
	  if ((line=input.readLine())==null) consoleMsg("Nothing returned from IM3",ERROR_TYPE_MSG);
	  input.close();
	  inputStream.close();

	  //** Make string for private/log file
	  StringBuffer inputString=new StringBuffer().append("<li><a href=\"").append(getCurrentURL()).append("\">").
	    append(documentControl.getDocumentTitle()).append("</a> <ul><br><a href=\"").append(line).append("\">IM3 Link</a><br>Date:").
	    append(date.toString()).append(")</ul>").append(newline);
	  StringBuffer filename=new StringBuffer().append(mainFrame.mainPath).append(mainFrame.privateANOHDir).append(mainFrame.rhPathSeparator).
	    append("im3.html");
	  //System.out.println("OUTPUT:"+inputString.toString());
	  try {
	    BufferedWriter fp=new BufferedWriter(new FileWriter(filename.toString(),true));
	    fp.write(inputString.toString(),0,inputString.length());
	    fp.close();
	  } catch (IOException ex) {
	    consoleMsg("Could not add to IM3 file:"+filename,RH_CommBus.ERROR_TYPE_MSG);
	  }

	} catch (UnknownHostException e) {
	  System.out.println("Unknown host : "+e.getMessage());
	  statusControl.message1(errorMsg.toString()+"Unknown host");
	  return; 
	} catch(NoRouteToHostException e) {
	  System.out.println("An error occurred while connecting to "+
			     im3String.toString()+" The server responded with:"+e.getMessage());
	  statusControl.message1(errorMsg.toString()+"URL exception");
	  return;
	} catch(IOException e) {
	  System.out.println("IO exception "+e);
	  e.printStackTrace();
	  System.out.println("Cannot connect to "+im3String.toString());
	  statusControl.message1(errorMsg.toString()+"URL exception");
	  return; 
	}
      } catch  (IOException ioex) {
	System.out.println("***Could not connect to IM3 URL:"+im3String.toString());
	statusControl.message1(errorMsg.toString()+"IO exception");
      }
    } catch  (MalformedURLException mex) {
      System.out.println("***Could not create IM3 URL:"+im3String.toString());
      statusControl.message1(errorMsg.toString()+"URL exception");
    }
    statusControl.message2(doneLabel);
    statusControl.message1("");
    setDefaultCursor();
    dialog.dispose();
  }

  public void turnOffSomeButtons() {
      //mainToolbar.setCalendarButton(false);
      //mainToolbar.setSimilarButton(false);
  }
  public void turnOnSomeButtons() {
    mainToolbar.setCalendarButton(true);
    mainToolbar.setSimilarButton(true);
  }



  public void setWaitCursor() {
    parent.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    mainFrame.setWaitCursor();
    if (documentControl!=null) documentControl.setWaitCursor();
    if (conceptControl!=null) conceptControl.setWaitCursor();
  }
  public void setDefaultCursor() {
    parent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    mainFrame.setDefaultCursor();
    if (documentControl!=null) documentControl.setDefaultCursor();
    if (conceptControl!=null) conceptControl.setDefaultCursor();
  }

 
  public boolean inTextMode() {
    return (mainFrame.currentMode==RH_GlobalVars.plainTextMode ? true : false);
  }
  public boolean inAnnotateMode() {
    return (mainFrame.currentMode==RH_GlobalVars.annotationMode ||
	    mainFrame.currentMode==RH_GlobalVars.summaryMode ? true : false);
  }
  
  /**
   * Returns the current mode the browser is in
   */
  public int getCurrentMode() {
    return mainFrame.currentMode;
  }

    /**
     * These are really temporary and should be deleted at some point - 4-16-98
     */
    public void setUseSentenceLocation(boolean set) {
	mainFrame.setUseSentenceLocation(set);
    }
    public boolean getUseSentenceLocation() {
	return mainFrame.getUseSentenceLocation();
    }

    /**
     * Toggles the WebPortal
     */
    public void setShowPortal(boolean show) {
	conceptControl.setShowPortal(show);
    }
    public boolean getShowPortal() {
	return parent.getShowPortal();
    }

    public Image fetchALink(String urlstr) {
	return null;
    }
    public void updatePortalLoadProgress(int val) {
	conceptControl.updatePortalLoadProgress(val);
    }
    public void resetPortalProgress () {
	conceptControl.resetPortalProgress();
    }
    /**
     * return the pluggable look and feel currently being used by the system
     */
    public String getLAF() {
	return parent.getLAF();
    }

    //public int getHistorySize() {
    //return (historyDB!=null ? historyDB.getHistorySize() : 0 );
    //}
    public void updateHistorySizeLabel() {
	if (statusControl!=null) statusControl.updateHistorySizeLabel();
    }

    public String getCurrentDocumentKey() {
	return mainFrame.currentDocumentKey;
    }

    public void parentInvalidate() {
	parent.invalidate();
    }
    public void parentValidate() {
	parent.validate();
    }

    /**
     * Updates the contents of the annotation buffer when the document was not annotated.  annotation buffer
     * is updated automatically when a document is annotated.  the annotation buffer is now used in printing of 
     * the document and is the buffer passed to the PIA server
     */
    public void updateDocumentBuffer(byte[] buf) {
	if (mainFrame!=null) mainFrame.annotationBuffer=buf;
    }

    public boolean getUseLexicon() {
	return mainFrame.useLexicon;
    }
    public void setUseLexicon(boolean set) {
	mainFrame.setUseLexicon(set);
    }

    public String getParsingProgress() {
	return documentControl.getParsingProgress();
    }

    public String getFirstBackDocument() {
	return ((mainFrame.historyPointer+1)<mainFrame.historyList.length ? mainFrame.historyList[mainFrame.historyPointer+1].getURL() : null);
    }
    public String getFirstFwdDocument() {
	return (mainFrame.historyPointer>0 ? mainFrame.historyList[mainFrame.historyPointer-1].getURL() : null);
    }

    public void updateTitle(String title) {
	mainFrame.setDocumentTitle(title);
    }
    public int getCacheSize() {
	return documentControl.getCacheSize();
    }
    public int getCurrentCacheSize() {
	return documentControl.getCurrentCacheSize();
    }
    public boolean okToProcess() {
	return parent.okToProcess();
    }
    public int getDocumentsBrowsed() {
	return documentControl.getDocumentsBrowsed();
    }
    public int getHistoryLoad() {
	return historyLoad;
    }

    public int getThumbarImageStatus() {
	return thumbarControl.getThumbarImageStatus();
    }
    public void setThumbarStatusStart() {
	thumbarControl.setThumbarStatusStart();
    }
    public void setThumbarStatusDone() {
	thumbarControl.setThumbarStatusDone();
    }
    public boolean thumbarDone() {
	return thumbarControl.thumbarDone();
    }

    public byte[] getAnnotationBuffer() {
	return mainFrame.getAnnotationBuffer();
    }
    public String getActiveSentences() {
	return mainFrame.getActiveSentences();
    }
    public String getActiveConcepts() {
	return mainFrame.getActiveConcepts();
    }
    public int getCurrentHliteStyle() {
	return conceptControl.getCurrentHliteStyle();
    }
    public String getDocumentTitle() {
	return (documentControl!=null ? documentControl.getDocumentTitle() : "");
    }
    public boolean checkThumbarLoading() {
	return (thumbarControl!=null ? thumbarControl.checkThumbarLoading() : true);
    }
    public String getRHVersion() {
	return mainFrame.makeVersion();
    }

    public boolean saveGroups() {
	boolean save=profile.saveGroups();
	if (save) conceptControl.updateConceptGroups();
	return save;
    }
    public boolean saveConcepts() {
	boolean save=profile.saveConcepts();
	if (save) conceptControl.updateConceptInformation();
	return save;
    }
    public boolean saveProfile() {
	boolean save=profile.saveProfile();
	//if (save) do somerthing
	return save;
    }
    /**
     * This is called after the profile has been updated; it updates all necessary gui
     * components.  put whatever needs updating here.
     */
    public int updateGUIComponents() {
	int success=1;
	thumbarControl.updateThumbar(); 

	return success;
    }

    public String[] getHistoryStack() {
	return documentControl.getHistoryStack();
    }
    public String[] getCacheStack() {
	return documentControl.getCacheStack();
    }

    /**
     * Request that the user's concepts be sent form the proxy server
     */
    public String requestProxyContent(String message) {
	return requestProxyContent(message,null);
    }
    public String requestProxyContent(String message, byte[] buffer) {
	StringBuffer url=new StringBuffer("http://"+profile.getProxyServerName()+":"+profile.getProxyServerPort()+"/"+profile.getProxyAgentName());
	HttpURLConnection httpConnect=null;
	boolean success=true, sendConcepts=false, sendthreshold=false, storeresults=false, sensitivity=false, urlflag=false;
	String pString=null, conceptsList=null, title=null, thresholdString="0", sensitivityString="0", urlstr="";
	if ((title=getCurrentTitle())=="") title="notitle";
	urlstr=getCurrentURL();
	//** Request that the document being sent in the buffer be annotated
	if (message.equalsIgnoreCase(RH_GlobalVars.piaProxyMsgProcessContent)) {
	    sendConcepts=true;
	    urlflag=sensitivity=true;
	    conceptsList=mainFrame.generateActiveConceptsList();
	    //System.out.println(">>>"+conceptsList);
	}
	//** Request that the results from the search be stored
	else if (message.equalsIgnoreCase(RH_GlobalVars.piaProxyMsgStoreResult)) {
	    urlflag=storeresults=true;
	    thresholdString=new String(""+getSimilarityThreshold());
	    System.out.println("...title:"+title+" thres:"+ thresholdString);
	    System.out.println("url:"+getCurrentURL()+" ver:"+mainFrame.makeVersion());
	    buffer=message.getBytes();
	}
	//** send the similarity threshold value to the server
	else if (message.equalsIgnoreCase(RH_GlobalVars.piaProxyMsgPutThreshold)) {
	    urlflag=sendthreshold=true;
	    thresholdString=new String(""+getSimilarityThreshold());
	    System.out.println("Threshold: "+thresholdString);
	    buffer=message.getBytes();
	}
	else if (message.equalsIgnoreCase(RH_GlobalVars.piaProxyMsgPutSensitivity)) {
	    urlflag=sensitivity=true;
	    if (mainFrame.preAnnotated) urlstr=mainFrame.pastAnnotatedURL;
	    sensitivityString=new String(""+getSensitivitySetting());
	    buffer=message.getBytes();
	}
	else if (message.equalsIgnoreCase(RH_GlobalVars.piaProxyMsgGetAnohFileConcepts) ||
		 message.equalsIgnoreCase(RH_GlobalVars.piaProxyMsgGetAnohFileSentences)) {
	    urlflag=true;
	    urlstr=mainFrame.pastAnnotatedURL;
	    System.out.println("...PastURL: "+urlstr);
	    buffer=message.getBytes();
	}
	else buffer=message.getBytes();
	
	System.out.print("...Sending message to agent: "+message);
	
	try {
	    URL piaURL=new URL(url.toString());
	    httpConnect=(HttpURLConnection)piaURL.openConnection();
	    //System.out.println("...connection created: "+piaURL+" -> "+httpConnect+"...buflen="+buffer.length);
	    httpConnect.setRequestMethod("POST");
	    httpConnect.setRequestProperty("Content-Length",new String(buffer.length+""));
	    httpConnect.setRequestProperty("Accept","*/*");
	    httpConnect.setRequestProperty("Content-Type",RH_GlobalVars.rhContentHeaderName);
	    httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeProxyName,profile.getProxyServerName());
	    httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeProxyPort,new String(""+profile.getProxyServerPort()));
	    httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeProxyAgent,profile.getProxyAgentName());
	    httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeUser,profile.getProxyUserName());
	    httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeMsg,message);
	    httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeTitle,title);
	    if (sendConcepts) {
		httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeClientVersion,mainFrame.makeVersion());
		httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeConcepts,conceptsList);
		//httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeURL,getCurrentURL());
		httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeThreshold,thresholdString);
	    }
	    if (storeresults) {
		httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeClientVersion,mainFrame.makeVersion());
		//httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeURL,getCurrentURL());
		httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeThreshold,thresholdString);
	    }
	    if (sendthreshold) {
		httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeClientVersion,mainFrame.makeVersion());
		httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeThreshold,thresholdString);
	    }
	    if (sensitivity) httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeSensitivity,sensitivityString);
	    if (urlflag) httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeURL,urlstr);

	    httpConnect.setRequestProperty(RH_GlobalVars.rhContentTypeBufferLen,new String(buffer.length+""));
	    //httpConnect.setRequestProperty("Content-RH-Buffer",URLEncoder.encode(new String(strBuf.length()+"")));
	    httpConnect.setDoOutput(true);
	    httpConnect.connect();
	    //System.out.println("...connection instantiated...");
	    BufferedOutputStream output = new BufferedOutputStream(httpConnect.getOutputStream());
	    output.write(buffer,0,buffer.length);
	    output.flush();
	    output.close();
	    //OutputStream output=httpConnect.getOutputStream();
	    InputStream input=httpConnect.getInputStream();
	    System.out.println("...Response: "+httpConnect.getResponseMessage());
	    RH_FileContents fc=new RH_FileContents();
	    pString=fc.grabFileContents(input);
	    //System.out.println(pString);
	    //----------------
	    //System.out.println("...response to writing output:"+httpConnect.getResponseMessage());
	    input.close();
	} catch (MalformedURLException ex) {
	    System.out.println("Bad URL:"+url.toString());
	    //new RH_PopupError(parent,"Proxy Error, bad url: "+url.toString());
	    success=false;
	}  catch (BindException bex) {
	    System.out.println("Bind exception:"+bex);
	    success=false;
	}  catch (ConnectException cex) {
	    System.out.println("Connect exception:"+cex);
	    success=false;
	}  catch (NoRouteToHostException nex) {
	    System.out.println("No Route exception:"+nex);
	    success=false;
	}  catch (ProtocolException pex) {	    
	    System.out.println("Protocol exception:"+pex);
	    success=false;
	}  catch (SocketException sex) {	    
	    System.out.println("Socket exception:"+sex);
	    success=false;
	}  catch (UnknownHostException uex) {	    
	    System.out.println("Unknown host exception:"+uex);
	    success=false;
	}  catch (UnknownServiceException usex) {	    
	    System.out.println("Unknown Service exception:"+usex);
	    success=false;
	} catch (Exception e) {
	    System.out.println("");
	    System.out.println("**----------------------------------------------------------");
	    System.out.println("**--ERROR: "+ e);
	    System.out.println("**--");
	    System.out.println("**--Proxy server "+profile.getProxyServerName()+":"+profile.getProxyServerPort()+" possibly not reachable");
	    System.out.println("**----------------------------------------------------------");
	    //new RH_PopupError(parent,"Proxy Error: "+e);
	    success=false;
	}
	
	return pString;
    }

    public void updateConceptValues(String conceptInfo) {
	mainFrame.updateConceptValues(conceptInfo);
    }

    public void updateConceptsWithSentenceData(String buffer) {
	//System.out.println("***Updating concepts sentences...");
	StringTokenizer tokens=new StringTokenizer(buffer," ");
	RH_Concept concept=null;
	String name=null, numstr=null;
	int num=0, i=0;
	Vector vector=null;

	while (tokens.hasMoreTokens()) {
	    name=(String)tokens.nextToken();
	    numstr=(String)tokens.nextToken();
	    num=Integer.parseInt(numstr);
	    concept=mainFrame.findConcept(name);
	    if (num>0 && concept!=null) {
		vector=new Vector();
		for (i=0;i<num;i++) {
		    numstr=(String)tokens.nextToken();
		    vector.addElement(new Integer(Integer.parseInt(numstr)));
		}
		//System.out.println(">"+concept.getShortName()+" processed "+vector.size());
		concept.setSentenceVector(vector);
	    }
	}
    }
}

  

