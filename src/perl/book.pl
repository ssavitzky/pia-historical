

package BOOK;
sub new{
    my($class,$base,$depth,$hints)=@_;
    $self={};
    bless $self,$class;
    $self->initialize($base,$depth,$hints);
    return $self;
}

sub initialize{
    my($self,$base,$depth,$hints)=@_;
    $$self{_html}=IF::IT->new();  #token list
    $$self{_links}=[];
    $$self{toc}={};
    $$self{page}=0;
    $$self{_base}=$base;
    $depth=2 unless $depth;
    $hints={} unless $hints;
    $$self{_max_depth}=$depth;
    $$self{_depth}=0;
    $$self{_hints}=$hints;
    local $machine=AGENT_MACHINE->new($self);
    $machine->callback(\&bookmaker_callback);

    $$self{_machine}=$machine;
    $self->add_link($base,0)
}


sub base{
    my($self,$url)=@_;
    $$self{_base}=$url if $url;
    return $$self{_base};
    
}
sub bookmaker_callback{
    my($self,$response)=@_;
    return unless $response->code eq '200';
#    print $response->content;
    my $html= IF::Run::parse_html_string($response->content);
    $self->add_page($html);
    my $old_depth=${$response->request}{_book_depth};
    if($old_depth+1 < $$self{_max_depth}){
	for (@{ $html->extract_links(qw(a)) }) {
	    my ($urltext, $element) = @$_;
	    my $url=URI::URL->new($urltext,$response->request->url);
	    $self->add_link($url,$old_depth+1);
	}
    }
    return 1;
}


sub add_link{
    my($self,$link,$depth)=@_;
    my $links=$$self{_links};
    my $machine=$$self{_machine};
print "getting page $link for book\n";    
    push(@{$links},$link);
    	my $req=new HTTP::Request('GET',$link);
	my $request=TRANSACTION->new($req,$machine);
    $$request{_book_depth}=$depth;
	$main::main_resolver->push($request);

    
}

sub add_page{
    my($self,$html)=@_;
    $self->html->push($html);
    my $contents=$$self{toc};
    local $element_title;
    $html->traverse(
		    sub {
			my($element, $start, $depth) = @_;
			return 1 unless $start;
			my $tag = $element->{'_tag'};
			if($tag eq 'title'){
			    $element_title=$element->content_string;
			    return;
			}
			return 1;
		    }, 'ignoretext');
$element_title="unknown" unless $element_title;
$$contents{$element_title}=$$self{page};
	    
    $$self{page}++;
    
}

sub html{
    my($self)=@_;
    return $$self{_html};
    
}

1;
