#This should eventually be a subclass of HTML::Format


$home = $ENV{"HOME"};

unshift(@INC,"/usr/local/bin");
unshift(@INC,"/usr/bin");


$GWtempdir = "/tmp/URLS";

$timeout = undef;
#$UNJPEG = "/color/bin/unjpeg -R ";
#$UJPEG = "djpeg ";
$JPEG2PNM = "djpeg ";
$GIFTOPNM = "giftopnm ";
$DEFAULTIMG = "/tmp/printer/wflogo.ps";#In case an image cant be found
     $GWfile = "URLGOT";
     $GWF ="$GWtempdir/$GWfile";

$latexheading = "\\batchmode\\pagestyle{myheadings}\\setlength{\\parskip4ex plus1ex minus1ex} \\setlength{\\parindent0em} \\addtolength{\\textwidth}{3.5cm}\\addtolength{\\textheight}{4.2cm}\\addtolength{\\topmargin}{-1.5cm}\\addtolength{\\headsep}{0.3cm}\\setlength{\\oddsidemargin}{-.75cm}\\setlength{\\evensidemargin}{-.75cm}";
$latexparams = "\\\\addtolength{\\\\baselineskip}{.75mm}\\\\footnotetext{$newurl}\\\\fboxrule 2.5pt ";
$header = "Generated By WebPrinter";
$ENV{'PATH'} = "$ENV{'PATH'}:/usr/local/bin";


sub html2latex{
    my $page=shift;
    my $psfile=shift;
    my $base_url=shift;
    my $docId=shift;

system("rm -f $GWtempdir/*");


     $imagenum = 1;		# 
     @hrefs = split(/<IMG/i,$page);
    $n = $[;
    my $agent=new LWP::UserAgent;
    while (++$n <= $#hrefs) {
	 # parse and get absolute url
	$hrefs[$n] =~ m|src\s*=\s*"?([^">]*)|i ;
my $url = new URI::URL ($1,$base_url);
        my $request=new HTTP::Request('GET',$url);
#  ($protocol, $host, $port, $rest1, $rest2, $rest3) = &url'parse_url($1);
#  $page = &http'get($host,$port,$rest1,$version);
				# now open the image file
     $isjpgfile = ($1 =~ /\.jpg$|\.jpeg$/i);
      $imagefile = "$GWtempdir/htmlIMG.$imagenum.gif";
      $imagefileps = "$GWtempdir/htmlIMG.$imagenum.ps"; # 

    $response=$agent->request($request,$imagefile);
#print $response->as_string;
#      open(IMAGEF,">$imagefile");				# 
#      print IMAGEF $page ;
#      close(IMAGEF);
				# Set scale so size appears similar to screen
     if($isjpgfile) {

      system("unset noclobber ;$JPEG2PNM < $imagefile  >$imagefile.pnm");
  } else { #default for gifs

      system("unset noclobber ;$GIFTOPNM $imagefile >$imagefile.pnm");
  }
$size = `pnmfile $imagefile.pnm`;
$size =~ /(\d*) by (\d*)/;
$w = $1;
if(($w/72) > 7) {
    $scale = (7 * 72) / $w;
} else {
    $scale = 1;
}
      $statimg = system("unset noclobber ;pnmtops -noturn -scale $scale $imagefile.pnm > $imagefileps");
     if($statimg) {
	 system("cp $DEFAULTIMG $imagefileps");
     }
      $imagenum++;
  }				# Now latexify it
				# Add in doc id if we have one
if($docId){
    $idCode = "-h \"$latexparams\\\\newsavebox{\\\\ebox}\\\\sbox{\\\\ebox}{\\\\encode{$docId}{Ricoh CRC WebDoc$docId}}\\\\markright{\\\\fbox{\\\\hspace{1cm}\\\\usebox{\\\\ebox} $header}}\\\\vspace*{-2.25cm}\\\\fbox{\\\\hspace{1cm}\\\\usebox{\\\\ebox} $header}\\\\par\\\\vspace{1.75cm} \" ";
} else {
    $idCode = "-h \"$latexparams\\\\markright{\\\\fbox{$header}}\\\\vspace*{-2.25cm}\\\\fbox{$header}\\\\par\\\\vspace{1.75cm} \" ";
}
open(MYFILE,">$GWF");
print MYFILE $page;
close MYFILE;


system("unset noclobber ; cd $GWtempdir ; html2latex -o '[psfig]{article} $latexheading' $idCode $GWfile ; echo q | latex $GWfile ; dvips -o $psfile $GWfile");

}
1;
