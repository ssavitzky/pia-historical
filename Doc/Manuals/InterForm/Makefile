###### Doc/Manuals/InterForm/Makefile:
# 	$Id$

############################################################################## 
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


PIADIR=../../..
MF_DIR=$(PIADIR)/Config/makefiles
MYNAME=InterForm
MYPATH=Doc/Manuals/InterForm
include $(MF_DIR)/file.make

IFAGENT=$(PIADIR)/Agents/InterForm
IFFILTER=$(PIADIR)/bin/iffilter

legacy_all:: tag_man.html examples.html

tag_man.html: $(IFAGENT)/tag_man.if
	$(IFFILTER) -o $@ -t Standard $?

examples.html: $(IFAGENT)/demo.if
	$(IFFILTER) -o $@ -t Standard $?

# This is normally done via "make doc" at the top level instead.
tagset_docs: 
	cd $(PIADIR)/src/java/crc/dps/tagset; make ts-doc \
	PIADIR=../../../../..

# This is legacy documentation.
legacy_tagset_docs: 
	cd $(PIADIR)/src/java/crc/interform/handle; make actor-doc \
	PIADIR=../../../../..
