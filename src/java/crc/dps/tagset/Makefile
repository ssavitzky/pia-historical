###### dps/tagset/Makefile:	InterForm tagsets.
# 	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.dps.tagset
TOPDIR=../../..

### Base classes:
BASE_CLASSES = \
	BasicTagset.java \
	Loader.java \
	TagsetProcessor.java 

### The effect of these actors is entirely confined to the current file
BASIC_ACTORS = \

### The effect of these actors is local; they do not require a PIA, 
###	but may have an effect on the local system. 

SA_ACTORS = \

### These actors can affect the operation of the PIA

PIA_ACTORS= \


JAVA_TAGSETS = \
	BOOT_ts.java \
	HTML_ts.java \
	tagset.java \
	basic.java \
	standalone.java \
	standard.java \
	legacy.java

FILES = $(BASE_CLASSES) $(JAVA_TAGSETS)

TAGSETS = \
	basic.ts \
	tagset.ts \
	tsdoc.ts

TAGSET_DOCS = $(TAGSETS:.ts=.html)

include $(TOPDIR)/makefiles/file.make

TSDOCDIR = $(PIADIR)/Doc/Manuals/InterForm/Tagset


### Extract the documentation comments:

DOC_TS = legacyTSdoc.if
#DOC_TS = tsdoc.ts

.SUFFIXES: .ts .html
.ts.html:
	 cat $(DOC_TS) $< | process -t legacy > $@

#legacyTSdoc is a temporary kludge. 

DOC_CMD= $(BINDIR)/pia_wrapper crc.dps.Filter

doc:: ts-doc

ts-doc:: $(TAGSET_DOCS)

#doc:: actor-doc

clean::
	rm -f $(TAGSET_DOCS)

doc-clean:: 
	rm -f $(TAGSET_DOCS)

actor-doc:	$(TAGSET_DOCS)
	cp $(TAGSET_DOCS) $(TSDOCDIR)


HTML_ts.class: BasicTagset.java
basic.class: BasicTagset.java HTML_ts.java
standalone.class: BasicTagset.java HTML_ts.java
standard.class: BasicTagset.java HTML_ts.java
legacy.class: BasicTagset.java HTML_ts.java

basic.html: Makefile $(DOC_TS)
tsdoc.html: Makefile $(DOC_TS)
