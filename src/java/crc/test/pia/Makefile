###### interform/test/Makefile:	InterForm Interpretor tests
# 	$Id$
#	Copyright 1997, Ricoh California Research Center
PACKAGE=crc.test.pia
TOPDIR=../../../..

PACKAGES = \
	agent


FILES = \
	T_headerFactory.java \
	T_headers.java \
	T_contentFactory.java \
	T_formContent.java \
	T_hTTPRequest.java \
	T_hTTPResponse.java \
	T_machine.java \
	T_agentRedir.java

include $(TOPDIR)/makefiles/package.make
include $(TOPDIR)/makefiles/file.make

CASEFILES = \
	headers_1.case \
	headerfac_1.case \
	contfac_1.case \
	formcont_1.case \
	formcont_2.case \
	formcont_3.case \
	httpreq_1.case \
	httpreq_2.case \
	httpreq_3.case \
	httpres_1.case \
	httpres_2.case \
	httpres_3.case \
	httpres_4.case \
	httpres_5.case \
	machine_1.case \
	machine_2.case \
	machine_3.case \
	agentredir.case

.SUFFIXES: .case .txt .html .if .out .ok .diff .run .java .class

DEBUG=-d
CLASS=crc.interform.Filter

.case.out:

	$< > $@ 2>$*.log
	[ ! -f $*.ok ] || diff $*.ok $*.out > $*.diff

all:: $(FILES:.java=.class)
all:: $(CASEFILES:.case=.out)


clean::
	rm -f *.out *.diff *.log

































































