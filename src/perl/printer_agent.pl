

#Agent for handling printing requests
#this eventually will be a model of printer...

##making books
require "make_book.pl";
require "book.pl";
require "formatLatex.pm";
require "html2latex.pl";


#Two main sections
# first deals with retrieving and converting URL's to postscript & previews
#second deals with webfax

##hack to find device
$GS = "gs";
my $dev=`$GS -h`;
$GIFEXT="gif";
if ($dev =~ /gif8/) {
    $GSDEVICE="gif8";
#    $GS2GIF="";
    $GSEXT=$GIFEXT;
} else {
    $GSDEVICE="ppm";
    $GS2GIF=" | ppmquant 256 | ppmtogif ";
    $GSEXT="ppm";
}

#where the temporary files live

### setup of filenames and DOFS agent done in initialize.if
#$printer_root_directory=  "/tmp/printer/";
#$ps_file="/tmp/printer/preview.ps";
#$image_URL="file:/tmp/printer/preview";
#$ps_URL="file:/tmp/printer/preview.ps";


sub file_names{
    $self=shift;


    my $num=$self->option('preview_number');
    my $image_file="preview$num";
    my $string = "$printer_root_directory$image_file";

## also set ps_file
    $ps_file="$printer_root_directory$image_file.ps";
    $image_URL="$printer_base_url";
    $ps_URL="$printer_base_url$image_file.ps";
    $num+=1;
    $self->option('preview_number',$num);
    return ($string,$ps_file,$image_URL,$ps_URL);

    #return $string;
}
sub ps_file_name{
    return $ps_file;
}

sub html_latex_ps{

    my $self=shift;
    my $response=shift;
    my $request=shift;
    my $docId=shift;
    my $directory=$self->option('tempdirectory');
    $directory=$self->agent_directory . "/temp" unless $directory;
    
    my $status=html2latex($response->content,$ps_file,$request->url,$docId,$directory);
    print "latex status is $status \n" if $main::debugging;
}

sub html_ps{
    my $self=shift;
    
    my $response=shift;

    open(PSFILE,">$ps_file");
#    my $html = HTML::Parse::parse_html($response->content);
    my $html = IF::Run::parse_html_string($response->content);
    require HTML::Parse;
    require("Format_PS.pm");
    my $f = HTML::Format_PS->new;
    print PSFILE $f->format($html);
    close PSFILE;
#    $html->delete;		# still uses HTML::Element
}

sub create_postscript{
    my $self=shift;
    
    my $request=shift;
    my $docId=shift;

    my $ua = $self->user_agent;
    my $response=$ua->simple_request($request); 

    my $render=$self->option("render_method");
    $docId = "010" unless $docId;

    if($render eq "latex"){
	html_latex_ps($self,$response,$request,$docId);
    }else{
	html_ps($self,$response,$request,$docId);
    }
return $response;    
    
}

sub create_preview{
    my $self=shift;
    my $request=shift;
    my @files=file_names($self);
    my $image_file=shift @files;
    $ps_file=shift @files;
    
    my $response=create_postscript($self,$request);


    my $thumbsize=$self->option('thumbsize');
    my $thumb_width=$self->option('thumb_width');
    my $thumb_height=$self->option('thumb_height');
#    my $cmd="cat /dev/null | gs -sOutputFile=$image_file -sDEVICE=gif8 -r72 -dNOPAUSE -q $ps_file";
#    my $cmd="cat /dev/null | gs -sOutputFile=- -sDEVICE=ppm -r$thumbsize -dNOPAUSE -q $ps_file |ppmquant 256 | ppmtogif > $image_file";
#broken if gs not support gif....


    my $cmd="rm -f $image_file.*.$GSEXT $image_file.*.$GIFEXT; cat /dev/null | $GS -sOutputFile=$image_file.%d.$GSEXT -sDEVICE=$GSDEVICE -r$thumbsize -dNOPAUSE -q $ps_file ";
#    $cmd.="$GS2GIF";
#    $cmd.=" > $image_file";  Multipage files cause us some pain
    

#    print $cmd;
    print $cmd  if $main::debugging;
    
    my $status=system ($cmd);
    #shouldgetstatushere & check for multiple pages...put %d in output filename
    print "Status is $status\n" if $main::debugging;
    local (@image_files)=glob "$image_file.*.$GSEXT";
    if($GS2GIF) {
#need step to convert to gif
	foreach $image_url (@image_files) {
	    my $file = $image_url;
	    $image_url =~ s/$GSEXT$/$GIFEXT/;
	    $status=system("cat $file $GS2GIF > $image_url");
	}
    }

    print "made $#image_files from $image_file.*.gif" . @image_files . "..\n" 
	if $main::debugging;

# === the following fails; apparently $request->url isn't a reference: (steve)
#    my $image_url = $request->url->as_string;
    my $element=IF::IT->new('p');
#    $element->attr( 'href',$image_url);
#   x my $img_url="file:$image_file";
    foreach $image_url (@image_files) {
	$image_url=~/\/([^\/]*)$/;
	my $img_url=$image_URL . $1;
	my $anchor=IF::IT->new('a', href => $img_url);
	my $particle=IF::IT->new('img', src => $img_url );
	$particle->attr('width',$thumb_width);
	$particle->attr('height',$thumb_height);
	$anchor->push_content($particle);
	$element->push_content($anchor);
    }

#This returns the postscript    return $response;
    return $element;#an html element which is linked to preview image
}


sub generate_preview_element{

    my($self,$image_file,$ps_file)=@_;
    
    my $thumbsize=$self->option('thumbsize');
    my $thumb_width=$self->option('thumb_width');
    my $thumb_height=$self->option('thumb_height');
#    my $cmd="cat /dev/null | gs -sOutputFile=$image_file -sDEVICE=gif8 -r72 -dNOPAUSE -q $ps_file";
#    my $cmd="cat /dev/null | gs -sOutputFile=- -sDEVICE=ppm -r$thumbsize -dNOPAUSE -q $ps_file |ppmquant 256 | ppmtogif > $image_file";
#broken if gs not support gif....


    my $cmd="rm -f $image_file.*.$GSEXT $image_file.*.$GIFEXT; cat /dev/null | $GS -sOutputFile=$image_file.%d.$GSEXT -sDEVICE=$GSDEVICE -r$thumbsize -dNOPAUSE -q $ps_file ";
#    $cmd.="$GS2GIF";
#    $cmd.=" > $image_file";  Multipage files cause us some pain
    

#    print $cmd;
    print $cmd  if $main::debugging;
    
    my $status=system ($cmd);
    #shouldgetstatushere & check for multiple pages...put %d in output filename
    print "Status is $status\n" if $main::debugging;
    local (@image_files)=glob "$image_file.*.$GSEXT";
    if($GS2GIF) {
#need step to convert to gif
	foreach $image_url (@image_files) {
	    my $file = $image_url;
	    $image_url =~ s/$GSEXT$/$GIFEXT/;
	    $status=system("cat $file $GS2GIF > $image_url");
	}
    }

    print "made $#image_files from $image_file.*.gif" . @image_files . "..\n" 
	if $main::debugging;

# === the following fails; apparently $request->url isn't a reference: (steve)
#    my $image_url = $request->url->as_string;
    my $element=IF::IT->new('p');
#    $element->attr( 'href',$image_url);
#   x my $img_url="file:$image_file";
    foreach $image_url (@image_files) {
	$image_url=~/\/([^\/]*)$/;
	my $img_url=$image_URL . $1;
	my $anchor=IF::IT->new('a', href => $img_url);
	my $particle=IF::IT->new('img', src => $img_url );
	$particle->attr('width',$thumb_width);
	$particle->attr('height',$thumb_height);
	$anchor->push_content($particle);
	$element->push_content($anchor);
    }

#This returns the postscript    return $response;
    return $element;#an html element which is linked to preview image
}


##############################################################################
##############################################################################
##############################################################################
###fax utils
sub next_doc_Id{
    my $agent =shift;
    my $docDir=$agent->option(root);
    my $docBase=1010;
    while(-e "$docDir/$docBase") {
#    print $docBase;
	$docBase++;
	if(($docBase % 10) > $maxdigit) {
	    $docBase = (1 + int($docBase / 10)) * 10;
	}
#    print $docBase;
	if(int(($docBase % 100) / 10) > $maxdigit) {
	    $docBase = (1 + int($docBase / 100)) * 100;
	}
#    print $docBase;
    }
    return $docBase;
}


#############################################################
#############################################################
#huge hack for interpreting digital paper
sub proc_tiff{
##Should return list of number and URLs to get
    $filename = shift;
    my $agent=shift;
    my $dir;
    chop($dir =`pwd`);
    my $root=$agent->option(root);
    chdir $root;

    $faxlog = "faxlog";
    $tmp = "tmp";
    $tmpcode = "tmpcode";
    $allcodes = "tmpallcodes";
    $defaulturl = "http://internal.crc.ricoh.com/people/wolff/defaultfax.html";
    $length = 1055;
    $width = 1728;			  
    $status = system("tiffinfo $filename > $tmp");
    open(TMP,"<$tmp");
    while(<TMP>) {
	if(/Image Width: (\d*) Image Length: (\d*)/) {
	    $width = $1;
	    $length = $2 - 1;
	}}
    close(TMP);
    chop($pd = `pwd`);	  
    $host = `hostname`;
    $status = system("/usr/local/bin/faxinfo $filename > $tmp");
    
    open(TMP,"<$tmp");
    while(<TMP>) {
	if(/Sender: (.*)/){
	    $sender = $1;
	    $sender =~ s/^001/\+/;
	    print "Sender is $sender\n" if $main::debugging;
#replace country code with +
	    
	}}
    close(TMP);
    
    
    $status = system("./tiff.to.pbm.pl incoming.fax");
    
#system("/usr/local/bin/tifftopnm $filename | /usr/local/bin/pnmcut 0 0	 $width $length > $filename.pbm");
##stupid fax machines creates tiffs with size 1 row too big
    
#$status = system(" pnmcrop $filename.pbm | pnmmargin -white 1 | ./pbmfill >$filename.tmp.pbm ");
    
    $status = system( "/bin/cat $filename.tmp.pbm | ./bdecode | ./procCode.pl > $filename.urls");
    
    my @urls;
    
###########################################################
###What follows is a temp hack to try to be more robust in finding names
    $numtosend=0;
    open(TMP,"<$filename.urls");
    while(<TMP>) {
	$numtosend++;
    }
    close(TMP);
    if(!numtosend) {
	$status = system( "cat $filename.tmp.pbm |./bdecode >> $tmpcode");
	$status = system(" pnmflip -r180 $filename.tmp.pbm |./bdecode >> $tmpcode");
	$status = system(" cat $tmpcode | ./procCode.pl >> $filename.urls");
    }
    
    open(TMP,"<$filename.urls");
    while(<TMP>) {
	$numtosend++;
    }
    close(TMP);
    if(!numtosend) {
	$status = system(" cat $filename.pbm | ./bdecode > $allcodes");
#    $status = system(" guessCode.pl $allcodes $tmpcode >> $filename.urls");
	$status = system(" ./guessCode.pl < $allcodes  >> $filename.urls");
	
    }
    
#####################################################################
    
    
    $numsent = 0;
    open(LOG,">>$faxlog");
    if($sender){
	push(@urls,$sender);
	open(TMP,"<$filename.urls");
	while(<TMP>) {
	    if ($SENTFAX[$_]) {
		#do nothing as we have already sent this one
	    } else {		
		#actually send the fax
		#protect any meta characters
#	    s/(\||\?|\&|\@|\#|\$|\^|\!|\*|\(|\)|\\)/\\$1/g;
#	    system("$faxcmd  $sender \"$_\" ");
		push(@urls,$_);
		print(LOG "sent $_ to $sender\n");
		$numsent++;
	    }
	}
	close(TMP);
	if($numsent == 0) {
#	system("$faxcmd  $sender \"$defaulturl\"");
	    push(@urls,$defaulturl);
	    print(LOG "sent $_ to $sender\n");
	    $numsent++;
	}
    } else {
	
	system("echo No sender for fax $filename | mail wolff ");
	print(LOG "echo No sender for fax $filename ");
    }
    close(LOG);
    
    
    chdir $dir;
    return @urls;
    
}		    

1;


