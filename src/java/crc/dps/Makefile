###### dps/Makefile:	Document Processing System package.
# 	$Id$
#	Copyright 1998, Ricoh Silicon Valley

PACKAGE=crc.dps
TOPDIR=../..

PACKAGES = \
	tags

FILES = \
	Tag.java \
	Parser.java \
	Processor.java \
	Token.java \
	TokenList.java \
	Util.java

include $(TOPDIR)/makefiles/file.make
include $(TOPDIR)/makefiles/package.make

### Dependencies:
###	Compiling a class will automatically build the classes it
###	depends on.  This happens quietly (unfortunately), but make
###	seems to cope with it and not build anything twice.

Actor.class: Actor.java Interp.java Handler.java \
	../sgml/Element.java ../sgml/Tokens.java ../sgml/SGML.java 

Environment.class: Environment.java Interp.java ../ds/Table.java

Filter.class: Filter.java Interp.java 

Handler.class: Handler.java Actor.java Interp.java \
	../sgml/SGML.java ../ds/Table.java

InterFormReader.class: Interp.java

InterFormStream.class: Interp.java

Interp.class: Interp.java Actor.java Input.java State.java \
	../sgml/Element.java ../sgml/Entity.java \
	../sgml/Token.java ../sgml/Tokens.java ../sgml/SGML.java

Parser.class: Parser.java Input.java ../sgml/Entity.java ../sgml/Element.java \
	../sgml/Token.java ../sgml/Tokens.java ../sgml/SGML.java

Run.class: Run.java Environment.java Interp.java ../ds/Table.java

SecureAttrs.class: Environment.java Interp.java ../sgml/SGML.java \
	../sgml/Attrs.java ../sgml/AttrWrap.java ../sgml/AttrTable.java

State.class: ../ds/List.java ../ds/Table.java

Tagset.class: Tagset.java ../sgml/Element.java ../sgml/SGML.java

TokenStream.class: TokenStream.java ../sgml/Tokens.java 

Util.class: Util.java ../sgml/Util.java ../sgml/SGML.java \
	../ds/List.java ../ds/Table.java

