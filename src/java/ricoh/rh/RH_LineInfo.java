/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 *  Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 7.22.97 - revised 02-11-98
 *
 */
package ricoh.rh;

import java.awt.*;

/**
 * Data structure for holding text line information for Docview
 */
class RH_LineInfo extends Component {
  private int x, y, h, w, imageWidth, imageHeight, boxNumber, fontType;
  private Color color;
  private String text=null;
    private Image image, tableImage;
  private RH_LineInfo next=null;

  public RH_LineInfo (String line, int xloc, int yloc, int width, int height, Color cl, int boxnum, int fonttype) {
    text=line;
    x=xloc;
    y=yloc;
    w=width;
    h=height;
    color=cl;
    fontType=fonttype;
    image=null;
    boxNumber=boxnum;
  }
  public RH_LineInfo (Image img, int xloc, int yloc, int width, int height, Color cl, int boxnum, int fonttype) {
    image=img;
    text=null;
    x=xloc;
    y=yloc;
    imageWidth=width;
    imageHeight=height;
    fontType=fonttype;
    color=cl;
    boxNumber=boxnum;
  }
  public RH_LineInfo (Image img, int xloc, int yloc, int width, int height, Color cl, int boxnum) {
    tableImage=img;
    text=null;
    x=xloc;
    y=yloc;
    imageWidth=width;
    imageHeight=height;
    fontType=-1;
    color=cl;
    boxNumber=boxnum;
  }
  public Color getColor() {
    return color;
  }
  public int getX() {
    return x;
  }
  public int getY() {
    return y;
  }
  public int getWidth() {
    return w;
  }
  public int getHeight() {
    return h;
  }
  public String getText() {
    return text;
  }
  public Image getImage() {
    return image;
  }
  public int getImageWidth() {
    return imageWidth;
  }
  public int getImageHeight() {
    return imageHeight;
  }
  public int getBoxNumber() {
    return boxNumber;
  }
  public int getFontType() {
    return fontType;
  }
  public Image getTableImage() {
    return tableImage;
  }
  public void setNext(RH_LineInfo item) {
    next=item;
  }
  public RH_LineInfo getNext() {
    return next;
  }
}
