/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 *  Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 8.21.97
 *
 */
package ricoh.rh;
 
import java.applet.Applet; 
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.io.*;
import java.lang.*;
import java.util.StringTokenizer;
import java.util.*;
import java.awt.image.ImageObserver;


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

public class RH_MainToolbar extends JPanel 
implements ActionListener {
  public RH_CommBus commBus;

  public RH_AnimatedLogo mainLogo;
  private boolean animate=true, freeze=false;

  private Color backColor=Color.gray, textColor=Color.white, locationBackColor=Color.lightGray, locationTextColor=Color.black,
    shadowColor=Color.black, shadowColor2=Color.gray, highlightColor=Color.white, buttonBackColor=Color.lightGray;
  private int top=2, left=1, bottom=1, right=1, width=0, height=0, numNavButtons=5, numANOHButtons=6, numOptionsButtons=6;
  public int backID=0, fwdID=1, homeID=2, reloadID=3, stopID=4, anohOptionsID=5, anohBackID=6, anohFwdID=7, anohReloadID=8,
    navigateID=9,anohID=10, cacheID=11, optionsID=12, searchID=13, loadimagesID=14, anohSummaryID=15, anohGroupedID=16,
    printerID=17, saveConceptsID=18, calendarID=19, anohSimilarID=20, collectionID=21, sendMailID=22;

  private JToolBar navToolbar, anohToolbar, optionsToolbar;
    private boolean plainText=false;
  RH_ToolButton navigate, back, fwd, reload, home, stop, anohNavigate, anohProfile, anohSummary, anohSimilar,
        options, print,calendar, search, images, cache, savestate, collection, sendmail;

  public RH_MainToolbar (RH_CommBus bus,int w, int h) {
      setDoubleBuffered(true);
    commBus=bus;
    backColor=commBus.getMainBackColor();
    textColor=commBus.getMainTextColor();
    highlightColor=commBus.getMainHighlightColor();
    shadowColor=commBus.getMainShadowColor();
    shadowColor2=commBus.getMainShadowColor2();
    int motif=commBus.getMotifNumber();
    width=w; height=h;
    setSize(width,height);
    plainText=false;

    int xpos=0;
    
    backColor=Color.lightGray;

    setFont(new Font("MS Sans Serif",Font.PLAIN,11));
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    setLayout(gbl);
    setBackground(backColor);
    setForeground(textColor);

    navToolbar = new JToolBar();
    navToolbar.setBackground(backColor);
    navToolbar.setMargin(new Insets(0,0,0,0));
    navToolbar.setDoubleBuffered(true);
    //***When convert o Swing 1.01, uncomment this!!!
    navToolbar.setFloatable(false);

    navigate=addTool(navToolbar,"navlabel-n","Navigation Mode",false);
    navigate.setEnabled(false);
    navigate.setDoubleBuffered(true);
    RH_ToolButton separator=addTool(navToolbar,"toolbarseparator-n","",true);
    separator.setEnabled(false);
    //navToolbar.addSeparator();
    back=addTool(navToolbar,"backbutton-n","Go Back",true);
    back.setDoubleBuffered(true);
    fwd=addTool(navToolbar,"fwdbutton-n","Go forward",true);
    reload=addTool(navToolbar,"reloadbutton-n","Reload Document",true);
    //reload.setKeyAccelerator('r');
    home=addTool(navToolbar,"homebutton-n","Go Home",true);
    stop=addTool(navToolbar,"stopbutton-n","Stop Load",true);
    //navToolbar.addSeparator();
    separator=addTool(navToolbar,"toolbarseparator-n","",true);
    separator.setEnabled(false);
    //navToolbar.addSeparator();

    //anohNavigate=addTool(navToolbar,"anohlabel-n","Annotate/Navigate Mode",false);
    //anohNavigate.setEnabled(true);
    //anohBack=addTool(navToolbar, "backbutton-n","Annotation: Go Back",true);
    //anohFwd=addTool(navToolbar,"fwdbutton-n","Annotation: Go Forward",true);
    //anohReload=addTool(navToolbar,"reloadbutton-n","Annotation: Reload Document",true);
    anohSummary=addTool(navToolbar,"summarybutton-n","Annotation: Document Summaries",true);
    anohSimilar=addTool(navToolbar,"similarbutton-n","Discover similar documents to an annotated document",true);
    //anohProfile=addTool(navToolbar,"optionsbutton-n","User Profile Options and Settings",true);
    //navToolbar.addSeparator();

    //separator=addTool(navToolbar,"toolbarseparator-n","",true);
    //separator.setEnabled(false);
    //navToolbar.addSeparator();
    collection=addTool(navToolbar,"collectionbutton-n","Access Document Archive",true);
    calendar=addTool(navToolbar,"calendarbutton-n","Calendar Interface",true);
    print=addTool(navToolbar,"printbutton-n","Print Document",true);
    cache=addTool(navToolbar,"cachebutton-n","Clear Document Cache",true);
    cache.setEnabled(false);
    search=addTool(navToolbar,"searchbutton-n","Search",true);
    sendmail=addTool(navToolbar,"sendmailbutton-n","Email document to another user",true);
    images=addTool(navToolbar,"loadimagesbutton-n","Load Images",true);
    //savestate=addTool(navToolbar,"savestatebutton-n","Save current concept state as default",true);

    animate=commBus.animateLogo();
    mainLogo = new RH_AnimatedLogo(this, commBus.getGifsPath());
    add(mainLogo);

    //gbc.fill=GridBagConstraints.NONE;
    gbc.fill=GridBagConstraints.HORIZONTAL;
    gbc.insets=new Insets(0,0,0,0);
    gbc.anchor = GridBagConstraints.WEST;

    buildConstraints(gbc,0,0,1,1,98,100);
    gbl.setConstraints(navToolbar,gbc);
    add(navToolbar);

    /*
    buildConstraints(gbc,1,0,1,1,1,1);
    gbl.setConstraints(anohToolbar,gbc);
    add(anohToolbar);


    buildConstraints(gbc,1,0,1,1,35,100);
    gbl.setConstraints(optionsToolbar,gbc);
    add(optionsToolbar);
    */

    gbc.fill=GridBagConstraints.REMAINDER;  // NONE
    gbc.anchor = GridBagConstraints.EAST;
    buildConstraints(gbc,1,0,1,1,2,100);
    gbl.setConstraints(mainLogo,gbc);
    
    stop.setEnabled(false);
    back.setEnabled(true);
    //anohBack.setEnabled(false);
    fwd.setEnabled(true);
    //anohFwd.setEnabled(false);
    anohSimilar.setEnabled(false);
    anohSummary.setEnabled(false);
    freeze=false;
    activatePlainText();
    noSummaryAvailable();
  }

  public RH_ToolButton addTool(JToolBar toolBar, String name, String tip, boolean rollover) {
    String path=commBus.getGifsPath()+"/";
    String imageName=path+name+".gif";
    RH_ToolButton b = (RH_ToolButton)toolBar.add(new RH_ToolButton(new ImageIcon(imageName,name)));
    b.setToolTipText(tip);
    imageName=path+name+"-d.gif";
    b.setDisabledIcon(new ImageIcon(imageName,"Disabled "+name));
    b.setFocusPainted(false);
    b.setBorderPainted(false);
    b.setMargin(new Insets(0,0,0,0));
    b.setBackground(backColor);
    b.addActionListener(this);
    if (rollover) {
      b.setRolloverEnabled(true);
      imageName=path+name+"-r.gif";
      b.setRolloverIcon(new ImageIcon(imageName,""));
    }
    return b;
  }

  /**
   * Perform this action whenever a toolbutton is selected
   */
  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();
    if (source==reload) {
      commBus.reloadURL();
    }
    if (source==back && !freeze) {
      // Go back in history list
      commBus.setPlainTextMode();
      commBus.browserGoBack();
      //commBus.setTextURL(commBus.getCurrentURL());
      commBus.statusMsg1(commBus.getCurrentURL()); // CHANGE THIS
    }
    else if (source==fwd && !freeze) {
      // Go forward in history list
      commBus.setPlainTextMode();
      commBus.browserGoForward();
      commBus.statusMsg1(commBus.getCurrentURL()); // CHANGE THIS
    }
    else if (source==stop) {
      commBus.browserURL_Stop();
      commBus.statusMsg1("");
      commBus.statusMsg2("Stopped");
    }
    else if (source==home && !freeze) {
	commBus.gotoHomeURL();
    }
    //else if (source==anohProfile && !freeze) {
	//RH_ProfileEditor editor = new RH_ProfileEditor(commBus.parent,commBus.getUserFirstName()+"'s Profile Editor - Modified:"+commBus.getLastModified());
    //    }
    /*
      else if (source==anohBack && !freeze) {
      // Go back in history list
      commBus.setAnnotateMode();
      commBus.browserGoBack();
      }
      else if (source==anohFwd && !freeze) {
      commBus.setAnnotateMode();
      commBus.browserGoForward();
      }
      else if (source==anohReload && !freeze) {
      commBus.anohReloadURL();
      }
    */
    else if (source==anohSummary && !freeze) {
      commBus.setSummaryMode();
    }
    else if (source==anohSimilar && !freeze) {
      commBus.showSimilarites();
    }
    else if (source==print && !freeze) {
      commBus.printDocument();
    }
    else if (source==cache && !freeze) {
      commBus.clearDocCache();
    }
    else if (source==search && !freeze) {
	//commBus.searchText();
	String str=commBus.requestProxyContent(RH_GlobalVars.piaProxyMsgPutSensitivity);
	System.out.println(">>"+str);
	/*
	  //** Request matching on this doc
	       String str=commBus.requestProxyContent(RH_GlobalVars.piaProxyMsgProcessContent);
	       if (str!=null) commBus.setAnnotationBuffer(str.getBytes());
	       //** Now ask for the values for each concept
		    str=commBus.requestProxyContent(RH_GlobalVars.piaProxyMsgGetConceptInfo);
		    System.out.println(">>>"+str);
		    commBus.updateConceptValues(str);
	*/
    }
    /*
    else if (source==savestate && !freeze) {
      commBus.saveConceptState();
    }
    */
    else if (source==collection && !freeze) {
	commBus.openDocumentCollection();
    }
    else if (source==sendmail && !freeze) {
	//commBus.sendMail();
	commBus.showCacheList();
    }
    else if (source==calendar && !freeze) {
	commBus.viewCalendarInterface();
    }
    else if (source==images && !freeze) {
	if (commBus.getUseLoadImages()) {
	    commBus.setLoadImages(false);
	    String imagename=commBus.getGifsPath()+"/loadimagesbutton-n-d.gif";
	    images.setIcon(new ImageIcon(imagename,"Load Images"));
	    imagename=commBus.getGifsPath()+"/loadimagesbutton-n-d-r.gif";
	    images.setRolloverIcon(new ImageIcon(imagename,""));
	}
	else {
	    commBus.setLoadImages(true);
	    String imagename=commBus.getGifsPath()+"/loadimagesbutton-n.gif";
	    images.setIcon(new ImageIcon(imagename,"Load Images"));
	    imagename=commBus.getGifsPath()+"/loadimagesbutton-n-r.gif";
	    images.setRolloverIcon(new ImageIcon(imagename,""));
	}
	//commBus.showCacheList();
    }
    else if (source==navigate && !freeze) {
	toggleMode();
    }
    /*
      else if (source==anohNavigate && !freeze) {
      commBus.setAnnotateMode();
      commBus.requestProxyContent(RH_GlobalVars.piaProxyMsgAnnotationOn);
      }
    */
    //lastButton=button;
  }

  public Insets getInsets() {
    return new Insets(top,left,bottom,right);
  }
  public void setInsets (int newtop, int newleft, int newbottom, int newright) {
    top=newtop; bottom=newbottom;
    left=newleft; right=newright;
    getInsets();
  }

  public void URL_Process(String url) {
    commBus.URL_Process(url);
  }
  public String getCurrentURL() {
    return commBus.getCurrentURL();
  }

  private void buildConstraints (GridBagConstraints constraints, int gx, int gy, int gw, int gh, int wx, int wy) {
    constraints.gridx = gx;
    constraints.gridy = gy;
    constraints.gridwidth = gw;
    constraints.gridheight = gh;
    constraints.weightx = wx;
    constraints.weighty = wy;
  }

  public void loadingNewDocument() {
    if (mainLogo!=null && animate) mainLogo.startLogo();
    freeze=true;
    stop.setEnabled(true);
    anohSimilar.setEnabled(false);
    anohSummary.setEnabled(false); //?? 02-27-98
    deactivateButtons();
  }
  public void setToDone() {
    if (mainLogo!=null && animate)  mainLogo.stopLogo();
    activateButtons();
    freeze=false;
    images.setToolTipText("Browsed:"+commBus.getDocumentsBrowsed());
    stop.setEnabled(false);
  }

  /**
   * Deactivate all buttons during processing of a document
   */
  public void deactivateButtons() {
    navigate.setRolloverEnabled(false);
    back.setRolloverEnabled(false);
    fwd.setRolloverEnabled(false);
    home.setRolloverEnabled(false);
    reload.setRolloverEnabled(false);
    //anohNavigate.setRolloverEnabled(false);
    //anohBack.setRolloverEnabled(false);
    //anohFwd.setRolloverEnabled(false);
    //anohReload.setRolloverEnabled(false);
    anohSummary.setRolloverEnabled(false);
    anohSimilar.setRolloverEnabled(false);
    //anohProfile.setRolloverEnabled(false);
    collection.setRolloverEnabled(false);
    sendmail.setRolloverEnabled(false);
    print.setRolloverEnabled(false);
    calendar.setRolloverEnabled(false);
    search.setRolloverEnabled(false);
    cache.setRolloverEnabled(false);
    images.setRolloverEnabled(false);
    //savestate.setRolloverEnabled(false);
    //repaint();
  }
  public void activateButtons() {
    navigate.setRolloverEnabled(true);
    back.setRolloverEnabled(true);
    fwd.setRolloverEnabled(true);
    home.setRolloverEnabled(true);
    reload.setRolloverEnabled(true);
    //anohNavigate.setRolloverEnabled(true);
    //anohBack.setRolloverEnabled(true);
    //anohFwd.setRolloverEnabled(true);
    //anohReload.setRolloverEnabled(true);
    anohSummary.setRolloverEnabled(true);
    anohSimilar.setRolloverEnabled(true);
    //anohProfile.setRolloverEnabled(true);
    collection.setRolloverEnabled(true);
    sendmail.setRolloverEnabled(true);
    print.setRolloverEnabled(true);
    calendar.setRolloverEnabled(true);
    search.setRolloverEnabled(true);
    cache.setRolloverEnabled(true);
    images.setRolloverEnabled(true);
    //savestate.setRolloverEnabled(true);
    //repaint();
  }

  public void stop() {
    if (animate) mainLogo.stopLogo();
    //activateButtons();
    freeze=false;
  }
  /*
  public void paint(Graphics gc) {
    Dimension size = getSize();
    gc.setColor(shadowColor);
    gc.drawLine(1,1,size.width,1); 
    gc.drawLine(1,1,1,size.height); 

    gc.setColor(Color.white);  // use white here because this line will always be next to a lightGray area
    gc.drawLine(size.width-1,1,size.width-1,size.height-1); 
    //gc.setColor(highlightColor);
    gc.drawLine(1,size.height-1,size.width-1,size.height-1); 
    
  }
  */
  public void modeChange(int id) {
    if (id==navigateID) {
      //activatePlainText();
      //navPanel.setActiveState(true);
      //anohPanel.setActiveState(false);
    }
  }
  public void activatePlainText() {
      String path=null, imageName=null;
          System.out.println("---PlainText:"+plainText);
      if (!plainText) {
	  path=commBus.getGifsPath()+"/";
	  imageName=path+"navlabel-n.gif";
	  navigate.setIcon(new ImageIcon(imageName,""));
	  plainText=true;
      }
      navigate.setEnabled(true);
      //imageName=path+"anohlabel-n-d.gif";
      //anohNavigate.setIcon(new ImageIcon(imageName,""));
      //anohNavigate.setEnabled(true);
  }
  public void activateAnnotation() {
      String path=null, imageName=null;
      if (plainText) {
	  path=commBus.getGifsPath()+"/";
	  imageName=path+"navlabel-n-d.gif";
	  navigate.setIcon(new ImageIcon(imageName,""));
	  plainText=false;
      }
      navigate.setEnabled(true);
  }

  public void toggleMode() {
      String path=null, imageName=null;
      System.out.println("---PlainText:"+plainText);
      if (plainText) {
	  path=commBus.getGifsPath()+"/";
	  imageName=path+"navlabel-n-d.gif";
	  navigate.setIcon(new ImageIcon(imageName,""));
	  plainText=false;
	  commBus.setAnnotateMode();
	  commBus.requestProxyContent(RH_GlobalVars.piaProxyMsgAnnotationOn);
      }
      else {
	  path=commBus.getGifsPath()+"/";
	  imageName=path+"navlabel-n.gif";
	  navigate.setIcon(new ImageIcon(imageName,""));
	  plainText=true;
	  commBus.setPlainTextMode();
	  commBus.requestProxyContent(RH_GlobalVars.piaProxyMsgAnnotationOff);
      }
      navigate.setEnabled(true);
  }

  public void noMoreBackDocuments() {
    back.setEnabled(false);
    //anohBack.setEnabled(false);
  }
  public void noMoreFwdDocuments() {
    fwd.setEnabled(false);
    //anohFwd.setEnabled(false);
  }
  public void moreBackDocuments() {
    back.setEnabled(true);
    //    anohBack.setEnabled(true);
  }
  public void moreFwdDocuments() {
    fwd.setEnabled(true);
    //anohFwd.setEnabled(true);
  }
  public void noSummaryAvailable() {
    anohSummary.setEnabled(false);
    anohSimilar.setEnabled(false);
  }

  public void summaryAvailable(boolean set) {
    anohSummary.setEnabled(set);
    anohSimilar.setEnabled(set);
  }
    
  public void setCalendarButton(boolean set) {
    calendar.setEnabled(set);
  }
  public void setSimilarButton(boolean set) {
    anohSimilar.setEnabled(set);
  }

  public void setMemoryToolTip() {
      int size=commBus.getCurrentCacheSize(), total=commBus.getDocumentCacheSize();
      String str=new String("Cache:"+size+"/"+total);
      cache.setToolTipText(str);
  }

  public void cacheEmpty() {
    /*
    StringBuffer str=new StringBuffer().append("Clear Cache - Size=").append(commBus.getDocCacheSize()).append(" of ")
      .append(commBus.getDocumentCacheSize());
    cache.setToolTipText(str.toString());
    */
    cache.setEnabled(false);
  }
  public void cacheNotEmpty() {
    cache.setEnabled(true);
    /*
    StringBuffer str=new StringBuffer().append("Clear Cache - Size=").append(commBus.getDocCacheSize()).append(" of ")
      .append(commBus.getDocumentCacheSize());
    cache.setToolTipText(str.toString());
    */
  }
  public void loadingImages(boolean set) {
    images.setEnabled(set);
  }


}

