###### dps/Makefile:	Document Processing System package.
# 	$Id$
#	Copyright 1998, Ricoh Silicon Valley

PACKAGE=crc.dps
TOPDIR=../..

PACKAGES = \
	handle \
	input \
	output \
	parse \
	tagset \
	test

FILES = \
	BasicEntityTable.java \
	BasicProcessor.java \
	BasicToken.java \
	BasicTokenList.java \
	EntityTable.java \
	Filter.java \
	Handler.java \
	Input.java \
	InputStack.java \
	NodeType.java \
	Output.java \
	ParseStack.java \
	Parser.java \
	Processor.java \
	ProcessorInput.java \
	StackFrame.java \
	Tagset.java \
	Token.java \
	TokenList.java \
	Util.java

include $(TOPDIR)/makefiles/file.make
include $(TOPDIR)/makefiles/package.make

### Dependencies:
###	Compiling a class will automatically build the classes it
###	depends on.  This happens quietly (unfortunately), but make
###	seems to cope with it and not build anything twice.

InputStack.class: StackFrame.java
ParseStack.class: StackFrame.java

