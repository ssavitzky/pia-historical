### package.make
# 	$Id$
# 	COPYRIGHT 1997, Ricoh California Research Center
# 	Portions COPYRIGHT 1997, Sun Microsystems

# This makefile should be included in all packages Makefiles. To use it, define
# the PACKAGES variable to the set of packages defined in your directory,
# and the PACKAGE variable to this package name.
# So, if you have a 'foo' package, included in 'w3c' and containing 'bar1'
# and 'bar2' sub packages, your Makefile should look like this:
# ----------
# PACKAGE=w3c.foo
# PACKAGES=bar1 bar2
# TOPDIR=../..
# include $(TOPDIR)/makefiles/package.make
# (formerly) include $(MAKEDIR)/package.make
# ----------
#
# This make file defines the following targets:
# all:	 to build the class files from the java files.
# clean: to clean all sub packages
# doc:   to build the appropriate documentation files from the source
# The 'doc' target uses DESTDIR variable that should point to the absolute 
# path of the target directory (in which doc files will be created).

# <steve@crc.ricoh.com>
#	The Sun originals require MAKEDIR and DESTDIR to be absolute.
#	This has serious problems when you're trying to use source control.

all::
	for p in `ls -d $(PACKAGES)`; do \
		echo 'building ' $(PACKAGE).$$p; \
		(cd $$p; make TOPDIR=../$(TOPDIR) VPATH=$(VPATH)/$$p); \
	done

doc::
	@@for p in `ls -d $(PACKAGES)`; do \
		echo 'doc ' $(PACKAGE).$$p; \
		(cd $$p; make TOPDIR=../$(TOPDIR)  doc); \
	done

clean::
	@@for p in `ls -d $(PACKAGES)`; do \
		echo 'cleaning ' $(PACKAGE).$$p; \
		(cd $$p ; make TOPDIR=../$(TOPDIR) clean) ; \
	done

