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
import crc.interform.Environment;

import crc.pia.Agent;
import crc.pia.Transaction;
import crc.pia.Resolver;

import crc.ds.List;
import crc.ds.Table;

/** Run the InterForm Interpretor inside the PIA.  This class contains
 *	everything needed for associating an interpretor and a set of
 *	entities with a document, either as an action or a handler. <p>
 *
 *	The Run object itself is used to associate an Interp object with
 *	its corresponding PIA context of Agent, Transaction, and Resolver.
 *
 * @see crc.interform.Filter for standalone operation.  */
public class Run  extends Environment {

  /************************************************************************
  ** Variables:
  ************************************************************************/

  public Agent 		agent = null;
  public Transaction 	transaction = null;
  public Resolver 	resolver = null;

  /************************************************************************
  ** Constructors:
  ************************************************************************/

  protected Run() {
    super();
  }

  protected Run(Agent ia, Transaction tr, Resolver res, String fn) {
    super(fn);
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
  ** Extract information:
  ************************************************************************/

  public static String getAgentName(Interp ii) {
    Run env = environment(ii);
    return (env == null)? null : env.agent.name();
  }

  public static String getAgentType(Interp ii, String name) {
    Run env = environment(ii);
    if (env == null) return null;
    if (name == null) return env.agent.type();
    Agent ia = env.resolver.agent(name);
    return (ia == null)? null : ia.type();
  }

  public static Agent getAgent(Interp ii, String name) {
    Run env = environment(ii);
    return (env == null)? null : env.resolver.agent(name);
  }    

  public Agent getAgent(String name) {
    if (name == null) return null;
    return resolver.agent(name);
  }    


  /************************************************************************
  ** Entity table:
  ************************************************************************/

  /** Initialize and return the entity table */
  public Table initEntities() {
    // === should get entities from transaction if present 
    //     my $ents = $trans->get_feature('entities') if defined $trans;

    if (entities == null) {
      super.initEntities();

      ent("url", transaction.url());

    }

    /*
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

	    'url'		=> ((ref $url)? $url->as_string : ''),
	    'urlQuery'		=> $query,
	    'urlPath'		=> $path,

	    'piaHOST'		=> $main::PIA_HOST,
	    'piaPORT'		=> $main::PIA_PORT,
	    'piaDIR'		=> $main::PIA_DIR,

	    'agentNames'	=> $agentNames,
	    'entityNames'   	=> '',
	    'actorNames'	=> $tagset? $tagset->actor_names : '',

	};

	$trans -> set_feature('entities', $ents) if defined $trans;
	## === WARNING! Agents can pass info through entities now ===
    }

*/

    /* Set these even if we retrieved the entity table from the */
    /* transaction -- the agent is (necessarily) different      */

    ent("agentName", agent.name());
    ent("agentType", agent.type());

    return entities;
  }



  /************************************************************************
  ** Run the Interpretor:
  ************************************************************************/

  /** Run a standard InterForm file on behalf of an Agent. Output is
   *  	sent directly to the transaction's receiver.  */
  public static void interform(Agent agent, String filepath, 
			       Transaction trans, Resolver res) {
    OutputStream out = null;	// === get from transaction.
    Run env = new Run(agent, trans, res, filepath);
    env.runStream(env.open(filepath), out, "Standard");
  }

  /** Run a standard InterForm file on behalf of an Agent. Output is
   *  	sent to a given OutputStream.  */
  public static void interform(Agent agent, String filepath, OutputStream out,
			       Transaction trans, Resolver res) {
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
    new Run(agent, trans, res, null).runCode(code, "Standard");
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
