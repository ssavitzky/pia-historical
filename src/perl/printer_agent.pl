

#Agent for handling printing requests
#this eventually will be a model of printer...
require HTML::Parse;
require HTML::FormatPS;



#Two main sections
# first deals with retrieving and converting URL's to postscript & previews
#second deals with webfax

#where the temporary files live
#$printer_root_directory=  "/home/wolff/www/printer/";
#$ps_file="/home/wolff/www/printer/preview.ps";
#$image_file="/home/wolff/www/printer/preview.gif";
#$image_URL="http://internal.crc.ricoh.com/~wolff/printer/preview.gif";
#$ps_URL="http://internal.crc.ricoh.com/~wolff/printer/preview.gif";

$printer_root_directory=  "/tmp/printer/";
$ps_file="/tmp/printer/preview.ps";
$image_file="/tmp/printer/preview.gif";
$image_URL="file:/tmp/printer/preview.gif";
$ps_URL="file:/tmp/printer/preview.ps";


sub html_latex_ps{

    require "html2latex.pl";
    
    my $response=shift;
    my $request=shift;
    my $docId=shift;
    my $status=html2latex($response->content,$ps_file,$request->url,$docId);
    print "2latex status is $status \n" if $main::debugging;
}

sub html_ps{

    my $response=shift;

    open(PSFILE,">$ps_file");
    my $html = HTML::Parse::parse_html($response->content);
    require HTML::FormatPS;
    my $f = new HTML::FormatPS;
    print PSFILE $f->format($html);
    close PSFILE;
    
}

sub create_postscript{
    my $request=shift;
    my $docId=shift;

    my $ua = new LWP::UserAgent;
    my $response=$ua->simple_request($request); 

    my $render=$current_self->option("render_method");
    

    if($render eq "latex"){
	html_latex_ps($response,$request,$docId);
    }else{
	html_ps($response,$request,$docId);
    }
return $response;    
    
}

sub create_preview{
    my $request=shift;
    my $response=create_postscript($request);
    
#    my $cmd="cat /dev/null | gs -sOutputFile=$image_file -sDEVICE=gif8 -r72 -dNOPAUSE -q $ps_file";
    my $cmd="cat /dev/null | gs -sOutputFile=- -sDEVICE=ppm -r72 -dNOPAUSE -q $ps_file | ppmtogif > $image_file";
#    print $cmd;
    my $status=system ($cmd);
    #shouldgetstatushere & check for multiple pages...put %d in output filename
    print "Status is $status\n" if $main::debugging;
    my $image_url = $request->url->as_string;
    
    my $string="<a href=$image_url> <img src=";
    $string.=$image_URL;
    $string.="></a>";
#replace content with image url
#TBD create new response rather than usurp preview
    $response->content($string);
    
    return $response;
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


