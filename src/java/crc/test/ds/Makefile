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

PACKAGE=crc.test.ds
TOPDIR=../../..

FILES = \
	T_indexPath.java \
	T_indexLookup.java 


include $(TOPDIR)/makefiles/file.make
#include $(TOPDIR)/makefiles/package.make

CASEFILES = \
	normalLookup.case \
	dlLookup.case \
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

































































