###### interform/Makefile:	InterForm Interpretor package.
# 	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.interform
TOPDIR=../../..

PACKAGES = \
	handle

FILES = \
	Actor.java \
	Filter.java \
	Handler.java \
	Input.java \
	Interp.java \
	Parser.java \
	Run.java \
	SGML.java \
	State.java \
	Tagset.java \
	Text.java \
	Token.java \
	TokenStream.java \
	Tokens.java \
	Util.java

include $(TOPDIR)/makefiles/file.make
include $(TOPDIR)/makefiles/package.make

### Dependencies:
###	Compiling a class will automatically build the classes it
###	depends on.  This happens quietly (unfortunately), but make
###	seems to cope with it and not build anything twice.

Actor.class: Actor.java Token.java Tokens.java SGML.java \
	Interp.java Handler.java

Filter.class: Filter.java Interp.java 

Handler.class: Handler.java Actor.java Interp.java SGML.java ../ds/Table.java

Interp.class: Interp.java Actor.java Input.java Token.java Tokens.java \
	SGML.java State.java

Parser.class: Parser.java Input.java Token.java Tokens.java SGML.java

Run.class: Run.java Interp.java

State.class: ../ds/List.java ../ds/Table.java

Tagset.class: Tagset.java Token.java SGML.java

Text.class: Text.java Token.java SGML.java

Token.class: Token.java Tokens.java SGML.java ../ds/List.java ../ds/Table.java

Tokens.class: Tokens.java SGML.java 

TokenStream.class: TokenStream.java Tokens.java 

Util.class: SGML.java ../ds/List.java ../ds/Table.java

