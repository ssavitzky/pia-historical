/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 *  Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 9.10.97 - revised 02-11-98
 *
 */
package ricoh.rh;

import java.awt.*;
import java.awt.event.*;

public class RH_ThumbarNavButton extends Canvas 
implements MouseListener {

  private int x=0,y=0,width=9,normalHeight=12, height=normalHeight,mode;
  private String imageName, imageNameSet;
  private Image image;
  private Color highlightColor=Color.white, shadeColor=Color.black, backColor=Color.gray,
    labelColor=Color.lightGray, labelSetColor=Color.yellow;
  private boolean buttonOn=false, drawOutline=false;
  private RH_CommBus commBus;
  private RH_ThumbarMode parent;
  private Color fillColor;
  private String modeFont="Arial", imagePath="";
  private Font font;

  RH_ThumbarNavButton (int modeNum, String imageStr, Color flColor, Color bkColor, RH_ThumbarMode modeParent, boolean ON) {
      setup(modeNum, imageStr, flColor, bkColor, modeParent, ON, width,height);
  }
  RH_ThumbarNavButton (int modeNum, String imageStr, Color flColor, Color bkColor, RH_ThumbarMode modeParent, boolean ON, int w, int h) {
      setup(modeNum, imageStr, flColor, bkColor, modeParent, ON, w, h);
  }
    private void setup(int modeNum, String imageStr, Color flColor, Color bkColor, RH_ThumbarMode modeParent, boolean ON, int w, int h) {
	parent=modeParent;
	imagePath=modeParent.commBus.getGifsPath()+"/";
	imageName=imagePath+imageStr+".gif";
	imageNameSet=imagePath+imageStr+"-d"+".gif";
	image=Toolkit.getDefaultToolkit().getImage(imageName);
	buttonOn=true;
	fillColor=flColor;
	backColor=bkColor;
	width=w; 
	height=h;
	setSize(width+1,height+1);    
	setBounds(0,0,width+1,height+1);
	mode=modeNum;
	setBackground(backColor);
	addMouseListener(this);
	setBackground(backColor);
	setVisible(true);
    }

  public void paint(Graphics gc) {
    Dimension size = getSize();
    int newHeight=0, remainder_h=0, number_y=0;
    Color numberColor;

    newHeight=size.height;
    highlightColor=Color.white;
    shadeColor=Color.black;
    number_y=10;
    numberColor=labelColor;
    if (drawOutline) {
      gc.setColor(highlightColor);
      gc.drawLine(x,y,x,y+newHeight);
      gc.drawLine(x,y,x+width,y);
      gc.setColor(shadeColor);
      gc.drawLine(x+width,y,x+width,y+newHeight);
      gc.drawLine(x,y+newHeight-1,x+width,y+newHeight-1);
      gc.setColor(Color.gray);
      gc.drawLine(x,y+newHeight,x+width,y+newHeight);
      gc.drawImage(image,x+3,y+3,this);
    }
    else gc.drawImage(image,x,y,this);
  }
  public boolean isOn() {
    return buttonOn;
  }
    public void setCanvasSize(int w, int h) {
	width=w;
	height=h;
	setSize(width+1,height+1);
    }
    public void setImageFilename(String filename) {
	imageName=imagePath+filename+".gif";
	imageNameSet=imagePath+filename+"-d"+".gif";
	image=Toolkit.getDefaultToolkit().getImage(imageName);
	repaint();
    }
  public void reset() {
    buttonOn=false;
    image=Toolkit.getDefaultToolkit().getImage(imageName);
    update(getGraphics());
    height=normalHeight;
  }
  public void mouseClicked(MouseEvent ev) {
  }
  public void mousePressed(MouseEvent ev) {
    buttonOn=true;
    parent.modeSelected(mode,this);
    update(getGraphics());
  }
  public void mouseReleased(MouseEvent ev) {
    update(getGraphics());
  }
  public void mouseEntered(MouseEvent ev) {
    //setCursor(Frame.HAND_CURSOR);
  }
  public void mouseExited(MouseEvent ev) {
    // setCursor(Frame.DEFAULT_CURSOR);
  }
}










