#!/usr/local/bin/perl
#Cleanup output from the decode program

##Really dumb attempt to guess what might have been circled but was not
## properly decoded.  Basically, returns the first URL which does not 
## get found in the document...

$docDir = "./URLS";			# This is where we should look for doc ids to match the ones found in the code
$currentDoc = 0;	# Default
$urlFile = "$docDir/allUrls";	
$guessUrl = "http://thames.crc.ricoh.com/people/wolff/faxguess.html";

#For now, just pipe in allcodes...
#$allcodes = shift;
#$circled = shift;

$MINWIDTH = 5;  #should be at least this many pixels
$MINDOC = 99;  #no doc ids under 3 digits
#Doc ids and URL ids should be maintained seperately (eg. no overlap
#so we can first test for a doc id and then for a URL id 
#Doc ids are just directory names...URL ids are just file names with the URL
#as the only line of the file...

$MAXDOC = 200000;
###Perl treats our ids as numbers instead of characters, so
###it tries to create an array as looooong as the number we 
### are using as a key...thus we need a limit  would be nicer to 
### force it to use a hash table

$mincode = 2000;

###First identifier that we should have found, but did not is sent back...
sub procUrlFile {				# This should be an associative array
    local($urlFile) = @_;
    open(URLS,"<$urlFile");
    while(<URLS>){
	($ucode, $url) = split;
	if(! defined $firsturl) {
	    $firsturl = $url;
	}
	$urls[$ucode] = $url;	# Put something here to limit scope to 1 doc
	if((! defined $CODES[$ucode]) && (($ucode > $mincode) || ($mincode == 2000))){
	    print "$url \n";	# If there is one...
	    print "$guessUrl \n"; # just in case...
	    #now exit
	    exit(0);
	}
    }
}
# No need to do this...&procUrlFile($urlFile);

$numSeq	= 1;
##Added test to make sure we don't try to create humongous array--dig codes are
## thus limited to below this number

#open(ALLC,"<$allcodes");
while(<>){
    ($code, $start, $end, $width) = split;
    if(($code > 0 ) && ($code < $MAXDOC) && ($width > $MINWIDTH)) {
	if($CODES[$code]) {
	    $ENDS[$CODES[$code]] = $end;
	    $NUMFND[$CODES[$code]]++;
	} else {
	    $CODES[$code] = $numSeq;
	    $STARTS[$numSeq] = $start;
	    $ENDS[$numSeq] = $end;
	    $WIDTH[$numSeq] = $width;
	    $PTR[$numSeq] = $code;
	    $NUMFND[$numSeq] = 1;
	    $numSeq++;
	    if ($mincode > $code) {
		$mincode = $code;
	    }
	}
    } else {
### just ignore super long codes	print $code;
    }
}				# done parsing codes, now find urls
# close(ALLC);
# open(ALLC,"<$tmpcodes");
# while(<ALLC>){
#     ($code, $start, $end, $width) = split;
#     if(($code > 0 ) && ($code < $MAXDOC) && ($width > $MINWIDTH)) {
# 	if($CODES[$code]) {
# 	    $ENDS[$CODES[$code]] = $end;
# 	    $NUMFND[$CODES[$code]]++;
# 	} else {
# 	    $CODES[$code] = $numSeq;
# 	    $STARTS[$numSeq] = $start;
# 	    $ENDS[$numSeq] = $end;
# 	    $WIDTH[$numSeq] = $width;
# 	    $PTR[$numSeq] = $code;
# 	    $NUMFND[$numSeq] = 1;
# 	    $numSeq++;
# 	}
#     } else {
# ### just ignore super long codes	print $code;
#     }
# }				# done parsing codes, now find urls


$docfound = 0;
$lasti = 101;
for($i=1;$i < $numSeq;$i++){
#The following is for debugging purposes only, it should be
#    print "code $i is $PTR[$i], starts at $STARTS[$i], ends at $ENDS[$i], width $WIDTH[$i], found $NUMFND[$i] times\n";
#     if( -d $docDir/$PTR[$i]) {
# 	$currentDoc = "$docDir/$PTR[$i])";
# 	print "Current doc is $currentDoc\n";
#     }
#     if( -e $currentDoc/$PTR[$i]) {
# 	$currentUrl = "$currentDoc/$PTR[$i])";
# 	print "Current URL is $currentUrl\n";
# 	open(CURR,"<$currentUrl");
# 	while(<CURR>){
# 	    print $_;		# Should set $url = <CURR>?
# 	}
#    }

    if($docfound) {
	#If in sequence, then a circled one would have been found
	if($lasti == ($PTR[$i] + 1)){
	    $lasti++;
	} else {
		#Otherwise if this was circled, we would not have found it
	if($urls[$PTR[$i]]){
	    print "$urls[$PTR[$i]] \n";	# If there is one...
	    print "$guessUrl \n"; # just in case...
	    #now exit
	    exit(0);
	}
    }
    } else {
	if(-e "$docDir/$PTR[$i]") {
	    &procUrlFile("$docDir/$PTR[$i]");
#We should never get to here, if we have, we have failed, so just
## return first url as guess
	    print "$url \n";	# If there is one...
	    print "$guessUrl \n"; # just in case...
	    #now exit
	    exit(0);

	    $docfound = 1;
	    $lasti = $mincode;
	} 
    }
}				# end...


