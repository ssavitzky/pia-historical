////// TopProcessor.java: Top-level Document Processor class
//	$Id$
//	Copyright 1998, Ricoh Silicon Valley.

package crc.dps.process;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Enumeration;
import java.text.DateFormat;

import java.io.PrintStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

import crc.dps.*;
import crc.dps.active.*;
import crc.dps.util.*;
import crc.dom.NodeList;

import crc.ds.List;
import crc.ds.Tabular;

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
 *	stream, or to switch to a different tagset.
 *
 * @version $Id$
 * @author steve@rsv.ricoh.com
 *
 * @see crc.dps.Processor
 * @see crc.dps.Context */

public class TopProcessor extends BasicProcessor implements TopContext
{
  protected Tagset tagset;
  protected String documentBase;
  protected String documentName;
  protected URL    documentLocation = null;

  /************************************************************************
  ** State accessors:
  ***********************************************************************/

  /** Obtain the current Tagset. */
  public Tagset getTagset() 		 { return tagset; }

  /** Set the current Tagset. */
  public void setTagset(Tagset bindings) { tagset = bindings; }

  /** Return the current ProcessorInput, if there is one. */
  public ProcessorInput getProcessorInput() {
    return (getInput() instanceof ProcessorInput)
      ? (ProcessorInput)getInput()
      : null;
  }

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
  ** Namespaces:
  ************************************************************************/

  /** Get the binding (Entity node) of an entity, given its name. 
   *
   * <p> Search the tagset if there is no binding in the current EntityTable.
   *	 Bindings in the tagset are considered local.
   *
   * @return <code>null</code> if the entity is undefined.
   */
  public ActiveEntity getEntityBinding(String name, boolean local) {
    ActiveEntity ent = (entities == null)
      ? null : entities.getEntityBinding(name);
    if (ent == null && tagset != null) ent = tagset.getEntityBinding(name);
    return (local || ent != null || nameContext == null)
      ? ent : nameContext.getEntityBinding(name, local);
  }

  /** Return a namespace with a given name.  If the name is null, 
   *	returns the most-locally namespace.  The tagset is also considered 
   *	a namespace.
   */
  public Namespace getNamespace(String name) {
    if (entities != null) {
      if (name == null || name.equals(entities.getName())) {
	return entities;
      } else if (entities.containsNamespaces()) {
	ActiveNode ns = entities.getBinding(name);
	if (ns != null && ns.asNamespace() != null)
	  return ns.asNamespace();
      }
    }
    if (tagset != null && tagset.getNamespace(name) != null)
      return tagset.getNamespace(name);
    if (nameContext != null) {
      return nameContext.getNamespace(name);
    } else {
      return null;
    }
  }

  /************************************************************************
  ** External Entities:
  ************************************************************************/

  /** The prefix (<code>protocol://host:port/</code>) that makes the
   *	<code>documentBase</code> into a URL.
   *
   * <p> If documents are local, <code>documentLocation</code> will be null.
   *	 That does not necessarily mean that <code>documentBase</code> is
   *	 an absolute path, however.
   */
  public URL  getDocumentLocation() { return documentLocation; }
  public void setDocumentLocation(URL u) { documentLocation = u; }

  /** The base path of the current document.  It always ends with 
   *	``<code>/</code>'' if non-null.
   *
   * <p> If <code>documentBase</code> starts with ``<code>/</code>'', it
   *	 indicates an absolute path to the base directory; otherwise is
   *	 is relative to some other root, for example the URL started by
   *	 <code>documentLocation</code>.
   *
   * <p> Inside a PIA or other server the base path is <em>not</em> a path 
   *	 from the filesystem root, but rather is relative to the server's
   *	 document root.  Any special handling required for this occurs in 
   *	<code>locateSystemResource</code> .
   */
  public String getDocumentBase() { return documentBase; }
  public void   setDocumentBase(String s) { documentBase = s; }

  /** The file name of the current document. 
   *	May be null if the current document is a string. 
   */
  public String getDocumentName() { return documentName; }
  public void   setDocumentName(String s) { documentName = s; }

  /** Determine whether a resource name is local or remote. */
  protected boolean isRemotePath(String path) {
    if (path.startsWith("file:")) return false;
    return (path.indexOf(":") > 0 && path.indexOf("/") > 0 
	    && path.indexOf(":") < path.indexOf("/") );      
  }

  /** Determine whether a resource name is special. */
  protected boolean isSpecialPath(String path) {
    return false;    
  }

  /** Read from a resource. 
   *	The given path always uses ordinary (forward) slashes as file
   *	separators, because it is really a URI.  If a protocol and host
   *	are not specified, the path is relative to some implementation-
   *	dependent origin if it starts with '<code>/</code>', otherwise
   *	it is relative to the start of the document being processed.
   */
  public InputStream readExternalResource(String path)
    throws IOException {
    if (isSpecialPath(path)) return readSpecialResource(path);
    if (documentLocation == null && !isRemotePath(path)) {
      File f = locateSystemResource(path, false);
      if (f != null) {
	return new java.io.FileInputStream(f);
      }
    } else {
      URL u = locateRemoteResource(path, false);
      if (u != null) {
	return u.openStream();
      }
    }
    return null;
  }

  /** Write to a resource. 
   * @param path a path.
   * @param append append to an existing resource. 
   * @param createIfAbsent if no resource of the given name exists, create one
   * @param doNotOverwrite do not overwrite an existing resource
   */
  public OutputStream writeExternalResource(String path, boolean append,
					    boolean createIfAbsent,
					    boolean doNotOverwrite)
    throws IOException {
    if (isSpecialPath(path))
      return writeSpecialResource(path, append, createIfAbsent, doNotOverwrite);
    if (documentLocation == null && !isRemotePath(path)) {
      File f = locateSystemResource(path, true);
      if (f != null) {
	// === worry about the booleans here!
	return new java.io.FileOutputStream(f);
      }
    } else {
      URL u = locateRemoteResource(path, true);
      if (u != null) {
	return null; // === writeExternalResource
      }
    }
    return null;
  }

  /** Locate a resource accessible as a file. */
  public File locateSystemResource(String path, boolean forWriting) {
    if (path.startsWith("file:")) {
      // Just remove the "file:" prefix.
      path = path.substring(5);
    }
    if (path.startsWith("/")) {
      // Path starting with "/" is relative to document root
      return new File(path);
    } else if (path.indexOf(":") >= 0) {
      // URL: fail.
      return null;
    } else {
      // Path not starting with "/" is relative to documentBase.
      if (path.startsWith("./")) path = path.substring(2);
      if (documentBase != null) path = documentBase + path;
      return new File(path);
    }
  }

  /** Locate a resource accessible as a URL. */
  public URL locateRemoteResource(String path, boolean forWriting) {
    if (path.startsWith("file:")) {
      return null; // === file: in locateRemoteResource -- not clear what to do
    }
    if (path.indexOf(":") > 0 && path.indexOf("/") > 0 
	&& path.indexOf(":") < path.indexOf("/") ) {
      try {
	return new URL(path);
      } catch (MalformedURLException e) {
	return null;
      }
    } else if (documentLocation != null) {
      try {
	return new URL(documentLocation, path);
      } catch (MalformedURLException e) {
	return null;
      }
    }
    return null;
  }

  /** Hook on which to hang any specialized paths supported by a subclass. */
  protected InputStream readSpecialResource(String path) {
    return null;
  }
  /** Hook on which to hang any specialized paths supported by a subclass. */
  public OutputStream writeSpecialResource(String path, boolean append,
					    boolean createIfAbsent,
					   boolean doNotOverwrite) {
    return null;
  }


  /************************************************************************
  ** Sub-processing:
  ************************************************************************/

  /** Load a Tagset by name. 
   * @param tsname the tagset name.  If null, returns the current tagset. 
   */
  public Tagset loadTagset(String tsname) {
    if (tsname == null) return tagset;
    else return crc.dps.tagset.Loader.loadTagset(tsname, this);
  }

  /** Process a new subdocument. 
   * 
   * @param in the input.
   * @param ts the tagset.  If null, the current tagset is used.
   * @param cxt the parent context. 
   * @param out the output.  If null, the parent context's output is used.
   */
  public TopContext subDocument(Input in, Context cxt, Output out, Tagset ts) {
    if (ts == null) ts = tagset;
    return new TopProcessor(in, cxt, out, ts);
  }

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

  /** Make an entity-table entry for a lookup table. */
  public void define(String n, Tabular v) {
    getLocalNamespace().setBinding(n, new NamespaceWrap(n, v));
  }

  /** Make an entity-table entry for a String. */
  public void define(String n, Object v) {
    if (v == null)
      setEntityValue(n, new ParseNodeList(), false);
    else
      setEntityValue(n, Create.createNodeList(v.toString()), false);
  }

  /** Make an entity-table entry for a List. */
  public void define(String n, Enumeration v) {
    setEntityValue(n, new ParseNodeList(v), false);
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

    /* The following must be defined by the caller:
     *	fileName -- the name of the file.
     *  filePath -- the full (from root) path to the file
     *  pathExt  -- the path extension (i.e. path after the filename)
     */

    define("piaUSER",		System.getProperty("user.name"));
    define("piaHOME",		System.getProperty("user.home"));

    define("entityNames", 	"");
    //=== define("entityNames", new Tokens(entities.keys(), " "));
  }

  /************************************************************************
  ** Construction:
  ************************************************************************/

  public TopProcessor() {
    initializeEntities();
    top = this;
  }

  public TopProcessor(boolean defaultEntities) {
    if (defaultEntities) initializeEntities();
    top = this;
  }

  public TopProcessor(Input in, Output out) {
    super(in, null, out, null);
    initializeEntities();
    top = this;
  }

  public TopProcessor(Input in, Output out, EntityTable ents) {
    super(in, null, out, ents);
    top = this;
  }

  public TopProcessor(Input in, Context prev, Output out, EntityTable ents) {
    super(in, prev, out, ents);
    top = this;
  }

  public TopProcessor(Input in, Context prev, Output out, Tagset ts) {
    super(in, prev, out, null);
    tagset = ts;
    top = this;
  }

}
