###### crc/Makefile:	Ricoh California Research Center's Java classes.
#	$Id$
#	Copyright 1997, Ricoh California Research Center

TOPDIR=.

PACKAGES= \
	crc

# makefiles/package.make defines PIADIR, LIBDIR, BINDIR, DOCDIR
include $(TOPDIR)/makefiles/package.make

#
# Stuff for jarring class files.
#
pia_class.toc::
	find crc -name "*.class" -print > pia_class.toc	
crc.zip: pia_class.toc
	$(BINDIR)/jarcrc
	mv $@ $(LIBDIR)












