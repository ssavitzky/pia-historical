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

import java.util.Date;

import crc.interform.Parser;
import crc.interform.Input;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Tagset;
import crc.interform.Text;

import crc.ds.List;
import crc.ds.Table;

/** Run-time environment for the InterForm.  This class contains
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

  /************************************************************************
  ** Constructors:
  ************************************************************************/

  public Environment() {
  }

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

      ent("second",	pad(date.getSeconds(), 2));
      ent("minute",	min);
      ent("hour",	hh);
      ent("day",	dd);
      ent("month",	mm);
      ent("year",	yyyy);
      // === ent("weekday",	date.getWeekday());
      // === ent("dayName",	dayNames[$wday]);
      ent("monthName",	monthNames.at(date.getMonth()));
      //ent("yearday",	date.getYday);
      ent("date",	yyyy+mm+dd);
      ent("time",	hh+":"+min);

      ent("dateString",	date.toLocaleString());

      if (filename != null) {
	ent("filePath", filename);
	ent("fileName", filenamePart(filename));
      }

      ent("entityNames", "");
      ent("piaUSER",	System.getProperty("user.name"));
      ent("piaHOME",	System.getProperty("user.home"));

    }

    /*

	ent("piaHOST",	main::PIA_HOST);
	ent("piaPORT",	main::PIA_PORT);
	ent("piaDIR",	main::PIA_DIR);

	};

	$ents->{'entityNames'} = join(' ', sort keys %$ents);

    */

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

  public FileInputStream open(String infile) {
    try {
      if (infile != null) return new FileInputStream(infile);
      else return null;
    } catch (Exception e) {
      System.err.println("Cannot open input file " + infile);
      return null;
    }
  }

  public void runStream(InputStream in, OutputStream out, String tsname) {
    Parser p = new Parser(in, null);
    Interp ii = new Interp(Tagset.tagset(tsname), initEntities(), false);
    ii.from(p).toStream(out);
    use(ii);

    //ii.debug  = debug;
    //p.debug = debug;

    ii.run();
  }

  public void runFile(OutputStream out, String tsname) {
    runStream(open(filename), out, tsname);
  }

  public String evalStream(InputStream in, String tsname) {
    Parser p = new Parser(in, null);
    Interp ii = new Interp(Tagset.tagset(tsname), initEntities(), false);
    ii.from(p).toText();
    use(ii);

    //ii.debug  = debug;
    //p.debug = debug;

    return ii.run().toString();
  }

  public String evalFile(String tsname) {
    return evalStream(open(filename), tsname);
  }

  public void runCode(SGML code, String tsname) {
    Interp ii = new Interp(Tagset.tagset(tsname), initEntities(), false);
    ii.pushInto(code);
    ii.setSkipping();
    use(ii);

    ii.run();
  }

  public Tokens parseStream(InputStream in, String tsname) {
    Parser p = new Parser(in, null);
    Interp ii = new Interp(Tagset.tagset(tsname), initEntities(),true);
    ii.from(p).toTokens();
    use(ii);

    //ii.debug  = debug;
    //p.debug = debug;

    return ii.run();
  }

  public SGML parseFile(String tsname) {
    return parseStream(open(filename), tsname);
  }

  public SGML parseString(String input, String tsname) {
    return parseStream(new java.io.StringBufferInputStream(input), tsname);
  }

  /************************************************************************
  ** Used by Actors:
  ************************************************************************/

  /** Look up a file on behalf of the agent invoked on the given Token. */
  public String lookupFile(String fn, Token it, boolean write) {
    
    // === unimplemented()
    return fn;
  }

  /** Retrieve a URL. */
  public InputStream retrieveURL(String url, Token it) {
    // === unimplemented()
    return null;
  }



/*========================================================================

sub file_lookup {
    my ($self, $it, $ii, $write) = @_;

    ## Look up a file.

    my $file = $it->attr('file');
    my $base = $it->attr('base');

    if ($it->attr('interform')) {
	$file = IF::Run::agent()->find_interform($file);
	$base = '';
				# file should be properly quantified
	return $file;
    }
    if ($file =~ /^~/) {
	$file =~ s/^~//;
	$base = $ENV{'HOME'};
    } elsif ($file =~ /^\//) {
	$base = '';
    } elsif ($base eq '') {
	$base = IF::Run::agent()->agent_directory;
    }
    if ($base ne '' && $base !~ /\/$/) {
	$base .= '/';
    }
    my $fn = "$base$file";
    $fn =~ s://:/:g;

    return $fn;
}

### === These really ought to be in Agent

sub eval_perl {
    my ($ia, $it, $ii) = @_;

    ## This bit of legacy crud evaluates the contents of $it as PERL code.
    ##	  The local variables $agent and $request will already have been
    ##	  set up by run_interform.

    print "II Error: missing token\n" unless defined $it;
    print "II Error: $it not a token\n" unless ref($it);
    return unless ref($it);

    my $status = $agent->run_code($it->content_string, $request, $resolver);
    print "Interform error: $@\n" if $@ ne '' && ! $main::quiet;
    print "code result is $status\n" if  $main::debugging;

    $ii->token(IF::IT->new($it->tag, $status));    
    return;
}

1;


=================================================================== */
}
