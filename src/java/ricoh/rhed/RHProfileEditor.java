/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: RH_ProfileEditor: editor for editing the user's profile
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 8.4.97 -- revised 4-16-98
 *
 */
package ricoh.rhed;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

import javax.swing.border.*;
import javax.swing.BorderFactory;

import ricoh.rhpm.RHActiveConcept;
import ricoh.rhpm.RHSortableConcept;
import ricoh.rhpm.RHTopicKeyword;

import ricoh.rh.RH_GlobalVars;
import ricoh.rh.RH_LocationItem;
import ricoh.rh.RH_ConceptGroup;
import ricoh.rh.RH_HighlightStyle;
import ricoh.rh.RH_MeterStyle;
import ricoh.rh.RH_PopupMsg;
import ricoh.rh.RH_PopupError;
import ricoh.rh.RH_PopupTextField;
import ricoh.rh.RH_FileContents;

public class RHProfileEditor extends JFrame implements ActionListener, ListSelectionListener, ChangeListener {

    private JPanel editConceptsPanel, editGroups, options, conceptsPanel, editGroupsPanel, editOptionsPanel, editThumbarPanel,
	editHlitePanel, editPrintPanel;
    private JScrollPane conceptsScrollPane, topicsScrollPane, groupsScrollPane, groupConceptsScrollPane, groupAllConceptsScrollPane;
    private JTabbedPane tabbedPane;
    private JList concepts, groups, groupConcepts, groupAllConcepts,textfont, guifont;
    private String[] conceptData, topicsData, groupData, groupConceptsData, allConceptsData;
    private Font borderFont=null;
    private TitledBorder conceptBorder, topicsBorder, groupBorder, groupConceptsBorder, groupAllConceptsBorder;
    private RH_ConceptGroup currentGroup;

    private String foo=RH_GlobalVars.RH_ANOH_History_Symbol;
    
    private JButton ok, cancel, editTopic, addConcept, editConcept, removeConcept, delTopic, editConceptsSave, editConceptsNoSave, editConceptsCancel,
	editGroupsSave, editGroupsNoSave, editGroupsCancel, editOptionsSave, editOptionsNoSave, editOptionsCancel,
	editThumbarSave, editThumbarNoSave, editThumbarCancel, editPrintSave, editPrintNoSave, editPrintCancel,
	editHliteSave, editHliteNoSave, editHliteCancel,
	removeGroupConcept, addGroupConcept, addGroup, editGroup, removeGroup,
	backSquare, lensSquare, wlSquare, anohSquare, linkSquare, lensLineSquare, style1TextSquare, style1BackSquare,
	style2TextSquare,style2BackSquare, style3TextSquare, style3BackSquare;
    private int width=650,height=450,maxLensRatio=14, minLensRatio=3, cacheSizeMinimum=1000000;
    private int x=88, y=107, currentTopicPos=0, currentConceptPos=0, currentGroupPos=0, currentGroupConceptPos=0, currentGroupAllConceptsPos=0;
    private String conceptBorderTitle="Topics of Interest: ", topicBorderTitle="Keyword Phrases: ", groupBorderTitle="Groups: ", 
	groupConceptsBorderTitle="Current Concepts: ", groupAllConceptsBorderTitle="All Concepts: ";
    private RH_HighlightStyle[] hstyles;

    private JTextField shortNameField, currentTopic, groupToolTip, prior, readerFName, readerLName, readerUName,
	lensRatio, thumbarBackR_Color, thumbarBackG_Color, thumbarBackB_Color, thumbarLensR_Color, thumbarLensG_Color, thumbarLensB_Color,
	thumbarWLR_Color, thumbarWLG_Color, thumbarWLB_Color, thumbarANOHR_Color, thumbarANOHG_Color, thumbarANOHB_Color,
	thumbarLinkR_Color, thumbarLinkG_Color, thumbarLinkB_Color, thumbarLensLineR_Color, thumbarLensLineG_Color, thumbarLensLineB_Color,
	homepath, gifspath, homeurl, privatepath, windowwidth, windowheight, windowx, windowy, textfontsize, guifontsize, cachesize,
	sensThreshold, simThreshold, style1TextR_Color, style1TextG_Color, style1TextB_Color, style1BackR_Color,
	style1BackG_Color, style1BackB_Color, style1Tip, style2Tip, style3Tip,
	style2TextR_Color, style2TextG_Color, style2TextB_Color, style2BackR_Color, style2BackG_Color, style2BackB_Color,
	style3TextR_Color, style3TextG_Color, style3TextB_Color, style3BackR_Color, style3BackG_Color, style3BackB_Color,
	piaHost, piaPort, piaAgent, piaPrinter;
    private JLabel numberTopicsField;
    private JCheckBox activeField, defaultGroup, useLensLogo, activecache, animateLogo, autoloadHome, populateConcepts, imageScaling,
	useAnohDoubleLine, useLinkDoubleLine, largemeters,
	style1Bold,  style1Shadow, style1Whole, style1Def, 
	style2Bold,  style2Shadow, style2Whole, style2Def, 
	style3Bold,  style3Shadow, style3Whole, style3Def;
    private JRadioButton style1Under,style2Under,style3Under, style1Box, style2Box, style3Box;
    private JList topicsList;
    private Color backColor=Color.gray, topicsTitleColor=Color.yellow, conceptsColor=Color.yellow, conceptsBackColor=new Color(100,100,100),
	mainBackColor=new Color(128,128,128), mainForeColor=Color.yellow, saveButtonsColor=new Color(128,255,128), 
	thumbarBackColor=Color.white, thumbarLensColor=Color.white, thumbarWLColor=Color.white, thumbarANOHColor=Color.white, 
	thumbarLinkColor=Color.blue, thumbarLensLineColor=Color.white, style1TextColor=Color.white, style1BackColor=Color.white,
	style2TextColor=Color.white, style2BackColor=Color.white, style3TextColor=Color.white,style3BackColor=Color.white;
    private Component previousTab;
    private RHActiveConcept currentConcept=null;
    private int numGroups;
    private String[] fontData={"Sans Serif", "Serif", null};
    private boolean modifiedConcepts=false;
    private RHProfile profile;
    private String path="", user="", laf=null;

    public static void main (String args[]) {
	RHProfileEditor maindude= new RHProfileEditor(args);
    }
    
    public RHProfileEditor (String args[]) {
	//super (parent,title);

	System.out.println("L&F:"+UIManager.getSystemLookAndFeelClassName());
	laf=UIManager.getSystemLookAndFeelClassName();
	if (laf!=RH_GlobalVars.windowsLAF) laf=RH_GlobalVars.motifLAF;//laf=RH_GlobalVars.metalLAF;  
	try {
	    UIManager.setLookAndFeel(laf);
	} catch (Exception exc) {
	    System.out.println("Error loading L&F: " + exc);
	}

	profile=new RHProfile(this);

	path="c:/pia/Agents/RHPMAgent/profiles";
	RHStartUp su=new RHStartUp(this,path);
	su.show();
	path=su.getPath();
	user=su.getUsername();
	su.dispose();
	su=null;
	setCursor(new Cursor(Cursor.WAIT_CURSOR));
	
	RH_FileContents fc=new RH_FileContents();
	String profilepath=path+RH_GlobalVars.rhPathSeparator+user+RH_GlobalVars.rhPathSeparator;
	System.out.println("PATH: "+profilepath);
	String profileString=fc.grabFileContents(profilepath+"profile.rh");
	String locationsString=fc.grabFileContents(profilepath+"locations.rh");
	String groupsString=fc.grabFileContents(profilepath+"groups.rh");
	String conceptsString=fc.grabFileContents(profilepath+"concepts.rh");
	//** if proxy laoded sucessfully, load the rest of the profile
	profile.loadProfile(profileString, conceptsString, groupsString, locationsString);
	profileString=conceptsString=groupsString=locationsString=null;
	if (!profile.successfulLoad()) {
	    System.out.println("****MAJOR PROBLEM: Profile files (profile.rh, locations.rh, or concepts.rh) not found...exiting");
	    System.exit(0);
	}

	topicsTitleColor=Color.black; //commBus.getLocationTextColor();
	conceptsColor=Color.yellow; //commBus.getModeTextColor();
	//ImageIcon icon=new ImageIcon(commBus.getGifsPath()+"/smallredball.gif");
	
	getContentPane().setLayout(new BorderLayout());
	getContentPane().setBackground(mainBackColor);
	tabbedPane=new JTabbedPane();
	tabbedPane.addChangeListener(this);
	tabbedPane.setBackground(mainBackColor);
	tabbedPane.setForeground(mainForeColor);
	tabbedPane.setFont(new Font("MS Sans Serif",Font.ITALIC,11));
	//tabbedPane.addChangeListener(this);
	getContentPane().add(tabbedPane,BorderLayout.CENTER);
	JPanel buttonPanel = new JPanel();
	buttonPanel.setBackground(mainBackColor);
	cancel = new JButton("Close Editor");
	cancel.setForeground(conceptsColor);
	cancel.setBackground(conceptsBackColor); //mainBackColor);
	cancel.addActionListener(this);
	buttonPanel.add(cancel,BorderLayout.SOUTH);
	getContentPane().add(buttonPanel,BorderLayout.SOUTH);
	//** subtract 1 from numGroups to take care of last group called retro which is a system group
	numGroups=profile.getNumberGroups()-1;
	
	borderFont= new Font("MS Sans Serif",Font.PLAIN,10);
	Font optionsFont= new Font("MS Sans Serif",Font.PLAIN,10),conceptsFont= new Font("MS Sans Serif",Font.PLAIN,11);
	
	//** Create panels
	makeEditConcepts(optionsFont,conceptsFont);
	makeEditGroups(optionsFont,conceptsFont);
	makeEditOptions(optionsFont,conceptsFont);
	makeEditThumbar(optionsFont,conceptsFont);
	makeEditHlite(optionsFont,conceptsFont);
	makeEditPrint(optionsFont,conceptsFont);
	
	tabbedPane.addTab("Options",editOptionsPanel);
	tabbedPane.addTab("Concepts",editConceptsPanel);
	tabbedPane.addTab("Groups",editGroupsPanel);
	tabbedPane.addTab("Thumbar",editThumbarPanel);
	tabbedPane.addTab("Highlighting",editHlitePanel);
	tabbedPane.addTab("Printing",editPrintPanel);

	modifiedConcepts=false;
	
	//Point loc=commBus.documentControl.getLocation();
	Dimension size=getSize();
	//	int x=(int)(loc.x+(size.width/2))-(width/2)-20, y=loc.y+100;
	int x=0, y=0;
	setSize(width,height);
	setLocation(x,y);
	//setModal(false);
	setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	show();
    } 
    
    private void makeEditGroups(Font optionsFont, Font conceptsFont) {
	editGroupsPanel=new JPanel();	
	editGroupsPanel.setLayout(new BorderLayout());
	Color mainButtonsBackColor=backColor;
	
	JPanel editGroupsButtonPanel=new JPanel();
	editGroupsButtonPanel.setLayout(new GridLayout(1,3));
	editGroupsButtonPanel.setBackground(mainButtonsBackColor);
	editGroupsSave=new JButton("Save Group Changes");
	editGroupsSave.setBackground(mainButtonsBackColor);
	editGroupsSave.setForeground(saveButtonsColor);
	editGroupsSave.setFont(conceptsFont);
	editGroupsSave.addActionListener(this);
	editGroupsButtonPanel.add(editGroupsSave);
	
	editGroupsNoSave=new JButton("This session only (no save)");
	editGroupsNoSave.setBackground(mainButtonsBackColor);
	editGroupsNoSave.setForeground(saveButtonsColor);
	editGroupsNoSave.setFont(conceptsFont);
	editGroupsNoSave.addActionListener(this);
	editGroupsButtonPanel.add(editGroupsNoSave);
	
	editGroupsCancel=new JButton("Disregard all changes");
	editGroupsCancel.setBackground(mainButtonsBackColor);
	editGroupsCancel.setForeground(saveButtonsColor);
	editGroupsCancel.setFont(conceptsFont);
	editGroupsCancel.addActionListener(this);
	editGroupsButtonPanel.add(editGroupsCancel);
	
	editGroupsSave.setEnabled(false);
	editGroupsNoSave.setEnabled(false);
	editGroupsCancel.setEnabled(false);
	editGroupsPanel.add(editGroupsButtonPanel,BorderLayout.SOUTH);
	
	JPanel groupsPanel=new JPanel();
	groupsPanel.setLayout(new BorderLayout());
	String str=groupBorderTitle+numGroups;
	groupBorder=new TitledBorder(new TitledBorder(""),str,TitledBorder.LEFT,TitledBorder.TOP,borderFont,topicsTitleColor);
	groupsPanel.setBorder(groupBorder);
	groupsPanel.setBackground(backColor);
	RH_ConceptGroup[] tmp=profile.getConceptGroups();
	groupData=new String[tmp.length-1];
	for (int i=0;i<tmp.length-1;i++) groupData[i]=tmp[i].getName();
	groups=new JList(groupData);
	groups.setBorder(BorderFactory.createBevelBorder(1,Color.gray,Color.black));
	groups.setFont(conceptsFont);
	groups.setForeground(conceptsColor);
	groups.setBackground(conceptsBackColor);
	groups.addListSelectionListener(this);
	groupsScrollPane = new JScrollPane(groups);
	groupsScrollPane.setForeground(conceptsColor);
	groupsPanel.add(groupsScrollPane,BorderLayout.CENTER);

	JPanel groupControlPanel=new JPanel();
	groupControlPanel.setLayout(new BorderLayout());
	JPanel groupsButtonsPanel=new JPanel();
	groupsButtonsPanel.setLayout(new GridLayout(1,3));
	groupsButtonsPanel.setBackground(backColor);
	addGroup=new JButton("Add");
	addGroup.setBackground(backColor);
	addGroup.setFont(conceptsFont);
	addGroup.setForeground(topicsTitleColor);
	addGroup.addActionListener(this);
	addGroup.setEnabled(true);
	addGroup.setToolTipText("Add new group to the list");
	groupsButtonsPanel.add(addGroup);
	
	editGroup=new JButton("Edit");
	editGroup.setBackground(backColor);
	editGroup.setFont(conceptsFont);
	editGroup.setForeground(topicsTitleColor);
	editGroup.addActionListener(this);
	editGroup.setEnabled(false);
	editGroup.setToolTipText("Edit the currently selected group name");
	groupsButtonsPanel.add(editGroup);
	
	removeGroup=new JButton("Remove");
	removeGroup.setBackground(backColor);
	removeGroup.setFont(conceptsFont);
	removeGroup.setForeground(topicsTitleColor);
	removeGroup.addActionListener(this);
	removeGroup.setEnabled(false);
	removeGroup.setToolTipText("Remove the currently selected group");
	groupsButtonsPanel.add(removeGroup);
	groupControlPanel.add(groupsButtonsPanel,BorderLayout.SOUTH);

	JPanel defaultGroupPanel=new JPanel();
	defaultGroupPanel.setLayout(new BorderLayout());
	defaultGroupPanel.setBackground(Color.darkGray);
	defaultGroupPanel.setBorder(BorderFactory.createBevelBorder(1,Color.gray,Color.black));

	groupToolTip=new JTextField(20);
	groupToolTip.setFont(conceptsFont);
	groupToolTip.addActionListener(this);
	groupToolTip.setFont(conceptsFont);
	groupToolTip.setSelectionColor(Color.darkGray);
	groupToolTip.setSelectedTextColor(topicsTitleColor);
	groupToolTip.setCaretColor(Color.blue);
	groupToolTip.setMargin(new Insets(2,5,2,2));
	//groupToolTip.setBorder(new EmptyBorder(0,0,0,0));
	groupToolTip.setToolTipText("Tool tip text used when mouse pointer points at group name in interface");
	groupToolTip.setBackground(Color.lightGray);
	groupToolTip.setForeground(Color.black);
	defaultGroupPanel.add(groupToolTip,BorderLayout.CENTER);

	defaultGroup=new JCheckBox("Def:");
	defaultGroup.setToolTipText("Set current group as default group at startup");
	defaultGroup.setFont(optionsFont);
	//defaultGroup.setHorizontalAlignment(AbstractButton.CENTER);
	defaultGroup.setBackground(Color.darkGray);
	defaultGroup.setForeground(topicsTitleColor);
	defaultGroup.setMargin(new Insets(2,2,2,2));
	defaultGroup.addActionListener(this);
	defaultGroupPanel.add(defaultGroup,BorderLayout.WEST);
	groupControlPanel.add(defaultGroupPanel,BorderLayout.NORTH);

	groupsPanel.add(groupControlPanel,BorderLayout.SOUTH);

	JPanel groupConceptsPanel=new JPanel();
	groupConceptsPanel.setLayout(new BorderLayout());
	str=groupConceptsBorderTitle;
	groupConceptsBorder=new TitledBorder(new TitledBorder(""),str,TitledBorder.LEFT,TitledBorder.TOP,borderFont,topicsTitleColor);
	groupConceptsPanel.setBorder(groupConceptsBorder);
	groupConceptsPanel.setBackground(backColor);
	groupConcepts=new JList();
	groupConcepts.setBorder(BorderFactory.createBevelBorder(1,Color.darkGray,Color.black));
	groupConcepts.setFont(conceptsFont);
	groupConcepts.setForeground(conceptsColor);
	groupConcepts.setBackground(conceptsBackColor);
	groupConcepts.addListSelectionListener(this);
	groupConceptsScrollPane = new JScrollPane(groupConcepts);
	groupConceptsScrollPane.setForeground(conceptsColor);
	groupConceptsPanel.add(groupConceptsScrollPane,BorderLayout.CENTER);
	removeGroupConcept=new JButton("Remove >>");
	removeGroupConcept.setBackground(backColor);
	removeGroupConcept.setForeground(topicsTitleColor);
	removeGroupConcept.setFont(conceptsFont);
	removeGroupConcept.addActionListener(this);
	removeGroupConcept.setEnabled(false);
	groupConceptsPanel.add(removeGroupConcept,BorderLayout.SOUTH);

	JPanel groupAllConceptsPanel=new JPanel();
	groupAllConceptsPanel.setLayout(new BorderLayout());
	str=groupAllConceptsBorderTitle+profile.numAllConcepts;
	groupAllConceptsBorder=new TitledBorder(new TitledBorder(""),str,TitledBorder.LEFT,TitledBorder.TOP,borderFont,topicsTitleColor);
	groupAllConceptsPanel.setBorder(groupAllConceptsBorder);
	groupAllConceptsPanel.setBackground(backColor);
	groupAllConcepts=new JList(conceptData);
	groupAllConcepts.setBorder(BorderFactory.createBevelBorder(1,Color.darkGray,Color.black));
	groupAllConcepts.setFont(conceptsFont);
	groupAllConcepts.setForeground(conceptsColor);
	groupAllConcepts.setBackground(conceptsBackColor);
	groupAllConcepts.addListSelectionListener(this);
	groupAllConceptsScrollPane = new JScrollPane(groupAllConcepts);
	groupAllConceptsScrollPane.setForeground(conceptsColor);
	groupAllConceptsPanel.add(groupAllConceptsScrollPane,BorderLayout.CENTER);
	addGroupConcept=new JButton("<< Add");
	addGroupConcept.setBackground(backColor);
	addGroupConcept.setForeground(topicsTitleColor);
	addGroupConcept.setFont(conceptsFont);
	addGroupConcept.addActionListener(this);
	addGroupConcept.setEnabled(false);
	groupAllConceptsPanel.add(addGroupConcept,BorderLayout.SOUTH);
	
	editGroupsPanel.add(groupsPanel,BorderLayout.WEST);
	editGroupsPanel.add(groupConceptsPanel,BorderLayout.CENTER);
	editGroupsPanel.add(groupAllConceptsPanel,BorderLayout.EAST);
    }

    private void makeEditPrint(Font optionsFont, Font conceptsFont) {
	editPrintPanel=new JPanel();	
	editPrintPanel.setBackground(backColor);
	editPrintPanel.setLayout(new BorderLayout());

	JPanel editPrintButtonPanel=new JPanel();
	editPrintButtonPanel.setLayout(new GridLayout(1,3));
	editPrintButtonPanel.setBackground(backColor);
	editPrintSave=new JButton("Save Profile Changes");
	editPrintSave.setBackground(backColor);
	editPrintSave.setForeground(saveButtonsColor);
	editPrintSave.setFont(conceptsFont);
	editPrintSave.addActionListener(this);
	editPrintButtonPanel.add(editPrintSave);
	
	editPrintNoSave=new JButton("This session only (no save)");
	editPrintNoSave.setBackground(backColor);
	editPrintNoSave.setForeground(saveButtonsColor);
	editPrintNoSave.setFont(conceptsFont);
	editPrintNoSave.addActionListener(this);
	editPrintButtonPanel.add(editPrintNoSave);
	
	editPrintCancel=new JButton("Disregard all changes");
	editPrintCancel.setBackground(backColor);
	editPrintCancel.setForeground(saveButtonsColor);
	editPrintCancel.setFont(conceptsFont);
	editPrintCancel.addActionListener(this);
	editPrintButtonPanel.add(editPrintCancel);
	
	editPrintSave.setEnabled(false);
	editPrintNoSave.setEnabled(false);
	editPrintCancel.setEnabled(false);
	editPrintPanel.add(editPrintButtonPanel,BorderLayout.SOUTH);

	JPanel printPanel=new JPanel();
	printPanel.setLayout(new BorderLayout());
	printPanel.setBackground(backColor);
	editPrintPanel.add(printPanel,BorderLayout.NORTH);
	
	JPanel optionsPanel=new JPanel();
	optionsPanel.setLayout(new GridLayout(4,1,4,4));
	optionsPanel.setBackground(backColor);
	optionsPanel.setBorder(new TitledBorder(new TitledBorder(""),"PIA Print Server Information",TitledBorder.LEFT,TitledBorder.TOP,
						borderFont,Color.yellow));
	printPanel.add(optionsPanel,BorderLayout.CENTER);
	
	//**
	JPanel hostPanel=new JPanel();
	hostPanel.setLayout(new BorderLayout());
	hostPanel.setBackground(backColor);
	piaHost=new JTextField(profile.getPiaHost(),30);
	piaHost.setBackground(Color.lightGray);
	piaHost.setForeground(Color.black);
	piaHost.setFont(conceptsFont);
	piaHost.addActionListener(this);
	piaHost.setToolTipText("Enter PIA host name, e.g. caliente or caliente.crc.ricoh.com");
	piaHost.setSelectionColor(Color.darkGray);
	piaHost.setSelectedTextColor(topicsTitleColor);
	piaHost.setMargin(new Insets(2,5,2,2));
	piaHost.setCaretColor(Color.blue);
	JLabel piahostlabel=new JLabel("Host Name:",JLabel.RIGHT);
	piahostlabel.setBackground(backColor);
	piahostlabel.setForeground(topicsTitleColor);
	piahostlabel.setFont(optionsFont);
	hostPanel.add(piaHost,BorderLayout.CENTER);
	hostPanel.add(piahostlabel,BorderLayout.WEST);

	JPanel portPanel=new JPanel();
	portPanel.setLayout(new BorderLayout());
	portPanel.setBackground(backColor);
	piaPort=new JTextField(profile.getPiaPort()+"",30);
	piaPort.setBackground(Color.lightGray);
	piaPort.setForeground(Color.black);
	piaPort.setFont(conceptsFont);
	piaPort.addActionListener(this);
	piaPort.setToolTipText("Enter port number, e.g. 8888");
	piaPort.setSelectionColor(Color.darkGray);
	piaPort.setSelectedTextColor(topicsTitleColor);
	piaPort.setMargin(new Insets(2,5,2,2));
	piaPort.setCaretColor(Color.blue);
	JLabel piaportlabel=new JLabel("Port Number:",JLabel.RIGHT);
	piaportlabel.setBackground(backColor);
	piaportlabel.setForeground(topicsTitleColor);
	piaportlabel.setFont(optionsFont);
	portPanel.add(piaPort,BorderLayout.CENTER);
	portPanel.add(piaportlabel,BorderLayout.WEST);

	JPanel agentPanel=new JPanel();
	agentPanel.setLayout(new BorderLayout());
	agentPanel.setBackground(backColor);
	piaAgent=new JTextField(profile.getPiaAgentName(),30);
	piaAgent.setBackground(Color.lightGray);
	piaAgent.setForeground(Color.black);
	piaAgent.setFont(conceptsFont);
	piaAgent.addActionListener(this);
	piaAgent.setToolTipText("Enter name of PIA Agent responsible for printing, e.g. RHPSAgent");
	piaAgent.setSelectionColor(Color.darkGray);
	piaAgent.setSelectedTextColor(topicsTitleColor);
	piaAgent.setMargin(new Insets(2,5,2,2));
	piaAgent.setCaretColor(Color.blue);
	JLabel piaagentlabel=new JLabel("PrintAgent Name:",JLabel.RIGHT);
	piaagentlabel.setBackground(backColor);
	piaagentlabel.setForeground(topicsTitleColor);
	piaagentlabel.setFont(optionsFont);
	agentPanel.add(piaAgent,BorderLayout.CENTER);
	agentPanel.add(piaagentlabel,BorderLayout.WEST);

	JPanel printerPanel=new JPanel();
	printerPanel.setLayout(new BorderLayout());
	printerPanel.setBackground(backColor);
	piaPrinter=new JTextField(profile.getPiaPrinterName(),30);
	piaPrinter.setBackground(Color.lightGray);
	piaPrinter.setForeground(Color.black);
	piaPrinter.setFont(conceptsFont);
	piaPrinter.addActionListener(this);
	piaPrinter.setToolTipText("Enter default printer, e.g. ps4");
	piaPrinter.setSelectionColor(Color.darkGray);
	piaPrinter.setSelectedTextColor(topicsTitleColor);
	piaPrinter.setMargin(new Insets(2,5,2,2));
	piaPrinter.setCaretColor(Color.blue);
	JLabel piaprinterlabel=new JLabel("Printer Name:",JLabel.RIGHT);
	piaprinterlabel.setBackground(backColor);
	piaprinterlabel.setForeground(topicsTitleColor);
	piaprinterlabel.setFont(optionsFont);
	printerPanel.add(piaPrinter,BorderLayout.CENTER);
	printerPanel.add(piaprinterlabel,BorderLayout.WEST);

	optionsPanel.add(hostPanel);
	optionsPanel.add(portPanel);
	optionsPanel.add(agentPanel);
	optionsPanel.add(printerPanel);
    }

    private void makeEditThumbar(Font optionsFont, Font conceptsFont) {
	editThumbarPanel=new JPanel();	
	editThumbarPanel.setBackground(backColor);
	editThumbarPanel.setLayout(new BorderLayout());

	JPanel editThumbarButtonPanel=new JPanel();
	editThumbarButtonPanel.setLayout(new GridLayout(1,3));
	editThumbarButtonPanel.setBackground(backColor);
	editThumbarSave=new JButton("Save Profile Changes");
	editThumbarSave.setBackground(backColor);
	editThumbarSave.setForeground(saveButtonsColor);
	editThumbarSave.setFont(conceptsFont);
	editThumbarSave.addActionListener(this);
	editThumbarButtonPanel.add(editThumbarSave);
	
	editThumbarNoSave=new JButton("This session only (no save)");
	editThumbarNoSave.setBackground(backColor);
	editThumbarNoSave.setForeground(saveButtonsColor);
	editThumbarNoSave.setFont(conceptsFont);
	editThumbarNoSave.addActionListener(this);
	editThumbarButtonPanel.add(editThumbarNoSave);
	
	editThumbarCancel=new JButton("Disregard all changes");
	editThumbarCancel.setBackground(backColor);
	editThumbarCancel.setForeground(saveButtonsColor);
	editThumbarCancel.setFont(conceptsFont);
	editThumbarCancel.addActionListener(this);
	editThumbarButtonPanel.add(editThumbarCancel);
	
	editThumbarSave.setEnabled(false);
	editThumbarNoSave.setEnabled(false);
	editThumbarCancel.setEnabled(false);
	editThumbarPanel.add(editThumbarButtonPanel,BorderLayout.SOUTH);


	//**----------------Thumbar
	Color subPanelsBackColor=new Color(110,110,110);
	JPanel thumbarPanel=new JPanel();
	thumbarPanel.setBorder(new TitledBorder(new TitledBorder(""),"",TitledBorder.LEFT,TitledBorder.TOP,
						      borderFont,topicsTitleColor));
	GridBagLayout thumbargbl = new GridBagLayout();
	GridBagConstraints thumbargbc = new GridBagConstraints();
	thumbarPanel.setLayout(new BorderLayout()); //thumbargbl);
	thumbarPanel.setBackground(subPanelsBackColor);

	JPanel lensRatioBorderPanel=new JPanel();
	lensRatioBorderPanel.setLayout(new GridLayout(1,3));
	lensRatioBorderPanel.setBackground(subPanelsBackColor);
	lensRatioBorderPanel.setBorder(new TitledBorder(new TitledBorder(""),"",TitledBorder.LEFT,TitledBorder.TOP,borderFont,topicsTitleColor));
	JPanel lensRatioPanel=new JPanel();
	lensRatioPanel.setLayout(new BorderLayout());
	lensRatioPanel.setBackground(subPanelsBackColor);
	JLabel lensRatioText=new JLabel("Lens Ratio:",JLabel.RIGHT);
	lensRatioText.setBackground(subPanelsBackColor);
	lensRatioText.setForeground(topicsTitleColor);
	lensRatioText.setFont(optionsFont);
	lensRatioPanel.add(lensRatioText,BorderLayout.WEST);
	lensRatio=new JTextField(new String(""+profile.getLensViewFraction()),2);
	lensRatio.setFont(conceptsFont);
	lensRatio.addActionListener(this);
	lensRatio.setFont(conceptsFont);
	lensRatio.setSelectionColor(Color.darkGray);
	lensRatio.setSelectedTextColor(topicsTitleColor);
	lensRatio.setCaretColor(Color.blue);
	lensRatio.setHorizontalAlignment(JTextField.CENTER);
	lensRatio.setMargin(new Insets(2,5,2,2));
	//groupToolTip.setBorder(new EmptyBorder(0,0,0,0));
	lensRatio.setToolTipText("Thumbar lens ratio, e.g. 6 = 1/6 the size of the document");
	lensRatio.setBackground(Color.lightGray);
	lensRatio.setForeground(Color.black);
	lensRatioPanel.add(lensRatio,BorderLayout.CENTER);

	useLensLogo=new JCheckBox("LensLogo");
	useLensLogo.setToolTipText("Display small, transparent logo in lens");
	useLensLogo.setFont(optionsFont);
	useLensLogo.setHorizontalAlignment(AbstractButton.RIGHT);
	//defaultGroup.setHorizontalAlignment(AbstractButton.CENTER);
	useLensLogo.setBackground(subPanelsBackColor);
	useLensLogo.setForeground(topicsTitleColor);
	useLensLogo.setMargin(new Insets(2,2,2,2));
	useLensLogo.addActionListener(this);
	if (profile.getUseLensLogo()==1) useLensLogo.setSelected(true);
	else useLensLogo.setSelected(false);

	useAnohDoubleLine=new JCheckBox("AnohDL");
	useAnohDoubleLine.setToolTipText("Use a double line for all annotations");
	useAnohDoubleLine.setFont(optionsFont);
	useAnohDoubleLine.setHorizontalAlignment(AbstractButton.RIGHT);
	//defaultGroup.setHorizontalAlignment(AbstractButton.CENTER);
	useAnohDoubleLine.setBackground(subPanelsBackColor);
	useAnohDoubleLine.setForeground(topicsTitleColor);
	useAnohDoubleLine.setMargin(new Insets(2,2,2,2));
	useAnohDoubleLine.addActionListener(this);
	if (profile.getUseAnohDoubleLine()==1) useAnohDoubleLine.setSelected(true);
	else useAnohDoubleLine.setSelected(false);

	useLinkDoubleLine=new JCheckBox("LinkDL");
	useLinkDoubleLine.setToolTipText("Use a double line for all annotations");
	useLinkDoubleLine.setFont(optionsFont);
	useLinkDoubleLine.setHorizontalAlignment(AbstractButton.RIGHT);
	//defaultGroup.setHorizontalAlignment(AbstractButton.CENTER);
	useLinkDoubleLine.setBackground(subPanelsBackColor);
	useLinkDoubleLine.setForeground(topicsTitleColor);
	useLinkDoubleLine.setMargin(new Insets(2,2,2,2));
	useLinkDoubleLine.addActionListener(this);
	if (profile.getUseLinkDoubleLine()==1) useLinkDoubleLine.setSelected(true);
	else useLinkDoubleLine.setSelected(false);
	
	lensRatioBorderPanel.add(lensRatioPanel);
	lensRatioBorderPanel.add(useLensLogo);
	lensRatioBorderPanel.add(useAnohDoubleLine);
	lensRatioBorderPanel.add(useLinkDoubleLine);
	thumbarPanel.add(lensRatioBorderPanel,BorderLayout.NORTH);


	Color titleColor=new Color(255,255,0);
	JPanel thumbarBackPanel=makeThumbarBackColorPanel(subPanelsBackColor,conceptsFont,optionsFont,titleColor);
	JPanel thumbarLensPanel=makeThumbarLensColorPanel(subPanelsBackColor,conceptsFont,optionsFont,titleColor);
	JPanel thumbarWindowLinePanel=makeThumbarWindowLineColorPanel(subPanelsBackColor,conceptsFont,optionsFont,titleColor);
	JPanel thumbarANOHPanel=makeThumbarANOHColorPanel(subPanelsBackColor,conceptsFont,optionsFont,titleColor);
	JPanel thumbarLinkPanel=makeThumbarLinkColorPanel(subPanelsBackColor,conceptsFont,optionsFont,titleColor);
	JPanel thumbarLensLinePanel=makeThumbarLensLineColorPanel(subPanelsBackColor,conceptsFont,optionsFont,titleColor);
	JPanel thumbarSubPanel=new JPanel();
	thumbarSubPanel.setLayout(new GridLayout(3,2));
	thumbarSubPanel.add(thumbarBackPanel);
	thumbarSubPanel.add(thumbarLensPanel);
	thumbarSubPanel.add(thumbarWindowLinePanel);
	thumbarSubPanel.add(thumbarANOHPanel);
	thumbarSubPanel.add(thumbarLinkPanel);
	thumbarSubPanel.add(thumbarLensLinePanel);
	thumbarPanel.add(thumbarSubPanel,BorderLayout.SOUTH);

	JPanel thumbarMainPanel=new JPanel();
	thumbarMainPanel.setBackground(backColor);
	thumbarMainPanel.add(thumbarPanel);
	editThumbarPanel.add(thumbarMainPanel,BorderLayout.CENTER);
    }

    private void makeEditHlite(Font optionsFont, Font conceptsFont) {
	editHlitePanel=new JPanel();	
	editHlitePanel.setBackground(backColor);
	editHlitePanel.setLayout(new BorderLayout());

	JPanel editHliteButtonPanel=new JPanel();
	editHliteButtonPanel.setLayout(new GridLayout(1,3));
	editHliteButtonPanel.setBackground(backColor);
	editHliteSave=new JButton("Save Profile Changes");
	editHliteSave.setBackground(backColor);
	editHliteSave.setForeground(saveButtonsColor);
	editHliteSave.setFont(conceptsFont);
	editHliteSave.addActionListener(this);
	editHliteButtonPanel.add(editHliteSave);
	
	editHliteNoSave=new JButton("This session only (no save)");
	editHliteNoSave.setBackground(backColor);
	editHliteNoSave.setForeground(saveButtonsColor);
	editHliteNoSave.setFont(conceptsFont);
	editHliteNoSave.addActionListener(this);
	editHliteButtonPanel.add(editHliteNoSave);
	
	editHliteCancel=new JButton("Disregard all changes");
	editHliteCancel.setBackground(backColor);
	editHliteCancel.setForeground(saveButtonsColor);
	editHliteCancel.setFont(conceptsFont);
	editHliteCancel.addActionListener(this);
	editHliteButtonPanel.add(editHliteCancel);
	
	editHliteSave.setEnabled(false);
	editHliteNoSave.setEnabled(false);
	editHliteCancel.setEnabled(false);
	editHlitePanel.add(editHliteButtonPanel,BorderLayout.SOUTH);

	//**----------------Colors
	Color subPanelsBackColor=new Color(110,110,110);
	JPanel colorPanel=new JPanel();
	colorPanel.setLayout(new BorderLayout());
	colorPanel.setBackground(subPanelsBackColor);
	//colorPanel.setBorder(new TitledBorder(new TitledBorder(""),"",TitledBorder.LEFT,TitledBorder.TOP,borderFont,topicsTitleColor));

	Color titleColor=new Color(255,255,0);
	int stylenum=0;
	hstyles=profile.getHighlightStyles();

	//** Style 1
	JPanel style1TextColorPanel=makeStyle1TextColorPanel(hstyles[stylenum],subPanelsBackColor,conceptsFont,optionsFont,titleColor);
	JPanel style1BackColorPanel=makeStyle1BackColorPanel(hstyles[stylenum],subPanelsBackColor,conceptsFont,optionsFont,titleColor);
	JPanel style1=new JPanel();
	style1.setLayout(new BorderLayout());
	style1.setBackground(subPanelsBackColor);
	style1.setBorder(new TitledBorder(new TitledBorder(""),"Style 1",TitledBorder.LEFT,TitledBorder.TOP,borderFont,titleColor));
	JPanel style1ColorPanel=new JPanel();
	style1ColorPanel.setLayout(new GridLayout(1,2));
	style1ColorPanel.setBackground(subPanelsBackColor);
	style1ColorPanel.add(style1TextColorPanel);
	style1ColorPanel.add(style1BackColorPanel);
	style1Tip=new JTextField(hstyles[stylenum].getTip(),20);
	style1Tip.setFont(conceptsFont);
	style1Tip.addActionListener(this);
	style1Tip.setFont(conceptsFont);
	style1Tip.setSelectionColor(Color.darkGray);
	style1Tip.setSelectedTextColor(topicsTitleColor);
	style1Tip.setCaretColor(Color.blue);
	style1Tip.setMargin(new Insets(2,5,2,2));
	style1Tip.setToolTipText("Tool tip for this highlight style");
	style1Tip.setBackground(Color.lightGray);
	style1Tip.setForeground(Color.black);
	JPanel style1ChecksPanel=makeStyle1ChecksPanel(hstyles[stylenum++],subPanelsBackColor,conceptsFont,optionsFont,titleColor);
	style1.add(style1ColorPanel,BorderLayout.NORTH);
	style1.add(style1ChecksPanel,BorderLayout.CENTER);
	style1.add(style1Tip,BorderLayout.SOUTH);
	//** Style 2
	JPanel style2TextColorPanel=makeStyle2TextColorPanel(hstyles[stylenum],subPanelsBackColor,conceptsFont,optionsFont,titleColor);
	JPanel style2BackColorPanel=makeStyle2BackColorPanel(hstyles[stylenum],subPanelsBackColor,conceptsFont,optionsFont,titleColor);
	JPanel style2=new JPanel();
	style2.setLayout(new BorderLayout());
	style2.setBackground(subPanelsBackColor);
	style2.setBorder(new TitledBorder(new TitledBorder(""),"Style 2",TitledBorder.LEFT,TitledBorder.TOP,borderFont,titleColor));
	JPanel style2ColorPanel=new JPanel();
	style2ColorPanel.setLayout(new GridLayout(1,2));
	style2ColorPanel.setBackground(subPanelsBackColor);
	style2ColorPanel.add(style2TextColorPanel);
	style2ColorPanel.add(style2BackColorPanel);
	style2Tip=new JTextField(hstyles[stylenum].getTip(),20);
	style2Tip.setFont(conceptsFont);
	style2Tip.addActionListener(this);
	style2Tip.setFont(conceptsFont);
	style2Tip.setSelectionColor(Color.darkGray);
	style2Tip.setSelectedTextColor(topicsTitleColor);
	style2Tip.setCaretColor(Color.blue);
	style2Tip.setMargin(new Insets(2,5,2,2));
	style2Tip.setToolTipText("Tool tip for this highlight style");
	style2Tip.setBackground(Color.lightGray);
	style2Tip.setForeground(Color.black);
	JPanel style2ChecksPanel=makeStyle2ChecksPanel(hstyles[stylenum++],subPanelsBackColor,conceptsFont,optionsFont,titleColor);
	style2.add(style2ColorPanel,BorderLayout.NORTH);
	style2.add(style2ChecksPanel,BorderLayout.CENTER);
	style2.add(style2Tip,BorderLayout.SOUTH);
	//** Style 3
	JPanel style3TextColorPanel=makeStyle3TextColorPanel(hstyles[stylenum],subPanelsBackColor,conceptsFont,optionsFont,titleColor);
	JPanel style3BackColorPanel=makeStyle3BackColorPanel(hstyles[stylenum],subPanelsBackColor,conceptsFont,optionsFont,titleColor);
	JPanel style3=new JPanel();
	style3.setLayout(new BorderLayout());
	style3.setBackground(subPanelsBackColor);
	style3.setBorder(new TitledBorder(new TitledBorder(""),"Style 3",TitledBorder.LEFT,TitledBorder.TOP,borderFont,titleColor));
	JPanel style3ColorPanel=new JPanel();
	style3ColorPanel.setLayout(new GridLayout(1,2));
	style3ColorPanel.setBackground(subPanelsBackColor);
	style3ColorPanel.add(style3TextColorPanel);
	style3ColorPanel.add(style3BackColorPanel);
	style3Tip=new JTextField(hstyles[stylenum].getTip(),20);
	style3Tip.setFont(conceptsFont);
	style3Tip.addActionListener(this);
	style3Tip.setFont(conceptsFont);
	style3Tip.setSelectionColor(Color.darkGray);
	style3Tip.setSelectedTextColor(topicsTitleColor);
	style3Tip.setCaretColor(Color.blue);
	style3Tip.setMargin(new Insets(2,5,2,2));
	style3Tip.setToolTipText("Tool tip for this highlight style");
	style3Tip.setBackground(Color.lightGray);
	style3Tip.setForeground(Color.black);
	JPanel style3ChecksPanel=makeStyle3ChecksPanel(hstyles[stylenum++],subPanelsBackColor,conceptsFont,optionsFont,titleColor);
	style3.add(style3ColorPanel,BorderLayout.NORTH);
	style3.add(style3ChecksPanel,BorderLayout.CENTER);
	style3.add(style3Tip,BorderLayout.SOUTH);

	JPanel colorSubPanel=new JPanel();
	colorSubPanel.setBackground(subPanelsBackColor);
	colorSubPanel.setLayout(new GridLayout(3,1));
	colorSubPanel.add(style1);
	colorSubPanel.add(style2);
	colorSubPanel.add(style3);
	colorPanel.add(colorSubPanel,BorderLayout.SOUTH);

	JPanel colorMainPanel=new JPanel();
	colorMainPanel.setBackground(subPanelsBackColor);
	colorMainPanel.add(colorPanel);
	editHlitePanel.add(colorMainPanel,BorderLayout.CENTER);
    }

    private JPanel makeStyle1ChecksPanel(RH_HighlightStyle style, Color subPanelsBackColor, Font conceptsFont, Font optionsFont, Color titleColor) {
	JPanel style1ChecksPanel=new JPanel();
	style1ChecksPanel.setLayout(new GridLayout(1,6));
	style1ChecksPanel.setBackground(subPanelsBackColor);
	//style1ChecksPanel.setBorder(new TitledBorder(new TitledBorder(""),"",TitledBorder.LEFT,TitledBorder.TOP,borderFont,topicsTitleColor));
	style1Bold=new JCheckBox("BoldFont");
	style1Bold.setToolTipText("Use a bold font in the highlight style");
	style1Bold.setFont(optionsFont);
	style1Bold.setBackground(subPanelsBackColor);
	style1Bold.setForeground(topicsTitleColor);
	style1Bold.setMargin(new Insets(2,2,2,2));
	style1Bold.addActionListener(this);
	if (style.getBold()==1) style1Bold.setSelected(true);
	else style1Bold.setSelected(false);
	style1Under=new JRadioButton("Underline");
	style1Under.setToolTipText("Underline the keyphrase in the highlight style");
	style1Under.setFont(optionsFont);
	style1Under.setBackground(subPanelsBackColor);
	style1Under.setForeground(topicsTitleColor);
	style1Under.setMargin(new Insets(2,2,2,2));
	style1Under.addActionListener(this);
	if (style.getUnder()==1) style1Under.setSelected(true);
	else style1Under.setSelected(false);
	style1Box=new JRadioButton("Boxed");
	style1Box.setToolTipText("Draw a colored box around annotation");
	style1Box.setFont(optionsFont);
	style1Box.setBackground(subPanelsBackColor);
	style1Box.setForeground(topicsTitleColor);
	style1Box.setMargin(new Insets(2,2,2,2));
	style1Box.addActionListener(this);
	if (style.getBox()==1) style1Box.setSelected(true);
	else style1Box.setSelected(false);
	style1Shadow=new JCheckBox("Shadow");
	style1Shadow.setToolTipText("Use a drop shadow in the highlight style");
	style1Shadow.setFont(optionsFont);
	style1Shadow.setBackground(subPanelsBackColor);
	style1Shadow.setForeground(topicsTitleColor);
	style1Shadow.setMargin(new Insets(2,2,2,2));
	style1Shadow.addActionListener(this);
	if (style.getShadow()==1) style1Shadow.setSelected(true);
	else style1Shadow.setSelected(false);
	style1Whole=new JCheckBox("WholeSentence");
	style1Whole.setToolTipText("Highlight whole sentence keyphrase contained in");
	style1Whole.setFont(optionsFont);
	style1Whole.setBackground(subPanelsBackColor);
	style1Whole.setForeground(topicsTitleColor);
	style1Whole.setMargin(new Insets(2,2,2,2));
	style1Whole.addActionListener(this);
	if (style.getWhole()==1) style1Whole.setSelected(true);
	else style1Whole.setSelected(false);
	style1Def=new JCheckBox("Def.");
	style1Def.setToolTipText("Use this style as the default style");
	style1Def.setFont(optionsFont);
	style1Def.setBackground(subPanelsBackColor);
	style1Def.setForeground(topicsTitleColor);
	style1Def.setMargin(new Insets(2,2,2,2));
	style1Def.addActionListener(this);
	if (profile.getDefaultHliteStyle()==style.getIdx()) style1Def.setSelected(true);
	else style1Def.setSelected(false);
	style1ChecksPanel.add(style1Bold);
	style1ChecksPanel.add(style1Under);
	style1ChecksPanel.add(style1Box);
	style1ChecksPanel.add(style1Shadow);
	style1ChecksPanel.add(style1Whole);
	style1ChecksPanel.add(style1Def);

	return style1ChecksPanel;
    }
    private JPanel makeStyle2ChecksPanel(RH_HighlightStyle style, Color subPanelsBackColor, Font conceptsFont, Font optionsFont, Color titleColor) {
	JPanel style2ChecksPanel=new JPanel();
	style2ChecksPanel.setLayout(new GridLayout(1,6));
	style2ChecksPanel.setBackground(subPanelsBackColor);
	//style2ChecksPanel.setBorder(new TitledBorder(new TitledBorder(""),"",TitledBorder.LEFT,TitledBorder.TOP,borderFont,topicsTitleColor));
	style2Bold=new JCheckBox("BoldFont");
	style2Bold.setToolTipText("Use a bold font in the highlight style");
	style2Bold.setFont(optionsFont);
	style2Bold.setBackground(subPanelsBackColor);
	style2Bold.setForeground(topicsTitleColor);
	style2Bold.setMargin(new Insets(2,2,2,2));
	style2Bold.addActionListener(this);
	if (style.getBold()==1) style2Bold.setSelected(true);
	else style2Bold.setSelected(false);
	style2Under=new JRadioButton("Underline");
	style2Under.setToolTipText("Underline the keyphrase in the highlight style");
	style2Under.setFont(optionsFont);
	style2Under.setBackground(subPanelsBackColor);
	style2Under.setForeground(topicsTitleColor);
	style2Under.setMargin(new Insets(2,2,2,2));
	style2Under.addActionListener(this);
	if (style.getUnder()==1) style2Under.setSelected(true);
	else style2Under.setSelected(false);
	style2Box=new JRadioButton("Boxed");
	style2Box.setToolTipText("Draw a colored box around annotation");
	style2Box.setFont(optionsFont);
	style2Box.setBackground(subPanelsBackColor);
	style2Box.setForeground(topicsTitleColor);
	style2Box.setMargin(new Insets(2,2,2,2));
	style2Box.addActionListener(this);
	if (style.getBox()==1) style2Box.setSelected(true);
	else style2Box.setSelected(false);
	style2Shadow=new JCheckBox("Shadow");
	style2Shadow.setToolTipText("Use a drop shadow in the highlight style");
	style2Shadow.setFont(optionsFont);
	style2Shadow.setBackground(subPanelsBackColor);
	style2Shadow.setForeground(topicsTitleColor);
	style2Shadow.setMargin(new Insets(2,2,2,2));
	style2Shadow.addActionListener(this);
	if (style.getShadow()==1) style2Shadow.setSelected(true);
	else style2Shadow.setSelected(false);
	style2Whole=new JCheckBox("WholeSentence");
	style2Whole.setToolTipText("Highlight whole sentence keyphrase contained in");
	style2Whole.setFont(optionsFont);
	style2Whole.setBackground(subPanelsBackColor);
	style2Whole.setForeground(topicsTitleColor);
	style2Whole.setMargin(new Insets(2,2,2,2));
	style2Whole.addActionListener(this);
	if (style.getWhole()==1) style2Whole.setSelected(true);
	else style2Whole.setSelected(false);
	style2Def=new JCheckBox("Def.");
	style2Def.setToolTipText("Use this style as the default style");
	style2Def.setFont(optionsFont);
	style2Def.setBackground(subPanelsBackColor);
	style2Def.setForeground(topicsTitleColor);
	style2Def.setMargin(new Insets(2,2,2,2));
	style2Def.addActionListener(this);
	if (profile.getDefaultHliteStyle()==style.getIdx()) style2Def.setSelected(true);
	else style2Def.setSelected(false);
	style2ChecksPanel.add(style2Bold);
	style2ChecksPanel.add(style2Under);
	style2ChecksPanel.add(style2Box);
	style2ChecksPanel.add(style2Shadow);
	style2ChecksPanel.add(style2Whole);
	style2ChecksPanel.add(style2Def);

	return style2ChecksPanel;
    }
    private JPanel makeStyle3ChecksPanel(RH_HighlightStyle style, Color subPanelsBackColor, Font conceptsFont, Font optionsFont, Color titleColor) {
	JPanel style3ChecksPanel=new JPanel();
	style3ChecksPanel.setLayout(new GridLayout(1,6));
	style3ChecksPanel.setBackground(subPanelsBackColor);
	//style3ChecksPanel.setBorder(new TitledBorder(new TitledBorder(""),"",TitledBorder.LEFT,TitledBorder.TOP,borderFont,topicsTitleColor));
	style3Bold=new JCheckBox("BoldFont");
	style3Bold.setToolTipText("Use a bold font in the highlight style");
	style3Bold.setFont(optionsFont);
	style3Bold.setBackground(subPanelsBackColor);
	style3Bold.setForeground(topicsTitleColor);
	style3Bold.setMargin(new Insets(2,2,2,2));
	style3Bold.addActionListener(this);
	if (style.getBold()==1) style3Bold.setSelected(true);
	else style3Bold.setSelected(false);
	style3Under=new JRadioButton("Underline");
	style3Under.setToolTipText("Underline the keyphrase in the highlight style");
	style3Under.setFont(optionsFont);
	style3Under.setBackground(subPanelsBackColor);
	style3Under.setForeground(topicsTitleColor);
	style3Under.setMargin(new Insets(2,2,2,2));
	style3Under.addActionListener(this);
	if (style.getUnder()==1) style3Under.setSelected(true);
	else style3Under.setSelected(false);
	style3Box=new JRadioButton("Boxed");
	style3Box.setToolTipText("Draw a colored box around annotation");
	style3Box.setFont(optionsFont);
	style3Box.setBackground(subPanelsBackColor);
	style3Box.setForeground(topicsTitleColor);
	style3Box.setMargin(new Insets(2,2,2,2));
	style3Box.addActionListener(this);
	if (style.getBox()==1) style3Box.setSelected(true);
	else style3Box.setSelected(false);
	style3Shadow=new JCheckBox("Shadow");
	style3Shadow.setToolTipText("Use a drop shadow in the highlight style");
	style3Shadow.setFont(optionsFont);
	style3Shadow.setBackground(subPanelsBackColor);
	style3Shadow.setForeground(topicsTitleColor);
	style3Shadow.setMargin(new Insets(2,2,2,2));
	style3Shadow.addActionListener(this);
	if (style.getShadow()==1) style3Shadow.setSelected(true);
	else style3Shadow.setSelected(false);
	style3Whole=new JCheckBox("WholeSentence");
	style3Whole.setToolTipText("Highlight whole sentence keyphrase contained in");
	style3Whole.setFont(optionsFont);
	style3Whole.setBackground(subPanelsBackColor);
	style3Whole.setForeground(topicsTitleColor);
	style3Whole.setMargin(new Insets(2,2,2,2));
	style3Whole.addActionListener(this);
	if (style.getWhole()==1) style3Whole.setSelected(true);
	else style3Whole.setSelected(false);
	style3Def=new JCheckBox("Def.");
	style3Def.setToolTipText("Use this style as the default style");
	style3Def.setFont(optionsFont);
	style3Def.setBackground(subPanelsBackColor);
	style3Def.setForeground(topicsTitleColor);
	style3Def.setMargin(new Insets(2,2,2,2));
	style3Def.addActionListener(this);
	if (profile.getDefaultHliteStyle()==style.getIdx()) style3Def.setSelected(true);
	else style3Def.setSelected(false);
	style3ChecksPanel.add(style3Bold);
	style3ChecksPanel.add(style3Under);
	style3ChecksPanel.add(style3Box);
	style3ChecksPanel.add(style3Shadow);
	style3ChecksPanel.add(style3Whole);
	style3ChecksPanel.add(style3Def);

	return style3ChecksPanel;
    }

    private JPanel makeStyle1TextColorPanel(RH_HighlightStyle style, Color subPanelsBackColor, Font conceptsFont, Font optionsFont, Color titleColor) {
    
	JPanel style1TextPanel=new JPanel();
	//style1TextPanel.setBorder(new TitledBorder(new TitledBorder(""),"Text Color",TitledBorder.LEFT,TitledBorder.TOP,borderFont,topicsTitleColor));
	style1TextPanel.setBackground(subPanelsBackColor);
	GridBagLayout backgbl = new GridBagLayout();
	GridBagConstraints backgbc = new GridBagConstraints();
	style1TextPanel.setLayout(backgbl);
	style1TextColor=profile.getOverviewWindowColor();

	JLabel r_label=new JLabel("TextColor R:",JLabel.RIGHT);
	r_label.setBackground(subPanelsBackColor);
	r_label.setForeground(topicsTitleColor);
	r_label.setFont(optionsFont);
	style1TextPanel.add(r_label);
	style1TextR_Color=new JTextField(style.getForeRed()+"",3);
	style1TextR_Color.setFont(conceptsFont);
	style1TextR_Color.addActionListener(this);
	style1TextR_Color.setFont(conceptsFont);
	style1TextR_Color.setSelectionColor(Color.darkGray);
	style1TextR_Color.setSelectedTextColor(topicsTitleColor);
	style1TextR_Color.setCaretColor(Color.blue);
	style1TextR_Color.setToolTipText("Red color value");
	style1TextR_Color.setBackground(Color.lightGray);
	style1TextR_Color.setForeground(Color.black);
	style1TextPanel.add(style1TextR_Color);

	JLabel g_label=new JLabel("G:",JLabel.RIGHT);
	g_label.setBackground(subPanelsBackColor);
	g_label.setForeground(topicsTitleColor);
	g_label.setFont(optionsFont);
	style1TextPanel.add(g_label);
	style1TextG_Color=new JTextField(style.getForeGreen()+"",3);
	style1TextG_Color.setFont(conceptsFont);
	style1TextG_Color.addActionListener(this);
	style1TextG_Color.setFont(conceptsFont);
	style1TextG_Color.setSelectionColor(Color.darkGray);
	style1TextG_Color.setSelectedTextColor(topicsTitleColor);
	style1TextG_Color.setCaretColor(Color.blue);
	style1TextG_Color.setToolTipText("Green color value");
	style1TextG_Color.setBackground(Color.lightGray);
	style1TextG_Color.setForeground(Color.black);
	style1TextPanel.add(style1TextG_Color);

	JLabel b_label=new JLabel("B:",JLabel.RIGHT);
	b_label.setBackground(subPanelsBackColor);
	b_label.setForeground(topicsTitleColor);
	b_label.setFont(optionsFont);
	style1TextPanel.add(b_label);
	style1TextB_Color=new JTextField(style.getForeBlue()+"",3);
	style1TextB_Color.setFont(conceptsFont);
	style1TextB_Color.addActionListener(this);
	style1TextB_Color.setFont(conceptsFont);
	style1TextB_Color.setSelectionColor(Color.darkGray);
	style1TextB_Color.setSelectedTextColor(topicsTitleColor);
	style1TextB_Color.setCaretColor(Color.blue);
	style1TextB_Color.setToolTipText("Blue color value");
	style1TextB_Color.setBackground(Color.lightGray);
	style1TextB_Color.setForeground(Color.black);
	style1TextPanel.add(style1TextB_Color);

	Color styleColor=new Color(style.getForeRed(),style.getForeGreen(),style.getForeBlue());
	style1TextSquare=new JButton("");
	style1TextSquare.setBackground(styleColor);
	style1TextSquare.setForeground(styleColor);
	style1TextSquare.setEnabled(false);
	style1TextPanel.add(style1TextSquare);

	int gridx=0;
	backgbc.anchor = GridBagConstraints.WEST;
	backgbc.fill = GridBagConstraints.BOTH;
	backgbc.insets = new Insets(2,1,2,1);
	buildConstraints(backgbc,0,0,1,1,100,100);
	backgbl.setConstraints(r_label,backgbc);
	buildConstraints(backgbc,1,0,1,1,100,100);
	backgbl.setConstraints(style1TextR_Color,backgbc);
	buildConstraints(backgbc,2,0,1,1,100,100);
	backgbl.setConstraints(g_label,backgbc);
	buildConstraints(backgbc,3,0,1,1,100,100);
	backgbl.setConstraints(style1TextG_Color,backgbc);
	buildConstraints(backgbc,4,0,1,1,100,100);
	backgbl.setConstraints(b_label,backgbc);
	buildConstraints(backgbc,5,0,1,1,100,100);
	backgbl.setConstraints(style1TextB_Color,backgbc);
	buildConstraints(backgbc,6,0,1,1,100,100);
	backgbl.setConstraints(backSquare,backgbc);

	return style1TextPanel;

    }

    private JPanel makeStyle1BackColorPanel(RH_HighlightStyle style, Color subPanelsBackColor, Font conceptsFont, Font optionsFont, Color titleColor) {
    
	JPanel style1BackPanel=new JPanel();
	//style1BackPanel.setBorder(new TitledBorder(new TitledBorder(""),"Back Color",TitledBorder.LEFT,TitledBorder.TOP,borderFont,topicsTitleColor));
	style1BackPanel.setBackground(subPanelsBackColor);
	GridBagLayout backgbl = new GridBagLayout();
	GridBagConstraints backgbc = new GridBagConstraints();
	style1BackPanel.setLayout(backgbl);
	style1BackColor=profile.getOverviewWindowColor();

	JLabel r_label=new JLabel("BackColor R:",JLabel.RIGHT);
	r_label.setBackground(subPanelsBackColor);
	r_label.setForeground(topicsTitleColor);
	r_label.setFont(optionsFont);
	style1BackPanel.add(r_label);
	style1BackR_Color=new JTextField(style.getRed()+"",3);
	style1BackR_Color.setFont(conceptsFont);
	style1BackR_Color.addActionListener(this);
	style1BackR_Color.setFont(conceptsFont);
	style1BackR_Color.setSelectionColor(Color.darkGray);
	style1BackR_Color.setSelectedTextColor(topicsTitleColor);
	style1BackR_Color.setCaretColor(Color.blue);
	style1BackR_Color.setToolTipText("Red color value");
	style1BackR_Color.setBackground(Color.lightGray);
	style1BackR_Color.setForeground(Color.black);
	style1BackPanel.add(style1BackR_Color);

	JLabel g_label=new JLabel("G:",JLabel.RIGHT);
	g_label.setBackground(subPanelsBackColor);
	g_label.setForeground(topicsTitleColor);
	g_label.setFont(optionsFont);
	style1BackPanel.add(g_label);
	style1BackG_Color=new JTextField(style.getGreen()+"",3);
	style1BackG_Color.setFont(conceptsFont);
	style1BackG_Color.addActionListener(this);
	style1BackG_Color.setFont(conceptsFont);
	style1BackG_Color.setSelectionColor(Color.darkGray);
	style1BackG_Color.setSelectedTextColor(topicsTitleColor);
	style1BackG_Color.setCaretColor(Color.blue);
	style1BackG_Color.setToolTipText("Green color value");
	style1BackG_Color.setBackground(Color.lightGray);
	style1BackG_Color.setForeground(Color.black);
	style1BackPanel.add(style1BackG_Color);

	JLabel b_label=new JLabel("B:",JLabel.RIGHT);
	b_label.setBackground(subPanelsBackColor);
	b_label.setForeground(topicsTitleColor);
	b_label.setFont(optionsFont);
	style1BackPanel.add(b_label);
	style1BackB_Color=new JTextField(style.getBlue()+"",3);
	style1BackB_Color.setFont(conceptsFont);
	style1BackB_Color.addActionListener(this);
	style1BackB_Color.setFont(conceptsFont);
	style1BackB_Color.setSelectionColor(Color.darkGray);
	style1BackB_Color.setSelectedTextColor(topicsTitleColor);
	style1BackB_Color.setCaretColor(Color.blue);
	style1BackB_Color.setToolTipText("Blue color value");
	style1BackB_Color.setBackground(Color.lightGray);
	style1BackB_Color.setForeground(Color.black);
	style1BackPanel.add(style1BackB_Color);

	Color styleColor=new Color(style.getRed(),style.getGreen(),style.getBlue());
	style1BackSquare=new JButton("");
	style1BackSquare.setBackground(styleColor);
	style1BackSquare.setForeground(styleColor);
	style1BackSquare.setEnabled(false);
	style1BackPanel.add(style1BackSquare);

	int gridx=0;
	backgbc.anchor = GridBagConstraints.WEST;
	backgbc.fill = GridBagConstraints.BOTH;
	backgbc.insets = new Insets(2,1,2,1);
	buildConstraints(backgbc,0,0,1,1,100,100);
	backgbl.setConstraints(r_label,backgbc);
	buildConstraints(backgbc,1,0,1,1,100,100);
	backgbl.setConstraints(style1BackR_Color,backgbc);
	buildConstraints(backgbc,2,0,1,1,100,100);
	backgbl.setConstraints(g_label,backgbc);
	buildConstraints(backgbc,3,0,1,1,100,100);
	backgbl.setConstraints(style1BackG_Color,backgbc);
	buildConstraints(backgbc,4,0,1,1,100,100);
	backgbl.setConstraints(b_label,backgbc);
	buildConstraints(backgbc,5,0,1,1,100,100);
	backgbl.setConstraints(style1BackB_Color,backgbc);
	buildConstraints(backgbc,6,0,1,1,100,100);
	backgbl.setConstraints(backSquare,backgbc);

	return style1BackPanel;

    }


    private JPanel makeStyle2TextColorPanel(RH_HighlightStyle style, Color subPanelsBackColor, Font conceptsFont, Font optionsFont, Color titleColor) {
    
	JPanel style2TextPanel=new JPanel();
	//style2TextPanel.setBorder(new TitledBorder(new TitledBorder(""),"Text Color",TitledBorder.LEFT,TitledBorder.TOP,borderFont,topicsTitleColor));
	style2TextPanel.setBackground(subPanelsBackColor);
	GridBagLayout backgbl = new GridBagLayout();
	GridBagConstraints backgbc = new GridBagConstraints();
	style2TextPanel.setLayout(backgbl);
	style2TextColor=profile.getOverviewWindowColor();

	JLabel r_label=new JLabel("TextColor R:",JLabel.RIGHT);
	r_label.setBackground(subPanelsBackColor);
	r_label.setForeground(topicsTitleColor);
	r_label.setFont(optionsFont);
	style2TextPanel.add(r_label);
	style2TextR_Color=new JTextField(style.getForeRed()+"",3);
	style2TextR_Color.setFont(conceptsFont);
	style2TextR_Color.addActionListener(this);
	style2TextR_Color.setFont(conceptsFont);
	style2TextR_Color.setSelectionColor(Color.darkGray);
	style2TextR_Color.setSelectedTextColor(topicsTitleColor);
	style2TextR_Color.setCaretColor(Color.blue);
	style2TextR_Color.setToolTipText("Red color value");
	style2TextR_Color.setBackground(Color.lightGray);
	style2TextR_Color.setForeground(Color.black);
	style2TextPanel.add(style2TextR_Color);

	JLabel g_label=new JLabel("G:",JLabel.RIGHT);
	g_label.setBackground(subPanelsBackColor);
	g_label.setForeground(topicsTitleColor);
	g_label.setFont(optionsFont);
	style2TextPanel.add(g_label);
	style2TextG_Color=new JTextField(style.getForeGreen()+"",3);
	style2TextG_Color.setFont(conceptsFont);
	style2TextG_Color.addActionListener(this);
	style2TextG_Color.setFont(conceptsFont);
	style2TextG_Color.setSelectionColor(Color.darkGray);
	style2TextG_Color.setSelectedTextColor(topicsTitleColor);
	style2TextG_Color.setCaretColor(Color.blue);
	style2TextG_Color.setToolTipText("Green color value");
	style2TextG_Color.setBackground(Color.lightGray);
	style2TextG_Color.setForeground(Color.black);
	style2TextPanel.add(style2TextG_Color);

	JLabel b_label=new JLabel("B:",JLabel.RIGHT);
	b_label.setBackground(subPanelsBackColor);
	b_label.setForeground(topicsTitleColor);
	b_label.setFont(optionsFont);
	style2TextPanel.add(b_label);
	style2TextB_Color=new JTextField(style.getForeBlue()+"",3);
	style2TextB_Color.setFont(conceptsFont);
	style2TextB_Color.addActionListener(this);
	style2TextB_Color.setFont(conceptsFont);
	style2TextB_Color.setSelectionColor(Color.darkGray);
	style2TextB_Color.setSelectedTextColor(topicsTitleColor);
	style2TextB_Color.setCaretColor(Color.blue);
	style2TextB_Color.setToolTipText("Blue color value");
	style2TextB_Color.setBackground(Color.lightGray);
	style2TextB_Color.setForeground(Color.black);
	style2TextPanel.add(style2TextB_Color);

	Color styleColor=new Color(style.getForeRed(),style.getForeGreen(),style.getForeBlue());
	style2TextSquare=new JButton("");
	style2TextSquare.setBackground(styleColor);
	style2TextSquare.setForeground(styleColor);
	style2TextSquare.setEnabled(false);
	style2TextPanel.add(style2TextSquare);

	int gridx=0;
	backgbc.anchor = GridBagConstraints.WEST;
	backgbc.fill = GridBagConstraints.BOTH;
	backgbc.insets = new Insets(2,1,2,1);
	buildConstraints(backgbc,0,0,1,1,100,100);
	backgbl.setConstraints(r_label,backgbc);
	buildConstraints(backgbc,1,0,1,1,100,100);
	backgbl.setConstraints(style2TextR_Color,backgbc);
	buildConstraints(backgbc,2,0,1,1,100,100);
	backgbl.setConstraints(g_label,backgbc);
	buildConstraints(backgbc,3,0,1,1,100,100);
	backgbl.setConstraints(style2TextG_Color,backgbc);
	buildConstraints(backgbc,4,0,1,1,100,100);
	backgbl.setConstraints(b_label,backgbc);
	buildConstraints(backgbc,5,0,1,1,100,100);
	backgbl.setConstraints(style2TextB_Color,backgbc);
	buildConstraints(backgbc,6,0,1,1,100,100);
	backgbl.setConstraints(backSquare,backgbc);

	return style2TextPanel;

    }

    private JPanel makeStyle2BackColorPanel(RH_HighlightStyle style, Color subPanelsBackColor, Font conceptsFont, Font optionsFont, Color titleColor) {
    
	JPanel style2BackPanel=new JPanel();
	//style2BackPanel.setBorder(new TitledBorder(new TitledBorder(""),"Back Color",TitledBorder.LEFT,TitledBorder.TOP,borderFont,topicsTitleColor));
	style2BackPanel.setBackground(subPanelsBackColor);
	GridBagLayout backgbl = new GridBagLayout();
	GridBagConstraints backgbc = new GridBagConstraints();
	style2BackPanel.setLayout(backgbl);
	style2BackColor=profile.getOverviewWindowColor();

	JLabel r_label=new JLabel("BackColor R:",JLabel.RIGHT);
	r_label.setBackground(subPanelsBackColor);
	r_label.setForeground(topicsTitleColor);
	r_label.setFont(optionsFont);
	style2BackPanel.add(r_label);
	style2BackR_Color=new JTextField(style.getRed()+"",3);
	style2BackR_Color.setFont(conceptsFont);
	style2BackR_Color.addActionListener(this);
	style2BackR_Color.setFont(conceptsFont);
	style2BackR_Color.setSelectionColor(Color.darkGray);
	style2BackR_Color.setSelectedTextColor(topicsTitleColor);
	style2BackR_Color.setCaretColor(Color.blue);
	style2BackR_Color.setToolTipText("Red color value");
	style2BackR_Color.setBackground(Color.lightGray);
	style2BackR_Color.setForeground(Color.black);
	style2BackPanel.add(style2BackR_Color);

	JLabel g_label=new JLabel("G:",JLabel.RIGHT);
	g_label.setBackground(subPanelsBackColor);
	g_label.setForeground(topicsTitleColor);
	g_label.setFont(optionsFont);
	style2BackPanel.add(g_label);
	style2BackG_Color=new JTextField(style.getGreen()+"",3);
	style2BackG_Color.setFont(conceptsFont);
	style2BackG_Color.addActionListener(this);
	style2BackG_Color.setFont(conceptsFont);
	style2BackG_Color.setSelectionColor(Color.darkGray);
	style2BackG_Color.setSelectedTextColor(topicsTitleColor);
	style2BackG_Color.setCaretColor(Color.blue);
	style2BackG_Color.setToolTipText("Green color value");
	style2BackG_Color.setBackground(Color.lightGray);
	style2BackG_Color.setForeground(Color.black);
	style2BackPanel.add(style2BackG_Color);

	JLabel b_label=new JLabel("B:",JLabel.RIGHT);
	b_label.setBackground(subPanelsBackColor);
	b_label.setForeground(topicsTitleColor);
	b_label.setFont(optionsFont);
	style2BackPanel.add(b_label);
	style2BackB_Color=new JTextField(style.getBlue()+"",3);
	style2BackB_Color.setFont(conceptsFont);
	style2BackB_Color.addActionListener(this);
	style2BackB_Color.setFont(conceptsFont);
	style2BackB_Color.setSelectionColor(Color.darkGray);
	style2BackB_Color.setSelectedTextColor(topicsTitleColor);
	style2BackB_Color.setCaretColor(Color.blue);
	style2BackB_Color.setToolTipText("Blue color value");
	style2BackB_Color.setBackground(Color.lightGray);
	style2BackB_Color.setForeground(Color.black);
	style2BackPanel.add(style2BackB_Color);

	Color styleColor=new Color(style.getRed(),style.getGreen(),style.getBlue());
	style2BackSquare=new JButton("");
	style2BackSquare.setBackground(styleColor);
	style2BackSquare.setForeground(styleColor);
	style2BackSquare.setEnabled(false);
	style2BackPanel.add(style2BackSquare);

	int gridx=0;
	backgbc.anchor = GridBagConstraints.WEST;
	backgbc.fill = GridBagConstraints.BOTH;
	backgbc.insets = new Insets(2,1,2,1);
	buildConstraints(backgbc,0,0,1,1,100,100);
	backgbl.setConstraints(r_label,backgbc);
	buildConstraints(backgbc,1,0,1,1,100,100);
	backgbl.setConstraints(style2BackR_Color,backgbc);
	buildConstraints(backgbc,2,0,1,1,100,100);
	backgbl.setConstraints(g_label,backgbc);
	buildConstraints(backgbc,3,0,1,1,100,100);
	backgbl.setConstraints(style2BackG_Color,backgbc);
	buildConstraints(backgbc,4,0,1,1,100,100);
	backgbl.setConstraints(b_label,backgbc);
	buildConstraints(backgbc,5,0,1,1,100,100);
	backgbl.setConstraints(style2BackB_Color,backgbc);
	buildConstraints(backgbc,6,0,1,1,100,100);
	backgbl.setConstraints(backSquare,backgbc);

	return style2BackPanel;

    }

    private JPanel makeStyle3TextColorPanel(RH_HighlightStyle style, Color subPanelsBackColor, Font conceptsFont, Font optionsFont, Color titleColor) {
    
	JPanel style3TextPanel=new JPanel();
	//style3TextPanel.setBorder(new TitledBorder(new TitledBorder(""),"Text Color",TitledBorder.LEFT,TitledBorder.TOP,borderFont,topicsTitleColor));
	style3TextPanel.setBackground(subPanelsBackColor);
	GridBagLayout backgbl = new GridBagLayout();
	GridBagConstraints backgbc = new GridBagConstraints();
	style3TextPanel.setLayout(backgbl);
	style3TextColor=profile.getOverviewWindowColor();

	JLabel r_label=new JLabel("TextColor R:",JLabel.RIGHT);
	r_label.setBackground(subPanelsBackColor);
	r_label.setForeground(topicsTitleColor);
	r_label.setFont(optionsFont);
	style3TextPanel.add(r_label);
	style3TextR_Color=new JTextField(style.getForeRed()+"",3);
	style3TextR_Color.setFont(conceptsFont);
	style3TextR_Color.addActionListener(this);
	style3TextR_Color.setFont(conceptsFont);
	style3TextR_Color.setSelectionColor(Color.darkGray);
	style3TextR_Color.setSelectedTextColor(topicsTitleColor);
	style3TextR_Color.setCaretColor(Color.blue);
	style3TextR_Color.setToolTipText("Red color value");
	style3TextR_Color.setBackground(Color.lightGray);
	style3TextR_Color.setForeground(Color.black);
	style3TextPanel.add(style3TextR_Color);

	JLabel g_label=new JLabel("G:",JLabel.RIGHT);
	g_label.setBackground(subPanelsBackColor);
	g_label.setForeground(topicsTitleColor);
	g_label.setFont(optionsFont);
	style3TextPanel.add(g_label);
	style3TextG_Color=new JTextField(style.getForeGreen()+"",3);
	style3TextG_Color.setFont(conceptsFont);
	style3TextG_Color.addActionListener(this);
	style3TextG_Color.setFont(conceptsFont);
	style3TextG_Color.setSelectionColor(Color.darkGray);
	style3TextG_Color.setSelectedTextColor(topicsTitleColor);
	style3TextG_Color.setCaretColor(Color.blue);
	style3TextG_Color.setToolTipText("Green color value");
	style3TextG_Color.setBackground(Color.lightGray);
	style3TextG_Color.setForeground(Color.black);
	style3TextPanel.add(style3TextG_Color);

	JLabel b_label=new JLabel("B:",JLabel.RIGHT);
	b_label.setBackground(subPanelsBackColor);
	b_label.setForeground(topicsTitleColor);
	b_label.setFont(optionsFont);
	style3TextPanel.add(b_label);
	style3TextB_Color=new JTextField(style.getForeBlue()+"",3);
	style3TextB_Color.setFont(conceptsFont);
	style3TextB_Color.addActionListener(this);
	style3TextB_Color.setFont(conceptsFont);
	style3TextB_Color.setSelectionColor(Color.darkGray);
	style3TextB_Color.setSelectedTextColor(topicsTitleColor);
	style3TextB_Color.setCaretColor(Color.blue);
	style3TextB_Color.setToolTipText("Blue color value");
	style3TextB_Color.setBackground(Color.lightGray);
	style3TextB_Color.setForeground(Color.black);
	style3TextPanel.add(style3TextB_Color);

	Color styleColor=new Color(style.getForeRed(),style.getForeGreen(),style.getForeBlue());
	style3TextSquare=new JButton("");
	style3TextSquare.setBackground(styleColor);
	style3TextSquare.setForeground(styleColor);
	style3TextSquare.setEnabled(false);
	style3TextPanel.add(style3TextSquare);

	int gridx=0;
	backgbc.anchor = GridBagConstraints.WEST;
	backgbc.fill = GridBagConstraints.BOTH;
	backgbc.insets = new Insets(2,1,2,1);
	buildConstraints(backgbc,0,0,1,1,100,100);
	backgbl.setConstraints(r_label,backgbc);
	buildConstraints(backgbc,1,0,1,1,100,100);
	backgbl.setConstraints(style3TextR_Color,backgbc);
	buildConstraints(backgbc,2,0,1,1,100,100);
	backgbl.setConstraints(g_label,backgbc);
	buildConstraints(backgbc,3,0,1,1,100,100);
	backgbl.setConstraints(style3TextG_Color,backgbc);
	buildConstraints(backgbc,4,0,1,1,100,100);
	backgbl.setConstraints(b_label,backgbc);
	buildConstraints(backgbc,5,0,1,1,100,100);
	backgbl.setConstraints(style3TextB_Color,backgbc);
	buildConstraints(backgbc,6,0,1,1,100,100);
	backgbl.setConstraints(backSquare,backgbc);

	return style3TextPanel;

    }

    private JPanel makeStyle3BackColorPanel(RH_HighlightStyle style, Color subPanelsBackColor, Font conceptsFont, Font optionsFont, Color titleColor) {
    
	JPanel style3BackPanel=new JPanel();
	//style3BackPanel.setBorder(new TitledBorder(new TitledBorder(""),"Back Color",TitledBorder.LEFT,TitledBorder.TOP,borderFont,topicsTitleColor));
	style3BackPanel.setBackground(subPanelsBackColor);
	GridBagLayout backgbl = new GridBagLayout();
	GridBagConstraints backgbc = new GridBagConstraints();
	style3BackPanel.setLayout(backgbl);
	style3BackColor=profile.getOverviewWindowColor();

	JLabel r_label=new JLabel("BackColor R:",JLabel.RIGHT);
	r_label.setBackground(subPanelsBackColor);
	r_label.setForeground(topicsTitleColor);
	r_label.setFont(optionsFont);
	style3BackPanel.add(r_label);
	style3BackR_Color=new JTextField(style.getRed()+"",3);
	style3BackR_Color.setFont(conceptsFont);
	style3BackR_Color.addActionListener(this);
	style3BackR_Color.setFont(conceptsFont);
	style3BackR_Color.setSelectionColor(Color.darkGray);
	style3BackR_Color.setSelectedTextColor(topicsTitleColor);
	style3BackR_Color.setCaretColor(Color.blue);
	style3BackR_Color.setToolTipText("Red color value");
	style3BackR_Color.setBackground(Color.lightGray);
	style3BackR_Color.setForeground(Color.black);
	style3BackPanel.add(style3BackR_Color);

	JLabel g_label=new JLabel("G:",JLabel.RIGHT);
	g_label.setBackground(subPanelsBackColor);
	g_label.setForeground(topicsTitleColor);
	g_label.setFont(optionsFont);
	style3BackPanel.add(g_label);
	style3BackG_Color=new JTextField(style.getGreen()+"",3);
	style3BackG_Color.setFont(conceptsFont);
	style3BackG_Color.addActionListener(this);
	style3BackG_Color.setFont(conceptsFont);
	style3BackG_Color.setSelectionColor(Color.darkGray);
	style3BackG_Color.setSelectedTextColor(topicsTitleColor);
	style3BackG_Color.setCaretColor(Color.blue);
	style3BackG_Color.setToolTipText("Green color value");
	style3BackG_Color.setBackground(Color.lightGray);
	style3BackG_Color.setForeground(Color.black);
	style3BackPanel.add(style3BackG_Color);

	JLabel b_label=new JLabel("B:",JLabel.RIGHT);
	b_label.setBackground(subPanelsBackColor);
	b_label.setForeground(topicsTitleColor);
	b_label.setFont(optionsFont);
	style3BackPanel.add(b_label);
	style3BackB_Color=new JTextField(style.getBlue()+"",3);
	style3BackB_Color.setFont(conceptsFont);
	style3BackB_Color.addActionListener(this);
	style3BackB_Color.setFont(conceptsFont);
	style3BackB_Color.setSelectionColor(Color.darkGray);
	style3BackB_Color.setSelectedTextColor(topicsTitleColor);
	style3BackB_Color.setCaretColor(Color.blue);
	style3BackB_Color.setToolTipText("Blue color value");
	style3BackB_Color.setBackground(Color.lightGray);
	style3BackB_Color.setForeground(Color.black);
	style3BackPanel.add(style3BackB_Color);

	Color styleColor=new Color(style.getRed(),style.getGreen(),style.getBlue());
	style3BackSquare=new JButton("");
	style3BackSquare.setBackground(styleColor);
	style3BackSquare.setForeground(styleColor);
	style3BackSquare.setEnabled(false);
	style3BackPanel.add(style3BackSquare);

	int gridx=0;
	backgbc.anchor = GridBagConstraints.WEST;
	backgbc.fill = GridBagConstraints.BOTH;
	backgbc.insets = new Insets(2,1,2,1);
	buildConstraints(backgbc,0,0,1,1,100,100);
	backgbl.setConstraints(r_label,backgbc);
	buildConstraints(backgbc,1,0,1,1,100,100);
	backgbl.setConstraints(style3BackR_Color,backgbc);
	buildConstraints(backgbc,2,0,1,1,100,100);
	backgbl.setConstraints(g_label,backgbc);
	buildConstraints(backgbc,3,0,1,1,100,100);
	backgbl.setConstraints(style3BackG_Color,backgbc);
	buildConstraints(backgbc,4,0,1,1,100,100);
	backgbl.setConstraints(b_label,backgbc);
	buildConstraints(backgbc,5,0,1,1,100,100);
	backgbl.setConstraints(style3BackB_Color,backgbc);
	buildConstraints(backgbc,6,0,1,1,100,100);
	backgbl.setConstraints(backSquare,backgbc);

	return style3BackPanel;

    }

    private void makeEditOptions(Font optionsFont, Font conceptsFont) {
	editOptionsPanel=new JPanel();	
	editOptionsPanel.setBackground(backColor);
	editOptionsPanel.setLayout(new BorderLayout());

	JPanel editOptionsButtonPanel=new JPanel();
	editOptionsButtonPanel.setLayout(new GridLayout(1,3));
	editOptionsButtonPanel.setBackground(backColor);
	editOptionsSave=new JButton("Save Profile Changes");
	editOptionsSave.setBackground(backColor);
	editOptionsSave.setForeground(saveButtonsColor);
	editOptionsSave.setFont(conceptsFont);
	editOptionsSave.addActionListener(this);
	editOptionsButtonPanel.add(editOptionsSave);
	
	editOptionsNoSave=new JButton("This session only (no save)");
	editOptionsNoSave.setBackground(backColor);
	editOptionsNoSave.setForeground(saveButtonsColor);
	editOptionsNoSave.setFont(conceptsFont);
	editOptionsNoSave.addActionListener(this);
	editOptionsButtonPanel.add(editOptionsNoSave);
	
	editOptionsCancel=new JButton("Disregard all changes");
	editOptionsCancel.setBackground(backColor);
	editOptionsCancel.setForeground(saveButtonsColor);
	editOptionsCancel.setFont(conceptsFont);
	editOptionsCancel.addActionListener(this);
	editOptionsButtonPanel.add(editOptionsCancel);
	
	editOptionsSave.setEnabled(false);
	editOptionsNoSave.setEnabled(false);
	editOptionsCancel.setEnabled(false);
	editOptionsPanel.add(editOptionsButtonPanel,BorderLayout.SOUTH);
	
	//**----------------Personal
	Color holdColor=backColor;
	backColor=new Color(110,110,110);
	JPanel personalPanel=new JPanel();
	GridBagLayout personalgbl = new GridBagLayout();
	GridBagConstraints personalgbc = new GridBagConstraints();
	personalPanel.setLayout(personalgbl);
	personalPanel.setBorder(new TitledBorder(new TitledBorder(""),"Personal Info",TitledBorder.LEFT,TitledBorder.TOP,borderFont,Color.yellow));
	personalPanel.setBackground(backColor);
	Color personalBackColor=Color.lightGray, personalForeColor=Color.black, personalSelectionColor=Color.gray,
	    personalSelectedTextColor=topicsTitleColor, personalCaretColor=Color.blue;

	JPanel fnamePanel=new JPanel();
	fnamePanel.setLayout(new BorderLayout());
	fnamePanel.setBackground(backColor);
	JLabel fname=new JLabel("First Name:",JLabel.RIGHT);
	fname.setBackground(backColor);
	fname.setForeground(topicsTitleColor);
	fname.setFont(optionsFont);
	fnamePanel.add(fname,BorderLayout.WEST);
	readerFName=new JTextField(profile.getUserFirstName(),10);
	readerFName.setBackground(personalBackColor);
	readerFName.setForeground(personalForeColor);
	readerFName.setSelectionColor(personalSelectionColor);
	readerFName.setSelectedTextColor(personalSelectedTextColor);
	readerFName.setCaretColor(personalCaretColor);
	readerFName.addActionListener(this);
	readerFName.setFont(conceptsFont);
	readerFName.setMargin(new Insets(2,5,2,2));
	fnamePanel.add(readerFName,BorderLayout.EAST);
	personalPanel.add(fnamePanel);

	JPanel lnamePanel=new JPanel();
	lnamePanel.setLayout(new BorderLayout());
	lnamePanel.setBackground(backColor);
	JLabel lname=new JLabel("Last Name:",JLabel.RIGHT);
	lname.setBackground(backColor);
	lname.setForeground(topicsTitleColor);
	lname.setFont(optionsFont);
	lnamePanel.add(lname,BorderLayout.WEST);
	readerLName=new JTextField(profile.getUserLastName(),10);
	readerLName.setBackground(personalBackColor);
	readerLName.setForeground(personalForeColor);
	readerLName.setSelectionColor(personalSelectionColor);
	readerLName.setSelectedTextColor(personalSelectedTextColor);
	readerLName.setCaretColor(personalCaretColor);
	readerLName.addActionListener(this);
	readerLName.setMargin(new Insets(2,5,2,2));
	readerLName.setFont(conceptsFont);
	lnamePanel.add(readerLName,BorderLayout.EAST);
	personalPanel.add(lnamePanel);

	JPanel unamePanel=new JPanel();
	unamePanel.setLayout(new BorderLayout());
	unamePanel.setBackground(backColor);
	JLabel acct=new JLabel("User Account:",JLabel.RIGHT);
	acct.setBackground(backColor);
	acct.setForeground(topicsTitleColor);
	acct.setFont(optionsFont);
	unamePanel.add(acct,BorderLayout.WEST);
	readerUName=new JTextField(profile.getUserAccountName(),10);
	readerUName.setBackground(personalBackColor);
	readerUName.setForeground(personalForeColor);
	readerUName.setSelectionColor(personalSelectionColor);
	readerUName.setSelectedTextColor(personalSelectedTextColor);
	readerUName.setCaretColor(personalCaretColor);
	readerUName.addActionListener(this);
	readerUName.setMargin(new Insets(2,5,2,2));
	readerUName.setFont(conceptsFont);
	unamePanel.add(readerUName,BorderLayout.EAST);
	personalPanel.add(unamePanel);

	personalgbc.anchor = GridBagConstraints.WEST;
	personalgbc.fill = GridBagConstraints.NONE;
	buildConstraints(personalgbc,0,0,1,1,100,100);
	personalgbl.setConstraints(fnamePanel,personalgbc);
	buildConstraints(personalgbc,1,0,1,1,100,100);
	personalgbl.setConstraints(lnamePanel,personalgbc);
	buildConstraints(personalgbc,2,0,1,1,100,100);
	personalgbl.setConstraints(unamePanel,personalgbc);

	editOptionsPanel.add(personalPanel,BorderLayout.NORTH);

	JPanel subOptionsPanel=new JPanel();
	subOptionsPanel.setLayout(new GridLayout(2,1));
	subOptionsPanel.setBackground(backColor);
	subOptionsPanel.setBorder(new TitledBorder(new TitledBorder(""),"Properties",TitledBorder.LEFT,TitledBorder.TOP,borderFont,Color.yellow));
	editOptionsPanel.add(subOptionsPanel,BorderLayout.CENTER);

	JPanel systemPanel=makeSystemPanel(optionsFont,conceptsFont);
	JPanel settingsPanel=makeSettingsPanel(optionsFont,conceptsFont);
	subOptionsPanel.add(systemPanel);
	subOptionsPanel.add(settingsPanel);
	backColor=holdColor;
    }
    
    private JPanel makeSettingsPanel(Font optionsFont, Font conceptsFont) {
	JPanel settingsPanel=new JPanel();
	settingsPanel.setLayout(new BorderLayout());
	settingsPanel.setBorder(new TitledBorder(new TitledBorder(""),"",TitledBorder.LEFT,TitledBorder.TOP,borderFont,topicsTitleColor));
	Color holdColor=backColor;
	//backColor=new Color(100,100,100);
	settingsPanel.setBackground(backColor);

	JPanel dimPanel=new JPanel();
	dimPanel.setLayout(new GridLayout(1,4,10,8));
	dimPanel.setBackground(backColor);

	JPanel widthPanel=new JPanel();
	widthPanel.setLayout(new BorderLayout());
	widthPanel.setBackground(backColor);
	windowwidth=new JTextField(profile.getPreferredWidth()+"",3);
	windowwidth.setBackground(Color.lightGray);
	windowwidth.setForeground(Color.black);
	windowwidth.setFont(conceptsFont);
	windowwidth.addActionListener(this);
	windowwidth.setToolTipText("Width of the browser at startup");
	windowwidth.setSelectionColor(Color.darkGray);
	windowwidth.setSelectedTextColor(topicsTitleColor);
	windowwidth.setCaretColor(Color.blue);
	windowwidth.setMargin(new Insets(2,5,2,2));
	JLabel windowwidthlabel=new JLabel("Width:",JLabel.RIGHT);
	windowwidthlabel.setBackground(backColor);
	windowwidthlabel.setForeground(topicsTitleColor);
	windowwidthlabel.setFont(optionsFont);
	widthPanel.add(windowwidth,BorderLayout.CENTER);
	widthPanel.add(windowwidthlabel,BorderLayout.WEST);

	JPanel heightPanel=new JPanel();
	heightPanel.setLayout(new BorderLayout());
	heightPanel.setBackground(backColor);
	windowheight=new JTextField(profile.getPreferredHeight()+"",3);
	windowheight.setBackground(Color.lightGray);
	windowheight.setForeground(Color.black);
	windowheight.setFont(conceptsFont);
	windowheight.addActionListener(this);
	windowheight.setToolTipText("Height of the browser at startup");
	windowheight.setSelectionColor(Color.darkGray);
	windowheight.setSelectedTextColor(topicsTitleColor);
	windowheight.setCaretColor(Color.blue);
	windowheight.setMargin(new Insets(2,5,2,2));
	JLabel windowheightlabel=new JLabel("Height:",JLabel.RIGHT);
	windowheightlabel.setBackground(backColor);
	windowheightlabel.setForeground(topicsTitleColor);
	windowheightlabel.setFont(optionsFont);
	heightPanel.add(windowheight,BorderLayout.CENTER);
	heightPanel.add(windowheightlabel,BorderLayout.WEST);

	JPanel xPanel=new JPanel();
	xPanel.setLayout(new BorderLayout());
	xPanel.setBackground(backColor);
	windowx=new JTextField(profile.getPreferredX()+"",2);
	windowx.setBackground(Color.lightGray);
	windowx.setForeground(Color.black);
	windowx.setFont(conceptsFont);
	windowx.addActionListener(this);
	windowx.setToolTipText("X location on the screen");
	windowx.setSelectionColor(Color.darkGray);
	windowx.setSelectedTextColor(topicsTitleColor);
	windowx.setCaretColor(Color.blue);
	windowx.setMargin(new Insets(2,5,2,2));
	JLabel windowxlabel=new JLabel("X:",JLabel.RIGHT);
	windowxlabel.setBackground(backColor);
	windowxlabel.setForeground(topicsTitleColor);
	windowxlabel.setFont(optionsFont);
	xPanel.add(windowx,BorderLayout.CENTER);
	xPanel.add(windowxlabel,BorderLayout.WEST);

	JPanel yPanel=new JPanel();
	yPanel.setLayout(new BorderLayout());
	yPanel.setBackground(backColor);
	windowy=new JTextField(profile.getPreferredY()+"",2);
	windowy.setBackground(Color.lightGray);
	windowy.setForeground(Color.black);
	windowy.setFont(conceptsFont);
	windowy.addActionListener(this);
	windowy.setToolTipText("Y location on the screen");
	windowy.setSelectionColor(Color.darkGray);
	windowy.setSelectedTextColor(topicsTitleColor);
	windowy.setCaretColor(Color.blue);
	windowy.setMargin(new Insets(2,5,2,2));
	JLabel windowylabel=new JLabel("Y:",JLabel.RIGHT);
	windowylabel.setBackground(backColor);
	windowylabel.setForeground(topicsTitleColor);
	windowylabel.setFont(optionsFont);
	yPanel.add(windowy,BorderLayout.CENTER);
	yPanel.add(windowylabel,BorderLayout.WEST);
	dimPanel.add(widthPanel);
	dimPanel.add(heightPanel);
	dimPanel.add(xPanel);
	dimPanel.add(yPanel);

	JPanel fontinfoPanel=new JPanel();
	fontinfoPanel.setLayout(new GridLayout(1,1));
	fontinfoPanel.setBackground(backColor);

	JPanel fontPanel=new JPanel();
	fontPanel.setBorder(new TitledBorder(new TitledBorder(""),"Text Font",TitledBorder.LEFT,TitledBorder.TOP,borderFont,topicsTitleColor));
	fontPanel.setLayout(new BorderLayout());
	fontPanel.setBackground(backColor);
	textfont=new JList(fontData);
	textfont.setBackground(Color.lightGray);
	textfont.setForeground(Color.black);
	textfont.setFont(conceptsFont);
	textfont.addListSelectionListener(this);
	textfont.setToolTipText("Set font for document text");
	int idx=memberReturnIdx(profile.getDocumentFontName(),fontData);
	if (idx>=0) textfont.setSelectedIndex(idx);
	JScrollPane textfontPane = new JScrollPane(textfont);
	textfontPane.setForeground(conceptsColor);
	textfontPane.setBackground(Color.darkGray);
	fontPanel.add(textfontPane,BorderLayout.CENTER);

	JPanel fontsizePanel=new JPanel();
	fontsizePanel.setLayout(new BorderLayout());
	fontsizePanel.setBackground(backColor);
	textfontsize=new JTextField(profile.getDocumentFontSize()+"",5);
	textfontsize.setBackground(Color.lightGray);
	textfontsize.setForeground(Color.black);
	textfontsize.setFont(conceptsFont);
	textfontsize.addActionListener(this);
	textfontsize.setToolTipText("font size, e.g. 12 = 12pt");
	textfontsize.setSelectionColor(Color.darkGray);
	textfontsize.setSelectedTextColor(topicsTitleColor);
	textfontsize.setCaretColor(Color.blue);
	textfontsize.setMargin(new Insets(2,5,2,2));
	JLabel textfontlabel=new JLabel("Size:",JLabel.RIGHT);
	textfontlabel.setBackground(backColor);
	textfontlabel.setForeground(topicsTitleColor);
	textfontlabel.setFont(optionsFont);
	fontsizePanel.add(textfontsize,BorderLayout.CENTER);
	fontsizePanel.add(textfontlabel,BorderLayout.WEST);
	fontPanel.add(fontsizePanel,BorderLayout.NORTH);

	JPanel ifontPanel=new JPanel();
	ifontPanel.setBorder(new TitledBorder(new TitledBorder(""),"GUI Font",TitledBorder.LEFT,TitledBorder.TOP,borderFont,topicsTitleColor));
	ifontPanel.setLayout(new BorderLayout());
	ifontPanel.setBackground(backColor);
	guifont=new JList(fontData);
	guifont.setBackground(Color.lightGray);
	guifont.setForeground(Color.black);
	guifont.setFont(conceptsFont);
	guifont.addListSelectionListener(this);
	guifont.setToolTipText("Set font for user interface");
	idx=memberReturnIdx(profile.getLocationFontName(),fontData);
	if (idx>=0) guifont.setSelectedIndex(idx);
	JScrollPane guifontPane = new JScrollPane(guifont);
	guifontPane.setForeground(conceptsColor);
	guifontPane.setBackground(Color.darkGray);
	ifontPanel.add(guifontPane,BorderLayout.CENTER);

	JPanel ifontsizePanel=new JPanel();
	ifontsizePanel.setLayout(new BorderLayout());
	ifontsizePanel.setBackground(backColor);
	guifontsize=new JTextField(profile.getLocationFontSize()+"",5);
	guifontsize.setBackground(Color.lightGray);
	guifontsize.setForeground(Color.black);
	guifontsize.setFont(conceptsFont);
	guifontsize.addActionListener(this);
	guifontsize.setSelectionColor(Color.darkGray);
	guifontsize.setSelectedTextColor(topicsTitleColor);
	guifontsize.setCaretColor(Color.blue);
	guifontsize.setMargin(new Insets(2,5,2,2));
	guifontsize.setToolTipText("font size, e.g. 12 = 12pt");
	JLabel guifontlabel=new JLabel("Size:",JLabel.RIGHT);
	guifontlabel.setBackground(backColor);
	guifontlabel.setForeground(topicsTitleColor);
	guifontlabel.setFont(optionsFont);
	ifontsizePanel.add(guifontsize,BorderLayout.CENTER);
	ifontsizePanel.add(guifontlabel,BorderLayout.WEST);
	ifontPanel.add(ifontsizePanel,BorderLayout.NORTH);

	JPanel cachePanel=new JPanel();
	cachePanel.setLayout(new BorderLayout());
	cachePanel.setBackground(backColor);
	JPanel cachesubPanel=new JPanel();
	cachesubPanel.setLayout(new BorderLayout());
	cachesubPanel.setBackground(backColor);
	cachesize=new JTextField(profile.getDocumentCacheSize()+"",10);
	cachesize.setBackground(Color.lightGray);
	cachesize.setForeground(Color.black);
	cachesize.setFont(conceptsFont);
	cachesize.addActionListener(this);
	cachesize.setSelectionColor(Color.darkGray);
	cachesize.setSelectedTextColor(topicsTitleColor);
	cachesize.setCaretColor(Color.blue);
	cachesize.setMargin(new Insets(2,5,2,2));
	cachesize.setToolTipText("Size of document cache, e.g. 1000000 = 1MB which is the minimum");
	JLabel cachesizelabel=new JLabel("Cache Size (in KBs):"); //JLabel.RIGHT
	cachesizelabel.setBackground(backColor);
	cachesizelabel.setForeground(topicsTitleColor);
	cachesizelabel.setFont(optionsFont);
	cachesubPanel.add(cachesize,BorderLayout.CENTER);
	cachesubPanel.add(cachesizelabel,BorderLayout.WEST);

	activecache=new JCheckBox("Caching");
	activecache.setFont(optionsFont);
	activecache.setForeground(topicsTitleColor);
	activecache.setBackground(backColor);
	activecache.setMargin(new Insets(2,2,2,2));
	activecache.setToolTipText("Check this box if you want document caching to be active(ON)");
	activecache.addActionListener(this);
	activecache.setSelected(profile.getUseCacheDocuments());

	largemeters=new JCheckBox("LargeMeters");
	largemeters.setFont(optionsFont);
	largemeters.setForeground(topicsTitleColor);
	largemeters.setBackground(backColor);
	largemeters.setMargin(new Insets(2,2,2,2));
	largemeters.setToolTipText("Check this box if you want larger concepts meters & labels");
	largemeters.addActionListener(this);
	largemeters.setSelected(profile.getLargeMeters());
	
	cachePanel.add(cachesubPanel,BorderLayout.EAST);
	cachePanel.add(activecache,BorderLayout.CENTER);
	cachePanel.add(largemeters,BorderLayout.WEST);


	JPanel miscchecksPanel= new JPanel();
	miscchecksPanel.setLayout(new GridLayout(2,2));
	miscchecksPanel.setBackground(backColor);
	
	animateLogo=new JCheckBox("Animate Logo");
	animateLogo.setToolTipText("Causes logo to animate while processing a document");
	animateLogo.setFont(optionsFont);
	animateLogo.setBackground(backColor);
	animateLogo.setForeground(topicsTitleColor);
	animateLogo.setMargin(new Insets(2,2,2,2));
	animateLogo.addActionListener(this);
	if (profile.getAnimateLogo()) animateLogo.setSelected(true);
	else animateLogo.setSelected(false);
	autoloadHome=new JCheckBox("Load HomePage");
	autoloadHome.setToolTipText("If true, loads user specified home page at startup");
	autoloadHome.setFont(optionsFont);
	autoloadHome.setBackground(backColor);
	autoloadHome.setForeground(topicsTitleColor);
	autoloadHome.setMargin(new Insets(2,2,2,2));
	autoloadHome.addActionListener(this);
	//** Making this inactive because i don't really want people to have the option of no home page
	autoloadHome.setEnabled(false);
	if (profile.getAutoLoadHomeURL()) autoloadHome.setSelected(true);
	else autoloadHome.setSelected(false);
	populateConcepts=new JCheckBox("Populate Concepts");
	populateConcepts.setToolTipText("Populate concepts when current document has be previously annotated");
	populateConcepts.setFont(optionsFont);
	populateConcepts.setBackground(backColor);
	populateConcepts.setForeground(topicsTitleColor);
	populateConcepts.setMargin(new Insets(2,2,2,2));
	populateConcepts.addActionListener(this);
	if (profile.getPopulateConcepts()) populateConcepts.setSelected(true);
	else populateConcepts.setSelected(false);
	imageScaling=new JCheckBox("Better Images");
	imageScaling.setToolTipText("Uses better image scaling in the Thumbar");
	imageScaling.setFont(optionsFont);
	imageScaling.setBackground(backColor);
	imageScaling.setForeground(topicsTitleColor);
	imageScaling.setMargin(new Insets(2,2,2,2));
	imageScaling.addActionListener(this);
	if (profile.getUseBetterImageScalingMethod()) imageScaling.setSelected(true);
	else imageScaling.setSelected(false);
	miscchecksPanel.add(animateLogo);
	miscchecksPanel.add(autoloadHome);
	miscchecksPanel.add(populateConcepts);
	miscchecksPanel.add(imageScaling);

	JPanel simsensPanel=new JPanel();
	simsensPanel.setLayout(new GridLayout(1,2,10,10));
	simsensPanel.setBackground(backColor);

	JPanel simPanel=new JPanel();
	simPanel.setLayout(new BorderLayout());
	simPanel.setBackground(backColor);
	simThreshold=new JTextField(profile.getSimilarityThreshold()+"",8);
	simThreshold.setBackground(Color.lightGray);
	simThreshold.setForeground(Color.black);
	simThreshold.setFont(conceptsFont);
	simThreshold.addActionListener(this);
	simThreshold.setSelectionColor(Color.darkGray);
	simThreshold.setSelectedTextColor(topicsTitleColor);
	simThreshold.setCaretColor(Color.blue);
	simThreshold.setMargin(new Insets(2,5,2,2));
	simThreshold.setToolTipText("Value used in similarity function as cut off, range=0-100");
	JLabel simlabel=new JLabel("Similarity Threshold:",JLabel.RIGHT);
	simlabel.setBackground(backColor);
	simlabel.setForeground(topicsTitleColor);
	simlabel.setFont(optionsFont);
	simPanel.add(simThreshold,BorderLayout.CENTER);
	simPanel.add(simlabel,BorderLayout.WEST);

	JPanel sensPanel=new JPanel();
	sensPanel.setLayout(new BorderLayout());
	sensPanel.setBackground(backColor);
	sensThreshold=new JTextField(profile.getSensitivitySetting()+"",8);
	sensThreshold.setBackground(Color.lightGray);
	sensThreshold.setForeground(Color.black);
	sensThreshold.setFont(conceptsFont);
	sensThreshold.addActionListener(this);
	sensThreshold.setSelectionColor(Color.darkGray);
	sensThreshold.setSelectedTextColor(topicsTitleColor);
	sensThreshold.setCaretColor(Color.blue);
	sensThreshold.setMargin(new Insets(2,5,2,2));
	sensThreshold.setToolTipText("");
	JLabel senslabel=new JLabel("Default Sensitivity:",JLabel.RIGHT);
	senslabel.setBackground(backColor);
	senslabel.setForeground(topicsTitleColor);
	senslabel.setFont(optionsFont);
	sensPanel.add(sensThreshold,BorderLayout.CENTER);
	sensPanel.add(senslabel,BorderLayout.WEST);
	simsensPanel.add(sensPanel);
	simsensPanel.add(simPanel);

	JPanel miscPanel=new JPanel();
	miscPanel.setLayout(new BorderLayout());
	miscPanel.setBackground(backColor);
	miscPanel.add(dimPanel,BorderLayout.NORTH);

	JPanel miscsubPanel=new JPanel();
	miscsubPanel.setLayout(new BorderLayout());
	miscsubPanel.setBackground(backColor);
	miscPanel.add(miscsubPanel,BorderLayout.CENTER);

	miscsubPanel.add(cachePanel,BorderLayout.NORTH);
	miscsubPanel.add(miscchecksPanel,BorderLayout.CENTER);
	miscsubPanel.add(simsensPanel,BorderLayout.SOUTH);

	fontinfoPanel.add(fontPanel);
	fontinfoPanel.add(ifontPanel);
	settingsPanel.setBorder(new TitledBorder(new TitledBorder(""),"",TitledBorder.LEFT,TitledBorder.TOP,borderFont,topicsTitleColor));
	settingsPanel.add(miscPanel,BorderLayout.EAST);
	settingsPanel.add(fontinfoPanel,BorderLayout.WEST);

	backColor=holdColor;
	return settingsPanel;
    }
	private JPanel makeSystemPanel(Font optionsFont, Font conceptsFont) {
	    JPanel systemPanel=new JPanel();
	    systemPanel.setLayout(new BorderLayout());
	    systemPanel.setBackground(backColor);
	    Color holdColor=backColor;
	    //backColor=new Color(100,100,100);
	    
	    JPanel locationsPanel=new JPanel();
	    locationsPanel.setLayout(new GridLayout(4,1));
	    //**Home Path
	    JPanel pathPanel=new JPanel();
	    pathPanel.setLayout(new BorderLayout());
	    pathPanel.setBackground(backColor);
	    homepath=new JTextField(profile.getPath(),30);
	    homepath.setBackground(Color.lightGray);
	    homepath.setForeground(Color.black);
	    homepath.setFont(conceptsFont);
	    homepath.addActionListener(this);
	    homepath.setToolTipText("User's default file system location");
	    homepath.setSelectionColor(Color.darkGray);
	    homepath.setSelectedTextColor(topicsTitleColor);
	    homepath.setMargin(new Insets(2,5,2,2));
	    homepath.setCaretColor(Color.blue);
	    JLabel homepathlabel=new JLabel("Home Path:",JLabel.RIGHT);
	    homepathlabel.setBackground(backColor);
	    homepathlabel.setForeground(topicsTitleColor);
	    homepathlabel.setFont(optionsFont);
	    pathPanel.add(homepath,BorderLayout.CENTER);
	    pathPanel.add(homepathlabel,BorderLayout.WEST);
	    //**Gifs Path
	    JPanel gifsPanel=new JPanel();
	    gifsPanel.setLayout(new BorderLayout());
	    gifsPanel.setBackground(backColor);
	    gifspath=new JTextField(profile.getGifsPath(),30);
	    gifspath.setBackground(Color.lightGray);
	    gifspath.setForeground(Color.black);
	    gifspath.setFont(conceptsFont);
	    gifspath.addActionListener(this);
	    gifspath.setToolTipText("User's default file system location for images");
	    gifspath.setSelectionColor(Color.darkGray);
	    gifspath.setSelectedTextColor(topicsTitleColor);
	    gifspath.setMargin(new Insets(2,5,2,2));
	    gifspath.setCaretColor(Color.blue);
	    JLabel gifspathlabel=new JLabel("Images Path:",JLabel.RIGHT);
	    gifspathlabel.setBackground(backColor);
	    gifspathlabel.setForeground(topicsTitleColor);
	    gifspathlabel.setFont(optionsFont);
	    gifsPanel.add(gifspath,BorderLayout.CENTER);
	    gifsPanel.add(gifspathlabel,BorderLayout.WEST);
	    //**Home URL
	    JPanel homeurlPanel=new JPanel();
	    homeurlPanel.setLayout(new BorderLayout());
	    homeurlPanel.setBackground(backColor);
	    homeurl=new JTextField(profile.getHomeURL(),30);
	    homeurl.setBackground(Color.lightGray);
	    homeurl.setForeground(Color.black);
	    homeurl.setFont(conceptsFont);
	    homeurl.addActionListener(this);
	    homeurl.setToolTipText("User's default file system location");
	    homeurl.setSelectionColor(Color.darkGray);
	    homeurl.setSelectedTextColor(topicsTitleColor);
	    homeurl.setMargin(new Insets(2,5,2,2));
	    homeurl.setCaretColor(Color.blue);
	    JLabel homeurllabel=new JLabel("Home Page:",JLabel.RIGHT);
	    homeurllabel.setBackground(backColor);
	    homeurllabel.setForeground(topicsTitleColor);
	    homeurllabel.setFont(optionsFont);
	    homeurlPanel.add(homeurl,BorderLayout.CENTER);
	    homeurlPanel.add(homeurllabel,BorderLayout.WEST);
	    //**Home Path
	    JPanel dirsPanel=new JPanel();
	    dirsPanel.setLayout(new BorderLayout());
	    dirsPanel.setBackground(backColor);
	    privatepath=new JTextField(profile.getPrivateDir(),15);
	    privatepath.setBackground(Color.lightGray);
	    privatepath.setForeground(Color.black);
	    privatepath.setFont(conceptsFont);
	    privatepath.addActionListener(this);
	    privatepath.setToolTipText("Directory name to use for the private directory for this user");
	    privatepath.setSelectionColor(Color.darkGray);
	    privatepath.setMargin(new Insets(2,5,2,2));
	    privatepath.setSelectedTextColor(topicsTitleColor);
	    privatepath.setCaretColor(Color.blue);
	    JLabel privatepathlabel=new JLabel("Private Directory:",JLabel.RIGHT);
	    privatepathlabel.setBackground(backColor);
	    privatepathlabel.setForeground(topicsTitleColor);
	    privatepathlabel.setFont(optionsFont);
	    dirsPanel.add(privatepath,BorderLayout.CENTER);
	    dirsPanel.add(privatepathlabel,BorderLayout.WEST);
	    locationsPanel.add(pathPanel);
	    locationsPanel.add(gifsPanel);
	    locationsPanel.add(homeurlPanel);
	    locationsPanel.add(dirsPanel);
	    locationsPanel.setBackground(backColor);
	    locationsPanel.setBorder(new TitledBorder(new TitledBorder(""),"",TitledBorder.LEFT,TitledBorder.TOP,borderFont,topicsTitleColor));
	    
	    systemPanel.add(locationsPanel,BorderLayout.CENTER);

	    backColor=holdColor;
	    return systemPanel;
	}

    private JPanel makeThumbarBackColorPanel(Color subPanelsBackColor, Font conceptsFont, Font optionsFont, Color titleColor) {
    
	JPanel thumbarBackPanel=new JPanel();
	thumbarBackPanel.setBorder(new TitledBorder(new TitledBorder(""),"Background Color",
						    TitledBorder.LEFT,TitledBorder.TOP,borderFont,titleColor));
	thumbarBackPanel.setBackground(subPanelsBackColor);
	GridBagLayout backgbl = new GridBagLayout();
	GridBagConstraints backgbc = new GridBagConstraints();
	thumbarBackPanel.setLayout(backgbl);
	thumbarBackColor=profile.getOverviewWindowColor();

	JLabel r_label=new JLabel("R:",JLabel.RIGHT);
	r_label.setBackground(subPanelsBackColor);
	r_label.setForeground(topicsTitleColor);
	r_label.setFont(optionsFont);
	thumbarBackPanel.add(r_label);
	thumbarBackR_Color=new JTextField(3);
	thumbarBackR_Color.setFont(conceptsFont);
	thumbarBackR_Color.addActionListener(this);
	thumbarBackR_Color.setFont(conceptsFont);
	thumbarBackR_Color.setSelectionColor(Color.darkGray);
	thumbarBackR_Color.setSelectedTextColor(topicsTitleColor);
	thumbarBackR_Color.setCaretColor(Color.blue);
	thumbarBackR_Color.setToolTipText("Red color value");
	thumbarBackR_Color.setBackground(Color.lightGray);
	thumbarBackR_Color.setForeground(Color.black);
	thumbarBackR_Color.setText(""+thumbarBackColor.getRed());
	thumbarBackPanel.add(thumbarBackR_Color);

	JLabel g_label=new JLabel("G:",JLabel.RIGHT);
	g_label.setBackground(subPanelsBackColor);
	g_label.setForeground(topicsTitleColor);
	g_label.setFont(optionsFont);
	thumbarBackPanel.add(g_label);
	thumbarBackG_Color=new JTextField(3);
	thumbarBackG_Color.setFont(conceptsFont);
	thumbarBackG_Color.addActionListener(this);
	thumbarBackG_Color.setFont(conceptsFont);
	thumbarBackG_Color.setSelectionColor(Color.darkGray);
	thumbarBackG_Color.setSelectedTextColor(topicsTitleColor);
	thumbarBackG_Color.setCaretColor(Color.blue);
	thumbarBackG_Color.setToolTipText("Green color value");
	thumbarBackG_Color.setBackground(Color.lightGray);
	thumbarBackG_Color.setForeground(Color.black);
	thumbarBackG_Color.setText(""+thumbarBackColor.getGreen());
	thumbarBackPanel.add(thumbarBackG_Color);

	JLabel b_label=new JLabel("B:",JLabel.RIGHT);
	b_label.setBackground(subPanelsBackColor);
	b_label.setForeground(topicsTitleColor);
	b_label.setFont(optionsFont);
	thumbarBackPanel.add(b_label);
	thumbarBackB_Color=new JTextField(3);
	thumbarBackB_Color.setFont(conceptsFont);
	thumbarBackB_Color.addActionListener(this);
	thumbarBackB_Color.setFont(conceptsFont);
	thumbarBackB_Color.setSelectionColor(Color.darkGray);
	thumbarBackB_Color.setSelectedTextColor(topicsTitleColor);
	thumbarBackB_Color.setCaretColor(Color.blue);
	thumbarBackB_Color.setToolTipText("Blue color value");
	thumbarBackB_Color.setBackground(Color.lightGray);
	thumbarBackB_Color.setForeground(Color.black);
	thumbarBackB_Color.setText(""+thumbarBackColor.getBlue());
	thumbarBackPanel.add(thumbarBackB_Color);

	backSquare=new JButton("");
	backSquare.setBackground(thumbarBackColor);
	backSquare.setForeground(thumbarBackColor);
	backSquare.setEnabled(false);
	thumbarBackPanel.add(backSquare);

	int gridx=0;
	backgbc.anchor = GridBagConstraints.WEST;
	backgbc.fill = GridBagConstraints.BOTH;
	backgbc.insets = new Insets(2,1,2,1);
	buildConstraints(backgbc,0,0,1,1,100,100);
	backgbl.setConstraints(r_label,backgbc);
	buildConstraints(backgbc,1,0,1,1,100,100);
	backgbl.setConstraints(thumbarBackR_Color,backgbc);
	buildConstraints(backgbc,2,0,1,1,100,100);
	backgbl.setConstraints(g_label,backgbc);
	buildConstraints(backgbc,3,0,1,1,100,100);
	backgbl.setConstraints(thumbarBackG_Color,backgbc);
	buildConstraints(backgbc,4,0,1,1,100,100);
	backgbl.setConstraints(b_label,backgbc);
	buildConstraints(backgbc,5,0,1,1,100,100);
	backgbl.setConstraints(thumbarBackB_Color,backgbc);
	buildConstraints(backgbc,6,0,1,1,100,100);
	backgbl.setConstraints(backSquare,backgbc);

	return thumbarBackPanel;

    }
    private JPanel makeThumbarLensColorPanel(Color subPanelsBackColor, Font conceptsFont, Font optionsFont, Color titleColor) {

	JPanel thumbarLensPanel=new JPanel();
	thumbarLensPanel.setBorder(new TitledBorder(new TitledBorder(""),"Lens Color",
						    TitledBorder.LEFT,TitledBorder.TOP,borderFont,titleColor));
	thumbarLensPanel.setBackground(subPanelsBackColor);
	GridBagLayout backgbl = new GridBagLayout();
	GridBagConstraints backgbc = new GridBagConstraints();
	thumbarLensPanel.setLayout(backgbl);
	thumbarLensColor=profile.getOverviewLensColor();

	JLabel r_label=new JLabel("R:",JLabel.RIGHT);
	r_label.setBackground(subPanelsBackColor);
	r_label.setForeground(topicsTitleColor);
	r_label.setFont(optionsFont);
	thumbarLensPanel.add(r_label);
	thumbarLensR_Color=new JTextField(3);
	thumbarLensR_Color.setFont(conceptsFont);
	thumbarLensR_Color.addActionListener(this);
	thumbarLensR_Color.setFont(conceptsFont);
	thumbarLensR_Color.setSelectionColor(Color.darkGray);
	thumbarLensR_Color.setSelectedTextColor(topicsTitleColor);
	thumbarLensR_Color.setCaretColor(Color.blue);
	thumbarLensR_Color.setToolTipText("Red color value");
	thumbarLensR_Color.setBackground(Color.lightGray);
	thumbarLensR_Color.setForeground(Color.black);
	thumbarLensR_Color.setText(""+thumbarLensColor.getRed());
	thumbarLensPanel.add(thumbarLensR_Color);

	JLabel g_label=new JLabel("G:",JLabel.RIGHT);
	g_label.setBackground(subPanelsBackColor);
	g_label.setForeground(topicsTitleColor);
	g_label.setFont(optionsFont);
	thumbarLensPanel.add(g_label);
	thumbarLensG_Color=new JTextField(3);
	thumbarLensG_Color.setFont(conceptsFont);
	thumbarLensG_Color.addActionListener(this);
	thumbarLensG_Color.setFont(conceptsFont);
	thumbarLensG_Color.setSelectionColor(Color.darkGray);
	thumbarLensG_Color.setSelectedTextColor(topicsTitleColor);
	thumbarLensG_Color.setCaretColor(Color.blue);
	thumbarLensG_Color.setToolTipText("Green color value");
	thumbarLensG_Color.setBackground(Color.lightGray);
	thumbarLensG_Color.setForeground(Color.black);
	thumbarLensG_Color.setText(""+thumbarLensColor.getGreen());
	thumbarLensPanel.add(thumbarLensG_Color);

	JLabel b_label=new JLabel("B:",JLabel.RIGHT);
	b_label.setBackground(subPanelsBackColor);
	b_label.setForeground(topicsTitleColor);
	b_label.setFont(optionsFont);
	thumbarLensPanel.add(b_label);
	thumbarLensB_Color=new JTextField(3);
	thumbarLensB_Color.setFont(conceptsFont);
	thumbarLensB_Color.addActionListener(this);
	thumbarLensB_Color.setFont(conceptsFont);
	thumbarLensB_Color.setSelectionColor(Color.darkGray);
	thumbarLensB_Color.setSelectedTextColor(topicsTitleColor);
	thumbarLensB_Color.setCaretColor(Color.blue);
	thumbarLensB_Color.setToolTipText("Blue color value");
	thumbarLensB_Color.setBackground(Color.lightGray);
	thumbarLensB_Color.setForeground(Color.black);
	thumbarLensB_Color.setText(""+thumbarLensColor.getBlue());
	thumbarLensPanel.add(thumbarLensB_Color);

	lensSquare=new JButton("");
	lensSquare.setBackground(thumbarLensColor);
	lensSquare.setForeground(thumbarLensColor);
	lensSquare.setEnabled(false);
	thumbarLensPanel.add(lensSquare);

	backgbc.anchor = GridBagConstraints.WEST;
	backgbc.fill = GridBagConstraints.BOTH;
	backgbc.insets = new Insets(2,1,2,1);
	buildConstraints(backgbc,0,0,1,1,100,100);
	backgbl.setConstraints(r_label,backgbc);
	buildConstraints(backgbc,1,0,1,1,100,100);
	backgbl.setConstraints(thumbarLensR_Color,backgbc);
	buildConstraints(backgbc,2,0,1,1,100,100);
	backgbl.setConstraints(g_label,backgbc);
	buildConstraints(backgbc,3,0,1,1,100,100);
	backgbl.setConstraints(thumbarLensG_Color,backgbc);
	buildConstraints(backgbc,4,0,1,1,100,100);
	backgbl.setConstraints(b_label,backgbc);
	buildConstraints(backgbc,5,0,1,1,100,100);
	backgbl.setConstraints(thumbarLensB_Color,backgbc);
	buildConstraints(backgbc,6,0,1,1,100,100);
	backgbl.setConstraints(lensSquare,backgbc);

	return thumbarLensPanel;
    }

    private JPanel makeThumbarWindowLineColorPanel(Color subPanelsBackColor, Font conceptsFont, Font optionsFont, Color titleColor) {
	
	JPanel thumbarWLPanel=new JPanel();
	thumbarWLPanel.setBorder(new TitledBorder(new TitledBorder(""),"Window Line Color",
						    TitledBorder.LEFT,TitledBorder.TOP,borderFont,titleColor));
	thumbarWLPanel.setBackground(subPanelsBackColor);
	GridBagLayout backgbl = new GridBagLayout();
	GridBagConstraints backgbc = new GridBagConstraints();
	thumbarWLPanel.setLayout(backgbl);
	thumbarWLColor=profile.getOverviewWindowLineColor();

	JLabel r_label=new JLabel("R:",JLabel.RIGHT);
	r_label.setBackground(subPanelsBackColor);
	r_label.setForeground(topicsTitleColor);
	r_label.setFont(optionsFont);
	thumbarWLPanel.add(r_label);
	thumbarWLR_Color=new JTextField(3);
	thumbarWLR_Color.setFont(conceptsFont);
	thumbarWLR_Color.addActionListener(this);
	thumbarWLR_Color.setFont(conceptsFont);
	thumbarWLR_Color.setSelectionColor(Color.darkGray);
	thumbarWLR_Color.setSelectedTextColor(topicsTitleColor);
	thumbarWLR_Color.setCaretColor(Color.blue);
	thumbarWLR_Color.setToolTipText("Red color value");
	thumbarWLR_Color.setBackground(Color.lightGray);
	thumbarWLR_Color.setForeground(Color.black);
	thumbarWLR_Color.setText(""+thumbarWLColor.getRed());
	thumbarWLPanel.add(thumbarWLR_Color);

	JLabel g_label=new JLabel("G:",JLabel.RIGHT);
	g_label.setBackground(subPanelsBackColor);
	g_label.setForeground(topicsTitleColor);
	g_label.setFont(optionsFont);
	thumbarWLPanel.add(g_label);
	thumbarWLG_Color=new JTextField(3);
	thumbarWLG_Color.setFont(conceptsFont);
	thumbarWLG_Color.addActionListener(this);
	thumbarWLG_Color.setFont(conceptsFont);
	thumbarWLG_Color.setSelectionColor(Color.darkGray);
	thumbarWLG_Color.setSelectedTextColor(topicsTitleColor);
	thumbarWLG_Color.setCaretColor(Color.blue);
	thumbarWLG_Color.setToolTipText("Green color value");
	thumbarWLG_Color.setBackground(Color.lightGray);
	thumbarWLG_Color.setForeground(Color.black);
	thumbarWLG_Color.setText(""+thumbarWLColor.getGreen());
	thumbarWLPanel.add(thumbarWLG_Color);

	JLabel b_label=new JLabel("B:",JLabel.RIGHT);
	b_label.setBackground(subPanelsBackColor);
	b_label.setForeground(topicsTitleColor);
	b_label.setFont(optionsFont);
	thumbarWLPanel.add(b_label);
	thumbarWLB_Color=new JTextField(3);
	thumbarWLB_Color.setFont(conceptsFont);
	thumbarWLB_Color.addActionListener(this);
	thumbarWLB_Color.setFont(conceptsFont);
	thumbarWLB_Color.setSelectionColor(Color.darkGray);
	thumbarWLB_Color.setSelectedTextColor(topicsTitleColor);
	thumbarWLB_Color.setCaretColor(Color.blue);
	thumbarWLB_Color.setToolTipText("Blue color value");
	thumbarWLB_Color.setBackground(Color.lightGray);
	thumbarWLB_Color.setForeground(Color.black);
	thumbarWLB_Color.setText(""+thumbarWLColor.getBlue());
	thumbarWLPanel.add(thumbarWLB_Color);

	wlSquare=new JButton("");
	wlSquare.setBackground(thumbarWLColor);
	wlSquare.setForeground(thumbarWLColor);
	wlSquare.setEnabled(false);
	thumbarWLPanel.add(wlSquare);

	backgbc.anchor = GridBagConstraints.WEST;
	backgbc.fill = GridBagConstraints.BOTH;
	backgbc.insets = new Insets(2,1,2,1);
	buildConstraints(backgbc,0,0,1,1,100,100);
	backgbl.setConstraints(r_label,backgbc);
	buildConstraints(backgbc,1,0,1,1,100,100);
	backgbl.setConstraints(thumbarWLR_Color,backgbc);
	buildConstraints(backgbc,2,0,1,1,100,100);
	backgbl.setConstraints(g_label,backgbc);
	buildConstraints(backgbc,3,0,1,1,100,100);
	backgbl.setConstraints(thumbarWLG_Color,backgbc);
	buildConstraints(backgbc,4,0,1,1,100,100);
	backgbl.setConstraints(b_label,backgbc);
	buildConstraints(backgbc,5,0,1,1,100,100);
	backgbl.setConstraints(thumbarWLB_Color,backgbc);
	buildConstraints(backgbc,6,0,1,1,100,100);
	backgbl.setConstraints(wlSquare,backgbc);

	return thumbarWLPanel;

    }


    private JPanel makeThumbarANOHColorPanel(Color subPanelsBackColor, Font conceptsFont, Font optionsFont, Color titleColor) {
    
	JPanel thumbarANOHPanel=new JPanel();
	thumbarANOHPanel.setBorder(new TitledBorder(new TitledBorder(""),"Annotation Color",
						    TitledBorder.LEFT,TitledBorder.TOP,borderFont,titleColor));
	thumbarANOHPanel.setBackground(subPanelsBackColor);
	GridBagLayout backgbl = new GridBagLayout();
	GridBagConstraints backgbc = new GridBagConstraints();
	thumbarANOHPanel.setLayout(backgbl);
	thumbarANOHColor=profile.getOverviewANOHColor();

	JLabel r_label=new JLabel("R:",JLabel.RIGHT);
	r_label.setBackground(subPanelsBackColor);
	r_label.setForeground(topicsTitleColor);
	r_label.setFont(optionsFont);
	thumbarANOHPanel.add(r_label);
	thumbarANOHR_Color=new JTextField(3);
	thumbarANOHR_Color.setFont(conceptsFont);
	thumbarANOHR_Color.addActionListener(this);
	thumbarANOHR_Color.setFont(conceptsFont);
	thumbarANOHR_Color.setSelectionColor(Color.darkGray);
	thumbarANOHR_Color.setSelectedTextColor(topicsTitleColor);
	thumbarANOHR_Color.setCaretColor(Color.blue);
	thumbarANOHR_Color.setToolTipText("Red color value");
	thumbarANOHR_Color.setBackground(Color.lightGray);
	thumbarANOHR_Color.setForeground(Color.black);
	thumbarANOHR_Color.setText(""+thumbarANOHColor.getRed());
	thumbarANOHPanel.add(thumbarANOHR_Color);

	JLabel g_label=new JLabel("G:",JLabel.RIGHT);
	g_label.setBackground(subPanelsBackColor);
	g_label.setForeground(topicsTitleColor);
	g_label.setFont(optionsFont);
	thumbarANOHPanel.add(g_label);
	thumbarANOHG_Color=new JTextField(3);
	thumbarANOHG_Color.setFont(conceptsFont);
	thumbarANOHG_Color.addActionListener(this);
	thumbarANOHG_Color.setFont(conceptsFont);
	thumbarANOHG_Color.setSelectionColor(Color.darkGray);
	thumbarANOHG_Color.setSelectedTextColor(topicsTitleColor);
	thumbarANOHG_Color.setCaretColor(Color.blue);
	thumbarANOHG_Color.setToolTipText("Green color value");
	thumbarANOHG_Color.setBackground(Color.lightGray);
	thumbarANOHG_Color.setForeground(Color.black);
	thumbarANOHG_Color.setText(""+thumbarANOHColor.getGreen());
	thumbarANOHPanel.add(thumbarANOHG_Color);

	JLabel b_label=new JLabel("B:",JLabel.RIGHT);
	b_label.setBackground(subPanelsBackColor);
	b_label.setForeground(topicsTitleColor);
	b_label.setFont(optionsFont);
	thumbarANOHPanel.add(b_label);
	thumbarANOHB_Color=new JTextField(3);
	thumbarANOHB_Color.setFont(conceptsFont);
	thumbarANOHB_Color.addActionListener(this);
	thumbarANOHB_Color.setFont(conceptsFont);
	thumbarANOHB_Color.setSelectionColor(Color.darkGray);
	thumbarANOHB_Color.setSelectedTextColor(topicsTitleColor);
	thumbarANOHB_Color.setCaretColor(Color.blue);
	thumbarANOHB_Color.setToolTipText("Blue color value");
	thumbarANOHB_Color.setBackground(Color.lightGray);
	thumbarANOHB_Color.setForeground(Color.black);
	thumbarANOHB_Color.setText(""+thumbarANOHColor.getBlue());
	thumbarANOHPanel.add(thumbarANOHB_Color);

	anohSquare=new JButton("");
	anohSquare.setBackground(thumbarANOHColor);
	anohSquare.setForeground(thumbarANOHColor);
	anohSquare.setEnabled(false);
	thumbarANOHPanel.add(anohSquare);

	int gridx=0;
	backgbc.anchor = GridBagConstraints.WEST;
	backgbc.fill = GridBagConstraints.BOTH;
	backgbc.insets = new Insets(2,1,2,1);
	buildConstraints(backgbc,0,0,1,1,100,100);
	backgbl.setConstraints(r_label,backgbc);
	buildConstraints(backgbc,1,0,1,1,100,100);
	backgbl.setConstraints(thumbarANOHR_Color,backgbc);
	buildConstraints(backgbc,2,0,1,1,100,100);
	backgbl.setConstraints(g_label,backgbc);
	buildConstraints(backgbc,3,0,1,1,100,100);
	backgbl.setConstraints(thumbarANOHG_Color,backgbc);
	buildConstraints(backgbc,4,0,1,1,100,100);
	backgbl.setConstraints(b_label,backgbc);
	buildConstraints(backgbc,5,0,1,1,100,100);
	backgbl.setConstraints(thumbarANOHB_Color,backgbc);
	buildConstraints(backgbc,6,0,1,1,100,100);
	backgbl.setConstraints(anohSquare,backgbc);

	return thumbarANOHPanel;

    }
    private JPanel makeThumbarLinkColorPanel(Color subPanelsBackColor, Font conceptsFont, Font optionsFont, Color titleColor) {

	JPanel thumbarLinkPanel=new JPanel();
	thumbarLinkPanel.setBorder(new TitledBorder(new TitledBorder(""),"Link Color",
						    TitledBorder.LEFT,TitledBorder.TOP,borderFont,titleColor));
	thumbarLinkPanel.setBackground(subPanelsBackColor);
	GridBagLayout backgbl = new GridBagLayout();
	GridBagConstraints backgbc = new GridBagConstraints();
	thumbarLinkPanel.setLayout(backgbl);
	thumbarLinkColor=profile.getOverviewLinkColor();

	JLabel r_label=new JLabel("R:",JLabel.RIGHT);
	r_label.setBackground(subPanelsBackColor);
	r_label.setForeground(topicsTitleColor);
	r_label.setFont(optionsFont);
	thumbarLinkPanel.add(r_label);
	thumbarLinkR_Color=new JTextField(3);
	thumbarLinkR_Color.setFont(conceptsFont);
	thumbarLinkR_Color.addActionListener(this);
	thumbarLinkR_Color.setFont(conceptsFont);
	thumbarLinkR_Color.setSelectionColor(Color.darkGray);
	thumbarLinkR_Color.setSelectedTextColor(topicsTitleColor);
	thumbarLinkR_Color.setCaretColor(Color.blue);
	thumbarLinkR_Color.setToolTipText("Red color value");
	thumbarLinkR_Color.setBackground(Color.lightGray);
	thumbarLinkR_Color.setForeground(Color.black);
	thumbarLinkR_Color.setText(""+thumbarLinkColor.getRed());
	thumbarLinkPanel.add(thumbarLinkR_Color);

	JLabel g_label=new JLabel("G:",JLabel.RIGHT);
	g_label.setBackground(subPanelsBackColor);
	g_label.setForeground(topicsTitleColor);
	g_label.setFont(optionsFont);
	thumbarLinkPanel.add(g_label);
	thumbarLinkG_Color=new JTextField(3);
	thumbarLinkG_Color.setFont(conceptsFont);
	thumbarLinkG_Color.addActionListener(this);
	thumbarLinkG_Color.setFont(conceptsFont);
	thumbarLinkG_Color.setSelectionColor(Color.darkGray);
	thumbarLinkG_Color.setSelectedTextColor(topicsTitleColor);
	thumbarLinkG_Color.setCaretColor(Color.blue);
	thumbarLinkG_Color.setToolTipText("Green color value");
	thumbarLinkG_Color.setBackground(Color.lightGray);
	thumbarLinkG_Color.setForeground(Color.black);
	thumbarLinkG_Color.setText(""+thumbarLinkColor.getGreen());
	thumbarLinkPanel.add(thumbarLinkG_Color);

	JLabel b_label=new JLabel("B:",JLabel.RIGHT);
	b_label.setBackground(subPanelsBackColor);
	b_label.setForeground(topicsTitleColor);
	b_label.setFont(optionsFont);
	thumbarLinkPanel.add(b_label);
	thumbarLinkB_Color=new JTextField(3);
	thumbarLinkB_Color.setFont(conceptsFont);
	thumbarLinkB_Color.addActionListener(this);
	thumbarLinkB_Color.setFont(conceptsFont);
	thumbarLinkB_Color.setSelectionColor(Color.darkGray);
	thumbarLinkB_Color.setSelectedTextColor(topicsTitleColor);
	thumbarLinkB_Color.setCaretColor(Color.blue);
	thumbarLinkB_Color.setToolTipText("Blue color value");
	thumbarLinkB_Color.setBackground(Color.lightGray);
	thumbarLinkB_Color.setForeground(Color.black);
	thumbarLinkB_Color.setText(""+thumbarLinkColor.getBlue());
	thumbarLinkPanel.add(thumbarLinkB_Color);

	linkSquare=new JButton("");
	linkSquare.setBackground(thumbarLinkColor);
	linkSquare.setForeground(thumbarLinkColor);
	linkSquare.setEnabled(false);
	thumbarLinkPanel.add(linkSquare);

	backgbc.anchor = GridBagConstraints.WEST;
	backgbc.fill = GridBagConstraints.BOTH;
	backgbc.insets = new Insets(2,1,2,1);
	buildConstraints(backgbc,0,0,1,1,100,100);
	backgbl.setConstraints(r_label,backgbc);
	buildConstraints(backgbc,1,0,1,1,100,100);
	backgbl.setConstraints(thumbarLinkR_Color,backgbc);
	buildConstraints(backgbc,2,0,1,1,100,100);
	backgbl.setConstraints(g_label,backgbc);
	buildConstraints(backgbc,3,0,1,1,100,100);
	backgbl.setConstraints(thumbarLinkG_Color,backgbc);
	buildConstraints(backgbc,4,0,1,1,100,100);
	backgbl.setConstraints(b_label,backgbc);
	buildConstraints(backgbc,5,0,1,1,100,100);
	backgbl.setConstraints(thumbarLinkB_Color,backgbc);
	buildConstraints(backgbc,6,0,1,1,100,100);
	backgbl.setConstraints(linkSquare,backgbc);

	return thumbarLinkPanel;
    }

    private JPanel makeThumbarLensLineColorPanel(Color subPanelsBackColor, Font conceptsFont, Font optionsFont, Color titleColor) {

	JPanel thumbarLensLinePanel=new JPanel();
	thumbarLensLinePanel.setBorder(new TitledBorder(new TitledBorder(""),"Lens Line Color",
						    TitledBorder.LEFT,TitledBorder.TOP,borderFont,titleColor));
	thumbarLensLinePanel.setBackground(subPanelsBackColor);
	GridBagLayout backgbl = new GridBagLayout();
	GridBagConstraints backgbc = new GridBagConstraints();
	thumbarLensLinePanel.setLayout(backgbl);
	thumbarLensLineColor=profile.getOverviewLensLineColor();

	JLabel r_label=new JLabel("R:",JLabel.RIGHT);
	r_label.setBackground(subPanelsBackColor);
	r_label.setForeground(topicsTitleColor);
	r_label.setFont(optionsFont);
	thumbarLensLinePanel.add(r_label);
	thumbarLensLineR_Color=new JTextField(3);
	thumbarLensLineR_Color.setFont(conceptsFont);
	thumbarLensLineR_Color.addActionListener(this);
	thumbarLensLineR_Color.setFont(conceptsFont);
	thumbarLensLineR_Color.setSelectionColor(Color.darkGray);
	thumbarLensLineR_Color.setSelectedTextColor(topicsTitleColor);
	thumbarLensLineR_Color.setCaretColor(Color.blue);
	thumbarLensLineR_Color.setToolTipText("Red color value");
	thumbarLensLineR_Color.setBackground(Color.lightGray);
	thumbarLensLineR_Color.setForeground(Color.black);
	thumbarLensLineR_Color.setText(""+thumbarLensLineColor.getRed());
	thumbarLensLinePanel.add(thumbarLensLineR_Color);

	JLabel g_label=new JLabel("G:",JLabel.RIGHT);
	g_label.setBackground(subPanelsBackColor);
	g_label.setForeground(topicsTitleColor);
	g_label.setFont(optionsFont);
	thumbarLensLinePanel.add(g_label);
	thumbarLensLineG_Color=new JTextField(3);
	thumbarLensLineG_Color.setFont(conceptsFont);
	thumbarLensLineG_Color.addActionListener(this);
	thumbarLensLineG_Color.setFont(conceptsFont);
	thumbarLensLineG_Color.setSelectionColor(Color.darkGray);
	thumbarLensLineG_Color.setSelectedTextColor(topicsTitleColor);
	thumbarLensLineG_Color.setCaretColor(Color.blue);
	thumbarLensLineG_Color.setToolTipText("Green color value");
	thumbarLensLineG_Color.setBackground(Color.lightGray);
	thumbarLensLineG_Color.setForeground(Color.black);
	thumbarLensLineG_Color.setText(""+thumbarLensLineColor.getGreen());
	thumbarLensLinePanel.add(thumbarLensLineG_Color);

	JLabel b_label=new JLabel("B:",JLabel.RIGHT);
	b_label.setBackground(subPanelsBackColor);
	b_label.setForeground(topicsTitleColor);
	b_label.setFont(optionsFont);
	thumbarLensLinePanel.add(b_label);
	thumbarLensLineB_Color=new JTextField(3);
	thumbarLensLineB_Color.setFont(conceptsFont);
	thumbarLensLineB_Color.addActionListener(this);
	thumbarLensLineB_Color.setFont(conceptsFont);
	thumbarLensLineB_Color.setSelectionColor(Color.darkGray);
	thumbarLensLineB_Color.setSelectedTextColor(topicsTitleColor);
	thumbarLensLineB_Color.setCaretColor(Color.blue);
	thumbarLensLineB_Color.setToolTipText("Blue color value");
	thumbarLensLineB_Color.setBackground(Color.lightGray);
	thumbarLensLineB_Color.setForeground(Color.black);
	thumbarLensLineB_Color.setText(""+thumbarLensLineColor.getBlue());
	thumbarLensLinePanel.add(thumbarLensLineB_Color);

	lensLineSquare=new JButton("");
	lensLineSquare.setBackground(thumbarLensLineColor);
	lensLineSquare.setForeground(thumbarLensLineColor);
	lensLineSquare.setEnabled(false);
	thumbarLensLinePanel.add(lensLineSquare);

	backgbc.anchor = GridBagConstraints.WEST;
	backgbc.fill = GridBagConstraints.BOTH;
	backgbc.insets = new Insets(2,1,2,1);
	buildConstraints(backgbc,0,0,1,1,100,100);
	backgbl.setConstraints(r_label,backgbc);
	buildConstraints(backgbc,1,0,1,1,100,100);
	backgbl.setConstraints(thumbarLensLineR_Color,backgbc);
	buildConstraints(backgbc,2,0,1,1,100,100);
	backgbl.setConstraints(g_label,backgbc);
	buildConstraints(backgbc,3,0,1,1,100,100);
	backgbl.setConstraints(thumbarLensLineG_Color,backgbc);
	buildConstraints(backgbc,4,0,1,1,100,100);
	backgbl.setConstraints(b_label,backgbc);
	buildConstraints(backgbc,5,0,1,1,100,100);
	backgbl.setConstraints(thumbarLensLineB_Color,backgbc);
	buildConstraints(backgbc,6,0,1,1,100,100);
	backgbl.setConstraints(lensLineSquare,backgbc);

	return thumbarLensLinePanel;
    }

    private void makeEditConcepts(Font optionsFont, Font conceptsFont) {
	//** Concepts Panel
	editConceptsPanel=new JPanel();
	editConceptsPanel.setLayout(new BorderLayout());
	editConceptsPanel.setBackground(backColor);
	editConceptsPanel.setForeground(conceptsColor);
	//editConceptsPanel.setBorder(BorderFactory.createBevelBorder(1,Color.lightGray,Color.darkGray));
	
	JPanel editConceptsButtonPanel=new JPanel();
	editConceptsButtonPanel.setLayout(new GridLayout(1,3));
	editConceptsButtonPanel.setBackground(backColor);
	editConceptsSave=new JButton("Save Concept Changes");
	editConceptsSave.setBackground(backColor);
	editConceptsSave.setForeground(saveButtonsColor);
	editConceptsSave.setFont(conceptsFont);
	editConceptsSave.addActionListener(this);
	editConceptsButtonPanel.add(editConceptsSave);
	
	editConceptsNoSave=new JButton("This session only (no save)");
	editConceptsNoSave.setBackground(backColor);
	editConceptsNoSave.setForeground(saveButtonsColor);
	editConceptsNoSave.setFont(conceptsFont);
	editConceptsNoSave.addActionListener(this);
	editConceptsButtonPanel.add(editConceptsNoSave);
	
	editConceptsCancel=new JButton("Disregard all changes");
	editConceptsCancel.setBackground(backColor);
	editConceptsCancel.setForeground(saveButtonsColor);
	editConceptsCancel.setFont(conceptsFont);
	editConceptsCancel.addActionListener(this);
	editConceptsButtonPanel.add(editConceptsCancel);
	
	editConceptsSave.setEnabled(false);
	editConceptsNoSave.setEnabled(false);
	editConceptsCancel.setEnabled(false);
	editConceptsPanel.add(editConceptsButtonPanel,BorderLayout.SOUTH);
	
	JPanel conceptsPanel=new JPanel();
	conceptsPanel.setLayout(new BorderLayout());
	String str=conceptBorderTitle+profile.numAllConcepts+" of "+profile.allConcepts.length;
	conceptBorder=new TitledBorder(new TitledBorder(""),str,TitledBorder.LEFT,TitledBorder.TOP,borderFont,topicsTitleColor);
	conceptsPanel.setBorder(conceptBorder);
	conceptsPanel.setBackground(backColor);
	int len=profile.numAllConcepts;
	conceptData=new String[len];
	for (int i=0;i<len;i++) conceptData[i]=profile.allConcepts[i].getName();
	concepts=new JList(conceptData);
	concepts.setBorder(BorderFactory.createBevelBorder(1,Color.darkGray,Color.black));
	concepts.setFont(conceptsFont);
	concepts.setForeground(conceptsColor);
	concepts.setBackground(conceptsBackColor);
	concepts.addListSelectionListener(this);
	conceptsScrollPane = new JScrollPane(concepts);
	conceptsScrollPane.setForeground(conceptsColor);
	conceptsPanel.add(conceptsScrollPane,BorderLayout.CENTER);
	
	//conceptsPanel.add(concepts,BorderLayout.WEST);
	JPanel conceptsButtonsPanel=new JPanel();
	conceptsButtonsPanel.setLayout(new GridLayout(1,3));
	conceptsButtonsPanel.setBackground(backColor);
	addConcept=new JButton("Add");
	addConcept.setBackground(backColor);
	addConcept.setFont(conceptsFont);
	addConcept.setForeground(topicsTitleColor);
	addConcept.addActionListener(this);
	addConcept.setToolTipText("Add a new concept to the list");
	conceptsButtonsPanel.add(addConcept);
	
	editConcept=new JButton("Edit");
	editConcept.setBackground(backColor);
	editConcept.setFont(conceptsFont);
	editConcept.setForeground(topicsTitleColor);
	editConcept.addActionListener(this);
	editConcept.setEnabled(false);
	editConcept.setToolTipText("Edit the selected concept's title");
	conceptsButtonsPanel.add(editConcept);
	
	removeConcept=new JButton("Remove");
	removeConcept.setBackground(backColor);
	removeConcept.setFont(conceptsFont);
	removeConcept.setForeground(topicsTitleColor);
	removeConcept.addActionListener(this);
	removeConcept.setEnabled(false);
	conceptsButtonsPanel.add(removeConcept);
	removeConcept.setToolTipText("Revove the selected concept");
	conceptsPanel.add(conceptsButtonsPanel,BorderLayout.SOUTH);
	editConceptsPanel.add(conceptsPanel,BorderLayout.WEST);
	
	JPanel subConceptsPanel=new JPanel();
	//subConceptsPanel.setBorder(BorderFactory.createBevelBorder(1,Color.darkGray,Color.lightGray));
	subConceptsPanel.setBackground(backColor);
	editConceptsPanel.add(subConceptsPanel,BorderLayout.CENTER);
	
	JPanel optionsPanel=new JPanel();
	optionsPanel.setBackground(backColor);
	optionsPanel.setBorder(new TitledBorder(new TitledBorder(""),"Options",TitledBorder.LEFT,TitledBorder.TOP,borderFont,topicsTitleColor));
	shortNameField=new JTextField(8);
	shortNameField.setFont(optionsFont);
	shortNameField.setBackground(Color.lightGray);
	shortNameField.setForeground(Color.black);
	shortNameField.setToolTipText("Short name label used for the Concepts interface (& for Groups)");
	shortNameField.addActionListener(this);
	optionsPanel.add(shortNameField);
	
	JLabel shortNameText=new JLabel("ShortName:",JLabel.LEFT);
	shortNameText.setFont(optionsFont);
	optionsPanel.add(shortNameText);
	
	activeField=new JCheckBox();
	activeField.setFont(optionsFont);
	activeField.setBackground(backColor);
	activeField.setMargin(new Insets(2,2,2,2));
	activeField.setToolTipText("Check this box if you want the default state to be active(ON)");
	activeField.addActionListener(this);
	optionsPanel.add(activeField);

	JLabel priorText=new JLabel("Prior:",JLabel.LEFT);
	priorText.setFont(optionsFont);
	optionsPanel.add(priorText);

	prior=new JTextField(5);
	prior.setFont(optionsFont);
	prior.setBackground(Color.lightGray);
	prior.setForeground(Color.black);
	prior.setToolTipText("Prior probability - do not change this value!");
	prior.addActionListener(this);
	optionsPanel.add(prior);
	
	JLabel activeText=new JLabel("Concept Active:",JLabel.RIGHT);
	activeText.setFont(optionsFont);
	optionsPanel.add(activeText);
	
	GridBagLayout optionsgbl = new GridBagLayout();
	GridBagConstraints optionsgbc = new GridBagConstraints();
	optionsPanel.setLayout(optionsgbl);
	optionsgbc.anchor = GridBagConstraints.EAST;
	optionsgbc.insets = new Insets(0,0,0,2);
	buildConstraints(optionsgbc,0,0,1,1,100,100);
	optionsgbl.setConstraints(shortNameText,optionsgbc);
	
	optionsgbc.anchor = GridBagConstraints.WEST;
	buildConstraints(optionsgbc,1,0,1,1,100,100);
	optionsgbl.setConstraints(shortNameField,optionsgbc);
	
	optionsgbc.anchor = GridBagConstraints.EAST;
	buildConstraints(optionsgbc,2,0,1,1,100,100);
	optionsgbl.setConstraints(priorText,optionsgbc);

	optionsgbc.anchor = GridBagConstraints.WEST;
	buildConstraints(optionsgbc,3,0,1,1,100,100);
	optionsgbl.setConstraints(prior,optionsgbc);

	optionsgbc.anchor = GridBagConstraints.EAST;
	buildConstraints(optionsgbc,4,0,1,1,100,100);
	optionsgbl.setConstraints(activeText,optionsgbc);
	
	optionsgbc.anchor = GridBagConstraints.WEST;
	buildConstraints(optionsgbc,5,0,1,1,100,100);
	optionsgbl.setConstraints(activeField,optionsgbc);
	
	JPanel topicsPanel=new JPanel();
	topicsPanel.setBackground(backColor);
	topicsPanel.setLayout(new GridLayout(1,1));
	topicsBorder=new TitledBorder(new TitledBorder(""),topicBorderTitle,TitledBorder.LEFT,TitledBorder.TOP,borderFont,topicsTitleColor);
	topicsPanel.setBorder(topicsBorder);
	topicsList=new JList();
	topicsList.setFont(conceptsFont);
	topicsList.setBorder(BorderFactory.createBevelBorder(1,Color.lightGray,Color.darkGray));
	topicsList.addListSelectionListener(this);
	topicsScrollPane = new JScrollPane(topicsList);
	topicsPanel.add(topicsScrollPane);
	
	JPanel currentTopicPanel=new JPanel();
	currentTopicPanel.setBackground(backColor);
	currentTopicPanel.setLayout(new BorderLayout());
	currentTopic=new JTextField(40);
	currentTopic.setFont(conceptsFont);
	currentTopicPanel.add(currentTopic,BorderLayout.CENTER);
	
	JPanel topicButtonsPanel=new JPanel();
	topicButtonsPanel.setLayout(new BorderLayout());
	topicButtonsPanel.setBackground(backColor);
	editTopic=new JButton("Edit");
	editTopic.setBackground(backColor);
	editTopic.setForeground(topicsTitleColor);
	editTopic.setFont(optionsFont);
	editTopic.addActionListener(this);
	topicButtonsPanel.add(editTopic,BorderLayout.WEST);
	delTopic=new JButton("Delete");
	delTopic.setBackground(backColor);
	delTopic.setForeground(topicsTitleColor);
	delTopic.setFont(optionsFont);
	delTopic.addActionListener(this);
	topicButtonsPanel.add(delTopic,BorderLayout.EAST);
	currentTopicPanel.add(topicButtonsPanel,BorderLayout.EAST);
	currentTopicPanel.setBorder(new TitledBorder(new TitledBorder(""),"Entered/Selected Phrase",TitledBorder.LEFT,TitledBorder.TOP,borderFont,
						     topicsTitleColor));
	
	GridBagLayout gbl = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();
	//subConceptsPanel.setLayout(gbl);
	//    subConceptsPanel.add(optionsPanel);
	//    subConceptsPanel.add(topicsPanel);
	//subConceptsPanel.add(currentTopicPanel);
	subConceptsPanel.setLayout(new BorderLayout());
	subConceptsPanel.add(optionsPanel, BorderLayout.NORTH);
	subConceptsPanel.add(topicsPanel,BorderLayout.CENTER);
	subConceptsPanel.add(currentTopicPanel,BorderLayout.SOUTH);
    }

  public void actionPerformed(ActionEvent ev) {
      Object source = ev.getSource();

	if (source == cancel) {
	    System.exit(0);
	}
	
	else if (source == editTopic) {
	    if (currentTopicPos>=0) {
		topicsData[currentTopicPos]=currentTopic.getText();
		topicsList.setListData(topicsData);
		currentTopic.setText("");
		editTopic.setText("Add");
		topicsList.revalidate();
		editConceptsSave.setEnabled(true);
		editConceptsNoSave.setEnabled(true);
		editConceptsCancel.setEnabled(true);
		currentConcept.addKeywordStrings(topicsData);
		delTopic.setEnabled(false);
	    }
	    else if (currentTopic.getText()!="" && currentTopic.getText().length()>0) {
		String[] holder=new String[topicsData.length+1];
		System.arraycopy(topicsData,0,holder,0,topicsData.length);
		topicsData=new String[holder.length];
		System.arraycopy(holder,0,topicsData,0,holder.length);
		topicsData[topicsData.length-1]=currentTopic.getText();
		topicsList.setListData(topicsData);
		topicsBorder.setTitle(topicBorderTitle+topicsData.length);
		currentTopic.setText("");
		editTopic.setText("Add");
		topicsList.revalidate();
		editConceptsSave.setEnabled(true);
		editConceptsNoSave.setEnabled(true);
		editConceptsCancel.setEnabled(true);
		currentConcept.addKeywordStrings(topicsData);
		editConceptsPanel.repaint();
		delTopic.setEnabled(false);
	    }
	}
	else if (source == delTopic) {
	    if (currentTopicPos>=0) {
		//System.out.println("delete topic:"+currentTopicPos+" topicsdata len="+topicsData.length);
		RH_PopupMsg popup=new RH_PopupMsg(this,"Are you sure you want to delete this topic?",mainBackColor,mainForeColor);
		if (popup.getAction()) {
		    String[] holder=new String[topicsData.length-1];
		    //System.out.println("New length:"+ holder.length);
		    System.arraycopy(topicsData,0,holder,0,currentTopicPos);
		    //System.out.println("CurrentPos:"+currentTopicPos);
		    for (int i=currentTopicPos+1, j=currentTopicPos;j<holder.length;i++,j++) {
			holder[j]=topicsData[i];
			//System.out.println("Moving "+i+"> "+topicsData[i]+" to <"+j+">"+holder[j]);
		    }
		    
		    topicsData=new String[holder.length];
		    System.arraycopy(holder,0,topicsData,0,holder.length);
		    topicsList.setListData(topicsData);
		    topicsBorder.setTitle(topicBorderTitle+topicsData.length);
		    currentTopic.setText("");
		    editTopic.setText("Add");
		    topicsList.revalidate();
		    //System.out.println("Actual length:"+ topicsData.length);
		    currentTopicPos=-1;
		    editConceptsSave.setEnabled(true);
		    editConceptsNoSave.setEnabled(true);
		    editConceptsCancel.setEnabled(true);
		    currentConcept.addKeywordStrings(topicsData);
		    editConceptsPanel.repaint();
		    delTopic.setEnabled(false);
		}
	    }
	}
	else if (source == editConceptsSave) {
	    modifiedConcepts=true;
	    saveConcepts();
	    editConceptsSave.setEnabled(false);
	    editConceptsNoSave.setEnabled(false);
	    editConceptsCancel.setEnabled(false);
	}
	else if (source == editConceptsNoSave) {
	    editConceptsSave.setEnabled(false);
	    editConceptsNoSave.setEnabled(false);
	    editConceptsCancel.setEnabled(false);
	}
	else if (source == editConceptsCancel) {
	    editConceptsSave.setEnabled(false);
	    editConceptsNoSave.setEnabled(false);
	    editConceptsCancel.setEnabled(false);
	}
	else if (source == addConcept) {
	    if (conceptData.length+1<profile.allConcepts.length) {
		RH_PopupTextField popup=new RH_PopupTextField(this,"Enter new concept name:","",backColor,Color.yellow);
		String newlabel=popup.getTextFieldString();
		if (newlabel.length()>0) {
		    String[] holder=new String[conceptData.length];
		    System.arraycopy(conceptData,0,holder,0,conceptData.length);
		    conceptData=new String[holder.length+1];
		    System.arraycopy(holder,0,conceptData,0,holder.length);
		    conceptData[conceptData.length-1]=newlabel;
		    concepts.setListData(conceptData);
		    concepts.revalidate();
		    int sub=10;
		    if (newlabel.length()<sub) sub=newlabel.length();
		    profile.allConcepts[profile.numAllConcepts]=new RHActiveConcept(newlabel,newlabel.substring(0,sub));
		    String[] phrases=new String[1];
		    phrases[0]=newlabel;
		    profile.allConcepts[profile.numAllConcepts++].addKeywordStrings(phrases);
		    conceptBorder.setTitle(conceptBorderTitle+profile.numAllConcepts+" of "+profile.allConcepts.length);
		    groupAllConceptsBorder.setTitle(groupAllConceptsBorderTitle+profile.numAllConcepts);
		    groupAllConcepts.setListData(conceptData);
		    groupAllConcepts.revalidate();
		    editConceptsSave.setEnabled(true);
		    editConceptsNoSave.setEnabled(true);
		    editConceptsCancel.setEnabled(true);
		}
	    }
	    else {
		new RH_PopupError(this,"You cannot add more concepts because max concepts reached");
	    }
	}
	else if (source == editConcept) {
	    RH_PopupTextField popup=new RH_PopupTextField(this,"Change concept name:",currentConcept.getName(),backColor,Color.yellow);
	    String newlabel=popup.getTextFieldString();

	    conceptData[currentConceptPos]=newlabel;
	    concepts.setListData(conceptData);
	    concepts.revalidate();
	    currentConcept.setName(newlabel);

	    editConceptsSave.setEnabled(true);
	    editConceptsNoSave.setEnabled(true);
	    editConceptsCancel.setEnabled(true);
	}
	else if (source == removeConcept) {
	    RH_PopupMsg popup=new RH_PopupMsg(this,"Delete concept: <"+currentConcept.getName()+">?",mainBackColor,mainForeColor);
	    if (popup.getAction()) {
		int i=0, j=0;
		RHActiveConcept[] holder=new RHActiveConcept[profile.allConcepts.length];
		System.arraycopy(profile.allConcepts,0,holder,0,currentConceptPos);
		for (i=currentConceptPos+1, j=currentConceptPos; j<holder.length-1;i++,j++) holder[j]=profile.allConcepts[i];
		profile.allConcepts=new RHActiveConcept[holder.length];
		System.arraycopy(holder,0,profile.allConcepts,0,holder.length);
		profile.numAllConcepts--;

		//** 10.13.98 at some point i need to remove the similarity directory for this concept

		conceptData=new String[profile.numAllConcepts];
		for (i=0;i<profile.numAllConcepts;i++) conceptData[i]=profile.allConcepts[i].getName();
		concepts.setListData(conceptData);
		conceptBorder.setTitle(conceptBorderTitle+profile.numAllConcepts+" of "+profile.allConcepts.length);
		concepts.revalidate();
		editConceptsPanel.repaint();
		editConceptsSave.setEnabled(true);
		editConceptsNoSave.setEnabled(true);
		editConceptsCancel.setEnabled(true);
	    }
	}
	else if (source == activeField) {
	    currentConcept.setActive(activeField.isSelected());
	    editConceptsSave.setEnabled(true);
	    editConceptsNoSave.setEnabled(true);
	    editConceptsCancel.setEnabled(true);
	}
	else if (source == prior) {
	    Double newprior=new Double(prior.getText());
	    currentConcept.setPrior(newprior.doubleValue());
	    editConceptsSave.setEnabled(true);
	    editConceptsNoSave.setEnabled(true);
	    editConceptsCancel.setEnabled(true);
	}
	else if (source == shortNameField) {
	    String oldname=currentConcept.getShortName();
	    currentConcept.setShortName(shortNameField.getText());
	    updateConceptInGroups(oldname);
	    editConceptsSave.setEnabled(true);
	    editConceptsNoSave.setEnabled(true);
	    editConceptsCancel.setEnabled(true);
	}
	else if (source == removeGroupConcept) {
	    if (currentGroupConceptPos>=0 && currentGroupPos>=0) {
		String[] holder=new String[groupConceptsData.length];
		System.arraycopy(groupConceptsData,0,holder,0,currentGroupConceptPos);
		for (int i=currentGroupConceptPos+1, j=currentGroupConceptPos;j<holder.length-1;i++,j++) holder[j]=groupConceptsData[i];
		groupConceptsData=new String[holder.length-1];
		System.arraycopy(holder,0,groupConceptsData,0,groupConceptsData.length);
		profile.setGroupConcepts(currentGroupPos,groupConceptsData);
		groupConcepts.setListData(groupConceptsData);
		groupConcepts.revalidate();
		groupConceptsBorder.setTitle(groupConceptsBorderTitle+groupConceptsData.length);
		editGroupsPanel.repaint();
		editGroupsSave.setEnabled(true);
		editGroupsNoSave.setEnabled(true);
		editGroupsCancel.setEnabled(true);
	    }
	}
	else if (source == addGroupConcept) {
	    if (currentGroupPos>=0 && currentConcept!=null && !member(currentConcept.getShortName(),groupConceptsData)) {
		String[] holder=new String[groupConceptsData.length];
		System.arraycopy(groupConceptsData,0,holder,0,groupConceptsData.length);
		groupConceptsData=new String[holder.length+1];
		System.arraycopy(holder,0,groupConceptsData,0,holder.length);
		groupConceptsData[groupConceptsData.length-1]=currentConcept.getShortName();
		profile.setGroupConcepts(currentGroupPos,groupConceptsData);
		groupConcepts.setListData(groupConceptsData);
		groupConcepts.revalidate();
		groupConceptsBorder.setTitle(groupConceptsBorderTitle+groupConceptsData.length);
		editGroupsPanel.repaint();
		editGroupsSave.setEnabled(true);
		editGroupsNoSave.setEnabled(true);
		editGroupsCancel.setEnabled(true);
	    }
	}
	else if (source == groupToolTip) {
	    if (currentGroupPos>=0) {
		profile.setGroupToolTipString(currentGroupPos,groupToolTip.getText());

		editGroupsSave.setEnabled(true);
		editGroupsNoSave.setEnabled(true);
		editGroupsCancel.setEnabled(true);
	    }
	}
	else if (source == addGroup) {
	    RH_PopupTextField popup=new RH_PopupTextField(this,"Enter new group name:","",backColor,Color.yellow);
	    String newlabel=popup.getTextFieldString();
	    if (newlabel.length()>0) {
		String[] holder=new String[groupData.length];
		System.arraycopy(groupData,0,holder,0,groupData.length);
		groupData=new String[holder.length+1];
		System.arraycopy(holder,0,groupData,0,holder.length);
		groupData[groupData.length-1]=newlabel;
		groups.setListData(groupData);
		profile.addGroup(newlabel,0,"no tool tip",null);
		numGroups=profile.getNumberGroups()-1;
		groups.setSelectedIndex(numGroups-1);
	       
		
		groupBorder.setTitle(groupBorderTitle+numGroups);
		groups.revalidate();
		editGroupsPanel.repaint();

		editGroupsSave.setEnabled(true);
		editGroupsNoSave.setEnabled(true);
		editGroupsCancel.setEnabled(true);
	    }

	    addGroup.setEnabled(true);
	    editGroup.setEnabled(false);
	    removeGroup.setEnabled(false);
	}
	else if (source == editGroup) {
	    RH_PopupTextField popup=new RH_PopupTextField(this,"Change group name:",currentGroup.getName(),backColor,Color.yellow);
	    String newlabel=popup.getTextFieldString();
	    //System.out.println("Newlabel:"+newlabel+" at pos "+currentGroupPos);

	    groupData[currentGroupPos]=newlabel;
	    groups.setListData(groupData);
	    groups.revalidate();
	    currentGroup.setName(newlabel);
	    addGroup.setEnabled(true);
	    editGroup.setEnabled(false);
	    removeGroup.setEnabled(false);

	    editGroupsSave.setEnabled(true);
	    editGroupsNoSave.setEnabled(true);
	    editGroupsCancel.setEnabled(true);
	}
	else if (source == removeGroup) {
	    //System.out.println("**Group Len="+groupData.length);
	    if (groupData.length>1) {
		RH_PopupMsg popup=new RH_PopupMsg(this,"Delete group: <"+currentGroup.getName()+">?",mainBackColor,mainForeColor);
		if (popup.getAction()) {
		    int i=0, j=0;
		    profile.removeGroup(currentGroupPos);
		    //** if you delete the default group, set the first group as default
		    if (currentGroupPos==profile.getDefaultGroup()) profile.setDefaultGroup(0);
		    RH_ConceptGroup[] tmp=profile.getConceptGroups();
		    groupData=new String[tmp.length-1];
		    for (i=0;i<tmp.length-1;i++) groupData[i]=tmp[i].getName();
		    groups.setListData(groupData);
		    numGroups=profile.getNumberGroups()-1;
		    groupBorder.setTitle(groupBorderTitle+numGroups);
		    
		    groups.setSelectedIndex(0);
		    groups.revalidate();
		    groupConcepts.revalidate();
		    
		    addGroup.setEnabled(true);
		    editGroup.setEnabled(false);
		    removeGroup.setEnabled(false);
		    editGroupsPanel.repaint();
		    editGroupsSave.setEnabled(true);
		    editGroupsNoSave.setEnabled(true);
		    editGroupsCancel.setEnabled(true);
		    if (numGroups==1) groups.clearSelection();
		}
	    }
	    else new RH_PopupError(this,"You must keep at least one group.",conceptsBackColor, conceptsColor);
	}
	else if (source == defaultGroup) {
	    profile.setDefaultGroup(currentGroupPos);
	    editGroupsSave.setEnabled(true);
	    editGroupsNoSave.setEnabled(true);
	    editGroupsCancel.setEnabled(true);
	}
	else if (source == editGroupsSave) {
	    profile.saveGroups(path+RH_GlobalVars.rhPathSeparator+user+RH_GlobalVars.rhPathSeparator);
	    editGroupsSave.setEnabled(false);
	    editGroupsNoSave.setEnabled(false);
	    editGroupsCancel.setEnabled(false);
	}
	else if (source == editGroupsNoSave) {
	    editGroupsSave.setEnabled(false);
	    editGroupsNoSave.setEnabled(false);
	    editGroupsCancel.setEnabled(false);
	}
	else if (source == editGroupsCancel) {
	    editGroupsSave.setEnabled(false);
	    editGroupsNoSave.setEnabled(false);
	    editGroupsCancel.setEnabled(false);
	}
	//** Options
	else if (source == editThumbarSave) {
	    saveProfile();
	    editThumbarSave.setEnabled(false);
	    editThumbarNoSave.setEnabled(false);
	    editThumbarCancel.setEnabled(false);
	}
	else if (source == editThumbarNoSave) {
	    editThumbarSave.setEnabled(false);
	    editThumbarNoSave.setEnabled(false);
	    editThumbarCancel.setEnabled(false);
	}
	else if (source == editThumbarCancel) {
	    editThumbarSave.setEnabled(false);
	    editThumbarNoSave.setEnabled(false);
	    editThumbarCancel.setEnabled(false);
	}
	else if (source == editOptionsSave) {
	    saveProfile();
	    editOptionsSave.setEnabled(false);
	    editOptionsNoSave.setEnabled(false);
	    editOptionsCancel.setEnabled(false);
	}
	else if (source == editOptionsNoSave) {
	    editOptionsSave.setEnabled(false);
	    editOptionsNoSave.setEnabled(false);
	    editOptionsCancel.setEnabled(false);
	}
	else if (source == editOptionsCancel) {
	    editOptionsSave.setEnabled(false);
	    editOptionsNoSave.setEnabled(false);
	    editOptionsCancel.setEnabled(false);
	}
	else if (source == readerFName) {
	    profile.setUserFirstName(readerFName.getText());
	    editOptionsSave.setEnabled(true);
	    editOptionsNoSave.setEnabled(true);
	    editOptionsCancel.setEnabled(true);
	}
	else if (source == readerLName) {
	    profile.setUserLastName(readerLName.getText());
	    editOptionsSave.setEnabled(true);
	    editOptionsNoSave.setEnabled(true);
	    editOptionsCancel.setEnabled(true);
	}
	else if (source == readerUName) {
	    profile.setUserAccountName(readerUName.getText());
	    editOptionsSave.setEnabled(true);
	    editOptionsNoSave.setEnabled(true);
	    editOptionsCancel.setEnabled(true);
	}
	else if (source == homepath) {
	    profile.setPath(homepath.getText());
	    editOptionsSave.setEnabled(true);
	    editOptionsNoSave.setEnabled(true);
	    editOptionsCancel.setEnabled(true);
	}
	else if (source == gifspath) {
	    profile.setGifsPath(gifspath.getText());
	    editOptionsSave.setEnabled(true);
	    editOptionsNoSave.setEnabled(true);
	    editOptionsCancel.setEnabled(true);
	}
	else if (source == homeurl) {
	    profile.setHomeURL(homeurl.getText());
	    editOptionsSave.setEnabled(true);
	    editOptionsNoSave.setEnabled(true);
	    editOptionsCancel.setEnabled(true);
	}
	else if (source == privatepath) {
	    profile.setPrivateDir(privatepath.getText());
	    editOptionsSave.setEnabled(true);
	    editOptionsNoSave.setEnabled(true);
	    editOptionsCancel.setEnabled(true);
	}
	else if (source == lensRatio) {
	    int fraction=Integer.parseInt(lensRatio.getText());
	    if (fraction>=minLensRatio && fraction<maxLensRatio) {
		profile.setLensViewFraction(fraction);
		editThumbarSave.setEnabled(true);
		editThumbarNoSave.setEnabled(true);
		editThumbarCancel.setEnabled(true);
	    }
	    else new RH_PopupError(this,"Thumbar ratio range: "+minLensRatio+"-"+maxLensRatio,conceptsBackColor,conceptsColor);
	}
	else if (source == useLensLogo) {
	    if (useLensLogo.isSelected()) profile.setUseLensLogo(1);
	    else profile.setUseLensLogo(0);
	    editThumbarSave.setEnabled(true);
	    editThumbarNoSave.setEnabled(true);
	    editThumbarCancel.setEnabled(true);
	}
	else if (source == useAnohDoubleLine) {
	    if (useAnohDoubleLine.isSelected()) profile.setUseAnohDoubleLine(1);
	    else profile.setUseAnohDoubleLine(0);
	    editThumbarSave.setEnabled(true);
	    editThumbarNoSave.setEnabled(true);
	    editThumbarCancel.setEnabled(true);
	}
	else if (source == useLinkDoubleLine) {
	    if (useLinkDoubleLine.isSelected()) profile.setUseLinkDoubleLine(1);
	    else profile.setUseLinkDoubleLine(0);
	    editThumbarSave.setEnabled(true);
	    editThumbarNoSave.setEnabled(true);
	    editThumbarCancel.setEnabled(true);
	}
	else if (source == animateLogo) {
	    if (animateLogo.isSelected()) profile.setAnimateLogo(1);
	    else profile.setAnimateLogo(0);
	    editOptionsSave.setEnabled(true);
	    editOptionsNoSave.setEnabled(true);
	    editOptionsCancel.setEnabled(true);
	}
	else if (source == autoloadHome) {
	    if (autoloadHome.isSelected()) profile.setAutoLoadHomeURL(1);
	    else profile.setAutoLoadHomeURL(0);
	    editOptionsSave.setEnabled(true);
	    editOptionsNoSave.setEnabled(true);
	    editOptionsCancel.setEnabled(true);
	}
	else if (source == populateConcepts) {
	    if (populateConcepts.isSelected()) profile.setPopulateConcepts(1);
	    else profile.setPopulateConcepts(0);
	    editOptionsSave.setEnabled(true);
	    editOptionsNoSave.setEnabled(true);
	    editOptionsCancel.setEnabled(true);
	}
	else if (source == imageScaling) {
	    if (imageScaling.isSelected()) profile.setUseBetterImageScalingMethod(1);
	    else profile.setUseBetterImageScalingMethod(0);
	    editOptionsSave.setEnabled(true);
	    editOptionsNoSave.setEnabled(true);
	    editOptionsCancel.setEnabled(true);
	}
	else if (source == activecache) {
	    if (activecache.isSelected()) profile.setUseCacheDocuments(1);
	    else profile.setUseCacheDocuments(0);
	    editOptionsSave.setEnabled(true);
	    editOptionsNoSave.setEnabled(true);
	    editOptionsCancel.setEnabled(true);
	}
	else if (source == largemeters) {
	    if (largemeters.isSelected()) profile.setLargeMeters(1);
	    else profile.setLargeMeters(0);
	    editOptionsSave.setEnabled(true);
	    editOptionsNoSave.setEnabled(true);
	    editOptionsCancel.setEnabled(true);
	}
	else if (source == windowwidth) {
	    int val=Integer.parseInt(windowwidth.getText());
	    if (val>0) {
		profile.setPreferredWidth(val);
		editOptionsSave.setEnabled(true);
		editOptionsNoSave.setEnabled(true);
		editOptionsCancel.setEnabled(true);
	    }
	    else new RH_PopupError(this,"Width value out of range",conceptsBackColor, conceptsColor);
	}
	else if (source == windowheight) {
	    int val=Integer.parseInt(windowheight.getText());
	    if (val>0) {
		profile.setPreferredHeight(val);
		editOptionsSave.setEnabled(true);
		editOptionsNoSave.setEnabled(true);
		editOptionsCancel.setEnabled(true);
	    }
	    else new RH_PopupError(this,"Height value out of range",conceptsBackColor, conceptsColor);
	}
	else if (source == windowx) {
	    int val=Integer.parseInt(windowx.getText());
	    if (val>=0) {
		profile.setPreferredX(val);
		editOptionsSave.setEnabled(true);
		editOptionsNoSave.setEnabled(true);
		editOptionsCancel.setEnabled(true);
	    }
	    else new RH_PopupError(this,"X value out of range",conceptsBackColor, conceptsColor);
	}
	else if (source == windowy) {
	    int val=Integer.parseInt(windowy.getText());
	    if (val>=0) {
		profile.setPreferredY(val);
		editOptionsSave.setEnabled(true);
		editOptionsNoSave.setEnabled(true);
		editOptionsCancel.setEnabled(true);
	    }
	    else new RH_PopupError(this,"Y value out of range",conceptsBackColor, conceptsColor);
	}
	else if (source == sensThreshold) {
	    int val=Integer.parseInt(sensThreshold.getText());
	    if (val>=0 && val<=100) {
		profile.setSensitivitySetting(val);
		editOptionsSave.setEnabled(true);
		editOptionsNoSave.setEnabled(true);
		editOptionsCancel.setEnabled(true);
	    }
	    else new RH_PopupError(this,"Value out of range: 0-100",conceptsBackColor, conceptsColor);
	}
	else if (source == simThreshold) {
	    int val=Integer.parseInt(simThreshold.getText());
	    if (val>=0 && val<=100) {
		profile.setSimilarityThreshold(val);
		editOptionsSave.setEnabled(true);
		editOptionsNoSave.setEnabled(true);
		editOptionsCancel.setEnabled(true);
	    }
	    else new RH_PopupError(this,"Value out of range: 0-100",conceptsBackColor, conceptsColor);
	}
	else if (source == cachesize) {
	    int val=Integer.parseInt(cachesize.getText());
	    if (val>=cacheSizeMinimum) {
		profile.setDocumentCacheSize(val);
		editOptionsSave.setEnabled(true);
		editOptionsNoSave.setEnabled(true);
		editOptionsCancel.setEnabled(true);
	    }
	    else new RH_PopupError(this,"Value invalid: range 1000000 to ????",conceptsBackColor, conceptsColor);
	}
	else if (source == textfontsize) {
	    int val=Integer.parseInt(textfontsize.getText());
	    if (val>=2 && val<20) {
		profile.setDocumentFontSize(val);
		editOptionsSave.setEnabled(true);
		editOptionsNoSave.setEnabled(true);
		editOptionsCancel.setEnabled(true);
	    }
	    else new RH_PopupError(this,"Value out of range. (range is 2-20)",conceptsBackColor, conceptsColor);
	}
	else if (source == guifontsize) {
	    int val=Integer.parseInt(guifontsize.getText());
	    if (val>=2 && val<20) {
		profile.setLocationFontSize(val);
		editOptionsSave.setEnabled(true);
		editOptionsNoSave.setEnabled(true);
		editOptionsCancel.setEnabled(true);
	    }
	    else new RH_PopupError(this,"Value out of range. (range is 2-20)",conceptsBackColor, conceptsColor);
	}
	else if (source == thumbarBackR_Color || source == thumbarBackG_Color || source == thumbarBackB_Color) {
	    int red=-1, green=-1, blue=-1;
	    red=Integer.parseInt(thumbarBackR_Color.getText());
	    green=Integer.parseInt(thumbarBackG_Color.getText());
	    blue=Integer.parseInt(thumbarBackB_Color.getText());
	    if (red>=0 && green>=0 && blue>=0) {
		profile.setOverviewWindowColor(thumbarBackColor=new Color(red,green,blue));
		backSquare.setBackground(thumbarBackColor);
		backSquare.setForeground(thumbarBackColor);   
		editThumbarSave.setEnabled(true);
		editThumbarNoSave.setEnabled(true);
		editThumbarCancel.setEnabled(true);
		editThumbarPanel.repaint();
	    }
	}
	else if (source == thumbarLensR_Color || source == thumbarLensG_Color || source == thumbarLensB_Color) {
	    int red=-1, green=-1, blue=-1;
	    red=Integer.parseInt(thumbarLensR_Color.getText());
	    green=Integer.parseInt(thumbarLensG_Color.getText());
	    blue=Integer.parseInt(thumbarLensB_Color.getText());
	    if (red>=0 && green>=0 && blue>=0) {
		profile.setOverviewLensColor(thumbarLensColor=new Color(red,green,blue));
		lensSquare.setBackground(thumbarLensColor);
		lensSquare.setForeground(thumbarLensColor);   
		editThumbarSave.setEnabled(true);
		editThumbarNoSave.setEnabled(true);
		editThumbarCancel.setEnabled(true);
		editThumbarPanel.repaint();
	    }
	}
	else if (source == thumbarWLR_Color || source == thumbarWLG_Color || source == thumbarWLB_Color) {
	    int red=-1, green=-1, blue=-1;
	    red=Integer.parseInt(thumbarWLR_Color.getText());
	    green=Integer.parseInt(thumbarWLG_Color.getText());
	    blue=Integer.parseInt(thumbarWLB_Color.getText());
	    if (red>=0 && green>=0 && blue>=0) {
		profile.setOverviewWindowLineColor(thumbarWLColor=new Color(red,green,blue));
		wlSquare.setBackground(thumbarWLColor);
		wlSquare.setForeground(thumbarWLColor);   
		editThumbarSave.setEnabled(true);
		editThumbarNoSave.setEnabled(true);
		editThumbarCancel.setEnabled(true);
		editThumbarPanel.repaint();
	    }
	}
	else if (source == thumbarANOHR_Color || source == thumbarANOHG_Color || source == thumbarANOHB_Color) {
	    int red=-1, green=-1, blue=-1;
	    red=Integer.parseInt(thumbarANOHR_Color.getText());
	    green=Integer.parseInt(thumbarANOHG_Color.getText());
	    blue=Integer.parseInt(thumbarANOHB_Color.getText());
	    if (red>=0 && green>=0 && blue>=0) {
		profile.setOverviewANOHColor(thumbarANOHColor=new Color(red,green,blue));
		anohSquare.setBackground(thumbarANOHColor);
		anohSquare.setForeground(thumbarANOHColor);   
		editThumbarSave.setEnabled(true);
		editThumbarNoSave.setEnabled(true);
		editThumbarCancel.setEnabled(true);
		editThumbarPanel.repaint();
	    }
	}
	else if (source == thumbarLinkR_Color || source == thumbarLinkG_Color || source == thumbarLinkB_Color) {
	    int red=-1, green=-1, blue=-1;
	    red=Integer.parseInt(thumbarLinkR_Color.getText());
	    green=Integer.parseInt(thumbarLinkG_Color.getText());
	    blue=Integer.parseInt(thumbarLinkB_Color.getText());
	    if (red>=0 && green>=0 && blue>=0) {
		profile.setOverviewLinkColor(thumbarLinkColor=new Color(red,green,blue));
		linkSquare.setBackground(thumbarLinkColor);
		linkSquare.setForeground(thumbarLinkColor);   
		editThumbarSave.setEnabled(true);
		editThumbarNoSave.setEnabled(true);
		editThumbarCancel.setEnabled(true);
		editThumbarPanel.repaint();
	    }
	}
	else if (source == thumbarLensLineR_Color || source == thumbarLensLineG_Color || source == thumbarLensLineB_Color) {
	    int red=-1, green=-1, blue=-1;
	    red=Integer.parseInt(thumbarLensLineR_Color.getText());
	    green=Integer.parseInt(thumbarLensLineG_Color.getText());
	    blue=Integer.parseInt(thumbarLensLineB_Color.getText());
	    if (red>=0 && green>=0 && blue>=0) {
		profile.setOverviewLensLineColor(thumbarLensLineColor=new Color(red,green,blue));
		lensLineSquare.setBackground(thumbarLensLineColor);
		lensLineSquare.setForeground(thumbarLensLineColor);   
		editThumbarSave.setEnabled(true);
		editThumbarNoSave.setEnabled(true);
		editThumbarCancel.setEnabled(true);
		editThumbarPanel.repaint();
	    }
	}
	//** highlighting
	else if (source == editHliteSave) {
	    saveProfile();
	    editHliteSave.setEnabled(false);
	    editHliteNoSave.setEnabled(false);
	    editHliteCancel.setEnabled(false);
	}
	else if (source == editHliteNoSave) {
	    editHliteSave.setEnabled(false);
	    editHliteNoSave.setEnabled(false);
	    editHliteCancel.setEnabled(false);
	}
	else if (source == editHliteCancel) {
	    editHliteSave.setEnabled(false);
	    editHliteNoSave.setEnabled(false);
	    editHliteCancel.setEnabled(false);
	}
	else if (source ==style1TextR_Color || source == style1TextG_Color || source == style1TextB_Color) {
	    int red=-1, green=-1, blue=-1;
	    red=Integer.parseInt(style1TextR_Color.getText());
	    green=Integer.parseInt(style1TextG_Color.getText());
	    blue=Integer.parseInt(style1TextB_Color.getText());
	    if (red>=0 && green>=0 && blue>=0) {
		Color newcolor=new Color(red,green,blue);
		style1TextSquare.setBackground(newcolor);
		style1TextSquare.setForeground(newcolor);   
		hstyles[0].setForeRed(red);
		hstyles[0].setForeGreen(green);
		hstyles[0].setForeBlue(blue);
		profile.setHighlightStyles(hstyles);
		editHliteSave.setEnabled(true);
		editHliteNoSave.setEnabled(true);
		editHliteCancel.setEnabled(true);
		editHlitePanel.repaint();
	    }
	}
	else if (source ==style2TextR_Color || source == style2TextG_Color || source == style2TextB_Color) {
	    int red=-1, green=-1, blue=-1;
	    red=Integer.parseInt(style2TextR_Color.getText());
	    green=Integer.parseInt(style2TextG_Color.getText());
	    blue=Integer.parseInt(style2TextB_Color.getText());
	    if (red>=0 && green>=0 && blue>=0) {
		Color newcolor=new Color(red,green,blue);
		style2TextSquare.setBackground(newcolor);
		style2TextSquare.setForeground(newcolor);
		hstyles[1].setForeRed(red);
		hstyles[1].setForeGreen(green);
		hstyles[1].setForeBlue(blue);
		profile.setHighlightStyles(hstyles);   
		editHliteSave.setEnabled(true);
		editHliteNoSave.setEnabled(true);
		editHliteCancel.setEnabled(true);
		editHlitePanel.repaint();
	    }
	}
	else if (source ==style3TextR_Color || source == style3TextG_Color || source == style3TextB_Color) {
	    int red=-1, green=-1, blue=-1;
	    red=Integer.parseInt(style3TextR_Color.getText());
	    green=Integer.parseInt(style3TextG_Color.getText());
	    blue=Integer.parseInt(style3TextB_Color.getText());
	    if (red>=0 && green>=0 && blue>=0) {
		Color newcolor=new Color(red,green,blue);
		style3TextSquare.setBackground(newcolor);
		style3TextSquare.setForeground(newcolor);   
		hstyles[2].setForeRed(red);
		hstyles[2].setForeGreen(green);
		hstyles[2].setForeBlue(blue);
		profile.setHighlightStyles(hstyles);   
		editHliteSave.setEnabled(true);
		editHliteNoSave.setEnabled(true);
		editHliteCancel.setEnabled(true);
		editHlitePanel.repaint();
	    }
	}
	else if (source ==style1BackR_Color || source == style1BackG_Color || source == style1BackB_Color) {
	    int red=-1, green=-1, blue=-1;
	    red=Integer.parseInt(style1BackR_Color.getText());
	    green=Integer.parseInt(style1BackG_Color.getText());
	    blue=Integer.parseInt(style1BackB_Color.getText());
	    if (red>=0 && green>=0 && blue>=0) {
		Color newcolor=new Color(red,green,blue);
		style1BackSquare.setBackground(newcolor);
		style1BackSquare.setForeground(newcolor);   
		hstyles[0].setRed(red);
		hstyles[0].setGreen(green);
		hstyles[0].setBlue(blue);
		profile.setHighlightStyles(hstyles);   
		editHliteSave.setEnabled(true);
		editHliteNoSave.setEnabled(true);
		editHliteCancel.setEnabled(true);
		editHlitePanel.repaint();
	    }
	}
	else if (source ==style2BackR_Color || source == style2BackG_Color || source == style2BackB_Color) {
	    int red=-1, green=-1, blue=-1;
	    red=Integer.parseInt(style2BackR_Color.getText());
	    green=Integer.parseInt(style2BackG_Color.getText());
	    blue=Integer.parseInt(style2BackB_Color.getText());
	    if (red>=0 && green>=0 && blue>=0) {
		Color newcolor=new Color(red,green,blue);
		style2BackSquare.setBackground(newcolor);
		style2BackSquare.setForeground(newcolor);   
		hstyles[1].setRed(red);
		hstyles[1].setGreen(green);
		hstyles[1].setBlue(blue);
		profile.setHighlightStyles(hstyles);   
		editHliteSave.setEnabled(true);
		editHliteNoSave.setEnabled(true);
		editHliteCancel.setEnabled(true);
		editHlitePanel.repaint();
	    }
	}
	else if (source == style3BackR_Color || source == style3BackG_Color || source == style3BackB_Color) {
	    int red=-1, green=-1, blue=-1;
	    red=Integer.parseInt(style3BackR_Color.getText());
	    green=Integer.parseInt(style3BackG_Color.getText());
	    blue=Integer.parseInt(style3BackB_Color.getText());
	    if (red>=0 && green>=0 && blue>=0) {
		Color newcolor=new Color(red,green,blue);
		style3BackSquare.setBackground(newcolor);
		style3BackSquare.setForeground(newcolor);   
		hstyles[2].setRed(red);
		hstyles[2].setGreen(green);
		hstyles[2].setBlue(blue);
		profile.setHighlightStyles(hstyles);   
		editHliteSave.setEnabled(true);
		editHliteNoSave.setEnabled(true);
		editHliteCancel.setEnabled(true);
		editHlitePanel.repaint();
	    }
	}
	else if (source == style1Bold) {
	    if (style1Bold.isSelected()) hstyles[0].setBold(1);
	    else hstyles[0].setBold(0);
	    profile.setHighlightStyles(hstyles); 
	    editHliteSave.setEnabled(true);
	    editHliteNoSave.setEnabled(true);
	    editHliteCancel.setEnabled(true);
	}
	else if (source == style2Bold) {
	    if (style2Bold.isSelected()) hstyles[1].setBold(1);
	    else hstyles[1].setBold(0);
	    profile.setHighlightStyles(hstyles); 
	    editHliteSave.setEnabled(true);
	    editHliteNoSave.setEnabled(true);
	    editHliteCancel.setEnabled(true);
	}
	else if (source == style3Bold) {
	    if (style3Bold.isSelected()) hstyles[2].setBold(1);
	    else hstyles[2].setBold(0);
	    profile.setHighlightStyles(hstyles); 
	    editHliteSave.setEnabled(true);
	    editHliteNoSave.setEnabled(true);
	    editHliteCancel.setEnabled(true);
	}
	else if (source == style1Under) {
	    if (style1Under.isSelected()) {
		hstyles[0].setUnder(1);
		hstyles[0].setBox(0);
		style1Box.setSelected(false);
	    }
	    else hstyles[0].setUnder(0);
	    profile.setHighlightStyles(hstyles); 
	    editHliteSave.setEnabled(true);
	    editHliteNoSave.setEnabled(true);
	    editHliteCancel.setEnabled(true);
	}
	else if (source == style2Under) {
	    if (style2Under.isSelected()) {
		hstyles[1].setUnder(1);
		hstyles[1].setBox(0);
		style2Box.setSelected(false);
	    }
	    else hstyles[1].setUnder(0);
	    profile.setHighlightStyles(hstyles); 
	    editHliteSave.setEnabled(true);
	    editHliteNoSave.setEnabled(true);
	    editHliteCancel.setEnabled(true);
	}
	else if (source == style3Under) {
	    if (style3Under.isSelected()) {
		hstyles[2].setUnder(1);
		hstyles[2].setBox(0);
		style3Box.setSelected(false);
	    }
	    else hstyles[2].setUnder(0);
	    profile.setHighlightStyles(hstyles); 
	    editHliteSave.setEnabled(true);
	    editHliteNoSave.setEnabled(true);
	    editHliteCancel.setEnabled(true);
	}
	else if (source == style1Box) {
	    if (style1Box.isSelected()) {
		hstyles[0].setBox(1);
		hstyles[0].setUnder(0);
		style1Under.setSelected(false);
	    }
	    else hstyles[0].setBox(0);
	    profile.setHighlightStyles(hstyles); 
	    editHliteSave.setEnabled(true);
	    editHliteNoSave.setEnabled(true);
	    editHliteCancel.setEnabled(true);
	}
	else if (source == style2Box) {
	    if (style2Box.isSelected()) {
		hstyles[1].setBox(1);
		hstyles[1].setUnder(0);
		style2Under.setSelected(false);
	    }
	    else hstyles[1].setBox(0);
	    profile.setHighlightStyles(hstyles); 
	    editHliteSave.setEnabled(true);
	    editHliteNoSave.setEnabled(true);
	    editHliteCancel.setEnabled(true);
	}
	else if (source == style3Box) {
	    if (style3Box.isSelected()) {
		hstyles[2].setBox(1);
		hstyles[2].setUnder(0);
		style3Under.setSelected(false);
	    }
	    else hstyles[2].setBox(0);
	    profile.setHighlightStyles(hstyles); 
	    editHliteSave.setEnabled(true);
	    editHliteNoSave.setEnabled(true);
	    editHliteCancel.setEnabled(true);
	}
	else if (source == style1Shadow) {
	    if (style1Shadow.isSelected()) hstyles[0].setShadow(1);
	    else hstyles[0].setShadow(0);
	    profile.setHighlightStyles(hstyles); 
	    editHliteSave.setEnabled(true);
	    editHliteNoSave.setEnabled(true);
	    editHliteCancel.setEnabled(true);
	}
	else if (source == style2Shadow) {
	    if (style2Shadow.isSelected()) hstyles[1].setShadow(1);
	    else hstyles[1].setShadow(0);
	    profile.setHighlightStyles(hstyles); 
	    editHliteSave.setEnabled(true);
	    editHliteNoSave.setEnabled(true);
	    editHliteCancel.setEnabled(true);
	}
	else if (source == style3Shadow) {
	    if (style3Shadow.isSelected()) hstyles[2].setShadow(1);
	    else hstyles[2].setShadow(0);
	    profile.setHighlightStyles(hstyles); 
	    editHliteSave.setEnabled(true);
	    editHliteNoSave.setEnabled(true);
	    editHliteCancel.setEnabled(true);
	}
	else if (source == style1Whole) {
	    if (style1Whole.isSelected()) hstyles[0].setWhole(1);
	    else hstyles[0].setWhole(0);
	    profile.setHighlightStyles(hstyles); 
	    editHliteSave.setEnabled(true);
	    editHliteNoSave.setEnabled(true);
	    editHliteCancel.setEnabled(true);
	}
	else if (source == style2Whole) {
	    if (style2Whole.isSelected()) hstyles[1].setWhole(1);
	    else hstyles[1].setWhole(0);
	    profile.setHighlightStyles(hstyles); 
	    editHliteSave.setEnabled(true);
	    editHliteNoSave.setEnabled(true);
	    editHliteCancel.setEnabled(true);
	}
	else if (source == style3Whole) {
	    if (style3Whole.isSelected()) hstyles[2].setWhole(1);
	    else hstyles[2].setWhole(0);
	    profile.setHighlightStyles(hstyles); 
	    editHliteSave.setEnabled(true);
	    editHliteNoSave.setEnabled(true);
	    editHliteCancel.setEnabled(true);
	}
	else if (source == style1Def) {
	    if (style1Def.isSelected()) profile.setDefaultHliteStyle(0);
	    style2Def.setSelected(false);
	    style3Def.setSelected(false);
	    profile.setHighlightStyles(hstyles); 
	    editHliteSave.setEnabled(true);
	    editHliteNoSave.setEnabled(true);
	    editHliteCancel.setEnabled(true);
	}
	else if (source == style2Def) {
	    if (style2Def.isSelected()) profile.setDefaultHliteStyle(1);
	    style1Def.setSelected(false);
	    style3Def.setSelected(false);
	    profile.setHighlightStyles(hstyles); 
	    editHliteSave.setEnabled(true);
	    editHliteNoSave.setEnabled(true);
	    editHliteCancel.setEnabled(true);
	}
	else if (source == style3Def) {
	    if (style3Def.isSelected()) profile.setDefaultHliteStyle(2);
	    style1Def.setSelected(false);
	    style2Def.setSelected(false);
	    profile.setHighlightStyles(hstyles); 
	    editHliteSave.setEnabled(true);
	    editHliteNoSave.setEnabled(true);
	    editHliteCancel.setEnabled(true);
	}
	else if (source == style1Tip) {
	    hstyles[0].setTip(style1Tip.getText());
	    editHliteSave.setEnabled(true);
	    editHliteNoSave.setEnabled(true);
	    editHliteCancel.setEnabled(true);
	}
	else if (source == style2Tip) {
	    hstyles[1].setTip(style1Tip.getText());
	    editHliteSave.setEnabled(true);
	    editHliteNoSave.setEnabled(true);
	    editHliteCancel.setEnabled(true);
	}
	else if (source == style2Tip) {
	    hstyles[2].setTip(style1Tip.getText());
	    editHliteSave.setEnabled(true);
	    editHliteNoSave.setEnabled(true);
	    editHliteCancel.setEnabled(true);
	}
	//** PIA printing
	else if (source == editPrintSave) {
	    saveProfile();
	    editPrintSave.setEnabled(false);
	    editPrintNoSave.setEnabled(false);
	    editPrintCancel.setEnabled(false);
	}
	else if (source == editPrintNoSave) {
	    editPrintSave.setEnabled(false);
	    editPrintNoSave.setEnabled(false);
	    editPrintCancel.setEnabled(false);
	}
	else if (source == editPrintCancel) {
	    editPrintSave.setEnabled(false);
	    editPrintNoSave.setEnabled(false);
	    editPrintCancel.setEnabled(false);
	}
	else if (source == piaHost) {
	    profile.setPiaHost(piaHost.getText());
	    editPrintSave.setEnabled(true);
	    editPrintNoSave.setEnabled(true);
	    editPrintCancel.setEnabled(true);
	}
	else if (source == piaPort) {
	    int port=Integer.parseInt(piaPort.getText());
	    if (port>=0) {
		profile.setPiaPort(port);
		editPrintSave.setEnabled(true);
		editPrintNoSave.setEnabled(true);
		editPrintCancel.setEnabled(true);
	    }
	    else new RH_PopupError(this,"Invalid PIA Port Number!");
	}
	else if (source == piaAgent) {
	    profile.setPiaAgentName(piaAgent.getText());
	    editPrintSave.setEnabled(true);
	    editPrintNoSave.setEnabled(true);
	    editPrintCancel.setEnabled(true);
	}
	else if (source == piaPrinter) {
	    profile.setPiaPrinterName(piaPrinter.getText());
	    editPrintSave.setEnabled(true);
	    editPrintNoSave.setEnabled(true);
	    editPrintCancel.setEnabled(true);
	}
  }
    private void setupConcept(RHActiveConcept concept) {
	currentConcept=concept;
	topicsData=new String[0];
	topicsList.setListData(topicsData);
	
	shortNameField.setText(currentConcept.getShortName());
	prior.setText(new String(""+currentConcept.getPrior()));
	activeField.setSelected(currentConcept.isActive());
	int len=currentConcept.getLength();
	
	topicsBorder.setTitle(topicBorderTitle+len);
	topicsData=currentConcept.getTopicKeywordStrings();
	topicsList.setListData(topicsData);
	currentTopic.setText("");
	topicsList.repaint();
	editConceptsPanel.repaint();
    }

    public void setupCurrentGroup() {
	if (currentGroupPos>=0) {
	    currentGroup=profile.getGroup(currentGroupPos);
	    groupConceptsData=currentGroup.getConcepts();
	    if (groupConceptsData!=null) {
		groupConcepts.setListData(groupConceptsData);
		groupConceptsBorder.setTitle(groupConceptsBorderTitle+groupConceptsData.length);
	    }
	    else {
		groupConcepts.setListData(groupConceptsData);
		groupConceptsBorder.setTitle(groupConceptsBorderTitle+"0");
	    }
	    if (profile.getDefaultGroup()==currentGroupPos) defaultGroup.setSelected(true);
	    else defaultGroup.setSelected(false);
	    groupToolTip.setText(currentGroup.getToolTipString());
	    
	    groupConcepts.revalidate();
	    editGroupsPanel.repaint();
	}
	//else System.out.println("**Profile Editor Err: invalid index value in Groups List");
    }

    public  void valueChanged(ListSelectionEvent ev) {
	Object source=ev.getSource();
	int who=ev.getFirstIndex();

	if (source==concepts) {
	    editConcept.setEnabled(true);
	    removeConcept.setEnabled(true);
	    currentConceptPos=concepts.getSelectedIndex();
	    currentTopicPos=-1;
	    editTopic.setEnabled(true);
	    editTopic.setText("Add");
	    delTopic.setEnabled(false);
	    RHActiveConcept concept=profile.findConcept((String)concepts.getSelectedValue()); //conceptData[who]);
	    if (concept!=null) setupConcept(concept);
	    else System.out.println("***ERROR: concept not found!");
	}
	else if (source==topicsList && who>=0 && topicsData.length>who) {
	    currentTopicPos=topicsList.getSelectedIndex();
	    editTopic.setEnabled(true);
	    editTopic.setText("Edit");
	    delTopic.setEnabled(true);
	    currentTopic.setText((String)topicsList.getSelectedValue()); //topicsData[who]);
	}
	else if (source==groups) {
	    //System.out.println("groups selected:"+groups.getSelectedIndex());
	    currentGroupPos=groups.getSelectedIndex();
	    addGroup.setEnabled(true);
	    editGroup.setEnabled(true);
	    removeGroup.setEnabled(true);
	    setupCurrentGroup();
	}
	else if (source==groupAllConcepts) {
	    currentConcept=profile.findConcept((String)groupAllConcepts.getSelectedValue()); //conceptData[who]);
	    addGroupConcept.setEnabled(true);
	    removeGroupConcept.setEnabled(false);
	}
	else if (source==groupConcepts) {
	    currentGroupConceptPos=groupConcepts.getSelectedIndex();
	    removeGroupConcept.setEnabled(true);
	    addGroupConcept.setEnabled(false);
	}
	else if (source==textfont) {
	    if (textfont.getSelectedIndex()>=0) {
		String newfont=fontData[textfont.getSelectedIndex()];
		profile.setDocumentFontName(newfont);
		//int idx=memberReturnIdx(commBus.getDocumentFontName(),fontData);
		//if (idx>=0) textfont.setSelectedIndex(idx);
		editOptionsSave.setEnabled(true);
		editOptionsNoSave.setEnabled(true);
		editOptionsCancel.setEnabled(true);
	    }

	}
	else if (source==guifont) {
	    if (guifont.getSelectedIndex()>=0) {
		String newfont=fontData[guifont.getSelectedIndex()];
		profile.setLocationFontName(newfont);
		//int idx=memberReturnIdx(commBus.getLocationFontName(),fontData);
		//if (idx>=0) guifont.setSelectedIndex(idx);
		editOptionsSave.setEnabled(true);
		editOptionsNoSave.setEnabled(true);
		editOptionsCancel.setEnabled(true);
	    }

	}
    
    }

    public boolean member(String item, String[] list) {
	int i=0;
	boolean found=false;
	for (i=0; i<list.length && !found;i++) if (item.equalsIgnoreCase(list[i])) found=true;
	return found;	
    }

    /**
     * Returns the index location if it find a item in a list.  Returns -1 otherwise
     */
    public int memberReturnIdx(String item, String[] list) {
	int i=0;
	for (i=0; i<list.length;i++) if (item.equalsIgnoreCase(list[i])) return i;
	return -1;	
    }

    /**
     * When you edit a concept's shortname, you must find all groups this concept is listed in and change the
     * short name so that the group will continue to use the conept
     */
    private void updateConceptInGroups(String oldShortName) {
	String shortName=currentConcept.getShortName();
	RH_ConceptGroup[] tmp=profile.getConceptGroups();
	int idx=0;
	for (int i=0;i<tmp.length-1;i++) {
	    String[] list=tmp[i].getConcepts();
	    if ((idx=memberReturnIdx(oldShortName,list))>0) {
		list[idx]=shortName;
		profile.setGroupConcepts(i,list);
	    }
	}
    }
    
    public void stateChanged(ChangeEvent ev) {
	if (tabbedPane.getSelectedComponent()==editGroupsPanel) {
	}
    }

    private void buildConstraints (GridBagConstraints constraints, int gx, int gy, int gw, int gh, int wx, int wy) {
	constraints.gridx = gx;
	constraints.gridy = gy;
	constraints.gridwidth = gw;
	constraints.gridheight = gh;
	constraints.weightx = wx;
	constraints.weighty = wy;
    }

    private void createSimilarDirs() {
	RHActiveConcept concept=null;
	File file=null;
	StringBuffer filename;
	String pathSep=RH_GlobalVars.rhPathSeparator;
	for (int i=0;i<conceptData.length;i++) {
	    concept=profile.findConcept(conceptData[i]);
	    if (concept!=null) {
		filename=new StringBuffer().append(path).append(pathSep).append(RH_GlobalVars.rhSimilarDir).append(pathSep).append(concept.getShortName());
		file=new File(filename.toString());
		if (!file.exists()) {
		    try {
			file.mkdir();
		    } catch (SecurityException ex) {
			new RH_PopupError(this,"Could not create similarity subdir:"+concept.getShortName());
		    }
		}
	    }
	}
    }

    private void saveConcepts () {
	profile.saveConcepts(path+RH_GlobalVars.rhPathSeparator+user+RH_GlobalVars.rhPathSeparator);
	if (modifiedConcepts) {
	    createSimilarDirs();
	    modifiedConcepts=false;
	} 
    }

    private void saveProfile() {
	profile.saveProfile(path+RH_GlobalVars.rhPathSeparator+user+RH_GlobalVars.rhPathSeparator);
	invalidate();
	//commBus.updateGUIComponents();
	validate();
	
    }
}

