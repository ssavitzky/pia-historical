/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 *  Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 4.24.97
 *
 */
package ricoh.rh;

import java.util.Date;
import java.awt.MenuItem;
//import javax.swing.*;
import java.awt.Font;


public class RH_LocationItem extends MenuItem {
  private String url, title, comment;
  private long lastVisited, lastModified;
  private int number=-1, position=0;
  private String fontname="MS Sans Serif";
  private int fontsize=11;

  public RH_LocationItem (String urlstr, String titlestr) {
    url=urlstr;
    if (titlestr==null) title="???";
    else title=titlestr;
    Date date=new Date();
    lastVisited=lastModified=date.getTime();
    position=0;
    setFont(new Font(fontname,Font.PLAIN,fontsize));
    setLabel(getNumberTitle());
  }
  public RH_LocationItem (String urlstr, String titlestr, long visited) {
    url=urlstr;
    if (titlestr==null) title="???";
    else title=titlestr;
    setLabel(title);
    lastVisited=visited;
    lastModified=0;
    setFont(new Font(fontname,Font.PLAIN,fontsize));
    position=0;
  }
  public String getURL() {
    return url;
  }
  public String getTitle() {
    if (number>=0) return new String (number + " - " +title);
    else return title;
  }
  public String getNumberTitle() {
    return new String (number + " - " +title);
  }
  public long getLastModified() {
    return lastModified;
  }
  public long getLastVisited() {
    return lastVisited;
  }
  public void setNumber (int num) {  
    number=num;
    setLabel(getNumberTitle());
  }
  public int getPosition() {
    return position;
  }
  public void setLastPosition(int pos) {
    position=pos;
  }
  public void setBold(boolean bold) {
    if (bold) setFont(new Font(fontname,Font.BOLD,fontsize));
    else setFont(new Font(fontname,Font.PLAIN,fontsize));
  }
}
