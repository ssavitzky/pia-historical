#!/usr/local/bin/perl
#Cleanup output from the decode program
#only return one instance of each decoded thing
$docDir = "./";			# This is where we should look for doc ids to match the ones found in the code
$currentDoc = 0;	# Default
$urlFile = "$docDir/allUrls";	

#Doc ids and URL ids should be maintained seperately (eg. no overlap
#so we can first test for a doc id and then for a URL id 
#Doc ids are just directory names...URL ids are just file names with the URL
#as the only line of the file...

$MAXDOC = 200000;
###Perl treats our ids as numbers instead of characters, so
###it tries to create an array as looooong as the number we 
### are using as a key...thus we need a limit  would be nicer to 
### force it to use a hash table

##TBD change [] to {} ....

##Assume codes shorter than this are noise
$MINWIDTH = 7;

sub procUrlFile {				# This should be an associative array
    local($urlFile) = @_;
    open(URLS,"<$urlFile");
    while(<URLS>){
	($ucode, $url) = split;
	$urls[$ucode] = $url;	# Put something here to limit scope to 1 doc
    }
}

&procUrlFile($urlFile);

$numSeq	= 1;

##Added test to make sure we don't try to create humongous array--dig codes are
## thus limited to below this number

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
	}
    } else {
### just ignore super long codes	print $code;
    }
}				# done parsing codes, now find urls

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
    if(-e "$docDir/$PTR[$i]") {
	&procUrlFile("$docDir/$PTR[$i]");
    } else {
	if($urls[$PTR[$i]]){
	    print "$urls[$PTR[$i]] \n";	# If there is one...
	}
    }
}				# end...


