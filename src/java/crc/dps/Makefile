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
	tagset

FILES = \
	Action.java \
	Active.java \
	BasicEntityTable.java \
	BasicProcessor.java \
	Context.java \
	EntityTable.java \
	Filter.java \
	Handler.java \
	Input.java \
	NodeType.java \
	Output.java \
	Parser.java \
	Processor.java \
	ProcessorInput.java \
	Syntax.java \
	Tagset.java \
	Util.java

# === Potential renames:
#	ParseStack -> BasicContext
#	Filter -> Process
#	StackFrame is almost totally useless.

include $(TOPDIR)/makefiles/file.make
include $(TOPDIR)/makefiles/package.make

### Dependencies:
###	Compiling a class will automatically build the classes it
###	depends on.  This happens quietly (unfortunately), but make
###	seems to cope with it and not build anything twice.

