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

all:: tagset_docs tag_man.html examples.html

tag_man.html: $(IFAGENT)/tag_man.if
	$(IFFILTER) -o $@ -t Standard $?

examples.html: $(IFAGENT)/demo.if
	$(IFFILTER) -o $@ -t Standard $?

# === This should really be done by "make doc" in src ===
tagset_docs: 
	cd $(PIADIR)/src/java/crc/interform/handle; make actor-doc \
	PIADIR=../../../../..
