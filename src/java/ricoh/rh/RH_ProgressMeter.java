/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 *  Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 11.4.97
 *
 */
package ricoh.rh;

import java.awt.*;
import jclass.bwt.*;
//import javax.swing.*;

class RH_ProgressMeter extends JCProgressMeter {
  private int top=0,bottom=0,left=0,right=0;

  RH_ProgressMeter(int st, int x, int max) {
    super(st,x,max);
    setInsets(top,left,bottom,right);    
  }
  public void update(Graphics gc) {
    paint(gc);
  }
  public void paint(Graphics gc) {
    super.paint(gc);
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
