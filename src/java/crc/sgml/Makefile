###### interform/Makefile:	InterForm Interpretor package.
# 	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.sgml
TOPDIR=../../..

FILES = \
	Attrs.java \
	AttrBase.java \
	AttrSGML.java \
	AttrTable.java \
	AttrWrap.java \
	Element.java \
	Entity.java \
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

AttrBase.class: Attrs.java SGML.java ../ds/List.java

AttrSGML.class: Attrs.java AttrBase.java SGML.java ../ds/List.java

AttrTable.class: AttrTable.java Attrs.java SGML.java ../ds/Table.java

AttrWrap.class: Attrs.java AttrBase.java AttrSGML.java SGML.java ../ds/List.java

Attrs.class: Attrs.java SGML.java

Element.class: Token.java Tokens.java SGML.java \
	../ds/List.java ../ds/Table.java

Element.class: Token.java SGML.java

SGML.class: SGML.java Attrs.java

Text.class: Text.java Token.java SGML.java

Token.class: Token.java Tokens.java SGML.java ../ds/List.java ../ds/Table.java

Tokens.class: Tokens.java Element.java SGML.java 

Util.class: SGML.java ../ds/List.java ../ds/Table.java

