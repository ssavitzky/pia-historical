/** 
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 *  Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 5.30.97 -- revised 02-24-98
 *
 */
package ricoh.rh;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.BorderFactory;

import jclass.bwt.JCSlider;
import jclass.bwt.JCLabel;
import jclass.bwt.JCAdjustmentEvent;
import jclass.bwt.JCAdjustmentListener;

class RH_ConceptControl extends JPanel implements ActionListener, JCAdjustmentListener, MouseListener, ChangeListener {
  public RH_CommBus commBus;
  public RH_MainFrame mainFrame;
  public RH_HliteControl hliteControl;
  
  private int width, height;
  private Panel sensPanel;
  private JPanel topicsPanel, groupsPanel, conceptsPanel, controlsPanel, sliderpanel, portalPanel, contourPanel;
  //private Image logo;
  private int topicsHeight = 285;
  private int MaxConcepts=20, numMeters=0, numGroups=0;
  public JButton[] concepts;
  public JLabel[] labels;
  public JRadioButton[] groupButtons;
  public JRadioButton lastGroup=null;
  public RH_Slider slider;
  public JLabel senslabel;
    //public RH_WebPortal webPortal;
    public RH_FurtherReading furtherReading;

  private int meter_w=50, meter_h=18, gridmeter_w=27, gridmeter_h=27, maxSensitivity=90;
  private Color defaultMeterColor=Color.red, meterTextColor=Color.white,
    backColor=Color.gray, textColor=Color.white, locationBackColor=Color.lightGray, locationTextColor=Color.black,
    shadowColor=Color.black, shadowColor2=Color.gray, highlightColor=Color.white,
    titleColor=Color.lightGray, labelColor=Color.white, labelColorOff=Color.lightGray, groupLabelColor=labelColor,
    groupLabelColorOff=labelColorOff;
  private int fontSize = 11, meterFontSize=11, durationFontSize=10;  // default size
  private String fontName = "TimesRoman", meterFontName="Sans Serif", anohFlagIcon="", gifext=".gif", gifpath="", extraFileFlag="",
    ballcolor="/smallyellowball", balloffcolor="/smallwhiteball";
  private int meterFontTitleType=Font.BOLD, meterFontType=Font.PLAIN;
  private Button updateButton;
  //private JCCheckbox colorScheme1, colorScheme2, colorScheme3, colorScheme4, colorScheme5;
  private GridBagLayout metergbl;
  private GridBagConstraints metergbc;
  private int top=4,left=1,bottom=2,right=1, selectedGroup=0;
  private boolean useLargeMeters=true;
  
  public RH_ConceptControl (RH_CommBus bus,int new_w, int h) {
    super();
    setDoubleBuffered(true);
    commBus=bus;
    mainFrame=commBus.mainFrame;
    String newline=mainFrame.getNewlineByte();

    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    setLayout(gbl);

    backColor=commBus.getMainBackColor();
    textColor=commBus.getMainTextColor();
    highlightColor=commBus.getMainHighlightColor();
    shadowColor=commBus.getMainShadowColor();
    shadowColor2=commBus.getMainShadowColor2();
    useLargeMeters=commBus.getLargeMeters();
    groupLabelColor=labelColor=commBus.getModeTextColor();
    if (useLargeMeters) {
      extraFileFlag="";
      //width=w; height=h;  i'm controling this from here
      width=110;
    }
    else {
      extraFileFlag="-s";
      width=40;
    }
    Font titlefont=new Font("Sans Serif",Font.BOLD,11);

    conceptsPanel=new JPanel();
    conceptsPanel.setBackground(backColor);
    conceptsPanel.setDoubleBuffered(true);
    conceptsPanel.setLayout(new BorderLayout()); 
    conceptsPanel.setMaximumSize(new Dimension(width,60));

    groupsPanel=new JPanel();
    groupsPanel.setBackground(backColor);
    groupsPanel.setDoubleBuffered(true);
    groupsPanel.setFont(new Font("MS Sans Serif",Font.PLAIN,10));
    groupsPanel.setBorder(new TitledBorder(new TitledBorder(""),"Groups",TitledBorder.LEFT,TitledBorder.TOP,titlefont,titleColor));

    RH_ConceptGroup[] groups=commBus.getConceptGroups();
    //System.out.println("***:::GROUPS:"+groups);
    numGroups=groups.length;
    if (numGroups<=2) groupsPanel.setLayout(new GridLayout(1,2)); 
    else if (numGroups<=4) groupsPanel.setLayout(new GridLayout(2,2)); 
    else if (numGroups<=6) groupsPanel.setLayout(new GridLayout(3,2)); 
    else groupsPanel.setLayout(new GridLayout(4,2)); 

    groupButtons=new JRadioButton[numGroups];
    int k=0;
    for (k=0;k<numGroups;k++) {
      groupButtons[k]=new JRadioButton(groups[k].getName());
      groupButtons[k].setBorderPainted(false);
      groupButtons[k].setBackground(backColor);
      groupButtons[k].setDoubleBuffered(true);
      if (k==numGroups-1) groupButtons[k].setFont(new Font("MS Sans Serif",Font.ITALIC,10));
      else groupButtons[k].setFont(new Font("MS Sans Serif",Font.BOLD,10));
      groupButtons[k].setForeground(groupLabelColorOff);
      groupButtons[k].setFocusPainted(false);
      //groupButtons[k].setBackground(backColor);
      groupButtons[k].setToolTipText(groups[k].getToolTipString());
      groupButtons[k].addActionListener(this);
      groupsPanel.add(groupButtons[k]);
    }
    groupButtons[k-1].setEnabled(false);  // disable the retro group
    
    topicsPanel=new JPanel();
    topicsPanel.setDoubleBuffered(true);
    //GridBagLayout conceptsgbl = new GridBagLayout();
    //GridBagConstraints conceptsgbc = new GridBagConstraints();
    //topicsPanel.setLayout(conceptsgbl);
    //conceptsgbc.insets = new Insets(1,0,1,0);
    topicsPanel.setFont(new Font("MS Sans Serif",Font.PLAIN,10));
    topicsPanel.setBackground(backColor);
    topicsPanel.setSize(width,0);
    topicsPanel.setBorder(new TitledBorder(new TitledBorder(""),"Concepts",TitledBorder.LEFT,TitledBorder.TOP,titlefont,titleColor));
    //topicsPanel.setSize(width-10,height-10);
    //topicsPanel.getPreferredSize();

    hliteControl=new RH_HliteControl(commBus,width,40);
    controlsPanel=new JPanel();
    controlsPanel.setDoubleBuffered(true);
    controlsPanel.setLayout(new BorderLayout()); 
    controlsPanel.setBackground(backColor);
    controlsPanel.setForeground(textColor);
    controlsPanel.setBorder(new TitledBorder(new TitledBorder(""),"Controls",TitledBorder.LEFT,TitledBorder.TOP,titlefont,titleColor));
    
    //** Make panel for holding controls: slider, highlighting options and duration label
    sliderpanel=new JPanel();
    sliderpanel.setDoubleBuffered(true);
    sliderpanel.setLayout(new BorderLayout()); 
    sliderpanel.setBackground(backColor);
    sliderpanel.setForeground(textColor);
    //sliderpanel.setBorder(new TitledBorder(new TitledBorder(""),"Controls",TitledBorder.LEFT,TitledBorder.TOP,titlefont,titleColor));

    senslabel=new JLabel(commBus.getSensitivitySetting()+"%",JLabel.LEFT);
    senslabel.setForeground(commBus.getModeTextColor());
    senslabel.setBackground(backColor);
    senslabel.setDoubleBuffered(true);
    senslabel.setFont(new Font("Sans Serif",Font.PLAIN,10));
    sliderpanel.add(senslabel,BorderLayout.WEST);

    slider = new RH_Slider(this,commBus,JSlider.HORIZONTAL, commBus.getSensitivitySetting(), 0, 10,width-20,26);
    slider.setFont(new Font("Sans Serif",Font.PLAIN,9));
    slider.setMaximum(maxSensitivity);
    slider.addChangeListener(this);
    slider.addMouseListener(this);
    slider.setMajorTickSpacing(10);
    slider.setMinorTickSpacing(5);
    slider.setPaintTicks(true);
    slider.setSnapToTicks(false);
    slider.setBackground(backColor);
    slider.setForeground(meterTextColor);
    slider.setBorder(new EmptyBorder(new Insets(0,0,0,0)));
    //slider.setExtent(10);
    slider.setOpaque(false);
    //slider.putClientProperty( "JSlider.isFilled", Boolean.FALSE);

    JLabel maxlabel=new JLabel(new StringBuffer().append(maxSensitivity).toString(),JLabel.RIGHT);
    maxlabel.setDoubleBuffered(true);
    maxlabel.setForeground(Color.white);
    maxlabel.setBackground(backColor);
    maxlabel.setFont(new Font("Sans Serif",Font.PLAIN,9));
    sliderpanel.add(maxlabel,BorderLayout.EAST);

    sliderpanel.add(slider,BorderLayout.CENTER);//,BorderLayout.CENTER);
    sliderpanel.setMaximumSize(new Dimension(width-15,30));

    controlsPanel.add(sliderpanel, BorderLayout.NORTH);
    controlsPanel.add(hliteControl,BorderLayout.SOUTH);
    controlsPanel.setMaximumSize(new Dimension(width-10,60));

    conceptsPanel.add(groupsPanel,BorderLayout.NORTH);
    conceptsPanel.add(topicsPanel,BorderLayout.CENTER);
    gbc.anchor = GridBagConstraints.NORTH;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    buildConstraints(gbc,0,0,1,1,100,60);
    gbl.setConstraints(conceptsPanel,gbc);
    add(conceptsPanel);

    //webPortal=new RH_WebPortal(this);
    //JPanel frPanel=new JPanel();
    //frPanel.setBackground(backColor);
    //frPanel.setForeground(textColor);
    //frPanel.setBorder(new TitledBorder(new TitledBorder(""),"FurtherReading",TitledBorder.LEFT,TitledBorder.TOP,titlefont,titleColor));
    //frPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED,Color.lightGray,Color.black),
    //			       "FurtherReading",TitledBorder.LEFT,TitledBorder.TOP,titlefont,titleColor));
    furtherReading=new RH_FurtherReading(commBus,backColor,titleColor,width,titlefont);
    //frPanel.add(furtherReading,BorderLayout.CENTER);
    gbc.anchor = GridBagConstraints.SOUTH;
    gbc.fill = GridBagConstraints.NONE;
    buildConstraints(gbc,0,1,1,1,100,10);
    gbl.setConstraints(furtherReading,gbc);
    System.out.println("***RH_ConceptControl: portalFlag="+commBus.getShowPortal());
    if (commBus.getShowPortal()) add(furtherReading);

    /*
    gbc.anchor = GridBagConstraints.NORTH;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    buildConstraints(gbc,0,1,1,1,100,80);
    gbl.setConstraints(topicsPanel,gbc);
    */

    gbc.anchor = GridBagConstraints.SOUTH;
    buildConstraints(gbc,0,2,1,1,100,10);
    gbl.setConstraints(controlsPanel,gbc);
    add(controlsPanel);

    //setBorder(BorderFactory.createEtchedBorder(shadowColor,highlightColor)); //Color.black,Color.lightGray));
    groupButtons[commBus.getDefaultGroup()].doClick();
    setBorder(BorderFactory.createBevelBorder(1,Color.black,Color.lightGray));
    setSize(width,height);
    setInsets(top,left,bottom,right);
    invalidate();
    setBackground(backColor);
    getPreferredSize();
    validate();
  }

  public void updateConcepts() {
    int val=0;
    //System.out.println("--->numConcepts:"+mainFrame.numConcepts+"  activeConcepts len:"+mainFrame.activeConcepts.length+
    //	   " concepts len:"+concepts.length);
    for (int i=0;i<mainFrame.numConcepts;i++) {
	//System.out.print(i+"> ");
      if (mainFrame.activeConcepts[i]!=null && mainFrame.activeConcepts[i].isActive()) {
	labels[i].setText(mainFrame.activeConcepts[i].getShortName() + " ("+mainFrame.activeConcepts[i].getValue()+"%)");
	val=mainFrame.activeConcepts[i].getIconValue();
	concepts[i].setIcon(new ImageIcon(gifpath+"/cm"+val+extraFileFlag+gifext));
	//System.out.println(mainFrame.activeConcepts[i].getShortName() + " ("+mainFrame.activeConcepts[i].getValue()+"%)");
      }
      //else System.out.println(i+" NADA");
    }
    //commBus.updateNumberPhrasesLabel();
  }

  private void changeGroups(int groupnum) {
    RH_ConceptGroup group=commBus.getGroup(groupnum);
    String[] groupConcepts=null;
    //System.out.println("***CHanging groups:"+groupnum+" group:"+group);
    if (group!=null && (groupConcepts=group.getConcepts())!=null) {
      invalidate();
      //System.out.println("***Making group:"+group.getName());
      topicsPanel.removeAll();
      GridBagLayout conceptsgbl = new GridBagLayout();
      GridBagConstraints conceptsgbc = new GridBagConstraints();
      topicsPanel.setLayout(conceptsgbl);
      conceptsgbc.insets = new Insets(1,0,1,0);

      concepts=new JButton[groupConcepts.length];
      labels=new JLabel[groupConcepts.length];
      gifpath=commBus.getGifsPath();
      String whichball="", whichicon="", whichvalue="";
      Color whichcolor=null;
      ImageIcon icon=null;
      mainFrame.numConcepts=groupConcepts.length;
      mainFrame.activeConcepts=new RH_Concept[mainFrame.numConcepts];

      for (int i=0; i<groupConcepts.length;i++) {
	mainFrame.activeConcepts[i]=mainFrame.findConcept(groupConcepts[i]);
	//System.out.println(i+">***Adding concept: "+mainFrame.activeConcepts[i].getName());
	if (mainFrame.activeConcepts[i]!=null) {
	  if (mainFrame.activeConcepts[i].isActive()) {
	      int val=mainFrame.activeConcepts[i].getIconValue();
	    whichball=ballcolor;
	    whichicon="/cm"+val+extraFileFlag;
	    whichcolor=labelColor;
	    whichvalue=" ("+mainFrame.activeConcepts[i].getValue()+"%)";
	  }
	  else {
	    whichball=balloffcolor;
	    whichicon="/cmoff"+extraFileFlag;
	    whichcolor=labelColorOff;
	    whichvalue=" (--)";
	  }
	  icon=new ImageIcon(gifpath+whichicon+gifext);
	  concepts[i]=new JButton(icon);
	  concepts[i].setDisabledIcon(new ImageIcon(gifpath+"/cmoff"+extraFileFlag+gifext));
	  concepts[i].setHorizontalAlignment(AbstractButton.RIGHT);
	  concepts[i].addActionListener(this); 
	  concepts[i].setBorderPainted(false);
	  concepts[i].setDoubleBuffered(true);
	  concepts[i].setFont(new Font("MS Sans Serif",Font.PLAIN,10));
	  concepts[i].setFocusPainted(false);
	  concepts[i].setBackground(backColor);
	  concepts[i].setToolTipText(mainFrame.activeConcepts[i].getName());
	  //concepts[i].setMaximumSize(new Dimension(icon.getIconWidth(),icon.getIconHeight()));
	  concepts[i].setMargin(new Insets(0,0,0,0));
	  if (useLargeMeters) 
	    labels[i]=new JLabel(mainFrame.activeConcepts[i].getShortName()+whichvalue,new ImageIcon(gifpath+whichball+gifext),JLabel.LEFT);
	  else
	    labels[i]=new JLabel(mainFrame.activeConcepts[i].getShortName()+whichvalue,JLabel.LEFT);
	  labels[i].setFont(new Font("MS Sans Serif",Font.PLAIN,10));
	  labels[i].setBackground(backColor);
	  labels[i].setOpaque(true);
	  labels[i].setDoubleBuffered(true);
	  labels[i].setForeground(whichcolor);
	  labels[i].setMaximumSize(new Dimension(width-20,icon.getIconHeight()));
	  conceptsgbc.anchor = GridBagConstraints.WEST;
	  conceptsgbc.fill = GridBagConstraints.HORIZONTAL;
	  buildConstraints(conceptsgbc,0,i,1,1,95,100);
	  conceptsgbl.setConstraints(labels[i],conceptsgbc);
	  topicsPanel.add(labels[i]);
	  conceptsgbc.anchor = GridBagConstraints.EAST;
	  buildConstraints(conceptsgbc,1,i,1,1,5,100);
	  conceptsgbl.setConstraints(concepts[i],conceptsgbc);
	  topicsPanel.add(concepts[i]);
	  concepts[i].revalidate();
	}
	else System.out.println("***Could not find concept ["+groupConcepts[i]+"] in changeGroup");
      }
      validate();
      //topicsPanel.validate();
      //hliteControl.refresh();
      repaint(); //paint(getGraphics());
    }
  }


  public void actionPerformed(ActionEvent evt) {  
    Object source = evt.getSource();  
    int i=0, val=0;
    //System.out.println("***Conceptgroups: action performed");
    groupsPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    topicsPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    while (i<numGroups && source!=groupButtons[i]) i++;
    if (i<numGroups) {
      //** note: it's ok for retro to be reselected
      if (lastGroup==null || lastGroup==groupButtons[groupButtons.length-1] || lastGroup!=source) {
	if (lastGroup!=null) {
	  lastGroup.setSelected(false);
	  lastGroup.setForeground(groupLabelColorOff);
	}
	groupButtons[i].setSelected(true);
	groupButtons[i].setForeground(groupLabelColor);
	changeGroups(i);
	lastGroup=groupButtons[i];
	selectedGroup=i;
      }
      return;
    }

    i=0;
    while (i<mainFrame.numConcepts && source!=concepts[i]) i++;
    if (i<mainFrame.numConcepts) {
      labels[i].setForeground(Color.black);
      if (mainFrame.activeConcepts[i].isActive()) {
	concepts[i].setIcon(new ImageIcon(gifpath+"/cmoff"+extraFileFlag+gifext));
	mainFrame.activeConcepts[i].setActive(false);
	labels[i].setForeground(labelColorOff);
	if (useLargeMeters) labels[i].setIcon(new ImageIcon(gifpath+balloffcolor+gifext));
	labels[i].revalidate();
	if (commBus.inAnnotateMode()) commBus.refreshDocument(false);
      }
      else {
	val=mainFrame.activeConcepts[i].getIconValue();
	concepts[i].setIcon(new ImageIcon(gifpath+"/cm"+val+extraFileFlag+gifext));
	mainFrame.activeConcepts[i].setActive(true);
	labels[i].setForeground(labelColor);
	if (useLargeMeters) labels[i].setIcon(new ImageIcon(gifpath+ballcolor+gifext));
	labels[i].revalidate();
	if (commBus.inAnnotateMode()) commBus.refreshDocument(false);
      }
      repaint();
    }
    groupsPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    topicsPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }


  public void setInsets (int newtop, int newleft, int newbottom, int newright) {
    top=newtop; bottom=newbottom;
    left=newleft; right=newright;
    getInsets();
  }
  public Insets getInsets() {
    return new Insets(top,left,bottom,right);
  }

  private void buildConstraints (GridBagConstraints constraints, int gx, int gy, int gw, int gh, int wx, int wy) {
    constraints.gridx = gx;
    constraints.gridy = gy;
    constraints.gridwidth = gw;
    constraints.gridheight = gh;
    constraints.weightx = wx;
    constraints.weighty = wy;
  }

  public void enableRetroGroup() {
    if (numGroups>0) groupButtons[numGroups-1].setEnabled(true);
  }

  public void disableRetroGroup() {
    if (numGroups>0) groupButtons[numGroups-1].setEnabled(false);
  }

  public void selectRetroGroup(String[] conceptNames) {
    if (numGroups>0) {
      enableRetroGroup();
      //System.out.println("***Setting up retro group:"+(numGroups-1));
      //** This sends the list of shortname concept names to the profile to update the mast copy of the concepts
      commBus.setGroupConcepts(numGroups-1,conceptNames);
      groupButtons[numGroups-1].doClick();
    }
  }

  public void setAnnotateMode(boolean set) {
    hliteControl.setHighlightControls(set);
  }

  public void selectDefaultGroup() {
    groupButtons[commBus.getDefaultGroup()].doClick();
    disableRetroGroup();
  }

  public int getCurrentSelectedGroup() {
    return selectedGroup;
  }

  public void adjustmentValueChanged(JCAdjustmentEvent ev) {
    //senslabel.setLabel(slider.getValue() + "%");
    senslabel.setText(slider.getValue() + "%");
  }

  public void mouseClicked(MouseEvent ev) {
  }
  public void mouseEntered(MouseEvent ev) {
  }
  public void mouseExited(MouseEvent ev) {
  }
  public void mousePressed(MouseEvent ev) {
  }
  public void mouseReleased(MouseEvent ev) {
    Object source=ev.getSource();
    commBus.setWaitCursor();
    slider.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    commBus.newSensitivityValue(slider.getValue());
    commBus.setDefaultCursor();
    slider.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }

  public void stateChanged(ChangeEvent e) {
    JSlider s1 = (JSlider)e.getSource();
    senslabel.setText(s1.getValue()+"%");
    /*
    commBus.setWaitCursor();
    slider.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    commBus.newSensitivityValue(slider.getValue());
    commBus.setDefaultCursor();
    slider.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    */
  }

  public void setWaitCursor() {
    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    topicsPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    groupsPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    controlsPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    sliderpanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    //slider.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    hliteControl.setCursor(new Cursor(Cursor.WAIT_CURSOR));
  }
  public void setDefaultCursor() {
    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    topicsPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    groupsPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    controlsPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    sliderpanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    //slider.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    hliteControl.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }


  /**
   * enables or disables the highlighting controls
   */
  public void setHighlightControls(boolean set) {
   hliteControl.setHighlightControls(set);
  }

    public void showLinkLocation(String link,Image img) {
	//webPortal.showLinkLocation(link,img);
    }

    public void setShowPortal(boolean show) {
	invalidate();
	System.out.println("*****Running setShowPortal...");
	if (show) {
	    //add(portalPanel);
	    //portalPanel.add(webPortal);
	    add(furtherReading,BorderLayout.CENTER);
	}
	else {
	    //portalPanel.remove(webPortal);
	    remove(furtherReading);
	    //remove(portalPanel);
	}
	validate();
	repaint();
    }

    public void updatePortalLoadProgress(int val) {
	//webPortal.updateProgress(val);
    }
    public void resetPortalProgress () {
	//webPortal.resetProgress();
    }
    
    public void updateFurtherReadingList(String key) {
	System.out.println("*****Updating FurtherReadingList...");
	StringBuffer keyfile=new StringBuffer().append(mainFrame.mainPath).append(mainFrame.privateANOHDir).append(mainFrame.rhPathSeparator).
	    append(mainFrame.rhDocumentDir).append(mainFrame.rhPathSeparator).append(key).append(mainFrame.rhPathSeparator).
	    append(mainFrame.rhSimFileName).append(mainFrame.rhSimilarExt);
	/*
	  File file=new File(keyfile.toString());
	  String newkey=null;
	  String[] names=null, sendnames=null;
	  int[] values=null, sendvalues=null;
	  Integer tmpval=null;
	  int i=0, idx=0;
	  Vector vec;
	  if (file.exists()) {
	  invalidate();
	  RH_DocumentLexicon doclex=new RH_DocumentLexicon(mainFrame);
	  Vector[] simArray=doclex.readSimFile(keyfile);
	  names=new String[simArray.length];
	  values=new int[simArray.length];
	  for (i=0, idx=0;i<simArray.length;i++) {
	  vec=(Vector)simArray[i];
	  newkey=(String)vec.elementAt(0);
	  //* do not store the element that matches the current document key
	      if (!key.equalsIgnoreCase(newkey)) {
	      names[idx]=newkey;
	      tmpval=(Integer)vec.elementAt(2);
	      values[idx++]=tmpval.intValue();
	      }
	      }
	      if (names.length>0) {
	      //** This part resizes the array since we eliminate the entry which matches the current document key
		   sendnames=new String[idx];
		   sendvalues=new int[idx];
		   System.arraycopy(names,0,sendnames,0,idx);
		   System.arraycopy(values,0,sendvalues,0,idx);
		   furtherReading.updateReadingList(sendnames, sendvalues);
		   if (idx>8) i=3;
		   else if (idx>5) i=2;
		   else i=1;
		   furtherReading.makeListAvailable(i); 
		   }
		   else System.out.println("#####****ERROR: could not update further reading list...");
		   validate();
		   repaint();
		   }
		   else furtherReading.makeListUnavailable();
		   }
	*/
    }
    public void resetFurtherReadingList() {
	furtherReading.reset();
    }
    
    public int getCurrentHliteStyle() {
	return hliteControl.getCurrentHliteStyle();
    }

    public void updateConceptGroups() {
	RH_ConceptGroup[] groups=commBus.getConceptGroups();
	if (groups!=null) {
	    numGroups=groups.length;
	    invalidate();
	    groupsPanel.removeAll();
	    if (numGroups<=2) groupsPanel.setLayout(new GridLayout(1,2)); 
	    else if (numGroups<=4) groupsPanel.setLayout(new GridLayout(2,2)); 
	    else if (numGroups<=6) groupsPanel.setLayout(new GridLayout(3,2)); 
	    else groupsPanel.setLayout(new GridLayout(4,2)); 
	    
	    groupButtons=new JRadioButton[numGroups];
	    int k=0;
	    for (k=0;k<numGroups;k++) {
		groupButtons[k]=new JRadioButton(groups[k].getName());
		groupButtons[k].setBorderPainted(false);
		groupButtons[k].setBackground(backColor);
		groupButtons[k].setDoubleBuffered(true);
		if (k==numGroups-1) groupButtons[k].setFont(new Font("MS Sans Serif",Font.ITALIC,10));
		else groupButtons[k].setFont(new Font("MS Sans Serif",Font.BOLD,10));
		groupButtons[k].setForeground(groupLabelColorOff);
		groupButtons[k].setFocusPainted(false);
		//groupButtons[k].setBackground(backColor);
		groupButtons[k].setToolTipText(groups[k].getToolTipString());
		groupButtons[k].addActionListener(this);
		groupsPanel.add(groupButtons[k]);
	    }
	    groupButtons[k-1].setEnabled(false);  // disable the retro group
	    groupButtons[commBus.getDefaultGroup()].doClick();
	    validate();
	    repaint();
	}
	else System.out.println("ERROR: no groups returned by profile!");
    }

    public void updateConceptInformation() {
	changeGroups(commBus.getDefaultGroup());
    }
}	
 
