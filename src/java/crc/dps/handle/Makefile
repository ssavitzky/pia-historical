###### dps/handle/Makefile:	InterForm tag handlers
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


PACKAGE=crc.dps.handle
TOPDIR=../../..

### Base classes and generic node-class handlers:
BASE_CLASSES = \
	AbstractHandler.java \
	BasicHandler.java \
	EntityHandler.java \
	GenericHandler.java \
	IgnorableHandler.java \
	SkippableHandler.java \
	TextNodeHandler.java \
	TypicalHandler.java

### These are the primitive handlers defined in basic.ts
###	Indented names are handlers for active sub-elements.
PRIM_HANDLERS = \
	defineHandler.java \
	  actionHandler.java \
	  valueHandler.java \
	connectHandler.java \
	expandHandler.java \
	extractHandler.java \
	headersHandler.java \
	hideHandler.java \
	ifHandler.java \
	  elseHandler.java \
	  elsfHandler.java \
	  thenHandler.java \
	includeHandler.java \
	logicalHandler.java \
	namespaceHandler.java \
	numericHandler.java \
	outputHandler.java \
	parseHandler.java \
	protectHandler.java \
	queryHandler.java \
	repeatHandler.java \
	statusHandler.java \
	substHandler.java \
	tagsetHandler.java \
	testHandler.java \
	textHandler.java \
	debugHandler.java \
	urlHandler.java 

### Additional actions for StandAlone XML

XXML_HANDLERS = \
	getHandler.java \
	setHandler.java 

### Additional handlers for extended HTML

XHTM_HANDLERS = \
	formHandler.java \
	processHandler.java

### These handlers can affect the operation of the PIA

PIA_HANDLERS= \
	submitHandler.java


HANDLERS = $(PRIM_HANDLERS) $(XXML_HANDLERS) $(XHTM_HANDLERS) $(PIA_HANDLERS)
FILES = $(BASE_CLASSES) $(HANDLERS) Loader.java

include $(TOPDIR)/makefiles/file.make

### Reporting.
all:: count

report:: count check

### Count unimplemented and total handlers.  
count::
	@echo 'Handlers - ' `echo $(HANDLERS) | wc -w` top, \
		`grep 'class ' $(HANDLERS) | wc -l` total, \
		`grep 'unimplemented(' $(HANDLERS) | wc -l` unimp.

### This finds and prints out every instance of the string "unimplemented" 
check::
	-@grep -n 'unimplemented(' `ls $(HANDLERS)` 

### Run a perl script over all handlers.  It can be redefined for various
###	one-shot fixups; the first use is transforming syntax comments
###	into a combination of javadoc and executable forms.

FIXUP=./fixup.pl
fixup:
	$(FIXUP) $(HANDLERS)

unfix:
	for f in *.java ; do if [ -f $$f.bak ]; then mv -f $$f.bak $$f; fi ; done


###
### make class tag=XXX
###

class: 
	@( [ -f $(tag)Handler.java ] \
	  && echo "$(tag)Handler.java already exists" ) || \
	( sed -e 's/Typical/$(tag)/g' < TypicalHandler.java \
	| sed -e 's/typical/$(tag)/g' > $(tag)Handler.java \
	; echo $(tag)Handler.java constructed. )

### Dependencies:

HANDLER_DEPS = GenericHandler.java BasicHandler.java AbstractHandler.java \
	../Handler.java

AbstractHandler.class: ../Handler.java
BasicHandler.class: AbstractHandler.java ../Handler.java
EntityHandler.class: AbstractHandler.java ../Handler.java
GenericHandler.class: BasicHandler.java AbstractHandler.java ../Handler.java
TextHandler.class: AbstractHandler.java ../Handler.java


defineHandler.class: $(HANDLER_DEPS)
actionHandler.class: $(HANDLER_DEPS)
valueHandler.class: $(HANDLER_DEPS)
elseHandler.class: $(HANDLER_DEPS)
elsfHandler.class: $(HANDLER_DEPS)
expandHandler.class: $(HANDLER_DEPS)
getHandler.class: $(HANDLER_DEPS)
hideHandler.class: $(HANDLER_DEPS)
logicalHandler.class: $(HANDLER_DEPS)
numericHandler.class: $(HANDLER_DEPS)
ifHandler.class: $(HANDLER_DEPS)
protectHandler.class: $(HANDLER_DEPS)
repeatHandler.class: $(HANDLER_DEPS)
selectHandler.class: $(HANDLER_DEPS)
setHandler.class: $(HANDLER_DEPS)
submitHandler.class: $(HANDLER_DEPS)
substHandler.class: $(HANDLER_DEPS)
tagsetHandler.class: $(HANDLER_DEPS)
testHandler.class: $(HANDLER_DEPS)
textHandler.class: $(HANDLER_DEPS)
thenHandler.class: $(HANDLER_DEPS)
debugHandler.class: $(HANDLER_DEPS)
