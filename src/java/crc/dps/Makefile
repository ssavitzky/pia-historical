###### dps/Makefile:	Document Processing System package.
# 	$Id$
#	Copyright 1998, Ricoh Silicon Valley

PACKAGE=crc.dps
TOPDIR=../..

PACKAGES = \
	active \
	aux \
	handle \
	input \
	output \
	parse \
	process \
	tagset

FILES = \
	Action.java \
	Active.java \
	Context.java \
	EntityTable.java \
	Filter.java \
	Handler.java \
	Input.java \
	Namespace.java \
	NodeType.java \
	Output.java \
	Parser.java \
	ProcessException.java \
	Processor.java \
	ProcessorInput.java \
	Syntax.java \
	Tagset.java \
	TopContext.java

# === Potential renames:
#	ParseStack -> BasicContext
#	Filter -> Process

include $(TOPDIR)/makefiles/file.make
include $(TOPDIR)/makefiles/package.make

### Dependencies:
###	Compiling a class will automatically build the classes it
###	depends on.  This happens quietly (unfortunately), but make
###	seems to cope with it and not build anything twice.

Handler.class: Syntax.java Action.java
