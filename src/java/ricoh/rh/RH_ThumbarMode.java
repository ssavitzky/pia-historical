/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 *  Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 4.24.97 - revised 02-11-98
 *
 */
package ricoh.rh;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class RH_ThumbarMode extends Panel {
  private RH_ThumbarControl parent;
  public RH_CommBus commBus;

  private int docview_w,docview_h, offset_horz,offset_vert,left_offset, button_w=14, 
    top=5,bottom=5,left=5,right=5;
  private Color documentColor = new Color(245,247,248),offGray=new Color(160,160,164);
  private Color  backColor=Color.gray, textColor=Color.white, highlightColor=Color.white, shadowColor=Color.black,
      shadowColor2=Color.gray;
  public Color buttonColor=Color.darkGray; //new Color(26,137,147);
  private int mode_w=0, mode_h=0, lastState, numButtons=0;
  private String modeFont="Arial";
  private RH_ThumbarNavButton[] navButtons;
  private RH_ThumbarNavButton lastButton;
    private String switchSet="switchon", switchNotSet="switchoff";
    private String stillLoading="stillloading", doneLoading="doneloading";
  
  RH_ThumbarMode(RH_ThumbarControl docview,int h, int docviewSize) {
    parent=docview;
    commBus=parent.commBus;
    backColor=commBus.getMainBackColor();
    textColor=commBus.getMainTextColor();
    highlightColor=commBus.getMainHighlightColor();
    shadowColor=commBus.getMainShadowColor();
    shadowColor2=commBus.getMainShadowColor2();
    int motif=commBus.getMotifNumber();
    mode_h=h;

    buttonColor=Color.lightGray; //backColor;
    Dimension size     = parent.getSize();
    setSize(size.width-offset_horz-25,mode_h);
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    setLayout(gbl);

    // Determine the size of docview
    navButtons=new RH_ThumbarNavButton[4];

    int i=0;
    gbc.gridy=3;
    gbc.fill = GridBagConstraints.NONE;
    //gbc.insets = new Insets(3,10,0,10);
    gbc.insets=new Insets(1,1,0,1);

    navButtons[i]=new RH_ThumbarNavButton(i,"navtop",buttonColor,backColor, this,true); 
    add(navButtons[i]);
    gbc.anchor=GridBagConstraints.WEST;
    gbc.fill=GridBagConstraints.BOTH;
    gbc.insets = new Insets(1,1,0,1);
    gbc.gridx=i; gbl.setConstraints(navButtons[i++],gbc);

    //Panel panel=new Panel();
    //add(panel);
    //gbc.anchor=GridBagConstraints.CENTER;
    //gbc.fill=GridBagConstraints.NONE;
    //gbc.insets=new Insets(0,0,0,0);
    //gbc.gridx=i; gbl.setConstraints(panel,gbc);

    navButtons[i]=new RH_ThumbarNavButton(i,doneLoading,buttonColor,backColor, this,true); 
    add(navButtons[i]);
    gbc.anchor=GridBagConstraints.EAST;
    gbc.insets = new Insets(1,18,0,0);
    gbc.gridx=i; gbl.setConstraints(navButtons[i++],gbc);

    navButtons[i]=new RH_ThumbarNavButton(i,switchNotSet,buttonColor,backColor, this,true,20,12); 
    add(navButtons[i]);
    gbc.anchor=GridBagConstraints.WEST;
    gbc.insets = new Insets(1,0,0,14);
    gbc.gridx=i; gbl.setConstraints(navButtons[i++],gbc);

    navButtons[i]=new RH_ThumbarNavButton(i,"navbottom", buttonColor,backColor, this,false); 
    add(navButtons[i]);
    gbc.anchor=GridBagConstraints.EAST;
    gbc.insets=new Insets(1,1,0,1);
    gbc.gridx=i; gbl.setConstraints(navButtons[i++],gbc);

    setInsets(5,2,5,2);
    lastButton=navButtons[0];lastState=0;
    setBackground(backColor);
  }
  public void modeSelected(int newmode, RH_ThumbarNavButton button) {
    //System.out.println("NavMode: "+newmode);
    if (newmode == 0) parent.navModeToTop();
    else if (newmode == 2) 
	if (parent.switchImageScalingMethod()) navButtons[newmode].setImageFilename(switchSet);
	else navButtons[newmode].setImageFilename(switchNotSet);
    //else if (newmode == 2) parent.navModeScreenDown();
    else if (newmode == 3) parent.navModeToBottom();
  }
  public Insets getInsets() {
    return new Insets(top,left,bottom,right);
  }
  public void setInsets (int newtop, int newleft, int newbottom, int newright) {
    top=newtop; bottom=newbottom;
    left=newleft; right=newright;
    getInsets();
  }

  public void paint(Graphics gc) {
    Dimension size = getSize();
    int modeLabel_w=size.width-5, modeLabel_h=size.height-6;
    int button_h=15, button_w=18, start_x=2, start_y=8, bar_w=(button_w*4)+2;

    // top line
    //gc.setColor(highlightColor);
    //gc.drawLine(0,0,size.width,0); 

    // Draw the bar above the buttons
    start_y=1;
    gc.setColor(highlightColor);
    //gc.drawLine(size.width-1,0,size.width-1,size.height-1);
    gc.drawLine(offset_horz,0,size.width,0);
    //gc.setColor(Color.white);
    //gc.drawLine(0,0,0,size.height);

    // bottom line - connecting to overview
    //gc.setColor(Color.red);
    //gc.drawLine(0,size.height,size.width,size.height);

  }

    public void setThumbarStatusStart() {
	if (navButtons!=null && navButtons[1]!=null) navButtons[1].setImageFilename(stillLoading);
    }
    public void setThumbarStatusDone() {
	if (navButtons!=null && navButtons[1]!=null) navButtons[1].setImageFilename(doneLoading);
    }

}
