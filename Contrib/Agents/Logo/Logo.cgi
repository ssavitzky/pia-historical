#!/usr/bin/perl
###### Logo Agent as cgi
###	$Id$
###	Copyright 1997, Ricoh California Research Center.
###
###	routines for creating PIA logos and icons.  Uses GD drawing package.
###	Callable either as query or as <type><height>.

use GD;

### CGI or command-line main program

$qs = $ENV{'QUERY_STRING'};
$path = $ENV{'PATH_INFO'};
$default_height=24;

if ($path) {
    $cgi = 1;
} else {
    if ($ARGV[0] eq "-cgi") {
	$cgi = 1;
	shift;
    } else {
	$cgi = 0;
    }
    $path = $ARGV[0];
    if ($path =~ /^([^?]*)\?([^\?]*)/) { $path = $1; $qs = $2; }
    else {$qs = $ARGV[1]; }
}

$path = "/$path" unless $path =~ m:^/:;


### utility function to turn post content into hash:

sub compute_form_parameters{
    my($tosplit)=@_;

    ## Split a urlencoded query string or content into pairs,
    ##	  and store the result as a DS::Thing (usable as a hash table)
    ##	  on $self's "parameters" attribute.

    my(@pairs) = split('&',$tosplit);
    my($param,$value);
    my $hash = {};
    foreach (@pairs) {
	($param,$value) = split('=');
	$param = &unescape($param);
	$value = &unescape($value);
	$hash->{lc($param)} = $value;
	print "  $param=$value.\n"  if $main::debugging;
    }
    $hash
}

# unescape URL-encoded data
sub unescape {
    my($todecode) = @_;
    $todecode =~ tr/+/ /;	# pluses become spaces
    $todecode =~ s/%([0-9a-fA-F]{2})/pack("c",hex($1))/ge;
    return $todecode;
}


### Stuff we need for drawing pentagons (wonder why?)
###	Blythely ignore the fact that the coordinate system is upside-down, 
###	which puts theta=0 on the bottom.  Bletch.

$pi = 3.1415926575;
$a  = ($pi*2.0)/5.0;		# central angle between vertices (72 degrees)

sub cx {
    my ($rho, $theta) = @_;

    ## Cartesian x from polar (rho, theta)

    $rho * sin($theta);
}
sub cy {
    my ($rho, $theta) = @_;

    ## Cartesian y from polar (rho, theta)

    $rho * cos($theta);
}


### Drawing Substrate:

local ($white, $black, $red, $blue, $rred);
local ($lw, $sp);			# line width, inter-word spacing.

sub make_image  {
    my ($width, $height) = @_;

    ## Make an image to draw into.
    ##	 allocate basic colors; make white transparent.

    my $im = new GD::Image($width,$height);
    # allocate some colors
    $white = $im->colorAllocate(255,255,255);
    $black = $im->colorAllocate(0,0,0);       
    $red = $im->colorAllocate(255,0,0);      
    $rred= $im->colorAllocate(196,0,38);
    $blue = $im->colorAllocate(0,0,255);
    # make the background transparent and interlaced
    $im->transparent($white);
    $im;
}

### Pieces:

sub make_pentagon {
    my ($im, $r, $x0, $y0, $n, $start) = @_;

    ## Make a pentagon of radius $r centered at ($x0,$y0)
    ##	  Draw $n segments (default 5) starting at $pi + $start*$a

    my $theta = $pi + $start*$a;	# start at the top
    my $next, $i;
    $n = 5 unless $n;

    for ($i = 0; $i < $n; $i++) {
	$next = $theta + $a;
	$im->line(cx($r, $theta) + $x0, cy($r, $theta) + $y0,
		  cx($r, $next) + $x0, cy($r, $next) + $y0, $rred);
	$theta = $next;
    }

    $im;
}

sub make_lines {
    my ($im, $rmin, $rmax, $x0, $y0) = @_;

    ## Make radial lines from $rmin to $rmax centered at $x0 $y0

    my $theta = $pi;		# start at the top
    my $i;

    for ($i = 0; $i < 5; $i++) {
	$im->line(cx($rmin, $theta) + $x0, cy($rmin, $theta) + $y0,
		  cx($rmax, $theta) + $x0, cy($rmax, $theta) + $y0, $rred);
	$theta += $a;
    }

    $im;
}

sub make_logo {
    my ($im, $width, $height, $x0, $y0, $n, $start) = @_;

    ## Make a pentagon + lines logo centered at $x0 $y0
    ##	  Fill $n segments (default 0) starting at $pi + $start*$a
    ##	  Segments are numbered counterclockwise from the top.

    $x0 = ($width / 2) unless $x0;
    $y0 = ($height/ 2) unless $y0;

    my $rmax = $width/2 - 1;
    $rmax = ($height/2 - 1) if $height < $width;

    ## === need to rescale $r, $y0 so it's full scale vertically.
    ## === need to adjust width after doing that.

    $adj = $rmax/10;		# approximate so far
    $rmax  += $adj;
    $y0 += $adj;
    $x0 += $adj/2;

    my $rmin = $rmax / 3;
    my $rmid = $rmin * 2;

    return 2*$rmax + $adj unless $im;

    make_pentagon($im, $rmin, $x0, $y0);
    make_pentagon($im, $rmid, $x0, $y0);
    make_pentagon($im, $rmax, $x0, $y0);

    make_lines($im, $rmin, $rmax, $x0, $y0);

    if ($n) {
	fill_pentagon_segments($im, $rmid, $rmax, $x0, $y0, $n, $start);
    }

    2*$rmax + $adj;		# approximation of actual width
}


sub fill_pentagon_segments {
    my ($im, $r0, $r1, $x0, $y0, $n, $start) = @_;

    if ($r1 < 15) {
	## Filling this way is slower, but much more reliable than flood fill
	##   for small pentagons.  Unfortunately it has moire streaks, so
	##   flood fill is better for large ones.

	my $rfill;
	for ($rfill = $r0; $rfill <= $r1; $rfill += 0.2) {
	    make_pentagon($im, $rfill, $x0, $y0, $n, $start);
	}
	return;
    }

    my $r = ($r0+$r1)/2;
    my $theta = $pi + $start*$a;

    my $i;
    for ($i = 0; $i < $n; $i++) {
	my ($x1, $y1) = (cx($r0, $theta), cy($r0, $theta));
	my ($x2, $y2) = (cx($r1, $theta+$a), cy($r1, $theta+$a));
	$im->fill(($x1+$x2)/2 + $x0, ($y1+$y2)/2 + $y0, $rred);
	$theta += $a;
    }
}

### Hash table:

%logos = ('pent' => \&make_pent,
	  );

### Letters.
###	All take $im, $w, $h, $x0, $y0 and return width.
###	local $lw and $sp are line width and spacing, respectively
###	If $im is not a reference, just returns width required.
###	Some letters may take additional parameters after $y0

$logos{'*'} = \&make_pent;
sub make_pent {
    my ($im, $w, $h, $x0, $y0) = @_;
    my $i;

    ## pentagon logo 

    $x0 += $h/2;
    $y0 += $h/2;

    make_logo($im, $h, $h, $x0, $y0, 0, 0);
}

$logos{'A'} = $logos{'a'} = \&make_A;
sub make_A {
    my ($im, $w, $h, $x0, $y0) = @_;
    my $i;

    ## A -- logo with upper 4/5 filled in

    $x0 += $h/2;
    $y0 += $h/2;

    make_logo($im, $h, $h, $x0, $y0, 4, -2);
}

$logos{'B'}  = $logos{'b'} = \&make_B;
sub make_B {
    my ($im, $w, $h, $x0, $y0) = @_;

    my $r = $h/4;
    my $d = $sp;

    return $lw + $d + $r unless ref $im;

    ## Make a w * h rectangle and fill it.

    $im->filledRectangle($x0, $y0, $x0+$lw, $y0+$h, $rred);

    ## make a semicircle of radius $r, $w + $d from the left.

    $im->line($x0+$lw+$d, $y0, $x0+$lw+$d, $y0+$h, $rred);
    $im->arc($x0+$lw+$d, $y0+$r, # center
	     2*$r, 2*$r,	# w, h
	     270, 90, $rred);	# start, end, color
    $im->fill($x0+$lw+$d+2, $y0+$r, $rred);

    $im->arc($x0+$lw+$d, $y0+3*$r, # center
	     2*$r, 2*$r,	# w, h
	     270, 90, $rred);	# start, end, color
    $im->fill($x0+$lw+$d+2, $y0+3*$r, $rred);

    return $lw + $d + $r;
}


$logos{'C'}  = $logos{'c'} = \&make_C;
sub make_C {
    my ($im, $w, $h, $x0, $y0) = @_;
    my $i;

    ## C

    $x0 += $h/2;
    $y0 += $h/2;
    make_logo($im, $h, $h, $x0, $y0, 4, -1);
}

$logos{'D'}  = $logos{'d'} = \&make_D;
sub make_D {
    my ($im, $w, $h, $x0, $y0) = @_;

    ## D -- a simple semicircle of radius $h/2
    ##	    $x0 $y0 is the upper left corner.

    ## make a semicircle of radius $h/2 and fill it.

    my $r = $h/2;
    return $r unless ref $im;

    $im->line($x0, $y0, $x0, $y0+2*$r, $rred);
    $im->arc($x0, $y0+$r,	# center
	     2*$r, $h,		# w, h
	     270, 90, $rred);	# start, end, color

    $im->fill($x0+$r/2, $y0+$r, $rred);

    $r;
}


$logos{'E'}  = $logos{'e'} = \&make_E;
sub make_E {
    my ($im, $w, $h, $x0, $y0) = @_;

    ## E

    my $ww = $h/3;
    my $hh = ($h - $lw) / 2;

    return $lw + $ww unless ref $im;

    $im->filledRectangle($x0, $y0, $x0+$lw, $y0+$h, $rred);
    $im->filledRectangle($x0+$lw, $y0, $x0+$lw+$ww, $y0+$lw, $rred);
    $im->filledRectangle($x0+$lw, $y0+$hh, $x0+$lw+$ww/2, $y0+$hh+$lw, $rred);
    $im->filledRectangle($x0+$lw, $y0+$h-$lw, $x0+$lw+$ww, $y0+$h, $rred);

    $lw + $ww;
}

$logos{'F'}  = $logos{'f'} = \&make_F;
sub make_F {
    my ($im, $w, $h, $x0, $y0) = @_;

    ## F

    my $ww = $h/3;
    my $hh = ($h - $lw) / 2;

    return $lw + $ww unless ref $im;

    $im->filledRectangle($x0, $y0, $x0+$lw, $y0+$h, $rred);
    $im->filledRectangle($x0+$lw, $y0, $x0+$lw+$ww, $y0+$lw, $rred);
    $im->filledRectangle($x0+$lw, $y0+$hh, $x0+$lw+$ww/2, $y0+$hh+$lw, $rred);

    $lw + $ww;
}

$logos{'G'}  = $logos{'g'} = \&make_G;
sub make_G {
    my ($im, $w, $h, $x0, $y0) = @_;
    my $i;

    ## A -- logo with upper 4/5 filled in

    $x0 += $h/2;
    $y0 += $h/2;
    make_logo($im, $h, $h, $x0, $y0, 4, 0);
}

$logos{'I'}  = $logos{'i'} = \&make_I;
sub make_I {
    my ($im, $w, $h, $x0, $y0) = @_;

    ## I -- Make a w * h rectangle and fill it.

    return $lw unless ref $im;

    $im->filledRectangle($x0, $y0, $x0+$lw, $y0+$h, $rred) if ref $im;
    $lw;

}

$logos{'L'}  = $logos{'l'} = \&make_L;
sub make_L {
    my ($im, $w, $h, $x0, $y0) = @_;

    ## E

    my $ww = $h/3;

    return $lw + $ww unless ref $im;

    $im->filledRectangle($x0, $y0, $x0+$lw, $y0+$h, $rred);
    $im->filledRectangle($x0+$lw, $y0+$h-$lw, $x0+$lw+$ww, $y0+$h, $rred);

    $lw + $ww;
}

$logos{'O'}  = $logos{'o'} = \&make_O;
sub make_O {
    my ($im, $w, $h, $x0, $y0) = @_;

    ## O -- pentagon with the outer segments filled

    $x0 += $h/2;
    $y0 += $h/2;

    make_logo($im, $h, $h, $x0, $y0, 5, 0);
}

$logos{'P'}  = $logos{'p'} = \&make_P;
sub make_P {
    my ($im, $w, $h, $x0, $y0) = @_;

    my $r = $h/4;
    my $d = $sp;

    return $lw + $d + $r unless ref $im;

    ## Make a w * h rectangle and fill it.

    $im->filledRectangle($x0, $y0, $x0+$lw, $y0+$h, $rred);

    ## make a semicircle of radius $r, $w + $d from the left.

    $im->line($x0+$lw+$d, $y0, $x0+$lw+$d, $y0+2*$r, $rred);
    $im->arc($x0+$lw+$d, $y0+$r, # center
	     2*$r, 2*$r,	# w, h
	     270, 90, $rred);	# start, end, color

    $im->fill($x0+$lw+$d+2, $y0+$r, $rred);

    return $lw + $d + $r;
}


$logos{'S'}  = $logos{'s'} = \&make_S;
sub make_S {
    my ($im, $w, $h, $x0, $y0) = @_;

    my $r = $h/4;
  
    return 2 * $r unless ref $im;

    ## make a semicircle of radius $r, $r from the left.

    $im->line($x0+$r, $y0, $x0+$r, $y0+$h, $rred);
    $im->arc($x0+$r, $y0+$r,	# center
	     2*$r, 2*$r,	# w, h
	     90, 270, $rred);	# start, end, color
    $im->fill($x0+$r-2, $y0+$r, $rred);

    ## Now one going the other way underneath.  Crude.

    $im->arc($x0+$r, $y0+3*$r,	# center
	     2*$r, 2*$r,	# w, h
	     270, 90, $rred);	# start, end, color
    $im->fill($x0+$r+2, $y0+3*$r, $rred);

    return 2 * $r;
}


### Combinations:

sub make_str {
    my ($im, $w, $h, $x0, $y0, $s) = @_;
    my @chrs = split(//, $s);
    my $sub;

    ## Make a logo out of a string;

    foreach $c (@chrs) {
	$sub = $logos{$c};
	$x0 += &$sub($im, $w, $h, $x0, $y0) + $sp if defined $sub;
    }

    $x0 - $sp;
}


### Take a path and a query string.
sub respond {
    my($path, $qs)=@_;
    my %hash=%{compute_form_parameters($qs)};

    ## See if we have a filename of the form <type><height>[.ext]
    ##	 Height may be 0, in which case the default is used.
    ##	 The extension defaults to "gif"

    $path =~ m:/([a-zA-Z]+)([0-9]+)(.[a-zA-Z]*)?$:;
    if (defined $1 && defined $2) {
	$hash{'type'} = $1 unless defined $hash{'type'};
	$hash{'height'} = $2 unless defined $hash{'height'};
	$hash{'ext'} = $3 if defined $3;
    }

    ## Get parameters out of query string:

    my $height = $hash{'height'};
    $height = $default_height if $height == 0;
    my $width = $hash{'width'};
    my $type = $hash{'type'};

    die "no height" unless $height;

    my $w = $hash{'w'};
    my $h = $hash{'h'};		# height of a letter ($height - 2*$bw)

    ## Propagate these down:

    local $bw = $hash{'bw'};	# border width (1)
    local $sp = $hash{'sp'};	# inter-word space
    local $lw = $hash{'lw'};	# width of a line

    ## Compute defaults:

    $bw = ($height <= 20? 0 : 1) unless defined $bw;
    $type = 'pent' unless defined $type;
    $h = $height - 2 * $bw unless defined $h;
    $w = $width - 2 * $bw if defined $width;

    $lw = 15*$h/100 unless defined $lw;
    $sp = 10*$h/100 unless defined $sp;

    ## Look up the appropriate routine:

    my $sub = $logos{$type};
    $sub = \&make_str unless defined $sub;

    ## Do it:

    $width = &$sub(0, $w, $h, $bw, $bw, $type)+$bw unless defined $width;
    my $im = make_image($width, $height);
    &$sub($im, $w, $h, $bw, $bw, $type);

    
    ## Create response:

    if ($cgi) {
	print "HTTP/1.1 200 OK\n";
	print "Content-type: image/gif\n";
	print "Content-length: ".length($im->gif)."\n";
	print "\n";
    }
    print $im->gif;
}

### Main program: after we're all initialized!

respond($path, $qs);
close STDOUT;
exit 0;

1;
