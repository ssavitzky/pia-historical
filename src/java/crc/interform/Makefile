###### interform/Makefile:	InterForm Interpretor package.
# 	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.interform
TOPDIR=../../..

FILES = \
	Actor.java \
	Input.java \
	Interp.java \
	Parser.java \
	SGML.java \
	Tagset.java \
	Text.java \
	Token.java \
	Tokens.java

include $(TOPDIR)/makefiles/file.make

Actor.class: Token.class Tokens.class

Interp.java: Actor.class Input.class Token.class Tokens.class

Tagset.class: Token.class

Text.class: Token.class

Token.class: Tokens.class SGML.class ../ds/List.class ../ds/Table.class

Tokens.class: SGML.class

Text.class: SGML.class
