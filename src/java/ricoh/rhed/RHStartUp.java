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
package ricoh.rhed;


import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.swing.*;

public class RHStartUp extends JDialog implements ActionListener {
    private JButton cont, cancel;
    private int width=350, height=150;
    private boolean action=false;
    private Color backColor=Color.lightGray, foreColor=Color.black;
    private JTextField username, path;
    private String usernameStr, pathStr, title="Profile Editor - Please enter user name";

    public RHStartUp(Frame parent, String defpath) {
	super(parent,"Information",true);
	JPanel panel=new JPanel();
	panel.setLayout(new BorderLayout());
	getContentPane().add(panel);
	panel.setBackground(backColor);
	panel.setForeground(foreColor);
	pathStr=defpath;

	JLabel label=new JLabel(title);
	panel.add(label,BorderLayout.NORTH);
	
	JPanel inputPanel=new JPanel();
	inputPanel.setLayout(new BorderLayout());
	username=new JTextField();
	username.setBackground(backColor);
	username.setForeground(foreColor);
	username.addActionListener(this);
	inputPanel.add(username,BorderLayout.NORTH);
	path=new JTextField(pathStr);
	path.setBackground(backColor);
	path.setForeground(foreColor);
	path.addActionListener(this);
	inputPanel.add(path,BorderLayout.SOUTH);
	panel.add(inputPanel,BorderLayout.CENTER);

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
	//setVisible(true);
	Dimension size=Toolkit.getDefaultToolkit().getScreenSize();
	System.out.println("Size w="+size.width+"  size.height="+size.height+" w="+width+" h="+height);
	int x=(int)(size.width/2)-(width/2), y=(int)(size.height/2)-(height-2);
	System.out.println("x="+ x+" y="+y);
	setLocation(x,y);
	setSize(width,height);
    }

    public void actionPerformed(ActionEvent ev) {
	Object source=ev.getSource();
	if (source==cont) {
	    action=true;
	    usernameStr=username.getText();
	    pathStr=path.getText();
	    dispose();
	}
	else if (source == username) {
	    usernameStr=username.getText();
	}
	else if (source == path) {
	    pathStr=path.getText();
	}
	else {
	    action=false;
	    dispose();
	}
    }

    public String getUsername() {
	return usernameStr;
    }
    public String getPath() {
	return pathStr;
    }

    public boolean getAction() {
	return action;
    }

}
