###### interform/test/Makefile:	InterForm Interpretor tests
# 	$Id$

############################################################################### 
 # The contents of this file are subject to the Ricoh Source Code Public
 # License Version 1.0 (the "License"); you may not use this file except in
 # compliance with the License.  You may obtain a copy of the License at
 # http://www.risource.org/RPL
 #
 # Software distributed under the License is distributed on an "AS IS" basis,
 # WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 # for the specific language governing rights and limitations under the
 # License.
 #
 # This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 # created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 # Rights Reserved.
 #
 # Contributor(s):
 #
############################################################################## 

PACKAGE=crc.test.pia
TOPDIR=../../..

PACKAGES = \
	agent


FILES = \
	T_headerFactory.java \
	T_headers.java \
	T_contentFactory.java \
	T_content.java \
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
	cont_1.case \
	cont_2.case \
	cont_3.case \
	cont_4.case \
	cont_5.case \
	cont_6.case \
	cont_7.case \
	httpreq_1.case \
	httpreq_2.case \
	httpreq_3.case \
	httpreq_4.case \
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
cases:: $(CASEFILES:.case=.out)


clean::
	rm -f *.out *.diff *.log

































































