/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: RH_Thumbar.java
 *@author Jamey Graham (jamey@rsv.ricoh.com)
 *@version 6.14.97
 * Desc: This is the thumbar, overview window showing a thumbnail version of the document
 *
 * Old array (from original RH) version
 *
 */
package ricoh.rh;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;

/**
 * The RH_Thumbar class provides both the canvas and the lens the thumbar.
 *
 *@author <a href="mailto:jamey@rsv.ricoh.com">
 *@version 6.14.97
 */
public class RH_Thumbar extends Canvas
implements ActionListener, MouseListener, MouseMotionListener, ImageObserver {
  private String version="";

  private RH_ThumbarControl parent;
  private RH_CommBus commBus;
  private RH_LineInfo[] lineInfo;
    private MediaTracker imageTracker;
    private int imageTrackerCount;
  private Graphics backGC, topGC, leftGC, bottomGC, rightGC;
  private Image backBuffer=null, topImage=null, leftImage=null, bottomImage=null, rightImage=null;
  private int docview_w,docview_h, offset_horz,offset_vert,left_offset,mode_h,
    zoomWindow_y=50,docview_x,docview_y, docview_bottom, zoomWindow_w,zoomWindow_h, zoomWindowInside_h,topImageHeight,bottomImageHeight,
    // this is the number of vertical pixels to shift the document canvas when the zoom window it "pushed"
    // at the bottom or the top (i.e. when the document is larger than the display
    vertShift=0, topLensLocation=0,
    // used when drawing a line at the user's last location
    lastLoc_x=0, lastLoc_y=0, previousLastLoc_y=0, lastLens_y=0, linesLeftInDoc=0,
    // this is the height of the top border of the window
    lensTopBorder_h=0;
  private Color documentColor = Color.white, //new Color(245,247,248),
      windowLensColor=Color.gray, lensLineColor=Color.black, windowLineColor=Color.gray, windowLinkColor=Color.blue, windowANOHColor=Color.red,
      offGray=new Color(160,160,164), longDocumentColor=Color.green, lastLocationColor=Color.red, boldLineColor=Color.black;
  private Color backColor=Color.gray, textColor=Color.white, highlightColor=Color.white, shadowColor=Color.black,
	shadowColor2=Color.gray;
  private Image tlCorner, trCorner, blCorner, brCorner, ricohR;
  private int line_newy=0, line_lasty=0, maxLineLen=0, maxNumberLines=0, maxNumberVisibleLines=0, lineWidthOffset=18,
    linex=0, liney=0, startLinex=0, startLiney, lineInfoLen=0, lineInfoPtr=0, wasDragging=-1, 
    // fraction by which to reduce our overview window by (set using parent's preddefined values)
    reductionValue=0, 
    lineOffsetY=8,
    // subtracted from length of canvas for maxNumberVisibleLine length (i.e. number of lines from the bottom not to draw in)
      visibleLinesVertOffset=9, //12,
    // this is the flag which determines which size docview to use: 0=small, 1=large
    sizeValue=0, showLineLoc=0, showLineLocOffset=0, width=0, height=0, motif=0, lastY=0;
  private String holdLine, filenameTag;
  private boolean drawExtraLine=true, noMoreScroll=false, resizingDocument=false,
    // longDocument is set when the document is longer than the docview display (has more lines and so we have to use scrolling)
    longDocument=false, useMarquee=true, useAnohDoubleLine=false, useLinkDoubleLine=false,
    // whether to draw a line at the user's last location or not
    useLastLocation=false, bufferSet=false, colorDocumentLens=true, moreInfoLineDraw=false, drawExtraLinkLine=true;
  private FontMetrics fontMetrics=null;
  private RH_LineInfo lastLine,topLine;
    private boolean betterImageScaling=false;
  
    /**
     *@param docview parent
     *@param sizeVal not in use ?? 6.15.98
     *@param off_h horizontal offset
     *@param off_v vertical offset
     *@param l_off offset fromleft side of screen
     *@param modeLabel_h not in use
     */
  RH_Thumbar(RH_ThumbarControl docview, int sizeVal, int off_h, int off_v, int l_off, int modeLabel_h) {
    parent=docview;
    commBus=parent.commBus;
    imageTracker=new MediaTracker(this);
    imageTrackerCount=0;
    backColor=commBus.getMainBackColor();
    textColor=commBus.getMainTextColor();
    highlightColor=commBus.getMainHighlightColor();
    shadowColor=commBus.getMainShadowColor();
    shadowColor2=commBus.getMainShadowColor2();
    documentColor=commBus.getOverviewWindowColor();
    windowLensColor=commBus.getOverviewLensColor();
    lensLineColor=commBus.getOverviewLensLineColor();
    windowANOHColor=commBus.getOverviewANOHColor();
    windowLineColor=commBus.getOverviewWindowLineColor();
    windowLinkColor=commBus.getOverviewLinkColor();
    boldLineColor=commBus.getOverviewBoldLineColor();
    if (commBus.getUseBetterImageScalingMethod()) betterImageScaling=true;
    else betterImageScaling=false;

    //** TEMP: i'm using the unused cacheImages flag to determine whether the marquee gets used or not
    if (commBus.getUseLensLogo()==1) useMarquee=true;
    else useMarquee=false;
    if (commBus.getUseAnohDoubleLine()==1) useAnohDoubleLine=true;
    else useAnohDoubleLine=false;
    if (commBus.getUseLinkDoubleLine()==1) useLinkDoubleLine=true;
    else useLinkDoubleLine=false;
    //moreInfoLineDraw=commBus.getANOHWindowMoreInfo();

    sizeValue=sizeVal;
    motif=commBus.getMotifNumber();
    reductionValue=parent.reductionValue;

    //if (reductionValue>6) drawExtraLinkLine=false;
    //    else drawExtraLinkLine=true;

    offset_horz=off_h;
    offset_vert=off_v;
    left_offset=l_off;
    mode_h=modeLabel_h;
    Dimension size = parent.getSize();
    width=size.width;
    height=size.height-offset_vert;
    setSize(width,height);
    setBackground(backColor);
    maxNumberVisibleLines=size.height-offset_vert-visibleLinesVertOffset;

    addMouseListener(this);
    addMouseMotionListener(this);

    tlCorner=Toolkit.getDefaultToolkit().getImage(commBus.getGifsPath()+"/tlcorner.gif");
    trCorner=Toolkit.getDefaultToolkit().getImage(commBus.getGifsPath()+"/trcorner.gif");
    blCorner=Toolkit.getDefaultToolkit().getImage(commBus.getGifsPath()+"/blcorner.gif");
    brCorner=Toolkit.getDefaultToolkit().getImage(commBus.getGifsPath()+"/brcorner.gif");
    ricohR=Toolkit.getDefaultToolkit().getImage(commBus.getGifsPath()+"/ricoh-r.gif");

    zoomWindow_h=0;
    zoomWindowInside_h=zoomWindow_h;
    topImageHeight=0;
    startLinex=left_offset+6;
    startLiney=4;
    lineInfoPtr=0;
    repaint();
    //System.out.println("&&&LENS:Calling setupBuffer in constructor");
    setupBuffer();
  }

    /**
     * Sets up the thumbar with size & dimension information already contained inthe class
     */
  public void setupThumbar() {
    System.out.println("***THUMBAR: Setting up...");
    setupBuffer();
  }

    /**
     * Called after profile is updated to update the thumbar.  put anything that is associated with
     * the profile in here.
     */
    public void updateThumbar() {
	invalidate();
	documentColor=commBus.getOverviewWindowColor();
	windowLensColor=commBus.getOverviewLensColor();
	lensLineColor=commBus.getOverviewLensLineColor();
	windowANOHColor=commBus.getOverviewANOHColor();
	windowLineColor=commBus.getOverviewWindowLineColor();
	windowLinkColor=commBus.getOverviewLinkColor();
	boldLineColor=commBus.getOverviewBoldLineColor();
	if (commBus.getUseBetterImageScalingMethod()) betterImageScaling=true;
	else betterImageScaling=false;
	if (commBus.getUseLensLogo()==1) useMarquee=true;
	else useMarquee=false;
	if (commBus.getUseAnohDoubleLine()==1) useAnohDoubleLine=true;
	else useAnohDoubleLine=false;
	if (commBus.getUseLinkDoubleLine()==1) useLinkDoubleLine=true;
	else useLinkDoubleLine=false;
	reductionValue=parent.reductionValue=commBus.getLensViewFraction();
	setupBuffer();
	validate();
	refreshDocument();
	//setToDone();
    }

    /**
     * Sets up the class, initializing variables and creating the dimension of the thumbar view 
     * area and the lens.
     */
  private boolean setupBuffer () {  
      //System.out.println("***SETUP BUFFER");
    boolean success=true;
    Dimension size = parent.getSize();
    bufferSet=true;
    width=size.width;
    height=size.height-offset_vert;
    docview_w=width-offset_horz-left_offset;
    docview_h=commBus.getBrowserCanvasHeight()-1;//-5;
    
    bottomImageHeight=5;
    topImageHeight=4;
    
    zoomWindow_w=docview_w+4;  
    zoomWindow_h=(int)(commBus.getBrowserCanvasHeight()/reductionValue)+topImageHeight+bottomImageHeight;
    zoomWindowInside_h=zoomWindow_h-(topImageHeight+bottomImageHeight);
    success=rebuildLens(backGC);
    try {   
      //set up image for double buffering
	//System.out.println("Making BackGC...");
      backBuffer = createImage (width,height);
      backGC = backBuffer.getGraphics();

      //System.out.println("!!!!!!!Canvas_W="+commBus.getBrowserCanvasWidth()+" Lens_W="+docview_w);
      return success;
    } catch (Exception e) { 
      bufferSet=false;
      commBus.consoleMsg("Could not setupBuffer in Thumbar",RH_CommBus.ERROR_TYPE_MSG);
      //e.printStackTrace ();
      return success=false;
    }
  }
    

  /**
   * when the whole frame of the window is resized, the docview gets this message so we can recalc the info
   * in the docview window
   */
  public void documentResized() {
    //System.out.println("===Thumbar: resizing...");
    resizingDocument=true;
    Dimension parentSize = parent.getSize();
    maxLineLen=parentSize.width-offset_horz-lineWidthOffset;   // set the line length for the docview lines that represent the text
    maxNumberVisibleLines=parentSize.height-offset_vert-visibleLinesVertOffset;
    if (setupBuffer()) {
	System.out.println("***THUMBAR RESIZING");
      showBrowserArea(commBus.getBrowserScrollerLocation());
      update(getGraphics());
      resizingDocument=false;
    }
    //System.out.println("Lens: Document resized: maxNumberVisibleLines="+maxNumberVisibleLines);
    // if this is the first time create the data structure
    /*
    if (lineInfoPtr==0) {
      lineInfo = new RH_LineInfo[maxNumberLines];
    }
    */
    //System.out.println("Docview Initialized");
  }
  
    /**
     * Do this when a document is done loading
     */
  public void setToDone() {
    resizingDocument=true;
    //System.out.print("Lens: Set to done: resizingDocument:"+resizingDocument);
    //System.out.println("&&&LENS:Calling setupBuffer in setToDone");
    // If we can setup the buffer then it's ok to do the following;  otherwise, the graphics context may not be available yet
    if (setupBuffer()) {
      //** This may be redundant because i set this up at the end of commBus.setToDone()
      showBrowserArea(commBus.getBrowserScrollerLocation());
      update(getGraphics());
      resizingDocument=false;
      //System.out.println("*** setup worked: resizingDocument:"+resizingDocument+" loc:"+commBus.getBrowserScrollerLocation());
    }
    else commBus.consoleMsg("Setup FAILED***",RH_CommBus.ERROR_TYPE_MSG);
    //commBus.parent.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    parent.setCursor(new Cursor(Cursor.HAND_CURSOR));
  }
  public void refreshDocument() {
    //System.out.println("Lens: Refreshing document");
    update(getGraphics());
  }

  /**
   * Sets the max number of lines for the document view datastructure
   *
   *@param bufferSize size of the document buffer in terms of linked-list items
   *@param resetZoomWindow if true, will reset lens to top of thumbar
   */
  public void sendDocumentBufferSize(int bufferSize, boolean resetZoomWindow) {
    Graphics gc=getGraphics();  // cleanup before processing the new document
    if (gc!=null) getGraphics().dispose();  // cleanup before processing the new document
    if (backGC!=null) backGC.dispose();

    maxNumberLines=bufferSize;
    //System.out.println("Lens: DocumentBuffer Size:" +maxNumberLines);
    lineInfo = new RH_LineInfo[maxNumberLines];
    lineInfoPtr=0;
    if (resetZoomWindow) {
      zoomWindow_y=0; // reset zoomwindow location
      vertShift=0;  // reset
    }
    //System.out.println("%%%%%%%%%%%%%Set Max lines=" + maxNumberLines + "  MaxVisibleLines=" + maxNumberVisibleLines);
  }

    /**
     * Returns the maximum number of lines visible in the thumbar
     */
  public int getMaxNumberVisibleLines() {
    return maxNumberVisibleLines;
  }

    /**
     * Line information method.  Receives this information form the ice.htmlbrowser classes after the document 
     * has been layed out by the rendering component. Width and height come as percentages of what they were for 
     * the main browser based n the current reduction value (size fraction).
     *
     *@param line text for box
     *@param x x location of line
     *@param y y location of line
     *@param h height of line
     *@param w width of line
     *@param color color of line
     *@param boxnum box number in linked list of boxes
     *@param fonttype font type of word in line (i.e. bold, italic, etc.)
     */
  public void receiveLineString (String line, int x, int y, int h, int w, Color color, int boxnum, int fonttype) {
    int len;
    float percentW=0,percentH=0,percentX=0,percentY=0;
    line_newy=y;
    //System.out.println("ReceiveLine: " + line);

    if (w>0 || line!=null) {
      percentW=((float)w/100)*(float)maxLineLen;
      percentH=((float)h/100)*(float)maxNumberVisibleLines;
      percentX=((float)x/100)*(float)maxLineLen;
      //percentY=(float)y/5;
      percentY=(float)y;
      
      //System.out.println("Ln:" + line + " W[" + w + "]=" + (int)percentW + " H=" + (int)percentH + " Max=" + maxLineLen +
      // " X=" + (int)percentX + " Y=" + (int)percentY + "%");
      //System.out.println("X= " + x + " Y=" + y + " W=" + w + " H=" + h);
      
      if (lineInfoPtr+1<maxNumberLines) {
	// Still drawing on same line
	if (line_newy==line_lasty) linex+=(int)percentW;
	// Case of a newline
	else {
	  line_lasty=y;
	  linex=startLinex;
	  liney+=3; // two pixels between each line ???
	}
	lineInfo[lineInfoPtr++] = new RH_LineInfo(line,(int)percentX+startLinex,(int)percentY,(int)percentW,
						  (int)percentH,color,boxnum,fonttype);
      }
      // 12-9-97: i get this message at times whenit seems irrelevant because in the end it seems to work and draw the info requested.
      // not sure whether it's relevant to notify myself of maxing out at this point.
      //else System.out.println("**************************Maxed out on lines: lineInfoPtr="+lineInfoPtr+" max="+maxNumberLines);
    }
    // The case of a blank line
    else {
      line_lasty=y;
      linex=startLinex;
      liney+=4; // two pixels between each line ???
    }
  }
    /**
     * Line information method.  Receives this information form the ice.htmlbrowser classes after the document 
     * has been layed out by the rendering component. Width and height come as percentages of what they were for 
     * the main browser based n the current reduction value (size fraction).
     *
     *@param image image to be rendered
     *@param x x location of line
     *@param y y location of line
     *@param h height of line
     *@param w width of line
     *@param color color of line
     *@param boxnum box number in linked list of boxes
     *@param fonttype font type of word in line (i.e. bold, italic, etc.)
     */
  public void receiveLineString (Image image, int x, int y, int h, int w, Color color, int boxnum,int fonttype) {
    int len;
    float percentX=0,percentY=0;
    double percentW=0,percentH=0,reductionPercent=.95;
    line_newy=y;
    //System.out.println("ReceiveLine: " + image);

    if (w>0 || image!=null) {
      percentX=((float)x/100)*(float)maxLineLen;
      percentY=(float)y;
      percentW=w/reductionValue;
      percentH=h/reductionValue;

      if (lineInfoPtr+1<maxNumberLines) {
	// Still drawing on same line
	if (line_newy==line_lasty) linex+=(int)percentW;
	// Case of a newline
	else {
	  line_lasty=y;
	  linex=startLinex;
	  liney+=3; // two pixels between each line ???
	}
	//System.out.println("@@@@@@@@@@Adding Image " + lineInfoPtr + " X=" + (int)percentX + " Y[" + (int)percentY + "]=" +
	//			   	  (int)((float)percentY/reductionValue-vertShift) + " H=" + (int)percentH);
	//AreaAveragingScaleFilter filter=new AreaAveragingScaleFilter((int)percentW,(int)percentH);
	//Image newimage=createImage (new FilteredImageSource (image.getSource(), filter));
	//image=image.getScaledInstance((int)percentW,(int)percentH,Image.SCALE_DEFAULT); 
	//lineInfo[lineInfoPtr++] = new RH_LineInfo(image.getScaledInstance((int)percentW,(int)percentH,Image.SCALE_AREA_AVERAGING),
	Image newimage=null;
	if (betterImageScaling) {
	    //newimage=image.getScaledInstance((int)percentW,(int)percentH,SCALE_DEFAULT);
	    //newimage=image.getScaledInstance((int)percentW,(int)percentH,Image.SCALE_REPLICATE);
	    //newimage=image.getScaledInstance((int)percentW,(int)percentH,Image.SCALE_FAST);
	    //newimage=image.getScaledInstance((int)percentW,(int)percentH,Image.SCALE_SMOOTH);
	    newimage=image.getScaledInstance((int)percentW,(int)percentH,Image.SCALE_AREA_AVERAGING);
	
 	    lineInfo[lineInfoPtr++] = new RH_LineInfo(newimage,
	 					      (int)percentX+startLinex,(int)percentY,(int)percentW,
						      (int)percentH,color,boxnum,fonttype);
	    imageTracker.addImage(newimage,imageTrackerCount++);
	}
	/**
	 * 8.27.98 jg: When i use these scaling methods they cause the images in the thumbar to refresh each time
	 * refreshDocument is called.  When i simply use the original image, there is no such refresh.  so for now,
	 * i'm using the basic image as the default mode until i figure this out so that it will not refresh when the 
	 * user uses the "non-betterImageScaling" mode
	 */
	/* 9/10/98 - commenting this out to just use the original image because animated gif keep screwing everything up!
 	else {
	    newimage=image.getScaledInstance((int)percentW,(int)percentH,Image.SCALE_DEFAULT); //FAST);
	    lineInfo[lineInfoPtr++] = new RH_LineInfo(newimage,
		 				      (int)percentX+startLinex,(int)percentY,(int)percentW,
						      (int)percentH,color,boxnum,fonttype);
	    imageTracker.addImage(newimage,imageTrackerCount++);
	}
	*/
	else {
	    lineInfo[lineInfoPtr++] = new RH_LineInfo(image,
		 				      (int)percentX+startLinex,(int)percentY,(int)percentW,
						      (int)percentH,color,boxnum,fonttype);
	    imageTracker.addImage(image,imageTrackerCount++);
	}
	/*
	  lineInfo[lineInfoPtr++] = new RH_LineInfo(image,
	  (int)percentX+startLinex,(int)percentY,(int)percentW,
	  (int)percentH,color,boxnum,fonttype);
	  imageTracker.addImage(image,imageTrackerCount++);
	*/
      }
      else System.out.println("**************************Maxed out on lines");
    }
    // The case of a blank line
    else {
      line_lasty=y;
      linex=startLinex;
      liney+=4; // two pixels between each line ???
    }
  }

   /**
     * Line information method.  Receives this information form the ice.htmlbrowser classes after the document 
     * has been layed out by the rendering component. Width and height come as percentages of what they were for 
     * the main browser based n the current reduction value (size fraction).
     *
     *@param image to be rendered
     *@param x x location of line
     *@param y y location of line
     *@param h height of line
     *@param w width of line
     *@param color color of line
     *@param boxnum box number in linked list of boxes
     *@param fonttype font type of word in line (i.e. bold, italic, etc.)
     */
  public void receiveLineString (Image image, int x, int y, int h, int w, Color color, int boxnum) {
    int len;
    float percentX=0,percentY=0;
    double percentW=0,percentH=0,reductionPercent=.95;
    line_newy=y;
    //System.out.println("ReceiveLine: " + image);

    if (w>0 || image!=null) {
      percentX=((float)x/100)*(float)maxLineLen;
      percentY=(float)y;
      percentW=w/reductionValue;
      percentH=h/reductionValue;

      if (lineInfoPtr+1<maxNumberLines) {
	// Still drawing on same line
	if (line_newy==line_lasty) linex+=(int)percentW;
	// Case of a newline
	else {
	  line_lasty=y;
	  linex=startLinex;
	  liney+=3; // two pixels between each line ???
	}
	//System.out.println("@@@@@@@@@@Adding Image " + lineInfoPtr + " X=" + (int)percentX + " Y[" + (int)percentY + "]=" +
	//			   	  (int)((float)percentY/reductionValue-vertShift) + " H=" + (int)percentH);
	lineInfo[lineInfoPtr++] = new RH_LineInfo(image,(int)percentX+startLinex,(int)percentY,(int)percentW,
						  (int)percentH,color,boxnum);
      }
      else System.out.println("**************************Maxed out on lines");
    }
    // The case of a blank line
    else {
      line_lasty=y;
      linex=startLinex;
      liney+=4; // two pixels between each line ???
    }
  }

  /**
   * sets the last Y coordinate for the current document; we receive only after all lineInfo nuggets have been received from browser
   *
   *@param lastyprocessed Y location of last line (or box) processed in document.  
   */
  public void sendBottomCoordinate(int lastyprocessed) {
      //System.out.println("===Thumbar: setupLastY:"+lastyprocessed+" maxLines="+maxNumberVisibleLines);
    // Locate last Y position lens may move before prohibiting further movement because you are at the end of the document;
    // Only do this when not dealing wiht longDocuments.
    if (lastyprocessed/reductionValue>maxNumberVisibleLines) {
      longDocument=true;
      //System.out.println("**Long doc TRUE");
    }
    else {
      longDocument=false;
      //System.out.println("**Long doc FALSE");
    }
    lastY=-1;

    topLensLocation=lastyprocessed/reductionValue-zoomWindow_h;
    //System.out.println("]]]]]]TopLensLocation:"+topLensLocation);
    if (lineInfoPtr>0) {
      int decr=1;
      lastLine=lineInfo[lineInfoPtr-decr];
      while (lastLine.getY()<0 && decr<lineInfoPtr) {
	//System.out.println("..decr="+decr+"...lastY Try:"+lastLine.getText()+" Y="+lastLine.getY()+" lineinfoptr="+lineInfoPtr);
	lastLine=lineInfo[lineInfoPtr-decr++];
      }
      //** This is the "best" guess at the last Y location; the actual lastY is not available until we render/layout
      //** the lineInfo objects; therefore, lastY gets set again (for real?) in updateTextLines based on the final
      //** line drawn to the canvas.
      lastY=(int)(lastLine.getY()/reductionValue)+lineOffsetY; 
      topLensLocation=lastY-zoomWindow_h+topImageHeight+bottomImageHeight;
      //System.out.println("=======LastLine["+lineInfoPtr+"-"+decr+"]: "+lastLine.getText()+ " - Y="+lastLine.getY()+" LastY="+lastY);
    }
  }

  /**
   * When a new document is about to be loaded we need to reset some things and clean up a bit 
   */
  public void loadingNewDocument() {
      //System.out.println("**===**Thumbar: loadingNewDoc: MaxLines=" + maxNumberLines);
    //commBus.parent.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    Graphics gc=null;
    liney=startLiney;
    linex=startLinex;
    linesLeftInDoc=previousLastLoc_y=lastLoc_y=0;
    imageTracker=new MediaTracker(this);
    imageTrackerCount=0;
    noMoreScroll=false;
    if (backGC!=null && (gc=getGraphics())!=null) {
	//System.out.println("**Cleanup thumbar...max lines:"+maxNumberVisibleLines+" docview_w="+docview_w);
      //navModeToTop();
      gc.clearRect(0,0,docview_w,maxNumberVisibleLines);
      getGraphics().dispose();  // trying this too 11-19-97
      backGC.dispose();  // offscreen buffer
    }

    // Clean up lineInfo
    Image image=null;
    /*
    for (int i=0;i<lineInfoPtr;i++) {
      if (lineInfo[i]!=null && (image=lineInfo[i].getImage())!=null) image.flush();
      //else if ((image=lineInfo[i].getTableImage())!=null) image.flush();
      lineInfo[i]=null;  // me thinks this helps the GC: by nullifying things get cleanup more efficiently
    }
    */
    lineInfo=null;
    // 11-17-97 New: try out calling dispose here to explicitly free up resources
  
    lineInfo = new RH_LineInfo[maxNumberLines]; // this may be redundant because we do not have the number of lines yet
    //lineInfo[0]=new RH_LineInfo("",0,0,0,0,Color.white,0,0);
    lineInfoPtr=0;

    if (backGC!=null) rebuildLens(backGC);
    update(getGraphics());

    // 11-19-97 not sure if this is proper but some folks are doing it to free up resources when they know the should.
    // Like here in my example, i just freed up a large array of objects and i'd like them cleaned up.
    //System.gc();
  }

  public void paint(Graphics gc) {
    update(gc);
  }
  public void update(Graphics gc) {
    Dimension parentSize = parent.getSize();
    Dimension v_size = commBus.getLocationControlSize();
    setSize(parentSize.width,parentSize.height-v_size.height);
    Dimension size     = getSize();
    //docview_w=size.width-offset_horz;
    //docview_h=size.height-offset_vert;
    maxLineLen=size.width-offset_horz-lineWidthOffset;   // set the line length for the docview lines that represent the text
    maxNumberVisibleLines=size.height-offset_vert-visibleLinesVertOffset;
    setLocation(0,v_size.height);
    try {
      backGC = backBuffer.getGraphics();
      int i=1, vHeight=0;

      // Right shadow lines under location control
      backGC.setColor(Color.darkGray);
      backGC.drawLine(size.width-offset_horz+1,0,size.width,0); 
      //backGC.drawLine(size.width-offset_horz+1,1,size.width,1); 
      
      // Left lines below top viewbar connection area
      backGC.setColor(Color.gray);
      backGC.drawLine(left_offset-2,vHeight+1,left_offset-2,docview_h-1); 
      backGC.setColor(Color.white);
      backGC.drawLine(left_offset,vHeight+2,left_offset,docview_h-2); 
      backGC.setColor(Color.lightGray);
      backGC.drawLine(left_offset+1,vHeight+2,left_offset+1,docview_h-2); 
      backGC.setColor(Color.gray);
      backGC.drawLine(left_offset+2,vHeight+2,left_offset+2,docview_h-2); 
      backGC.setColor(Color.darkGray);
      backGC.drawLine(left_offset+3,vHeight+3,left_offset+3,docview_h-4); 
      //backGC.drawLine(left_offset+4,vHeight+3,left_offset+4,docview_h-5); 
      
      // Top
      backGC.setColor(Color.darkGray);
      backGC.drawLine(left_offset+3,0,docview_w-4,0); 
      backGC.setColor(Color.black);
      backGC.drawLine(left_offset+4,1,docview_w-4,1); 
      // bottom line 
      backGC.setColor(Color.darkGray);
      backGC.drawLine(2,0,left_offset+1,0); 
      
      // Bottom Lines
      i=-1;
      int extra=5;
      backGC.setColor(Color.darkGray);
      backGC.drawLine(left_offset+i+2,docview_h-i,docview_w-i,docview_h-i++); 
      backGC.setColor(Color.black);
      backGC.drawLine(left_offset+i-1,docview_h-i,docview_w-i,docview_h-i++); 
      backGC.setColor(Color.gray);
      backGC.drawLine(left_offset+i-2,docview_h-i,docview_w-i,docview_h-i++); 
      backGC.setColor(Color.lightGray);
      backGC.drawLine(left_offset+i,docview_h-i,docview_w-i,docview_h-i++); 
      backGC.setColor(Color.white);
      backGC.drawLine(left_offset+i,docview_h-i,docview_w-i,docview_h-i++); 
      backGC.setColor(Color.lightGray);
      backGC.drawLine(left_offset+i,docview_h-i,docview_w-i,docview_h-i++); 

      // Document Window
      docview_x=left_offset+4;
      docview_y=2;
      backGC.setColor(documentColor);
      //backGC.fillRect(docview_x,docview_y,docview_w-docview_x-4,docview_h-6); //-(vHeight-12));
      backGC.fillRect(docview_x,docview_y,docview_w-docview_x,docview_h-6); //-(vHeight-12));
      
      if (useLastLocation) {
	// clean up last pointer first
	backGC.clearRect(left_offset-3,previousLastLoc_y-4,4,9);
	backGC.setColor(Color.gray); //backColor);
	backGC.drawLine(left_offset-2,previousLastLoc_y-4,left_offset-2,previousLastLoc_y+4); 
	backGC.setColor(backColor);
	backGC.drawLine(left_offset-1,previousLastLoc_y-4,left_offset-1,previousLastLoc_y+4); 
	backGC.setColor(Color.white);
	backGC.drawLine(left_offset,previousLastLoc_y-4,left_offset,previousLastLoc_y+4); 
	
	if (lastLoc_y>0) {
	  backGC.setColor(lastLocationColor);
	  backGC.drawLine(left_offset-3,lastLoc_y-4,left_offset+4,lastLoc_y); 
	  backGC.drawLine(left_offset-3,lastLoc_y,left_offset+4,lastLoc_y); 
	  backGC.drawLine(left_offset-3,lastLoc_y+4,left_offset+4,lastLoc_y); 
	  backGC.drawLine(left_offset-3,lastLoc_y-4,left_offset-3,lastLoc_y+4); 
	}
      }
      
      drawZoomWindow(backGC,zoomWindow_y);
      gc.drawImage(backBuffer,0,0,this);

      // 11-11-97 new - based on some sample code found in deja news:
      //backGC.dispose();
    } catch (Exception e) {}

  }

    /**
     * Clears the last location of the lens
     *
     *@param gc graphics object
     *@param y_loc last ylocation of lens (top left hand corner)
     */
  private void clearZoomWindow(Graphics gc, int y_loc) {
    Dimension size = getSize();
    //int docview_w=size.width-offset_horz;
    int docview_x=left_offset+4, vHeight=0;
    gc.clearRect(0,y_loc+1,left_offset+zoomWindow_w+8,zoomWindow_h); //+7); //blCorner.getHeight(this));
    if (colorDocumentLens && topImage!=null && leftImage!=null) {
      gc.setColor(windowLensColor);
      gc.fillRect(docview_x,y_loc+topImage.getHeight(this),docview_w-docview_x-4,leftImage.getHeight(this));
      //gc.fillRect(docview_x,y_loc+tlCorner.getHeight(this)+1,docview_w-docview_x-4,zoomWindowInside_h-2);
    }
  }
    /**
     * Rebuilds the lens each time the document is resized.  Creates images which represent the top, bottom, left and right
     * side of the lens so that they can simply be rendered in the paint/update routines.  
     *
     *@param gc graphics object
     */
  private boolean rebuildLens(Graphics gc) {
    Dimension size = getSize();

    int leftW=tlCorner.getWidth(this), rightW=trCorner.getWidth(this), bottomCR_w=brCorner.getHeight(this);
    int docview_x=left_offset+4, vHeight=0, win_w=zoomWindow_w, win_h=zoomWindowInside_h;
    setBackground(backColor);
    int start_x=0, i=0, start_y=0;
    //System.out.println("--------->Rebuilding Lens: docview_w="+docview_w+" docview_h="+docview_h+" leftW="+leftW+" rightW="+rightW);
    if (docview_w>0 && docview_h>0 && leftW>0 && rightW>0) {
      // I suppose this is cleanup time;  i'm trying to help out the GC each time a rebuild this lens window
      //System.out.println(">> Cleaning up: docview_x="+docview_x+" win_h="+win_h+" win_w="+win_w);
      if (topGC!=null && topImage!=null) { topGC.dispose(); topImage=null; }
      if (leftGC!=null && leftImage!=null) { leftGC.dispose(); leftImage=null; }
      if (bottomGC!=null && bottomImage!=null) { bottomGC.dispose(); bottomImage=null;}
      if (rightGC!=null && rightImage!=null) { rightGC.dispose(); rightImage=null; }
      try {
	topImage=createImage(docview_w-docview_x,tlCorner.getHeight(this));
	leftImage=createImage(leftW,win_h);
	bottomImage=createImage((win_w-leftW)-rightW,bottomCR_w);
	rightImage=createImage(rightW+1,win_h);
	topGC=topImage.getGraphics();
	leftGC=leftImage.getGraphics();
	bottomGC=bottomImage.getGraphics();
	rightGC=rightImage.getGraphics();

	//System.out.println("=====Lens: win_h="+win_h);
	
	// Create Top Image
	i=0;
	topGC.setColor(documentColor);
	topGC.drawLine(start_x,start_y+i,start_x+win_w-rightW-5,start_y+i++); 
	topGC.setColor(Color.white);
	topGC.drawLine(start_x,start_y+i,start_x+win_w-rightW-5,start_y+i++); 
	topGC.drawLine(start_x,start_y+i,start_x+win_w-rightW-5,start_y+i++); 
	topGC.setColor(Color.lightGray);
	topGC.drawLine(start_x,start_y+i,start_x+win_w-rightW-5,start_y+i++); 
	topGC.setColor(Color.gray);
	topGC.drawLine(start_x,start_y+i,start_x+win_w-rightW-5,start_y+i++); 
	topGC.setColor(Color.darkGray);
	topGC.drawLine(start_x,start_y+i,start_x+win_w-rightW-5,start_y+i++); 
	topGC.setColor(Color.black);
	topGC.drawLine(start_x,start_y+i,start_x+win_w-rightW-5,start_y+i++); 
	topGC.setColor(Color.gray);
	topGC.drawLine(start_x,start_y+i,start_x+win_w-rightW-5,start_y+i++); 
	// now set the value for the border so it can be used by findMouseLoc
	lensTopBorder_h=i-3;
	
	// Create Left Image
	i=0;
	leftGC.setColor(Color.darkGray);
	leftGC.drawLine(start_x+i,start_y,start_x+i++,start_y+win_h+2); 
	leftGC.setColor(Color.gray);
	leftGC.drawLine(start_x+i,start_y,start_x+i++,start_y+win_h); 
	leftGC.setColor(Color.lightGray);
	leftGC.drawLine(start_x+i,start_y,start_x+i++,start_y+win_h); 
	leftGC.setColor(Color.white);
	leftGC.drawLine(start_x+i,start_y,start_x+i++,start_y+win_h+2); 
	leftGC.setColor(Color.lightGray);
	leftGC.drawLine(start_x+i,start_y,start_x+i++,start_y+win_h+3); 
	leftGC.setColor(Color.gray);
	leftGC.drawLine(start_x+i,start_y,start_x+i++,start_y+win_h-1); 
	leftGC.setColor(Color.darkGray);
	leftGC.drawLine(start_x+i,start_y,start_x+i++,start_y+win_h-1); 
	leftGC.setColor(Color.black);
	leftGC.drawLine(start_x+i,start_y,start_x+i++,start_y+win_h-1); 
	
	// Create Right Image
	i=0;
	rightGC.setColor(Color.gray);
	rightGC.drawLine(start_x+i,start_y,start_x+i++,start_y+win_h); 
	rightGC.setColor(Color.white);
	rightGC.drawLine(start_x+i,start_y,start_x+i++,start_y+win_h); 
	rightGC.drawLine(start_x+i,start_y,start_x+i++,start_y+win_h); 
	rightGC.setColor(Color.lightGray);
	rightGC.drawLine(start_x+i,start_y,start_x+i++,start_y+win_h); 
	rightGC.setColor(Color.gray);
	rightGC.drawLine(start_x+i,start_y,start_x+i++,start_y+win_h); 
	rightGC.setColor(Color.darkGray);
	rightGC.drawLine(start_x+i,start_y,start_x+i++,start_y+win_h); 
	rightGC.drawLine(start_x+i,start_y,start_x+i++,start_y+win_h); 
	rightGC.setColor(Color.black);
	rightGC.drawLine(start_x+i,start_y,start_x+i++,start_y+win_h); 
	rightGC.setColor(Color.darkGray);
	rightGC.drawLine(start_x+i,start_y,start_x+i++,start_y+win_h); 
	//rightGC.drawLine(start_x+i,start_y,start_x+i++,start_y+win_h); 
	
	// Create Bottom Image
	i=0;
	bottomGC.setColor(Color.white);
	bottomGC.drawLine(start_x,start_y+i,start_x+win_w,start_y+i++); 
	bottomGC.drawLine(start_x,start_y+i,start_x+win_w,start_y+i++); 
	bottomGC.setColor(Color.lightGray);
	bottomGC.drawLine(start_x,start_y+i,start_x+win_w,start_y+i++); 
	bottomGC.setColor(Color.gray);
	bottomGC.drawLine(start_x,start_y+i,start_x+win_w,start_y+i++); 
	bottomGC.setColor(Color.darkGray);
	bottomGC.drawLine(start_x,start_y+i,start_x+win_w,start_y+i++); 
	bottomGC.drawLine(start_x,start_y+i,start_x+win_w,start_y+i++); 
	bottomGC.setColor(Color.black);
	bottomGC.drawLine(start_x,start_y+i,start_x+win_w,start_y+i++); 
	
	// Shadow under bottom portion
	start_x=0;
	bottomGC.setColor(Color.darkGray);
	bottomGC.drawLine(start_x,start_y+i,start_x+win_w-rightW-leftW-4,start_y+i++); 
	bottomGC.setColor(Color.gray);
	bottomGC.drawLine(start_x+1,start_y+i,start_x+win_w-rightW-leftW-5,start_y+i++); 
	bottomGC.setColor(Color.lightGray);  
	bottomGC.drawLine(start_x+1,start_y+i,start_x+win_w-rightW-leftW-5,start_y+i++); 
	//bottomGC.setColor(Color.lightGray);
	//bottomGC.drawLine(start_x,start_y+i,start_x+win_w-rightW-leftW,start_y+i++); 
      } catch (Exception e) {
	commBus.consoleMsg("could not create lens images",RH_CommBus.ERROR_TYPE_MSG);
	//e.printStackTrace ();
	return false;
      }
      return true;
    }
    else {
      commBus.consoleMsg("CANNOT Make Lens Frame Yet ...",RH_CommBus.ERROR_TYPE_MSG);
      return false;
    }

  }
    /**
     * Draws the Lens (i used to call this the zoom window for lack of a better name) each time the user
     * relocates the lens in the thumbar.
     *
     *@param gc graphics object to draw on
     *@param y_loc new y location to draw lens
     */
  private void drawZoomWindow(Graphics gc, int y_loc) {
    Dimension size = getSize();

    if (gc!=null) {
	int leftW=tlCorner.getWidth(this), rightW=trCorner.getWidth(this);
	int docview_x=left_offset+4, vHeight=0, win_w=zoomWindow_w, win_h=zoomWindowInside_h;
	
	clearZoomWindow(gc, zoomWindow_y);
	setBackground(backColor);
	zoomWindow_y=y_loc;
	int start_y=zoomWindow_y, start_x=left_offset-3, //6, 
	    i=0;
	
	// draws the lines in the frame on the right side because they get screwed up when i clear the last zoom area
	i=-1;
	//    if (longDocument) gc.setColor(Color.lightGray);
	if (motif==0) gc.setColor(Color.gray); 
	else gc.setColor(Color.black);
	gc.drawLine(docview_w-i,i+vHeight-1,docview_w-i,docview_h-i++); 
	gc.setColor(Color.darkGray);
	gc.drawLine(docview_w-i,i+vHeight-1,docview_w-i,docview_h-i++); 
	gc.setColor(Color.gray);
	gc.drawLine(docview_w-i,i+vHeight-1,docview_w-i,docview_h-i++); 
	gc.setColor(Color.lightGray);
	gc.drawLine(docview_w-i,i+vHeight-1,docview_w-i,docview_h-i++); 
	gc.setColor(Color.white);
	gc.drawLine(docview_w-i,i+vHeight,docview_w-i,docview_h-i++);  
	gc.setColor(Color.lightGray);
	gc.drawLine(docview_w-i,i+vHeight,docview_w-i,docview_h-i); 
	
	if (lineInfoPtr>0) updateTextLines(gc);
	
	//**** Draws Lens
	if (topImage!=null && leftImage!=null && rightImage!=null && tlCorner!=null &&
	    trCorner!=null && blCorner!=null && bottomImage!=null && brCorner!=null) {
	    // Top
	    gc.drawImage(topImage,start_x+tlCorner.getWidth(this),start_y+1,this);
	    //Left
	    gc.drawImage(leftImage,start_x,start_y+topImage.getHeight(this),this);
	    // Right
	    //gc.drawImage(rightImage,start_x+win_w-rightW-4,start_y+trCorner.getHeight(this),this);
	    gc.drawImage(rightImage,start_x+tlCorner.getWidth(this)+topImage.getWidth(this)-3,start_y+trCorner.getHeight(this),this);
	
	    // Draw Corners
	    gc.drawImage(tlCorner,start_x,start_y,this);
	    gc.drawImage(trCorner,start_x+tlCorner.getWidth(this)+topImage.getWidth(this)-2,start_y,this);
	    gc.drawImage(blCorner,start_x,start_y+leftImage.getHeight(this),this);
	    // Bottom
	    gc.drawImage(bottomImage,start_x+tlCorner.getWidth(this),start_y+win_h,this);
	    gc.drawImage(brCorner,start_x+tlCorner.getWidth(this)+topImage.getWidth(this)-2,
			 start_y+rightImage.getHeight(this),this);
	}
	
	
	// Draws a transparent Ricoh "R" in bottom right corener ah la the logos TV stations use (VH1, CBS, ...)
	if (useMarquee && ricohR!=null) gc.drawImage(ricohR,(start_x+win_w)-ricohR.getWidth(this)-13,
						     (start_y+win_h)-ricohR.getHeight(this)-4,this);
    }

  }

    /**
     * This method is responsible for drawing all of the lines in the thumbar both inside and outside of the lens.
     * It iterates through the current list of lines (lineInfo[]) and draws the objects according to their position,
     * their color, and the size of the thumbar.
     *
     *@param gc graphics obejct to draw with
     */
  private void updateTextLines(Graphics gc) {
    //System.out.println("===================UPDATE LINES[" + lineInfoPtr + "] Shift=" + vertShift + 
    //	       " zoomWindow_y=" + zoomWindow_y);
    Color tmp, linkEnhancementColor=Color.lightGray;
    int i=0, x=0, lasty=0, incrx=0, incry=lineOffsetY, imgWidth=0, adder=8,
      //bottomOfLens=zoomWindow_y+topImage.getHeight(this)+leftImage.getHeight(this)+bottomImage.getHeight(this);
      bottomOfLens=zoomWindow_y+zoomWindow_h;
    float y=0, new_w=0, new_h=0;
    Image image;
    //Dimension docSize = commBus.getDocumentWindowSize();
    //int docviewWindow_h=leftImage.getHeight(this),
    int docviewWindow_h=zoomWindowInside_h,
      shiftValue=vertShift,  box_w=0, box_h=0, box_x=0, box_y=0, pane_w=0, pane_h=0, pane_x=0, pane_y=0;
    double percent=0;
    boolean noShowLine=true, foundTop=false;
    String lastword=null;

    if (shiftValue>=0) i=shiftValue;
    else i=0;
    for (; i<lineInfoPtr && lasty<maxNumberVisibleLines && lineInfo!=null; i++) {
      if (lineInfo[i]!=null && (image=lineInfo[i].getImage())!=null) {
	//System.out.println("Image: " + lineInfo[i].getImage());

	if (!foundTop && ((int)y+incry)>zoomWindow_y && ((int)y+incry)<bottomOfLens) {
	  topLine=lineInfo[i];
	  foundTop=true;
	}
	x=lineInfo[i].getX();
	if (shiftValue>0) {
	  y=(float)((float)lineInfo[i].getY()/reductionValue-shiftValue); 
	}
	else y=(float)lineInfo[i].getY()/reductionValue; 

	gc.setColor(lineInfo[i].getColor());  // this may not be necessary
	image=lineInfo[i].getImage();

	imgWidth=lineInfo[i].getImageWidth();
	if (x+imgWidth>=docview_w) {
	  percent=(double)(docview_w-x-4)/(double)imgWidth;
	  imgWidth=docview_w-x-4;
	  //System.out.println("Percent=" + percent);
	  pane_w=(int)((double)image.getWidth(this)*percent);
	}
	else pane_w=image.getWidth(this);
	box_w=imgWidth; box_h=lineInfo[i].getImageHeight(); box_x=x; box_y=(int)y+incry;
	pane_h=image.getHeight(this); pane_x=0; pane_y=0;  // these are constant

	//gc.drawImage(image,box_x, box_y,this);
	if (image!=null) gc.drawImage(image,box_x, box_y, box_w+box_x, box_h+box_y,
				      pane_x, pane_y, pane_w+pane_x, pane_h+pane_y, this);
      }
      else if (lineInfo[i]!=null && lineInfo[i].getX()<docview_w) { //if (lineInfo[i].getText()!=null) {
	x=lineInfo[i].getX();
	// make the docview area X times smaller than the main document???
	if (shiftValue>0) {
	  y=(float)((float)lineInfo[i].getY()/reductionValue-shiftValue); 
	}
	else y=(float)lineInfo[i].getY()/reductionValue; 
	// Determine the closest line within the lens 
	if (noShowLine && lineInfo[i].getText()!=null && ((int)y+incry)>=zoomWindow_y && ((int)y+incry)<bottomOfLens) {
	  showLineLoc=i; showLineLocOffset=0;
	  //System.out.println("Set ShowLineLoc: "+showLineLoc+" ->"+lineInfo[showLineLoc].getText());
	  noShowLine=false; // means we found our line and do not want to eval any more
	}
	if (y<maxNumberVisibleLines) {
	  // If line is within lens then change to special color
	  if (((int)y+incry)>zoomWindow_y && ((int)y+incry)<bottomOfLens) {
	    if (!foundTop) {
	      topLine=lineInfo[i];
	      foundTop=true;
	    }
	    // determine the color of the line and the emphasis line (tmp)
	    if (lineInfo[i].getColor()==documentColor) {
	      gc.setColor(tmp=windowLensColor);
	      drawExtraLine=false;
	    }
	    else if (lineInfo[i].getColor()==windowANOHColor) {
	      gc.setColor(tmp=lineInfo[i].getColor());
	      drawExtraLine=useAnohDoubleLine;
	    }
	    else if (lineInfo[i].getColor()==windowLinkColor) {
	      gc.setColor(tmp=lineInfo[i].getColor());
	      drawExtraLine=useLinkDoubleLine;
	    }
	    else if (lineInfo[i].getColor()==boldLineColor) {
	      gc.setColor(lineInfo[i].getColor());
	      tmp=boldLineColor;
	      drawExtraLine=true;
	    }
	    else {
	      gc.setColor(lensLineColor);
	      tmp=windowLensColor;
	    }
	    
	    //*** Draw the line representing the sentence or word: checks to see if line is longer than display, crops it necessary
	    if (x+lineInfo[i].getWidth()>=docview_w-adder) gc.drawLine(x,(int)y+incry,docview_w-adder,(int)y+incry);
	    else gc.drawLine(x,(int)y+incry,x+lineInfo[i].getWidth(),(int)y+incry);
	    // draw extra line under these lines for emphasis
	    if (drawExtraLine) {
	      gc.setColor(tmp);
	      if (x+lineInfo[i].getWidth()>=docview_w-adder) gc.drawLine(x,(int)y+incry+1,docview_w-adder,(int)y+incry+1);
	      else gc.drawLine(x,(int)y+incry+1,x+lineInfo[i].getWidth(),(int)y+incry+1);
	    }
	  }
	  // this handles alllines outside the lens
	  else if (lineInfo[i]!=null) {
	    /*if (moreInfoLineDraw) {
	      FontMetrics fm=commBus.getBrowserFontMetrics(lineInfo[i].getFontType());
	      moreInfoDrawLine(gc,lineInfo[i].getText(),fm,x,(int)y+incry,x+lineInfo[i].getWidth(),(int)y+incry);
	    }*/
	    gc.setColor(windowLineColor); //lineInfo[i].getColor());
	    if (lineInfo[i].getColor()==windowLinkColor) {
		drawExtraLine=useLinkDoubleLine;
		gc.setColor(lineInfo[i].getColor());
	    }
	    else if (lineInfo[i].getColor()==windowANOHColor) {
		drawExtraLine=useAnohDoubleLine;
		gc.setColor(lineInfo[i].getColor());
	    }
	    else if (lineInfo[i].getColor()==boldLineColor) {
		drawExtraLine=true;
		gc.setColor(boldLineColor);
	    }
	    else drawExtraLine=false;

	    //*** Draw the line representing the sentence or word: checks to see if line is longer than display, crops it necessary
	    if (x+lineInfo[i].getWidth()>=docview_w-adder) gc.drawLine(x,(int)y+incry,docview_w-adder,(int)y+incry);
	    else gc.drawLine(x,(lasty=(int)y+incry),x+lineInfo[i].getWidth(),(int)y+incry);
	    if (drawExtraLine && lineInfo[i].getColor()!=windowLineColor) {
	      if ((tmp=lineInfo[i].getColor())==Color.black) tmp=Color.gray;
	      gc.setColor(tmp);
	      if (x+lineInfo[i].getWidth()>=docview_w-adder) gc.drawLine(x,(int)y+incry+1,docview_w-adder,(int)y+incry+1);
	      else gc.drawLine(x,(lasty=(int)y+incry+1),x+lineInfo[i].getWidth(),(int)y+incry+1);
	    }
	    lastword=lineInfo[i].getText();
	  }
	  //System.out.println("[" + i + "] X=" + x + " Y=" + ((int)y+incry) + " W=" +
	  //	     lineInfo[i].getWidth() + ">" + lineInfo[i].getText());
	} 
      }

      //if (lasty!=(int)y && y>0) {
	//lasty=(int)y+incry;
	
      //System.out.Lasty="+lasty+"*****");
    }

    //if (resizingDocument) {
    //** If the last line s off the screen, use the original lastY form lastline
    if (longDocument && i<lineInfoPtr) {
	//System.out.print("---(Case1)Reset LastY  was:"+lastY);
      lastY=(int)(lastLine.getY()/reductionValue)+incry; 
      int foo=(int)(lastLine.getY()/reductionValue)+lineOffsetY; 
      topLensLocation=lastY-zoomWindow_h+topImageHeight+bottomImageHeight;
      //System.out.println(" is now:"+lastY+" i="+i+" lineInfoPtr="+lineInfoPtr+" foo="+foo);
    }
    else if (resizingDocument) { // || (longDocument && i==lineInfoPtr)) {
	//System.out.print("---(Case2)Reset LastY  was:"+lastY);
      topLensLocation=lasty-zoomWindow_h+topImageHeight+bottomImageHeight;
      //lastY=lasty+topImageHeight+bottomImageHeight;
      if (lastLine!=null) lastY=(int)(lastLine.getY()/reductionValue)+lineOffsetY; 
      else lastY=0;
      //System.out.println(" is now:"+lastY+" i="+i+" lineInfoPtr="+lineInfoPtr);
    }
    //}
    
    // set the long document flag 
    //System.out.println("LineInfoPtr=" + lineInfoPtr + " I=" + i);
    //Dimension size     = getSize();

    // Why do i update these values here?  they should get update in one spot only
    //docview_w=size.width-offset_horz;
    //docview_h=size.height-offset_vert;

    //gc.setColor(Color.red);
    //gc.drawLine(0,lastY,docview_w,lastY);

    int add=6;
    //System.out.println("***UpdateText: lasty="+lasty+" maxlines="+maxNumberVisibleLines+" lineInfoPtr="+lineInfoPtr+" i="+i);
    if (longDocument && i<lineInfoPtr) { //lasty>maxNumberVisibleLines) {
      gc.setColor(longDocumentColor);
      gc.drawLine(left_offset+4,maxNumberVisibleLines+add,docview_w-5,maxNumberVisibleLines+add++); 
      gc.drawLine(left_offset+4,maxNumberVisibleLines+add,docview_w-4,maxNumberVisibleLines+add++); 
      gc.drawLine(left_offset+3,maxNumberVisibleLines+add,docview_w-4,maxNumberVisibleLines+add++); 
    }

    // Check to see if we should display colored lines at top when scrolling downwards
    if (shiftValue>0) {
      add=0;
      gc.setColor(longDocumentColor);
      gc.drawLine(left_offset+4,add,docview_w-4,add++); 
      gc.drawLine(left_offset+4,add,docview_w-4,add++); 
      gc.drawLine(left_offset+4,add,docview_w-5,add++); 
    }

    //System.out.println("*****RedLine: topLensLocation="+topLensLocation);
    if (shiftValue>0 && lastY-shiftValue<=maxNumberVisibleLines) {
	/*
      System.out.println("&&&LASTY VISIBLE!!! lastY:"+lastY+" local lasty="+lasty+" maxlines="+maxNumberVisibleLines+" shiftValue="+shiftValue);
      gc.setColor(Color.cyan);
      gc.drawLine(0,topLensLocation-shiftValue,docview_w,topLensLocation-shiftValue);
      gc.setColor(Color.red);
      gc.drawLine(0,lastY-shiftValue,docview_w,lastY-shiftValue);
      gc.setColor(Color.green);
      gc.drawLine(0,lasty,docview_w,lasty);
	*/
      noMoreScroll=true;
    }
    else noMoreScroll=false;
  }

  /**
   * This routine draws a more representative version of the thumbnail document by drawing shorter lines that represent
   * the individual words in the line of text.  the user has the choice of either using this method or simply drawing a 
   * complete line for the whole line of text.
   */
  /*
  private void moreInfoDrawLine(Graphics gc, String text, FontMetrics fm, int x1, int y1, int x2, int y2) {
    int i=0, len=text.length(),width=0;
    float percentW=0;
    byte[] buffer=new byte[len];
    byte[] wordBuf=new byte[len]; 
    buffer=text.getBytes();

    for (i=0;i<len;) {
      //System.out.print("len="+len+" i="+i+">");
      // parse line of text until reach space.
      for (int x=0;i<len && x<len && buffer[i]!=' ';) {
	wordBuf[x++]=buffer[i++];
	//System.out.print("."+wordBuf[x-1]);
      }
      width=fm.stringWidth(new String(wordBuf));  
      percentW=(float)width/reductionValue;
      //percentW=((float)width*100)*(float)maxLineLen;
      gc.drawLine(x1,y1,x1+(int)percentW,y2);
      x1+=(int)percentW+2;
      i++;  // skip the space you just found
      //System.out.println("");
    }
    //gc.drawLine(x1,y1,x2,y2);
  }*/

  public void actionPerformed(ActionEvent ev) {
    Object source = ev.getSource();
    System.out.println("Somthing happened");
  }
  private void updateLinesLeft(int shift) {
    int diff=lastY-shift;
    if (diff>0) linesLeftInDoc=diff;
    else linesLeftInDoc=0;
  }
    /**
     * Mouse dragging to very important in the thumbar because it means that the user is trying to relocate
     * the lens to a new position in the document.  This method captures the drag event, calculates the new Y position
     * to move to and then calls for an update to the a screen.  
     */
  public void mouseDragged(MouseEvent ev) {
    if (lineInfoPtr>0) {
      int newy=ev.getY(), top=0;
      Dimension size = getSize();
      int win_h= size.height-offset_vert-zoomWindow_h, lensBottom=newy+(zoomWindow_h-(topImageHeight+bottomImageHeight));
      Graphics gc=null;
      boolean debug=false;
      useLastLocation=true;
      
      //if ((newy+(vertShift+(newy-win_h)))>lastY) System.out.println("=== Thumbar gone too far");
      if (newy<=lastY) {
	//System.out.println("**Newy=" + newy + " Shift=" + vertShift + " win_h=" + win_h  + " lineInfoPtr=" + lineInfoPtr);
	if (bufferSet) gc=backGC;
	else gc=getGraphics();
	//updateLinesLeft(((vertShift+(newy-win_h))+newy));
	if (newy>top && newy<win_h) {
	  if (newy<=topLensLocation) {
	    if (debug) System.out.println("**Newy=" + newy + " Shift=" + vertShift + " win_h=" + win_h  + " lineInfoPtr=" + lineInfoPtr+
					  " linesLeft="+linesLeftInDoc+" lastY="+lastY);
	    //wasDragging=ev.getY();
	    wasDragging=newy;
	    drawZoomWindow(gc,newy+topImageHeight);
	  }
	  else {
	    wasDragging=topLensLocation;
	    drawZoomWindow(gc,wasDragging);
	  }
	  update(getGraphics());
	}
	else if (newy<=top) {
	  if (debug) System.out.println("$$Newy=" + newy + " Shift=" + vertShift  + " lineInfoPtr=" + lineInfoPtr);
	  wasDragging=top;
	  if (vertShift>0) vertShift+=newy;
	  drawZoomWindow(gc,top);
	  update(getGraphics());
	}
	else if (newy>=win_h && longDocument) {
	  // Subtract the colored lines from the bottom so that they still show when scrolling
	  // the document
	  wasDragging=win_h;
	  if (longDocument) {// && linesLeftInDoc>0) { //(vertShift+(newy-win_h))+newy<=lastY) {
	    if (debug) {
	      //System.out.println("--PreviousLastLoc_y="+previousLastLoc_y+" linesLeftInDoc="+linesLeftInDoc);
	      System.out.println("##Newy=" + newy + " Shift=" + vertShift + " win_h=" + win_h + " incr="+(newy-win_h)+
				 " lastY="+lastY+" POS:"+((vertShift+(newy-win_h))+newy));
	    }
	    int nextIncr=vertShift+(newy-win_h);
	    //if ((lastY-nextIncr)>0 && (lastY-nextIncr)<maxNumberVisibleLines) {
	    if (noMoreScroll) {
	      if (debug) System.out.println("==========NO SHIFT INCRE: nextIncr="+nextIncr+" diff="+(lastY-nextIncr)+" maxlines"+
					    maxNumberVisibleLines+" topLens="+topLensLocation+" lens_h="+zoomWindow_h);
	      
	      drawZoomWindow(gc,topLensLocation-vertShift);
	    }
	    else {
	      if (debug) System.out.println("=====NextIncr:"+ nextIncr+" diff="+(lastY-nextIncr)+ " maxlines:"+maxNumberVisibleLines);
	      vertShift=nextIncr;
	      drawZoomWindow(gc,win_h);
	    }
	    update(getGraphics());
	  }
	  else if (debug) {
	    System.out.println("--PreviousLastLoc_y="+previousLastLoc_y+" linesLeftInDoc="+linesLeftInDoc+" lastLens_y="+lastLens_y);
	    System.out.println("#<<<Newy=" + newy + " Shift=" + vertShift + " win_h=" + win_h + " incr="+(newy-win_h)+
			       " lastY="+lastY+" POS:"+((vertShift+(newy-win_h))+newy));
	    drawZoomWindow(gc,win_h);
	    update(getGraphics());
	  }
	}
	else {
	  // else redraw the window where it was last found -- this hopefully will prevent the window
	  // from doing wild things like disappearing from the screen
	  if (debug) System.out.println("%%%Newy=" + newy + " Shift=" + vertShift + " win_h=" + win_h+" lastY="+lastY);
	  //wasDragging=-1;
	  wasDragging=topLensLocation;
	  drawZoomWindow(gc,wasDragging);
	}
	// record last Y position for showBrowserArea
	lastLens_y=newy;
      }
      // Else we have moved the mouse beyond the end of the doc so just go to the end of the doc
      else if (lineInfoPtr>0 && !longDocument && newy>lastY) {
	//System.out.println("===Thumbar: mouse offscreen: newy="+newy);
	if (bufferSet) gc=backGC;
	else gc=getGraphics();
	wasDragging=topLensLocation; //lastY+bottomImageHeight;
	if (debug) System.out.println("@@@Newy=" + newy + " wasDragging="+wasDragging+" lastY="+lastY);
	if (wasDragging>win_h) wasDragging=win_h;
	drawZoomWindow(gc,wasDragging);
	update(getGraphics());
	lastLens_y=lastY;
      }
    }
  }

  public void mouseMoved(MouseEvent ev) {
    /*int newy=ev.getY(), top=0;
    Dimension size = getSize();
    int win_h= size.height-offset_vert-zoomWindow_h;
    */
    // move mouse back into docview area so dragging window is easier
  }
    
  public void mouseClicked(MouseEvent ev) {
    if (lineInfoPtr>0) {
      Graphics gc=null;
      Dimension size = getSize();
      int newy=ev.getY(), top=0, win_h= size.height-offset_vert-zoomWindow_h;
      //System.out.println("---Mouse CLicked: lineInfoPtr="+lineInfoPtr+" newy="+newy);
      if (lineInfoPtr>0 && newy<=lastY) {
	if (bufferSet) gc=backGC;
	else gc=getGraphics();
	
	if (newy>top && newy<win_h) {
	  wasDragging=ev.getY();
	  drawZoomWindow(gc,newy+topImageHeight);
	  update(getGraphics());
	}
	else {
	  // else redraw the window where it was last found -- this hopefully will prevent the window
	  // from doing wild things like disappearing from the screen
	  wasDragging=-1;
	  drawZoomWindow(gc,zoomWindow_y);
	}
      }
      else if (lineInfoPtr>0 && !longDocument && newy>lastY) {
	if (bufferSet) gc=backGC;
	else gc=getGraphics();
	wasDragging=lastY+bottomImageHeight;
	drawZoomWindow(gc,wasDragging);
	update(getGraphics());
      }
    }
  }
  public void mousePressed(MouseEvent ev) {
  }

  /**
   * This routine figures out where you have just moved th lens and returns the lineInfo pointer to the box
   * which should be displayed at the top of the lens.  You can then send the lineInfo Y coordinate to the
   * browser so that the scrollbars can be updated, therefore moving the document to the lcoation specified by the lens
   */
  private int findMouseLoc() {
    if (lineInfoPtr>0) {
      int i=0, add_to_it=0;
      float newy=0, range=0, offset_percent=0;
      boolean quit=false, debug=false;
      Dimension size = getSize();
      //int win_h= size.height-zoomWindow_h-visibleLinesVertOffset, 
      int win_h= size.height-offset_vert-zoomWindow_h, idxPtr=wasDragging;

      //System.out.println("MaxLines="+maxNumberVisibleLines+" LineInfoPtr="+lineInfoPtr+" idxPtr="+idxPtr+
      //" vertShift="+vertShift+" win_h="+win_h);
      previousLastLoc_y=lastLoc_y;
      lastLoc_y=zoomWindow_y;
      if (idxPtr+vertShift>0 && idxPtr<=win_h) {
	newy=(float)(lineInfo[0].getY()/reductionValue-vertShift+lineOffsetY-topImageHeight);
	
	// should i be 1 here because i grab the value prior to the loop???
	for (i=0;i<lineInfoPtr-1 && newy<maxNumberVisibleLines && !quit;
	     newy=(float)(lineInfo[i].getY()/reductionValue-vertShift+lineOffsetY-topImageHeight)) {
	  if (debug) System.out.print("...max="+maxNumberVisibleLines+" len="+lineInfoPtr+" target="+idxPtr+" ->trying ["+i+"]: y=");
	  if (debug) System.out.print(newy+"["+lineInfo[i].getY()+"]");
	  // check for the case where you are over an image
	  if (lineInfo[i].getImage()!=null) {
	    range=newy+lineInfo[i].getImageHeight();
	    //System.out.println(i + "> idxPtr=" + idxPtr + " Range " + newy + " to " + range);
	    if (idxPtr+lensTopBorder_h>=(int)newy && idxPtr<=range)  {
	      quit=true;
	      if (debug) System.out.println("==GOTIT (IMAGE)");
	    }
	    else {
	      i++;
	      if (debug) System.out.println("==");
	    }
	  }
	  else if (idxPtr+lensTopBorder_h<=(int)newy) {
	    quit=true;
	    if (debug) System.out.println("==GOTIT (TEXT)");
	  }
	  else {
	    i++;
	    if (debug) System.out.println("==");
	  }
	}

	// this handles the situation where we are over or partially over an image.  otherwise the showLineLoc is
	// defined in updateTextLines() as i process the lines for the overview window
	if (i>=0 && i<lineInfoPtr && lineInfo[i].getImage()!=null) {
	  //System.out.println("Condition 1");
	  offset_percent=(idxPtr-newy)/lineInfo[i].getImageHeight(); // gives percentage offset that view window is over image
	  showLineLocOffset=(int)((lineInfo[i].getImageHeight()*reductionValue)*offset_percent+topImageHeight); // gives org. image height
	  //commBus.tmpFoo(lineInfo[i].getY()+add_to_it);
	}
	else showLineLocOffset=0;
      }
      if (i==lineInfoPtr) return i-1;
      else return i;
    }
    else return 0;
  }

    /**
     * Once the mouse has been released we notify the commBus that we are ready for the document to
     * be repositioned to the new coordinate.
     */
  public void mouseReleased(MouseEvent ev) {
    if (lineInfoPtr>0) {

      if (zoomWindow_y>=lastY){
	//System.out.println(">>..Lens Moved to last loc: y="+lastLine.getY());
	commBus.showDocViewArea(lastLine.getY()+showLineLocOffset);
      }
      else {
	showLineLoc=findMouseLoc();      
	wasDragging=-1; // reset
	if (showLineLoc>=0 && showLineLoc<lineInfoPtr) {
	  /*
	  System.out.println(">>..Lens Moved ("+showLineLoc+" of "+(lineInfoPtr-1)+") y="+lineInfo[showLineLoc].getY()+"  MAX Y="+
			     lineInfo[lineInfoPtr-1].getY());
	  System.out.println(">>....LineInfo:"+(lineInfoPtr-1)+"-"+showLineLoc+"="+((lineInfoPtr-1)-showLineLoc)+
			     " , "+lineInfo[lineInfoPtr-1].getY()+"-"+lineInfo[showLineLoc].getY()+"="+
			     (lineInfo[lineInfoPtr-1].getY()-lineInfo[showLineLoc].getY())+" LASTY="+lastY);
			     */
	  commBus.showDocViewArea(lineInfo[showLineLoc].getY()+showLineLocOffset);
	}
      }
    }
    //else commBus.showDocViewArea(lineInfo[0].getY());
  }
  public void mouseEntered(MouseEvent ev) {
    commBus.parent.setCursor(new Cursor(Cursor.HAND_CURSOR));
  }
  public void mouseExited(MouseEvent ev) {
    commBus.parent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }

  /**
   * Move the lens to the location specified by the browser's scrollbars
   */
  public void showBrowserArea1(int lineNum) {
    Dimension size=getSize();
    System.out.println("========================================");
    System.out.println("--LineNum="+lineNum+" LastY["+lastLine.getY()+"]="+lastY+" LineInfoPtr="+lineInfoPtr+" win_h="+
		       (size.height-offset_vert-zoomWindow_h));
    int i=0, new_y=0;
    boolean found=false;
    RH_LineInfo line=null;

    if (lineNum==0) {
      new_y=wasDragging=0;
    }
    else {
      for (i=0; i<lineInfoPtr-1 && lineInfo[i]!=null && lineInfo[i].getY()<lineNum; i++);
      if (i<lineInfoPtr-1) {
	line=lineInfo[i];
	System.out.println("---Line found: "+line.getText()+" Y="+line.getY());
	int win_h=size.height-offset_vert-zoomWindow_h, holder=previousLastLoc_y;
	wasDragging=win_h;
	previousLastLoc_y=holder;
	
	//vertShift=wasDragging=0;
	new_y=(int)line.getY()/reductionValue; 
	
	System.out.println("===> new_y["+new_y+"] - shift["+vertShift+"] = "+(new_y-vertShift)+" vs. "+win_h);
	
	//** new_y will not get converted from the absolute Y reference (i.e. the actual reduced Y value in the whole
	//   display area) to the relative Y position, between 0 and win_h.  The shift represents how far we are from the 
	//   top of the document (i.e. the number of lines we have had to shift the docview display up as we scroll down)
	// am i still in viewing the lens in the main overview window?  (
	if (new_y>0 && (new_y-vertShift)<=win_h) {
	  // Set new_y to be less the current vertShift amount
	  if (new_y<lastY) {
	    vertShift+=new_y-win_h;
	    new_y-=vertShift;
	    System.out.print("----->This 1: new_y="+new_y+" shift="+vertShift);
	  }
	  else {
	    System.out.print("----->This 2: new_y="+new_y+" shift="+vertShift);
	  }
	  wasDragging=new_y;
	}
	else if (longDocument && new_y>=win_h) {
	  System.out.print("----->That new_y="+new_y+" shift=");
	  if (new_y+zoomWindow_h>lastY) {
	    vertShift+=win_h-(new_y-vertShift);
	    //vertShift+=newy-win_h;
	    System.out.println("*1:"+vertShift+" zoomWindow_h="+zoomWindow_h);
	  }	    
	  else {
	    vertShift+=new_y-win_h;
	    System.out.println("#2:"+vertShift);
	  }
	  wasDragging=win_h;
	  new_y=win_h;
	}
	else {
	  System.out.println("----->Other: new_y="+new_y+" zoomWindow_y="+zoomWindow_y);
	  // used in mousedragged but i'm not sure what it will do
	  wasDragging=-1;
	  new_y=zoomWindow_y;
	}
	System.out.println("...NewY="+new_y+" shift="+vertShift+" zoomwindow_y="+zoomWindow_y);
      }
    }
    drawZoomWindow(backGC,new_y);
    update(getGraphics());

  }


  public void showBrowserArea3(int lineNum) {
    Dimension size=getSize();
    System.out.println("========================================");
    int i=0, new_y=0, win_h=0, holder=0;
    boolean found=false;
    RH_LineInfo line=null;
    Graphics gc=null;
    if (bufferSet) gc=backGC;
    else gc=getGraphics();

    if (lineNum==0) {
      new_y=wasDragging=0;
    }
    else {
      for (i=0; i<lineInfoPtr-1 && lineInfo[i]!=null && lineInfo[i].getY()<lineNum; i++);
      if (i<lineInfoPtr-1) {
	line=lineInfo[i];
	win_h=size.height-offset_vert-zoomWindow_h;
	holder=previousLastLoc_y;
	wasDragging=win_h;
	previousLastLoc_y=holder;

	//new MouseEvent(this,MouseEvent.MOUSE_DRAGGED,0,0,0,new_y,1,false);
	
	//vertShift=wasDragging=0;
	new_y=(int)line.getY()/reductionValue; 
	
	//*** Cond 1: Lens within overview pane (not at the bottom when you need to scroll the pane contents up)
	if (new_y>0 && new_y-vertShift<=win_h) {
	  System.out.println("Cond1: "+new_y+ " Shift="+vertShift);
	  wasDragging=new_y;
	  
	  //*** Cond 1A: When the lens is moving in the current overview pane either up or down but the *very* top is not visible
	  if (vertShift>0) {	  
	    System.out.println("...Cond1A: "+new_y+ " Shift="+vertShift);
	  }
	  //*** Cond 1B: When shift is zero, we have not shifted the contents either up or down
	  else {
	    System.out.println("...Cond1B: "+new_y+ " Shift="+vertShift);
	  }
	}
	//*** Cond 2: When you start moveing down the document, you need to scroll contents upward
	else if (new_y>win_h) {
	  System.out.println("Cond2: "+new_y+" Shift="+vertShift+" Amount added-"+(new_y-win_h));
	  wasDragging=win_h;
	  vertShift=vertShift+(new_y-win_h);
	  
	  //*** Cond 2A: when the lens is at the bottom of the screen
	  if (zoomWindow_y>=win_h) {
	    new_y=win_h;
	    System.out.println("...Cond2A: "+new_y+" Shift="+vertShift);
	  }
	  //*** Cond 2B: when the lens is not at the bottom of the screen
	  else {
	    System.out.println("...Cond2B: "+new_y+" Shift="+vertShift);
	  }
	}
      }
    }
    System.out.println("**FINAL: New_y="+new_y+" Shift="+vertShift+" Win_h="+win_h+" LastY="+lastY);
    drawZoomWindow(gc,new_y);
    update(getGraphics());
  }

  /*
  public void showBrowserArea2(int lineNum) {
    Dimension size = getSize();
    int win_h= size.height-offset_vert-zoomWindow_h, newy=0, i=0, top=0, holder=0;
    Graphics gc=null;
    RH_LineInfo line=null;

    if (bufferSet) gc=backGC;
    else gc=getGraphics();
    
    if (lineNum<=top) {
      System.out.println("--CondA lineNum="+lineNum);
      newy=wasDragging=top;
      drawZoomWindow(gc,top);
      update(getGraphics());
      lastLens_y=top;
    }
    else {
      for (i=0; i<lineInfoPtr-1 && lineInfo[i]!=null && lineInfo[i].getY()<lineNum; i++);
      if (i<lineInfoPtr-1) {
	line=lineInfo[i];
	newy=(int)line.getY()/reductionValue; 
	win_h=size.height-offset_vert-zoomWindow_h;
	useLastLocation=true;
      
	System.out.print("**LineNum="+lineNum+" Newy=" + newy + ">"+line.getText()+" lastLens_y="+lastLens_y);
	if (newy<=lastY) {	  
	  if (newy>top && (newy-vertShift)<win_h) {
	    System.out.print("--Cond1");
	    if (newy<=lastLens_y) {
	      System.out.print("--A--");
	      wasDragging=newy;
	      drawZoomWindow(gc,newy);
	    }
	    else if (newy>=win_h) {
	      System.out.print("--B--");
	      wasDragging=win_h;
	      if (longDocument) vertShift+=(newy-win_h);
	      drawZoomWindow(gc,win_h);
	    }
	    else {
	      System.out.print("--C--");
	      wasDragging=newy;
	      drawZoomWindow(gc,newy); //+topImageHeight);
	    }
	    update(getGraphics());
	  }
	  else if (newy<=top) {
	    System.out.print("--Cond2--");
	    wasDragging=top;
	    if (vertShift>0) vertShift+=newy;
	    drawZoomWindow(gc,top);
	    update(getGraphics());
	  }
	  else if (newy>=win_h) {
	    System.out.print("--Cond3--");
	    wasDragging=win_h;
	    if (longDocument) vertShift+=newy-win_h;
	    drawZoomWindow(gc,win_h);
	    update(getGraphics());
	  }
	  else {
	    System.out.print("--Cond4--");
	    // else redraw the window where it was last found -- this hopefully will prevent the window
	    // from doing wild things like disappearing from the screen
	    wasDragging=-1;
	    drawZoomWindow(gc,zoomWindow_y);
	  }
	  lastLens_y=newy;
	  System.out.println(" Shift=" + vertShift + " win_h=" + win_h  + " lineInfoPtr=" + lineInfoPtr);
	}
	// Else we have moved the mouse beyond the end of the doc so just go to the end of the doc
	else if (lineInfoPtr>0 && !longDocument && newy>lastY) {
	  if (bufferSet) gc=backGC;
	  else gc=getGraphics();
	  wasDragging=lastY+bottomImageHeight;
	  //lastY=((int)y+bottomImageHeight)-zoomWindowInside_h;
	  //System.out.println("$$$Newy=" + newy + " wasDragging="+wasDragging);
	  System.out.println("--Cond5-- Shift="+vertShift);
	  drawZoomWindow(gc,wasDragging);
	  update(getGraphics());
	  lastLens_y=newy;
	}
      }     
    }
  }
  */

public void showBrowserAreaGreatest(int lineNum) {
    Dimension size = getSize();
    int win_h= size.height-offset_vert-zoomWindow_h, newy=0, i=0, top=0, holder=0, actual_y=0;
    Graphics gc=null;
    RH_LineInfo line=null;

    if (bufferSet) gc=backGC;
    else gc=getGraphics();
    
    if (lineNum<=top) {
      System.out.println("--CondA lineNum="+lineNum);
      newy=wasDragging=top;
      drawZoomWindow(gc,top);
      update(getGraphics());
      lastLens_y=top;
    }
    else {
      for (i=0; i<lineInfoPtr-1 && lineInfo[i]!=null && lineInfo[i].getY()<lineNum; i++);
      if (i<lineInfoPtr-1) {
	line=lineInfo[i];
	actual_y=(int)line.getY()/reductionValue; 
	win_h=size.height-offset_vert-zoomWindow_h;
	useLastLocation=true;

	System.out.println("***zoomWindow_y="+zoomWindow_y+" actual_y="+actual_y+" DIFF="+(actual_y-zoomWindow_y));
      }     
    }
  }

    /**
     * Sets the lens to a given location in the thumbar
     *
     *@param lineNum the line number to resposition the lens to 
     */
  public void setLensLocation(int lineNum) {
    RH_LineInfo line=null;
    Dimension size = getSize();
    Graphics gc=null;
    int win_h= size.height-offset_vert-zoomWindow_h, newy=0, i=0, top=0, holder=0, actual_y=0, diff=0;
    //System.out.println("***Thumbar: setLens Loc:"+lineNum+" lastLens_y="+lastLens_y);
    for (i=0; i<lineInfoPtr-1 && lineInfo[i]!=null && lineInfo[i].getY()<lineNum; i++);
    if (i<lineInfoPtr-1) {
      if (bufferSet) gc=backGC;
      else gc=getGraphics();
      line=lineInfo[i];
      actual_y=lineNum/reductionValue; 
      win_h=size.height-offset_vert-zoomWindow_h;
      if (actual_y>win_h) {
	wasDragging=win_h;
	vertShift=actual_y-win_h;
      }
      else {
	useLastLocation=true;
	diff=(actual_y-lastLens_y);
	wasDragging=actual_y;
      }
      //System.out.println("***["+line.getText()+"] zoomWindow_y="+zoomWindow_y+" actual_y="+actual_y+" lastLens_y="+lastLens_y);
      drawZoomWindow(gc,wasDragging);
      update(getGraphics());
    }
  }

    /**
     * This method if used by the commBus to send a message to the thumbar telling the thumbar to reposition
     * itself at the new location.  A line number is specified (lineNum) which must be translated into a RH_LineInfo
     * object in the lineInfo[] array.  We must take into consideration the fact that the user may have scrolled the
     * whole thumbar down and so a simple lookup of the line number will not always work.  Instead, we sometimes must
     * take into consideration the offset amount (i.e. the amount of scrolling) which is defined as <b>vertShift</a>.
     * If vertShift > 0 then the user has scrolled the whole thumbar (i.e. now there is a green line at the top of the thumbar).
     *
     *@param lineNum line number to reposition lens to
     */
  public void showBrowserArea(int lineNum) {
    Dimension size = getSize();
    int win_h= size.height-offset_vert-zoomWindow_h, newy=0, i=0, top=0, holder=0, actual_y=0, diff=0;
    Graphics gc=null;
    RH_LineInfo line=null;
    boolean debug=false;
    //System.out.println("***ShowBrowserArea: lineNum:"+lineNum+" wasDragging="+wasDragging);

    //System.out.println("&&&&&&&&&&LineNum="+lineNum+" which is really "+(lineNum/reductionValue)+" plus zoomWindow_H:"+zoomWindow_h+
    //	       " equals:"+(lineNum/reductionValue+zoomWindow_h));
    if (bufferSet) gc=backGC;
    else gc=getGraphics();
    if (gc!=null) {
	if (debug) System.out.println(".............................................................");
	
	if (lineNum<=top) {
	    if (debug) System.out.println("--CondA lineNum="+lineNum);
	    actual_y=wasDragging=top;
	    vertShift=0;
	    drawZoomWindow(gc,top);
	    update(getGraphics());
	    lastLens_y=top;
	}
	else if (((wasDragging=lineNum/reductionValue)-zoomWindow_h)>=lastY) {
	    if (debug) System.out.println("--CondC lineNum="+lineNum+ " wasDragging="+wasDragging+" lastY="+lastY);
	    //(lastY+zoomWindow_h)-wasDragging;
	    actual_y=lastY;
	    vertShift=lastY-win_h;  // this could be wrong
	    if (wasDragging>win_h) wasDragging=win_h;
	    drawZoomWindow(gc,wasDragging);
	    update(getGraphics());
	    lastLens_y=actual_y;
	}
	else {
	    if (debug) System.out.print("-----Go Figure");
	    for (i=0; i<lineInfoPtr-1 && lineInfo[i]!=null && lineInfo[i].getY()<lineNum; i++);
	    if (debug) System.out.println("== i="+i+" lineINfoPtr="+lineInfoPtr);
	    if (i<lineInfoPtr-1) {
		line=lineInfo[i];
		actual_y=lineNum/reductionValue; 
		//actual_y=(int)line.getY()/reductionValue; 
		win_h=size.height-offset_vert-zoomWindow_h;
		useLastLocation=true;
		diff=(actual_y-lastLens_y);
		
		if (debug) System.out.println("***["+line.getText()+"] zoomWindow_y="+zoomWindow_y+" actual_y="+actual_y+" DIFF="+diff+" lastLens_y="+lastLens_y);
		//System.out.print("**LineNum="+lineNum+" Actual_y=" +actual_y+ ">"+line.getText()+" lastLens_y="+lastLens_y);
		
		//*** DIFF will be <0 when going up and >=0 when going down
		if (actual_y<=lastY) {	  
		    if (actual_y>top && actual_y<win_h) {
			if (debug) System.out.print("--Cond1");
			if (vertShift>0 && diff>0) {
			    if (debug) System.out.print(".A");
			    if (diff>0) {
				if (debug) System.out.print(".1");
				wasDragging=zoomWindow_y+diff; //actual_y-vertShift;
			    }
			    else if (diff<0) {
				if (debug) System.out.print(".2");
				wasDragging=zoomWindow_y+diff; //actual_y-vertShift;
				if (wasDragging<top || zoomWindow_y==top) {
				    wasDragging=top;
				    vertShift+=diff;
				}
			    }
			}
			else {
			    if (debug) System.out.print(".B");
			    if (diff>=0) {
				if (debug) System.out.print(".1");
				wasDragging=zoomWindow_y+diff;
				if (wasDragging<=top) {
				    wasDragging=top;
				}
			    }
			    else {
				if (debug) System.out.print(".2");
				wasDragging=zoomWindow_y+diff;
				if (wasDragging<=top) {
				    wasDragging=top;
				    vertShift+=diff;
				}
			    }
			}
			drawZoomWindow(gc,wasDragging);
			update(getGraphics());
		    }
		    else if (actual_y>=win_h) {
			if (debug) System.out.print("--Cond2");
			if (diff>0 && zoomWindow_y+zoomWindow_h>=win_h) {
			    if (debug) System.out.print(".A");
			    vertShift+=(zoomWindow_y+diff)-win_h;
			    wasDragging=win_h;
			}
			else if (diff<0 && vertShift>0) {
			    if (debug) System.out.print(".B");
			    wasDragging=zoomWindow_y+diff;
			    if (wasDragging>win_h) wasDragging=win_h;
			    else if (wasDragging<top) {
				wasDragging=top;
				vertShift+=diff;
			    }
			}
			else {
			    if (debug) System.out.print(".C");
			    wasDragging=actual_y-vertShift;
			    if (wasDragging>win_h) wasDragging=win_h;
			    else if (wasDragging<top) {
				wasDragging=top;
				vertShift+=diff;
			    }
			}
			drawZoomWindow(gc,wasDragging);
			update(getGraphics());
		    }
		    else {
			if (debug) System.out.print("--Cond4--");
			// else redraw the window where it was last found -- this hopefully will prevent the window
			// from doing wild things like disappearing from the screen
			wasDragging=-1;
			drawZoomWindow(gc,zoomWindow_y);
		    }
		    lastLens_y=actual_y;
		    if (debug) System.out.println(" WasDragging="+wasDragging+ " Shift=" + vertShift + " win_h=" + win_h+" lastY="+lastY+" zoomWin_h="+
						  zoomWindow_h);
		}
		// Else we have moved the mouse beyond the end of the doc so just go to the end of the doc
		else if (lineInfoPtr>0 && actual_y>lastY) {
		    wasDragging=win_h;
		    if (debug) System.out.println("--Cond5-- wasDragging="+wasDragging+ " Shift="+vertShift+" win_h="+win_h+" lastY="+lastY);
		    drawZoomWindow(gc,wasDragging);
		    update(getGraphics());
		    lastLens_y=actual_y;
		}
	    }     
	}
    }
  }


    /**
     * Typically a button will be assigned to this method which when pressed, takes the
     * user to the top of the doucment and repositions the lens accordingly.
     */
  public void navModeToTop() {
    vertShift=0;  // reset
    drawZoomWindow(backGC,0);
    update(getGraphics());
    lastLens_y=0;
    repaint();
    commBus.showDocViewArea(0);
  }
  public void navModeScreenUp() {
    clearZoomWindow(getGraphics(), zoomWindow_y);
    zoomWindow_y=0;
    repaint();
  }
  public void navModeScreenDown() {
    clearZoomWindow(getGraphics(), zoomWindow_y);
    Dimension size= getSize();
    int win_h= size.height-offset_vert-zoomWindow_h;
    repaint();
  }
    /**
     * Typically a button will be assigned to this method which will reposition the lens at the bottom of the document
     */
  public void navModeToBottom() {
    if (lineInfoPtr>0) {
      Dimension size= getSize();
      int win_h=size.height-offset_vert-zoomWindow_h, new_y=0, holder=previousLastLoc_y;
      wasDragging=win_h;
      showLineLoc=findMouseLoc();
      previousLastLoc_y=holder;
      if (longDocument) {
	vertShift=(int)((float)lastLine.getY()/reductionValue)-win_h-zoomWindow_h; 
	new_y=size.height-offset_vert-zoomWindow_h;
      }
      else {
	vertShift=wasDragging=0;
	//new_y=(int)lineInfo[showLineLoc].getY()/reductionValue; 
	new_y=wasDragging=lastY-(zoomWindow_h-bottomImageHeight-topImageHeight); //lastY+bottomImageHeight;
      }
      
      drawZoomWindow(backGC,new_y);
      update(getGraphics());
      lastLens_y=lastY;
      commBus.showDocViewArea(lineInfo[showLineLoc].getY());
    }
  }

  public String getVersion() {
    return version;
  }
  
    public boolean switchImageScalingMethod() {
	if (betterImageScaling) return betterImageScaling=false; 
	else return betterImageScaling=true;
    }

    public int getThumbarImageStatus() {
	return imageTracker.statusAll(true);
    }
    public boolean thumbarDone() {
	if (imageTrackerCount==0 || imageTracker.statusAll(true)==MediaTracker.COMPLETE ||
	    imageTracker.statusAll(true)==MediaTracker.ERRORED || imageTracker.statusAll(true)==MediaTracker.ABORTED)
	    return true;
	else return false;
    }
    public boolean checkThumbarLoading() {
	return imageTracker.checkAll(true);
    }
}



