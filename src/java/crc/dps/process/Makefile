###### dps/handle/Makefile:	InterForm tag handlers
# 	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.dps.handle
TOPDIR=../../..


FILES = \
	BasicProcessor.java \
	TopProcessor.java \
	ActiveDoc.java \
	Hook.java

include $(TOPDIR)/makefiles/file.make


### Dependencies:

BasicProcessor.class: ../Processor.java ../Context.java \
	../util/ContextStack.java
TopProcessor.class: BasicProcessor.java ../TopContext.java \
	../Processor.java ../Context.java ../util/ContextStack.java
