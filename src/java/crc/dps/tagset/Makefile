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

### Java tagsets: 
### 	Most are implemented in Java for speed or for historical reasons.
###	the BOOT tagset is in Java because it has to be.
JAVA_TAGSETS = \
	BOOT_ts.java \
	HTML_ts.java \
	tagset.java \
	basic.java \
	legacy.java

### Obsolete Java tagsets.
OBS_TAGSETS = \
	standalone.java \
	standard.java

FILES = $(BASE_CLASSES) $(JAVA_TAGSETS)

### Main tagsets: the ones people will actually use.
MAIN_TAGSETS = \
	basic.ts \
	standalone.ts

### Auxiliary tagsets: used internally..
AUX_TAGSETS = \
	tagset.ts \
	tsdoc.ts

TAGSETS = $(MAIN_TAGSETS) $(AUX_TAGSETS)

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

doc:: count ts-doc

ts-doc:: $(TAGSET_DOCS)

count::
	@echo Elements: \
	  `egrep '<define element' $(TAGSETS) | wc -l` defined, \
	  `egrep '^[^>]*<define element' $(TAGSETS) | wc -l` top, \
	  `egrep '>.*<define element' $(TAGSETS) | wc -l` sub.
	@echo Handled: \
	  `egrep '<define element.*handle' $(TAGSETS) | wc -l` defined, \
	  `egrep '^[^>]*<define element.*handle' $(TAGSETS) | wc -l` top, \
	  `egrep '>.*<define element.*handle' $(TAGSETS) | wc -l` sub.

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
