###### interform/Makefile:	InterForm Interpretor package.
# 	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.sgml
TOPDIR=../../..

FILES = \
	Attrs.java \
	AttrTable.java \
	SGML.java \
	Text.java \
	Token.java \
	Tokens.java \
	Util.java

include $(TOPDIR)/makefiles/file.make

### Dependencies:
###	Compiling a class will automatically build the classes it
###	depends on.  This happens quietly (unfortunately), but make
###	seems to cope with it and not build anything twice.

AttrTable.class: AttrTable.java Attrs.java SGML.java ../ds/Table.java

Attrs.class: Attrs.java SGML.java

Text.class: Text.java Token.java SGML.java

Token.class: Token.java Tokens.java SGML.java ../ds/List.java ../ds/Table.java

Tokens.class: Tokens.java SGML.java 

Util.class: SGML.java ../ds/List.java ../ds/Table.java

