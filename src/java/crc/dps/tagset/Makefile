###### dps/tagset/Makefile:	InterForm tagsets.
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
	tagset.java 

FILES = $(BASE_CLASSES) $(JAVA_TAGSETS)

### Main tagsets: the ones people will actually use.
MAIN_TAGSETS = \
	basic.ts \
	xxml.ts \
	xhtml.ts

### Specialized tagsets but still meant for ordinary users
SPECIAL_TAGSETS = \
	slides.ts \
	pia-xhtml.ts

### Auxiliary tagsets: used internally..
AUX_TAGSETS = \
	tagset.ts \
	tsdoc.ts \
	tsstrip.ts

TAGSETS = $(MAIN_TAGSETS) $(AUX_TAGSETS) $(SPECIAL_TAGSETS)

TAGSET_DOCS = $(TAGSETS:.ts=.html)
TAGSET_TSOS = $(TAGSETS:.ts=.tso)
TAGSET_TSSS = $(TAGSETS:.ts=.tss)

include $(TOPDIR)/makefiles/file.make


.SUFFIXES: .ts .html .obj .tso .tss

### Convert tagsets to serialized object (.tso) files
### Strictly speaking we would like this to be done by "all::", but
###	unfortunately we can't guarantee that the DPS is fully functional
###	by then, so we do in in "doc::" instead.

.ts.tso:
	process -t tagset -p $< > $@

.ts.tss:
	process -t tsstrip -q $< > $@

ts-tsos:: $(TAGSET_TSOS)

ts-tsss:: $(TAGSET_TSSS)

### Extract the documentation comments:

#DOC_TS = legacyTSdoc.if
DOC_TS = tsdoc.ts

.ts.html:
	process -t tsdoc -q $<  > $@

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

### Tagset dependencies:

standard.tso: basic.ts
standalone.tso: basic.ts

### Documentation dependencies:

basic.html: $(DOC_TS)
tsdoc.html: $(DOC_TS)
standard.html: $(DOC_TS)
standalone.html: $(DOC_TS)
tagset.html: $(DOC_TS)

