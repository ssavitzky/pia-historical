###### crc/Makefile:	Ricoh California Research Center's Java classes.
#	$Id$
#	Copyright 1997, Ricoh California Research Center

TOPDIR=..
LIBDIR=$(TOPDIR)/../../../lib/java
BINDIR=$(TOPDIR)/../../../bin

# mail, mime

include $(TOPDIR)/makefiles/file.make

#
# Stuff for jarring class files.
#
pia_class.toc::
	find crc -name "*.class" -print > pia_class.toc	
crc.zip: pia_class.toc
	$(BINDIR)/jarcrc
	mv $@ $(LIBDIR)












