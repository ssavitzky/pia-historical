////// DocumentProcessor.java: Top-level Document Processor class
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.DateFormat;

import java.io.PrintStream;

import crc.dps.aux.*;
import crc.dom.NodeList;
import crc.ds.List;

/**
 * A top-level Processor, implementing the TopContext and Processor
 *	interfaces.
 *
 *	A TopContext is the root of a document-processing Context stack. 
 *	As such, it contains the tagset, global entity table, and other
 *	global information. <p>
 *
 *	Note that there may be more than one top context in a stack; this
 *	may be done in order to insert a sub-document into the processing
 *	stream.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Processor
 * @see crc.dps.Context */

public class DocumentProcessor extends BasicProcessor implements TopContext
{
  protected Tagset tagset;

  /************************************************************************
  ** State accessors:
  ***********************************************************************/

  /** Obtain the current Tagset. */
  public Tagset getTagset() 		 { return tagset; }

  /** Set the current Tagset. */
  public void setTagset(Tagset bindings) { tagset = bindings; }

  /************************************************************************
  ** Input and Output
  ************************************************************************/

  /** Registers an Input object for the Processor.  
   */
  public void setInput(Input anInput)    { input = anInput; }

  /** Registers an Output object for the Processor.  
   */
  public void setOutput(Output anOutput) { output = anOutput; }

  /************************************************************************
  ** Message Reporting:
  ************************************************************************/

  public void setLog(PrintStream log) 	 { this.log = log; }

  /************************************************************************
  ** Setup:
  ************************************************************************/

  /** Convert an int to a string padded on the left with zeros */
  protected String pad(int i, int fieldLength) {
    String s = Integer.toString(i);
    while (s.length() < fieldLength) s = '0' + s;
    return s;
  }

  /** Make an entity-table entry for a String. */
  protected void define(String n, Object v) {
    setEntityValue(n, Create.createNodeList(v.toString()), false);
  }

  static List dayNames = List.split("Sunday Monday Tuesday Wednesday"
				    + " Thursday Friday Saturday");

  static List monthNames= List.split("January February March April"
				     + " May June July August"
				     + " September October November December");

  /** Initialize date-dependentent entities. */
  public void initializeDateEntities(Date date) {
    // The Calendar instance performs all the necessary extraction.
    Calendar today = new GregorianCalendar();
    String yyyy	   = pad(today.get(Calendar.YEAR), 4);
    int    m	   = today.get(Calendar.MONTH);
    String mm	   = pad(m+1, 2);
    String dd      = pad(today.get(Calendar.DAY_OF_MONTH), 2);
    String hh	   = pad(today.get(Calendar.HOUR_OF_DAY), 2);
    String min     = pad(today.get(Calendar.MINUTE), 2);
    String sec     = pad(today.get(Calendar.SECOND), 2);

    // Handle any reasonable value of Sunday.  We need Sunday = 0.
    int wday	   = (today.get(Calendar.DAY_OF_WEEK)- Calendar.SUNDAY + 7) % 7;

    // Define the entities:
    define("second",		sec);
    define("minute",		min);
    define("hour",		hh);
    define("day",		dd);
    define("month",		mm);
    define("year",		yyyy);
    define("weekday",		pad(wday, 1));
    define("dayName",		dayNames.at(wday));
    define("monthName",		monthNames.at(m));
    define("yearday",		pad(today.get(Calendar.DAY_OF_YEAR), 3));
    define("date",		yyyy+mm+dd);
    define("time",		hh+":"+min);

    // Get a formatter to create a properly-formatted date.
    DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.FULL,
							  DateFormat.LONG);
    define("dateString",	formatter.format(date));
  }

  public void initializeEntities() {
    // Extract formatted information from today's Date.
    initializeDateEntities(new Date());

    // Form counter.  Increment as each <form> is passed to the output.
    define("forms", 		"0");

    /*
      if (filename != null) {
	define("filePath", 	filename);
	define("fileName", 	filenamePart(filename));
      }

      if (putfn != null) {
	define("pathTran", 	putfn);
      }
      */

    define("piaUSER",		System.getProperty("user.name"));
    define("piaHOME",		System.getProperty("user.home"));

    define("entityNames", 	"");
    //=== define("entityNames", new Tokens(entities.keys(), " "));
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  DocumentProcessor() {
    initializeEntities();
    top = this;
  }

  DocumentProcessor(boolean defaultEntities) {
    if (defaultEntities) initializeEntities();
    top = this;
  }

  public DocumentProcessor(Input in, Output out) {
    super(in, null, out, null);
    initializeEntities();
    top = this;
  }

  public DocumentProcessor(Input in, Output out, EntityTable ents) {
    super(in, null, out, ents);
    top = this;
  }

  public DocumentProcessor(Input in, Context prev, Output out,
			   EntityTable ents) {
    super(in, prev, out, ents);
    top = this;
  }

}
