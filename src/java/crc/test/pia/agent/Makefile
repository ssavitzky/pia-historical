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

































































