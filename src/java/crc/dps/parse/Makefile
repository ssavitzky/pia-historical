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

AbstractParser.class: ../Tagset.java ../Parser.java \
	../EntityTable.java 
BasicParser.class: AbstractParser.java ../Tagset.java \
	../Parser.java ../EntityTable.java 
HTMLParser.class: BasicParser.java AbstractParser.java \
	../Tagset.java \
	../Parser.java ../EntityTable.java 
