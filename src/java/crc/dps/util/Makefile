###### dps/aux/Makefile:	DPS auxiliary classes
# 	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.dps.util
TOPDIR=../../..

FILES = $(CLASSES) $(UTILITIES)

CLASSES = \
	BasicNamespace.java \
	NamespaceWrap.java \
	EntityInput.java \
	EntityWrap.java \
	BasicEntityTable.java \
	ContextStack.java \
	CurrentActive.java \
	CurrentNode.java \
	CursorStack.java

UTILITIES = \
	Copy.java \
	Create.java \
	Expand.java \
	Index.java \
	Log.java \
	Test.java \
	ListUtil.java \
	MathUtil.java \
	TextUtil.java

include $(TOPDIR)/makefiles/file.make

### Dependencies

BasicEntityTable.class: BasicNamespace.java \
	../Namespace.java ../EntityTable.java

BasicNamespace.class: ../Namespace.java ../EntityTable.java
