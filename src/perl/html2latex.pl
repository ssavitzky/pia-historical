#This should eventually be a subclass of HTML::Format


$home = $ENV{"HOME"};

unshift(@INC,"/usr/local/bin");
unshift(@INC,"/usr/bin");


$timeout = undef;
#$UNJPEG = "/color/bin/unjpeg -R ";
#$UJPEG = "djpeg ";
$JPEG2PNM = "djpeg ";
$GIFTOPNM = "giftopnm ";
$DEFAULTIMG = $PIA_ROOT . "/printer/wflogo.ps";#In case an image cant be found

$latexheading = "\\batchmode\\pagestyle{myheadings}\\setlength{\\parskip4ex plus1ex minus1ex} \\setlength{\\parindent0em} \\addtolength{\\textwidth}{3.5cm}\\addtolength{\\textheight}{4.2cm}\\addtolength{\\topmargin}{-1.5cm}\\addtolength{\\headsep}{0.3cm}\\setlength{\\oddsidemargin}{-.75cm}\\setlength{\\evensidemargin}{-.75cm}";
$latexparams = "\\\\addtolength{\\\\baselineskip}{.75mm}\\\\footnotetext{$newurl}\\\\fboxrule 2.5pt ";
$header = "Generated By WebPrinter";



sub html2latex{
    my $page=shift;
    my $psfile=shift;
    my $base_url=shift;
    my $docId=shift;
    $GWtempdir = shift;

    $GWtempdir = "/tmp/URLS" unless $GWtempdir;
    $GWtempdir =~ s:/$::;
    print "using temporary directory $GWtempdir"  if $main::debugging;
    system("mkdir $GWtempdir") unless -d $GWtempdir;
    $GWfile = "URLGOT";
    $GWF ="$GWtempdir/$GWfile";

    my $spooldir = $psfile;
    $spooldir =~ s:/[^/]*$::;
    system("mkdir $spooldir") unless -d $spooldir;

    system("rm -f $GWtempdir/*");

    my $html; 
    if(ref($page)){
	$html=$page;
    } else {
	$html=IF::Run::parse_html_string($page);
    }
    $imagenum = 1;		# 
#    $ua=new LWP::UserAgent unless ref($ua) eq 'LWP::UserAgent';
    $ua=$main::main_resolver;

    for (@{ $html->extract_links(qw(img)) }) {
	my ($url, $element) = @$_;
	# parse and get absolute url
	my $url = new URI::URL ($url,$base_url);
        my $request=new HTTP::Request('GET',$url);

	$imagefile = "$GWtempdir/htmlIMG.$imagenum.gif";
	$imagefileps = "$GWtempdir/htmlIMG.$imagenum.ps"; # 

#	my $response=$ua->request($request,$imagefile);
	my $response=$ua->simple_request(TRANSACTION->new($request),$imagefile);
	my $image_type=$response->content_type;
	

	system("rm $imagefile.pnm") if -f $imagefile.pnm ; 

				# Set scale so size appears similar to screen
     if($image_type !~ m:image/(gif|jpeg):) {
	 #failed to get image
## this done later	 system("cp $DEFAULTIMG $imagefile.pnm");
     } else {
	 $image_type=$1;
	 if($image_type=~/jpeg/){
	     system("unset noclobber ;$JPEG2PNM < $imagefile  >$imagefile.pnm");
	 } else { #default for gifs
	     system("unset noclobber ;$GIFTOPNM $imagefile >$imagefile.pnm");
	 }
     }
	$size = `pnmfile $imagefile.pnm`;
	$size =~ /(\d*) by (\d*)/;
	$w = $1;
	if(($w/72) > 7) {
	    $scale = (7 * 72) / $w;
	} else {
	    $scale = 1;
	}
#setwidth height here
	$statimg = system("unset noclobber ;pnmtops -noturn -scale $scale $imagefile.pnm > $imagefileps");
	if($statimg) {
	    system("cp $DEFAULTIMG $imagefileps");
	}
	$element->attr('psfile',$imagefileps) unless $statimg;
	my $width=$element->attr('width');
	$width=$1 unless $width;
	my $height=$element->attr('height');
	$height=$2 unless $height;
	$element->attr('width',$width);
	$element->attr('height',$height);
	$imagenum++;
    }				# Now latexify it
				# Add in doc id if we have one

    my $f=HTML::FormatLatex->new;
    my $latex=$f->format($html);

    open(MYFILE,">$GWF");
    print MYFILE $latex;
    close MYFILE;


    print("unset noclobber ; cd $GWtempdir ; echo q | latex $GWfile ; dvips -o $psfile $GWfile \n")  if $main::debugging;
##twice to get references right
    system("unset noclobber ; cd $GWtempdir ; echo q | latex $GWfile ; echo q | latex $GWfile ; dvips -o $psfile $GWfile");
    
}


1;
