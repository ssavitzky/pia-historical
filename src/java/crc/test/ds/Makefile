###### interform/test/Makefile:	InterForm Interpretor tests
# 	$Id$
#	Copyright 1997, Ricoh California Research Center
PACKAGE=crc.test.ds
TOPDIR=../../..

FILES = \
	T_indexPath.java \
	T_indexLookup.java 


include $(TOPDIR)/makefiles/package.make
include $(TOPDIR)/makefiles/file.make

CASEFILES = \
	normalLookup_0.case \
	dlLookup_0.case \
	makePath_0.case \
	buildHtml_0.case


.SUFFIXES: .case .txt .html .if .out .ok .diff .run .java .class

DEBUG=-d
CLASS=crc.interform.Filter

.case.out:

	$< > $@ 2>$*.log
	[ ! -f $*.ok ] || diff $*.ok $*.out > $*.diff

all:: $(FILES:.java=.class)
cases:: $(CASEFILES:.case=.out)


clean::
	rm -f *.out *.diff *.log

































































