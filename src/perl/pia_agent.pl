#superclass for agents

package PIA_AGENT;
use HTML::Parse;
use HTML::FormatPS;
use HTML::Element;

sub new {
    my($class,$name) = @_;
    my $self = {};
    my $options={};
    my $criterion={};
    my $computation={};
        
    bless $self,$class;
    $self->name($name) if defined $name;
    $$self{options}=$options;
    $$self{criterion}=$criterion;
    $$self{computation}=$computation;
    
    $self->initialize;
    return $self;
}

sub initialize{
    my $self=shift;
    #sub classes override
#should emit a request for /$name/initialize.if
    my $name=$self->name;
    my $url="/$name/initialize.if";
    my $request=$self->create_request('GET',$url);
    $self->request($request);
    
}

sub name{
    my($self,$name)=@_;
    $$self{name}=$name if defined $name;
    return $$self{name} ;
    
}

sub version{
    my($self,$argument)=@_;
    $$self{version}=$argument if defined $argument;
    return $$self{version} if exists $$self{version};
    my $string="PIA/";
    $string.=$self->name;
    return $string;
    
}

############################################################################
#agents maintain a list of features and code that computes Boolean features
# resolve uses mask on features to determine which agents get which transactions
# agent can add new requests to stack or modified this request
 # another function gets called to actually handle a request

sub  new_requests{
    my($self,$request)=@_;
    #subclasses should override
    return ();
    
}

# agents should not assume every transaction they get matches their desired feature set

#feature is string naming a feature
#value is 0,1 (exact match--for don't care remove corresponding feature)
#code is perl function takes transaction as argument returns Boolean
sub match_criterion{
    my($self,$feature,$value,$code)=@_;
    my $criterion=$$self{criterion};
    $self->criterion_computation($feature,$code) if defined $code;
    
    $$criterion{$feature}=$value if defined $value;
    return $criterion;
    
}

sub criterion_computation{
    my($self,$feature,$code)=@_;
    my $computation=$$self{computation};
    $$computation{$feature}=$code if defined $code;
    return $computation;
    
}

#this should be in resolve
sub matches{
    my($self,$values)=@_;
    my $criterion=$self->match_criterion();
    print $self->name if  $main::debugging;
    foreach $key (keys %$criterion){
	return 0 unless $$values{$key}==$$criterion{$key};
	print "$key matched\n" if $main::debugging;
    }

    print "matches request\n" if  $main::debugging;
    return 1;
}

############################################################################

#sub classes or instances should override this or install methods using handler
 # by default assumes request is for an interform

sub handle{
    my($self,$request)=@_;

    my $response;
    my $file=$self->find_interform($request->url);
    print "$name parsing $file\n" if  $main::debugging;
    if(! defined $file){
	$response=HTTP::Response->new(&HTTP::Status::RC_NOT_FOUND, "nointerform");
	$response->content("no file found");
	
    } else {
#TBD check for path parameters    
	my $string=$self->parse_interform_file($file,$request);
#air if file not found    
	$response=HTTP::Response->new(&HTTP::Status::RC_OK, "OK");
	$response->content_type("text/html");    
	$response->header('Version',$self->version());
	$response->content($string);
    }
    $response->request($request);
    
    $response=TRANSACTION->new($response,$main::this_machine,$request->from_machine());
    
    return $response;
    
}

sub find_interform{
    my($self,$url)=@_;
    return unless $url;
    my $path=$url->path();
    my $name=$self->name;
    $path=~m:$name/(.*)$:;
    my $form=$1 || "home.if";
    my $root=$self->option(root);
    my $file="$root$form";
    return $file if -e $file;
    #check if we an inherit form
    $root =~ m:^(.*/)[^/]*/$:;
    my $new_root=$1;
    $file="$new_root$form";
    print "inheriting $file\n" if $main::debugging;
    return $file if -e $file;
    return;
    #found no file
}

############################################################################
#utility functions for changing, viewing options
sub options{
    my $self=shift;
    return keys(%{$$self{options}});
    
}

sub options_as_html{
    my($self)=@_;
    my $string;
    foreach $key ($self->options){
	my $values=$self->option($key);
	$string.="<input name=$key size=50 value=$values >";
    }
    return $string;
    
}

sub as_html{
#returns html element which embodies this agent--appropriate for installing agent in future
    my $name=$self->name;
    my $element=$self->options_form("/agency/install_agent.if","install_$name");
    return $element; 
}

# need to add functions for multiple values and comments
sub options_form{
    my($self,$url, $label)=@_;
    $label ="change_options" unless defined $label;
    my $string;
    my $element=HTML::Element->new( 'form',method => "POST",action => $url);
    my %attributes,$particle;
    foreach $key ($self->options){
	my $values=$self->option($key);
	$particle=HTML::Element->new(input);
	$particle->attr('name',$key);
	$particle->attr(value,$values);
	$particle->attr(size,length($values));
	$element->push_content("$key :");
	$element->push_content($particle);
	$particle=HTML::Element->new(br);
	$element->push_content($particle);
    }
    $particle=HTML::Element->new(input);
	$particle->attr(type,submit);
	$particle->attr(value,$label);
    $element->push_content($particle);
    return $element;
    
}

sub  parse_options{
    my($self,$argument)=@_;
    print "parsing  options \n" if  $main::debugging;
    my $hash=$argument->parameters;
    foreach $key (keys(%{$hash})){
	print("changing $key to",$$hash{$key}," \n\n") if  $main::debugging;
	$self->option($key,$$hash{$key});
    }
    
}

sub option{
    my($self,$key,$value)=@_;
    my $options=$$self{options};
    $$options{$key}=$value if defined $value;
    return $$options{$key};
    
}


#utility functions;
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

sub create_request{
# create a new request object given method,  url, content
 # TBD proper handling of content and types and headers
    my($self,$method,$url,$content)=@_;
    my $request=new HTTP::Request  $method,$url;
    if (ref($content)){
#treat as html element
	my $string="";
#create string out of form parameters, perhaps should check tag type
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
############################################################################
# interform processing
# interforms  must execute in context of agent therefore we cannot create
# a new class for interforms


#this is  a callback for html traverse
#TBD change formulti-threads
local $current_self;
local $current_request;


sub execute_interform{
    my $element=shift;
    my $start=shift;
    return 1 unless $start; #only do it once
    return 1 unless  lc($element->tag) eq "code";

    #getcode
    my $code_status;
    my $language=lc($element->attr('language'));
#obvious branching on language TBD
    if ($language eq 'perl'){

	my $code_array=$element->content;
#       replace with new array
	my @new_elements;

	my $code;
	while($code=shift(@$code_array)){
	    print "execing $code \n" if  $main::debugging;
	    if( ref( $code)){
		$code_status=$code; #this is an html element
	    }else{
	    #evaluate string andreturnlast expression value
		$code_status=eval $code;
		print "code status is $code_status\n" if  $main::debugging;
	    }
	    push(@new_elements,$code_status) if $code_status;
	}
	my $parent=$element->parent;
	$parent->pos($element);
	while($code=shift(@new_elements)){
	    if(ref($code)){
		$parent->insert_element($code);
	    }else{
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
    my ($self,$html,$request)=@_;
    $current_request=$request;
    $current_self=$self;
    
    # execute any perl code
    my $status=$html->traverse(\&execute_interform,1);
    
    return $status;
    
        
}

sub parse_interform_string{
    my($self,$string,$request)=@_;
    my $html=parse_html($string);
    my $status=$self->run_interform($html,$request);
    my $string=$html->as_HTML;
    $html->delete;
    return $string;
    
}

sub parse_interform_file{
    my($self,$file,$request)=@_;
    my $html=parse_htmlfile($file);
    my $status=$self->run_interform($html,$request);
    my $string=$html->as_HTML;
    $html->delete;
    return $string;
    
}

1;
