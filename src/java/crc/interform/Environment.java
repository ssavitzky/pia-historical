////// Environment.java: Run-time environment for the InterForm Interpretor
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.StringBufferInputStream;

import java.util.Date;

import crc.interform.Parser;
import crc.interform.Input;
import crc.interform.Interp;
import crc.interform.Tagset;

import crc.sgml.SGML;
import crc.sgml.Text;
import crc.sgml.Tokens;

import crc.ds.List;
import crc.ds.Table;

/** Run-time environment for the InterForm Interpretor  This class contains
 *	everything needed for associating an interpretor and a set of
 *	entities with a document, either as an action or a handler. <p>
 *
 *	The Environment object itself is used by actors for operations,
 *	including filename lookup, that differ depending on whether the
 *	interpretor is operating stand-alone or inside a PIA.<p>
 *
 * @see crc.interform.Filter for standalone operation.  */
public class Environment {

  /************************************************************************
  ** Variables:
  ************************************************************************/

  public String 	filename = null;
  public Table		entities = null;
  public Interp		interpretor = null;

  public boolean	debug = false;

  /************************************************************************
  ** Constructors:
  ************************************************************************/

  public Environment() {
  }

  /** Construct an environment with a filename. */
  public Environment(String fn) {
    filename = fn;
  }

  /************************************************************************
  ** Association with Interpretor:
  ************************************************************************/

  public void use(Interp ii) {
    ii.environment = this;
    if (entities == null) initEntities();
    if (entities != null) ii.entities = entities;
    interpretor = ii;
    if (debug) ii.debug = debug;
  }

  /************************************************************************
  ** Extract information:
  ************************************************************************/


  /************************************************************************
  ** Entity table:
  ************************************************************************/

  static Tokens dayNames = Util.splitTokens("Sunday Monday Tuesday Wednesday"
					    + " Thursday Friday Saturday");

  static Tokens monthNames= Util.splitTokens("January February March April"
					     + " May June July August"
					     + " September October November"
					     + " December");

  /** Initialize and return the entity table */
  public Table initEntities() {
    // === should get entities from transaction if present 
    //     my $ents = $trans->get_feature('entities') if defined $trans;

    if (entities == null) {
      entities = new Table();

      Date date = new Date();
      String yyyy = pad(date.getYear()+1900, 4);
      String mm	  = pad(date.getMonth()+1, 2);
      String dd   = pad(date.getDate(), 2);
      String hh	  = pad(date.getHours(), 2);
      String min  = pad(date.getMinutes(), 2);
      int wday	  = /* date.getWeekday()) */ Util.getWeekday(date);

      ent("second",	pad(date.getSeconds(), 2));
      ent("minute",	min);
      ent("hour",	hh);
      ent("day",	dd);
      ent("month",	mm);
      ent("year",	yyyy);
      ent("weekday",	pad(wday, 1));
      ent("dayName",	dayNames.at(wday));
      ent("monthName",	monthNames.at(date.getMonth()));
      //ent("yearday",	date.getYday);
      ent("date",	yyyy+mm+dd);
      ent("time",	hh+":"+min);

      ent("dateString",	date.toLocaleString());

      if (filename != null) {
	ent("filePath", filename);
	ent("fileName", filenamePart(filename));
      }

      ent("piaUSER",	System.getProperty("user.name"));
      ent("piaHOME",	System.getProperty("user.home"));

      ent("entityNames", "");
      ent("entityNames", new Tokens(entities.keys(), " "));
    }

    return entities;
  }


  /** Convert an int to a string padded on the left with zeros */
  protected String pad(int i, int fieldLength) {
    String s = Integer.toString(i);
    while (s.length() < fieldLength) s = '0' + s;
    return s;
  }

  /** Make an entity-table entry for a String. */
  protected void ent(String n, Object v) {
    entities.at(n, new Text(v));
  }

  /** Make an entity-table entry for an SGML object. */
  protected void ent(String n, SGML v) {
    entities.at(n, v);
  }

  /** Extract the filename from a path. */
  public String filenamePart(String path) {
    char sep = System.getProperty("file.separator", "/").charAt(0);
    int i = path.lastIndexOf(sep);
    return (i < 0)? path : path.substring(i+1);
  }

  /************************************************************************
  ** Run the Interpretor:
  ************************************************************************/

  /** Open an input file (usually <code>filename</code>). */
  public FileInputStream open(String infile) {
    try {
      if (infile != null) return new FileInputStream(infile);
      else return null;
    } catch (Exception e) {
      System.err.println("Cannot open input file " + infile);
      return null;
    }
  }

  /** Run the interpretor on an InputStream, with output to a stream. */
  public void runStream(InputStream in, OutputStream out, String tsname) {
    Parser p = new Parser(in, null);
    Interp ii = new Interp(Tagset.tagset(tsname), initEntities(), false);
    ii.from(p).toStream(out);
    use(ii);
    ii.run();
  }

  /** Run the interpretor on the input file, with output to a stream. */
  public void runFile(OutputStream out, String tsname) {
    runStream(open(filename), out, tsname);
  }

  /** Run the interpretor on an InputStream to produce a String value. */
  public String evalStream(InputStream in, String tsname) {
    Parser p = new Parser(in, null);
    Interp ii = new Interp(Tagset.tagset(tsname), initEntities(), false);
    ii.from(p).toText();
    use(ii);
    return ii.run().toString();
  }

  /** Run the interpretor on a file to produce a String value. */
  public String evalFile(String tsname) {
    return evalStream(open(filename), tsname);
  }

  /** Filter an InputStream on an interform, i.e. produce an InputStream 
   *	that contains the results of evaluating the interform.
   */
  public InputStream filterStream(InputStream in, String tsname) {
    Parser p = new Parser(in, null);
    Interp ii = new Interp(Tagset.tagset(tsname), initEntities(), false);
    ii.from(p).toText();
    use(ii);

    // === this is a kludge, to be fixed later. 

    String interformOutput = ii.run().toString();
    return new StringBufferInputStream( interformOutput );
  }

  /** Filter a file, producing an InputStream. */
  public InputStream filterFile(String tsname) {
    return filterStream(open(filename), tsname);
  }

  /** Run some already-parsed InterForm code and discard the output. */
  public void runCode(SGML code, String tsname) {
    Interp ii = new Interp(Tagset.tagset(tsname), initEntities(), false);
    ii.pushInto(code);
    ii.setSkipping();
    use(ii);
    ii.run();
  }

  /** Parse an InputStream, producing a parse tree. */
  public Tokens parseStream(InputStream in, String tsname) {
    Parser p = new Parser(in, null);
    Interp ii = new Interp(Tagset.tagset(tsname), initEntities(),true);
    ii.from(p).toTokens();
    use(ii);
    return ii.run();
  }

  /** Parse a file, producing a parse tree. */
  public SGML parseFile(String tsname) {
    return parseStream(open(filename), tsname);
  }

  /** Parse a String, producing a parse tree. */
  public SGML parseString(String input, String tsname) {
    return parseStream(new java.io.StringBufferInputStream(input), tsname);
  }

  /** Run the interpretor on an InputStream, discarding the output. */
  public void skipStream(InputStream in, String tsname) {
    Parser p = new Parser(in, null);
    Interp ii = new Interp(Tagset.tagset(tsname), initEntities(), false);
    ii.from(p).toNull();
    ii.setSkipping();
    use(ii);
    ii.run();
  }

  /** Run the interpretor on a file, discarding the output. */
  public void skipFile(String tsname) {
    skipStream(open(filename), tsname);
  }

  /************************************************************************
  ** Used by Actors:
  ************************************************************************/

  /** Look up a file on behalf of the agent invoked on the given SGML. */
  public String lookupFile(String fn, SGML it, boolean write) {
    
    // === unimplemented()
    return fn;
  }

  /** Retrieve a URL. */
  public InputStream retrieveURL(String url, SGML it) {
    // === unimplemented()
    return null;
  }

  /** Return a suitable base directory for read or write operations. */
  public String baseDir(SGML it) {
    return "";
  }

  /** Return a string suitable for setting the proxy environment variables */
  public String proxies() {
    return "";
  }

}
