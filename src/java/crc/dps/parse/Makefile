###### dps/parse/Makefile:	InterForm parsers
# 	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.dps.parse
TOPDIR=../../..

FILES = \
	AbstractParser.java \
	BasicParser.java \
	HTMLParser.java

include $(TOPDIR)/makefiles/file.make

### Dependencies:

AbstractParser.class: ../Token.java ../Tagset.java ../Parser.java \
	../EntityTable.java ../input/AbstractInputFrame.java
BasicParser.class: AbstractParser.java ../Token.java ../Tagset.java \
	../Parser.java ../EntityTable.java ../input/AbstractInputFrame.java
HTMLParser.class: BasicParser.java AbstractParser.java \
	../Token.java ../Tagset.java \
	../Parser.java ../EntityTable.java ../input/AbstractInputFrame.java
