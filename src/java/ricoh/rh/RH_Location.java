/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 *  Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 6.30.97 - revised 02-06-98
 *
 */
package ricoh.rh;

import java.util.StringTokenizer;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.BorderFactory;


class RH_Location extends JPanel
implements ActionListener, KeyListener {
  public RH_CommBus commBus;

  private JTextField textURL;
  private Color backColor=Color.gray, textColor=Color.white, locationBackColor=Color.lightGray, locationTextColor=Color.black,
    shadowColor=Color.black, shadowColor2=Color.gray, highlightColor=Color.white, wheat=new Color(247,223,181);
  private String fontName="Arial";
  private int viewFrame_w=50, viewFrame_h=36, viewFrame_x=0, viewFrame_y=0, right_inset=4, left_inset=3;
  private int top=6, left=5, bottom=7, right=20, fontSize=10;
  
  public RH_Location (RH_CommBus bus,String title, int w, int h) {
    super();
    commBus=bus;
    //setInsets(new Insets(top,left,bottom,right));
    backColor=commBus.getMainBackColor();
    textColor=commBus.getMainTextColor();
    highlightColor=commBus.getMainHighlightColor();
    shadowColor=commBus.getMainShadowColor();
    shadowColor2=commBus.getMainShadowColor2();
    int motif=commBus.getMotifNumber();

    if (motif==1) {
      setBackground(backColor);
      setForeground(textColor);
      locationBackColor=backColor;
      locationTextColor=commBus.getLocationTextColor(); //Color.white;
    }
    else {
      setBackground(backColor);
      setForeground(textColor);
      locationBackColor=backColor;
      locationTextColor=commBus.getLocationTextColor(); //Color.black;
    }
    fontName=commBus.getLocationFontName();
    fontSize=commBus.getLocationFontSize();

    int viewx=0;
    setLayout(new BorderLayout());

    textURL = new JTextField(commBus.mainFrame.getHomeURL());
    textURL.addActionListener(this);
    //textURL.setInsets (new Insets(0,1,1,1)); // new Insets(1,1,1,1));
    //textURL.setShadowThickness(0);
    //textURL.setShowCursorPosition(true);
    textURL.setEditable(true);
    textURL.setBackground(locationBackColor);
    textURL.setForeground(locationTextColor);
    textURL.setSelectionColor(Color.lightGray);
    textURL.setSelectedTextColor(Color.blue);
    textURL.setCaretColor(Color.green);
    textURL.setBorder(new EmptyBorder(0,0,0,0));
    textURL.addKeyListener(this);
    add("Center",textURL);
    if (commBus.getLocationUseBold()) textURL.setFont(new Font(fontName,Font.BOLD,fontSize));
    else textURL.setFont(new Font(fontName,Font.PLAIN,fontSize));

    //setBorder(BorderFactory.createEtchedBorder(shadowColor,highlightColor)); //Color.black,Color.lightGray));
    //setBorder(BorderFactory.createBevelBorder(0,Color.black,Color.lightGray));
    setBorder(BorderFactory.createBevelBorder(0,Color.black,Color.lightGray));
    setSize(w,h+50);
    getPreferredSize();
    setVisible(true);
  }
  
  public Insets getInsets() {
    return new Insets(top,left,bottom,right);
  }

  /*
  public void update (Graphics gc) {
    paint(gc);
  }

  public void paint(Graphics gc) {
    Dimension size     = getSize();
    Dimension labelSize=textURL.getSize();
    int i=1, offset=0, rightOffset=right;  // offset from left inward

    // Bottom Lines
    i=1;
    //gc.setColor(highlightColor);
    //gc.drawLine(offset,size.height-2,size.width-rightOffset+1,size.height-2); 
    gc.setColor(highlightColor);
    gc.drawLine(offset,size.height-3,size.width-rightOffset+1,size.height-3); 
    gc.setColor(Color.lightGray);
    gc.drawLine(offset,size.height-4,size.width-rightOffset,size.height-4); 

    // Right side lines
    gc.setColor(highlightColor);
    gc.drawLine(size.width-rightOffset,3,size.width-rightOffset,size.height-3); 
    gc.drawLine(size.width-rightOffset+1,3,size.width-rightOffset+1,size.height-3); 

    // Left lines
    gc.setColor(shadowColor);
    gc.drawLine(offset,2,offset,size.height-2); 
    gc.setColor(shadowColor);
    gc.drawLine(offset+1,3,offset+1,size.height-3); 

    // Top lines
    gc.setColor(highlightColor);
    gc.drawLine(offset,0,size.width,0); 
    gc.setColor(backColor);
    gc.drawLine(offset+1,1,size.width-1,1); 
    gc.setColor(shadowColor2);
    gc.drawLine(offset+1,2,size.width-rightOffset,2); 
    gc.setColor(shadowColor);
    gc.drawLine(offset+1,3,size.width-rightOffset-1,3); 

    gc.setColor(backColor);
    gc.fillRect(size.width-rightOffset+1,2,size.width,size.height-1); //labelSize.height+8);
  }
  */
  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();
    if (source == textURL) {
      System.out.println("TextURL Action: " + textURL.getText());
      commBus.statusMsg1("Loading: " + textURL.getText());
      commBus.URL_Process(textURL.getText());
    }
  }

  public void setTextURL(String name) {
    textURL.setText(name);
    //update(getGraphics());
  }

  public void setModeLabel(String newmode) {
    //modeLabel.setText(newmode);
  }

  public void keyTyped(KeyEvent e) {}
  
  public void keyReleased(KeyEvent e) { }
  
  public void keyPressed(KeyEvent e) {
    /*
    int code=e.getKeyCode();
    
    if (code==KeyEvent.VK_CONTROL) {
      System.out.println("**Pressing Control in TextURL:"+e.getKeyModifiersText(code));
    }
    */
    //update(getGraphics());
  }
}	

