######  content/Makefile:	Content package.
# 	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE=crc.content
TOPDIR=../..

PACKAGES = \
	text

FILES = \
	GenericContent.java \
	StreamingContent.java \
	ByteStreamContent.java 

include $(TOPDIR)/makefiles/file.make
include $(TOPDIR)/makefiles/package.make

### Dependencies

Text.class: GenericContent.java

ByteStreamContent.class:  GenericContent.java

GenericContent.class: ../pia/Content.java



