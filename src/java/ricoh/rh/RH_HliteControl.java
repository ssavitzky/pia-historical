/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * RH_ModeControl Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 4.24.97 - revised 02-25-98, was called RH_ModeControl
 *
 */
package ricoh.rh;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.BorderFactory;

class RH_HliteControl extends JPanel implements ActionListener {
  public RH_CommBus commBus;

  private Color myGray=new Color(160,160,164), armedBackColor=myGray; //new Color(128,0,0);
  private boolean useSetButton=true, annotateMode=false;
  private int viewFrame_w=50, viewFrame_h=36, viewFrame_x=0, viewFrame_y=0, right_inset=4, left_inset=3,
    height=0, width=0, fontSize=10, lastState=0, numButtons=0;
  public JLabel label;
  public JButton one,two,three,lastButton;
  private Color backColor=Color.gray, textColor=Color.white, highlightColor=Color.white, shadowColor=Color.black,
    shadowColor2=Color.gray, labelBackColor=Color.lightGray, labelTextColor=Color.black, labelColorOn=Color.red,
    labelColorOff=Color.white, labelColorDisabled=Color.lightGray, labelColorBkgdOn=Color.yellow.darker(),
       labelColorBkgdOff=Color.gray;
  private String fontName="Arial", currentMode="Mode:";
  private int top=5,bottom=4,left=2,right=2;
  // Label Strings
  private String plainLabel = RH_GlobalVars.plainLabel, annotateLabel = RH_GlobalVars.annotateLabel,
    summaryLabel = RH_GlobalVars.summaryLabel,  groupLabel=RH_GlobalVars.groupLabel, iconFile="/hliteroff.gif", 
    iconFileIndented="/hliter.gif", smallredball="/smallredball.gif", smallwhiteball="/smallwhiteball.gif";
  private Font font;
  private ImageIcon smallwhiteballIcon=null;
    //** this is the currently selected style
    private int currentHliteStyle=0; 

  RH_HliteControl(RH_CommBus bus, int w, int h) {
    commBus=bus;
    setDoubleBuffered(true);
    commBus.hliteControl=this;
    labelBackColor=commBus.getModeBackColor();
    labelTextColor=commBus.getModeTextColor();
    backColor=commBus.getMainBackColor();
    textColor=commBus.getMainTextColor();
    highlightColor=commBus.getMainHighlightColor();
    shadowColor=commBus.getMainShadowColor();
    shadowColor2=commBus.getMainShadowColor2();
    fontName=commBus.getLocationFontName();
    fontSize=commBus.getModeFontSize();
    int motif=commBus.getMotifNumber();

    int button_w=24, button_h=14;

    if (motif==1) {
      labelBackColor=backColor;
      labelTextColor=Color.yellow;
    }
    else {
      labelBackColor=backColor;
      labelTextColor=Color.blue;
    }

    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    setLayout(new GridLayout(1,4,2,2)); //setLayout(gbl);

    ImageIcon icon=new ImageIcon(commBus.getGifsPath()+iconFile);
    label=new JLabel("",icon,JLabel.LEFT);
    label.setToolTipText("Highlighting Styles");
    //label.setBorder(BorderFactory.createEtchedBorder(Color.darkGray,Color.lightGray)); 
    add(label);

    smallwhiteballIcon=new ImageIcon(commBus.getGifsPath()+smallwhiteball);
    Font font=new Font("MS Sans Serif",Font.PLAIN,9);
    one=new JButton("1",smallwhiteballIcon);
    one.setHorizontalAlignment(AbstractButton.RIGHT);
    one.setDoubleBuffered(true);
    one.setBorderPainted(true);
    one.setFont(font);
    one.setOpaque(true);
    one.setFocusPainted(false);
    one.setForeground(labelColorDisabled);
    one.setBackground(backColor);
    one.setToolTipText(commBus.getHighlightStyleToolTip(0));
    //one.setMaximumSize(new Dimension(icon.getIconWidth(),icon.getIconHeight()));
    one.setMaximumSize(new Dimension(button_w,button_h));
    one.setMargin(new Insets(0,0,0,0));
    one.addActionListener(this);
    add(one);

    two=new JButton("2",smallwhiteballIcon);
    two.setHorizontalAlignment(AbstractButton.RIGHT);
    two.setDoubleBuffered(true);
    two.setBorderPainted(true);
    two.setOpaque(true);
    two.setFont(font);
    two.setFocusPainted(false);
    two.setBackground(backColor);
    two.setForeground(labelColorDisabled);
    two.setToolTipText(commBus.getHighlightStyleToolTip(1));
    //two.setMaximumSize(new Dimension(10,10));setMaximumSize(new Dimension(icon.getIconWidth(),icon.getIconHeight()));
    two.setMaximumSize(new Dimension(button_w,button_h));
    two.setMargin(new Insets(0,0,0,0));
    two.addActionListener(this);
    add(two);

    three=new JButton("3",smallwhiteballIcon);
    three.setHorizontalAlignment(AbstractButton.RIGHT);
    three.setDoubleBuffered(true);
    three.setBorderPainted(true);
    three.setOpaque(true);
    three.setFont(font);
    three.setFocusPainted(false);
    three.setBackground(backColor);
    three.setForeground(labelColorDisabled);
    three.setToolTipText(commBus.getHighlightStyleToolTip(2));
    //three.setMaximumSize(new Dimension(10,10));setMaximumSize(new Dimension(icon.getIconWidth(),icon.getIconHeight()));
    three.setMaximumSize(new Dimension(button_w,button_h));
    three.setMargin(new Insets(0,0,0,0));
    three.addActionListener(this);
    add(three);

    gbc.fill = GridBagConstraints.BOTH;
    gbc.anchor = GridBagConstraints.WEST;
    buildConstraints(gbc,0,0,1,1,40,100);
    gbl.setConstraints(label,gbc);
    
    gbc.anchor = GridBagConstraints.EAST;
    buildConstraints(gbc,1,0,1,1,15,100);
    gbl.setConstraints(one,gbc);
    buildConstraints(gbc,2,0,1,1,15,100);
    gbl.setConstraints(two,gbc);
    buildConstraints(gbc,3,0,1,1,15,100);
    gbl.setConstraints(three,gbc);
    //buildConstraints(gbc,4,0,1,1,15,100);
      //gbl.setConstraints(four,gbc);
    
      annotateMode=false;
    currentMode=plainLabel;
    //setBorder(BorderFactory.createBevelBorder(0,Color.black,Color.lightGray));
    setBorder(BorderFactory.createEtchedBorder(Color.lightGray,Color.darkGray)); 
    width=w; height=20;
    setSize(width,height);
    getPreferredSize();
    setBackground(backColor);
    setForeground(textColor);
    setInsets(top,left,bottom,right);
    setVisible(true);
  }
  public void refresh() {
    invalidate();
    validate();
    if (getGraphics()!=null) paint(getGraphics());
  }

  public void loadingNewDocument() {
    deactivateButtons();
  }
  public void setToDone() {
    activateButtons();
    repaint();
  }
  private void deactivateButtons() {
  //for (int i=0;i<numButtons; i++) highlightButtons[i].deactivate();
  }
  private void activateButtons() {
    //for (int i=0;i<numButtons; i++) highlightButtons[i].activate();
  }

  public void setModeLabel (String label) {
    //modeLabel.setText("Mode: "+label);
    //modeLabel.setText(label);
    //currentMode=label;
    //update(getGraphics());
  }

  private void buildConstraints (GridBagConstraints constraints, int gx, int gy, int gw, int gh, int wx, int wy) {
    constraints.gridx = gx;
    constraints.gridy = gy;
    constraints.gridwidth = gw;
    constraints.gridheight = gh;
    constraints.weightx = wx;
    constraints.weighty = wy;
  }

  public void actionPerformed(ActionEvent evt) {  
    Object source = evt.getSource();  
    if (annotateMode) {
      commBus.setWaitCursor();
      commBus.parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      if (source==one && lastButton!=source) {
	commBus.changeColorScheme(0);
	currentHliteStyle=0;
	
	if (lastButton!=null) {
	  lastButton.setIcon(smallwhiteballIcon);
	  lastButton.setForeground(labelColorOff);
	  lastButton.setBackground(labelColorBkgdOff);
	}
	one.setForeground(labelColorOn);
	one.setBackground(labelColorBkgdOn);
	one.setIcon(new ImageIcon(commBus.getGifsPath()+smallredball));
	lastButton=one;
      }
      else if (source==two && lastButton!=source) {
	commBus.changeColorScheme(1);
	currentHliteStyle=1;
	//if (lastButton!=null) lastButton.setIcon(new ImageIcon(commBus.getGifsPath()+iconFile));
	//two.setIcon(new ImageIcon(commBus.getGifsPath()+iconFileIndented));
	if (lastButton!=null) {
	  lastButton.setIcon(smallwhiteballIcon);
	  lastButton.setForeground(labelColorOff);
	  lastButton.setBackground(labelColorBkgdOff);
	}
	two.setForeground(labelColorOn);
	two.setBackground(labelColorBkgdOn);
	two.setIcon(new ImageIcon(commBus.getGifsPath()+smallredball));
	lastButton=two;
      }
      else if (source==three && lastButton!=source) {
	commBus.changeColorScheme(2);
	currentHliteStyle=2;
	//if (lastButton!=null) lastButton.setIcon(new ImageIcon(commBus.getGifsPath()+iconFile));
	//three.setIcon(new ImageIcon(commBus.getGifsPath()+iconFileIndented));
	if (lastButton!=null) {
	  lastButton.setIcon(smallwhiteballIcon);
	  lastButton.setForeground(labelColorOff);
	  lastButton.setBackground(labelColorBkgdOff);
	}
	three.setForeground(labelColorOn);
	three.setBackground(labelColorBkgdOn);
	three.setIcon(new ImageIcon(commBus.getGifsPath()+smallredball));
	lastButton=three;
      }
      /*
	else if (source==four && lastButton!=source) {
	commBus.changeColorScheme(3);
	currentHliteStyle=3;
	//if (lastButton!=null) lastButton.setIcon(new ImageIcon(commBus.getGifsPath()+iconFile));
	//four.setIcon(new ImageIcon(commBus.getGifsPath()+iconFileIndented));
	if (lastButton!=null) {
	lastButton.setIcon(smallwhiteballIcon);
	lastButton.setForeground(labelColorOff);
	lastButton.setBackground(labelColorBkgdOff);
	}
	four.setForeground(labelColorOn);
	four.setBackground(labelColorBkgdOn);
	four.setIcon(new ImageIcon(commBus.getGifsPath()+smallredball));
	lastButton=four;
	}
      */
      repaint(); //paint(getGraphics());
      setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
      commBus.setDefaultCursor();
    }
  }

  public Insets getInsets() {
    return new Insets(top,left,bottom,right);
  }
  public void setInsets (int newtop, int newleft, int newbottom, int newright) {
    top=newtop; bottom=newbottom;
    left=newleft; right=newright;
    getInsets();
  }

  public void setHighlightControls(boolean set) {
    if (set) {
      annotateMode=true;
      label.setIcon(new ImageIcon(commBus.getGifsPath()+iconFileIndented));
      //** Setup for first time
      int style=commBus.getDefaultHliteStyle();
      if (style==0) one.doClick();
      else if (style==1) two.doClick();
      else if (style==2) three.doClick();
      //else if (style==3) four.doClick();
      //** Added 1.5.99 because jdk 1.2 got a null pointer after annotation
      repaint(); //if (getGraphics()!=null) paint(getGraphics());
    }
    else {
      annotateMode=false;
      //lastButton.setIcon(new ImageIcon(""));
      label.setIcon(new ImageIcon(commBus.getGifsPath()+iconFile));
      one.setForeground(labelColorDisabled);
      one.setBackground(labelColorBkgdOff);
      one.setIcon(smallwhiteballIcon);
      two.setForeground(labelColorDisabled);
      two.setBackground(labelColorBkgdOff);
      two.setIcon(smallwhiteballIcon);
      three.setForeground(labelColorDisabled);
      three.setBackground(labelColorBkgdOff);
      three.setIcon(smallwhiteballIcon);
      //four.setForeground(labelColorDisabled);
      //four.setBackground(labelColorBkgdOff);
      //four.setIcon(smallwhiteballIcon);
      lastButton=null;
      repaint(); //paint(getGraphics());
    }
  }
    public int getCurrentHliteStyle() {
	return currentHliteStyle;
    }
}
