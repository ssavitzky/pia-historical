###### Doc/Manuals/InterForm/Makefile:
# 	$Id$
#	Copyright 1997, Ricoh California Research Center

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
