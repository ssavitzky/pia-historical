###### dps/input/Makefile:	InterForm Token input sources
# 	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.dps.input
TOPDIR=../../..

FILES = \
	AbstractInput.java \
	ActiveInput.java \
	FromParseNodes.java \
	FromParseTree.java 

OLDFILES = \
	AbstractInputFrame.java \
	ExpandToken.java \
	Guard.java \
	SingleToken.java \
	Wrapper.java

include $(TOPDIR)/makefiles/file.make

### Dependencies:

AbstractInput.class: ../Input.java ../util/CurrentNode.java
ActiveInput.class: ../Input.java ../util/CurrentActive.java
FromParseTree.class:  ../Input.java ../util/CurrentActive.java ActiveInput.java
FromParseNodes.class:  ../Input.java ../util/CurrentActive.java ActiveInput.java
