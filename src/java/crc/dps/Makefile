###### dps/Makefile:	Document Processing System package.
# 	$Id$
#	Copyright 1998, Ricoh Silicon Valley

PACKAGE=crc.dps
TOPDIR=../..

PACKAGES = \
	handle \
	input \
	output

FILES = \
	AbstractHandler.java \
	AbstractInput.java \
	AbstractInputFrame.java \
	AbstractOutput.java \
	AbstractParser.java \
	BasicEntityTable.java \
	BasicProcessor.java \
	BasicTagset.java \
	BasicToken.java \
	EntityTable.java \
	Handler.java \
	Input.java \
	InputStack.java \
	Output.java \
	ParseStack.java \
	Parser.java \
	Processor.java \
	StackFrame.java \
	Tagset.java \
	Token.java \
	Util.java

include $(TOPDIR)/makefiles/file.make
include $(TOPDIR)/makefiles/package.make

### Dependencies:
###	Compiling a class will automatically build the classes it
###	depends on.  This happens quietly (unfortunately), but make
###	seems to cope with it and not build anything twice.

InputStack.class: StackFrame.java
ParseStack.class: StackFrame.java

