////// Submit.java:  Handler for <submit>
//	$Id$
//	Copyright 1997, Ricoh California Research Center.

package crc.interform.handle;

import crc.interform.Actor;
import crc.interform.Handler;
import crc.interform.Interp;
import crc.interform.SGML;
import crc.interform.Token;

/** Handler class for &lt;submit&gt tag */
public class Submit extends crc.interform.Handler {
  public void handle(Actor ia, SGML it, Interp ii) {

    ii.deleteIt();
  }
}

/* ====================================================================
### submit
###	Submits $it or every form in its contents.  Used during initialization. 
###	The following InterForm code makes <form> active:
###		<actor name=form handle="submit_forms"></actor>

# does not handle post submissions
# variable binding is done at time of submission
#  element added to cron list of agent which is reviewed by resolve during idle time
#  agent will run this element again, so only this handler is responsible for actual submissions

define_actor('submit-forms', 
	     'dscr' => "Submit a form or link ELEMENT
 or every form (not links) in CONTENT.  
 Optionally submit at HOUR, MINUTE, DAY, MONTH, WEEKDAY. 
 Optionally REPEAT=N times (missing hour, day, month, weekday are wildcards).  
 Optionally UNTIL=MM-DD-HH time when submissions are halted
  use options interform of agent to delete repeating entries");

sub submit_forms_handle {
    my ($self, $it, $ii) = @_;
    my $agent = IF::Run::agent();

 #if this form is not ready to run at this time then return
# takes care of time comparisons, notify agent, etc.
    return if timed_submission($it,$agent);

    if ($it->tag eq 'form') {
	my $url = $it->attr('action');
	my $method = $it->attr('method');

	my $request = $agent->create_request($method,$url,$it);
	
	    $IF::Run::resolver->unshift($request);
    } elsif ($it->attr('href')) {
	my $url = $it->attr('href');
	my $request = $agent->create_request('GET', $url);
	
	    $IF::Run::resolver->unshift($request);
    } else {
	$it->traverse(sub {
			  my($elt, $start, $depth) = @_;
			  return 1 unless $start;
			  submit_forms_handle($self, $elt, $ii) 
			      if $elt->tag eq 'form';
			  return 1;
		      }, 'ignoretext');
    }
}

@time_attrs = qw( repeat until hour minute day month weekday );

sub timed_submission {
    my ($it, $agent) = @_;

    ##  return true if $it has timing attributes and now is not the time,
    ##	otherwise return false (means that form will be run now)

    if($it->attr('cancel')){
	$agent->cron_remove($it->attr('cancel'));
	return 1;
    }
    my $timed = 0;
    my %attrs;
    my ($a, $v);

    foreach $a (@time_attrs) {
	if (($v = $it->attr($a))) {
	    $attrs{$a} = $v;
	    $timed ++;
	}
    }
    return 0 unless $timed;

    if(! $it->attr('-agent-cron-job')){
	$it->attr('-agent-cron-job',1);
	$agent->cron_add($it);
    }
				# should check for invalid submissions
    return 1;
    
#now see if it is time to submit

# don't submit if no repeat specified and we already submitted
#should remove or at least add cancel attribute
    my $last_submission=$it->attr('-last-submission');
    return 1 if $last_submission && !$it->attr('repeat');
   

   
}
*/
