package PIA::Utilities; ###### Utilities for the PIA
###	$Id$
###	Copyright 1997, Ricoh California Research Center.
###

use Exporter;
@ISA = qw(Exporter);

@EXPORT = qw(writeTo appendTo readFrom protect_markup make_form make_list
	     );


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
### Conversion Utilities:
###

sub protect_markup {
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
