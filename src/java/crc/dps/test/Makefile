###### interform/test/Makefile:	InterForm Interpretor tests
# 	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.interform.test
TOPDIR=../../..

FILES = \
	t1.txt \
	t2.html \
	t3.if \
	t4.if \
	t5.if

.SUFFIXES: .txt .html .if .out .ok .diff .run

DEBUG=
CLASS=crc.dps.Filter

.txt.out:
	$(BINDIR)/pia_wrapper $(CLASS) $(DEBUG) -t Bootstrap $< > $@ 2>$*.log
	[ ! -f $*.ok ] || diff $*.ok $*.out > $*.diff

.html.out:
	$(BINDIR)/pia_wrapper $(CLASS) $(DEBUG) -t HTML -e $< > $@ 2>$*.log
	[ ! -f $*.ok ] || diff $*.ok $*.out > $*.diff

# ./$< is used to make fileName and filePath different.
.if.out:
	$(BINDIR)/pia_wrapper $(CLASS) $(DEBUG) -t StandAlone ./$< > $@ 2>$*.log
	[ ! -f $*.ok ] || diff $*.ok $*.out > $*.diff

# make foo.run runs the tests with output on STDOUT.
.txt.run:
	$(BINDIR)/pia_wrapper $(CLASS) $(DEBUG) -t Bootstrap $<  2>$*.log \
	| tee $*.out

.html.run:
	$(BINDIR)/pia_wrapper $(CLASS) $(DEBUG) -t HTML -e $< 2>$*.log \
	| tee $*.out

# ./$< is used to make fileName and filePath different.
.if.run:
	$(BINDIR)/pia_wrapper $(CLASS) $(DEBUG) -t StandAlone ./$< 2>$*.log \
	| tee $*.out


include $(TOPDIR)/makefiles/file.make

all:: $(FILES:.txt=.out)

all:: $(FILES:.if=.out)

all:: $(FILES:.html=.out)

clean::
	rm -f *.out *.diff *.log *.tmp*


BIG_FILE=../../../../../Doc/Manuals/InterForm/tag_man.html
OCLASS=crc.interform.Filter

time_test::
	@ls -l $(BIG_FILE)
	time cat $(BIG_FILE) >/dev/null
	time $(BINDIR)/pia_wrapper $(CLASS) -t HTML $(BIG_FILE) -n -p >/dev/null
	time $(BINDIR)/pia_wrapper $(CLASS) -t HTML $(BIG_FILE) -n >/dev/null
	time $(BINDIR)/pia_wrapper $(CLASS) -t HTML $(BIG_FILE) -p >/dev/null
	time $(BINDIR)/pia_wrapper $(CLASS) -t HTML $(BIG_FILE) >/dev/null
	time $(BINDIR)/pia_wrapper $(OCLASS) -t HTML $(BIG_FILE) >/dev/null

