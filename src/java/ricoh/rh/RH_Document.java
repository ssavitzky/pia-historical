/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: RH_Document: container for ice.htmlbrowser.Document
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 4.24.97 - revised 02-06-98
 *
 */
package ricoh.rh;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;

import ice.htmlbrowser.RHBrowser;
//import ice.htmlbrowser.BrowserTools;

public class RH_Document extends Panel { 
    // 8.10.98 version 4: this does not exist
    //implements ice.htmlbrowser.BrowserContext {

    public String theVersion = "";//"3.084RH_1a";
  public transient RH_CommBus commBus;
  public ice.htmlbrowser.RHBrowser browser;
  public transient RH_MainFrame parent;

  // Annotation Headers
  public final static String RH_ANNOTATION_BEGIN=RH_GlobalVars.RH_DocumentHeader_BeginTag;
  public final static String RH_ANNOTATION_END=RH_GlobalVars.RH_DocumentHeader_EndTag;
  public final static String RH_ANNOTATION_GROUPSUMMARY_BEGIN=RH_GlobalVars.RH_GroupSummaryHeader_BeginTag;
  public final static String RH_ANNOTATION_GROUPSUMMARY_END=RH_GlobalVars.RH_GroupSummaryHeader_EndTag;
  public final static String RH_ANNOTATION_SUMMARY_BEGIN=RH_GlobalVars.RH_SummaryHeader_BeginTag;
  public final static String RH_ANNOTATION_SUMMARY_END=RH_GlobalVars.RH_SummaryHeader_EndTag;
  public final static String RH_ANNOTATION_URL=RH_GlobalVars.RH_DocumentHeader_URLTag;
  public final static String RH_ANNOTATION_VER=RH_GlobalVars.RH_DocumentHeader_VERTag;
  public final static String RH_ANNOTATE_SENTENCE_BEGIN=RH_GlobalVars.RH_ANNOTATE_SENTENCE_BEGIN;
  public final static String RH_ANNOTATE_SENTENCE_END=RH_GlobalVars.RH_ANNOTATE_SENTENCE_END;
  public final static String RH_ANNOTATE_SENTENCE_NUMBER=RH_GlobalVars.RH_ANNOTATE_SENTENCE_NUMBER;
  public final static String RH_ANNOTATE_BEGIN=RH_GlobalVars.RH_ANNOTATE_BEGIN; 
  public final static String RH_ANNOTATE_END=RH_GlobalVars.RH_ANNOTATE_END; 
  public final static String RH_CONCEPT_TAG=RH_GlobalVars.RH_CONCEPT_TAG; 
  public final static String RH_TOPIC_TAG=RH_GlobalVars.RH_TOPIC_TAG; 
  public final static String RH_SCORE_TAG=RH_GlobalVars.RH_SCORE_TAG; 
  public final static String RH_NUMBER_TAG=RH_GlobalVars.RH_NUMBER_TAG; 
  public final static String RH_SENTENCE_TAG=RH_GlobalVars.RH_SENTENCE_TAG; 
  public final static String RH_EMAIL_TAG_BEGIN=RH_GlobalVars.RH_EMAIL_TAG_BEGIN; 
  public final static String RH_URL_TAG_BEGIN=RH_GlobalVars.RH_URL_TAG_BEGIN; 
  public final static String RH_EMAIL_TAG_END=RH_GlobalVars.RH_EMAIL_TAG_END; 
  public final static String RH_URL_TAG_END=RH_GlobalVars.RH_URL_TAG_END; 

  public final static int RH_Annotate_Highlight=RH_GlobalVars.RH_Annotate_Highlight, RH_Annotate_BoldType=RH_GlobalVars.RH_Annotate_BoldType,
    RH_Annotate_BoldUnderline=RH_GlobalVars.RH_Annotate_BoldUnderline, RH_Annotate_Balloon=RH_GlobalVars.RH_Annotate_Balloon,
    RH_Annotate_ContinueType=RH_GlobalVars.RH_Annotate_ContinueType, RH_Annotate_Outline=RH_GlobalVars.RH_Annotate_Outline;
  public final static String RH_BulletString="?";

  protected Hashtable docCache = new Hashtable();
  protected Stack backHistory = new Stack();
  protected Stack fwdHistory = new Stack();
  private int width, height, docCacheSize=0, maxDocCacheSize=0;
  private String fname = "readme.txt";
  private String textBuffer="";
  private Label modeLabel, titleLabel, pageLabel, pageCountLabel;
  private Color labelBackColor=Color.lightGray, labelForeColor=Color.blue;
  private String fontName="TimesRoman";
  private String documentTitle="no title yet";

  private boolean memoryCaching=true, lensMoved=false, newDocument=false;
  private Font defaultFixedFont, defaultPropFont;
  
  public RH_Document (RH_CommBus bus, RH_MainFrame newParent, int x, int y, int w, int h) {
    //super();
    parent=newParent;
    commBus=bus;
    commBus.documentControl=this;
    setLayout(new BorderLayout());
    width=w; height=h;

    //*** NEED TO CHANEG: 8.9.98
    setMemoryCaching(commBus.getUseCacheDocuments());
    setSize(width,height);
    //** This creates a new document and puts it into this Panel
    browser=new RHBrowser(this,commBus.getDocumentCacheSize());
    add("Center",browser);
    browser.setProportionalFont(new Font(commBus.getDocumentFontName(),Font.PLAIN,commBus.getDocumentFontSize()));
    gotoLocation(commBus.getHomeURL());
    theVersion=browser.theVersion;
    System.out.println("***ICE VERSION:"+browser.theVersion);
    setBackground(Color.lightGray);  // it's important to set this color because the motif and metal versions show scrollbars in this color!
  }

  public void addNotify() {
    super.addNotify();
    System.out.println("DOC AddNotify");
    commBus.setupThumbar();
  }

  /*
  public Dimension getPreferredSize() {
   return  new Dimension(width,height);
  }*/

  //*** BrowserContext Methods ****

  /**
   * Goes to a specified location in top frame. The location can be relative (in this
   * case the current DocumentBase will be used as a base or absolute.
   * @url Location to go to
   */
      /*
	public void gotoLocation(URL url) {
	gotoLocation(url,"_top");
	}
      */
  public void gotoLocation(String urlstr) {
      gotoLocation(urlstr,"_top","");
  }

  /**
   * Goes to a specified location in the specified frame. The location can be relative (in this
   * case the current DocumentBase will be used as a base or absolute.
   * @url Location to go to

   public void gotoLocation(URL url, String targetFrame) {
   gotoLocation(url,targetFrame,null);
   }
  */

  public void gotoLocation(String urlstr, String targetFrame, String outputString) {
    commBus.setWaitCursor();
    browser.gotoLocation(urlstr);

    // Update current URL
    updateCurrentURL();
  }

  public void updateTitle(String title) {
    documentTitle=title;
    commBus.updateTitle(title);
    // FIX THIS: 8.11.98: not sure where frame comes from at the moment
    //Frame f = getFrame();
    //if (f!=null) f.setTitle(commBus.getUserFirstName()+"'s "+RH_GlobalVars.RH_TitleString+" - "+title);
  }

  public String getDocumentTitle() {
    return documentTitle;
  }

  public void updateCurrentURL() {
  }
    
  public boolean isMemoryCaching() {
    return(memoryCaching);
  }

  private URL referer;
  public void setReferer(URL ref) {
    referer = ref;
  }
  
  public URL getReferer() { return referer; }

  /**
   * Refresh the browser window
   */
  public void refresh() {
    invalidate();
    validate();
  }

  public void setParsingDone(boolean flag) {
    if (flag) commBus.doneProcessing();
  }

  public void setToDone() {
    commBus.setToDone();
    browser.refresh(false); /// true???
  }

    public void setPortalToDone() {
	// update progress meter in RH_WebPortal
    }

  /**
   * Sends a message to DocviewArea stating the last Y coordinate for the current document
   */
  public void sendBottomCoordinate(int lasty) {
    commBus.sendBottomCoordinate(lasty);
  }

  public static String makeCacheKey(URL url) {
    String key = url.toString();
    if (url.getRef()!=null)
      key = key.substring(0,key.lastIndexOf("#"));
    return key;
  }

    /* 8.10.98 not use in version ice4.0
  public Frame getFrame() {
    return BrowserTools.findFrame(this);
  }
    */

  /**
   * Reloads the current document
   */
  public void reload() {
      browser.reload();
  }

  /**
   * Goes back in history list
   */
  public void goBack() {
      browser.goBack();
  }

  /**
   * Goes forward in history list
   */
  public void goForward() {
      browser.goForward();
  }

  private void updateCurrentHistoryEntry() {
    //		if (doc!=null) doc.updateCurrentHistoryEntry();
  }
  
  public String getCurrentLocation() {
    if (browser==null) return("");
    return browser.getCurrentLocation();
  }

  public String getCurrentTitle() {
    return documentTitle;
  }

  /**
   * Stops loading of HTML file
   */
  public void stopLoading() {
    if (browser!=null) browser.htmlInterrupt();

  }

  public String getVersion() {
    return theVersion;
  }

  public String getBrowserName() {
    StringBuffer str=new StringBuffer().append("Ricoh RH Java Browser (v.").append(theVersion).append(")");
    return str.toString();
  }

  /**
   * Get current document base
   * @return Document base
   */
  public URL getDocumentBase() {
      return (browser!=null ? browser.getDocumentBase() : null);
  }

    public void setDocumentBase(String urlstr) {
	System.out.println("%%%%%%%%%%%%%%%%%Setting document base:"+urlstr);
	if (browser!=null) {
	    try { browser.setDocumentBaseString(urlstr); }
	    catch (MalformedURLException ex) {
		System.out.println("***ERR: Could not set document base to: "+urlstr);
	    }
	}
    }
  
  /**
   * Cleans up the cache after annotating a document; there are times when the cache contains documents which
   * are related to the annotated document; specifically the summary & index files.  if the document gets annotated
   * again, these files become invalid because the contents of the summaries most likely changed.  this method purifies
   * the contents of the cache by removing these old entires.
   *
   * 8.9.98 LOOK HERE: may need to reimplement purify method with new caching scheme : jg
   *   
   public void purifyCache(String path,String indexname,String summaryname,String groupedname,String scoresname,String htmlext,
   String calfile, String timelinefile) {
   System.out.println("=-=-=-=-Purifying Cache:"+path);
   try {
   //****IF YOU ADD MORE SUMMARY FILES, ADD THEM HERE!!!
	 URL indexurl=new URL(path+indexname+htmlext), 
	 summaryurl=new URL(path+summaryname+htmlext),
	 groupedurl=new URL(path+groupedname+htmlext),
	 scoresurl=new URL(path+scoresname+htmlext),
	 calurl=new URL(calfile),
	 timelineurl=new URL(timelinefile);
	 
	 removeDocFromCache(indexurl.toString(),true);
	 removeDocFromCache(summaryurl.toString(),true);
	 removeDocFromCache(groupedurl.toString(),true);
	 removeDocFromCache(scoresurl.toString(),true);
	 removeDocFromCache(calurl.toString(),true);
	 removeDocFromCache(timelineurl.toString(),true);
	 } catch (MalformedURLException ex) {
	 System.out.println("***ERROR: could not create URLS in purify method");
	 }
	 
	 }
  */


  /**
   * Enables or disables memory caching
   * @param flag If <TT>true</TT> memory caching is enabled
   */
  public void setMemoryCaching(boolean flag) {
    memoryCaching=flag;
  }

  //*** END CACHING STUFF ***

  public void setWaitCursor() {
    if (browser!=null) browser.setWaitCursor();
  }
  public void setDefaultCursor() {
    if (browser!=null) browser.setDefaultCursor();
  }

  public void showStatusMsg(String msg) {
    commBus.showStatusMsg(msg);
  }

  public String getNewlineByte() {
    return commBus.getNewlineByte();
  }

  public void updateURLLoadProgress(int val) {
    commBus.updateURLLoadProgress(val);
  }
  public void resetURLProgress () {
    commBus.resetURLProgress();
  }

  public void updatePortalLoadProgress(int val) {
    commBus.updatePortalLoadProgress(val);
  }
  public void resetPortalProgress () {
    commBus.resetPortalProgress();
  }

  public boolean setUsingAnnotatedDocument(String pasturl, String ver) {
    return commBus.setUsingAnnotatedDocument(pasturl,ver);
  }
  public boolean setUsingAnnotatedIndex(String pasturl, String ver) {
    return commBus.setUsingAnnotatedIndex(pasturl,ver);
  }
  public boolean setUsingAnnotatedSummary(String orgurl, String ver) {
    return commBus.mainFrame.setUsingAnnotatedSummary(orgurl,ver);
  }
  public boolean setUsingHtmlDocument() {
    return commBus.setUsingHtmlDocument();
  }

  /**
   * Tells whether to highlight the whole sentence the phrase is in or not
   */
  public boolean useHighlightWholeSentences() {
    return commBus.useHighlightWholeSentences();
  }
  /**
   * Tells whether using bold/underline mthod of annotation
   */
  public boolean useUnderlineHighlight() {
    return commBus.useUnderlineHighlight();
  }
  /**
   * Tells whether using bold/underline mthod of annotation
   */
  public boolean useBoldOutlineHighlight() {
    return commBus.useBoldOutlineHighlight();
  }
  /**
   * Tells whether to use a bold font in the highlighting schemes or not;
   */
  public boolean useBoldFontInHighlight() {
    return commBus.useBoldFontInHighlight();
  }
  /**
   * Tells whether to use a drop shadow for the highlighted words to enhance their viewability
   */
  public boolean useHighlightDropShadow() {
    return commBus.useHighlightDropShadow();
  }
  /**
   * Returns the current state of a concept
   */
  public boolean isConceptActive(String conceptStr) {
    return commBus.isConceptActive(conceptStr);
  }
  /**
   * Returns the current state of the sentence
   */
  public boolean isSentenceActive(int loc) {
    return commBus.isSentenceActive(loc);
  }

  public Color getBackgroundColor() {
    return commBus.getBackgroundColor();
  }
  public Color getTextColor() {
    return commBus.getTextColor();
  }
  public Color getLinkColor() {
    return commBus.getLinkColor();
  }
  public Color getLineAnnotationColor() {
    return commBus.getLineAnnotationColor();
  }
  public Color getLineSentenceTagColor() {
    return commBus.getLineSentenceTagColor();
  }
  public Color getLineEmailTagColor() {
    return commBus.getLineEmailTagColor();
  }
  public Color getLineURLTagColor() {
    return commBus.getLineURLTagColor();
  }
  public Color getOverviewBoldLineColor() {
    return commBus.getOverviewBoldLineColor();
  }
  public Color getAnnotationColor() {
    return commBus.getAnnotationColor();
  }
  public Color getSentenceAnnotationColor() {
    return commBus.getSentenceAnnotationColor();
  }
  public Color getHighlightColor() {
    return commBus.getHighlightColor();
  }
  public Color getBoldUnderlineColor() {
    return commBus.getBoldUnderlineColor();
  }
  public Color getHighlightShadowColor() {
    return commBus.getHighlightShadowColor();
  }
  public Color getOverviewWindowColor() {
    return commBus.getOverviewWindowColor();
  }
  /**
   * Receives a line of text from the browser as it processes a new document
   */
  public void receiveLineString(String line, int x, int y, int h, int w, int boxnum, int fontType) {
    commBus.receiveLineString(line, x, y, h, w, boxnum,fontType);
  }
  public void receiveLineString(String line, int x, int y, int h, int w, Color color, int boxnum,int fontType) {
    commBus.receiveLineString(line, x, y, h, w, color, boxnum,fontType);
  }
  public void receiveLineString(Image image, int x, int y, int h, int w, Color color, int boxnum,int fontType) {
    commBus.receiveLineString(image, x, y, h, w, color, boxnum,fontType);
  }
  public void receiveLineString(Image image, int x, int y, int h, int w, int boxnum,int fontType) {
    commBus.receiveLineString(image, x, y, h, w, boxnum,fontType);
  }
  public void receiveLineString(Image image, int x, int y, int h, int w, Color color, int boxnum) {
    commBus.receiveLineString(image, x, y, h, w, color, boxnum);
  }  

  /**
   * Sends a message to the lens describing, via y, where we have just scrolled the browser scrollba
   */
  public void showBrowserArea(int lineNum) {
    //System.out.println("...Browser got scroll position:"+lineNum);
    commBus.showBrowserArea(lineNum);
  }
  public int getBrowserCanvasHeight() {
    if (browser!=null) {
      Dimension size = browser.getSize();
      return size.height;
    }
    else return 0;
  }
  public int getBrowserCanvasWidth() {
    if (browser!=null) {
      Dimension size = browser.getSize();
      System.out.println("**DOC SIZE W="+size.width+" H="+size.height);
      return size.width;
    }
    else return 0;
  }
  /*
  public FontMetrics getBrowserFontMetrics(int font_type) {
    FontMetrics fm=null;
    Font font=fi.getFont(font_type);
    if (font!=null) return panel1.canvas.getFontMetrics(font);
    else return null;
  }
  */

  /**
   * requests the Y location of the vertical scrollbar so that the thumbar can be updated
   */
  public int getBrowserScrollerLocation() {
    return (browser!=null ? browser.getBrowserScrollerLocation() : 0);
  }

  /**
   * Move Scrollers to location specified by the Docview window
   */
  public void showDocViewArea(int y) {
    System.out.println("---Requesting DOC to MOVETO: "+y);
    if (browser!=null) {
      // use this flag to notify the DocContainer (who handles the actualy scroll) that the lens moved.  if this
      // flag is not set then the scroll was a result of the user using the scroll bar or controlling movement
      // via the keyboard.
      browser.lensMoved=true;  
      browser.scrollTo(0,y);
      browser.lensMoved=false;
    }
  }

  /**
   * Sends the buffer sizeof the new document to docview
   */
  public void sendDocumentBufferSize(int bufferSize,boolean reset) {
    commBus.sendDocumentBufferSize(bufferSize,reset);
  }

  /**
   * shows the URL location of the link currently pointed to by the mouse pointer
   */
  public void showLinkLocation (String urlstr) {
   commBus.showLinkLocation(urlstr);
  }

  public void refreshDocument(boolean redoLayout) {
    //commBus.refreshDocument(redoLayout);
      //System.out.println("*****JUST CALLED REFRESH IN RH_DOC");
    if (browser!=null) {
      setWaitCursor();
      browser.refresh(redoLayout);
      browser.sendThumbarLineInfo(false, "Working...");
      setDefaultCursor();
      //System.out.println("*****REFRESH IN RH_DOC COMPLETE");
    }
  }

  public boolean backHistoryAvailable() {
    if (backHistory.size()>0) return true;
    else return false;
  }
  public boolean fwdHistoryAvailable() {
    if (fwdHistory.size()>0) return true;
    else return false;
  }

  public void setNewDocumentFlag(boolean flag) {
    newDocument=flag;
  }

  public boolean doAnnotation() {
    return commBus.doAnnotation();
  }

  public int getAnnotationBufferSize() {
    return  commBus.getAnnotationBufferSize();
  }

  public byte[] matchConcepts(byte[] buffer, int len) {
    return commBus.matchConcepts(buffer,len);
  }

  public boolean rhPrintDocument(Frame frame) {
    Properties printProp= new Properties();
    PrintJob pj = getToolkit().getPrintJob(frame, "ICE Browser", printProp);
    if (pj != null && browser!=null) {
      //printDoc(pj);
      //doc.setVisible(false);
      browser.setSize(pj.getPageDimension().width,10000);
      browser.printDoc(pj);
      pj.end();
      return true;
    }
    else return false;
  }

  /**
   * shows the URL location of the link currently pointed to by the mouse pointer
   */
  public void showAnnotation (String concept,String topic, int number) {
    commBus.statusMsg1("Concept: " + concept + "->" + topic + " [" + number + "] ");
  }
  
  /**
   * do somthing when an annotation is selected
   */
  public void annotationSelected (String conceptName,String topic, int number) {
    commBus.annotationSelected(conceptName,topic,number);
  }
  /**
   * Called when user selects a annoatted phrase ina document
   */
  public void annotationInfoRequested(String conceptName,String topic, int number, int x, int y) {
    commBus.annotationInfoRequested(conceptName,topic,number,x,y);
  }

    /**
     * Fetch a thumbnail image based on a link(url) string
     */
    public Image fetchALink(String urlstr) {
	return null; //doc.fetchALink(urlstr);
    }

    public void showLinkInPortal(String urlstr) {
	commBus.showLinkInPortal(urlstr);
    }
    public void showLinkInPortal(URL url) {
	commBus.showLinkInPortal(url.toString());
    }

    public RHBrowser getCurrentDocument() {
	return browser; //.getDocContainer();
    }
    public Vector getDocumentLinkedList() {
	return browser.getDocumentLinkedList();
    }
    public ice.htmlbrowser.Box getBoxListHead() {
	return browser.getBoxListHead();
    }

    public void printSetup() {
	browser.printSetup();
    }

    public void updateDocumentBuffer(byte[] buf) {
	//System.out.println("0000000000000000000 UPDATE DOC BUFFER 0000000000000000000000");
	commBus.updateDocumentBuffer(buf);
    }

    /**
     *  This tag should match the "name" tag used in RH_MatchConcepts when creating a meta tag
     */
    public String getRHDocumentTagName() {
	return RH_GlobalVars.RH_DocumentHeader_BeginTag;
    }

    /**
     * Returns the current Y position of the document in the browser
     */
    public int getScrollPositionY() {
	return browser.getScrollPositionY();
    }
    /**
     * Returns the current size of the document in terms of pixels
     */
    public Dimension getDocumentSize() {
	return browser.getDocumentSize();
    }
    /**
     * Returns the current size of the document that is visible on the screen
     */
    public Dimension getDocVisSize() {
	return browser.getDocVisSize();
    }

    /**
     * Loading a new document
     */
    public void loadingNewDocument(String urlstr) {
	System.out.println("*****RH_Doc: loading new document...");
	//** 8.18.98 not sure if i need to set these vars anymore
	/*
	boolean reloadingDocument=commBus.loadIsReload();
	boolean isPrivateDocument=commBus.docIsPrivateDocument();
	boolean documentInCache=false;
	boolean annotateDocument=commBus.doAnnotation();
	boolean cachingOn=isMemoryCaching();
	 */

	commBus.startingToProcess(urlstr);
    }

    public void loadingBackHistoryDocument() {
	System.out.println("*****RH_Doc: loading back document...");
	commBus.loadingBackHistoryDocument();
    }
    public void loadingFwdHistoryDocument() {
	System.out.println("*****RH_Doc: loading fwd document...");
	commBus.loadingFwdHistoryDocument();
    }
    public void loadingHistoryDocument(int idx) {
	System.out.println("*****RH_Doc: loading history document...");
	commBus.loadingHistoryDocument(idx);
    }


    public String getParsingProgress() {
	return browser.getParsingProgress();
    }


    public String getFirstBackDocument() {
	return commBus.getFirstBackDocument();
    }
    public String getFirstFwdDocument() {
	return commBus.getFirstFwdDocument();
    }
    public int getCacheSize() {
	return browser.getCacheSize();
    }
    public int getCurrentCacheSize() {
	return browser.getCurrentCacheSize();
    }
    public void setCacheSize(int n) {
	browser.setCacheSize(n);
    }
    public void clearDocCache() {
	browser.clearCache();
    }
    public boolean okToProcess() {
	return commBus.okToProcess();
    }
    public void htmlWait(boolean all) {
	System.out.println("...Waiting for document loading to complete...");
	browser.htmlWait(all);
	System.out.println("...Document loading to completed!...");
    }
    public void callSetToDone() {
	browser.setToDone();
    }
    public int getDocumentsBrowsed() {
	return browser.getDocumentsBrowsed();
    }

    public int getHistoryLoad() {
	return commBus.getHistoryLoad();
    }

    public int getThumbarImageStatus() {
	return commBus.getThumbarImageStatus();
    }

    public void setThumbarStatusStart() {
	commBus.setThumbarStatusStart();
    }
    public void setThumbarStatusDone() {
	commBus.setThumbarStatusDone();
    }
    public boolean thumbarDone() {
	return commBus.thumbarDone();
    }

    /**
     * RH Mod: 11-6-97 jmg
     * This is straight out of "Java: Networking and Communications by T. Courtois, Prentice Hall"
     */
    public String makeByteBuffer(Reader in) {
	StringBuffer output=new StringBuffer();
	int bytesRead=1, bufsize=1024;
	char[] buf = new char[bufsize];
	String str=null;
	try {
	    while ((bytesRead=in.read(buf,0,bufsize))!=-1) {
		if (bytesRead>0) output.append(new String(buf));
	    } 
	    in.reset();
	    in.close(); // do not close because i need to use this stream in the Parser
	} catch (IOException ex) {
	    System.out.println("Could not read from input stream in covert in to byte[]");
	}
	return output.toString(); //output.toByteArray();
    }

    public byte[] makeByteBuffer(InputStream in) {
	ByteArrayOutputStream output=new ByteArrayOutputStream();
	int bytesRead=1, bufsize=1024;
	byte[] buf = new byte[bufsize];
	try {
	    bytesRead=1;
	    do {
		bytesRead=in.read(buf,0,bufsize);
		if (bytesRead>0) {
		    output.write(buf,0,bytesRead);
		    output.flush();
		}
	    } while (bytesRead>=0);
	    
	    in.close(); // do not close because i need to use this stream in the Parser
	    output.close();
	} catch (IOException ex) {
	    System.out.println("Could not read from input stream in covert in to byte[]");
	}
	return output.toByteArray();
    }
    

    public void setHistoryLoad(boolean set){
	browser.setHistoryLoad(set);
    }
    public boolean checkThumbarAlive() {
	return browser.checkThumbarAlive();
    }
    public boolean checkParseAlive() {
	return browser.checkParseAlive();
    }

    public String[] getHistoryStack() {
	return browser.getHistoryStack();
    }
    public String[] getCacheStack() {
	return browser.getCacheStack();
    }
}
