/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 *  Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 8.6.97 - revised 02-06-98
 *
 */
package ricoh.rh;


public class RH_MeterStyle {
  private int r=0, g=0, b=0;
  private String type;

  public RH_MeterStyle (String newtype, int r_color, int g_color, int b_color) {
    type=newtype;
    r=r_color;
    g=g_color;
    b=b_color;
  }
  public String getType() {
    return type;
  }
  public int getRed() {
    return r;
  }
  public int getGreen() {
    return g;
  }
  public int getBlue() {
    return b;
  }
}
