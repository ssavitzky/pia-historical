

#Agent for handling printing requests
#this eventually will be a model of printer...
require HTML::Parse;
require HTML::FormatPS;



#Two main sections
# first deals with interforms for printer
# second deals with retrieving and converting URL's to postscript & previews

#where the interforms live
$printer_root_directory=  "/home/wolff/www/printer/";

#execute interforms-- should be moved to own class and return code to be executed in this context

#for interforms we use global variables to access state
 # options are persistent
 # form parameters hold hash from put request
$options{"render_method"}="latex";
$options{"preview"}=0;  #should we make ps preview?
$options{"default_printer"}="ps3";


$ps_file="/home/wolff/www/printer/preview.ps";
$options{"ps_file"}=$ps_file;
$image_file="/home/wolff/www/printer/preview.gif";
$image_URL="http://internal.crc.ricoh.com/~wolff/printer/preview.gif";
$ps_URL="http://internal.crc.ricoh.com/~wolff/printer/preview.gif";
$print_URL="http://ps3/Print";


%form_parameters=(); #hold the hash for a put request
$current_request; # hold the current request object

#this is  a callback for html traverse
sub execute_interform{
    my $element=shift;
    my $start=shift;
    return 1 unless $start; #only do it once
    return 1 unless  lc($element->tag) eq "code";

    #getcode
    my $code_status;
    my $language=lc($element->attr('language'));
    if ($language eq 'perl'){
	my $code_array=$element->content;
#       replace with new array
	my @new_elements;

	my $code;
	while($code=shift(@$code_array)){
	    print "execing $code \n";
	    if( ref( $code)){
		$code_status=$code; #this is and html element
	    }else{
	    #evaluate string andreturnlast expression value
		$code_status=eval $code;
	    }
	    push(@new_elements,$code_status) if $code_status;
	}
	
	while($code=shift(@new_elements)){
	    push(@$code_array,$code);
	}
	$element->attr('language',"perl_output");
    } 
#other languages?
    return 0;#do children?
    
}

use HTML::Parse;

sub parse_interform{
    my $file=shift;
    my $html=parse_htmlfile($file);
    
    # execute any perl code
    $html->traverse(\&execute_interform,1);
    return $html;
    
}

sub printer_home_page{
    my $printer=shift;
    my $request=shift;
    my $handler=shift;
    my $parameters=$handler->parse_post($request->content);
    
    $current_request=$request;
    
    %form_parameters=%$parameters;
    $form_parameters{'printer'}=$printer;
    
#Do Real work here...
    my $url=$request->url;
    my $path=$url->path;
    my $string="";
    
    $path="$printer_root_directory$path";
    if(-e $path){
	my $html=parse_interform($path);
	$string=$html->as_HTML;
	print $string;
    } else{
	#do other things
	$string="<body><H1> Printer $printer home page </H1>\n";
	$string.="options andstatus go here";
#    $string.=$request->content;
	if($$parameters{"action"}=~"Print"){
	    $string.="would have printed". $$parameters{'URL'} ."on  printer".  $printer;
    $string.="</body>";
	}
    }


    my $response=HTTP::Response->new(&HTTP::Status::RC_OK, "OK");
    $response->content_type("text/html");    

    $response->content($string);
    return $response;
}


sub preview_form{
    my $embedded=shift;
    my $request=shift;
    my $response=HTTP::Response->new(&HTTP::Status::RC_OK, "OK");
    $response->content_type("text/html");
    my $string="<body><H1> Previewed page </H1>\n";
    $string.='<form method=POST action=http://ps3/preview.if>';
    $string.="You are Previewing <input name=URL size=50 value=";
    $string.=$request->url->as_string;
    $string.=">\n<br>";
    $string.='<input type=submit name=action value="Print">
<input type=submit name=action value="Ghostview">
<input type=submit name=action value="Options">
<input type=submit name=action value="Status">
<input type=submit name=action value="PreviewMode">

</form> <hr>\n';
    $string.=$embedded->content;
    
    $string.="</body>";
    $response->content($string);
    return $response;
}


sub html_latex_ps{

    require "html2latex.pl";
    
    my $response=shift;
    my $request=shift;
    my $docId=shift;
    my $status=html2latex($response->content,$ps_file,$request->url,$docId);
    print "2latex status is $status \n";
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
    
    my $cmd="cat /dev/null | /usr/local/bin/gs -sOutputFile=$image_file -sDEVICE=gif8 -r72 -dNOPAUSE -q $ps_file";
#    print $cmd;
    my $status=system ($cmd);
    #shouldgetstatushere & check for multiple pages...put %d in output filename
    print "Status is $status\n";
    my $image_url = $request->url->as_string;
    
    my $string="<a href=$image_url> <img src=";
    $string.=$image_URL;
    $string.="></a>";
#replace content with image url
#TBD create new response rather than usurp preview
    $response->content($string);
    
    return $response;
}


#this not used...printerhandler does instead...
sub printer_preview_reply{
    my $request=shift;
    

    my $response=create_preview($request)     ;
    return preview_form($response,$request);
    
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
    my $element=new HTML::Element 'form',method => "POST",action => shift;
    my %attributes,$particle;
    my @widgets=@_;

    my $last_hack;
    foreach $widget (@widgets){
	%attributes=%$widget;
	$attributes{tag}='input' unless $attributes{tag};
	$particle=new HTML::Element $attributes{tag};
	delete $attributes{tag};
	$particle->push_content($attributes{text}) if exists $attributes{text};
	delete $attributes{text};
	foreach $key (keys %attributes){
	    $particle->attr($key,$attributes{$key});
	}
	if($particle->tag() eq 'option'){
	    $last_hack->push_content($particle);
	}else{
	    $element->push_content($particle);
	    $last_hack=$particle;
	}
    }
    return $element;
}

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
$status = system("/usr/local/SunOS/bin/tiffinfo $filename > $tmp");
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
	print "Sender is $sender\n";
#replace country code with +

    }}
close(TMP);


$status = system("./tiff.to.pbm.pl incoming.fax");

#system("/usr/local/bin/tifftopnm $filename | /usr/local/bin/pnmcut 0 0  $width $length > $filename.pbm");
##stupid fax machines creates tiffs with size 1 row too big

#$status = system(" /usr/local/bin/pnmcrop $filename.pbm | /usr/local/bin/pnmmargin -white 1 | ./pbmfill >$filename.tmp.pbm ");

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
    $status = system(" /usr/local/bin/pnmflip -r180 $filename.tmp.pbm |./bdecode >> $tmpcode");
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


