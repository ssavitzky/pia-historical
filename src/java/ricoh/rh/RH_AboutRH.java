/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: RH_AboutRH: About dialog for RH (with version in it)
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 01.07.98
 *
 */
package ricoh.rh;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import jclass.bwt.*;
import javax.swing.*;

class RH_AboutRH extends JDialog 
implements ActionListener {

  private JButton ok;
  private int width=490, height=120;
  private static String titlestr="About the Reader's Helper", rsvString="Copyright (c) 1998 Ricoh Silicon Valley, Inc.";
  private Font font=new Font("Sans Serif",Font.PLAIN,10);

  public RH_AboutRH (ReadersHelper parent, String ver, String otherInfo) {
    super (parent,titlestr,false);
    String newline=parent.mainFrame.getNewlineByte();
    Color backColor=Color.lightGray,textColor=Color.blue;

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());

    String version="Ricoh RH Browser/"+ver+" ("+parent.RH_JDK_Version;


    ImageIcon logo=new ImageIcon(parent.commBus.getPrivateDir()+"/images/rhlogo.gif");
    JLabel iconLabel=new JLabel("",logo,JLabel.CENTER);
    iconLabel.setBackground(backColor);
    iconLabel.setForeground(textColor);
    iconLabel.setBorder(BorderFactory.createBevelBorder(1,Color.white,Color.gray));
    iconLabel.setOpaque(false);
    panel.add(iconLabel,BorderLayout.CENTER);


    JPanel buttonpanel = new JPanel();
    buttonpanel.setBackground(backColor);
    buttonpanel.setForeground(textColor);
    buttonpanel.setLayout(new BorderLayout());
    panel.add(buttonpanel,BorderLayout.SOUTH);

    JPanel labelpanel = new JPanel();
    labelpanel.setBackground(backColor);
    labelpanel.setForeground(textColor);
    labelpanel.setLayout(new BorderLayout());
    
    StringBuffer vstr=new StringBuffer().append(version).append(parent.commBus.getThumbarVersion());
    JLabel versionLabel=new JLabel(vstr.toString(),JLabel.CENTER);
    versionLabel.setBackground(backColor);
    versionLabel.setForeground(textColor);
    versionLabel.setFont(font);
    labelpanel.add(versionLabel,BorderLayout.NORTH);

    vstr=new StringBuffer().append("Other: ").append(parent.getSystemVersion());  
    JLabel systemLabel=new JLabel(vstr.toString(),JLabel.CENTER);
    systemLabel.setBackground(backColor);
    systemLabel.setForeground(textColor);
    systemLabel.setFont(font);
    labelpanel.add(systemLabel,BorderLayout.CENTER);

    vstr=new StringBuffer().append(rsvString);  
    JLabel titleLabel=new JLabel(vstr.toString(),JLabel.CENTER);
    titleLabel.setBackground(backColor);
    titleLabel.setForeground(textColor);
    titleLabel.setFont(font);
    labelpanel.add(titleLabel,BorderLayout.SOUTH);
    if (otherInfo!=null) {
      JLabel otherlabel=new JLabel(otherInfo,JLabel.CENTER);
      otherlabel.setBackground(backColor);
      otherlabel.setForeground(textColor);
      otherlabel.setFont(font);
      buttonpanel.add(otherlabel,BorderLayout.CENTER);
    }
    buttonpanel.add(labelpanel,BorderLayout.NORTH);

    String str="OK";
    ok=new JButton(str);
    ok.setForeground(textColor);
    ok.setBackground(backColor);
    buttonpanel.add(ok,BorderLayout.SOUTH);
    ok.addActionListener(this); 

    getContentPane().add(panel);
    
    width=logo.getIconWidth()+240;
    if (otherInfo!=null) height=logo.getIconHeight()+140;
    else height=logo.getIconHeight()+100;
    Point loc=parent.commBus.documentControl.getLocation();
    Dimension size=parent.getSize();
    int x=(int)(loc.x+(size.width/2))-(width/2), y=(int)((loc.y+(size.height/2))-(height-2));
    setLocation(x,y);
    setSize(width,height);
    setTitle(titlestr);
    setModal(false);
    show();
    
  }

  public void actionPerformed(ActionEvent ev) {
    Object source = ev.getSource();
    if (source == ok) {
      System.out.println("OK BUTTON");
      dispose();
    }
  }

}

