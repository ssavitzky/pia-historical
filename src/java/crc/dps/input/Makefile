###### dps/input/Makefile:	InterForm Token input sources
# 	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.dps.input
TOPDIR=../../..

FILES = \
	AbstractInput.java \
	AbstractInputFrame.java \
	ExpandToken.java \
	Guard.java \
	SingleToken.java \
	Wrapper.java

include $(TOPDIR)/makefiles/file.make

### Dependencies:

AbstractInput.class: ../Input.java ../Token.java
AbstractInputFrame.class: AbstractInput.java ../Input.java ../Token.java \
	../InputStack.java ../InputStackFrame.java Wrapper.java
ExpandToken.class: AbstractInputFrame.java  ../Input.java ../Token.java
SingleToken.class: AbstractInputFrame.java  ../Input.java
Wrapper.class: AbstractInputFrame.java  ../Input.java
