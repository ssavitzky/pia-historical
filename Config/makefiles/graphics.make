### graphics.make
# $Id$
# COPYRIGHT 1997, Ricoh California Research Center

########################################################################
#
# This makefile contains rules for graphics format conversion.
#
########################################################################

.SUFFIXES: .tex .dvi .html .idx .ps .xbm .fig .eps .gif .mif .fm .ppm


### .fig (xfig) to various output formats.
#	Works even with GNU ghostscript, which doesn't have GIF drivers.

.fig.gif:	 
	fig2dev -Lps -P $< \
	| gs -q -dNOPAUSE -sDEVICE=ppmraw -sOutputFile=- - \
	| pnmcrop | ppmtogif -transparent white > $@

.fig.ps:	 
	fig2dev -Lps -P $< > $@

.fig.eps:	 
	fig2dev -Lps $<  > $@

### Other image format conversions 

.ps.gif:	 
	gs -q -dNOPAUSE -sDEVICE=ppmraw -sOutputFile=- $< </dev/null\
	| pnmcrop | ppmtogif -transparent white > $@

.ppm.gif:	 
	ppmtogif -transparent white $< > $@

.gif.eps:
	giftopnm $< | pnmtops -noturn -rle -nocenter > $@

