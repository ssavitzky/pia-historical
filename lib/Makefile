### Makefile for lib
#	$Id$
#	COPYRIGHT 1997, Ricoh Silicon Valley

PIADIR=..
MF_DIR=$(PIADIR)/Config/makefiles
MYNAME=lib
MYPATH=lib

include $(MF_DIR)/file.make

all:: perl
	@echo "symlinks made"

perl: 
	-test -d perl || ( test -d ../src/perl && ln -s ../src/perl . )
