
## routines for creating icons from html
##  uses GD package


package PIA::Agent::graphics;

use PIA::Agent;
push(@ISA,PIA::Agent);

use GD;

sub make_icon{
    my($self,$transaction,$width,$height)=@_;
    $width=$self->option('width') unless $width;
    $height=$self->option('height') unless $height;

    my $type=$transaction->content_type;

    return $self->make_icon_html($transaction,$width,$height) if $type eq 'text/html';
    return $self->make_icon_gif($transaction,$width,$height) if $type eq 'image/gif';
    return $self->make_icon_unknown($transaction,$width,$height);
}
sub make_icon_unknown{
    my($self,$transaction,$width,$height,$error_message)=@_;
    local @position=(0,0);
    # create a new image
    local    $im = new GD::Image($width,$height);
        # allocate some colors
    local    $white = $im->colorAllocate(255,255,255);
    local    $black = $im->colorAllocate(0,0,0);       
    local    $red = $im->colorAllocate(255,0,0);      
    local    $blue = $im->colorAllocate(0,0,255);
        # make the background transparent and interlaced
        $im->transparent($white);
	$im->string(gdTinyFont,$position[0],$position[1],"ERROR",$black);
	$im->string(gdTinyFont,$position[0],$position[30],"unkown type",$black);
	$im->string(gdTinyFont,$position[0],$position[50],$error_message,$black);
    return $im->gif;
    
}

sub make_icon_html{
    local($self,$transaction,$width,$height)=@_;
    local @position=(0,0);
    
    local $reduction=$self->option('reduction'); 
    $reduction= 3 unless $reduction;
    # create a new image
    local    $im = new GD::Image($width,$height);
        # allocate some colors
    local    $white = $im->colorAllocate(255,255,255);
    local    $black = $im->colorAllocate(0,0,0);       
    local    $red = $im->colorAllocate(255,0,0);      
    local    $blue = $im->colorAllocate(0,0,255);
        # make the background transparent and interlaced
        $im->transparent($white);


#       print $im->gif;


    my $html= IF::Run::parse_html_string($transaction->content);

    local $draw=sub {
	my $element=shift;
	$im->string(gdTinyFont,$position[0],$position[1],$element->as_string('contentonly'),$black);
	$position[1]+=gdTinyFont->height + 2;
	return 1;
    };
    local $add_image=sub {
	my $element=shift;
	my $url=$element->attr('src');
	return unless $url;
	$url=URI::URL->new($url,$transaction->request->url);
	print "url is $url \n"  if $main::debugging;
	my $newrequest=$self->create_request('GET',$url);
#	my $newresponse=$self->retrieve($newrequest,"/tmp/graphics_agent.gif");
	my $newresponse=$main::resolver->simple_request(PIA::Transaction->new($newrequest),"/tmp/graphics_agent.gif");
	return 1 unless ($newresponse->code eq '200' && -e "/tmp/graphics_agent.gif");
	open(IMAGE,"</tmp/graphics_agent.gif");
	my $image=GD::Image->newFromGif(IMAGE);
	close IMAGE;
	return 1 unless $image;
	print "gotimage $url\n" if $main::debugging;
	my (@size)=($image->getBounds);
	my @newsize;
	$newsize[0]=$size[0] / $reduction;
	$newsize[1]=$size[1] / $reduction;
	$newsize[0]=$width if $newsize[0] > $width;
	$newsize[1]=$height-$position[1] if $newsize[1] > ($height - $position[1]);
	$im->copyResized($image,$position[0],$position[1],0,0,$newsize[0],$newsize[1],$size[0],$size[1]);
	$position[1]+=$newsize[1];
	return 1;
    };
    local %wanttype;
    $wanttype{'h1'}=$draw;
    $wanttype{'h2'}=$draw;
    $wanttype{'title'}=$draw;
    $wanttype{'h3'}=$draw;
    $wanttype{'img'}=$add_image;
# use traversal to draw icon 
    $html->traverse(
	sub {
	    my($element, $start, $depth) = @_;
	    return 1 unless $start;
	    my $tag = $element->{'_tag'};
	    return 1 unless exists $wanttype{$tag};
	    &{$wanttype{$tag}}($element);
	    return $position[1] < $height;
	}, 'ignoretext');


#     for (@{ $html->extract_links(qw(title h1 h2 h3 h4 img)) }) {
# 	my ($url, $element) = @$_;
# #    for $text (@links){
# 	print "URL $url tag " . $element->tag . "\n";
# ##loopover interesting tags, title, image, headers
# #	$im->string(gdTinyFont,$position[0],$position[1],$element->as_string('contentOnly'),$black);
	
#     }
    $im->rectangle(0,0,$width-1,$height-1,$black);
    return $im->gif;
    
}

##caution this tries to read the file directly... if content already in
# string this will not work

sub make_icon_gif{
    my($self,$transaction,$width,$height,$factor)=@_;
    $factor=1 unless $factor; #reduction factor, with,heightaremax
    my $stream=$transaction->from_machine;
    return unless $stream;
    $stream=$stream->stream;
    return unless $stream;
    my $original=newFromGif GD::Image($stream);
    return $original;
    ##need to make copy
}

sub machine_callback{
    my($self,$newresponse)=@_;
    print "\nProcessing called back for thumbnail\n";
#    my $newresponse=$self->retrieve($newrequest);
    my $request=$newresponse->request;
    my $destination=$$request{_thumbnail_requestor}; #hack for now
    my $original=$$request{_thumbnail_request}; #hack for now
    my $image=$self->make_icon($newresponse);
#    if(! ref($image) ){
	#my $url=$request->url if ref($request);
	#$url=$url->as_string if ref($url);
#	$image=$self->make_icon_unknown($newresponse,$self->option(width),$self->option(height),$newresponse->code);	
#    }
	
    my $response;
#    if(ref($image)){
	$response=HTTP::Response->new(&HTTP::Status::RC_OK,"OK"); 
	$response->content_length(length($image));
	$response->content_type('image/gif');
	$response->content($image);
    $response->header($self->version);
	$response->request($request);
        ## === have to do all the above *before* the following:
	$response=PIA::Transaction->new($response,$self->machine,$destination);
# #    } else {
# 	$response=HTTP::Response->new(&HTTP::Status::RC_INTERNAL_SERVER_ERROR,"giffailed"); 
	
# 	$response=TRANSACTION->new($response,$self->machine,$destination);
# 	$response->request($request);
#     }

    $main::resolver->push($response);
    
    return $response;
    
}

sub respond {
    my($self, $request, $resolver)=@_;
    my %hash=%{$request->parameters};
    my $url = $request->url;

    return $self->respond_to_interform($request, $url, $resolver) 
	unless exists $hash{'url_to_iconify'};

    my $newrequest=$self->create_request('GET',$hash{'url_to_iconify'});
    my $machine=$self->machine;
    $machine->callback(\&machine_callback);
    $$newrequest{_thumbnail_requestor}=$request->from_machine;
    $$newrequest{_thumbnail_request}=$request;
    $newrequest=PIA::Transaction->new($newrequest,$machine);
    return $newrequest;
}

1;
