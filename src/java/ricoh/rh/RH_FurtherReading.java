/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 5.13.98
 *
 */
package ricoh.rh;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.BorderFactory;

class RH_FurtherReading extends JPanel implements ActionListener, ListSelectionListener {
    private RH_CommBus commBus;
    private String[] names, titles;
    private int[] values;
    private JList readingList;
    private JScrollPane scrollPane;
    private int width=0, top=11,bottom=3,left=1,right=1, b_top=1, b_bottom=1, b_left=0, b_right=0, currentValue;
    private Color backColor, titleColor, listBackColor=new Color(100,100,100), listForeColor=Color.yellow;
    private String gifspath="", gifext=".gif", frButtonName="frb";
    private JButton button, closeButton;
    private ImageIcon buttonIcon;
    private int list_w=128, list_h=90, mlist_h=list_h-60;

    RH_FurtherReading(RH_CommBus bus, Color bColor, Color titlecolor, int w, Font titlefont) {
	commBus=bus;
	width=w;
	backColor=bColor; 
	titleColor=titlecolor;
	GridBagLayout gbl = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();
	//setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED,Color.lightGray,Color.black),
	//		   "FurtherReading",TitledBorder.LEFT,TitledBorder.TOP,titlefont,titleColor));
	setBorder(new EtchedBorder(EtchedBorder.LOWERED,Color.lightGray,Color.black));
	listForeColor=commBus.getModeTextColor();
	//setLayout(gbl);
	setLayout(new BorderLayout());

	setDoubleBuffered(true);
	setBackground(backColor);
	Font listFont=new Font("Sans Serif",Font.BOLD,10);
	gifspath=commBus.getGifsPath();
	
	readingList=new JList();
	readingList.setFont(listFont);
	//readingList.setBorder(new BevelBorder(BevelBorder.LOWERED,Color.white,Color.gray));
	readingList.addListSelectionListener(this);
	readingList.setForeground(listForeColor);
	readingList.setBackground(listBackColor);
	readingList.setCellRenderer(new RH_FRCellRenderer(readingList,this,gifspath));

	closeButton=new JButton(new ImageIcon(gifspath+"/"+"frbclose"+gifext));
	closeButton.setBorderPainted(false);
	closeButton.setDoubleBuffered(true);
	closeButton.setBackground(backColor);
	closeButton.setToolTipText("Close List");
	closeButton.setEnabled(true);
	closeButton.addActionListener(this);
	
	Dimension closeSize=closeButton.getSize();
	if (commBus.getLargeMeters()) {
	    list_w=128; list_h=110-closeSize.height-2; mlist_h=list_h-60;
	}
	else {
	    list_w=120; list_h=50-closeSize.height-2; mlist_h=30;
	}

	scrollPane = new JScrollPane(readingList);
	scrollPane.setFont(listFont);
	scrollPane.setBackground(listBackColor);
	scrollPane.setForeground(listForeColor);
	//scrollPane.setAlignmentX(LEFT_ALIGNMENT);
	//scrollPane.setAlignmentY(TOP_ALIGNMENT);
	scrollPane.setMaximumSize(new Dimension(list_w,list_h)); //new Dimension(width,100));
	scrollPane.setMinimumSize(new Dimension(list_w-20,mlist_h)); //new Dimension(width,100));
	scrollPane.setPreferredSize(new Dimension(list_w,list_h)); //new Dimension(width,50));
	//add(scrollPane);
	/*
	gbc.anchor = GridBagConstraints.CENTER;
	gbc.fill = GridBagConstraints.BOTH;
	buildConstraints(gbc,0,0,1,1,50,50);
	gbl.setConstraints(scrollPane,gbc);
	*/
	//scrollPane.validate();
	buttonIcon=new ImageIcon(gifspath+"/"+"frb0"+gifext);
	button=new JButton(buttonIcon);
	button.setBackground(backColor);
	button.setDisabledIcon(buttonIcon);
	button.setBorderPainted(false);
	button.setDoubleBuffered(true);
	button.setToolTipText("FurtherReading list");
	button.setEnabled(false);
	button.addActionListener(this);
	add(button);

	//setSize(width,100);
	//setInsets(top,left,bottom,right);
	setInsets(top,left,bottom,right);
	setVisible(true);
    }
    public void makeListAvailable(int num) {
	if (num>=1 && num <=3) buttonIcon=new ImageIcon(gifspath+"/"+frButtonName+num+gifext);
	else buttonIcon=new ImageIcon(gifspath+"/"+frButtonName+"0"+gifext);
	currentValue=num;
	button.setIcon(buttonIcon);
	button.setEnabled(true);
    }
    public void makeListUnavailable() {
	buttonIcon=new ImageIcon(gifspath+"/"+frButtonName+"off"+gifext);
	button.setIcon(buttonIcon);
	button.setDisabledIcon(buttonIcon);
	button.setEnabled(false);
    }
    public void showList() {
	commBus.parentInvalidate();
	setInsets(top,left,bottom,right);
	remove(button);
	add(scrollPane,BorderLayout.CENTER);
	add(closeButton,BorderLayout.NORTH);
	Dimension closeSize=closeButton.getSize();
	setSize(width,list_h+closeSize.height+20);
	commBus.parentValidate();
    }
    public void showButton() {
	showTheButton(frButtonName+"off");
	button.setEnabled(false);
    }
    public void showButton(int num) {
	showTheButton(frButtonName+num);
	button.setEnabled(true);
    }
    private void showTheButton(String filename) {
	commBus.parentInvalidate();
	setInsets(b_top,b_left,b_bottom,b_right);
	remove(scrollPane);
	remove(closeButton);
	buttonIcon=new ImageIcon(gifspath+"/"+filename+gifext);
	button.setIcon(buttonIcon);
	add(button);
	commBus.parentValidate();
    }

    public void updateReadingList(String[] docs, int[] vals) {
	System.out.println("###In update reading list...");
	names=new String[docs.length];
	titles=new String[docs.length];
	values=new int[vals.length];
	System.arraycopy(docs,0,names,0,docs.length);
	System.arraycopy(vals,0,values,0,vals.length);
	for (int i=0;i<names.length;i++) {
	    titles[i]=commBus.getPrivateDocumentTitle(names[i]);
	}
	readingList.setListData(names);
    }

    public void reset() {
	names=new String[0];
	values=new int[0];
	titles=new String[0];
	readingList.setListData(names);
	currentValue=-1;
	showButton();
    }

    public int getListItemValue(int idx) {
	return (values!=null && idx<values.length ? values[idx] : 0);
    }
    public String getListItemName(int idx) {
	return (names!=null && idx<names.length ? names[idx] : null);
    }
    public String getListItemTitle(int idx) {
	return (titles!=null && idx<titles.length ? titles[idx] : null);
    }

    public void actionPerformed(ActionEvent evt) {  
	Object source = evt.getSource();  
	if (source == button) {
	    showList();
	}
	else if (source == closeButton) {
	    showButton(currentValue);
	}
    }

    public  void valueChanged(ListSelectionEvent ev) {
	Object source=ev.getSource();
	int who=ev.getFirstIndex();

	if (source==readingList && who>=0) {
	    commBus.statusMsg1("Title:"+titles[who]+" ("+values[who]+"%): "+names[who]);
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

    public void setInsets (int newtop, int newleft, int newbottom, int newright) {
	top=newtop; bottom=newbottom;
	left=newleft; right=newright;
	getInsets();
    }
    public Insets getInsets() {
	return new Insets(top,left,bottom,right);
    }

}
