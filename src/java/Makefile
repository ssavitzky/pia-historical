###### crc/Makefile:	Ricoh California Research Center's Java classes.
#	$Id$
#	Copyright 1997, Ricoh California Research Center

TOPDIR=.

ADOCDIR = $(PIADIR)/Doc/Manuals/Api/JavaDoc

PACKAGES= \
	misc \
	crc

### These are the names of all the packages in the PIA.
###	We need these because javadoc does _not_ operate recursively.

PIAPACKAGENAMES= \
	crc.content crc.content.text \
	crc.dom \
	crc.dps crc.dps.handle crc.dps.input crc.dps.output crc.dps.parse \
	crc.ds \
	crc.gnu.regexp \
	crc.interform crc.interform.handle \
	crc.pia crc.pia.agent \
	crc.sgml \
	crc.tf \
	crc.util \
	crc.dom \
	misc

### These are the names of the classes in the JDK that the PIA uses.  
###	Assuming javadoc can find the source code, we should be able to
###	include them in the API documentation.

SUNPACKAGENAMES= \
	java.lang \
	java.io \
	java.net \
	java.text \
	java.util

MISCPACKAGENAMES= \
	w3c.www.http \
	w3c.www.mime

PACKAGENAMES= $(PIAPACKAGENAMES) $(MISCPACKAGENAMES) $(SUNPACKAGENAMES)


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
	for p in $(PACKAGES); do \
		(cd `echo $$p`; make clean) ; \
	done
	mv $@ $(LIBDIR)

### Recursive documentation:

doc:: alldoc
	@echo Java documentation complete

### "make logs" does "make clean", "make all", and "make doc".
###	Each operation is logged and timed.  

logs::  report start.logs clean.log make.log doc.log 
	@date						>> summary.log
	@echo make logs complete.  Summary:
	cat summary.log

start.logs::
	@echo Doing clean makes to produce log files.
	@echo Go get some coffee -- this takes about 20 minutes

clean.log::
	(time make -k clean				2>&1) | tee clean.log

make.log::
	(time make -k JAVAFLAGS=-deprecation all	2>&1) | tee make.log
	@echo Times:  make				>> summary.log
	@tail -2 make.log				>> summary.log

doc.log::
	(time make -k doc				2>&1) | tee doc.log
	@echo Times:  doc				>> summary.log
	@tail -2 doc.log				>> summary.log

report:: all_holes.log all_lines.log
	@date 						| tee	   summary.log
	@echo FILES: `wc -l all_lines.log`		| tee -a   summary.log
	@echo HOLES: `wc -l all_holes.log`		| tee -a   summary.log

### Recursive hole report.
all_holes.log::
	$(PIADIR)/src/app/tools/hole-report > all_holes.log

### Recursive line count.  
###	Suitable for use with "xdu all_lines.log"
all_lines.log::
	find ${*-.} -type f -name '*.java' -exec wc -l {} \; > all_lines.log

### JavaDoc documentation -- non-recursive.
###	We do this because javadoc doesn't append to existing index files,
###	it smashes them.  This makes it useless when run recursively.

JIGSAWSOURCE = /pia1/ext/java/Jigsaw/src/classes
JAVADOC_CLASSES = $(CLASSDIR):$(JAVACLASSES):$(LIBCLASSES):$(CLASSPATH)
JAVADOC_SOURCES = $(CLASSDIR):$(CLASSPATH):$(JAVASOURCE):$(JIGSAWSOURCE)
JAVADOC_CMD = javadoc -J-mx64m -J-ms64m -d $(ADOCDIR) -package \
	-classpath $(JAVADOC_CLASSES) -sourcepath $(JAVADOC_SOURCES)

alldoc::
	-$(JAVADOC_CMD) $(PACKAGENAMES)

piadoc::
	-$(JAVADOC_CMD) $(PIAPACKAGENAMES)

sundoc::
	-$(JAVADOC_CMD) $(SUNPACKAGENAMES)

