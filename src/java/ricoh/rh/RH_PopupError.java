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

public class RH_PopupError extends JDialog implements ActionListener {
    private JButton ok;
    private int width=350, height=80;
    private Color backColor=Color.lightGray, foreColor=Color.black;

    public RH_PopupError(Frame parent, String msg, Color bcolor, Color fcolor) {
	backColor=bcolor;
	foreColor=fcolor;
	setup(parent,msg);
    }
    public RH_PopupError(Frame parent, String msg) {
	setup(parent,msg);
    }
    private void setup(Frame parent, String msg) {
	JPanel panel=new JPanel();
	getContentPane().add(panel);
	panel.setBackground(backColor);
	panel.setForeground(foreColor);

	JLabel label=new JLabel(msg,JLabel.CENTER);
	label.setBackground(backColor);
	label.setForeground(foreColor);
	panel.add(label,BorderLayout.NORTH);
	ok=new JButton("OK");
	ok.addActionListener(this);
	ok.setBackground(backColor);
	ok.setForeground(foreColor);
	panel.add(ok,BorderLayout.SOUTH);
	
	Dimension size=parent.getSize();
	Point loc=parent.getLocationOnScreen();
	int x=(int)(loc.x+(size.width/2))-(width/2), y=(int)((loc.y+(size.height/2))-(height-2));
	setSize(width,height);
	setLocation(x,y);
	setModal(true);
	show();
    }

    public void actionPerformed(ActionEvent ev) {
	dispose();
    }
}
