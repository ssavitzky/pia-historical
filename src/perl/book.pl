###	$Id$

##############################################################################
 # The contents of this file are subject to the Ricoh Source Code Public
 # License Version 1.0 (the "License"); you may not use this file except in
 # compliance with the License.  You may obtain a copy of the License at
 # http://www.risource.org/RPL
 #
 # Software distributed under the License is distributed on an "AS IS" basis,
 # WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 # for the specific language governing rights and limitations under the
 # License.
 #
 # This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 # created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 # Rights Reserved.
 #
 # Contributor(s):
 #
############################################################################## 



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
    $$self{page}=1;
    $$self{_base}=$base;
    $$self{_references}={};
    $depth=2 unless $depth;
    $hints={} unless $hints;
    $chapters=[]; #sub books
    $$self{_max_depth}=$depth;
    $$self{_depth}={};
    $$self{_hints}=$hints;
    my $max_pages=$$hints{max_pages};
    $max_pages=20 unless $max_pages;
    $$self{_max_pages}=$max_pages;
    print "max pages " . $max_pages;
    local $machine=PIA::Agent::Machine->new($self);
    $machine->callback(\&bookmaker_callback);

    $$self{_machine}=$machine;
    $self->add_link($base,1) if $base;
}

sub filename{
    my($self,$file)=@_;
    $$self{_file} = $file;
}

sub save_to_file{
    my $self=shift;
    return unless $$self{_file};
    open(FILE,">$$self{_file}");

    print FILE $$self{page} . " " . $$self{_base} . "\n";
    print FILE join(" ",keys(%{$$self{_references}}));
    print FILE "\n";
    print FILE join(" ",values(%{$$self{_references}}));
    print FILE "\n";
    select(FILE);
    my $toc=$$self{toc};
    for(keys(%{$toc})){
	print $_ . " ";
	$self->print_page($$toc{$_});
	print "\n";
    }
    select(STDOUT);
    close(FILE);

}


sub read_from_file{
    my $self=shift;
    return unless $$self{_file};
    open(FILE,"<$$self{_file}");

    ($$self{page},$$self{_base}) = split(" ",<FILE>);
    my(@keys)=split(" ",<FILE>);
    my(@values)=split(" ",<FILE>);
    my $refs=$$self{_references};
    for(@keys){
	$$refs{$_}=shift(@values);
    }
    my $toc=$$self{toc};
    while(<FILE>){
	my($k,@rest)=split(" ");
	my $page={};
	$$toc{$k}=$page;
	while($k=shift(@rest)){
	    last if $k eq 'links';
	    $$page{$k}=shift(@rest);
	}
	if($k eq 'links') {
	    $$page{$k}=\@rest; #Does rest get a new value above..?
	}
    }
    close(FILE);

}

sub print_page{
    my $self=shift;
    my $page=shift;
    for(keys(%{$page})){
	next if $_ eq 'links';
	next if $_ eq 'html';
	print $_ ." " . $$page{$_} ." " ;
    }
    if(exists($$page{links})){
	print 'links ';
	for(keys(%{$$page{links}})){
	    print $_ . " ";
	}
    }
}

sub base{
    my($self,$url)=@_;
    $$self{_base}=$url if $url;
    return $$self{_base};
    
}
sub bookmaker_callback{
    my($self,$response)=@_;
    print "bookmaker code" .  $response->code . "\n"  if $main::debugging;
    return unless $response->code eq '200';
#    print $response->content;
    my $type=$response->content_type;
    my $html;
    if($type=~ /text\/html/){
	$html= IF::Run::parse_html_string($response->content);
    } elsif($type=~ /text/){ 
	$html=IF::IT->new ('pre', $response->content);
    } else {
	return 0; #for now only html
	print "$type is not html\n";
    }
    my $old_request=$response->response_to;
    my $reference=$$old_request{'_book_reference'};
    my $reference_url=$old_request->url;
    $reference=$self->reference($reference_url) unless $reference;
    my $new_page=$self->add_page($html,$reference_url,$reference);
    my $cache;
#caching disabled for now    my $cache=$response->header('Cache-Location');
    $$new_page{cache}=$cache if $cache;
    # don't keep html if it is in cache
    my $hints = $$self{_hints};
    delete $$new_page{html} if $$hints{cache_only} && $cache;
    my $old_depth;
    $old_depth=$$old_request{_book_depth} if $old_request;
    print "requestthinksitis $old_depth \n" if $main::debugging;
    $old_depth=$self->depth($reference) unless $old_depth;
    $old_depth=$$self{_max_depth} unless $old_depth;
    my @new_keys;
    my $new_key,$new;
    print "depth is $old_depth, max is " . $$self{_max_depth} . "\n" if $main::debugging;
    print "max pages exceeded " . $$self{page} . "\n" if($$self{page}>$$self{_max_pages});
    return 1 if($$self{page}>$$self{_max_pages});
    if($old_depth < $$self{_max_depth}){
	local @link_elements;
			#get links
	$html->traverse(sub{
	    my ($tag,$start)=@_;
	    return unless $start;
	    push(@link_elements,$tag) if $tag->tag  eq 'a';
	    return 1;
	},1);
	#for (@{ $html->extract_links(qw(a)) }) {
	for (@link_elements){
	    my ($urltext, $element);
	    $element = $_;
	    $urltext=$element->attr('href') if ref($element);
	    next unless $urltext;
	    my $url=URI::URL->new($urltext,$response->url);
#	    print "new: " . $url->abs->as_string . " base: " . $response->url->as_string ." \n";
	    $url=$url->abs;
	    if($url->host && $url->scheme eq 'http'){
		($new_key,$new)=$self->add_link($url,$old_depth+1) ;
		push(@new_keys,$new_key) if $new;
		$element->attr('book_reference',$new_key);
	    }
	}
	$$new_page{links}=\@new_keys;
    }
    return 1;
}
sub depth{
    my($self,$key,$depth)=@_;
    my $hash=$$self{_depth};
    $$hash{$key}=$depth if $depth;
    return $$hash{$key};
}

sub add_link{
    my($self,$link,$depth)=@_;
    my $links=$$self{_links};
    my $machine=$$self{_machine};
print "getting page $link for book\n" if $main::debugging;    
    my $references=$$self{_references};
    my $isnew= ! exists $$references{$link};
    my $key=$self->reference($link);
    return ($key,$isnew) unless $isnew;
    $self->depth($key,$depth);    
    push(@{$links},$key);

    my $req=new HTTP::Request('GET',$link);
    my $request=PIA::Transaction->new($req,$machine);
    $$request{_book_depth}=$depth;
    $$request{_book_reference}=$key;
    $main::resolver->push($request);
    return ($key,$isnew);
    
}

sub add_page{
    my($self,$html,$url,$reference)=@_;
    my %page;
    return unless ref($html) eq 'IF::IT';
#    print($html->as_string);
    $reference=$self->reference($url) unless $reference;    
    $page{html}=$html;
    local $reference_key=$reference;
    
    my $contents=$$self{toc};
    local $element_title;
    $html->traverse(
		    sub {
			my($element, $start, $depth) = @_;
			return 1 unless $start;
			my $tag = $element->{'_tag'};
			if($tag eq 'title'){
			    $element_title=$element->content_string;
			    $element->attr('book_reference',$reference_key);
			    
			    return;
			}
			return 1;
		    }, 'ignoretext');
$element_title="unknown" unless $element_title;

$page{title}=$element_title;
$page{reference}=$reference;
$page{url}=$url;
$$contents{$reference}=\%page;
print "added page " . $$page{title} . "key: $reference \n";
return \%page;
    
}

sub reference{
    my($self,$url,$value)=@_;
    $url=$url->as_string if ref $url;
    my $hash=$$self{_references};
    if (! exists $$hash{$url}){
	$value=$$self{page}++ unless $value;
	$$hash{$url}=$value;
    }
    return $$hash{$url};
}
sub  table_of_contents{
    $self=shift;
    my $contents=$$self{toc};
    my (@keys)=keys(%$contents);
#    my $page=$$contents{'0'};
    my $page=$$contents{1};
    print "page keys " . join(" ",keys(%$page)) . " end keys\n" if $main::debugging;
    my $element=IF::IT->new('p');
    $element->push($self->page_title($page));
    $element->push($self->page_links($page));
    return $element;
    
}
sub page{
    my($self,$argument)=@_;
    my $contents=$$self{toc};
    return $$contents{$argument};
}

sub page_title{
    my($self,$page)=@_;
    my $element=IF::IT->new('a');
    $element->attr('href',$$page{url});
    $element->push($$page{title});
    return $element;
}

sub page_links{
    my($self,$page)=@_;
    my $list=IF::IT->new('ol');
    my $item;
    my $flag;
    my @keys=@{$$page{links}};
    print join(" ",@keys) . "keys for links\n" if $main::debugging;
    for(@keys){
	my $newpage=$self->page($_);
	if($newpage){
	    $flag=1;
	    $item=IF::IT->new('li');
	    $item->push($self->page_title($newpage));
	    $list->push($item);
	    my $subpages=$self->page_links($newpage);
	    $list->push($subpages) if $subpages;
	}
    }
    return $list if $flag;
    return;
}

sub  table_of_contents_table{
    my($self)=@_;
    my $contents=$$self{toc};
    my $element=IF::IT->new('table');
    my $temp,$data;
    for (keys(%{$contents})){
	$temp=IF::IT->new('tr');
	$element->push($temp);
	$data=IF::IT->new('td', 'align' => 'left');
	$data->push($_);
	$temp->push($data);
	$data=IF::IT->new('td', 'align' => 'right');
	$data->push($$contents{$_});
	$temp->push($data);
    }
    return $element;
    
}

sub html{
    local($self)=@_;
##order matters
    my $string;
    my $html=IF::IT->new('book');
my $page;
    my $counter=0;
    my $reference_key;
    print "page total is " . $$self{page} ."\n";
    while($counter<$$self{page}){
	$page=$self->page($counter);
	print "generate html for $counter \n";
	if ($page){
	    $refrenece_key=$$page{reference};
	    $base_url=$$page{url};
	    if($$page{html}){
		$html->push($$page{html}) ;
	    } elsif( $$page{cache}) {
		my $tmphtml= IF::Run::parse_html_file($$page{cache} . "/.content") if $$page{cache};
##add in references
		$tmphtml->traverse(
				   sub {
				       my($element, $start, $depth) = @_;
			return 1 unless $start;
			my $tag = $element->{'_tag'};
			if($tag eq 'title'){
			    $element_title=$element->content_string;
			    $element->attr('book_reference',$reference_key);
			    
			    return 1;
			}
			if($tag eq 'a' && $tag->attr('href')){
			    my $url=$tag->attr('href');
			    my $url=URI::URL->new($url,$base_url);
			    $url=$url->abs;
			    my $refkey=$self->reference($url);
			    $tag->attr('book_reference',$refkey);
			}
			return 1;
		    }, 'ignoretext');
		

		$html->push($tmphtml);
	    }
	   
	}

	$counter++;
    }
    return $html;
    
}

1;
