/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 
 *
 */
package ricoh.rh;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

class RH_ToolButton extends JButton {
  private int top=0, left=0, bottom=0, right=0;

  public RH_ToolButton(ImageIcon image) {
    super(image);
  }
  public Insets getInsets() {
    return new Insets(top,left,bottom,right);
  }
  public void setInsets (int newtop, int newleft, int newbottom, int newright) {
    top=newtop; bottom=newbottom;
    left=newleft; right=newright;
    getInsets();
  }

}
