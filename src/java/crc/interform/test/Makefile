###### interform/test/Makefile:	InterForm Interpretor tests
# 	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.interform.test
TOPDIR=../../../..

FILES = \
	t1.txt \
	t2.html

.SUFFIXES: .txt .html .if .out .ok .diff

DEBUG=-d
CLASS=crc.interform.Filter

.txt.out:
	$(BINDIR)/pia_wrapper $(CLASS) $(DEBUG) $< > $@ 2>$*.log
	[ ! -f $*.ok ] || diff $*.ok $*.out > $*.diff

.html.out:
	$(BINDIR)/pia_wrapper $(CLASS) $(DEBUG) -t HTML $< > $@ 2>$*.log
	[ ! -f $*.ok ] || diff $*.ok $*.out > $*.diff

.if.out:
	$(BINDIR)/pia_wrapper $(CLASS) $(DEBUG) -t StandAlone $< > $@ 2>$*.log
	[ ! -f $*.ok ] || diff $*.ok $*.out > $*.diff

include $(TOPDIR)/makefiles/file.make

all:: $(FILES:.txt=.out)

all:: $(FILES:.if=.out)

all:: $(FILES:.html=.out)

clean::
	rm -f *.out *.diff *.log
