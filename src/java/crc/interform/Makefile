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

Actor.class: Token.java Tokens.java SGML.java

Interp.class: Actor.java Input.java Token.java Tokens.java SGML.java

Parser.class: Input.java Token.java Tokens.java SGML.java

Tagset.class: Token.java SGML.java

Text.class: Token.java SGML.java

Token.class: Tokens.java SGML.java ../ds/List.java ../ds/Table.java

Tokens.class: SGML.java 

Text.class: SGML.java
