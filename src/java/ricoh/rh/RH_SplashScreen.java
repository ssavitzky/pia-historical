/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: RH_SplashScreen
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 01.20.98
 *
 */
package ricoh.rh;

import java.awt.*;
import javax.swing.*;

class RH_SplashScreen extends JFrame {

  private int width=300, height=220;
  private Image logo, backBuffer;
  private Graphics backGC=null;
  private String version="", systemVersion="";
  private Font titlefont, textfont, copyfont;
  private String message="", loadingString="Loading: ", copyString="Copyright (c) 1998 Ricoh Silicon Valley, Inc.";
  private boolean newUpdate;
  private JLabel loadingLabel;

  public RH_SplashScreen(Frame parent, String ver, String sys) {
    setBackground(Color.lightGray);
    version=ver;
    systemVersion=sys;
    //String versionString=new StringBuffer().append("Version: ").append(version).append(" System: ").append(systemVersion).toString();      
    String versionString=new StringBuffer().append("Version: ").append(version).toString();      
    message="Main";
    titlefont=new Font("Serif",Font.BOLD,12);
    textfont=new Font("Sans Serif",Font.PLAIN,10);
    copyfont=new Font("Sans Serif",Font.PLAIN,10);

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    Color backColor=Color.lightGray, titleColor=Color.black, textColor=Color.blue;
    getContentPane().add(panel);
    panel.setBackground(backColor);
    
    ImageIcon logo=new ImageIcon("private/images/rhlogo.gif");  // since i do not have the path yet, ???
    JLabel iconLabel=new JLabel("",logo,JLabel.CENTER);
    iconLabel.setBackground(backColor);
    iconLabel.setForeground(titleColor);
    iconLabel.setOpaque(false);
    iconLabel.setBorder(BorderFactory.createBevelBorder(1,Color.white,Color.gray));
    panel.add(iconLabel,BorderLayout.CENTER);

    loadingLabel=new JLabel(loadingString+message,JLabel.CENTER);
    loadingLabel.setBackground(Color.lightGray.darker());//backColor.darker());
    loadingLabel.setForeground(Color.blue);
    loadingLabel.setFont(textfont);
    panel.add(loadingLabel, BorderLayout.SOUTH);

    /*
    JLabel versionLabel=new JLabel(versionString,JLabel.CENTER);
    versionLabel.setBackground(backColor);
    versionLabel.setForeground(titleColor);
    versionLabel.setFont(copyfont);
    titlepanel.add("South",versionLabel);

    JLabel copyLabel=new JLabel(copyString,JLabel.CENTER);
    copyLabel.setBackground(backColor);
    copyLabel.setForeground(titleColor);
    copyLabel.setFont(copyfont);
    titlepanel.add("North",copyLabel);
    */
    // panel.add(titlepanel,BorderLayout.CENTER);

    width=logo.getIconWidth();
    height=logo.getIconHeight()+50;
    setVisible(true);
    Dimension size=Toolkit.getDefaultToolkit().getScreenSize();
    int x=(int)(size.width/2)-(width/2), y=(int)(size.height/2)-(height-2);
    setLocation(x,y);
    setSize(width,height);
    show();
    repaint();
  }
  public void newMsg(String msg) {
    message=msg;
    loadingLabel.setText(loadingString+message);
    repaint();
  }
  public void finalize() throws Throwable {
    //logo.flush();
    getGraphics().dispose();
    //logo=null;
  }

}
