###### Class PIA_AGENT -- superclass for PIA agents
###	$Id$
###
###	Agents have both a name and a type.  
###	   An agent responds to requests for //agency/$name
###	   ...but looks for interforms in  .../Agents/$type
###	This imposes a primitive class system on agents.

package PIA_AGENT;
use HTML::Parse;
use HTML::FormatPS;
use HTML::Element;
use HTML::AsSubs;		# defines element constructor functions.
				# very useful for interforms.
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
    #sub classes override
#should emit a request for /$name/initialize.if
    my $name=$self->name;
    my $type=$self->option('type');
    $self->type($type) if defined $type && $type ne $name;

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

############################################################################
###
### Attributes:
###	Attributes are stored as instance variables in the object's
###	hash.  They are set from the options if undefined.  This provides
###	a clean way to perform special processing (e.g. filename or list
###	expansion) on attributes at the time they are first needed.

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
	    my $home = $ENV{'HOME'};
	    print "substituting $home for ~ in $value\n" if $main::verbose;
	    $value =~ s:^\~/:$home/:;
	    if ($value !~ m:/$:) { $value .= '/'; }
	    $$self{$key} = $value if defined $value;
	}
    }
    return $value;
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

    my $response = $self->respond_to_interform($request);
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
    my $string, $e;
    my $form = HTML::Element->new( 'form', method=>"POST", action=>$url);
    my $t = HTML::Element->new('table');
    $form->push_content($t);
    # $e = $form;

    my %attributes;
    foreach $key ($self->options()) {
	my $values=$self->option($key);
	$e = HTML::Element->new('tr');
	$e->push_content(td("$key: "));
	$e->push_content(td(input({'name'=>$key, 'value'=>$values,
				'size'=>max(30, length($values))})));
	$e->push_content(br);
	$e->push_content("\n");
	$t->push_content($e);
    }
    $e = HTML::Element->new('tr');
    $e->push_content(td(" "));
    $e->push_content(td(input({'type'=>submit, 'value'=>$label})));
    $t->push_content($e);

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
### Utility functions for responding to requests:
###

#merges html
sub integrate_responses{
    my($response)=shift; 
    if(ref($response)){
	$response=$response->content;
    }
    my $foo;
    
    while($foo=shift){
	if(ref($foo)){
	    #bad news if type not html check foo  contenttype
	    $foo=$foo->content;
	}
	$response.=$foo;
	
    }
    return $response;
}

#return html element
sub make_form{
    my $element=HTML::Element->new( 'form',method => "POST",action => shift);
    my %attributes,$particle;
    my @widgets=@_;

    my $last_hack;
    foreach $widget (@widgets){
	%attributes=%$widget;
	$attributes{tag}='input' unless $attributes{tag};
	$particle=HTML::Element->new( $attributes{tag});
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
	    $element->push_content("$attributes{'name'} :");
	    $element->push_content($particle);
	    $last_hack=$particle;
	}
    }
    return $element;
}


sub request{
#put request on stack for resolution
    my($self,$request)=@_;
    $request=TRANSACTION->new($request,$main::this_machine) unless ref($request) eq 'TRANSACTION';
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

############################################################################
###
### Find Interforms for agent:
###
###	The caller should map extension to file type if necessary.

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

    my $if_path = $self->attribute(if_path);
    if (! defined $if_path) {
	## If the path isn't already defined, set it up now.

	my $home = $main::PIA_ROOT;
	if ($home !~ m:/$:) { $home .= "/"; }

	my $root = $self->dir_attribute(if_root);
	if (defined $root) {
	    ## Handle a user-defined root first:

	    if ($root !~ m:/$:) { $root .= "/"; }
	    if ($root =~ m:/$name/$:) { $root =~ s:/$name/$:/:; } 
	    if ($root =~ m:/$type/$:) { $root =~ s:/$type/$:/:; } 
	    push(@$if_path, "$root$name/$form");
	    push(@$if_path, "$root$name/$form") if $name ne $type;
	    push(@$if_path, "$root/$form");
	}
    
	## Then see whether the user has overridden the form.
	##    It's possible that one of these will be a duplicate.
	##    That slows us down, but not much.
	$root = $main::USR_ROOT;
	if ($root !~ m:/$:) { $root .= "/"; }

	push @$if_path, ("$root/$name/$form");
	push @$if_path, ("$root/$type/$form") if $name ne $type;
	push @$if_path, ("$home/$name/$form");
	push @$if_path, ("$home/$type/$form") if $name ne $type;
	push @$if_path, ("$home/$form", "$root/$form");

	$self->attribute($if_path);
    }
    
    print "find_interform: (@$if_path)\n" if $main::debugging;

    foreach $file (@$if_path) { return $file if -e $file; }

    return;			#found no file
}

############################################################################
############################################################################
###
### Interform Processing:
###
###	Interforms must execute in the context of an agent; therefore we
###	cannot create a new class for interforms.
###
### ===	It would be better to use something like <html language=perl> and
###	have a new element type, e.g. <eval>, for code elements.  Another
###	possibility would be to use the extension.  Knowing the language up
###	front makes it possible to pass different languages off to an
###	appropriate agency for evaluation.
###
### === We need to be able to handle CGI scripts and plain HTML
###	eventually.  We need this for the DOFS, in particular.
###

### Environment in which the code is run:

local $agent;			# The agent that owns the interform
local $request;			# The request being handled
#local $response;		# The response being constructed.

local $current_self;		# old name for $agent
local $current_request;		# old name for $request

#this is  a callback for html traverse
#TBD change for multi-threads
sub execute_interform{
    my $element=shift;
    my $start=shift;
    return 1 unless $start;	# only do it once, when entering a node.

    my $tag = lc($element->tag);
    return 1 unless  ($tag eq "code" || $tag eq "eval");

    ## At this point we have a code element.  Branch on language attribute.
    my $code_status;
    my $language=lc($element->attr('language'));
#obvious branching on language TBD
    if ($language eq 'perl'){

	my $code_array=$element->content;
#       replace with new array
	my @new_elements;

	my $code;
	while($code=shift(@$code_array)){
	    print "execing $code \n" if $main::debugging > 1;
	    if (ref($code)){
		$code_status = $code; #this is an html element
	    } else {
		#evaluate string and return last expression value
		$code_status=eval $code;
		print "Interform error: $@\n" if $@ ne '' && ! $main::quiet;
		print "code status is $code_status\n" if  $main::debugging;
	    }
	    push(@new_elements,$code_status) if $code_status;
	}
	my $parent=$element->parent();
	$parent->pos($element);
	while($code=shift(@new_elements)){
	    if (ref($code)) {
		$parent->insert_element($code);
		$element->tag('output'); # === kludge because pos broken ===
	    } else {
		push(@$code_array,$code);
	    }
	}
	$element->attr('language',"perl_output");
    } 
#other languages?
    return 0;#do children?
    
}

 # remember to delete parse tree otherwise memory leak occurs
sub run_interform{
    my ($self,$html,$req)=@_;

    ## Set up appropriate environment:

    $agent = $self;
    $request = $req;
    $current_request=$req;
    $current_self=$self;	# old name
    
    ## execute the code:

    my $status=$html->traverse(\&execute_interform,1);
    return $status;
}

sub parse_interform_string{
    my($self,$string,$request)=@_;
    $HTML::Parse::IGNORE_UNKNOWN = 0;
    $HTML::Parse::IMPLICIT_TAGS = 0;
    my $html=parse_html($string);
    my $status=$self->run_interform($html,$request);
    my $string=$html->as_HTML;
    $html->delete;
    return $string;
}

sub parse_interform_file{
    my($self,$file,$request)=@_;
    $HTML::Parse::IGNORE_UNKNOWN = 0;
    $HTML::Parse::IMPLICIT_TAGS = 0;
    my $html=parse_htmlfile($file);
    my $status=$self->run_interform($html,$request);
    my $string=$html->as_HTML;
    $html->delete;
    return $string;
}

sub respond_to_interform {
    my($self, $request, $url)=@_;

    ## Respond to a request directed at an agent, by running an interform. 
    ##	  The InterForm's url may be passed separately, since the agent may
    ##	  need to modify the URL in the request.  It can pass either a full
    ##	  URL or a path.

    ## === At some point we're going to have to have a hash that maps
    ##	  extensions into handlers, because not everything is an
    ##	  interform.  We should allow plain HTML and CGI's.

    $url = $request->url unless defined $url;
    my $file=$self->find_interform($url);
    my $name = $self->name();
    print "$name parsing $file\n" if  $main::debugging;

    local $response;		# available for interforms.

    if(! defined $file){
	$response=HTTP::Response->new(&HTTP::Status::RC_NOT_FOUND,
				      "nointerform");
	$response->content("no InterForm file found for $url");
	$response->header('Version',$self->version());
	## === should really just return 0 ===
    } else {
#TBD check for path parameters  
	## === TBD verify extension and file type.

	my $string=$self->parse_interform_file($file,$request);
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

############################################################################
###
### Initialization Files:
###
###	Initialization files are looked up like InterForms, but instead
###	of containing code to evaluate, they just contain forms to
###	submit.  Every link is fetched, and every form is submitted.
###
### ===	It might be better to make them interforms, and have the first
###	code chunk auto_submit flag.
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
	$html = parse_html($fn);
    } else {
	my $file = $find? $file = $self->find_interform($fn) : $fn;
	return unless -e $file;
	$html = parse_htmlfile($file);
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
    $html->delete;
    return $count;
}

1;
