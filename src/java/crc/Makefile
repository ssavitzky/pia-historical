###### crc/Makefile:	Ricoh California Research Center's Java classes.
#	$Id$
#	Copyright 1997, Ricoh California Research Center

PACKAGE="crc"
TOPDIR=..

### These are the packages to be built.  
###	Only the top-level directories are needed; 
###	make descends into each one recursively.

PACKAGES = \
	ds \
	interform \
	pia \
	sgml \
	tf \
	util

### These are the names of all the packages in the PIA.
###	We need these because javadoc does _not_ operate recursively.

PIAPACKAGENAMES= \
	crc.ds \
	crc.gnu.regexp \
	crc.interform crc.interform.handle \
	crc.pia crc.pia.agent \
	crc.sgml \
	crc.tf \
	crc.util 

### These are the names of the classes in the JDK that the PIA uses.  
###	Assuming javadoc can find the source code, we should be able to
###	include them in the API documentation.

SUNPACKAGENAMES= \
	java.lang \
	java.io \
	java.util \
	java.net

PACKAGENAMES= $(PIAPACKAGENAMES) $(SUNPACKAGENAMES)


include $(TOPDIR)/makefiles/package.make
include $(TOPDIR)/makefiles/file.make














