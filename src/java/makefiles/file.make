### file.make
# $Id$
# COPYRIGHT 1997, Ricoh California Research Center
# Portions COPYRIGHT 1997, Sun Microsystems

# This makefile should be included in all leaves packages directory. It uses
# the FILES variable to know what are the files to be compiled.
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
# The 'doc' target uses DESTDIR variable that should point to the absolute 
# path of the target directory (in which doc files will be created).

# <steve@crc.ricoh.com>
#	The Sun originals require MAKEDIR and DESTDIR to be absolute.
#	This has serious problems when you're trying to use source control.

CLASSDIR= $(TOPDIR)/classes

#piahome/lib/java/*.zip   
#zip files added in file.make...could determine automatically...
LIBDIR= $(TOPDIR)/../../../lib/java

LIBCLASSES= $(LIBDIR)/jigsaw.zip:$(LIBDIR)/jgl1.1.zip

##javac wrapper should find these  .. specify explicitly if problem
 #JAVACLASSES= /usr/local/src/www/java-SDK/jdk1.1.1/lib/classes.zip
#sun 1.0.2 location
JAVACLASSES= /usr/local/src/www/java-SDK/java/lib/classes.zip

DOCDIR  = $(TOPDIR)/Doc

.SUFFIXES: .java .class

.java.class:
#	javac -d $(CLASSDIR) -classpath $(CLASSDIR):$(JAVACLASSES):$(CLASSPATH) -O $<
	javac -d $(CLASSDIR) -classpath $(CLASSDIR):$(JAVACLASSES):$(LIBCLASSES):$(CLASSPATH) -g $<

all:: $(FILES:.java=.class)

doc::	$(DOCDIR)
	@echo $(DOCDIR) made

$(DOCDIR):
	mkdir $(DOCDIR)

doc::
	javadoc -d $(DOCDIR) -classpath $(CLASSDIR):$(JAVACLASSES):$(LIBCLASSES):$(CLASSPATH) $(PACKAGE)

clean::
	@@rm -rf *~ *.class


