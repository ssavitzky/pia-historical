////// Run.java: Run the InterForm Interpretor inside the PIA
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

import crc.pia.Agent;
import crc.pia.Transaction;
import crc.pia.Resolver;

import crc.ds.List;
import crc.ds.Table;

/** Run the InterForm Interpretor inside the PIA.  This class contains
 *	everything needed for associating an interpretor and a set of
 *	entities with a document, either as an action or a handler. <p?
 *
 *	The Run object itself is used to associate an Interp object with
 *	its corresponding PIA context of Agent, Transaction, and Resolver.
 *
 * @see crc.interform.Filter for standalone operation.  */
public class Run {

  /************************************************************************
  ** Variables:
  ************************************************************************/

  public Agent 		agent = null;
  public Transaction 	transaction = null;
  public Resolver 	resolver = null;
  public String 	filename = null;
  public Table		entities = null;

  /************************************************************************
  ** Constructors:
  ************************************************************************/

  protected Run() {
  }

  protected Run(Agent ia, Transaction tr, Resolver res, String fn) {
    agent = ia;
    transaction = tr;
    resolver = res;
    filename = fn;
  }

  /************************************************************************
  ** Association with Interpretor:
  ************************************************************************/

  public void use(Interp ii) {
    ii.environment = this;
  }

  public static Run environment(Interp ii) {
    try {
      return (Run) ii.environment;
    } catch (Exception e) {
      return null;
    }
  }

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
  public Table ifEntities() {
    // === should get entities from transaction if present 
    //     my $ents = $trans->get_feature('entities') if defined $trans;

    if (entities == null) {
      entities = new Table();

      Date date = new Date();

      ent("url", transaction.url());

    }

    /*
	my $date=sprintf("%d%02d%02d", $year, $mon+1, $mday);
	## === should be $year + 1900, of course.
	my $time=sprintf("%02d:%02d", $hour, $min);

	my ($request, $response);
	if (defined $trans && $trans->is_response) {
	    $response = $trans;
	    $request  = $response->response_to;
	} else {
	    $request = $trans;
	}
	my $url = $trans->url;
	my $path = $url->path if defined $url;
	my $query = $url->query if defined $url;
	$file =~ m:([^/]*)$:;
	my $fn = $1;

	## === request method, response type, etc. ===

	my $agentNames = join(' ', sort($resolver->agent_names));

	## === $trans->test('agent') returns 1; doesn't compute.
	my $transAgentName = $trans->get_feature('agent');
	my $transAgentType;
	if ($transAgentName) {
	    my $ta = $resolver->agent($transAgentName);
	    $transAgentType = $ta->type if ref $ta;
	}

	$ents = {
	    'transAgentName'	=> $transAgentName,
	    'transAgentType'	=> $transAgentType,

	    'agentName' 	=> $agent->name,
	    'agentType' 	=> $agent->type,
	    'fileName' 		=> $fn,
	    'filePath' 		=> $file,

	    'url'		=> ((ref $url)? $url->as_string : ''),
	    'urlQuery'		=> $query,
	    'urlPath'		=> $path,

	    'piaUSER'		=> $ENV{'USER'} || getlogin,
	    'piaHOME'		=> $ENV{'HOME'},
	    'piaHOST'		=> $main::PIA_HOST,
	    'piaPORT'		=> $main::PIA_PORT,
	    'piaDIR'		=> $main::PIA_DIR,

	    'agentNames'	=> $agentNames,
	    'entityNames'   	=> '',
	    'actorNames'	=> $tagset? $tagset->actor_names : '',

	    'second'		=> $sec,
	    'minute'		=> $min,
	    'hour'		=> $hour,
	    'day'		=> $mday,
	    'month'		=> $mon+1,
	    'year'		=> $year+1900,
	    'weekday'		=> $wday,
	    'dayName'		=> $dayNames[$wday],
	    'monthName'		=> $monthNames[$mon],
	    'yearday'		=> $yday,
	    'date'		=> $date,
	    'time'		=> $time,
	};

	$ents->{'entityNames'} = join(' ', sort keys %$ents);

	$trans -> set_feature('entities', $ents) if defined $trans;
	## === WARNING! Agents can pass info through entities now ===
    }

    $ents->{'agentName'} 	= $agent->name;
    $ents->{'agentType'} 	= $agent->type;
    $ents;

*/

    // ===
    return entities;
  }


  private void ent(String n, Object v) {
    entities.at(n, new Text(v));
  }

  private void ent(String n, SGML v) {
    entities.at(n, v);
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
    Interp ii = new Interp(Tagset.tagset(tsname), ifEntities(), false);
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
    Interp ii = new Interp(Tagset.tagset(tsname), ifEntities(), false);
    ii.from(p).toText();
    use(ii);

    //ii.debug  = debug;
    //p.debug = debug;

    return ii.run().toString();
  }

  public String evalFile(String tsname) {
    return evalStream(open(filename), tsname);
  }

  public Tokens parseStream(InputStream in, String tsname) {
    Parser p = new Parser(in, null);
    Interp ii = new Interp(Tagset.tagset(tsname), ifEntities(),true);
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

  /** Run a standard InterForm file on behalf of an Agent. Output is
   *  	sent directly to the transaction's receiver.  */
  public static void interform(Agent agent, String filepath, 
			       Transaction trans, Resolver res) {
    OutputStream out = null;	// === get from transaction.
    Run env = new Run(agent, trans, res, filepath);
    env.runStream(env.open(filepath), out, "Standard");
  }

  /** Run a standard InterForm file and return a String.  Use of this
   *	operation is deprecated, but it will be useful while we're
   *	figuring out how to properly interface using streams. */
  public static String interformFile(Agent agent, String filepath, 
				     Transaction trans, Resolver res) {
    return new Run(agent, trans, res, filepath).evalFile("Standard");
  }

  /** Run an already-parsed InterForm element as an Agent's actOn hook. */
  public static void interformHook(Agent agent, SGML code,
				   Transaction trans, Resolver res) {
    // ===
  }

/*========================================================================


### === These really ought to be in Agent

sub agent {
    return $agent;
}
sub request {
    return $request;
}
sub transaction {
    return $request;
}
sub resolver {
    return $resolver;
}

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
