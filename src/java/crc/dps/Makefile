###### dps/Makefile:	Document Processing System package.
# 	$Id$
#	Copyright 1998, Ricoh Silicon Valley

PACKAGE=crc.dps
TOPDIR=../..

PACKAGES = \
	active \
	handle \
	input \
	output \
	parse \
	process \
	tagset \
	util

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

include $(TOPDIR)/makefiles/package.make
include $(TOPDIR)/makefiles/file.make

### Dependencies:
###	Compiling a class will automatically build the classes it
###	depends on.  This happens quietly (unfortunately), but make
###	seems to cope with it and not build anything twice.

Handler.class: Syntax.java Action.java
