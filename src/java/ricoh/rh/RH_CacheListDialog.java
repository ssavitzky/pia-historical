/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: RH_KeywordsDialog:  shows the keywords associated with a concept
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 01.20.98
 *
 */
package ricoh.rh;

import java.awt.*;
import java.awt.event.*;
import jclass.bwt.JCList;
import jclass.bwt.JCButton;
import jclass.bwt.JCButtonListener;
import jclass.bwt.JCActionListener;
import jclass.bwt.JCActionEvent;
import jclass.bwt.JCButtonEvent;

class RH_CacheListDialog extends Dialog 
implements JCActionListener, JCButtonListener {

  private JCButton ok;
  private JCList list;
  private TextField text;
  private int width=560, height=400, x=20,y=20;
  private static String titlestr="Current Cache Entries";

  public RH_CacheListDialog (RH_MainFrame mainFrame) {
    super (mainFrame.commBus.parent,titlestr,false);

    Panel panel = new Panel();
    Font font=new Font("Arial",Font.PLAIN,10);
    panel.setFont(font);
    panel.setLayout(new BorderLayout());

    list = new JCList();
    list.setBackground(Color.black);
    list.setForeground(Color.yellow);   
    String[] cacheList=mainFrame.commBus.getCacheStack();
    int len=cacheList.length;
    for (int i=0; i<len; i++) {
      list.addItem(cacheList[i]);
    }
    list.setFont(font);
    panel.add("Center",list);

    ok=new JCButton();
    ok.setLabel("OK");
    ok.setFont(font);
    ok.setForeground(Color.cyan);
    ok.setBackground(Color.darkGray);
    panel.add("South",ok);
    ok.addActionListener(this); 

    add(panel);
    setLocation(x,y);
    setSize(width,height);
    setTitle(titlestr);
    setModal(false);
    show();
    
  }

  public void actionPerformed(JCActionEvent ev) {
    Object source = ev.getSource();
    System.out.println("**EVENT:"+source);
    if (source == ok) {
      System.out.println("OK BUTTON");
      dispose();
    }
  }

  public void buttonArmBegin(JCButtonEvent ev) {}
  public void buttonArmEnd(JCButtonEvent ev) {}
  public void buttonDisarmBegin(JCButtonEvent ev) {}
  public void buttonDisarmEnd(JCButtonEvent ev) {}

}

