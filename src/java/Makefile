###### crc/Makefile:	Ricoh California Research Center's Java classes.
#	$Id$
#	Copyright 1997, Ricoh California Research Center

TOPDIR=.

ADOCDIR = $(PIADIR)/Doc/Manuals/Api/JavaDoc

PACKAGES= \
	crc \
	misc

### These are the names of all the packages in the PIA.
###	We need these because javadoc does _not_ operate recursively.

PIAPACKAGENAMES= \
	crc.ds \
	crc.gnu.regexp \
	crc.interform crc.interform.handle \
	crc.pia crc.pia.agent \
	crc.sgml \
	crc.tf \
	crc.util \
	misc

### These are the names of the classes in the JDK that the PIA uses.  
###	Assuming javadoc can find the source code, we should be able to
###	include them in the API documentation.

SUNPACKAGENAMES= \
	java.lang \
	java.io \
	java.util \
	java.net

PACKAGENAMES= $(PIAPACKAGENAMES) $(SUNPACKAGENAMES)


# makefiles/file.make defines PIADIR, LIBDIR, BINDIR, DOCDIR
include $(TOPDIR)/makefiles/package.make
include $(TOPDIR)/makefiles/file.make

#
# Stuff for jarring class files.
#
pia_class.toc::
	rm -f pia_class.toc
	for p in $(PACKAGES); do \
		find `echo $$p` -name "*.class" -print >> pia_class.toc; \
	done

crc.zip: pia_class.toc
	$(BINDIR)/jarcrc
	mv $@ $(LIBDIR)

### Recursive documentation:

doc:: alldoc
	@echo Java documentation complete

# Non-recursive JavaDoc documentation -- the whole shebang.
#	We do this because javadoc doesn't append to existing index files,
#	it smashes them.  This makes it useless when run recursively.

JAVADOC_CMD = javadoc -d $(ADOCDIR) -classpath $(CLASSDIR):$(JAVACLASSES):$(LIBCLASSES):$(CLASSPATH) -sourcepath $(CLASSDIR):$(CLASSPATH):$(JAVASOURCE)

# non-recursive doc.
alldoc::
	-$(JAVADOC_CMD) $(PACKAGENAMES)

piadoc::
	-$(JAVADOC_CMD) $(PIAPACKAGENAMES)

sundoc::
	-$(JAVADOC_CMD) $(SUNPACKAGENAMES)













