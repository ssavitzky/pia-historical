### text.make
# $Id$
# COPYRIGHT 1997, Ricoh California Research Center

########################################################################
#
# This makefile contains rules for text format conversion.
#
########################################################################

.SUFFIXES: .tex .dvi .html .idx .ps .xbm .fig .eps .gif .mif .fm .ppm

### .fm (frame5) to mif    put cmds in file then do fmbatch

.fm.mif:
	echo "Open $<" >> .tmp ;echo "SaveAs m $< $*.mif" >> .tmp; \
 echo "Quit " >> .tmp ; fmbatch .tmp ; rm .tmp

### .fm (frame5) to ps    ../pia_temp.fm holds printer settings
#	 can go to file or printer...

.fm.ps:
	echo "Open $<" >> .tmp ;echo "Open ../pia_temp.fm" >> .tmp; \
 echo "Print $< ../pia_temp.fm" >> .tmp ; fmbatch .tmp ; rm .tmp

### .mif (framemaker) to html

.mif.html:
	webmaker -c ../pia.wml -w ./ -t "$*" -n "page" -f BOTTOM -e \
 CONVERT-TO-IMAGES  $< ; ln page-1.html $*.html


### LaTeX to dvi and dvi to postscript 

.tex.dvi:
	echo q | latex $*
	if [ -f $*.toc ]; then echo q | latex $*; else true; fi

.dvi.ps:
	dvips -r0 -o $*.ps $*

### HTML to `slides'

PROCESS=$(PIADIR)/bin/process
SLIDES_TS = $(PIADIR)/src/java/crc/dps/tagset/slides.ts

%.slides.html: %.html $(SLIDES_TS)
	time $(PROCESS) -t slides $*.html > $@
