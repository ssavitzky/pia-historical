package PIA::Utilities; ###### Utilities for the PIA
###	$Id$
###

use Exporter;
@ISA = qw(Exporter);

@EXPORT = qw(writeTo appendTo readFrom asHTML make_form make_list
	     run_init_file submit);


############################################################################
###
### File Utilities:
###

sub writeTo {
    my ($fn, $str) = @_;

    ## Write $str to file $fn

    open(FILE, ">$fn");
    print FILE $str;
    close(FILE);
}

sub appendTo {
    my ($fn, $str) = @_;

    ## append $str to file $fn

    open(FILE, ">>$fn");
    print FILE $str;
    close(FILE);
}

sub readFrom {
    my ($fn, $str) = @_;

    ##  Read from file $fn and returns the contents as a string.
    ##	  If an optional second arg $str is provided, it is appended to.

    open(FILE, "<$fn");
    while (<FILE>) {
	$str .= $_;
    }
    close(FILE);
    return $str;
}


############################################################################
###
### Code Files:
###

############################################################################
###
### Initialization Files:
###
###	Initialization files are looked up like InterForms, but instead
###	of containing code to evaluate, they just contain forms to
###	submit.  Every link is fetched, and every form is submitted.
###

sub submit {
    my($self, $request)=@_;

    ## put request on stack for resolution
    ##	 used only in PIA::Agent::initialize and run_init_file.

    $request=PIA::Transaction->new($request,$main::this_machine)
	unless ref($request) eq 'PIA::Transaction';

    ## Unshift the request so they get popped in sequence.

    $main::resolver->unshift($request);
}

sub run_init_file {
    my($self,$fn,$find)=@_;

    ## Submit each form and get each link in $fn.
    ##    Look up $fn as an interform if $find is positive.
    ##    Treat $fn as a string if $find is negative.

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
	my $status=$self->submit($request);
	$count+=1;
    }
    ## $html->delete;
    return $count;
}

############################################################################
###
### Conversion Utilities:
###

sub asHTML {
    my $s = shift;

    ##	convert $string to HTML by properly escaping &, <, and >.

    $s =~ s'&'&amp;'g;
    $s =~ s'<'&lt;'g;
    $s =~ s'>'&gt;'g;
    $s
}

### === the following came out of pia_agent.pl and don't seem to be used.

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


1;
