/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 *  Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 11.14.97
 *
 */
package ricoh.rh;

import java.awt.*;
import javax.swing.*;

class RH_BorderLabel extends JLabel {
  private String text;
  private Color textColor=Color.black, backColor=Color.white, highlight=Color.white, shadow=Color.black;
  private int top=1,bottom=1,left=1,right=1, iconx=2, icony=4;
  private Image icon;

  RH_BorderLabel(String str) {
    super(str);
    setDoubleBuffered(true);
    text=str;
    setInsets(top,left,bottom,right);    
    icon=null;
  }
  RH_BorderLabel(String str,int align) {
    super(str,align);
    setDoubleBuffered(true);
    text=str;
    icon=null;
    setInsets(top,left,bottom,right);    
  }
  RH_BorderLabel(String str,String iconfile,int align) {
    super(str,align);
    setDoubleBuffered(true);
    text=str;
    icon=Toolkit.getDefaultToolkit().getImage(iconfile);
    setInsets(top,left,bottom,right);      
  }
  public void setImage(String iconfile) {
    icon=Toolkit.getDefaultToolkit().getImage(iconfile);
    repaint();
  }
  public void removeImage() {
    icon=null;
    repaint();
  }
  public void update(Graphics gc) {
      paint(gc);
  }
  public void paint(Graphics gc) {
    Dimension size = getSize();
    if (icon!=null) {
      gc.drawImage(icon,iconx,icony,this);
    }
    else {
    }
    gc.setColor(shadow);
    gc.drawLine(0,0,size.width,0);  // top
    gc.drawLine(0,0,0,size.height); // left
    gc.setColor(highlight);
    gc.drawLine(0,size.height-1,size.width,size.height-1);  // bottom
    gc.drawLine(size.width-1,0,size.width-1,size.height-1);  //right
  }
  public Insets getInsets() {
    return new Insets(top,left,bottom,right);
  }
  public void setInsets (int newtop, int newleft, int newbottom, int newright) {
    top=newtop; bottom=newbottom;
    left=newleft; right=newright;
    getInsets();
  }
  public void setHighlightColors(Color high, Color low) {
    highlight=high;
    shadow=low;
    repaint();
  }
  public void setIconXY(int x, int y) {
    iconx=x;
    icony=y;
  }
}
