/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 *  Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 6.14.97 revised 02-11-98
 *
 */
package ricoh.rh;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;

public class RH_ThumbarControl extends Panel 
implements ActionListener {
  public RH_CommBus commBus;

  public RH_Thumbar thumbar;
  public RH_ThumbarMode modeControl;

  // this are the dimensions for the *whole* canvas
  private int width=0, height=0, smallWidth=80, mediumWidth=90, largeWidth=100; 
  private int rect_w, rect_h;
  private int docview_w=0, docview_h=0, docview_x,docview_y, modeLabel_h=29;  // this is the width of the actual document view window.
  // these are the offsets for the amount of space between the whole width or height and the document view area
  private int offset_vert=4, 
      offset_horz=3, //14, 
      left_offset=5; //10;
  public int zoomWindow_y=50; // this is the Y location of the zoom window
  private Label text;
  private Image logo;
  //private EtchedRectangle rect;
  private Color  documentColor = new Color(245,247,248),
    viewbarColor=Color.lightGray, modeBackColor=Color.lightGray, modeForeColor=Color.blue,
    offGray=new Color(160,160,164), defaultLineColor=Color.gray, defaultLensLineColor=Color.gray;
  private Color backColor=Color.gray, textColor=Color.white, highlightColor=Color.white, shadowColor=Color.black,
      shadowColor2=Color.gray;
  public String mode="Mode";
  private String modeFont="Arial";
  private int docviewSize=0; // 0 is small, 1 is larger, 2 is largest
  // this means we shrink the docview window to X times the size of the main document window
  public int  largeReductionValue=6, reductionValue=0;
  
  public RH_ThumbarControl (RH_CommBus bus, int h) {
    super();
    commBus=bus;
    backColor=commBus.getMainBackColor();
    textColor=commBus.getMainTextColor();
    highlightColor=commBus.getMainHighlightColor();
    shadowColor=commBus.getMainShadowColor();
    shadowColor2=commBus.getMainShadowColor2();
    defaultLineColor=commBus.getOverviewWindowLineColor();
    defaultLensLineColor=commBus.getOverviewLensLineColor();
    //docviewSize=newsize;
    int motif=commBus.getMotifNumber();

    reductionValue=commBus.getLensViewFraction();
    if (reductionValue>6) offset_horz=2;
    setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
    setBackground(backColor);
    getPreferredSize();
    Dimension size = getSize();

    height=h;
    //setDocview();  // this will be used by commBus so that user can specify size
    setSize(width,height);

    //System.out.println("***DONE MAKING THUMBAR CONTROL...fraction="+reductionValue);
    //repaint();
    //addMouseListener(this);
    //addMouseMotionListener(this);
    setVisible(true);
  }
  
  /*  public void addNotify() {
    super.addNotify();
    System.out.println("-->AddNotify setupbuffer");

  }*/

  public void setupThumbar() {
    Dimension size = getSize();
    //System.out.println("***THUMBARCONTROL WIDTH="+size.width+" HEIGHT="+size.height);
    removeAll();
    //System.out.println("***setDoneview...reduction="+reductionValue);
    width=(int)(commBus.getBrowserCanvasWidth()/reductionValue);
    //System.out.println("***setDoneview...width="+width);
    setSize(width,height);
    thumbar=new RH_Thumbar(this,docviewSize,offset_horz,offset_vert,left_offset,modeLabel_h);
    //System.out.println("***setDoneview...docviewSize="+docviewSize+" offset_horz="+offset_horz+" offset_vert="+offset_vert+
    //	       " left_offset="+left_offset+" modeLabel_h="+modeLabel_h);
    modeControl=new RH_ThumbarMode(this,modeLabel_h,docviewSize);
    add(modeControl);
    add(thumbar);
    thumbar.setupThumbar();
  }
  public void setModeLabel(String newmode) {
    mode=newmode;
    modeControl.update(modeControl.getGraphics());
  }
  public void actionPerformed(ActionEvent ev) {
    Object source = ev.getSource();
    System.out.println("Somthing happened");
  }
  /*
  public void mouseDragged(MouseEvent ev) {
  }
  public void mouseMoved(MouseEvent ev) {
  }
  public void mouseClicked(MouseEvent ev) {
  }
  public void mousePressed(MouseEvent ev) {
  }
  public void mouseReleased(MouseEvent ev) {
  }
  public void mouseEntered(MouseEvent ev) {
  }
  public void mouseExited(MouseEvent ev) {
  }
  */

  public Dimension getMinimumSize() { return getPreferredSize(); }
  public Dimension getPreferredSize() {
    return new Dimension(width,height);
  }
  public void setSize (int w, int h) {
    setBounds(new Rectangle(getLocation().x,getLocation().y,w,h));
    //rect.resize(rect_w=w-2,rect_h=h-1);
  }

  public void update(Graphics gc) {
    paint(gc);
  }
  public void paint(Graphics gc) {
    Dimension size=getSize(), modeSize=modeControl.getSize();
    updateDocView(gc);
    modeControl.repaint();
    //gc.setColor(highlightColor);
    //gc.drawLine(0,0,size.width,1);
    //gc.setColor(Color.white);  // use white here because it's next to a lightGray border
    //gc.drawLine(0,0,0,size.height);
  }

  public void updateDocView(Graphics gc) {
    Dimension size     = getSize();
    Dimension v_size = commBus.getLocationControlSize();
    docview_w=size.width-offset_horz;
    docview_h=size.height-offset_vert;
    int modeLabel_w=docview_w-12;
    
    int i=1, mode_h=0, //commBus.getModeLabelHeight()+2,
      bar_h=v_size.height,
      vHeight=bar_h+mode_h;

    // Connecting bar with ViewToolbar
    gc.setColor(viewbarColor);
    gc.drawLine(left_offset+1,vHeight-1,docview_w-3,vHeight-1); 
    //gc.setColor(Color.red);
    //gc.drawLine(left_offset+2,vHeight,docview_w-4,vHeight); 

    // Top lines
    gc.setColor(highlightColor);
    gc.drawLine(1,0,size.width,0); 
    //gc.setColor(Color.lightGray);
    //    gc.drawLine(left_offset+1,1,size.width,1); 

    // the two lines that connect this window to the viwbar at the inside corner
    gc.setColor(Color.gray);
    gc.drawLine(size.width-offset_horz-1,vHeight-mode_h-2,size.width,vHeight-mode_h-2); 
    gc.drawLine(size.width-offset_horz-1,vHeight-mode_h-1,size.width-offset_horz,vHeight-mode_h-1); 
    gc.setColor(Color.black);
    gc.drawLine(size.width-offset_horz,vHeight-mode_h-1,size.width,vHeight-mode_h-1); 
    gc.setColor(Color.gray);
    gc.drawLine(size.width-(offset_horz-1),vHeight-mode_h,size.width,vHeight-mode_h); 
    gc.drawLine(size.width-(offset_horz-2),vHeight-mode_h+2,size.width,vHeight-mode_h+2); 

    // Left lines
    i=0;
    //gc.setColor(Color.white);
    //gc.drawLine(left_offset+i,i,left_offset+i++,vHeight+1); 
    gc.setColor(Color.white);
    gc.drawLine(1,0,1,vHeight+1); 
    // bottom lines
    gc.setColor(Color.black);
    gc.drawLine(1,vHeight-1,size.width,vHeight-1); 
    gc.setColor(Color.darkGray);
    gc.drawLine(2,vHeight,size.width,vHeight); 
    gc.drawLine(3,vHeight+1,size.width,vHeight+1); 

  }

  public void receiveLineString (String line, int x, int y, int h,int w,int boxnum,int fonttype) {
    //System.out.println("==>RX: "+line);
    if (thumbar!=null) thumbar.receiveLineString(line,x,y,h,w,defaultLineColor,boxnum,fonttype);
  }
  public void receiveLineString (String line, int x, int y, int h, int w, Color color,int boxnum,int fonttype) {
    //System.out.println("==>RX: "+line);
    if (thumbar!=null) thumbar.receiveLineString(line,x,y,h,w,color,boxnum,fonttype);
  }
  public void receiveLineString (Image image, int x, int y, int h, int w, Color color, int boxnum,int fonttype) {
    //System.out.println("==>RX: "+image);
    if (thumbar!=null) thumbar.receiveLineString(image,x,y,h,w,color,boxnum,fonttype);
  }
  public void receiveLineString (Image image, int x, int y, int h, int w, int boxnum,int fonttype) {
    //System.out.println("==>RX: "+image);
    if (thumbar!=null) thumbar.receiveLineString(image,x,y,h,w,defaultLineColor,boxnum,fonttype);
  }
  // for handling table images (and others?)
  public void receiveLineString (Image image, int x, int y, int h, int w, Color color, int boxnum) {
    //System.out.println("==>RX: "+image);
    if (thumbar!=null) thumbar.receiveLineString(image,x,y,h,w,color,boxnum);
  }
  /**
   * Sends a message to DocviewArea stating the last Y coordinate for the current document
   */
  public void sendBottomCoordinate(int lasty) {
    if (thumbar!=null) thumbar.sendBottomCoordinate(lasty);
  }

  public void loadingNewDocument() {
    commBus.parent.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    if (thumbar!=null) thumbar.loadingNewDocument();
  }
  public void documentResized () {
    if (modeControl!=null & thumbar!=null) {
      //System.out.println("===ThumbarControl: resizing");
      width=(int)(commBus.getBrowserCanvasWidth()/reductionValue);
      height=commBus.getBrowserCanvasHeight()+modeControl.getSize().height;
      setSize(width,height);
      thumbar.documentResized();
    }
  }
  /*
  public void updateThumbar() {
    System.out.println("===ThumbarControl: update");
    if (thumbar!=null) thumbar.updateThumbar();
  }
  */
  public void setToDone() {
    //System.out.println("===ThumbarControl: setToDone");
    commBus.parent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    if (thumbar!=null) {
      thumbar.setToDone();
      repaint();
      //thumbar.updateThumbar();
    }
  }
  public void sendDocumentBufferSize(int bufferSize, boolean reset) {
    if (thumbar!=null) thumbar.sendDocumentBufferSize(bufferSize,reset);
  }
  public int getMaxNumberVisibleLines() {
    return (thumbar!=null ? thumbar.getMaxNumberVisibleLines() : 0);
  }
  public void refreshDocument() {
    //System.out.println("===ThumbarControl: refreshing");
    if (thumbar!=null) thumbar.refreshDocument();
  }

  public void setLensLocation(int loc) {
    //System.out.println("***ThumbarControl: set lens loc:"+loc);
    thumbar.setLensLocation(loc);
  }

    public boolean switchImageScalingMethod() {
	return thumbar.switchImageScalingMethod();
    }
    public void navModeToTop() {
	if (thumbar!=null) thumbar.navModeToTop();
    }
    public void navModeScreenUp() {
	if (thumbar!=null) thumbar.navModeScreenUp();
    }
    public void navModeScreenDown() {
	if (thumbar!=null) thumbar.navModeScreenDown();
    }
    public void navModeToBottom() {
	if (thumbar!=null) thumbar.navModeToBottom();
    }
    public void showBrowserArea(int lineNum) {
	//System.out.println("Docview: Browser Moved="+lineNum);
	if (thumbar!=null && lineNum>=0) thumbar.showBrowserArea(lineNum);
    }
    public String getVersion() { return (thumbar!=null ? thumbar.getVersion() : ""); }
    
    public void setWaitCursor() {
	setCursor(new Cursor(Cursor.WAIT_CURSOR));
    }
    public void setDefaultCursor() {
	setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    public int getThumbarImageStatus() {
	return (thumbar!=null ? thumbar.getThumbarImageStatus() : 0);
    }
    public void setThumbarStatusStart() {
	if (modeControl!=null) modeControl.setThumbarStatusStart();
    }
    public void setThumbarStatusDone() {
	if (modeControl!=null) modeControl.setThumbarStatusDone();
    }
    public boolean thumbarDone() {
	return (thumbar!=null ? thumbar.thumbarDone() : true);
    }
    public boolean checkThumbarLoading() {
	return (thumbar!=null ? thumbar.checkThumbarLoading() : true);
    }
    public void updateThumbar() {
	if (modeControl!=null & thumbar!=null) {
	    //System.out.println("===ThumbarControl: resizing");
	    width=(int)(commBus.getBrowserCanvasWidth()/reductionValue);
	    height=commBus.getBrowserCanvasHeight()+modeControl.getSize().height;
	    setSize(width,height);
	    if (thumbar!=null) thumbar.updateThumbar();
	}
    }
}	
  

