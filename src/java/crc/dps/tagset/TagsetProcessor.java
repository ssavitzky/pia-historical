////// TagsetProcessor.java: Top Processor for Tagset loading
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.tagset;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.DateFormat;

import java.io.PrintStream;

import java.net.URL;

import crc.dps.*;
import crc.dps.aux.*;
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
    super(false);
    initializeEntities();
  }

  public TagsetProcessor(Input in, Output out) {
    super(in, null, out, (Tagset)null);
    initializeEntities();
  }


}
