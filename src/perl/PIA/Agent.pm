package PIA::Agent; ###### superclass for PIA agents
###	$Id$
###
###	Agents have both a name and a type.  
###	   An agent responds to requests for //agency/$name
###	   ...but looks for interforms in  .../Agents/$type
###	This imposes a primitive class system on agents.

use DS::Thing;			# Generic data structure
push(@ISA, DS::Thing);

use IF::Run;			# The new InterForm interpretor.
use PIA::Utilities;		# File and conversion utilities
#use PIA::Agent::Options;	# converting paths, loading files...
use PIA::Agent::Machine;

sub new {
    my($class, $name, $type) = @_;
    my $self = {};
    $self->{_list} = ['name', 'type', ];
    bless $self,$class;

    ## We do not initialize $self->criteria.
    ##   because undefined matches nothing, 
    ##   while an empty list matches everything.

    $self->name($name) if defined $name;
    if (defined $type) {
	$self->type($type);
    } else {
	$self->type($name) if defined $name;
    }
    
    $self->initialize;
    return $self;
}

sub initialize {
    my $self=shift;

    ## Default initialization; sub classes may override

    my $name=$self->name;
    my $type=$self->option('type');
    $self->type($type) if defined $type && $type ne $name;

    $self->option('name', $name);
    $self->option('type', $self->type);

    ## Issue a request for the initialization document.

    my $url="/$name/initialize.if";
    my $request=$self->create_request('GET',$url);
    $self->submit($request);
}

############################################################################
###
### Access Functions:
###

sub name {
    my($self, $name) = @_;
    $$self{'name'} = $name if defined $name;
    return $$self{'name'} ;
}

sub type {
    my($self, $type) = @_;
    $$self{'type'} = $type if defined $type;
    return $$self{'type'} ;
}

sub version{
    my($self,$argument)=@_;
    $$self{version}=$argument if defined $argument;
    return $$self{version} if exists $$self{version};
    my $string="PIA/";
    my $type = $self->type;
    my $name = $self->name;
    $string .= "$type/" if $type ne $name;
    $string .= $name;
    return $string;
}

sub error_message {
    my($self,$argument)=@_;
    print "agent " . $self->name . " Reports an error: $argument"
	unless $main::quiet;
}

sub agent_directory{
    ## returns a directory that we can write data into.
    ##  creates one if necessary, starts with agent_directory,
    ##  then if_root, USR_ROOT/$name, PIA_ROOT/$name, /tmp/$name

    my($self)=@_;
    my $directory=$self->dir_attribute(agent_directory);
    return $directory if $directory;
    my  @possibilities;
    my $name=$self->name;
    my $type=$self->type;

    push(@possibilities,$main::USR_DIR . "/$name/");
    push(@possibilities,$main::USR_DIR . "/$type/");

    ## It is really a bad idea to mix these with the InterForms --
    ## 	  it leads to a potentially serious security hole.

    push(@possibilities, "/tmp/$name/"); # default of last resort

    foreach $directory (@possibilities) {
	$directory =~ s:/$::;	# mkdir doesn't like trailing slash!
	if(-e $directory  || mkdir($directory,0777)){
	    if(-d $directory && -w $directory) {
		$directory=$self->dir_attribute(agent_directory,$directory );
		return "$directory/";
	    }
	}
    }
    $self->error_message("could not find appropriate, writable directory");
    return ;
}

sub agent_if_root{
    ## returns a directory that we can write InterForms into
    ##  creates one if necessary, starts with if_root, then
    ##  USR_ROOT/$name, PIA_ROOT/$name, /tmp/$name

    my($self)=@_;
    my $root=$self->dir_attribute(if_root);
    my  @possibilities;
    push(@possibilities,$root) if $root;
## not clear if we want to append agent name to if_root, for now we assume that
##  someone else did the right thing
    my $name=$self->name;
    my $type=$self->type;

    push(@possibilities,$main::USR_ROOT . "/$name/");
    push(@possibilities,$main::USR_ROOT . "/$type/");
    push(@possibilities,$main::PIA_ROOT . "/$name/");
    push(@possibilities,$main::PIA_ROOT . "/$type/");
    push(@possibilities, "/tmp/$name/"); # default of last resort

    foreach $directory (@possibilities) {
	$directory =~ s:/$::;	# mkdir doesn't like trailing slash!
	if(-e $directory  || mkdir($directory,0777)){
	    if(-d $directory && -w $directory) {
		$directory=$self->dir_attribute(agent_directory,$directory );
		return "$directory/";
	    }
	}
    }
    $self->error_message("could not find appropriate, writable directory");
    return ;
}

sub agent_url{
    my($self, $path)=@_;

    ## returns the base url (as string) for this agent
    ##   optional path argument just for convenience-- 
    ##	 returns full url for accessing that file

    my $url=$main::PIA_URL . $self->name . "/$path";
    return $url;
}

############################################################################
###
### Matching Transactions:
###
###	Agents maintain a list of feature names and expected values;
###	the features themselves are maintained by a FEATURES object
###	attached to each transaction.
###

sub criteria {
    my ($self, $arg) = @_;

    ## Return (or set) the list of match criteria.

    $$self{'_criteria'} = $arg if defined $arg;
    return $$self{'_criteria'};
}

sub match_criterion {
    my($self, $feature, $value, $code)=@_;

    ## Set a match criterion.
    ##    $feature is string naming a feature
    ##    $value is 0,1 (exact match--for don't care, omit the feature)
    ##    $code is perl function takes transaction as argument returns Boolean

    my $criteria=$self->criteria();
    $criteria = $self->criteria([]) unless defined $criteria;

    PIA::TFeatures::register($feature => $code) if defined $code;
    
    $value = 1 unless defined $value;
    my $i;

    ## If feature already present, only change the value
    for ($i = 0; $i <= $#$criteria; $i += 2) {
	if ($$criteria[$i] eq $feature) {
	    $$criteria[$i+1] = $value;
	    return $criteria;
	}	
    }

    ## Not present -- add it.

    push (@$criteria, $feature);
    push (@$criteria, $value);
    return $criteria;
}

############################################################################
###
### Machine: 
###
###	agents are associated with a virtual machine which is an
###	interface for actually getting and sending transactionss.  Posts
###	explicitly to an agent get sent to the agent's machine (then to
###	the agent's interform_request method). Other requests can be
###	handled implicitly by the agent.

sub machine {
    my($self, $machine)=@_;

    ## Return the virtual Machine to which explicit requests are directed.
    ##	 an agent may use multiple virtual machines for different kind
    ##	 of requests; this is the canonical one that explicit requests
    ##	 are directed to.

    if(! exists $$self{_machine}  && ! $machine){
	$machine=PIA::Agent::Machine->new($self);
    }	
    $$self{_machine}=$machine if $machine;
    return $$self{_machine};
}



############################################################################
###
### Handle transactions:
###
###	These are normally overridden by subclasses; they can also be 
###	handled by code or InterForm hooks.
###

sub act_on {
    my($self, $transaction, $resolver)=@_;

    ## Called by the Resolver to act on a transaction that this agent
    ##	  has matched.   

    return $self->run_hook('act_on', $transaction, $resolver);
}

sub handle {
    my($self, $transaction, $resolver)=@_;

    ## Handle a transaction matched by an act_on method.  
    ##	  Requests directly _to_ an agent are handled by its Machine;
    ##	  the "handle" method is used only by agents like "cache" that
    ##	  may want to intercept a transaction meant for somewhere else.

    return $self->run_hook('handle', $transaction, $resolver);
}

sub respond {
    my($self, $trans, $res)=@_;

    ## Respond to a direct request.  
    ##	This is called from the agent's Agent::Machine

    my $url = $trans->url;
    local $resolver = $res;
    respond_to_interform($self, $trans, $url, $resolver);
}

############################################################################
###
### Options:
###
###	Options are strings stored in attributes.  Options may have 
###	corresponding features derived from them, which we compute on demand.

sub option{
    my($self,$key,$value)=@_;

    ## Set or retrieve an option.
    ##	  If a feature with the same name exists, it has to be recomputed
    ##	  whenever we change the option.

    if (defined $value) {
	$self->attr($key, $value);
	$self->compute($key) if $self->has($key);
    }
    return $self->attr($key);
}

sub  parse_options{
    my($self, $argument, $hash)=@_;
    print "parsing options \n" if  $main::debugging;
    $hash=$argument->parameters unless defined $hash;
    foreach $key (keys(%{$hash})){
	print("  setting $key = ",$$hash{$key},"\n") if  $main::debugging;
	$self->option($key,$$hash{$key});
    }
}

###############====================================################

### === These really ought to be implemented using features ===

$home = $ENV{'HOME'};
sub file_attribute {
    ## Set or retrieve a file attribute.
    ##	  Performs ~ expansion on the filename.
    my ($self, $key, $value) = @_;

    if (defined $value) {
	$$self{"_$key"} = $value;
    } else {
	$value = $$self{"_$key"};
	if (! defined $value) {
	    $value = $self->option($key);
	    print "substituting $home for ~ in $value\n" if $main::verbose;
	    $value =~ s:^\~/:$home/:;
	    $$self{"_$key"} = $value if defined $value;
	}
    }
    return $value;
}

sub dir_attribute {
    ## Set or retrieve a directory attribute.
    ##	  Performs ~ expansion on the filename
    ##    Makes sure that it ends in a '/' character.
    my ($self, $key, $value) = @_;

    if (defined $value) {
	$$self{"_$key"} = $value;
    } else {
	$value = $$self{"_$key"};
	if (! defined $value) {
	    $value = $self->option($key);
	    if (defined $value){
		print "substituting $home for ~ in $value\n" if $main::verbose;
		$value =~ s:^\~/:$home/:;
		if ($value !~ m:/$:) { $value .= '/'; }
		$$self{"_$key"} = $value;
	    }
	}
    }
    return $value;
}


############################################################################
###
### Code Files:
###

local $agent;
local $resolver;	
local $context;
local $request;
local $response;
local $url;
local $path;

### === no longer used ===
sub load_file {
    my ($self, $attr, $fn_attr, $default) = @_;

    ## Read the file whose name is in attribute $fn_attr (or $default)
    ##	  and stash it as one enormous string attribute in $attr.

    my $name = $self->name;
    my $fn = $self->file_attribute($fn_attr);
    $fn = $default unless defined $fn && $fn ne '';
    $fn = $self->find_interform($fn);

    return unless defined $fn;

    my $code = readFrom($fn);
    print "  $name loaded $attr from $fn\n" unless $main::quiet;

    $self->attribute($attr, $code);
    return 1;
}

sub compile_file {
    my ($self, $attr, $fn_attr, $default) = @_;

    ## Read the file whose name is in attribute $fn_attr (or $default)
    ##	  and make it into an anonymous subroutine in &$attr.  UCK!
    ##	  Note that the code is compiled in PIA_AGENT and not in the
    ##	  appropriate agent subclass.  Not too good.

    my $name = $self->name;
    my $fn = $self->file_attribute($fn_attr);
    $fn = $default unless defined $fn && $fn ne '';
    $fn = $self->find_interform($fn);

    return unless defined $fn;

    my $code = readFrom($fn);
    $code = "\$subr = sub {\n" . $code . "\n};\n";

    local $subr;
    my $status = eval($code) if defined $code;
    print "  $name: error in $fn: $@\n" if $@ ne '';
    print "  $name compiled $attr from $fn\n" unless $main::quiet;

    $self->attr("_$attr", $subr);
    return 1;
}



############################################################################
###
### Evaluating Code in Context:
###
### 	Execute some code in the context of the current agent.  
###	This allows other objects (e.g. InterForms and hooks) to be run
###	as if they were part of the agent that owns them.

sub run_code {
    my ($self, $code, $trans, $res, $op) = @_;

    ## Run some code in the context of this agent.
    ##	  The context, defined by local variables, is suitable for either
    ##	  an act_on or a handle operation.

    return if !defined $code;
    $op = 'run' if !defined $op;
    my $status;

    local $agent = $self;
    local $resolver;	
    local $context = $trans;
    local $request = $trans;
    local $url = $trans->url;
    local $path;

    $resolver = $res if defined $res;
    if (defined $trans && $trans->is_response) {
	$response = $trans;
	$request  = $trans->response_to;
    }
    $path = $url->path if defined $url;

    if (ref($code) eq 'CODE') {
	$status = &$code($trans, $res);
    } elsif (ref($code) =~ m'IF::') {
	$status = IF::Run::interform_hook($self, $code, $trans, $res);
    } elsif (ref($code)) {
	$status = $code -> $op($trans, $res);
    } else {
	$status = eval ($code) if defined $code;
	print "Error in $attr string: $@\n" if $@ ne '';
    }
    return $status;
}


sub run_hook {
    my ($self, $attr, $trans, $res) = @_;

    ## Run the code attached to a hook attribute.

    my $code = $self->attr("_$attr");
    return $self->run_code($code, $trans, $res, $attr);
}

sub set_hook {
    my ($self, $attr, $code) = @_;

    ## Define a hook corresponding to an attribute

    $self->attr("_$attr", $code);
}


############################################################################
###
### Utility functions for responding to requests:
###

sub create_request {
    my($self,$method,$url,$content)=@_;

    ## Create a new request given $method, $url, and $content
    ##
    ##	  If $content is a reference, we assume that it refers to an
    ##	  HTML element containing a form, so construct the appropriate 
    ##	  contents for a PUT request.

 # TBD proper handling of content and types and headers

    my $request=new HTTP::Request  $method,$url;
    print "making $method request to $url\n" if $main::debugging;

    if (ref($content)){
	## treat as html element
	my $string="";
	## create string out of form parameters, perhaps should check tag type
	$content->traverse(
	sub {
	    my($self, $start, $depth) = @_;
	    return 1 unless $start;
	    my $tag = $self->tag;
	    return 1 unless $tag =~ /input/;
	    my $key = $self->attr('name');
	    return 1 unless defined $key;
	    my $value = $self->attr('value');
	    return 1 unless defined $value;
	    $string.="$key=$value&";
	    1;
	}, 1);
	$request->content($string);
    }else {
	$request->content($content) if defined $content ;
    }
    return $request;
}

sub retrieve {
    my($self,$request,$file)=@_;

    ## simple utility to grab a file or other URL.
    ##	  Because it uses LWP::UserAgent it gets the content type right.
#if file exists, writes  content to the file

##should be using proxy...
## user agent should remain in existence
    my $ua = $self->user_agent;

    my $response;
#    $request=TRANSACTION->new($request) unless ref($request) eq 'TRANSACTION';
    $response=$ua->simple_request($request,$file); 

    return $response;
}

## get or set the user agent... legacy from when agents actually retrieved
# documents directly...
sub user_agent{
    my($self,$ua)=@_;
    $$self{_ua}=$ua if $ua;
    $$self{_ua}=new LWP::UserAgent unless $$self{_ua};
    #should set parameters (proxy, etc.) here
    return $$self{_ua};
}

############################################################################
###
### Interform Processing:
###

sub find_interform {
    my($self, $url, $noDefaults) = @_;

    ## Find an interform, using a simple search path and a crude kind
    ## of inheritance.  Allow for the fact that the user may be trying
    ## to override the interform by putting it in $USR_ROOT/$name/.

    ## The search path used is:
    ##		option(root)/name : option(root)
    ##		USR_ROOT/name : PIA_ROOT/name : USR_ROOT : PIA_ROOT

    return unless $url;
    my $path = ref($url) ? $url->path() : $url;
    my $name = $self->name;
    my $type = $self->type;
    my $form;

    ## Find interform name in URL.

    if ($noDefaults) {		# are we doing defaults?
	## no
    } elsif ($path =~ m:$name/(.*)$:) {
	$form = $1 || "index.if";	# default to index.if
    } elsif ($path =~ m:$name$:) {
	$form = "home.if";		# default to home.if
    } elsif ($path =~ m:^/$: || $path eq '') {
	$form = 'ROOTindex.if';		# root's index.
	## In practice $path is never null; either the browser
	## or the acceptor forces a trailing slash.
    }

    my $if_path = $self->dir_attribute(if_path);
    if (! defined $if_path) {
	## If the path isn't already defined, set it up now.
	##
        ##  the path puts any  defined if_root first 
	##   (if_root/$name, if_root/$type, if_root),
	##  then USR_ROOT (USR_ROOT/$name, USR_ROOT/$type)
	##  then PIA_ROOT (PIA_ROOT/$name, PIA_ROOT/$type)
	##  then USR_ROOT, then PIA_ROOT (for inheriting forms)

	my $home = $main::PIA_ROOT;
	if ($home !~ m:/$:) { $home .= "/"; }

	my $root = $self->dir_attribute(if_root);
	if (defined $root) {
	    ## Handle a user-defined root first:

	    if ($root !~ m:/$:) { $root .= "/"; }
	    if ($root =~ m:/$name/$:) { $root =~ s:/$name/$:/:; } 
	    if ($root =~ m:/$type/$:) { $root =~ s:/$type/$:/:; } 
	    push(@$if_path, "$root$name/");
	    push(@$if_path, "$root$type/") if $name ne $type;
	    push(@$if_path, "$root");
	}
    
	## Then see whether the user has overridden the form.
	##    It's possible that one of these will be a duplicate.
	##    That slows us down, but not much.
	$root = $main::USR_ROOT;
	if ($root !~ m:/$:) { $root .= "/"; }

	push @$if_path, ("$root$name/");
	push @$if_path, ("$root$type/") if $name ne $type;
	push @$if_path, ("$home$name/");
	push @$if_path, ("$home$type/") if $name ne $type;
	push @$if_path, ("$root","$home");

	$self->dir_attribute(if_path,$if_path);
    }
    
    print "find_interform  $form: (@$if_path)\n" if $main::debugging;

    foreach $file (@$if_path) { return "$file$form" if -e "$file$form"; }

    return;			#found no file
}

### === We need to be able to handle CGI scripts eventually.  
###	We need this for the DOFS, in particular.

sub respond_to_interform {
    my($self, $request, $url, $res)=@_;

    ## Respond to a request directed at one of an agent's interforms. 
    ##	  The InterForm's url may be passed separately, since the agent may
    ##	  need to modify the URL in the request.  It can pass either a full
    ##	  URL or a path.

    if($request->method eq 'PUT' && ! $url){
	return $self->respond_to_interform_put($request);
    }
    $url = $request->url unless defined $url;
    my $file=$self->find_interform($url);

    local $response;		# available for interforms.
    local $resolver = defined($res)? $res : $main::resolver;

    my $string;
    if(! defined $file){
	$response=HTTP::Response->new(&HTTP::Status::RC_NOT_FOUND,
				      "nointerform");
	$response->content("no InterForm file found for $url");
	$response->header('Version',$self->version());
	## === should really just return 0 ===
    } else {

	if ($file =~ /\.if$/i) {
	    ## If find_interform substituted .../home.if for .../ 
	    ## we have to tell what follows that it's an interform.
	    $request->assert('interform');
	}

	if($request->is('interform')) {
	    $string=IF::Run::interform_file($self, $file, $request, $resolver);
	} elsif ($file =~ /\.cgi$/i && -x $file) {
	    print $self->name . " Executing $file \n" if $main::debugging;
	    ## === not entirely clear what to do for CGI's ===
	    ## === have to handle the MIME header ===
	} else {
	    my $new_url= newlocal URI::URL $file;
### I don't think we need to clone the request,url should be enough
	    
	    my $new_request=$request->request->clone;
	    print $self->name . " looking up $new_url\n" if $main::debugging;
	    $new_request->url($new_url);
	    $response=$self->retrieve($new_request);
	    my $content=$response->content;
	    $content =~ s/<BASE HREF[^>]*>//;
	    $response->content($content);
	    print $self->name." contenttype: " . $response->content_type."\n" 
		if $main::debugging;
	}	    
       
	if (! defined $response) {
	    $response=HTTP::Response->new(&HTTP::Status::RC_OK, "OK");
	    $response->content_type("text/html");    
	    $response->header('Version',$self->version());
	    $response->content($string);
	}
    }
    return unless ref($response);
    $response->header('Version',$self->version()) unless $response->header('Version');
    return $request->respond_with($response, $main::this_machine);
}

##Puts need a slightly different processing...
##for now uses above method after hacking url
sub respond_to_interform_put{
    my($self,$request)=@_;
    print "interform put request \n" if $main::debugging;
    #agents may subclass
    my $url=$request->url;
    
    my $path = ref($url) ? $url->path() : $url;
    ##for now stickeverything after the .if onto form params
    if($path=~/(.*\.if)(.*)$/){
	my %parameters;
	$parameters{'_post_path'}=$2;
	$request->parameters(\%parameters);
         return $self->respond_to_interform($request,$1);
}
print ".if not found\n";
return $self->respond_to_interform($request,$url);

}

1;
