###### Class PIA_AGENT -- superclass for PIA agents
###	$Id$
###
###	Agents have both a name and a type.  
###	   An agent responds to requests for //agency/$name
###	   ...but looks for interforms in  .../Agents/$type
###	This imposes a primitive class system on agents.

package PIA_AGENT;

### === HTML::Element and HTML::AsSubs are now used only in 
### === legacy InterForms and in the printer agent.
#use HTML::Element;
#use HTML::AsSubs;		# defines element constructor functions.

use IF::Run;			# The new InterForm interpretor.

sub new {
    my($class, $name, $type) = @_;
    my $self = {};
    my $options={};
    my $criteria=[];		# a list (not a hash) of name => value pairs.
        
    bless $self,$class;
    $$self{'options'}=$options;
    $$self{'criteria'}=$criteria;

    $self->name($name) if defined $name;
    if (defined $type) {
	$self->type($type);
    } else {
	$self->type($name) if defined $name;
    }
    
    $self->initialize;
    return $self;
}

sub initialize{
    my $self=shift;

    ## Default initialization; sub classes may override

    my $name=$self->name;
    my $type=$self->option('type');
    $self->type($type) if defined $type && $type ne $name;

    ## Issue a request for the initialization document.
    my $url="/$name/initialize.if";
    my $request=$self->create_request('GET',$url);
    $self->request($request);
}

sub name {
    my($self,$name)=@_;
    $$self{'name'}=$name if defined $name;
    return $$self{'name'} ;
}

sub type {
    my($self,$type)=@_;
    $$self{'type'}=$type if defined $type;
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

sub error_message{
    my($self,$argument)=@_;
    print "agent " . $self->name . " Reports an error: $argument" unless $main::quiet;
    
}
############################################################################
###
### Attributes:
###	Attributes are stored as instance variables in the object's
###	hash.  They are set from the options if undefined.  This provides
###	a clean way to perform special processing (e.g. filename or list
###	expansion) on attributes at the time they are first needed.

#################
### CAUTION: this blows the correspondence between options and attributes
###           once an attribute gets cached, it never changes
###           should revisit after changing relevant options?

sub attribute {
    ## Set or retrieve a named attribute
    my ($self, $key, $value) = @_;
    if (defined $value) {
	$$self{$key} = $value;
    } else {
	$value = $$self{$key};
	if (! defined $value) {
	    $value = $self->option($key);
	    $$self{$key} = $value if defined $value;
	}
    }
    return $value;
}

sub file_attribute {
    ## Set or retrieve a file attribute.
    ##	  Performs ~ expansion on the filename.
    my ($self, $key, $value) = @_;

    if (defined $value) {
	$$self{$key} = $value;
    } else {
	$value = $$self{$key};
	if (! defined $value) {
	    $value = $self->option($key);
	    my $home = $ENV{'HOME'};
	    print "substituting $home for ~ in $value\n" if $main::verbose;
	    $value =~ s:^\~/:$home/:;
	    $$self{$key} = $value if defined $value;
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
	$$self{$key} = $value;
    } else {
	$value = $$self{$key};
	if (! defined $value) {
	    $value = $self->option($key);
	    if (defined $value){
		my $home = $ENV{'HOME'};
		print "substituting $home for ~ in $value\n" if $main::verbose;
		$value =~ s:^\~/:$home/:;
		if ($value !~ m:/$:) { $value .= '/'; }
		$$self{$key} = $value;
	    }
	}
    }
    return $value;
}

sub agent_directory{
    ## returns a directory that we can write to
    ##  creates one if necessary, starts with agent_directory,
    ##  then if_root, USR_ROOT/$name, PIA_ROOT/$name, /tmp/$name

    my($self)=@_;
    my $directory=$self->dir_attribute(agent_directory);
    return $directory if $directory;
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
    ### returns the base url (as string) for this agent
	###  optional path argument just for convenience-- returns full url for accessing that file
    my($self,$path)=@_;
    my $url=$main::PIA_URL . $self->name . "/$path";
    return $url;
}

############################################################################
###
### Matching Requests:
###
###	Agents maintain a list of feature names and expected values;
###	the features themselves are maintained by a FEATURES object
###	attached to each transaction.
###

sub criteria {
    my ($self, $arg) = @_;
    $$self{'criteria'} = $arg if defined $arg;
    return $$self{'criteria'};
}

sub match_criterion {
    my($self,$feature,$value,$code)=@_;

    ## Set a match criterion.
    ##    $feature is string naming a feature
    ##    $value is 0,1 (exact match--for don't care, omit the feature)
    ##    $code is perl function takes transaction as argument returns Boolean

    my $criteria=$self->criteria();
    FEATURES::register($feature => $code) if defined $code;
    
    $value = 1 unless defined $value;
    push (@$criteria, $feature);
    push (@$criteria, $value);
    return $criteria;
}

############################################################################
### machine: agents are associated with a virtual machine which is an
###  interface for actually getting and sending requests
### posts  explicitly to an agent get sent to the agents machine
### ( then to the agent's interform_request method)
###  other requests can be handled implicitly by the agent

sub  machine{
    my($self,$machine)=@_;
    #anagent may use multiple virtual machines for different kind of requests
    # this machine should be the canonical 1 that  explicit requests are directed to
    if(! exists $$self{_machine}  && ! $machine){
	$machine=AGENT_MACHINE->new($self);
    }	
    $$self{_machine}=$machine if $machine;
    return $$self{_machine};
}



############################################################################
###
### Handle transactions:
###

###### agent -> act_on($transaction, $resolver)
###
###	Called by the Resolver to act on a transaction that this agent
###	has matched.  It is acceptable for an agent to leave this alone 
###	if it only responds to direct requests.
###
sub act_on {
    my($self, $transaction, $resolver)=@_;

    ## Default is to do nothing.  
    ##   OK for agents that only respond to direct requests.

    return 0;
}

###### agent -> handle($transaction, $resolver)
###
###	Handle a request directed AT an agent.  The default is to use an
###	interform; subclasses or instances should override this or install
###	methods if different behavior is required.
###
local $current_resolver;
sub handle{
    my($self, $request, $resolver)=@_;
    $current_resolver = $resolver;

## no longer needed, agent machine should call
##    my $response = $self->respond_to_interform($request);
    return 0 unless defined $response;
    $resolver->push($response);
    return 1;
}

############################################################################
###
### Options:
###

sub options {
    ## Return the names, but not the values, of the options.

    my $self=shift;
    return keys(%{$$self{'options'}});
}

sub options_as_html {
    ## Return a series of form input fields for the options.

    my($self)=@_;
    my $string;
    foreach $key ($self->options){
	my $values=$self->option($key);
	$string.="<input name=$key size=50 value=$values >";
	## === size should be parameter; value should be quoted. ===
    }
    return $string;
}

sub as_html{
    ## Return a form element that can re-install the agent.
    my($self)=@_;
    my $name=$self->name;
    my $element=$self->options_form("/agency/install_agent.if","install_$name");
    return $element; 
}

sub max {
    my ($x, $y)=@_;
    return ($x >= $y)? $x : $y;
}


# need to add functions for multiple values and comments
sub options_form{
    my($self,$url, $label)=@_;
    $label ="change_options" unless defined $label;
    my $e;
    my $form = IF::IT->new('form', method=>"POST", action=>$url);
    my $t = IF::IT->new('table');
    $form->push($t);
    # $e = $form;

    my %attributes;
    foreach $key ($self->options()) {
	my $values=$self->option($key);
	$e = IF::IT->new('tr');
	$e->push(IF::IT->new('td')->push("$key: "));
	$e->push(IF::IT->new('td')
		 ->push(IF::IT->new('input',
				    'name'=>$key, 'value'=>$values,
				    'size'=>max(30, length($values)))));
	$e->push(IF::IT->new('br'));
	$e->push("\n");
	$t->push($e);
    }
    $e = IF::IT->new('tr');
    $e->push(IF::IT->new('td')->push(" "));
    $e->push(IF::IT->new('td')
	     ->push(IF::IT->new('input', 'type'=>submit, 'value'=>$label)));
    $t->push($e);

    return $form;
}

sub  parse_options{
    my($self,$argument)=@_;
    print "parsing options \n" if  $main::debugging;
    my $hash=$argument->parameters;
    foreach $key (keys(%{$hash})){
	print("  setting $key = ",$$hash{$key},"\n") if  $main::debugging;
	$self->option($key,$$hash{$key});
    }
}

sub option{
    ## Set or retrieve an option.
    ##	  Subclasses should override this to perform magic on some options.
    ##	  For example, expanding ~ in filenames or creating list values.

    my($self,$key,$value)=@_;
    my $options=$$self{'options'};
    $$options{$key}=$value if defined $value;
#    my $v = $$options{$key};    print "option($key $value) -> $v\n";
    return $$options{$key};
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
local $current_self;		# old name for $agent
local $current_request;		# old name for $request

sub readFrom {
    my ($fn, $str) = @_;

    open(FILE, "<$fn");
    while (<FILE>) {
	$str .= $_;
    }
    close(FILE);
    return $str;
}

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

    $self->attribute($attr, $subr);
    return 1;
}

sub require_file {
    my ($self, $fn_attr, $default) = @_;

    ## require the file whose name is in attribute $fn_attr (or $default)
    ##	  The file is located as an interform and require'd.

    my $name = $self->name;
    my $fn;
    $fn = $self->file_attribute($fn_attr);
    $fn = $default unless defined $fn;
    $fn = $self->find_interform($fn);

    return unless defined $fn;

    require $fn;

    print "  $name loaded $attr from $fn\n" unless $main::quiet;

    $self->attribute($code_attr, $code);
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
    local $url;
    local $path;

    $resolver = $res if defined $res;
    if (defined $trans && $trans->is_response) {
	$response = $trans;
	$request  = $trans->request;
    }
    $url = $request->url if defined $request;
    $path = $url->path if defined $url;

    local $current_self = $agent;		# old name for $agent
    local $current_request = $request;		# old name for $request

    if (ref($code) eq 'CODE') {
	$status = &$code($trans, $res);
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

    my $code = $self->attribute($attr);
    return $self->run_code($code, $trans, $res, $attr);
}


############################################################################
###
### Utility functions for responding to requests:
###

sub make_list{
    my ($reference,@rest)=@_;
    my $element;
    my $particle;
    my $key;
    if(ref($reference)){
	$element=IF::IT->new( 'dl');
	foreach $key (keys(%{$reference})){
	   $particle= IF::IT->new( 'dt');
	   $particle->push_content($key);
	   $element->push_content($particle);
	   $particle= IF::IT->new( 'dd');
	   $particle->push_content($$reference{$key});
	   $element->push_content($particle);
       }
    } else {
	unshift(@rest,$reference);
	
	$element=IF::IT->new( 'ul');
	foreach $key (@rest){
	   $particle= IF::IT->new( 'li');
	   $particle->push_content($key);
	   $element->push_content($particle);
       }
    }
    return $element;
}

#return html element
sub make_form{
    my $element=IF::IT->new( 'form',method => "POST",action => shift);
    my %attributes,$particle;
    my @widgets=@_;

    my $last_hack;
    foreach $widget (@widgets){
	%attributes=%$widget;
	$attributes{tag}='input' unless $attributes{tag};
	$particle=IF::IT->new( $attributes{tag});
	delete $attributes{tag};
	$particle->push_content($attributes{text}) if exists $attributes{text};
	delete $attributes{text};
	foreach $key (keys %attributes){
	    $particle->attr($key,$attributes{$key});
	}
	if($particle->tag() eq 'option'){
	    $last_hack->push_content($particle);
	}else{
	    my $txt=$attributes{'name'};
	    $element->push_content("$attributes{'name'} :") unless ($attributes{'type'} eq 'hidden');
	    $element->push_content($particle);
	    $last_hack=$particle;
	}
    }
    return $element;
}


### === Appears to be used only in run_init_file;
### use retrieve for the moment to actually get a response returned to you
sub request{
#put request on stack for resolution
    my($self,$request)=@_;
    $request=TRANSACTION->new($request,$main::this_machine)
	unless ref($request) eq 'TRANSACTION';
    $main::main_resolver->push($request);
}

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
    } elsif ($path =~ m:^/$:) {
	$form = 'ROOTindex.if';		# root's index.
    } elsif ($path eq '') {
	$form = 'ROOThome.if';		# root's home page.
	## === for some reason this is never accessed:        ===
	## === Probably, the browser forces a trailing slash. ===
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
    my($self, $request, $url)=@_;

    ## Respond to a request directed at an agent, by running an interform. 
    ##	  The InterForm's url may be passed separately, since the agent may
    ##	  need to modify the URL in the request.  It can pass either a full
    ##	  URL or a path.

    if($request->method eq 'PUT' && ! $url){
	return $self->respond_to_interform_put($request);
    }
    $url = $request->url unless defined $url;
    my $file=$self->find_interform($url);
    my $name = $self->name();

    local $response;		# available for interforms.
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
	    $string=IF::Run::interform_file($self, $file, $request,
					    $current_resolver);
	} elsif ($file =~ /\.cgi$/i && -x $file) {
	    print $self->name . " Executing $file \n" if $main::debugging;
	    ## === not entirely clear what to do for CGI's ===
	    ## === have to handle the MIME header ===
	} else {
	    my $new_url= newlocal URI::URL $file;
### I don't think we need to clone the request,url should be enough
	    
	    my $new_request=$request->clone;
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
    $response->request($request);
    $response=TRANSACTION->new($response,
			       $main::this_machine,
			       $request->from_machine());

    return $response;
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

############################################################################
###
### Initialization Files:
###
###	Initialization files are looked up like InterForms, but instead
###	of containing code to evaluate, they just contain forms to
###	submit.  Every link is fetched, and every form is submitted.
###

sub run_init_file {
    my($self,$fn,$find)=@_;

    ## Submit each form and get each link in $fn.
    ##    Look up $fn as an interform if $find is positive.
    ##    Treat $fn as a string if $find is negative.
    ##
    ##    This is done for initialization, and is similar enough to 
    ##    interform processing to be put with it.

    my $html;
    my $count=0;
    my $request;

    if ($find < 0) {
	$html = IF::Run::parse_html_string($fn);
    } else {
	my $file = $find? $file = $self->find_interform($fn) : $fn;
	return unless -e $file;
	$html = IF::Run::parse_init_file($file);
    }
    for (@{ $html->extract_links(qw(a form)) }) {
	my ($url, $element) = @$_;
	if ($element->tag =~ /form/i) {
	    $method=$element->attr('method');
	    $request=$self->create_request($method,$url,$element);
	} else {
	    ## A name= link would probably cause confusion.
	    $request=$self->create_request('GET',$url,$element);
	}
	my $status=$self->request($request);
	$count+=1;
    }
    ## $html->delete;
    return $count;
}

1;
