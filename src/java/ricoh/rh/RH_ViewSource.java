/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 11.09.98
 *
 */
package ricoh.rh;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.accessibility.*;
import java.io.*;
import java.net.*;

import javax.swing.border.*;
import javax.swing.BorderFactory;

public class RH_ViewSource extends JDialog implements ActionListener {
    private RH_CommBus commBus;
    private JTextArea document;
    private JButton ok;
    private int width=639,height=479;
	
    RH_ViewSource (ReadersHelper parent, byte[] buffer) {
	commBus=parent.commBus;
	parent.setCursor(new Cursor(Cursor.WAIT_CURSOR));

	JPanel panel=new JPanel();
	panel.setLayout(new BorderLayout());
	getContentPane().add(panel);

	//String text=loadFile("/ReadersHelper/ricoh/rh/RH_ViewSource.java");
	String text=new String(buffer);
	document=new JTextArea(text);
	document.setBackground(Color.gray);
	document.setForeground(Color.yellow);
	JScrollPane scroller = new JScrollPane();
	scroller.getViewport().add(document);

	panel.add(scroller,BorderLayout.CENTER);
	ok=new JButton("OK");
	ok.addActionListener(this);
	panel.add(ok,BorderLayout.SOUTH);

	Point loc=commBus.documentControl.getLocation();
	Dimension size=parent.getSize();
	int x=(int)(loc.x+(size.width/2))-(width/2)-20, y=loc.y+100;
	setSize(width,height);
	setLocation(x,y);
	setModal(false);
	parent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	show();
    }

    public static String loadFile(String filename) {
	String s = new String();
	File f;
	char[] buff = new char[50000];
	InputStream is;
	InputStreamReader reader;
	URL url;

	try {
	    f = new File(filename);
	    reader = new FileReader(f);
	    int nch;
	    while ((nch = reader.read(buff, 0, buff.length)) != -1) {
		s = s + new String(buff, 0, nch);
	    }
	} catch (java.io.IOException ex) {
	    s = "Could not load file: " + filename;
	}
	
	return s;
    }

  public void actionPerformed(ActionEvent ev) {
      Object source = ev.getSource();
      if (source==ok) {
	  dispose();
      }
  }

}
