////// Filter.java: the Interform Interpretor used as a filter
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.dps;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.OutputStreamWriter;

import crc.dps.Parser;
import crc.dps.Input;
import crc.dps.Processor;
import crc.dps.BasicProcessor;


/**
 * Interpret an input stream or file as an InterForm.  
 *	=== Should really descend from Environment.
 */
public class Filter {
  static String infile = null;
  static String outfile = null;
  static String propfile = null;
  static String tsname = "Basic";
  static boolean tree = false;
  static boolean debug = false;
  static boolean entities = true;
  static boolean verbose = false;
  static boolean parsing = false;

  /** Main program.
   * 	Interpret the given arguments, then run the interpretor over
   *	standard input or the specified input file.
   */
  public static void main(String[] args) {
    if (!options(args)) {
      usage();
      System.exit(-1);		// return an error
    }

    /* Open the input and output files. */

    InputStream in = System.in;
    try {
      if (infile != null) in = new FileInputStream(infile);
    } catch (Exception e) {
      System.err.println("Cannot open input file " + infile);
    }
      
    OutputStream out = System.out;
    try {
      if (outfile != null) out = new FileOutputStream(outfile);
    } catch (Exception e) {
      System.err.println("Cannot open output file " + outfile);
    }

    if (verbose) {
      java.util.Properties env = System.getProperties();
      java.util.Enumeration names = env.propertyNames();
      while (names.hasMoreElements()) {
	String name = names.nextElement().toString();
	System.err.println(name+" = " + env.getProperty(name));
      }
    }
    if (verbose) {
      System.err.println("infile = " + infile );
      System.err.println("outfile= " + outfile);
      System.err.println("propfile= " + propfile);
    }

    /* Initialize and run the interpretor */

    // === Must get the tagset at this point and ask _it_ for the Parser.
    // === Eventually tagsets need to be in a NamedNodeList; for now
    // === just load the class.

    Parser p = new crc.dps.parse.BasicParser(in, null);
    // p.debug = debug;

    OutputStreamWriter outw = new OutputStreamWriter(out);
    BasicProcessor ii = new BasicProcessor();
    ii.pushProcessorInput(p);
    ii.setOutput(new crc.dps.output.ToWriter(outw));
    ii.setExpanding(true);

    if (parsing) {
      ii.setParsing(true);
      ii.setNode(new BasicToken("ParseTree", 0));
    } else {
      ii.setPassing(true);
    }
    //if (entities) new Environment(infile).use(ii);
    if (debug) ii.setDebug();

    ii.run();

    if (parsing) { 
      System.err.println("=====================\n");
      System.err.println(ii.getNode());
    }
  }

  /** Print a usage string.
   */
  public static void usage() {
    PrintStream o = System.out ;

    o.println("Usage: java crc.interform.Filter [option]... [infile]");
    o.println("    options:");
    o.println("        -e	no entities");
    o.println("        -h	print help string");
    o.println("        -o file	specify output file");
    o.println("	       -p	build parse tree");
    o.println("        -t ts	specify tagset name");
    o.println("	       -v	verbose");
    o.println("        -d	debug");
  }

  /** Decode the arguments.
   *	Returns false if the -h option or an invalid option is present.
   */
  public static boolean options(String[] args) {
    suckEnvironment(args);
    for (int i = 0 ; i < args.length ; i++) {
      if (args[i].indexOf("=") >= 0) {
	continue;
      } else if (args[i].equals("-h")) {
	return false;
      } else if (args[i].equals("-tree")) {
	tree = true;
      } else if (args[i].equals("-d")) {
	debug = true;
      } else if (args[i].equals("-e")) {
	entities = false;
      } else if (args[i].equals("-o")) {
	if (i == args.length - 1) return false;
	outfile = args[++i];
      } else if (args[i].equals("-p")) {
	parsing = true;
      } else if (args[i].equals("-t")) {
	if (i == args.length - 1) return false;
	tsname = args[++i];
      } else if (args[i].equals("-v")) {
	verbose = true;
      } else if (args[i].charAt(0) != '-') {
	if (infile != null) return false;
	infile = args[i];
      } else {
	System.err.println("bad arg: "+args[i]);
	return false;
      }
    }
    return true;
  }

  /** Stolen from Configuration.  Set system props from name=value pairs. */
  protected static void suckEnvironment(String[] args) {
    for (int i = 0; i < args.length; ++i) {
      int pos = args[i].indexOf("=");
      if (pos >= 0) {
	String key = args[i].substring(0, pos);
	String value = "";
	if (pos < (args[i].length() - 1)) {
	  value = args[i].substring( pos+1 );
	}
	System.getProperties().put(key.trim(), value.trim());
      }
    }
  }
}
