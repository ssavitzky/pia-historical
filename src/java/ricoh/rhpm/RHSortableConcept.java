/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 *  Class: 
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 10.1.97  - revised 02-27-98
 *
 */
package ricoh.rhpm;


public class RHSortableConcept {
  private int idx, value;

  RHSortableConcept (int newidx, int newvalue) {
    idx=newidx;
    value=newvalue;
  }
  public int getIdx() {
    return idx;
  }
  public int getValue() {
    return value;
  }
}
