////// TagsetProcessor.java: Top Processor for Tagset loading
//	$Id$

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/


package crc.dps.tagset;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.DateFormat;

import java.io.PrintStream;

import java.net.URL;

import crc.dps.*;
import crc.dps.util.*;
import crc.dps.process.TopProcessor;

import crc.dom.NodeList;
import crc.ds.List;

/**
 * A TopProcessor for processing Tagset definition files.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.pia
 * @see crc.dps.process.TopProcessor
 * @see crc.dps.Processor
 * @see crc.dps.Context */

public class TagsetProcessor extends TopProcessor {

  /************************************************************************
  ** Variables and Access Functions:
  ************************************************************************/

  protected BasicTagset newTagset = null;

  public BasicTagset getNewTagset() { return newTagset; }
  public void setNewTagset(BasicTagset ts) {
    newTagset = ts;
    if (ts != null) {
      ts.setVerbosity(getVerbosity());
      ts.setLog(getLog());
      setTagset(ts);
    }
  }

  public void setVerbosity(int v) {
    verbosity = v;
    if (newTagset != null) newTagset.setVerbosity(getVerbosity());
  }  

  /************************************************************************
  ** Setup:
  ************************************************************************/

  public void initializeEntities() {
    // === probably need the character entities. ===
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public TagsetProcessor() {
  }

  public TagsetProcessor(Input in, Output out) {
    super(in, null, out, (Tagset)null);
  }


}
