###### interform/test/Makefile:	InterForm Interpretor tests
# 	$Id$
#	Copyright 1997, Ricoh California Research Center
PACKAGE=crc.test.pia.agent
TOPDIR=../../../../..

FILES = \
	T_agency.java \
	T_dofs.java

include $(TOPDIR)/makefiles/file.make

CASEFILES = \
	agency_1.case \
	dofs_1.case \
	dofs_2.case \
	dofs_3.case \
	dofs_4.case 

.SUFFIXES: .case .txt .html .if .out .ok .diff .run .java .class

DEBUG=-d

.case.out:

	$< > $@ 2>$*.log
	[ ! -f $*.ok ] || diff $*.ok $*.out > $*.diff

all:: $(FILES:.java=.class)
all:: $(CASEFILES:.case=.out)


clean::
	rm -f *.out *.diff *.log

































































