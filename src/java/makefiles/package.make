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
# all:	   to build the class files from the java files.
# clean:   to clean all sub packages
# doc:     to build the appropriate documentation files from the source
# alldoc:  to build all the documentation at once

# <steve@crc.ricoh.com>
#	The Sun originals require MAKEDIR and DESTDIR to be absolute.
#	This has serious problems when you're trying to use source control.

CLASSDIR= $(TOPDIR)/classes
PIADIR  = $(TOPDIR)/../../..
DOCDIR  = $(TOPDIR)/Doc
ADOCDIR = $(PIADIR)/Doc/Manuals/Api/JavaDoc
LIBDIR  = $(TOPDIR)/../../../lib/java
BINDIR  = $(TOPDIR)/../../../bin

#LIBCLASSES= $(LIBDIR)/jigsaw.zip:$(LIBDIR)/jgl2.0.2.zip:$(LIBDIR)/regexp.zip
LIBCLASSES= $(LIBDIR)/jigsaw.zip:$(LIBDIR)/regexp.zip
##javac wrapper should find these  .. specify explicitly if problem
 #JAVACLASSES= /usr/local/src/www/java-SDK/jdk1.1.1/lib/classes.zip
#sun 1.0.2 location
JAVACLASSES= /usr/local/src/www/java-SDK/java/lib/classes.zip
JAVASOURCE=  /usr/local/src/www/java-SDK/jdk1.1.1/src

all::
	for p in `ls -d $(PACKAGES)`; do \
		echo 'building ' $(PACKAGE).$$p; \
		(cd $$p; make TOPDIR=../$(TOPDIR) VPATH=$(VPATH)/$$p); \
	done

JAVADOC_CMD = javadoc -d $(ADOCDIR) -classpath $(CLASSDIR):$(JAVACLASSES):$(LIBCLASSES):$(CLASSPATH) -sourcepath $(CLASSDIR)$(CLASSPATH):$(JAVASOURCE)

# non-recursive doc.
alldoc::
	-$(JAVADOC_CMD) $(PACKAGENAMES)

piadoc::
	-$(JAVADOC_CMD) $(PIAPACKAGENAMES)

sundoc::
	-$(JAVADOC_CMD) $(SUNPACKAGENAMES)

# Recursive doc -- generates all the class files but damages the indexes
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

