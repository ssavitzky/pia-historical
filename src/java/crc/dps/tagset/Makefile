###### dps/tagset/Makefile:	InterForm tagsets.
# 	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.dps.tagset
TOPDIR=../../..

### Directories:
TSDOCDIR = $(PIADIR)/Doc/Manuals/InterForm/Tagset

### Base classes:
BASE_CLASSES = \
	BasicTagset.java \
	Loader.java \
	TagsetProcessor.java 

### Java tagsets: 
### 	Most are implemented in Java for speed or for historical reasons.
###	the BOOT tagset is in Java because it has to be.
JAVA_TAGSETS = \
	BOOT_ts.java \
	HTML_ts.java \
	tagset.java \
	legacy.java

FILES = $(BASE_CLASSES) $(JAVA_TAGSETS)

### Main tagsets: the ones people will actually use.
MAIN_TAGSETS = \
	basic.ts \
	xxml.ts \
	xhtml.ts

### Auxiliary tagsets: used internally..
AUX_TAGSETS = \
	tagset.ts \
	tsdoc.ts

TAGSETS = $(MAIN_TAGSETS) $(AUX_TAGSETS)

TAGSET_DOCS = $(TAGSETS:.ts=.html)
TAGSET_TSOS = $(TAGSETS:.ts=.tso)

include $(TOPDIR)/makefiles/file.make


.SUFFIXES: .ts .html .obj .tso

### Convert tagsets to serialized object (.tso) files
### Strictly speaking we would like this to be done by "all::", but
###	unfortunately we can't guarantee that the DPS is fully functional
###	by then, so we do in in "doc::" instead.

.ts.tso:
	process -t tagset -p $< > $@

ts-tsos:: $(TAGSET_TSOS)

### Extract the documentation comments:

#DOC_TS = legacyTSdoc.if
DOC_TS = tsdoc.ts

.ts.html:
	process -t tsdoc -q $<  > $@

#legacyTSdoc is a temporary kludge. 

DOC_CMD= $(BINDIR)/pia_wrapper crc.dps.Filter

doc:: count ts-tsos ts-doc

ts-doc:: $(TAGSET_DOCS)
	cp $? $(TSDOCDIR)

count::
	@echo Elements: \
	  `egrep '<define element' $(TAGSETS) | wc -l` defined, \
	  `egrep '^[^>]*<define element' $(TAGSETS) | wc -l` top, \
	  `egrep '>.*<define element' $(TAGSETS) | wc -l` sub.
	@echo Handled: \
	  `egrep '<define element.*handle' $(TAGSETS) | wc -l` defined, \
	  `egrep '^[^>]*<define element.*handle' $(TAGSETS) | wc -l` top, \
	  `egrep '>.*<define element.*handle' $(TAGSETS) | wc -l` sub.

clean:: ts-clean doc-clean

ts-clean::
	rm -f *.tss *.tso *.obj

doc-clean:: 
	rm -f $(TAGSET_DOCS)

actor-doc:	$(TAGSET_DOCS)
	cp $(TAGSET_DOCS) $(TSDOCDIR)


### Java dependencies:

HTML_ts.class: BasicTagset.java
basic.class: BasicTagset.java HTML_ts.java
standalone.class: BasicTagset.java HTML_ts.java
standard.class: BasicTagset.java HTML_ts.java
legacy.class: BasicTagset.java HTML_ts.java

### Tagset dependencies:

standard.tso: basic.ts
standalone.tso: basic.ts

### Documentation dependencies:

basic.html: $(DOC_TS)
tsdoc.html: $(DOC_TS)
standard.html: $(DOC_TS)
standalone.html: $(DOC_TS)
tagset.html: $(DOC_TS)

