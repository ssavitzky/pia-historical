### file.make
# $Id$
# COPYRIGHT 1997, Ricoh California Research Center
# Portions COPYRIGHT 1997, Sun Microsystems

# This makefile should be included in the Makefile of each package directory
# For example, if you have a package 'foo' containing 'a.java' and 'b.java'
# your Makefile should look like this:
# ----------
# PACKAGE=foo
# FILES=a.java b.java
# TOPDIR=../../..
# include $(TOPDIR)/makefiles/files.make
# ----------
#
# This file defines the following targets:
# all:	 to build the class files from the java files.
# clean: to clean all sub packages
# doc:   to build the appropriate documentation files from the source
#   	 Normally does nothing, since javadoc is called non-recursively
#	 at the top level.  May be used for auxiliary documentation.
# jdoc:	 to invoke javadoc in this directory.  Does not make package index
#	 or class hierarchy, since these are shared by all packages, but can
#	 be used to quickly update the documentation for a changed package.

# <steve@rsv.ricoh.com>
#	The Sun originals required MAKEDIR and DESTDIR to be absolute paths.
#	This has serious problems when you're trying to use source control.

CLASSDIR= $(TOPDIR)

PIADIR=$(TOPDIR)/../..
LIBDIR=$(PIADIR)/lib/java
BINDIR=$(PIADIR)/bin
DOCDIR=$(PIADIR)/Doc/Manuals/Api/JavaDoc

#LIBCLASSES= $(LIBDIR)/jigsaw.zip:$(LIBDIR)/jgl2.0.2.zip:$(LIBDIR)/regexp.zip
LIBCLASSES= $(LIBDIR)/jigsaw.zip:$(LIBDIR)/crc.zip

##javac wrapper should find these  .. specify explicitly if problem
JAVACLASSES= /usr/local/java/lib/classes.zip
JAVASOURCE=  /usr/local/java/src

BUILDCLASSES=$(CLASSDIR):$(JAVACLASSES):$(LIBCLASSES):$(CLASSPATH)

## Set this on the command line to see warnings about deprecated API's
# JAVAFLAGS=-deprecation

.SUFFIXES: .java .class

.java.class:
#	javac -d $(CLASSDIR) -classpath $(CLASSDIR):$(JAVACLASSES):$(CLASSPATH) -O $<
	javac -d $(CLASSDIR) -classpath $(BUILDCLASSES) -g $(JAVAFLAGS) $<

all:: $(FILES:.java=.class)

doc::
	@echo Documenting.

jdoc::
	javadoc -d $(DOCDIR) -noindex -notree -classpath $(BUILDCLASSES) $(PACKAGE)

clean::
	@@rm -rf *~ *.class



