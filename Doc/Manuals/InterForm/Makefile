###### Doc/Manuals/InterForm/Makefile:
# 	$Id$
#	Copyright 1997, Ricoh California Research Center

PIADIR=../../..
IFAGENT=$(PIADIR)/Agents/InterForm
IFFILTER=$(PIADIR)/bin/iffilter

all:: tagset_docs tag_man.html examples.html

tag_man.html: $(IFAGENT)/tag_man.if
	$(IFFILTER) -o $@ -t Standard $?

examples.html: $(IFAGENT)/demo.if
	$(IFFILTER) -o $@ -t Standard $?

# === this should probably be one level down ===
tagset_docs: 
	cd $(PIADIR)/src/java/crc/interform/handle; make actor-doc
