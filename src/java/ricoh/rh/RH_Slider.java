/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: RH_Slider: my own slider based on KL Group's which updates only after mouse is released
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 03.28.98 
 *
 */
package ricoh.rh;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

class RH_Slider extends JSlider {
  RH_CommBus commBus;

  RH_Slider(RH_ConceptControl parent, RH_CommBus bus,int orient, int setting, int min, int max, int width, int height) {
    super();
    this.setOrientation(orient);
    this.setMinimum(min);
    this.setMaximum(max);
    this.setValue(setting);
    this.setMaximumSize(new Dimension(width,height));
    this.setPreferredSize(new Dimension(width,height));
    commBus=bus;
  }

}
