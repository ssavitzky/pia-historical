###### dps/tagset/Makefile:	InterForm tagsets.
# 	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.dps.tagset
TOPDIR=../../..

### Base classes:
BASE_CLASSES = \
	BasicTagset.java

### The effect of these actors is entirely confined to the current file
BASIC_ACTORS = \

### The effect of these actors is local; they do not require a PIA, 
###	but may have an effect on the local system. 

SA_ACTORS = \

### These actors can affect the operation of the PIA

PIA_ACTORS= \


TAGSETS = \
	HTML_ts.java \
	Basic_ts.java \
	StandAlone_ts.java \
	Standard_ts.java

ACTORS = $(BASIC_ACTORS) $(SA_ACTORS) $(PIA_ACTORS)

FILES = $(BASE_CLASSES) # $(TAGSETS)

TAGSET_DOCS =  \
	Basic-tagset.html  \
	StandAlone-tagset.html  \
	Standard-tagset.html

include $(TOPDIR)/makefiles/file.make

TSDOCDIR = $(PIADIR)/Doc/Manuals/InterForm/Tagset


### Extract the documentation comments:

DOC_CMD= $(BINDIR)/pia_wrapper crc.dps.Filter

Basic-tagset.html::	 $(BASIC_ACTORS) Basic_ts.java
	$(DOC_CMD) -t Basic actor-doc.if > Basic-tagset.html

StandAlone-tagset.html:: $(BASIC_ACTORS) $(SA_ACTORS) StandAlone_ts.java
	$(DOC_CMD) -t StandAlone actor-doc.if > StandAlone-tagset.html

Standard-tagset.html::	 $(BASIC_ACTORS) $(SA_ACTORS) $(PIA_ACTORS) \
	Standard_ts.java
	$(DOC_CMD) -t Standard actor-doc.if > Standard-tagset.html

#doc:: actor-doc report

clean::
	rm -f $(TAGSET_DOCS)

actor-doc:	$(TAGSET_DOCS)
	cp $(TAGSET_DOCS) $(TSDOCDIR)

