/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 8.28.98
 *
 */
package ricoh.rh;


import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.swing.*;

public class RH_PopupMsg extends JDialog implements ActionListener {
    private JButton cont, cancel;
    private int width=350, height=80;
    private boolean action=false;
    private Color backColor=Color.lightGray, foreColor=Color.black;

    public RH_PopupMsg(Frame parent, String msg) {
	this(parent,msg,Color.lightGray, Color.black); //backColor,foreColor);
    }
    public RH_PopupMsg(Frame parent, String msg, Color bColor, Color fColor) {
	super(parent,"Warning!",true);
	JPanel panel=new JPanel();
	panel.setLayout(new BorderLayout());
	getContentPane().add(panel);
	panel.setBackground(backColor=bColor);
	panel.setForeground(foreColor=fColor);

	JLabel label=new JLabel(msg,JLabel.CENTER);
	panel.add(label,BorderLayout.NORTH);
	JPanel buttonPanel=new JPanel();
	buttonPanel.setBackground(backColor);
	buttonPanel.setForeground(foreColor);
	buttonPanel.setLayout(new BorderLayout());
	cont=new JButton("Continue");
	cont.setBackground(backColor);
	cont.setForeground(foreColor);
	cont.addActionListener(this);
	buttonPanel.add(cont,BorderLayout.WEST);
	cancel=new JButton("Cancel");
	cancel.setBackground(backColor);
	cancel.setForeground(foreColor);
	cancel.addActionListener(this);
	buttonPanel.add(cancel,BorderLayout.EAST);
	panel.add(buttonPanel,BorderLayout.SOUTH);
	
	action=false;
	Dimension size=parent.getSize();
	Point loc=parent.getLocationOnScreen();
	int x=(int)(loc.x+(size.width/2))-(width/2), y=(int)((loc.y+(size.height/2))-(height-2));
	setSize(width,height);
	setLocation(x,y);
	setModal(true);
	show();
    }

    public void actionPerformed(ActionEvent ev) {
	Object source=ev.getSource();
	if (source==cont) {
	    action=true;
	    dispose();
	}
	else {
	    action=false;
	    dispose();
	}
    }
    public boolean getAction() {
	return action;
    }

}
